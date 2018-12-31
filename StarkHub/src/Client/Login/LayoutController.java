package Client.Login;

import Client.DataClasses.Channel;
import Client.UI.MainPageController;
import Client.Utility.ByeService;
import Server.Server;
import com.google.common.hash.Hashing;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXTextField;
import hubFramework.Video;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import sun.security.util.Password;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Scanner;

public class LayoutController implements Initializable {

    @FXML
    AnchorPane pane1, pane2, rootPane;
    @FXML
    ImageView signInImg, signUpImg;
    @FXML
    Button signIn, signUp;
    @FXML
    JFXTextField signInUsernameTxt, signUpUsername, signUpName;
    @FXML
    JFXPasswordField signInPass, signUpPass;

    String userHome;

    public static String USERNAME = "";
    public static boolean isNotifReady = false;
    public static boolean isPremium = false;

    JFXPopup invalidUsernamePopup, emptyPopup;

    Thread serverThread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userHome = System.getProperty("user.home");

        invalidUsernamePopup = initInvalidUsernamePopup();
        emptyPopup = initEmptyPopup();
        signUpPass.setTooltip(new Tooltip("-> Password should be alphanumeric and 8 chars in length.\n-> Should have a special char .*_@#\n"));
        pane1.setVisible(false);
    }

    public void signInClicked(){
        if(!pane2.isVisible()) {
            pane2.setVisible(true);
            pane1.setVisible(false);
        }
    }

    public void signUpClicked(){
        if(!pane1.isVisible()) {
            pane1.setVisible(true);
            pane2.setVisible(false);
        }
    }

    // Submitting signIn details
    public void signInSubmit(){

        String userName = signInUsernameTxt.getText();
        String pass = signInPass.getText();

        if(userName==null || userName.isEmpty()){
            emptyPopup.show(signInUsernameTxt, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
            return;
        }

        if(pass == null || pass.isEmpty()){
            emptyPopup.show(signInPass, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
            return;
        }

        File f = new File(userHome+"/starkhub/"+userName+"/credentials.cfg");
        if(f.exists() && f.isFile()){
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String savedUsername = br.readLine();
                String savedPassword = br.readLine();
                String name = br.readLine();
                String prem = br.readLine();

                if(prem.equalsIgnoreCase("true")){
                    isPremium = true;
                }

                System.out.println("userName from file: "+savedUsername);
                System.out.println("password from file: "+savedPassword);

                String hashedPass = Hashing.sha256().hashString(pass, StandardCharsets.UTF_8).toString();

                if(savedUsername.equals(userName) && savedPassword.equals(hashedPass)){
                    Main.USERNAME = userName;
                    Main.isNewUser = false;
                    USERNAME = userName;
                    Server.starkHubUsername = userName;
                    serverThread = new Thread(new Server());
                    serverThread.start();
                    startMainPage();
                }else{
                    System.out.println("AUTHENTICATION FAILED");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            System.out.println("New user...SignUp !!    ");
        }

    }

    // Submitting signUp details
    public void signUpSubmit(){

        System.out.println("SignUpClicked..!!");

        String name = signUpName.getText();
        String userName = signUpUsername.getText();
        String pass = signUpPass.getText();
        if(!preValidation(name, userName,pass)){
            return;
        }
        boolean res;
        if(res = authenticateSignUp(userName)) {
            System.out.println("Auth: "+res);
            File f = new File(userHome + "/starkhub/"+userName+"/credentials.cfg");

            if (f.exists() && f.isFile() && f.length() != 0) {
                try {
                    System.out.println("A User Already Exists");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Creating new User");
                try {
                    createNewUser(userName, pass, name);
                    Main.USERNAME = userName;
                    USERNAME = userName;
                    Server.starkHubUsername = userName;
                    serverThread = new Thread(new Server());
                    serverThread.start();
                    Main.isNewUser = true;
                    startMainPage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



        }else{
            System.out.println("Auth: "+res);
            signUpUsername.setText("");
            invalidUsernamePopup.show(signUpUsername,JFXPopup.PopupVPosition.TOP,JFXPopup.PopupHPosition.LEFT);
        }

    }

    // Creating a new user and directory structure
    public void createNewUser(String userName, String passWord, String name) throws Exception{
        File f = new File(userHome+"/starkhub/"+userName+"/credentials.cfg");
        new File(userHome+"/starkhub").mkdir();
        new File(userHome+"/starkhub/"+userName+"/thumbnails").mkdirs();
        new File(userHome+"/starkhub/"+userName+"/playlists").mkdirs();
        new File(userHome+"/starkhub/"+userName+"/mychannels").mkdirs();
        new File(userHome+"/starkhub/"+userName+"/temp").mkdirs();
        new File(userHome+"/starkhub/"+userName+"/watchLater").mkdirs();
        new File(userHome+"/starkhub/"+userName+"/history").mkdirs();
        new File(userHome+"/starkhub/"+userName+"/comments").mkdirs();
        new File(userHome+"/starkhub/"+userName+"/subscriptions").mkdirs();
        new File(userHome+"/starkhub/"+userName+"/premium").mkdirs();
        new File(userHome+"/starkhub/"+userName+"/prem").mkdirs();
        PrintWriter pw = new PrintWriter(f);
        pw.println(userName);
        String hashedPassword = Hashing.sha256().hashString(passWord, StandardCharsets.UTF_8).toString();
        pw.println(hashedPassword);
        pw.println(name);
        pw.println("false");
        pw.close();
    }

    public void exitClicked(){
        System.exit(0);
    }


    // Starting the main view of the application
    public void startMainPage(){
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../Layouts/mainFrame.fxml"));
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        Stage curStage = (Stage) rootPane.getScene().getWindow();

        //curStage.initStyle(StageStyle.DECORATED);
        curStage.setScene(new Scene(root));

        // Action on the application to be closed
        curStage.setOnCloseRequest(e -> {
            try {

                ByeService bye = new ByeService();
                bye.start();

                ArrayList<Video> watchLater, history;
                watchLater = MainPageController.watchLaterList;
                history = MainPageController.historyList;
                ArrayList<Pair<Client.DataClasses.Video, String>> premList = MainPageController.premiumVideoList;

                HashMap<String, String> subscribedChannelMap = MainPageController.subscribedChannelMap;
                HashMap<String, Channel> myChannelMap = MainPageController.myChannelMap;

                System.out.println("WatchLater: "+watchLater);
                System.out.println("Histroy: "+history);
                System.out.println("My channels: "+myChannelMap);
                System.out.println("Subscriptions: "+subscribedChannelMap );


                File f = new File(System.getProperty("user.home") + "/starkhub/"+ Main.USERNAME +"/watchLater/list");
                if(!f.exists())
                    f.createNewFile();
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
                oos.writeObject(watchLater);
                oos.close();
                System.out.println("Object written");
                System.out.println("Add to watch later ArrayList Written");

                f = new File(System.getProperty("user.home") + "/starkhub/"+ Main.USERNAME +"/history/list");
                if(!f.exists()) {
                    f.createNewFile();
                }
                ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f));
                os.writeObject(history);
                os.close();
                System.out.println("Object written");
                System.out.println("History  ArrayList Written");

                f = new File(System.getProperty("user.home") + "/starkhub/"+ Main.USERNAME +"/mychannels/list");
                if(!f.exists()) {
                    f.createNewFile();
                }
                os = new ObjectOutputStream(new FileOutputStream(f));
                os.writeObject(myChannelMap);
                os.close();
                System.out.println("Object written");
                System.out.println("MyChannelMap Written");

                f = new File(System.getProperty("user.home") + "/starkhub/"+ Main.USERNAME +"/subscriptions/list");
                if(!f.exists()) {
                    f.createNewFile();
                }
                os = new ObjectOutputStream(new FileOutputStream(f));
                os.writeObject(subscribedChannelMap);
                os.close();
                System.out.println("Object written");
                System.out.println("SubscribedChannelsMap Written");


                f = new File(System.getProperty("user.home") + "/starkhub/"+ Main.USERNAME +"/prem/list");
                if(!f.exists()) {
                    f.createNewFile();
                }
                os = new ObjectOutputStream(new FileOutputStream(f));
                os.writeObject(premList);
                os.close();
                System.out.println("Object written");
                System.out.println("PremiumVideoList Written");


                serverThread.interrupt();

            }catch(Exception ex){
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        });

    }

    // Athenticating signUp from the HUB
    boolean authenticateSignUp(String username){
        boolean result = false;
        try {
            Socket hubConn = new Socket(Main.HUB_IP, Main.PORT);
            DataInputStream din = new DataInputStream(hubConn.getInputStream());
            DataOutputStream dout = new DataOutputStream(hubConn.getOutputStream());

            ObjectInputStream ois = new ObjectInputStream(hubConn.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(hubConn.getOutputStream());

            dout.writeUTF("#USERNAME");
            dout.writeUTF(username);
            result = din.readBoolean();

            dout.close();
            din.close();
            hubConn.close();

        }catch( Exception e){
            e.printStackTrace();
        }


        return result;
    }

    // POPUP: for invalid username
    JFXPopup initInvalidUsernamePopup(){
        Label l = new Label("Username already taken..!!");
        l.setStyle("-fx-foreground-color:#ff0000");
        VBox vbox = new VBox(l);
        vbox.setPadding(new Insets(10));
        JFXPopup p = new JFXPopup(vbox);
        return p;
    }

    // POPUP: for invalid empty field
    JFXPopup initEmptyPopup(){
        Label l = new Label("This is a required Feild\nand Cannot be empty");
        l.setWrapText(true);
        VBox vbox = new VBox(l);
        vbox.setPadding(new Insets(10));
        JFXPopup p = new JFXPopup(vbox);
        return p;
    }

    // Prevalidation before connecting to HUB
    boolean preValidation(String name, String userName, String pass){
        if(name == null || name.isEmpty()){
            emptyPopup.show(signUpName, JFXPopup.PopupVPosition.TOP ,JFXPopup.PopupHPosition.LEFT);
           return false;
        }if(userName == null || userName.isEmpty()){
            emptyPopup.show(signUpUsername, JFXPopup.PopupVPosition.TOP ,JFXPopup.PopupHPosition.LEFT);
            return false;
        }if(pass == null || pass.isEmpty()){
            emptyPopup.show(signUpPass, JFXPopup.PopupVPosition.TOP ,JFXPopup.PopupHPosition.LEFT);
            return false;
        }
        if(pass.length()<8){
            Label l = new Label("Password too short.\nMust be 8 chars");
            l.setWrapText(true);
            VBox vbox = new VBox(l);
            vbox.setPadding(new Insets(10));
            JFXPopup p = new JFXPopup(vbox);
            p.show(signUpPass, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
            signUpPass.clear();
            return false;
        }
        if(!name.matches("[a-zA-Z ]+")){
            Label l = new Label("Name cannot contain digits or special chars");
            l.setWrapText(true);
            VBox vbox = new VBox(l);
            vbox.setPadding(new Insets(10));
            JFXPopup p = new JFXPopup(vbox);
            p.show(signUpName, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
            return false;
        }

        return true;
    }


}
