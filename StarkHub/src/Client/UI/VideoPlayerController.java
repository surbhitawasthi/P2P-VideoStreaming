package Client.UI;

import Client.Utility.CommentRecceiverService;
import Client.Utility.SetCommentsOnUIService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class VideoPlayerController implements Initializable {

    @FXML
    ScrollPane scrollPane;

    @FXML
    public static AnchorPane rootAnchorPane;

    VBox vbox;

    public static HashMap<String, String> commentsMap;
    public static String peerIP, videoName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try{
            init();
        }catch (Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        // TODO: Service to receive Comments
        if(!MediaPlayerAndControlsController.isAlternateIp) {
            CommentRecceiverService commentRecceiverService = new CommentRecceiverService(peerIP, videoName);
            commentRecceiverService.start();

            commentRecceiverService.setOnSucceeded(e -> {
                SetCommentsOnUIService scui = new SetCommentsOnUIService(vbox);
                scui.start();
            });
        }
    }



    void init() throws Exception{
        vbox = new VBox(10);
        vbox.setPrefWidth(1280);
        vbox.setMaxWidth(1280);

        AnchorPane mediaControlPane = FXMLLoader.load(getClass().getResource("../Layouts/mediaPlayerAndControls.fxml"));
        vbox.getChildren().add(mediaControlPane);

        System.out.println("Loaded mediaPlayerandControls");

        Separator separator = new Separator();
        separator.setPrefWidth(1270);
        separator.setHalignment(HPos.CENTER);
        vbox.getChildren().add(separator);

        AnchorPane commentsSectionPane = FXMLLoader.load(getClass().getResource("../Layouts/commentSectionLayout.fxml"));
        vbox.getChildren().add(commentsSectionPane);

        System.out.println("Loaded Comments Section");

        scrollPane.setContent(vbox);
    }

}
