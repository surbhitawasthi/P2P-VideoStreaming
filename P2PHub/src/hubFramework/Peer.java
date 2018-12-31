package hubFramework;

import java.io.*;
import java.net.Socket;

// Peer type data type
public class Peer {
    protected Socket peerSocket;
    protected DataInputStream dis;
    protected DataOutputStream dos;
    protected ObjectOutputStream oos;
    protected ObjectInputStream ois;

    Peer(Socket s)
    {
        peerSocket = s;
        try{
            dis = new DataInputStream(peerSocket.getInputStream());
            dos = new DataOutputStream(peerSocket.getOutputStream());
            oos = new ObjectOutputStream(peerSocket.getOutputStream());
            ois = new ObjectInputStream(peerSocket.getInputStream());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
