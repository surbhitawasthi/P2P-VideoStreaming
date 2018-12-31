package Client.Login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/*
    Main class: Starts the application
 */

public class Main extends Application {


    public static String HUB_IP = "172.31.84.87";
    //public static String HUB_IP = "192.168.43.240";
    public static int PORT = 1111;
    public static String USERNAME="";
    public static boolean isNewUser = true;


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../Layouts/loginLayout.fxml"));
        //primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Stark Hubs");
        Scene scene = new Scene(root);


        final double[] xoffset = new double[1];  // This is because variables used in lambda expression should
        final double[] yoffset = new double[1];  // final or effectively final. And IDE suggested this method

        root.setOnMousePressed(e -> {
            xoffset[0] = e.getSceneX();
            yoffset[0] = e.getSceneY();
        });

        root.setOnMouseDragged(e -> {
            primaryStage.setX(e.getScreenX() - xoffset[0]);
            primaryStage.setY(e.getScreenY() - yoffset[0]);
        });


        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
