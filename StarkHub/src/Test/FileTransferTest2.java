package Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class FileTransferTest2 {

    public static void main(String[] args) {
        try {
            ServerSocket ssock = new ServerSocket(5001);
            Socket sock = ssock.accept();

            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());

            System.out.println("Socket Received: "+sock);

            ArrayList<String> l = (ArrayList<String>) ois.readObject();

            SocketChannel sc = SocketChannel.open();
            sc.connect( new InetSocketAddress("172.31.85.85", 5002));

            for(String s: l ){
                System.out.println(s);
                receiveData(sc, s, dis);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static void receiveData(SocketChannel sc, String name, DataInputStream dis) throws Exception{
        FileChannel fc = new  RandomAccessFile("/home/aks/Desktop/dest/"+name, "rw").getChannel();
        ByteBuffer buf = ByteBuffer.allocate(2048);

        dis.readInt();
        int bytesRead = sc.read(buf);
        while(bytesRead >=0){
            buf.flip();
            fc.write(buf);
            buf.clear();
            if(dis.readInt() == -1)
                break;
            bytesRead = sc.read(buf);

        }
    }
}
