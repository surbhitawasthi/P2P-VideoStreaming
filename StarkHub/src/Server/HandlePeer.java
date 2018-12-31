package Server;

import java.io.*;
import java.util.HashMap;


/*
        TODO: Not used anymore: REMOVE
 */

@Deprecated
public class HandlePeer implements Runnable
{
    Peer peer;
    HandlePeer(Peer peer)
    {
        this.peer = peer;
    }

    @Override
    public void run()
    {
        boolean endSession = false;
        while(true)
        {
            try {
                if(endSession) {
                    synchronized (this) {
                        Server.numberOfConnections--;
                        try {
                            peer.dos.close();
                            peer.dis.close();
                            peer.oos.close();
                            peer.ois.close();
                            peer.peerSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                String flag = peer.dis.readUTF();
                switch (flag)
                {
                    case "#DISCONNECT":
                    {
                        endSession = true;
                        break;
                    }
                    case "#GETCOMMENTS":
                    {
                        System.out.println("#GETCOMMENTS");
                        HashMap<String, String> commentMap = null;
                        String videoName = peer.dis.readUTF();
                        File file = new File(System.getProperty("user.home")+"/starkhub/"+Server.starkHubUsername+"/comments/"+videoName);
                        if(!file.exists())
                        {
                            peer.oos.writeObject(null);
                        }
                        else
                        {
                            ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
                            try {
                                commentMap = (HashMap<String, String>) readSerializedObject.readObject();
                                peer.oos.writeObject(commentMap);
                                readSerializedObject.close();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                    case "#ADDCOMMENT":
                    {
                        System.out.println("#ADDCOMMENT");
                        HashMap<String, String> commentMap = null;
                        String videoName = peer.dis.readUTF();
                        String commenter = peer.dis.readUTF();
                        String commentText = peer.dis.readUTF();
                        File file = new File(System.getProperty("user.home")+"/starkhub/"+Server.starkHubUsername+"/comments/"+videoName);
                        if(!file.exists())
                        {
                            ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(file));
                            commentMap = new HashMap<String, String>();
                            commentMap.put(commenter, commentText);
                            writeSerializedObject.writeObject(commentMap);
                            writeSerializedObject.close();
                        }
                        else
                        {
                            try {
                                ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
                                commentMap = (HashMap<String, String>) readSerializedObject.readObject();
                                commentMap.put(commenter, commentText);
                                System.out.println("Writting back");
                                ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(file));
                                writeSerializedObject.writeObject(commentMap);
                                readSerializedObject.close();
                                writeSerializedObject.close();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                    default:
                    {
                        System.out.println("Ye kya flag hai be "+flag);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//            finally{
//                if(!peer.peerSocket.isClosed()) {
//                    try {
//                        peer.dos.close();
//                        peer.dis.close();
//                        peer.oos.close();
//                        peer.ois.close();
//                        peer.peerSocket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
        }
    }
}
