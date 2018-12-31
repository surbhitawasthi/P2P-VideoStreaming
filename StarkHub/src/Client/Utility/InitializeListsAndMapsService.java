package Client.Utility;

import Client.DataClasses.Channel;
import Client.Login.Main;
import Client.UI.MainPageController;
import hubFramework.Video;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/*
        Initialised at startup
        reads the state of previous instance
        and accordingly deserializes objects
 */

public class InitializeListsAndMapsService extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try{

                    // TODO: Add functionality
                    //System.out.println("\n\n Add Functionality to me \n\n");
                    ObjectInputStream ois;
                    File f = new File(System.getProperty("user.home") + "/starkhub/"+Main.USERNAME +"/watchLater/list");
                    if(!f.exists()){
                        System.out.println("WatchLater file does not exist");
                        MainPageController.watchLaterList = new ArrayList<>();
                    }else {
                        ois = new ObjectInputStream(new FileInputStream(f));
                        MainPageController.watchLaterList = (ArrayList<Video>) ois.readObject();
                        ois.close();
                        System.out.println("Read WatchLater");
                    }

                    f = new File(System.getProperty("user.home") + "/starkhub/"+Main.USERNAME +"/history/list");
                    if(!f.exists()){
                        System.out.println("history file does not exist");
                        MainPageController.historyList = new ArrayList<>();
                    }else {
                        ois = new ObjectInputStream(new FileInputStream(f));
                        MainPageController.historyList = (ArrayList<Video>) ois.readObject();
                        ois.close();
                        System.out.println("Read History");
                    }

                    f = new File(System.getProperty("user.home") + "/starkhub/"+Main.USERNAME +"/mychannels/list");
                    if(!f.exists()){
                        System.out.println("myChannelMap file does not exist");
                        MainPageController.myChannelMap= new HashMap<>();
                    }else {
                        ois = new ObjectInputStream(new FileInputStream(f));
                        MainPageController.myChannelMap = (HashMap<String, Channel>) ois.readObject();
                        ois.close();
                        System.out.println("Read Mychannels");
                    }

                    f = new File(System.getProperty("user.home") + "/starkhub/"+Main.USERNAME +"/subscriptions/list");
                    if(!f.exists()){
                        System.out.println("Subscribed file does not exist");
                        MainPageController.subscribedChannelMap= new HashMap<>();
                    }else {
                        ois = new ObjectInputStream(new FileInputStream(f));
                        MainPageController.subscribedChannelMap = (HashMap<String, String>) ois.readObject();
                        ois.close();
                        System.out.println("Read Subscriptions");
                    }

                    f = new File(System.getProperty("user.home") + "/starkhub/"+Main.USERNAME +"/prem/list");
                    if(!f.exists()){
                        System.out.println("Subscribed file does not exist");
                        MainPageController.premiumVideoList= new ArrayList<>();
                    }else {
                        ois = new ObjectInputStream(new FileInputStream(f));
                        MainPageController.premiumVideoList = (ArrayList<Pair<Client.DataClasses.Video, String>>) ois.readObject();
                        ois.close();
                        System.out.println("Read premList");
                    }

                    System.out.println("WatchLater: "+MainPageController.watchLaterList);
                    System.out.println("Histroy: "+MainPageController.historyList);
                    System.out.println("My channels: "+MainPageController.myChannelMap);
                    System.out.println("Subscriptions: "+MainPageController.subscribedChannelMap );
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
    }


}
