package Test;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;

public class ServerTest  {
    public static void main(String[] args) {
        try {

            ServerSocket s = new ServerSocket(5000);
            new Thread(new Clienttest()).start();
            Socket sock = s.accept();

            System.out.println(sock.getInetAddress());
            System.out.println(sock.getInetAddress().getHostName());
            System.out.println(sock.getLocalAddress());
            System.out.println(sock.getLocalAddress().getHostName());
            System.out.println(sock.getRemoteSocketAddress());


        }catch(Exception e){
            e.printStackTrace();
        }
    }
}


class Clienttest implements Runnable{

    @Override
    public void run() {
        try{

            Thread.sleep(1000);
            Socket sock = new Socket("172.31.85.85",5000);;
            System.out.println(sock.getInetAddress());
            System.out.println(sock.getInetAddress().getHostName());
            System.out.println(sock.getLocalAddress());
            System.out.println(sock.getLocalAddress().getHostName());
            System.out.println(sock.getRemoteSocketAddress());

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}