package Client.Utility;

import Client.DataClasses.Video;
import Client.Login.Main;
import Client.UI.MediaPlayerAndControlsController;
import Client.UI.VideoPlayerController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import static Client.Login.Main.HUB_IP;

/*
        Contacting HUB to get the IP of
        the serving Peer.
 */

public class GetIpService extends Service {

    String userName;
    String videoName;
    String channelName;

    public GetIpService(String userName, String videoName, String channelName){
        this.userName = userName;
        this.videoName = videoName;
        this.channelName = channelName;
    }

    @Override
    protected Task<Void> createTask()  {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try{

                    Socket socket = new Socket(HUB_IP, 1111);
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                    dout.writeUTF("#GETIP");
                    dout.writeUTF(Main.USERNAME);
                    dout.writeUTF(userName);
                    dout.writeUTF(videoName);
                    dout.writeUTF(channelName);

                    String ip = dis.readUTF();
                    System.out.println("Received ip from hub:"+ ip);
                    String altIp = "";
                    if(dis.readBoolean()){
                        altIp = dis.readUTF();
                    }

                    // PINGING the peer to see if it's online
                    try{
                        Socket sock = new Socket(ip, 15001);
                        DataInputStream ds = new DataInputStream(sock.getInputStream());
                        DataOutputStream dt = new DataOutputStream(sock.getOutputStream());
                        ObjectInputStream os = new ObjectInputStream(sock.getInputStream());
                        ObjectOutputStream ooos = new ObjectOutputStream(sock.getOutputStream());

                        dt.writeUTF("#PING");
                        ois.close();
                        ooos.close();
                        sock.close();
                    }catch (Exception e){
                        // Switching to alternate IP if peer is down
                        System.out.println("Error in connecting to primary host, switching to alternate host");
                        ip = altIp;
                        MediaPlayerAndControlsController.isAlternateIp = true;
                    }

                    MediaPlayerAndControlsController.videoPeerIP = ip;
                    VideoPlayerController.peerIP = MediaPlayerAndControlsController.videoPeerIP;

                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
