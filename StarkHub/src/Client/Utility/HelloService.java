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
        Intimating the HUB that the peer is UP
 */

public class HelloService extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try{

                    Socket socket = new Socket(Main.HUB_IP,1111);
                    DataInputStream  dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                    dout.writeUTF("#HELO");
                    System.out.println("Sent #HELO");
                    dout.writeUTF(Main.USERNAME);
                    System.out.println("Sent Username");
                    ois.close();
                    oos.close();
                    socket.close();

                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
