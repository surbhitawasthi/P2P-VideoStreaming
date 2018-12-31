package Client.Utility;

import Client.Login.LayoutController;
import Client.UI.MainPageController;
import hubFramework.Video;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.shape.Circle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/*
        Initialised at login
        Receives pending notifications
        while the user was offline
 */

public class NotificationServiceAtLogin extends Service {

    Circle notifCircle;
    public NotificationServiceAtLogin(Circle notifCircle){
        this.notifCircle = notifCircle;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try{
                    ServerSocket ssock = new ServerSocket(15002);
                    Socket sock = ssock.accept();
                    DataInputStream dis = new DataInputStream(sock.getInputStream());
                    DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                    // TODO: Receive some Data

                    if(dis.readBoolean()){
                        // TODO: get the notifications
                        try{

                            int num = dis.readInt();
                            String notification;
                            Video v;
                            for(int i=0;i<num;i++){
                                notification = dis.readUTF();
                                v = (Video)(ois.readObject());

                                while(!LayoutController.isNotifReady){
                                    Thread.sleep(100);
                                }

                                synchronized (this) {
                                    MainPageController.notificationMap.put(notification, v);
                                }
                            }

                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    }

                    ssock.close();
                    dout.close();
                    dis.close();
                    sock.close();

                    SetUpNotificationPopupService service = new SetUpNotificationPopupService(notifCircle);
                    service.start();

                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
