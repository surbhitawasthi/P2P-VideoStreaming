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

public class MainPageController implements Initializable {

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


    // Create JFXButton
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
        vbox.getChildren().add(createDashboardButton("Dashboard"));
        vbox.getChildren().add(createCreatorDashboardButton("Creator Dashboard"));
        vbox.getChildren().add(createMyCHannelsButton("My Channels"));
        vbox.getChildren().add(createLogOutButton("Log Out"));
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



    // Creating DashBoard Button
    JFXButton createDashboardButton(String text){
        text = "Dashboard";
        JFXButton button = new JFXButton(text);

        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setPrefWidth(150);
        button.setPadding(new Insets(10));

        button.setOnAction(e -> dashBoardButtonClicked() );

        return button;
    }



    // Creating Creator Dashboard Button
    JFXButton createCreatorDashboardButton(String text){
        text = "Creator Dashboard";
        JFXButton button = new JFXButton(text);

        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setPrefWidth(150);
        button.setPadding(new Insets(10));

        button.setOnAction(e -> creatorDashBoardButtonClicked() );

        return button;
    }



    // Creating MyCHannels Button
    JFXButton createMyCHannelsButton(String text){
        text = "My Channels";
        JFXButton button = new JFXButton(text);

        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setPrefWidth(150);
        button.setPadding(new Insets(10));

        button.setOnAction(e -> myChannelsButtonClicked() );

        return button;
    }




    // Creating LogOut Button
    JFXButton createLogOutButton(String text){
        text = "Log Out";
        JFXButton button = new JFXButton(text);

        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setPrefWidth(150);
        button.setPadding(new Insets(10));

        button.setOnAction(e -> logOutButtonClicked() );

        return button;
    }




    void logOutButtonClicked(){
        System.out.println("Logout Clicked");
    }

    void myChannelsButtonClicked(){
        System.out.println("My Channels clicked");
    }

    void dashBoardButtonClicked(){
        System.out.println("Dashboard Clicked");
    }

    void creatorDashBoardButtonClicked(){
        System.out.println("Creator Dashboard clicked");
    }

}
