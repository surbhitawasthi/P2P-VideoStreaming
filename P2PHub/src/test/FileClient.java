package test;

import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class FileClient {
    public static void main(String[] args) throws Exception {
        // Opening a SocketChannel
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("Address of Server", 5000));

        // Opening a File Channel
        FileChannel fc = new RandomAccessFile("path to file", "rw").getChannel();

        // Creating a byteBuffer and allocating a capacity of 2048bytes
        ByteBuffer buf = ByteBuffer.allocate(2048);

        // Reading from SocketChannel into the buffer
        int bytesRead = sc.read(buf);
        while (bytesRead != -1) {

            // Flipping the buffer so that it can be read
            buf.flip();
            // Reading from buffer and writing to FileChannel
            fc.write(buf);

            // Clearing the buffer
            buf.clear();

            // Reading from SocketChannel into the buffer
            bytesRead = sc.read(buf);
        }
        fc.close();
        sc.close();
    }
}
