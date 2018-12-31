package Client.Utility;

import Client.Login.Main;
import Client.UI.VideoPlayerController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;


/*
        initiated when a comment has been made on
        one of this peer's videos
 */

public class CommentRecceiverService extends Service {


    String peerIP;
    String videoName;

    public CommentRecceiverService(String peerIP, String videoName){
        this.peerIP = peerIP;
        this.videoName = videoName;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                try{
                    // TODO: Program Logic

                    Socket sock = new Socket(peerIP,15001);
                    DataInputStream dis = new DataInputStream(sock.getInputStream());
                    DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                    dout.writeUTF(Main.USERNAME);
                    dis.readBoolean();
                    dout.writeUTF("#GETCOMMENTS");
                    dout.writeUTF(videoName);

                    VideoPlayerController.commentsMap = (HashMap<String, String>) ois.readObject();

                }catch(Exception e){
                    e.printStackTrace();
                }


                return null;
            }
        };
    }
}
