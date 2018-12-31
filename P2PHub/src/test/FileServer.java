package test;

import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class FileServer {
    public static void main(String[] args){
        try {
            // Creating a ServerSocketChannel and binding to port 5000 (random Port)
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(5000));

            // SocketChannel to accept new Connections
            SocketChannel sc = ssc.accept();

            // Opening a FileChannel
            FileChannel fc = new RandomAccessFile("file path","rw").getChannel();

            // Creating a byteBuffer and allocating a capacity of 2048bytes
            ByteBuffer buf = ByteBuffer.allocate(2048);

            // Reading from FileChannel into the buffer
            int bytesRead = fc.read(buf);

            while(bytesRead!= -1){
                buf.flip();

                // Writing to SocketChannel from the buffer
                sc.write(buf);

                // Clearing the buffer
                buf.clear();

                // Reading from FileChannel into the buffer
                bytesRead = fc.read(buf);
            }

            fc.close();
            sc.close();
            ssc.close();

        } catch (Exception ex) {
            System.out.println(""+ex);
        }
    }
}
