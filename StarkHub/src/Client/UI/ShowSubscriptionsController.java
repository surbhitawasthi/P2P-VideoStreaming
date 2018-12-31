package Client.UI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ShowSubscriptionsController implements Initializable {

    @FXML
    ScrollPane clientScrollPane;

    VBox vbox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            init();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void init() throws Exception{
        vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        HashMap<String, String> map = MainPageController.subscribedChannelMap;
        for(Map.Entry<String, String> e: map.entrySet()){
            AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/subscriptionItem.fxml"));
            Label ownerLabel = (Label)(pane.getChildren().get(0));
            Label channelLabel = (Label)(pane.getChildren().get(1));

            ownerLabel.setText(e.getKey());
            channelLabel.setText(e.getValue());

            vbox.getChildren().add(pane);
        }

        clientScrollPane.setContent(vbox);
    }
}
