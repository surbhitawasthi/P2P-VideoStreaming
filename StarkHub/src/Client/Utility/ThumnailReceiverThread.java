package Client.Utility;

import Client.Login.Main;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/*
        TODO: No longer used
 */

public class ThumnailReceiverThread implements Runnable{
    @Override
    public void run() {
        try{
            ServerSocket serverSocket = new ServerSocket(11234);
            Socket sock = serverSocket.accept();

            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

            int numberOfThumbs = ois.readInt();
            for(int i=0;i<numberOfThumbs;i++){
                String thumbName = ois.readUTF();
                String savePath = System.getProperty("user.home")+"/starkhub/"+ Main.USERNAME +"/temp/"+thumbName;
                (new SaveFile()).saveFile(savePath,ois);
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
