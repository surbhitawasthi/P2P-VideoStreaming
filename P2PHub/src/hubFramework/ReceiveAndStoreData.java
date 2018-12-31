package hubFramework;

import javafx.util.Pair;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class ReceiveAndStoreData implements Runnable
{
    Peer peer;
    String targetUsername, targetIP, premiumUsername;

    ReceiveAndStoreData(String premiumUsername, Peer peer, String targetUsername, String targetIP)
    {
        this.peer = peer;
        this.premiumUsername = premiumUsername;
        this.targetUsername = targetUsername;
        this.targetIP = targetIP;
    }

    @Override
    public void run()
    {
        System.out.println("Starting to receive video");
        String storagePath = System.getProperty("user.home")+"/Hub/tmp/"+targetUsername;
        File tempDir = new File(storagePath);
        tempDir.mkdirs();
        ArrayList<String> videoList = new ArrayList<String>();
        ArrayList<String> channelList = new ArrayList<String>();
        int n = 0;
        try {
            System.out.println("Getting n");
            n = peer.dis.readInt();
            System.out.println("Got n = "+n);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SocketChannel sc = null;
//        try {
////            sc = SocketChannel.open();
////            sc.connect(new InetSocketAddress(peer.peerSocket.getInetAddress().getHostAddress(), 5000));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        String videoName, channelName;
        //Reception of data started
        for(int i = 0;i < n;i++)
        {
            System.out.println("i ="+i);
            try {
                Thread.sleep(500);
                sc = SocketChannel.open();
                sc.connect(new InetSocketAddress(peer.peerSocket.getInetAddress().getHostAddress(), 5000));
                videoName = peer.dis.readUTF();
                videoList.add(videoName);
                System.out.println("vid name "+videoName);
                channelName = peer.dis.readUTF();
                channelList.add(channelName);
                System.out.println("channel name "+channelName);
                receiveFile(sc, storagePath+"/"+videoName);
                System.out.println("Saved "+videoName+" of channel "+channelName);
                sc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Reception done now trying connrction with target");
        //Reception of data finished now initiating sending data
        Socket socket = null;
        try {
            socket = new Socket(targetIP, 15001);
            System.out.println("connected to"+socket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataInputStream dis = null;
        DataOutputStream dos = null;
        ObjectInputStream ois = null;
        ObjectOutputStream oos;
        Peer targetPeer = null;
        String alternatePathPrefix = "";
        try {
            dis = new DataInputStream(socket.getInputStream());
            System.out.println("dis done");
            dos = new DataOutputStream(socket.getOutputStream());
            System.out.println("dos done");
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("ois done");
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("oos done");
            //targetPeer = new Peer(socket);
            System.out.println("Streams initialized");
            dos.writeUTF("HUB");
            System.out.println("\'hub\' written");
            dis.readBoolean();// always true in this case so no need of if
            System.out.println("Sending #RECEIVEDATA");
            dos.writeUTF("#RECEIVEDATA");
            dos.writeInt(n);
            System.out.println("Sent n="+n);
            System.out.println("Sending list : "+videoList);
            oos.writeObject(videoList);
            alternatePathPrefix = dis.readUTF();
            dis.readBoolean();//for stopping of starting new sender thread
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("GOing to spawn thread sendData with alternate path prefix"+alternatePathPrefix);
        //sending data thread started
        new Thread(new SendData(targetIP,storagePath+"/", dis,dos)).start();
        // continuing with house keeping stuff like wrapping up everything that's open
        File file = new File(System.getProperty("user.home") + "/Hub/Client/" + premiumUsername);
        ObjectInputStream readSerializedObject = null;
        try {
            readSerializedObject = new ObjectInputStream(new FileInputStream(file));
            User obj = (User) readSerializedObject.readObject();
            for(int i=0;i<channelList.size();i++)
            {
                String chName = channelList.get(i);
                for(Channel ch:obj.channels)
                {
                    int f = 0;
                    if(ch.channelName.equals(chName))
                    {
                        for(Video vid:ch.videos)
                        {
                            if(vid.videoName.equals(videoList.get(i))) {
                                f = 1;
                                vid.alternatePathOfVideo = alternatePathPrefix + videoList.get(i);
                                break;
                            }
                        }
                        if(f == 1)
                            break;
                    }
                }
            }
            System.out.println("Update object now writing back");
            ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                    new File(System.getProperty("user.home") + "/Hub/Client/" + premiumUsername)));
            writeSerializedObject.writeObject(obj);
            writeSerializedObject.close();
            System.out.println("Done Writting back object " + premiumUsername);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // function of reception of files
    private void receiveFile(SocketChannel sc, String path) throws IOException
    {
        FileChannel fc = new RandomAccessFile(path, "rw").getChannel();
        System.out.println("fc tayar hai");
        ByteBuffer buf = ByteBuffer.allocate(2048);

        //int flag = peer.dis.readInt();
        int bytesRead = sc.read(buf);
        //System.out.println("Byteread "+bytesRead);
        while (bytesRead != -1)
        {
            buf.flip();
            //System.out.println("buf.flip ho gaya");
            fc.write(buf);
            //System.out.println("fc.write ho gaya");
            buf.clear();
            //System.out.println("buffer cleared");
            //flag =  peer.dis.readInt();
            bytesRead = sc.read(buf);
            //System.out.println("Byteread: "+bytesRead);
        }
        fc.close();
        System.out.println("file received "+path);
    }
}
