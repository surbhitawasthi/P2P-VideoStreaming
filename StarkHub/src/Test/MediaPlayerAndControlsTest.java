package Test;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;

public class MediaPlayerAndControlsTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
//        Client.UI.MediaPlayerAndControlsController.videoPeerIP = "172.31.85.85";
//        Client.UI.MediaPlayerAndControlsController.videoPath = "Berklee.mp4";
//        Parent root = FXMLLoader.load(getClass().getResource("../Client/Layouts/mediaPlayerAndControls.fxml"));
//        primaryStage.setTitle("Stark Hub");
//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();
//        Client.UI.MediaPlayerAndControlsController.videoPeerIP = "172.31.85.85";
//        Client.UI.MediaPlayerAndControlsController.videoPath = "Berklee.mp4";

        try {
            Parent pane = new AnchorPane();
            MediaView mv = new MediaView();
            File f = new File("/home/aks/Videos/Berklee.mp4");
            MediaPlayer player = new MediaPlayer(new Media(f.toURI().toURL().toString()));
            player.setAutoPlay(true);
            mv.setMediaPlayer(player);

            ((AnchorPane) pane).getChildren().add(mv);

            primaryStage.setTitle("Stark Hub");
            primaryStage.setScene(new Scene(pane, 600, 400));
            primaryStage.show();
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        launch(args);
    }
}
