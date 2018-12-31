package Client.Utility;

import Client.UI.MainPageController;
import hubFramework.Video;
import javafx.application.Platform;
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
        Initialised at Startup
        Daemon for receiing notifications at Real-time
 */

public class NotificationServiceRealTime extends Service {

    Circle notifCircle;

    public NotificationServiceRealTime(Circle notifCircle){
        this.notifCircle = notifCircle;
    }

    @Override
    protected Task<Void> createTask() {

            return new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    try{
                        ServerSocket ssock = new ServerSocket(15003);
                        DataInputStream dis;
                        DataOutputStream dout;
                        ObjectInputStream ois;
                        ObjectOutputStream oos;

                        Socket sock;
                        // TODO: Receive some Data

                        while(true){
                            // TODO: Listen continously for notifications

                            sock = ssock.accept();
                            System.out.println("Notification Received");
                            dis = new DataInputStream(sock.getInputStream());
                            dout = new DataOutputStream(sock.getOutputStream());
                            ois = new ObjectInputStream(sock.getInputStream());
                            oos = new ObjectOutputStream(sock.getOutputStream());

                            try{

                                int num = dis.readInt();
                                System.out.println("Number of notifs: "+num);
                                String notification;
                                Video v;
                                for(int i=0;i<num;i++){
                                    notification = dis.readUTF();
                                    v = (Video)(ois.readObject());

                                    synchronized (this) {
                                        MainPageController.notificationMap.put(notification, v);
                                    }
                                }

                            }catch(Exception e) {
                                e.printStackTrace();
                            }

                            SetUpNotificationPopupService service = new SetUpNotificationPopupService(notifCircle);
                            service.start();

                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return null;
                }
            };
    }
}
