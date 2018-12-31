package Client.Utility;

import Client.Login.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/*
        Initiated  when a comment is made on a video
 */

public class CommentPostService extends Service {

    String peerIP;
    String comment, videoName, commentorName ;


    public CommentPostService(String peerIP, String comment, String videoName, String commentorName){
        this.peerIP = peerIP;
        this.comment = comment;
        this.videoName = videoName;
        this.commentorName = commentorName;
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

                    System.out.println("CommentPostService: Socket "+sock);

                    dout.writeUTF(Main.USERNAME);
                    boolean bool = dis.readBoolean();
                    System.out.println("CommentPostService: Read boolean "+bool);


                    dout.writeUTF("#ADDCOMMENT");
                    System.out.println("CommentPostService: VideoName"+ videoName);
                    dout.writeUTF(videoName);
                    System.out.println("VideoName Sent");
                    System.out.println("CommentPostService: CommentorName: "+commentorName);
                    dout.writeUTF(commentorName);
                    System.out.println("CommentorName sent");
                    System.out.println("CommentPostService: Comment "+comment);
                    dout.writeUTF(comment);
                    System.out.println("Comment Sent");
                    
                }catch(Exception e){
                    e.printStackTrace();
                }


                return null;
            }
        };
    }
}
