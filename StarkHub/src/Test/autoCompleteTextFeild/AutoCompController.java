package Test.autoCompleteTextFeild;

import com.jfoenix.controls.JFXListView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AutoCompController implements Initializable {


    @FXML
    TextField txtFeild;
    @FXML
    JFXListView listView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

//        String[] possibleOptions = {"Hello", "Hi", "Hell", "Lion", "Leo","Abhishek", "Abhinav", "Abhilasha"};
//
//        TextFields.bindAutoCompletion(txtFeild, possibleOptions);
//
//        listView.getItems().addAll(new Label("Hello"), new Label("Hi"), new Label("Aloha"));
//
//
//        ObservableList<Label> lst = listView.getSelectionModel().getSelectedItems();
//        System.out.println("list: "+lst);
//        for(Label l : lst){
//            System.out.println(""+l);
//        }
    }

    public void doIt(){
        ArrayList<Label> lst = (ArrayList<Label>) listView.getSelectionModel().getSelectedItems();
        System.out.println("list: "+lst);
        for(Label l : lst){
            System.out.println(""+l);
        }
    }
}
