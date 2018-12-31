package Client.UI;


import Client.Utility.*;
import hubFramework.Video;
import Client.Login.Main;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;

import static Client.UI.MediaPlayerAndControlsController.videoPeerIP;

public class ClientPageController implements Initializable {

    @FXML
    ScrollPane clientScrollPane;

    //ArrayList<Client.HubServices.Video> receivedVideos, receivedVideosRecommended;
    public static HashMap<String, hubFramework.Video> receivedVideos, receivedVideosRecommended;

    public HashMap<String, ImageView > nameImageViewMap;

    public static boolean startResetingThumbNail=false;

    //public static Stage playWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources)  {

        receivedVideos = new HashMap<>();
        receivedVideosRecommended = new HashMap<>();
        nameImageViewMap = new HashMap<>();


        if(Client.Login.Main.isNewUser){
            try {
                Client.Login.Main.isNewUser = false;
                Socket sock = new Socket(Main.HUB_IP, Main.PORT);
                DataInputStream dis = new DataInputStream(sock.getInputStream());
                DataOutputStream dout = new DataOutputStream(sock.getOutputStream());

                ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                dout.writeUTF("#NEWUSER");
                System.out.println("#NEWUSER flag sent");
                dout.writeUTF(Main.USERNAME);
                System.out.println("Sent username: "+ Main.USERNAME);
                String msg;
                if((msg = dis.readUTF()).equals( "#NOFILES")){
                    System.out.println("Received #NOFILES");
                    makeUpUI(receivedVideos, receivedVideosRecommended);
                    //TODO NOTHING
                }else if(msg.equals("#SENDING")){
                    //TODO: Some Operation

                    //new Thread(new ThumnailReceiverThread()).start();

                    ThumbnailReceiverService service = new ThumbnailReceiverService();
                    service.start();
                    service.setOnSucceeded(e -> {
                        putOnThumbnails();
                        // TODO: Correct ThumbnailPath
                        System.out.println("Put on Thumbnails Ended");
                       ResetThumbnailPathService r =  new ResetThumbnailPathService();
                       r.start();
                    });

                    receivedVideosRecommended = (HashMap<String, hubFramework.Video>) ois.readObject();
                     receivedVideos = (HashMap<String, hubFramework.Video>) ois.readObject();

                    startResetingThumbNail = true;

                    System.out.println("Size of 1st Map: "+receivedVideosRecommended.size());
                    System.out.println("Size of 2nd Map: "+receivedVideos);
                     makeUpUI(receivedVideos, receivedVideosRecommended);


                }
            }catch(Exception e){
                makeUpUI(receivedVideos, receivedVideosRecommended);
                e.printStackTrace();
            }
        }else{

            // TODO: if not NEWUSER
            try {
                Socket sock = new Socket(Main.HUB_IP, Main.PORT);
                DataInputStream dis = new DataInputStream(sock.getInputStream());
                DataOutputStream dout = new DataOutputStream(sock.getOutputStream());

                ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                dout.writeUTF("#EXISTINGUSER");
                System.out.println("#EXISTINGUSER flag sent");
                dout.writeUTF(Main.USERNAME);
                System.out.println("Sent username: "+ Main.USERNAME);
                String msg;
                if((msg = dis.readUTF()).equals( "#NOFILES")){
                    System.out.println("Received #NOFILES");
                    makeUpUI(receivedVideos, receivedVideosRecommended);
                    //TODO NOTHING
                }else if(msg.equals("#SENDING")){
                    //TODO: Some Operation
                    System.out.println("Received #SENDING");


                    //new Thread(new ThumnailReceiverThread()).start();

                    ThumbnailReceiverService service = new ThumbnailReceiverService();
                    service.start();
                    service.setOnSucceeded(e -> {
                        putOnThumbnails();
                        System.out.println("Put on Thumbnails Ended");
                        ResetThumbnailPathService r =  new ResetThumbnailPathService();
                        r.start();
                    });

                    receivedVideosRecommended = (HashMap<String, hubFramework.Video>) ois.readObject();
                    receivedVideos = (HashMap<String, hubFramework.Video>) ois.readObject();

                    startResetingThumbNail = true;

                    System.out.println("Size of 1st Map: "+receivedVideosRecommended.size()+" : "+receivedVideosRecommended);
                    System.out.println("Size of 2nd Map: "+receivedVideos.size()+" : "+receivedVideos);

                    makeUpUI(receivedVideos, receivedVideosRecommended);


                }
            }catch(Exception e){
                makeUpUI(receivedVideos, receivedVideosRecommended);
                e.printStackTrace();
            }
        }


    }


    public void makeUpUI(HashMap<String, hubFramework.Video> receivedVideos, HashMap<String, hubFramework.Video> receivedVideosRecommended){
        VBox vbox = new VBox(50);
        vbox.setFillWidth(true);

        try {
            Reflection reflection = new Reflection();
            reflection.setFraction(0.50);

            if(receivedVideosRecommended!=null){
                Iterator it = receivedVideosRecommended.entrySet().iterator();
                Label l = new Label("Recommeded Videos");
                l.setFont(Font.font(18));
                l.setEffect(reflection);
                vbox.getChildren().add(l);
                int k=0;
                for(int i=0;i<2 && it.hasNext();i++){
                    HBox h = createSection();
                    for(int j=0;j<4 && it.hasNext();j++){
                        String name = ((Map.Entry) it.next()).getKey().toString();
                        AnchorPane p = createSingleItem((name));
                        h.getChildren().add(p);
                        k++;
                    }
                    vbox.getChildren().add(h);
                }

            }

            if(receivedVideos!=null){
                System.out.println("Creating More Videos Section");
                Iterator it = receivedVideos.entrySet().iterator();
                Label l = new Label("More Videos");
                l.setFont(Font.font(18));
                l.setEffect(reflection);
                vbox.getChildren().add(l);
                int k=0;

                for(int i=0;i<2 && it.hasNext();i++){
                    HBox h = createSection();
                    for(int j=0;j<4 && it.hasNext();j++){
                        String name = ((Map.Entry) it.next()).getKey().toString();
                        AnchorPane p = createSingleItem((name));
                        h.getChildren().add(p);
                        k++;
                    }
                    vbox.getChildren().add(h);
                }

            }

        }catch(Exception e){
            e.printStackTrace();
        }
        StackPane pane = new StackPane(vbox);
        pane.setPadding(new Insets(15));
        clientScrollPane.setContent(pane);
    }



    // length of ArrayList should be 4
    HBox createSection() throws Exception{
        HBox hbox  = new HBox(15);
        return hbox;
    }

    AnchorPane createSingleItem( String videoName) throws Exception{
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("../Layouts/videoItemLayout.fxml"));
        ((Label)(anchorPane.getChildren().get(1))).setText(videoName);
        Label l = (Label)(anchorPane.getChildren().get(1));
        JFXButton mainButton = (JFXButton)(anchorPane.getChildren().get(2));
        JFXButton watchLaterButton = (JFXButton)(anchorPane.getChildren().get(4));
        ImageView imageView = (ImageView)(anchorPane.getChildren().get(0));

        nameImageViewMap.put(videoName, imageView);


        mainButton.setOnAction(e -> {
            System.out.println("Video button clicked");
            String vidName = l.getText();
            Video v=null;
            if(receivedVideosRecommended.containsKey(vidName)){
                v = receivedVideosRecommended.get(vidName);

                // TODO: open player and contact server
            }
            else if(receivedVideos.containsKey(vidName)) {
                v = receivedVideos.get(vidName);
            }
            if(v!=null){

                MainPageController.historyList.add(v);

                String userName = v.getOwnerName();
                String channel = v.getChannelName();

                MediaPlayerAndControlsController.ownerName = userName;
                MediaPlayerAndControlsController.channelName = channel;
                MediaPlayerAndControlsController.videoPath = v.getPathOfVideo();
                MediaPlayerAndControlsController.alternateVideoPath = v.getAlternatePathOfVideo();
                VideoPlayerController.videoName = v.getVideoName();

                // TODO: show loading
                GetIpService gis = new GetIpService(userName, v.getVideoName(), channel);
                gis.start();

                gis.setOnSucceeded(event -> {
                    // TODO: Remove Loading
                    Stage playWindow = new Stage();
                    playWindow.initModality(Modality.APPLICATION_MODAL);
                    //settingsWindow.initStyle(StageStyle.TRANSPARENT);
                    try {
                        //Parent settingsRoot = FXMLLoader.load(getClass().getResource("../Layouts/mediaPlayerAndControls.fxml"));
                        Parent settingsRoot = FXMLLoader.load(getClass().getResource("../Layouts/videoPlayer.fxml"));
                        Scene sc = new Scene(settingsRoot);
                        //sc.setFill(Color.TRANSPARENT);
                        playWindow.setScene(sc);
                        //playWindow.showAndWait();
                        playWindow.show();

                        playWindow.setOnCloseRequest(evt -> {
                            System.out.println("Video Player SetonClose calles");
                            MediaPlayerAndControlsController.mediaPlayer.stop();
                            try {
                                Socket sock = new Socket(videoPeerIP, 15001);
                                DataInputStream dis = new DataInputStream(sock.getInputStream());
                                DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
                                ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                                ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                                dout.writeUTF(Main.USERNAME);
                                dis.readBoolean();
                                dout.writeUTF("#DISCONNECT");

                                System.out.println("VideoPlayer Set on Close request exiting");

                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        });

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                });


            }

        });


        watchLaterButton.setOnAction(e -> {
            System.out.println("Watch Later clicked");
            Video v = null;
            String vidName = l.getText();
            if(receivedVideosRecommended.containsKey(vidName)){
                v = receivedVideosRecommended.get(vidName);
            }
            else if(receivedVideos.containsKey(vidName)){
                v = receivedVideos.get(vidName);
            }
            if(v!=null) {
                MainPageController.watchLaterList.add(v);
            }else{
                System.out.println("Video object is null");

            }

        });


        l.setWrapText(true);
        return anchorPane;
    }


    void putOnThumbnails(){
        System.out.println("Put on Thumbnails Triggered");
        String path = System.getProperty("user.home")+"/starkhub/"+Main.USERNAME +"/temp";
        File f = new File(path);
        File[] list = f.listFiles();

        System.out.println("FilesList: "+list);
        System.out.println("nameImageViewMap: "+nameImageViewMap);
        for(File thumb: list){
            if(thumb.isFile()){
                System.out.println(thumb.getName());
                String vidName = thumb.getName().substring(0, thumb.getName().lastIndexOf('.'));
                System.out.println("Vidname from thumb: "+vidName);
                System.out.println(nameImageViewMap.containsKey(vidName));
                if(nameImageViewMap.containsKey(vidName)){
                    ImageView i = nameImageViewMap.get(vidName);

                    Platform.runLater(() -> {
                        try{
                            i.setImage(new Image(new FileInputStream(thumb)));
                        }catch(Exception e){
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
        System.out.println("Put on thumbnails Ending");
    }

}
