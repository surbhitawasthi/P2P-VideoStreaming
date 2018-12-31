package Client.Java;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPopup;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MainFrameController implements Initializable {

    @FXML
    JFXButton btn;
    @FXML
    AnchorPane contentAnchorPane, menuBtnAnchor;
    @FXML
    ImageView menuButton;

    JFXPopup popup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AnchorPane pane  = FXMLLoader.load(getClass().getResource("../Layouts/clientPage.fxml"));
            contentAnchorPane.getChildren().setAll(pane);
        } catch (Exception e) {
            e.printStackTrace();
        }


        popup = initPopup();


    }


    //Create JFXButton
    JFXButton createButton(String text){
        JFXButton button = new JFXButton(text);

        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setPrefWidth(150);
        button.setPadding(new Insets(10));

        button.setOnAction(e -> testMethod() );

        return button;
    }

    // Initialize Popup
    JFXPopup initPopup(){
        VBox vbox = new VBox(10);
        for(int i=0;i<4;i++){
            vbox.getChildren().add(createButton("Option "+i));
        }
        JFXPopup popup = new JFXPopup(vbox);
        return popup;
    }

    // Show Popup
    public void openMenu(MouseEvent e){
        popup.show(menuBtnAnchor, JFXPopup.PopupVPosition.TOP,JFXPopup.PopupHPosition.LEFT, e.getX(), e.getY());
    }

    // Test method
    void testMethod(){
        System.out.println("On action working");
    }
}
