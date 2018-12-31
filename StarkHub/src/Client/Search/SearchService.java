package Client.Search;

import Client.Login.Main;
import Client.UI.MainPageController;
import hubFramework.Video;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/*
        Fires up when Search is clicked
 */

public class SearchService extends Service {

    ArrayList<String> searchQuery;

    public SearchService(ArrayList<String> searchQuery){
        this.searchQuery = searchQuery;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try{

                    Socket sock = new Socket(Main.HUB_IP, 1111);
                    DataInputStream dis = new DataInputStream(sock.getInputStream());
                    DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                    // Search Criteria
                    if(MainPageController.SEARCH_CRITERIA == 0){
                        dout.writeUTF("#SEARCH");
                    }else if(MainPageController.SEARCH_CRITERIA == 3){
                        dout.writeUTF("#TAGSEARCH");
                    }

                    oos.writeObject(searchQuery);

                    System.out.println("Received: "+dis.readUTF());

                    MainPageController.searchVideosResult = (HashMap<String, Video>)(ois.readObject());
                    System.out.println("Results received: "+MainPageController.searchVideosResult);

                }catch(Exception ex){
                    ex.printStackTrace();
                }

                return null;
            }
        };
    }
}
