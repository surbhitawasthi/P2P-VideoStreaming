package Client.UI;

import Client.DataClasses.Channel;
import Client.DataClasses.Video;
import Client.Utility.NotifyNewChannelService;
import Client.Utility.SendFile;
import com.jfoenix.controls.*;
import com.sun.deploy.util.SessionState;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import static Client.Login.Main.*;

public class AddChannelController implements Initializable {

    @FXML
    JFXButton addVideosButton, createChannelButton, discardButton;

    @FXML
    JFXTextField channelNameTxt;

    @FXML
    AnchorPane addChannelAnchorPane, loadingAnchorPane, holderAnchorPane;

    @FXML
    JFXTextArea tagsTextArea;

    @FXML
    JFXListView<Label> videoListView;

    @FXML
    ScrollPane scrollPane;

    HBox hbox;

    ArrayList<Video> videoList;
    FileChooser fileChooser ;

    String userHome;

    //String outPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        videoList = new ArrayList<>();
        fileChooser = new FileChooser();
        userHome = System.getProperty("user.home");
        videoListView.setExpanded(true);
        videoListView.setPadding(new Insets(10));
        videoListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        hbox = new HBox(25);
        hbox.setPadding(new Insets(10));
        scrollPane.setContent(hbox);
        //outPath = userHome+"/starkhub/thumbnails/out.png";
        loadingAnchorPane.setVisible(false);
        holderAnchorPane.setVisible(true);
    }

    // Creating channel and notifying HUB
    public  void createChannel(){
        String channelName = channelNameTxt.getText();
        File channelFile = new File(userHome+"/starkhub/"+USERNAME+"/mychannels/list");
        System.out.println("Channel File path: "+channelFile.getAbsolutePath());
        if(MainPageController.myChannelMap.containsKey(channelName)){
            System.out.println("Error...Select another Channel name");
            // TODO: Show popup -> Choose another channel name
            JFXPopup popup = initAnotherChannelName();
            popup.show(channelNameTxt,JFXPopup.PopupVPosition.TOP ,JFXPopup.PopupHPosition.LEFT);

        }else{
            try{
                //channelFile.createNewFile();
                Channel channel = new Channel(channelName, USERNAME);
                System.out.println("VideoList of the Channel: "+videoList);
                channel.setVideoList(videoList);
                channel.setNumberOfVideos(videoList.size());

                MainPageController.myChannelMap.put(channel.getChannelName(), channel);
                HashMap<String, Channel> map = MainPageController.myChannelMap;
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(channelFile));
                oos.writeObject(map);
                oos.close();


            }catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

            try{

                showLoading();

                // Service for Notifying the HUB about the new Channel
                NotifyNewChannelService nns = new NotifyNewChannelService(videoList, channelName);
                nns.start();
                nns.setOnSucceeded(e -> {
                    System.out.println("nns Succeded");
                    showLoading();
                    videoListView.getItems().clear();
                    hbox.getChildren().clear();
                    channelNameTxt.clear();
                    //videoList.clear();
                });

            }catch(Exception e){
                e.printStackTrace();
            }
        }

        //Todo: Redirect to home page or my channels page after creating the channel
        //videoList.clear();
    }


    public void addVideos(){

        try {
            fileChooser.setInitialDirectory(new File(userHome));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Videos", "*.mp4","*.mkv","*.avi","*.webm"),
                    new FileChooser.ExtensionFilter("All","*.*")
            );
            File file = fileChooser.showOpenDialog((Stage) (addChannelAnchorPane.getScene().getWindow()));
            Video v = new Video(file.getName(), null, file.getAbsolutePath());
            videoList.add(v);

            videoListView.getItems().add(new Label(file.getName()));

//            File f = new File(outPath);
//            if(f.exists() && f.delete()){
//                System.out.println("ThumbNail deleted Successfully");
//            }else System.out.println("Failed to delete Thumbnail");

            String outPath = generateThumbnail(v.getVideoPath());
            //v.setThumbnail(outPath);
            v.setThumbnailPath(outPath);



            AnchorPane thumbPane = FXMLLoader.load(getClass().getResource("../Layouts/videoThumbnailView.fxml"));
            ((ImageView)thumbPane.getChildren().get(0)).setImage(v.getThumbnail());
            ((JFXButton)thumbPane.getChildren().get(1)).setText(v.getVideoName());
            hbox.getChildren().add(thumbPane);


        }catch(Exception e){
            System.out.println(e.getMessage());
            //System.out.println("No file Selected");
            e.printStackTrace();
        }
    }

    public void discard(){
        /*
            // TODO: Load back the main UI

         */
        showLoading();

    }

    // Show a loading component while data is being fetched or transfered
    void showLoading(){
        if(!loadingAnchorPane.isVisible()) {
            System.out.println("Showing loading gif");
            loadingAnchorPane.setOpacity(1.0);
            loadingAnchorPane.setVisible(true);
            addVideosButton.setDisable(true);
            addVideosButton.setDisable(true);
            createChannelButton.setDisable(true);
        }
        else {
            System.out.println("Removing loading gif");
            loadingAnchorPane.setVisible(false);
            loadingAnchorPane.setOpacity(0.0);
            loadingAnchorPane.setVisible(true);
            addVideosButton.setDisable(false);
            addVideosButton.setDisable(false);
            createChannelButton.setDisable(false);
        }
    }


    // Generate Thumbnails from videos
    String generateThumbnail(String path) throws Exception{
        String outPath = userHome+"/starkhub/"+USERNAME+"/thumbnails/out.png";
        String time = "00:00:20";
           // String outPath = System.getProperty("user.home") + "/Desktop/out.png";
        String vidName = path.substring(path.lastIndexOf("/")+1);
        String firstPath = path.substring(0, path.lastIndexOf("/")+1);
        System.out.println(vidName+"\n"+firstPath);

        vidName = vidName.replaceAll(" ","");
        outPath = outPath.replaceAll("out", vidName.substring(0,vidName.indexOf('.')));

        System.out.println("OutPath: "+outPath);

        File f = new File(outPath);
        if(f.exists() && f.delete()){
            System.out.println("ThumbNail deleted Successfully");
        }else System.out.println("Failed to delete Thumbnail");

        if((new File(path)).renameTo(new File(firstPath+vidName))){
            System.out.println("File successfully renamed");
        }else System.out.println("Renaming failed");

        String command = "ffmpeg "+" -ss "+time+" -i "+firstPath+"'"+vidName+"'"+" -vf scale=-1:120  -vcodec png "+outPath;
        //System.out.println(command);

        String command2 = "ffmpeg -ss 00:00:20 -i "+firstPath+""+vidName+"" +" -vf scale=-1:120 -vframes 1 " + outPath;
        System.out.println(command2);

        Runtime run = Runtime.getRuntime();
        Process p = run.exec(command2);
        p.waitFor();


        System.out.println("Thumbnail Created");

        if((new File(firstPath+vidName)).renameTo(new File(path))){
            System.out.println("Name of file reverted back");
        }else System.out.println("Reverting failed");

        return outPath;
    }



    // Adding tags to selected Videos
    public void addTagsToVideo(){

        System.out.println("Add Tags Clicked");

        ArrayList<String > videoNames = new ArrayList<>();

        ObservableList<Label> selectedLabels =  videoListView.getSelectionModel().getSelectedItems();
        if(selectedLabels.isEmpty()) {
            System.out.println("No Videos Selected..!!");
            JFXPopup popup = initNoVideosPopup();
            popup.show(videoListView,JFXPopup.PopupVPosition.TOP ,JFXPopup.PopupHPosition.LEFT);
            return;
        }

        for(Label l: selectedLabels){
            videoNames.add(l.getText());
        }

        System.out.println("VideoNames: "+videoNames);

        ArrayList<String> tags = getTagsFromTextArea();

        if(tags.isEmpty()) {
            System.out.println("No tags specified");
            return;
        }

        for(String videoName: videoNames) {
            for (Video v : videoList) {
                if (videoName.equals(v.getVideoName())) {
                    v.setTags(tags);
                }
            }
        }

        System.out.println("Tags Added");

        videoListView.getSelectionModel().clearSelection();
        tagsTextArea.clear();

    }

    // Adding tags to video Objects
    ArrayList<String> getTagsFromTextArea(){
        String rawString = tagsTextArea.getText();
        StringTokenizer tokenizer = new StringTokenizer(rawString, ",");
        ArrayList<String> extractedTags = new ArrayList<>();

        while(tokenizer.hasMoreTokens()){
            extractedTags.add((tokenizer.nextToken()).trim());
        }

        return extractedTags;
    }

    // initialize no Videos Selected POPUP
    JFXPopup initNoVideosPopup(){
        Label l = new Label("No Videos Selected");
        l.setWrapText(true);
        VBox vbox = new VBox(l);
        vbox.setPadding(new Insets(10));
        JFXPopup p = new JFXPopup(vbox);
        return p;
    }

    // initialize duplicate channel name POPUP
    JFXPopup initAnotherChannelName(){
        Label l = new Label("Select another Channel Name");
        l.setWrapText(true);
        VBox vbox = new VBox(l);
        vbox.setPadding(new Insets(10));
        JFXPopup p = new JFXPopup(vbox);
        return p;
    }

}
