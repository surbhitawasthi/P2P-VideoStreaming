package hubFramework;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class HubPeerCommunication implements Runnable {

    private Peer peer;
    HubPeerCommunication(Peer peer)
    {
        this.peer = peer;
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

    @Override
    public void run()
    {
        try {
            String flag = peer.dis.readUTF();
            switch (flag) {
                case "#USERNAME":
                    System.out.println("#USERNAME");
                    authenticate(); // method for authentication of username
                    break;
                case "#NEWUSER":
                    System.out.println("#NEWUSER");
                    newUser(); // setUp new user account and house keeping stuff
                    break;
                case "#EXISTINGUSER":
                {
                    System.out.println("#EXISTINGUSER");
                    String username = peer.dis.readUTF();
                    Statement stm = connect();
                    stm.executeUpdate("update useripmap set ip='"+peer.peerSocket.getInetAddress().getHostAddress()
                            +"' where username='"+username+"'");
                    existingUser(username, 2); // send back videos
                    break;
                }
                case "#SEARCH": {
                    System.out.println("#SEARCH");
                    search(); //search for videos in db
                    break;
                }
                case "#TAGSEARCH": {
                    System.out.println("#TAGSEARCH");
                    tagSearch(); //tag_search for videos in db
                    break;
                }
                case "#MAKECHANNEL": {
                    System.out.println("#MAKECHANNEL");
                    makeChannel(); //make a new channel for a username
                    break;
                }
                case "#ADDVIDEOINCHANNEL":
                {
                    addVideoInChannel();
                    break;
                }
                case "#SETLIKES":
                {
                    System.out.println("#SETLIKES");
                    setLikes(); //increment or decrement likes on a video
                    break;
                }
                case "#TRENDING":
                {
                    trending(); // send back trending list
                    break;
                }
                case "#COMMENTNUMBER":
                {
                    System.out.println("#COMMENTNUMBER");
                    commentNumber(); // increment count of all comments in the specified video and channel
                    break;
                }
                case "#GETSTATOFCHANNEL":
                {
                    System.out.println("#GETSTATOFCHANNEL");
                    getStatOfChannel(); // send stat of particular channel
                    break;
                }
                case "#ADDSUBSCRIBER":
                {
                    System.out.println("#ADDSUBSCRIBER");
                    addSubscriber(); // add person in subscriber list
                    break;
                }
                case "#GETIP":
                {
                    System.out.println("#GETIP");
                    getIP(); // send back ip or alternate ip
                    break;
                }
                case "#PREMIUM":
                {
                    System.out.println("#PREMIUM");
                    premium(); // make videos premium
                    break;
                }
                //house keeping function for wrapping up multiple start up and end time file
                case "#HELO":
                {
                    System.out.println("#HELO");
                    hello();
                    System.out.println("Back from helo function");
                    break;
                }
                case "#BYE":
                {
                    System.out.println("#BYE");
                    bye();
                    break;
                }
                default:
                    System.out.println("Ye kya flag hai "+peer.peerSocket.getInetAddress().getHostAddress()+" bhai: "+ flag);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        finally {
            //try {
                //peer.dis.close();
                //peer.dos.close();
                //peer.oos.close();
                //peer.ois.close();
                //peer.peerSocket.close();
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}
        }
    }

    private void premium() throws SQLException, IOException {
        Statement stm = connect();
        String username = peer.dis.readUTF();
        System.out.println("In premium got username as"+username);
        String queryForUserSelection = "SELECT username FROM useripmap WHERE dataWritten='n' AND " +
                "username <> '"+username+"' ORDER BY finalUptime DESC LIMIT 5;";
        System.out.println(queryForUserSelection);
        ResultSet rs = stm.executeQuery(queryForUserSelection);
        ArrayList<String> userlist = new ArrayList<String>();
        while(rs.next())
        {
            userlist.add(rs.getString(1));
        }
        String targetUsername = userlist.get((int)(Math.random()*userlist.size()));
        String query = "SELECT ip FROM useripmap WHERE username='"+targetUsername+"';";
        System.out.println("Conneted to db");
        System.out.println(query);
        rs = stm.executeQuery(query);
        System.out.println("rs found");
        rs.next();
        String targetIP = rs.getString(1);
        try{
            Socket targetSocket = new Socket(targetIP, 15001);
            Peer targetPeer = new Peer(targetSocket);
            targetPeer.dos.writeUTF("HUB");
            targetPeer.dis.readBoolean();
            targetPeer.dos.writeUTF("#PING");
            query = "UPDATE useripmap SET alternateip='"+targetIP+"' where username='"+username+"';";
            System.out.println(query);
            stm.executeUpdate(query);
            System.out.println("alternate ip added for user"+username);
            peer.dos.writeBoolean(true);
            System.out.println("True send and starting receive thread");
            new Thread(new ReceiveAndStoreData(username, peer, targetUsername, targetIP)).start();
            //new Thread(new SendData(new Peer()) -> do this at end of

            // updating n to y in table
            String queryDataWritten = "UPDATE useripmap SET dataWritten='y' WHERE username='"+targetUsername+"';";
            stm.executeUpdate(queryDataWritten);
        } catch (ConnectException e) {
            peer.dos.writeBoolean(false);
            System.out.println("Connect exception unable to connect to alternate ip");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        catch (UnknownHostException e)
        {
            peer.dos.writeBoolean(false);
            System.out.println("UnknownHost exception unable to connect to alternate ip");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void commentNumber()
    {
        try {
            String username = peer.dis.readUTF();
            String channelName = peer.dis.readUTF();
            String videoName = peer.dis.readUTF();
            File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
            ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
            User obj = (User) readSerializedObject.readObject();
            System.out.println("obj for " + username);
            int f = 0;
            for (Channel ch : obj.channels)
            {
                if (ch.channelName.equals(channelName)) {
                    for (Video vid : ch.videos) {
                        if (vid.videoName.equals(videoName)) {
                            f = 1;
                            //System.out.println(vid.videoName);
                            vid.numberOfComments += 1;
                            ch.totalNoOfComments += 1;
                            break;
                        }
                    }
                    if(f == 1)
                        break;
                }
            }
            System.out.println("Writting back");
            ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                    new File(System.getProperty("user.home") + "/Hub/Client/" + username)));
            writeSerializedObject.writeObject(obj);
            writeSerializedObject.close();
            System.out.println("Done Writting back object " + username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void trending() throws SQLException, IOException, InterruptedException {
        System.out.println("#TRENDING");
        Statement stm = connect();
        String selectionQuery = "SELECT channelVideo, username FROM trendingtable ORDER BY score DESC LIMIT 5;";
        ResultSet rs = stm.executeQuery(selectionQuery);
        int c = 0;
        ArrayList<String> userList = new ArrayList<String>();
        ArrayList<String> channelList = new ArrayList<String>();
        ArrayList<String> videoList = new ArrayList<String>();
        String username, channelName, videoName,channelVideo;
        while(rs.next())
        {
            c++;
            channelVideo = rs.getString(1);
            username = rs.getString(2);
            channelName = channelVideo.substring(0, channelVideo.indexOf(":"));
            videoName = channelVideo.substring(channelVideo.indexOf(":")+1);
            userList.add(username);
            channelList.add(channelName);
            videoList.add(videoName);
        }
        HashMap<String, Video> data = new HashMap<String, Video>();
        for(int i=0;i<c;i++)
        {
            try {
                ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream
                        (new File(System.getProperty("user.home") + "/Hub/Client/"+ userList.get(i))));
                User obj = (User) readSerializedObject.readObject();
                int f = 0;
                for (Channel ch : obj.channels) {
                    if (ch.channelName.equals(channelList.get(i))) {
                        for (Video vid : ch.videos) {
                            if (vid.videoName.equals(videoList.get(i))) {
                                f = 1;
                                System.out.println(vid.videoName);
                                data.put(vid.videoName, vid);
                                break;
                            }
                        }
                        if(f == 1)
                            break;
                    }
                }
                readSerializedObject.close();
            } catch (Exception e) {
                System.out.println("Error in reading serialized files");
                e.printStackTrace();
            }
        }
        peer.dos.writeUTF("#SENDING");
        //System.out.println(peer.peerSocket.getInetAddress().getHostAddress()+":11234");
        Thread.sleep(500);
        new Thread(new SendThumbnails(data, peer.peerSocket.getInetAddress().getHostAddress())).start();
        peer.oos.writeObject(data);
    }

    private String saveFile(String fileName, ObjectInputStream ois) throws Exception
    {
        String path1 = "";

        FileOutputStream fos = null;
        byte[] buffer = new byte[1024];
        // Read file name.
        Object o = null;
//        Object o = ois.readObject();
        path1 = System.getProperty("user.home") + "/Hub/Thumbnails/"+fileName+".png";
        fos = new FileOutputStream(path1);

        // Read file to the end.
        Integer bytesRead = 0;
        do {
            o = ois.readObject();
            if (!(o instanceof Integer)) {
                throwException("Something is wrong");
            }
            bytesRead = (Integer) o;
            o = ois.readObject();
            if (!(o instanceof byte[])) {
                throwException("Something is wrong");
            }
            buffer = (byte[]) o;
            // Write data to output file.
            fos.write(buffer, 0, bytesRead);
        } while (bytesRead == 1024);
        System.out.println("File transfer success");
        fos.close();
        return path1;
    }

    public static void throwException(String message) throws Exception {
        throw new Exception(message);
    }

    private void authenticate()
    {
        //TODO:Authenticate passwords and update IP address of people connecting.

        try {
            String username = peer.dis.readUTF();
            System.out.println("IN USERNAME WITH "+ username);
            //Statement stm = connect();
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/starkhub", "surbhit", "awasthi@7");
            System.out.println("Conneted to db");
            System.out.println("SELECT * FROM useripmap WHERE username = '"+username+"';");
            PreparedStatement authQuery = conn.prepareStatement("SELECT * FROM useripmap WHERE username = ?;");
            authQuery.setString(1,username);
            ResultSet rs = authQuery.executeQuery();
            //ResultSet rs = stm.executeQuery("SELECT * FROM useripmap WHERE username = '"+username+"';");
            System.out.println("rs found");
            boolean res = !(rs.first());
            System.out.println("calculated returning value "+res);
            peer.dos.writeBoolean(res);
            System.out.println("returned "+res);
            if(res) {
//                stm.executeUpdate("INSERT INTO useripmap VALUES(" + "'" + username + "'" + "," + "'" +
//                        peer.peerSocket.getInetAddress().getHostAddress() + "'," + "''" + "," + "''" + "," +
//                        "'0 0 0 0'" + "," + "''" + "," + "''" + "," + "0" + "," + "'n'" + ")" + ";");
                PreparedStatement updateTableQuery = conn.prepareStatement("INSERT INTO useripmap VALUES (?,?,?,?,?,?,?,?,?)");
                updateTableQuery.setString(1,username);
                updateTableQuery.setString(2,peer.peerSocket.getInetAddress().getHostAddress());
                updateTableQuery.setString(3,"");
                updateTableQuery.setString(4,"");
                updateTableQuery.setString(5,"0 0 0 0");
                updateTableQuery.setString(6,"");
                updateTableQuery.setString(7,"");
                updateTableQuery.setDouble(8,0);
                updateTableQuery.setString(9, "n");
                updateTableQuery.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void hello() throws SQLException, IOException {
        //TODO:set start time
        String username = peer.dis.readUTF();
        System.out.println("IN USERNAME WITH "+ username);
        Statement stm = connect();
        System.out.println("Conneted to db");
        long time = ZonedDateTime.now().toInstant().toEpochMilli();
        System.out.println("Got time in milliseconds: " +time);
        String query = "UPDATE useripmap SET starttime='"+time+"' WHERE username='"+username+"';";
        System.out.println(query);
        stm.executeUpdate(query);
        System.out.println("query update done");
    }

    private void bye() throws IOException, SQLException {
        //TODO: set end time and update uptime ie. slide window
        String username = peer.dis.readUTF();
        System.out.println("IN USERNAME WITH "+ username);
        Statement stm = connect();
        System.out.println("Conneted to db");
        long time = ZonedDateTime.now().toInstant().toEpochMilli();
        System.out.println("Got time in milliseconds: " +time);
        String query = "UPDATE useripmap SET endtime='"+time+"' WHERE username='"+username+"';";
        System.out.println(query);
        stm.executeUpdate(query);
        System.out.println("query update of end time done");
        String resultedUptime, startTime;
        query = "SELECT uptime FROM useripmap WHERE username='"+username+"';";
        String query1 = "SELECT starttime FROM useripmap WHERE username='"+username+"';";
        System.out.println(query+"\n"+query1);
        ResultSet rs = stm.executeQuery(query);
        rs.next();
        resultedUptime = rs.getString(1);
        ResultSet rs1 = stm.executeQuery(query1);
        rs1.next();
        startTime = rs1.getString(1);
        Long addTime  = time - Long.parseLong(startTime);
        StringTokenizer uptime = new StringTokenizer(resultedUptime);
        System.out.println(uptime.nextToken()+ " is going to be removed");
        System.out.println(addTime+" is going to be added");
        StringBuffer addTimeString = new StringBuffer("");
        long calculatedUptime = 0;
        while(uptime.hasMoreTokens())
        {
            String t = uptime.nextToken();
            calculatedUptime += Long.parseLong(t);
            addTimeString.append(t+" ");
        }
        double finalUptime = ((double)calculatedUptime)/4;
        addTimeString.append(addTime);
        System.out.println("Got uptime string: " +addTimeString.toString()+"\ngot finalUptime: "+finalUptime);
        query = "UPDATE useripmap SET uptime='"+addTimeString.toString()+"', finalUptime="+finalUptime+" WHERE username='"+username+"';";
        System.out.println(query);
        stm.executeUpdate(query);
        System.out.println("query update done");
    }

    private void makeChannel() throws Exception {
        String username = peer.dis.readUTF();
        String channelName = peer.dis.readUTF();
        try {
            File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
            ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
            User obj = (User) readSerializedObject.readObject();
            System.out.println("obj for " + username);
            obj.isCreator = true;
            obj.channels.add(new Channel(channelName));
            System.out.println("Writting back");
            ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                    new File(System.getProperty("user.home") + "/Hub/Client/" + username)));
            writeSerializedObject.writeObject(obj);
            writeSerializedObject.close();
            System.out.println("Done Writting back object " + username);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void newUser() throws Exception {
        String username = peer.dis.readUTF();
        Statement stm = connect();
        User user = new User(username);
        try {
            ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                    new File(System.getProperty("user.home") + "/Hub/Client/" + username)));
            writeSerializedObject.writeObject(user);
            writeSerializedObject.close();
        }catch (Exception e){
            System.out.println("Error in writing Serialized object");
            e.printStackTrace();
        }
        stm.executeUpdate("update useripmap set ip='"+peer.peerSocket.getInetAddress().getHostAddress()
                +"' where username='"+username+"'");
        existingUser(username, 1);
    }

    private void existingUser(String username, int flag) throws Exception {
        //Send Video
        int f = 0;
        //ArrayList<String> paths = new ArrayList<>();
        File dir = new File(System.getProperty("user.home") + "/Hub/Client/");
        for (File usr : dir.listFiles()) {
            try {
                ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(usr));
                User obj = (User) readSerializedObject.readObject();
                if (obj.isCreator) {
                    f = 1;
                    break;
//                    for(Channel ch:obj.channels)
//                    {
//                        if(ch.videos.size() != 0)
//                        {
//                            f = 1;
//                            for(Video vid:ch.videos)
//                            {
//                                paths.add(vid.pathOfVideo);
//                            }
//                        }
//                    }
                }
            } catch (Exception e) {
                System.out.println("Error in reading serialized files");
                e.printStackTrace();
            }
        }
        if (f == 0) {
            peer.dos.writeUTF("#NOFILES");
            System.out.println("#NOFILES send");
        }
        else
        {
            HashMap<String, Video> recommendedData = null;
            String n = "12";
            if(flag == 1) {
                n = "16";
            }
            else
            {
                recommendedData = new HashMap<String, Video>();
                //TODO: make recommendedData
                /*GetRecommendation recommendation = new GetRecommendation(username);
                ArrayList<String> videoList = recommendation.generate();
                String usernameOfRecommededVideo, channelNameOfRecommededVideo, videoNameOfRecommededVideo;
                int size = videoList.size();
                for(int i=0;i<size;i++)
                {
                    String itemID = videoList.get(i);
                    usernameOfRecommededVideo = itemID.substring(0,itemID.indexOf(":"));
                    channelNameOfRecommededVideo = itemID.substring(itemID.indexOf(":")+1,itemID.lastIndexOf(":"));
                    videoNameOfRecommededVideo = itemID.substring(itemID.lastIndexOf(":")+1);
                    System.out.println(usernameOfRecommededVideo+"\n"+channelNameOfRecommededVideo+"\n"+videoNameOfRecommededVideo);
                    try {
                        ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream
                                (new File(System.getProperty("user.home") + "/Hub/Client/"+ usernameOfRecommededVideo)));
                        User obj = (User) readSerializedObject.readObject();
                        int f2 = 0;
                        for (Channel ch : obj.channels) {
                            if (ch.channelName.equals(channelNameOfRecommededVideo)) {
                                for (Video vid : ch.videos) {
                                    if (vid.videoName.equals(videoNameOfRecommededVideo)) {
                                        f2 = 1;
                                        System.out.println(vid.videoName);
                                        recommendedData.put(vid.videoName, vid);  // #this line was out of this if initially
                                        break;
                                    }
                                }
                                if(f2 == 1)
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error in reading serialized files");
                        e.printStackTrace();
                    }
                }*/
                //Make recommender over
            }
            HashMap<String, Video> data = new HashMap<String, Video>();
            System.out.println("IN USERNAME WITH " + username);
            Statement stm = connect();
            System.out.println("Conneted to db");
            ResultSet rs = stm.executeQuery("SELECT * FROM paththumbnailmap ORDER BY RAND() LIMIT "+n+";");
            for (int i = 1; rs.next() && i <= Integer.parseInt(n); i++) {
                String path = rs.getString(1);
                String ownerName = rs.getString(3);
                String channelName = rs.getString(4);
                System.out.println(path+"\n"+ownerName+"\n"+channelName);
                try {
                    ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(new File(System.getProperty("user.home") + "/Hub/Client/"+

                            ownerName)));
                    User obj = (User) readSerializedObject.readObject();
                    int f2 = 0;
                    for (Channel ch : obj.channels) {
                        if (ch.channelName.equals(channelName)) {
                            for (Video vid : ch.videos) {
                                if (vid.pathOfVideo.equals(path)) {
                                    f2 = 1;
                                    System.out.println(vid.videoName);
                                    data.put(vid.videoName, vid);  // #this line was out of this if initially
                                    break;
                                }
                            }
                            if(f2 == 1)
                                break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error in reading serialized files");
                    e.printStackTrace();
                }
            }
            peer.dos.writeUTF("#SENDING");
            //System.out.println(peer.peerSocket.getInetAddress().getHostAddress()+":11234");
            Thread.sleep(500);
            HashMap<String, Video> hm = new HashMap<String, Video>();
            hm.putAll(data);
            if(recommendedData != null)
                hm.putAll(recommendedData);
            new Thread(new SendThumbnails(hm, peer.peerSocket.getInetAddress().getHostAddress())).start();
            peer.oos.writeObject(recommendedData);
            peer.oos.writeObject(data);
        }

        //Send  login time notifications if any
        SendLoginTimeNotificationDaemon loginTime = new SendLoginTimeNotificationDaemon(username, peer.peerSocket.getInetAddress().getHostAddress());
        new Thread(loginTime).start();
    }

    private void addVideoInChannel() throws IOException {
        synchronized (this)
        {
            System.out.println("#ADDVIDEOINCHANNEL");
            String username = peer.dis.readUTF();
            String channelName = peer.dis.readUTF();
            int numberOfVideo = peer.dis.readInt();
            try {
                System.out.println("Opened starting connecting to db");
                Statement stm = connect();
                System.out.println("Conneted to db");
                File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
                ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
                User obj = (User) readSerializedObject.readObject();
                System.out.println("obj for " + username);
                ArrayList<Video> allVideos = new ArrayList<Video>();
                while(numberOfVideo-- > 0)
                {
                    String pathOfVideo = peer.dis.readUTF();
                    String videoName = pathOfVideo.substring(pathOfVideo.lastIndexOf("/")+1);
                    ArrayList<String> tagsList = (ArrayList<String>) peer.ois.readObject();
                    System.out.println("give image");
                    String pathOfthumbnail = saveFile(videoName, peer.ois);
                    System.out.println("Path of thumbnail "+pathOfthumbnail);
                    Video vid = new Video(pathOfVideo, username, pathOfthumbnail, tagsList, channelName);
                    allVideos.add(vid);
                    //obj.channels.get(obj.channels.indexOf(channelName)).videos.add(vid);
                    StringBuffer tagBuffer = new StringBuffer("");
                    String finalTag = "";
                    try {
                        if (!(tagsList == null)) {
                            for (String tag : tagsList)
                                tagBuffer.append(tag);
                        }
                        finalTag = tagBuffer.toString();
                    } catch (Exception e){
                        System.out.println("Andar se ayya hoon");
                        e.printStackTrace();
                    }
                    System.out.println("Query:- " + "INSERT INTO paththumbnailmap VALUES(" + "'" + pathOfVideo + "'" + "," + "'" + finalTag + "','" + username + "','" + channelName + "','" + vid.videoName + "')" + ";");
                    stm.executeUpdate("INSERT INTO paththumbnailmap VALUES(" + "'" + pathOfVideo + "'" + "," + "'" + finalTag + "','" + username + "','" + channelName + "','" + vid.videoName + "')" + ";");
                    System.out.println("Updated entry");
                }
                Channel notifierChannel = null;
                for (Channel ch : obj.channels) {
                    if (ch.channelName.equals(channelName))
                    {
                        notifierChannel = ch;
                        for(Video vid : allVideos)
                            ch.videos.add(vid);
                        break;
                    }
                }
                System.out.println("Writting back");
                ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                        new File(System.getProperty("user.home") + "/Hub/Client/" + username)));
                writeSerializedObject.writeObject(obj);
                writeSerializedObject.close();
                System.out.println("Done Writting back object " + username);
                System.out.println("allVideos "+allVideos);
                Notification notificationObject = new Notification(notifierChannel, allVideos);
                Thread notificationThread = new Thread(notificationObject);
                notificationThread.start();

            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void search() throws IOException, ClassNotFoundException, SQLException, InterruptedException {
        ArrayList<String> searchWaliString = (ArrayList<String>) peer.ois.readObject();
        StringBuffer queryBuffer = new StringBuffer("SELECT * FROM paththumbnailmap WHERE ");
        for (int i = 0; i < searchWaliString.size() - 1; i++) {
            queryBuffer.append("name LIKE '%" + searchWaliString.get(i) + "%' OR ");
        }
        queryBuffer.append("name LIKE '%" + searchWaliString.get(searchWaliString.size() - 1) + "%';");
        String query = queryBuffer.toString();
        Statement stm = connect();
        System.out.println("Conneted to db");
        System.out.println(query);
        ResultSet rs = stm.executeQuery(query);
        System.out.println("rs found");

        HashMap<String, Video> result = new HashMap<String, Video>();
        while (rs.next()) {
            String pathOfVideo = rs.getString(1);
            String username = rs.getString(3);
            String channelName = rs.getString(4);
            try {
                File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
                ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
                User obj = (User) readSerializedObject.readObject();
                System.out.println("obj for " + username);
                int f = 0;
                for (Channel ch : obj.channels) {
                    if (ch.channelName.equals(channelName)) {
                        for (Video vid : ch.videos) {
                            if (vid.pathOfVideo.equals(pathOfVideo)) {
                                f = 1;
                                result.put(vid.videoName, vid);
                                break;
                            }
                        }
                        if (f == 1)
                            break;
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        peer.dos.writeUTF("#SENDING");
        //System.out.println(peer.peerSocket.getInetAddress().getHostAddress()+":11234");
        Thread.sleep(3000);
        new Thread(new SendThumbnails(result, peer.peerSocket.getInetAddress().getHostAddress())).start();
        peer.oos.writeObject(result);
        peer.oos.flush();
    }

    private void tagSearch() throws IOException, ClassNotFoundException, SQLException, InterruptedException {
        ArrayList<String> tagsWaliString = (ArrayList<String>) peer.ois.readObject();
        StringBuffer queryBuffer = new StringBuffer("SELECT * FROM paththumbnailmap WHERE ");
        for (int i = 0; i < tagsWaliString.size() - 1; i++) {
            queryBuffer.append("tags LIKE '%" + tagsWaliString.get(i) + "%' OR ");
        }
        queryBuffer.append("tags LIKE '%" + tagsWaliString.get(tagsWaliString.size() - 1) + "%';");
        String query = queryBuffer.toString();
        Statement stm = connect();
        System.out.println("Conneted to db");
        System.out.println(query);
        ResultSet rs = stm.executeQuery(query);
        System.out.println("rs found");
        HashMap<String, Video> result = new HashMap<String, Video>();
        while (rs.next()) {
            String pathOfVideo = rs.getString(1);
            String username = rs.getString(3);
            String channelName = rs.getString(4);
            try {
                File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
                ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
                User obj = (User) readSerializedObject.readObject();
                System.out.println("obj for " + username);
                int f = 0;
                for (Channel ch : obj.channels) {
                    if (ch.channelName.equals(channelName)) {
                        for (Video vid : ch.videos) {
                            if (vid.pathOfVideo.equals(pathOfVideo)) {
                                f = 1;
                                result.put(vid.videoName, vid);
                                break;
                            }
                        }
                        if (f == 1)
                            break;
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        peer.dos.writeUTF("#SENDING");
        //System.out.println(peer.peerSocket.getInetAddress().getHostAddress()+":11234");
        Thread.sleep(3000);
        new Thread(new SendThumbnails(result, peer.peerSocket.getInetAddress().getHostAddress())).start();
        peer.oos.writeObject(result);
        peer.oos.flush();
    }

    private void addSubscriber() throws IOException, ClassNotFoundException {
        String username = peer.dis.readUTF();
        String subscriberName = peer.dis.readUTF();
        String channelName = peer.dis.readUTF();
        File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
        ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
        User obj = (User) readSerializedObject.readObject();
        int f = 0;
        for (Channel ch : obj.channels) {
            if(ch.channelName.equals(channelName)){
                synchronized (this) {
                    ch.channelSubscribers += 1;
                    ch.subscriberName.add(subscriberName);
                }
                f = 1;
            }
            if (f == 1)
                break;
        }
        System.out.println("Subscriber added: "+subscriberName+" to channel "+channelName);
        ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                new File(System.getProperty("user.home") + "/Hub/Client/" + username)));
        writeSerializedObject.writeObject(obj);
        writeSerializedObject.close();
        System.out.println("Done Writting back object " + username);
    }

    private void setLikes() {
        try {
            String username = peer.dis.readUTF();
            String channelName = peer.dis.readUTF();
            String videoName = peer.dis.readUTF();
            //Video vid = (Video) peer.ois.readObject();
            File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
            ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
            User obj = (User) readSerializedObject.readObject();
            System.out.println("obj for " + username);
            String event = peer.dis.readUTF();
            if(event.equals("#ADD"))
            {
                int f = 0;
                for (Channel ch : obj.channels) {
                    if(ch.channelName.equals(channelName)){
                        for (Video v : ch.videos) {
                            if (v.videoName.equals(videoName)) {
                                synchronized (this) {
                                    v.numberOfLikes += 1;
                                    ch.totalNoOfLikes += 1;
                                }
                                f = 1;
                                break;
                            }
                        }
                    }
                    if (f == 1)
                        break;
                }
            }
            else if(event.equals("#SUB"))
            {
                int f = 0;
                for (Channel ch : obj.channels) {
                    if(ch.channelName.equals(channelName)){
                        for (Video v : ch.videos) {
                            if (v.videoName.equals(videoName)) {
                                synchronized (this) {
                                    v.numberOfLikes -= 1;
                                    ch.totalNoOfLikes -= 1;
                                }
                                f = 1;
                                break;
                            }
                        }
                    }
                    if (f == 1)
                        break;
                }
            }
            System.out.println("Writting back");
            ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                    new File(System.getProperty("user.home") + "/Hub/Client/" + username)));
            writeSerializedObject.writeObject(obj);
            writeSerializedObject.close();
            System.out.println("Done Writting back object " + username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getStatOfChannel() {
        try {
            Statement stm = connect();
            System.out.println("Conneted to db");
            double rating = 0;
            String username = peer.dis.readUTF();
            String channelName = peer.dis.readUTF();
            System.out.println(channelName+"--- 1");
            File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
            ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
            User obj = (User) readSerializedObject.readObject();
            System.out.println("---obj for " + username);
            Channel requestedChannel = null;
            for (Channel ch : obj.channels) {
                System.out.println(ch.channelName + " for username "+username);
                if (ch.channelName.equals(channelName)) {
                    requestedChannel = ch;
                    rating = 0.5*ch.channelSubscribers + 0.25*ch.totalNoOfComments + 0.25*ch.totalNoOfLikes;
                    break;
                }
            }
            String query = "SELECT finalUptime FROM useripmap WHERE username='"+username+"';";
            System.out.println(query);
            ResultSet rs = stm.executeQuery(query);
            rs.next();
            double uptime = rs.getDouble(1);
            rating = 0.5*rating + 0.5*uptime;
            System.out.println("query update done");
            peer.dos.writeDouble(rating);
            peer.dos.writeInt(requestedChannel.totalNoOfLikes);
            peer.dos.writeInt(requestedChannel.totalNoOfComments);
            peer.dos.writeInt(requestedChannel.channelSubscribers);
            peer.dos.writeInt(requestedChannel.totalNoOfViews);
            peer.oos.writeObject(requestedChannel.videos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getIP() throws IOException, SQLException, ClassNotFoundException {
        String username = peer.dis.readUTF();
        String ownerName = peer.dis.readUTF();
        String videoName = peer.dis.readUTF();
        String channelName = peer.dis.readUTF();

        new Thread(new InsertInTrendingController(ownerName, channelName, videoName)).start();

        String query = "SELECT ip, alternateip FROM useripmap WHERE username = '"+ownerName+"';";
        Statement stm = connect();
        System.out.println(query);
        ResultSet rs = stm.executeQuery(query);
        rs.next();
        String ip = rs.getString(1);
        System.out.println("Sending back ip "+ip);
        peer.dos.writeUTF(ip);
        String alternateIP = rs.getString(2);
        System.out.println("Alternate ip before sending to user: ------------"+alternateIP+"------------");
        if(alternateIP == null || alternateIP.isEmpty())
        {
            System.out.println("Sending alternate ip: false");
            peer.dos.writeBoolean(false);
        }
        else
        {
            System.out.println("Sending alternate ip: true");
            peer.dos.writeBoolean(true);
            peer.dos.writeUTF(alternateIP);
        }
        Video videoObject = null;
        synchronized (this){
            File file = new File(System.getProperty("user.home") + "/Hub/Client/" + ownerName);
            ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
            User obj = (User) readSerializedObject.readObject();
            System.out.println("obj for " + ownerName);
            int f = 0;

            for (Channel ch : obj.channels)
            {
                if(ch.channelName.equals(channelName)){
                    for (Video v : ch.videos) {
                        if (v.videoName.equals(videoName)) {
                                videoObject = v;
                                v.numberOfViews += 1;
                                ch.totalNoOfViews += 1;
                            f = 1;
                            break;
                        }
                    }
                }
                if (f == 1)
                    break;
            }
            System.out.println("Writting back");
            ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                    new File(System.getProperty("user.home") + "/Hub/Client/" + ownerName)));
            writeSerializedObject.writeObject(obj);
            writeSerializedObject.close();
            System.out.println("Done Writting back object " + ownerName);
        }
        //Recommendation service
        //new Thread(new AddDataToRecommder(videoObject, username)).start();
    }
}
