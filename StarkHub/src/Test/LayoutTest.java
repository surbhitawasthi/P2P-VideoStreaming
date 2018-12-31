package Test;

import com.google.common.hash.Hashing;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;

public class LayoutTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("../Client/Layouts/mediaPlayerAndControls.fxml"));
//        primaryStage.setTitle("Stark Hub");
//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();
    }


    public static void main(String[] args) {

        //launch(args);
        String hashed = Hashing.sha256()
                .hashString("hass@1120", StandardCharsets.UTF_8)
                .toString();

        System.out.println(hashed);
    }
}
