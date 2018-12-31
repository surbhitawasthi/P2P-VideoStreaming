package Client.Utility;


import Client.UI.VideoPlayerController;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

/*
        Service to set comments on the UI when the VideoPlayer is Loaded
 */

public class SetCommentsOnUIService extends Service {

    VBox vbox;

    public SetCommentsOnUIService(VBox vbox){
        this.vbox = vbox;
    }


    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                try{
                    // TODO: Program Logic
                    HashMap<String, String> commentsMap = VideoPlayerController.commentsMap;

                    if(commentsMap!=null && !commentsMap.isEmpty()) {

                        for (Map.Entry<String, String> e : commentsMap.entrySet()) {
                            String name = e.getValue();
                            String comment = e.getKey();

                            AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/commentItem.fxml"));
                            JFXTextArea ta = (JFXTextArea) pane.getChildren().get(0);
                            ta.setPromptText(name);
                            ta.setText(comment);
                            ta.setEditable(false);

                            Platform.runLater(() -> {
                                vbox.getChildren().add(pane);
                            });
                        }
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }


                return null;
            }
        };
    }
}
