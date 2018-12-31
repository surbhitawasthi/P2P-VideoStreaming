package Client.Utility;

import Client.Login.Main;
import Client.UI.MainPageController;
import com.jfoenix.controls.JFXButton;
import hubFramework.Video;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.util.HashMap;
import java.util.Map;

public class SetUpNotificationPopupService extends Service {

    HashMap<String, Video> notificationMap;
    Circle notifCircle;

    SetUpNotificationPopupService(Circle notifCircle){
        notificationMap = MainPageController.notificationMap;
        this.notifCircle = notifCircle;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>(){

            @Override
            protected Void call() throws Exception {
                try {

                    for(Map.Entry<String, Video> e: notificationMap.entrySet()){

                        AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/notificationItem.fxml"));
                        JFXButton bt = (JFXButton)(pane.getChildren().get(0));
                        bt.setText(e.getKey());

                        bt.setOnAction(evt -> {
                            MainPageController.notificationMap.remove(e.getKey());
                            MainPageController.notificationVbox.getChildren().remove(pane);
                            // TODO: Open video
                        });

                        Platform.runLater(() -> {
                            MainPageController.notificationVbox.getChildren().add(pane);
                        });
                    }

                    Platform.runLater(() -> {
                        notifCircle.setOpacity(1.0);
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        };
    }
}
