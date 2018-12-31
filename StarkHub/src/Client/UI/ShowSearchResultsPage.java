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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import static Client.UI.MediaPlayerAndControlsController.videoPeerIP;

public class ShowSearchResultsPage implements Initializable {


    @FXML
    ScrollPane clientScrollPane;

    HashMap<String, Video> videoMap;
    HashMap<String, ImageView > nameImageViewMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ThumbnailReceiverService service = new ThumbnailReceiverService();
        service.start();
        System.out.println("ThumbNail service started");
        service.setOnSucceeded(e -> {
            System.out.println("thumbNail service succeded");
            putOnThumbnails();
            System.out.println("Thubnails displayed");

            ResetThumbnailPathService r = new ResetThumbnailPathService();
            r.start();

            r.setOnSucceeded(ev-> {
                System.out.println("Thumbnail path resetting service ended");
                //ClientPageController.startResetingThumbNail = false;
            });

        });

        nameImageViewMap = new HashMap<>();
        ClientPageController.startResetingThumbNail = true;

        try{
            videoMap = MainPageController.searchVideosResult;
            System.out.println("Search Results: "+videoMap);
            makeUI();
        }catch(Exception ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        ClientPageController.startResetingThumbNail = false;

    }


    public void makeUI(){
        VBox vBox = new VBox(50);
        vBox.setFillWidth(true);

        try{

            Reflection reflection = new Reflection();
            reflection.setFraction(0.50);

            if(videoMap!=null && !videoMap.isEmpty()){
                System.out.println("Setting up search UI");
                Iterator it = videoMap.entrySet().iterator();
                Label l = new Label("Search Results");
                l.setFont(Font.font(18));
                l.setEffect(reflection);

                vBox.getChildren().add(l);

                int k = videoMap.size();
                System.out.println("SearchResult size: "+ k );
                if(k<4)
                    k=4;
                k /= 4;
                if(k%4!=0)
                    k++;

                for(int i=0;i<k;i++){
                    HBox h = createSection();
                    System.out.println("created Section");
                    for(int j=0;j<4 && it.hasNext();j++){
                        System.out.println("Creating a single item");
                        String name = ((Map.Entry)it.next()).getKey().toString();
                        AnchorPane p = createSingleItem(name);
                        h.getChildren().add(p);
                    }
                    vBox.getChildren().add(h);
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        StackPane pane = new StackPane(vBox);
        pane.setPadding(new Insets(15));
        clientScrollPane.setContent(pane);

    }

    HBox createSection() throws Exception{
        HBox hbox  = new HBox(15);
        return hbox;
    }


    AnchorPane createSingleItem(String videoName) throws Exception{
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

            v = videoMap.get(vidName);

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
            if(videoMap.containsKey(vidName)){
                v = videoMap.get(vidName);
            }
            else if(videoMap.containsKey(vidName)){
                v = videoMap.get(vidName);
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
