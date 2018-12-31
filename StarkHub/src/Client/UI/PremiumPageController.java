package Client.UI;

import Client.DataClasses.Channel;
import Client.DataClasses.Video;
import Client.Login.LayoutController;
import Client.Login.Main;
import Client.Utility.SendData;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PremiumPageController implements Initializable {

    @FXML
    JFXListView<Label> allVideosListView;

    @FXML
    JFXListView<AnchorPane> selectedVideoListView;

    @FXML
    JFXButton makePremiumButton, addButton;

    ArrayList<Pair<Video, String> > premList, list;
    HashMap<String,Channel> map;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try{
            allVideosListView.setExpanded(true);
            allVideosListView.setPadding(new Insets(10));
            allVideosListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            selectedVideoListView.setExpanded(true);
            selectedVideoListView.setPadding(new Insets(10));
            selectedVideoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


             map = MainPageController.myChannelMap;
            System.out.println("MyChannel Map: "+map);
             premList = new ArrayList<>();
             list = new ArrayList<>();

            for(Map.Entry<String, Channel> entry: map.entrySet()){
                Channel c = entry.getValue();
                ArrayList<Video> vl = c.getVideoList();
                for(Video v: vl){
                    list.add(new Pair<>(v, c.getChannelName()));

                    Label l = new Label();
                    l.setText(c.getChannelName()+": "+v.getVideoName());
                    allVideosListView.getItems().add(l);
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }




    }


    AnchorPane createSelectedItem(String string) throws Exception{

        AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/selectedVideoPremLayout.fxml"));
        JFXButton btn = (JFXButton)(pane.getChildren().get(0));
        Label l = (Label)(pane.getChildren().get(1));

        l.setText(string);

        btn.setOnAction(e -> {
            String s = ((Label)(pane.getChildren().get(1))).getText();
            String vidName = s.substring(s.indexOf(':')+1).trim();
            allVideosListView.getItems().add(new Label(s));
            selectedVideoListView.getItems().remove(pane);
            ArrayList<Pair<Video, String>> lst = premList;
            for(Pair<Video, String> p: lst){
                if(p.getKey().getVideoName().equals(vidName)){
                    premList.remove(p);
                }
            }
        });

        return pane;
    }


    public void addButtonClicked(){
        ObservableList<Label> ll = allVideosListView.getSelectionModel().getSelectedItems();

        for(Label l: ll){
            try {
                selectedVideoListView.getItems().add(createSelectedItem(l.getText()));
            }catch (Exception e){
                e.printStackTrace();
            }

            allVideosListView.getItems().remove(l);
        }
    }


    public void makePremiumButtonClicked(){
        ObservableList<AnchorPane> list = selectedVideoListView.getItems();

        for(AnchorPane p: list) {

            String s = ((Label) (p.getChildren().get(1))).getText();
            String channelName = s.substring(0,s.indexOf(':'));
            String vidName = s.substring(s.indexOf(':')+1).trim();

            System.out.println("VidName from listitem: "+vidName);
            System.out.println("channelName from listItem: "+channelName);

            Channel c = map.get(channelName);

            ArrayList<Video> l = c.getVideoList();
            System.out.println("Looping in videoList");
            for(Video v: l){
                System.out.println("-VideoName: "+v.getVideoName());
                if(v.getVideoName().equals(vidName)){
                    premList.add(new Pair<>(v, channelName));
                    break;
                }
            }
        }

        System.out.println("PremList: "+premList);

        MainPageController.premiumVideoList = premList;

        if(!premList.isEmpty()) {
            SendData sd = new SendData();
            sd.start();

            sd.setOnSucceeded(e -> {
                System.out.println("Premeium list successfully sent");
                try {
                    File f = new File(System.getProperty("user.home") + "/starkhub/" + Main.USERNAME + "/credentials.cfg");
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    String usname = br.readLine();
                    String pass = br.readLine();
                    String name = br.readLine();
                    br.close();
                    PrintWriter pw = new PrintWriter(f);
                    pw.println(usname);
                    pw.println(pass);
                    pw.println(name);
                    pw.println("true");
                    pw.close();

                    LayoutController.isPremium = true;

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }else{
            System.out.println("PremList is Empty...!!");
        }

    }

}
