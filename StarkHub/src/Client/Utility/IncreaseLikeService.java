package Client.Utility;

import Client.UI.MediaPlayerAndControlsController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static Client.Login.Main.HUB_IP;

/*
    Initialised when Like button Clicked
 */

public class IncreaseLikeService extends Service {


    String ownerName, channelName, videoName;

    public IncreaseLikeService(String ownerName, String channelName, String videoName){
        this.ownerName = ownerName;
        this.channelName = channelName;
        this.videoName = videoName;
    }


    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try{

                    Socket socket = new Socket(HUB_IP, 1111);
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                    dout.writeUTF("#SETLIKES");
                    dout.writeUTF(ownerName);
                    System.out.println("Sent OwnerName: "+ownerName);
                    dout.writeUTF(channelName);
                    System.out.println("Sent ChannelName: "+channelName);
                    dout.writeUTF(videoName);
                    System.out.println("Sent VideoName: "+videoName);

                    dout.writeUTF("#ADD");


                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
