package hubFramework;

import javafx.util.Pair;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class Notification implements Runnable
{
    ArrayList<Video> videoNames;
    Channel ch;
    Notification(Channel ch, ArrayList<Video> videoNames)
    {
        this.ch = ch;
        this.videoNames = videoNames;
    }

    private Statement connect() {
        Statement stm = null;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/starkhub", "surbhit", "awasthi@7");
            stm = conn.createStatement();

        } catch (SQLException ex) {
            System.out.println("SQL me error!!!");
            ex.printStackTrace();
        }
        return stm;
    }
    // adding notification to subscribed users in the subscriber user list
    protected void addNotificationToUserObject() throws IOException, ClassNotFoundException, SQLException {
        ArrayList<String> subscriberList = ch.subscriberName;
        System.out.println("In notification adder thread");
        System.out.println("Subsription list "+subscriberList);
        for(String username : subscriberList)
        {
            System.out.println(username + " <- adding noti for him");
            synchronized (this) {
                File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
                ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
                User obj = (User) readSerializedObject.readObject();
                System.out.println("Adding in " + username+" videos: "+videoNames);
                for(Video video: videoNames)
                    obj.notification.add(new Pair<>(ch.channelName+" uploaded: "+video.videoName, video));

                String query = "SELECT ip FROM useripmap WHERE username='"+username+"';";
                Statement stm = connect();
                System.out.println("Conneted to db in noti");
                System.out.println(query);
                ResultSet rs = stm.executeQuery(query);
                System.out.println("rs found in noti");
                rs.next();

                ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                        new File(System.getProperty("user.home") + "/Hub/Client/" + username)));
                writeSerializedObject.writeObject(obj);
                writeSerializedObject.close();
                System.out.println("Done writting");
                HubServer.userListToNotify.add(new Pair<>(username, rs.getString(1)));
            }
        }
        System.out.println("out of notification add thread");
    }

    @Override
    public void run()
    {
        try {
            addNotificationToUserObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
