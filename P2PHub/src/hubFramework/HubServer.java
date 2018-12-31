package hubFramework;

import javafx.util.Pair;

import java.net.*;
import java.io.*;
import java.util.*;

// Main class for all connection accepting

public class HubServer
{

    protected static Queue<Pair<String,String> > userListToNotify;
    protected static HashSet<String> unSentNotification;


    public static void main(String[] args)throws IOException
    {
        userListToNotify = new LinkedList<Pair<String,String> >();
        unSentNotification = new HashSet<String>();

        // starting real_time notification
        SendRealTimeNotificationDaemon realTime = new SendRealTimeNotificationDaemon();
        new Thread(realTime).start();

        // timer for trend_setter
        Timer timer = new Timer();
        timer.schedule(new TrendSetter(), 0, 300 * 1000);

        ServerSocket hub = new ServerSocket(1111);
        //connection accept and start new thread for all
        while(true)
        {
            System.out.println("Waiting for connection accept");
            Socket connectedSocket = hub.accept();
            System.out.println("Connection arrived: "+connectedSocket.getInetAddress().getHostAddress());
            System.out.println("canonical host name: "+connectedSocket.getInetAddress().getCanonicalHostName());
            Peer peer = new Peer(connectedSocket);
            System.out.println("Streams initialized and objectified");
            new Thread(new HubPeerCommunication(peer)).start();
            System.out.println("Thread started for "+connectedSocket.getInetAddress().getHostAddress());
        }
    }
}
