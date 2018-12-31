package Test;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;


public class VideoPlayTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, 600, 400);


        String source = "http://localhost/Videos/Berklee.mp4";
        //source = getParameters().getRaw().get(0);
        System.out.println(source);
        Media media = new Media(source);


        // Create the player and set to play automatically.
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);

        // Create the view and add it to the Scene.
        MediaView mediaView = new MediaView(mediaPlayer);

        DoubleProperty mvw = mediaView.fitWidthProperty();
        DoubleProperty mvh = mediaView.fitHeightProperty();
        mvw.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        mvh.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

        mvw.bind(scene.widthProperty());
        mvh.bind(scene.heightProperty());

        mediaView.setPreserveRatio(false);

        root.getChildren().addAll(mediaView);

        Timeline slideIn = new Timeline();
        Timeline slideOut = new Timeline();


        VBox v = new VBox();
        Slider slider  = new Slider();
        v.getChildren().add(slider);

        //v.getChildren().addAll(v);
        root.getChildren().add(v);

        stage.setScene(scene);

        mediaPlayer.setOnReady(()->{
            int h = (int)scene.getHeight();
            int w = (int)scene.getWidth();
            v.setMinSize(w,100);
            v.setTranslateY(h-100);

            slider.setMin(0.0);
            slider.setValue(0.0);
            slider.setMax(mediaPlayer.getMedia().getDuration().toSeconds());

            slideIn.getKeyFrames().addAll(
                    new KeyFrame(new Duration(0),new KeyValue(v.translateYProperty(), h), new KeyValue(v.opacityProperty(), 0.0)),
                    new KeyFrame(new Duration(300), new KeyValue(v.translateYProperty(), h-100), new KeyValue(v.opacityProperty(), 0.5))
            );

            slideOut.getKeyFrames().addAll(
                    new KeyFrame(new Duration(0),new KeyValue(v.translateYProperty(), h-100), new KeyValue(v.opacityProperty(), 0.5)),
                    new KeyFrame(new Duration(300), new KeyValue(v.translateYProperty(), h), new KeyValue(v.opacityProperty(), 0.0))
            );
        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> slider.setValue(newValue.toSeconds()));

        slider.setOnMousePressed(e -> {
            mediaPlayer.seek(Duration.seconds(slider.getValue()));
        });

        slider.setOnMouseDragged(e -> {
            mediaPlayer.seek(Duration.seconds(slider.getValue()));
        });

        slider.setOnMouseEntered(e -> {
            slider.setOpacity(0.9);
            v.setOpacity(0.9);
        });
        slider.setOnMouseExited(e -> {
            slider.setOpacity(0.5);
            v.setOpacity(0.5);
        });


        root.setOnMouseEntered(e -> {
            System.out.println("mouse entered");
            slideIn.play();
        });
        root.setOnMouseExited(e -> {
            System.out.println("mouse Exited");
            slideOut.play();
        });

        // Name and display the Stage.
        stage.setTitle("Hello Media");
        stage.show();

        // Create the media source.
//        String source = "http://localhost/Videos/Berklee.mp4";
//         //source = getParameters().getRaw().get(0);
//        System.out.println(source);
//        Media media = new Media(source);
//
//
//        // Create the player and set to play automatically.
//        MediaPlayer mediaPlayer = new MediaPlayer(media);
//        mediaPlayer.setAutoPlay(true);
//
//        // Create the view and add it to the Scene.
//        MediaView mediaView = new MediaView(mediaPlayer);
//
//        DoubleProperty mvw = mediaView.fitWidthProperty();
//        DoubleProperty mvh = mediaView.fitHeightProperty();
//        mvw.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
//        mvh.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
//
//        mediaView.setPreserveRatio(false);

        //((Group) scene.getRoot()).getChildren().add(mediaView);
    }
}
