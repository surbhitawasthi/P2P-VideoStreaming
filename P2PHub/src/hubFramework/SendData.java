package hubFramework;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class SendData implements Runnable
{
    private String targetIP;
    private String pathOfFiles;
    private DataOutputStream dos;
    private DataInputStream dis;
    SendData(String targetIP,String pathOfFiles, DataInputStream dis, DataOutputStream dos)
    {
        this.targetIP = targetIP;
        this.pathOfFiles = pathOfFiles;
        this.dis = dis;
        this.dos = dos;
    }


    @Override
    public void run()
    {
        System.out.println("In send data");
        SocketChannel ssc = null;
        try {
            System.out.println("target ip : "+targetIP);
            File dir = new File(pathOfFiles);
            for(File file : dir.listFiles())
            {
                Thread.sleep(500);
                ssc = SocketChannel.open();
                ssc.connect(new InetSocketAddress(targetIP, 15004));
                System.out.println("connected to "+targetIP);
                System.out.println("writing name "+file.getName());
                dos.writeUTF(file.getName());
                System.out.println("sending video "+file.getAbsolutePath());
                sendFile(ssc, file.getAbsolutePath());
                System.out.println("Video send");
                ssc.close();
            }
            //ssc.close();
            System.out.println("Socket closed");
            System.out.println("Deleting temp folder");
            File[] listFiles = dir.listFiles();
            for(File file : listFiles){
                System.out.println("Deleting "+file.getName());
                file.delete();
            }
            //now directory is empty, so we can delete it
            System.out.println("Deleting Directory. Success = "+dir.delete());
        }
        catch (SocketException e) {
            //TODO: manage if connection is broken
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(SocketChannel ssc, String path)
    {
        System.out.println("in sending file");
        try {
            FileChannel fc = new RandomAccessFile(path,"rw").getChannel();
            ByteBuffer buf = ByteBuffer.allocate(2048);
            int bytesRead = fc.read(buf);
            while(bytesRead!= -1){
                buf.flip();
                //dos.writeUTF("1");
                ssc.write(buf);
                buf.clear();
                bytesRead = fc.read(buf);
                //System.out.println("bytesread: "+bytesRead);
            }
            //dos.writeInt(-1);
            fc.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("out of send file");
    }
}
