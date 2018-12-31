package Client.Utility;

import Client.DataClasses.Video;
import Client.Login.Main;
import Client.UI.MainPageController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/*
    Initialised when the User goes premium
    and transfers his premium videos to other peer
 */

public class SendData extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                try{

                    // Opening socket and streams o HUB
                    Socket sock = new Socket(Main.HUB_IP,1111);
                    DataInputStream dis = new DataInputStream(sock.getInputStream());
                    DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                    dout.writeUTF("#PREMIUM");
                    System.out.println("-Sent Premium");
                    dout.writeUTF(Main.USERNAME);
                    System.out.println("-Sent username");
                    boolean bool = dis.readBoolean();
                    System.out.println("Received bool: "+bool);

                    // If Receiving peer is online
                    if(bool){
                        try {
                            ServerSocketChannel ssc = ServerSocketChannel.open();
                            ssc.socket().bind(new InetSocketAddress(5000));

                            ArrayList<Pair<Video, String>> list = MainPageController.premiumVideoList;
                            dout.writeInt(list.size());

                            System.out.println("Sent List size: "+list.size());
                            System.out.println("PremList: "+list);



//                            System.out.println("Socket: "+sock + " isClosed: "+sock.isClosed() );
//
//                            System.out.println("Listening for SocketChannel ");
//                            SocketChannel sc = ssc.accept();
//                            System.out.println("Received SocketChannel: "+sc);

                            for (Pair<Video, String> p: list) {
                                try {
                                    System.out.println("Socket: "+sock + " isClosed: "+sock.isClosed() );

                                    System.out.println("Listening for SocketChannel ");
                                    SocketChannel sc = ssc.accept();
                                    System.out.println("Received SocketChannel: "+sc);
                                    System.out.println("Vid Name: "+p.getKey().getVideoName());
                                    dout.writeUTF(p.getKey().getVideoName());
                                    System.out.println("p.getValue: "+p.getValue());
                                    dout.writeUTF(p.getValue());
                                    String vidPath = p.getKey().getVideoPath();
                                    System.out.println("VidPath: " + vidPath);
                                    if (sendFile(sc, vidPath, dout)) {
                                        System.out.println("File sent successfully");
                                    } else {
                                        System.out.println("Some error occured while transfering: " + p.getKey().getVideoName() + ", path: " + p.getKey().getVideoPath());
                                    }
                                    sc.close();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            ssc.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }

                    // Closing HUB connections
                    ois.close();
                    oos.close();
                    sock.close();


                }catch(Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        };
    }

    // Method to Transfer files
    boolean sendFile(SocketChannel sc, String filePath, DataOutputStream dout){
        try {
            FileChannel fc = new RandomAccessFile(filePath, "rw").getChannel();
            ByteBuffer buf = ByteBuffer.allocate(2048);
            int bytesRead = fc.read(buf);

            while (bytesRead >= 0) {
                buf.flip();
                //dout.writeInt(1);
                sc.write(buf);
                buf.clear();
                bytesRead = fc.read(buf);
                //System.out.println("bytesRead: "+bytesRead);

            }

            fc.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
