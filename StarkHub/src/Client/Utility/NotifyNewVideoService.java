package Client.Utility;

import Client.DataClasses.Video;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static Client.Login.Main.HUB_IP;
import static Client.Login.Main.PORT;
import static Client.Login.Main.USERNAME;

public class NotifyNewVideoService extends Service {

    ArrayList<Video> videoList;
    String channelName;
    public NotifyNewVideoService( ArrayList<Video> videoList, String channelName){
        this.videoList = videoList;
        this.channelName = channelName;
    }


    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try{

                    if(!(videoList.isEmpty())){

                        Socket sock = new Socket(HUB_IP, PORT);
                        DataInputStream diss = new DataInputStream(sock.getInputStream());
                        DataOutputStream douts = new DataOutputStream(sock.getOutputStream());
                        ObjectInputStream oiss = new ObjectInputStream(sock.getInputStream());
                        ObjectOutputStream ooss = new ObjectOutputStream(sock.getOutputStream());


                        douts.writeUTF("#ADDVIDEOINCHANNEL");
                        douts.writeUTF(USERNAME);
                        douts.writeUTF(channelName);
                        douts.writeInt(videoList.size());

                        for(Video v: videoList){
                            System.out.println("Sending: "+v.getVideoName()+" to hub");

                            douts.writeUTF(v.getVideoPath());
                            ooss.writeObject(v.getTags());

//                    String outPath = generateThumbnail(v.getVideoPath());
//                    v.setThumbnail(outPath);

                            //ooss.writeObject(v.getThumbnail());
                            // Sending Thumbnail to hub
                            SendFile sendFileObj = new SendFile();
                            sendFileObj.sendFile(sock, v.getThumbnailPath(), ooss);

                            System.out.println("ThumbNail Sent");
                        }
                        sock.close();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
