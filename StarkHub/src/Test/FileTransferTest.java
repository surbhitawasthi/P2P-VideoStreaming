package Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class FileTransferTest {

    public static void main(String[] args) {
        try {
            File[] list = new File("/home/aks/Desktop/test/").listFiles();
            Socket sock = new Socket("172.31.84.87",5001);
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

            ArrayList<String> l = new ArrayList<>();
            for(File f: list){
                l.add(f.getName());
            }

            System.out.println("List: "+l);
            oos.writeObject(l);
//            ServerSocketChannel ssc = ServerSocketChannel.open();
//            ssc.socket().bind(new InetSocketAddress(5002));
//
//
//            System.out.println("Socket: "+sock + " isClosed: "+sock.isClosed() );
//
//            System.out.println("Listening for SocketChannel ");
//            SocketChannel sc = ssc.accept();
//            System.out.println("Received SocketChannel: "+sc);
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(5002));
            for(File f: list){



                System.out.println("Socket: "+sock + " isClosed: "+sock.isClosed() );

                System.out.println("Listening for SocketChannel ");
                SocketChannel sc = ssc.accept();
                System.out.println("Received SocketChannel: "+sc);
                System.out.println(f.getAbsolutePath());
                sendFile(sc, f.getAbsolutePath(), dos);
                sc.close();
            }


        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    static void sendFile(SocketChannel sc, String path, DataOutputStream dos) throws Exception{
        FileChannel fc = new RandomAccessFile(path, "rw").getChannel();
        ByteBuffer buf = ByteBuffer.allocate(2048);
        int bytesRead = fc.read(buf);

        while(bytesRead != -1 ){
            buf.flip();
            //dos.writeInt(1);
            sc.write(buf);
            buf.clear();
            bytesRead = fc.read(buf);
        }

        //dos.writeInt(-1);

    }

}
