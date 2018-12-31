package hubFramework;

import javafx.util.Pair;

import java.io.*;
import java.net.Socket;

public class SendLoginTimeNotificationDaemon implements Runnable
{
    String username, ip;
    SendLoginTimeNotificationDaemon(String username, String ip)
    {
        this.username = username;
        this.ip = ip;
    }

    @Override
    public void run()
    {
        try {
            synchronized (this)
            {
                System.out.println("In login time noti");
                if(HubServer.unSentNotification.contains(username))
                {
                    System.out.println("login time noti user present" + username);
                    File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
                    ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
                    User obj = (User) readSerializedObject.readObject();
                    System.out.println("obj for " + username);
                    Socket socket = new Socket(ip,15002);
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    if(obj.notification.size() == 0)
                        dos.writeBoolean(false);
                    else {
                        dos.writeBoolean(true);
                        dos.writeInt(obj.notification.size());

                        Pair<String, Video> message;
                        while((message = obj.notification.poll()) != null ) {
                            //message = obj.notification.poll();
                            System.out.println("Sendng: "+message.getKey());
                            dos.writeUTF(message.getKey());
                            oos.writeObject(message.getValue());
                        }
                        System.out.println("Written msgs");
                        System.out.println("All notification in object after sending:\n"+obj.notification);
                        System.out.println("Writting back");
                        ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                                new File(System.getProperty("user.home") + "/Hub/Client/" + username)));
                        writeSerializedObject.writeObject(obj);
                        writeSerializedObject.close();
                    }
                    dis.close();
                    dos.close();
                    oos.close();
                    ois.close();
                    System.out.println("Written or sent false msgs");
                    HubServer.unSentNotification.remove(username);
                    dos.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
