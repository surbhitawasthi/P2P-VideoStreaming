package hubFramework;

import javafx.util.Pair;

import java.io.*;
import java.net.Socket;

public class SendRealTimeNotificationDaemon implements Runnable
{

    @Override
    public void run()
    {
        while(true)
        {
            String username;
            Pair<String, String> user;
            //TODO:synchronise all in one only
            synchronized (this) {
                user = HubServer.userListToNotify.poll();
            }

            if(user != null)
            {
                System.out.println("In real notification thread");
                username = user.getKey();
                try {
                    System.out.println("Making connection");
                    Socket socket = new Socket(user.getValue(), 15003);
                    synchronized (this) {
                        File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
                        ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
                        User obj = (User) readSerializedObject.readObject();
                        System.out.println("obj for " + username);
                        DataInputStream dis = new DataInputStream(socket.getInputStream());
                        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        System.out.println("Sending data");
                        dos.writeInt(obj.notification.size());
                        System.out.println("All notification in object before sending:\n"+obj.notification);
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
                        dis.close();
                        dos.close();
                        oos.close();
                        ois.close();
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                    synchronized (this) {
                        System.out.println("Adding to unsent notificataion list");
                        HubServer.unSentNotification.add(username);
                    }
                }
            }
        }
    }
}
