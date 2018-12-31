package Server;

import Client.Login.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/*
        Service to recieve Premium Data of another peer from HUB
 */

public class ReceiveData extends Service {

    int n;
    ArrayList<String> list;
    DataInputStream dis;

    public ReceiveData(int n, ArrayList<String> list, DataInputStream dis){
        this.n = n;
        this.list = list;
        this.dis = dis;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try{
                    System.out.println("ReceiveData thread started");
                    ServerSocketChannel ssc = ServerSocketChannel.open();
                    System.out.println("Initialised ssc");
                    ssc.socket().bind(new InetSocketAddress(15004));
                    System.out.println("Bound to port 15004");


                    for(int i=0;i<n;i++){
                        String vidName = dis.readUTF();
                        System.out.println("vidname : "+vidName);
                        SocketChannel sc = ssc.accept();
                        System.out.println("socket: "+sc);
                        String path = System.getProperty("user.home")+"/starkhub/"+ Main.USERNAME+"/premium/"+vidName;

                        if(save(sc, path, dis)){
                            System.out.println("File saved successfully");
                        }else{
                            System.out.println("Some error occured while saving file: "+vidName);
                        }
                        sc.close();
                        System.out.println("file received: "+vidName);
                    }

                    ssc.close();


                }catch (Exception e){
                    e.printStackTrace();
                }


                return null;
            }
        };
    }

    // Method to save large files on local storage
    boolean save(SocketChannel sc, String path, DataInputStream dis){

        try{

            FileChannel fc = new RandomAccessFile(path,"rw").getChannel();
            ByteBuffer buf = ByteBuffer.allocate(2048);

            //int n = dis.readInt();
            int bytesRead = sc.read(buf);
            while(bytesRead!= -1){
                buf.flip();
                fc.write(buf);
                buf.clear();
                //n = dis.readInt();
                if(n == -1)
                    break;
                bytesRead = sc.read(buf);
            }
            fc.close();

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
