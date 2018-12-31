package Server;

import Client.Login.Main;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;


/*
    Client side server: Handles serving of videos to other clients
 */

public class Server implements Runnable
{
    protected static int numberOfConnections = 0;
    public static String starkHubUsername;
    private static String username;
    private static String rootPasswd;

    // Accessor: username
    public static String getUsername() {
        return username;
    }

    // Modifier: username
    public static void setUsername(String username) {
        Server.username = username;
    }

    // Accessor: rootPasswd
    public static String getRootPasswd() {
        return rootPasswd;
    }

    // Modifier: rootPasswd
    public static void setRootPasswd(String rootPasswd) {
        Server.rootPasswd = rootPasswd;
    }

    public void run() {

        //Start the apache service
        try {
            JPanel panel1 = new JPanel();
            JPanel panel2 = new JPanel();
            JLabel label1 = new JLabel("Enter your root password:");
            JLabel label2 = new JLabel("Enter your username:");
            JTextField user = new JTextField(30);
            JPasswordField pass = new JPasswordField(30);
            panel1.add(label1);
            panel1.add(pass);
            panel2.add(label2);
            panel2.add(user);
            String[] options = new String[]{"OK", "Cancel", "Exit"};
            Runtime runtime = Runtime.getRuntime();
            int option;
            while (true) {
                try {
                    option = JOptionPane.showOptionDialog(null, panel2, "Username", JOptionPane.NO_OPTION,
                            JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
                    if (option == 0) // pressing OK button
                    {
                        String username = user.getText();
                        setUsername(username);
                    } else if (option == 1) {
                        JOptionPane.showMessageDialog(null, "You must specify a username");
                    } else {
                        System.exit(0);
                    }

                    option = JOptionPane.showOptionDialog(null, panel1, "Password", JOptionPane.NO_OPTION,
                            JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
                    if (option == 0) // pressing OK button
                    {
//                    String username = user.getText();
//                    setUsername(username);
                        String passwd = pass.getText();
                        setRootPasswd(passwd);
                        String command = "echo " + getRootPasswd() + " | sudo -S /etc/init.d/apache2 restart";
                        String[] cmd = {"/bin/bash", "-c", command};
                        Process process = runtime.exec(cmd, null);
                        Scanner z = new Scanner(process.getInputStream());
                        process.waitFor();
                        if (!z.hasNext()) {
                            JOptionPane.showMessageDialog(null, "Sorry you entered wrong username or password.");
                        } else {
                            break;
                        }

                    } else if (option == 1) {
                        JOptionPane.showMessageDialog(null, "You must specify a password name");
                    } else {
                        System.exit(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.println(getUsername() + "\n" + getRootPasswd());

            // Listening for connections from other peers
            starkHubUsername = Main.USERNAME;
            HashSet<String> people = new HashSet<String>();
            ServerSocket incomingConnection = new ServerSocket(15001);
            while (true) {
                Socket socket = incomingConnection.accept();
                System.out.println("Server: Incoming socket: "+socket);
                String username = "";
                Peer peer=null;
                try{
                    peer = new Peer(socket);
                    username = peer.dis.readUTF();
                    System.out.println("Server: Read Username: "+username);
                }catch(Exception ex){
                    ex.printStackTrace();
                }

                // Max number of peer connections is 5
                if (people.size() < 5 || people.contains(username) || socket.getInetAddress().getHostAddress().equals("172.31.84.87")) {
                    peer.dos.writeBoolean(true);
                    if (people.size() < 5)
                        people.add(username);
                    try {
                        String flag = peer.dis.readUTF();
                        switch (flag) {
                            case "#DISCONNECT": {
                                System.out.println("Server: #DISCONNECT received");
                                people.remove(username);
                                peer.dis.close();
                                peer.dos.close();
                                peer.oos.close();
                                peer.ois.close();
                                peer.peerSocket.close();
                                break;
                            }
                            case "#GETCOMMENTS": {
                                System.out.println("Server: #GETCOMMENTS");
                                HashMap<String, String> commentMap = null;
                                String videoName = peer.dis.readUTF();
                                File file = new File(System.getProperty("user.home") + "/starkhub/" + Server.starkHubUsername + "/comments/" + videoName);
                                if (!file.exists()) {
                                    peer.oos.writeObject(null);
                                } else {
                                    ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
                                    try {
                                        commentMap = (HashMap<String, String>) readSerializedObject.readObject();
                                        peer.oos.writeObject(commentMap);
                                        readSerializedObject.close();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                                System.out.println("Server: Comments Sent");
                                break;
                            }
                            case "#ADDCOMMENT": {
                                System.out.println("Server: #ADDCOMMENT");
                                HashMap<String, String> commentMap = null;
                                String videoName = peer.dis.readUTF();
                                String commenter = peer.dis.readUTF();
                                String commentText = peer.dis.readUTF();
                                File file = new File(System.getProperty("user.home") + "/starkhub/" + Server.starkHubUsername + "/comments/" + videoName);
                                if (!file.exists()) {
                                    ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(file));
                                    commentMap = new HashMap<String, String>();
                                    commentMap.put(commentText, commenter);
                                    writeSerializedObject.writeObject(commentMap);
                                    writeSerializedObject.close();
                                } else {
                                    try {
                                        ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
                                        commentMap = (HashMap<String, String>) readSerializedObject.readObject();
                                        commentMap.put(commentText, commenter);
                                        System.out.println("Server: Writting back");
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

                            case "#RECEIVEDATA" : {
                                // TODO: Logic
                                System.out.println("Server:  #RECEIVEDATA");
                                int n = peer.dis.readInt();
                                System.out.println("Server: rececived n: "+n);
                                ArrayList<String> list = new ArrayList<>();
                                list = (ArrayList<String>) peer.ois.readObject();
                                System.out.println("Server: list: "+list );
                                peer.dos.writeUTF(System.getProperty("user.home")+"/starkhub/"+ Main.USERNAME+"/premium/");
                                ReceiveData receive = new ReceiveData(n, list, peer.dis);
                                receive.start();
                                Thread.sleep(500);
                                peer.dos.writeBoolean(true);
                                System.out.println("Sent Boolean: true" );
                                break;
                            }

                            default: {
                                System.out.println("Ye kya flag hai be :" + flag+"---------");
                            }
                        }
                    } catch (IOException e) {
                        e.getCause();
                        e.getMessage();
                        e.printStackTrace();
                    }
                } else {
                    try {
                        // Closing connection and streams to a peer
                        peer.dos.writeBoolean(false);
                        peer.dos.close();
                        peer.dis.close();
                        peer.oos.close();
                        peer.ois.close();
                        peer.peerSocket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            //Listen for closing of appliction -> if yes people[index].interrupt();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}