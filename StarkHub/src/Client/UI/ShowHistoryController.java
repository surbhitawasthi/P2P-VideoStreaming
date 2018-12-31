package Client.UI;


import Client.Login.Main;
import Client.Utility.GetIpService;
import Client.Utility.ResetThumbnailPathService;
import Client.Utility.ThumbnailReceiverService;
import com.jfoenix.controls.JFXButton;
import hubFramework.Video;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static Client.UI.MediaPlayerAndControlsController.videoPeerIP;

public class ShowHistoryController implements Initializable {

    // TODO: Could be used both for History and Trending


    @FXML
    ScrollPane clientScrollPane;


    private ArrayList<Video> list;
    VBox vbox;

    HashMap<String, ImageView > nameImageViewMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {



        if(MainPageController.IS_HISTORY){
            try {
                initHistory();

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else if(MainPageController.IS_TRENDING){
            // TODO: Trending stuff

            try{
                nameImageViewMap = new HashMap<>();
                initTrending();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    void initHistory() throws Exception{
        list = MainPageController.historyList;

        vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        int count = 50;
        for(Video v: list){
            if(count < 0)
                break;
            count--;

            AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/historyItem.fxml"));
            JFXButton button = (JFXButton) pane.getChildren().get(1);
            ImageView imgv = (ImageView)(pane.getChildren().get(0));

            button.setText(v.getVideoName());
            String thumbNailPath = v.getThumbnailPath();
            imgv.setImage(new Image(new FileInputStream(thumbNailPath)));

            button.setOnAction(e -> {
                String userName = v.getOwnerName();
                String channel = v.getChannelName();

                MediaPlayerAndControlsController.ownerName = userName;
                MediaPlayerAndControlsController.channelName = channel;
                MediaPlayerAndControlsController.videoPath = v.getPathOfVideo();
                VideoPlayerController.videoName = v.getVideoName();

                GetIpService gis = new GetIpService(userName, v.getVideoName(), channel);
                gis.start();

                gis.setOnSucceeded(event -> {
                    Stage playWindow = new Stage();
                    playWindow.initModality(Modality.APPLICATION_MODAL);

                    try{
                        Parent parent = FXMLLoader.load(getClass().getResource("../Layouts/videoPlayer.fxml"));
                        Scene sc = new Scene(parent);
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

                    }

                });


            });

            vbox.getChildren().add(0,pane);

        }
        clientScrollPane.setContent(vbox);
    }


    void initTrending() throws Exception{

        vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Socket sock = new Socket(Main.HUB_IP, 1111);
        DataInputStream dis = new DataInputStream(sock.getInputStream());
        DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

        dout.writeUTF("#TRENDING");

        System.out.println("Received: "+dis.readUTF());

        ThumbnailReceiverService thumbnailReceiverService = new ThumbnailReceiverService();
        thumbnailReceiverService.start();
        thumbnailReceiverService.setOnSucceeded(e->{
            putOnThumbnails();

            ResetThumbnailPathService r = new ResetThumbnailPathService();
            r.start();
        });
        //System.out.println("Received: "+dis.readUTF());
        MainPageController.trendingVideos = (HashMap<String, Video>)ois.readObject();

        ClientPageController.startResetingThumbNail = true;

        // TODO: Set up Trending UI

        HashMap<String, Video> map = MainPageController.trendingVideos;

        for(Map.Entry<String, Video> entry: map.entrySet()){
            AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/historyItem.fxml"));
            JFXButton button = (JFXButton) pane.getChildren().get(1);
            ImageView imgv = (ImageView)(pane.getChildren().get(0));

            button.setText(entry.getKey());

            nameImageViewMap.put(entry.getValue().getVideoName(), imgv);
            Video v = entry.getValue();
            button.setOnAction(e -> {
                String userName = v.getOwnerName();
                String channel = v.getChannelName();

                MediaPlayerAndControlsController.ownerName = userName;
                MediaPlayerAndControlsController.channelName = channel;
                MediaPlayerAndControlsController.videoPath = v.getPathOfVideo();
                VideoPlayerController.videoName = v.getVideoName();

                GetIpService gis = new GetIpService(userName, v.getVideoName(), channel);
                gis.start();

                gis.setOnSucceeded(event -> {
                    Stage playWindow = new Stage();
                    playWindow.initModality(Modality.APPLICATION_MODAL);

                    try{
                        Parent parent = FXMLLoader.load(getClass().getResource("../Layouts/videoPlayer.fxml"));
                        Scene sc = new Scene(parent);
                        playWindow.setScene(sc);
                        //playWindow.showAndWait();
                        playWindow.show();

                        playWindow.setOnCloseRequest(evt -> {
                            System.out.println("Video Player SetonClose calles");
                            MediaPlayerAndControlsController.mediaPlayer.stop();
                            try {
                                Socket socks = new Socket(videoPeerIP, 15001);
                                DataInputStream ds = new DataInputStream(socks.getInputStream());
                                DataOutputStream dot = new DataOutputStream(socks.getOutputStream());
                                ObjectInputStream os = new ObjectInputStream(socks.getInputStream());
                                ObjectOutputStream ooos = new ObjectOutputStream(socks.getOutputStream());

                                dot.writeUTF(Main.USERNAME);
                                ds.readBoolean();
                                dot.writeUTF("#DISCONNECT");

                                System.out.println("VideoPlayer Set on Close request exiting");

                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        });


                    }catch (Exception ex){

                    }

                });
            });

            vbox.getChildren().add(pane);
        }

        clientScrollPane.setContent(vbox);

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
