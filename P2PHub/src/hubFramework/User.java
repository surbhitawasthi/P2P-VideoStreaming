package hubFramework;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


//Main datatype for each and every user with the following attributes:
public class User implements Serializable
{
    protected String username;
    protected Boolean isCreator;
    protected ArrayList<Channel> channels;
    protected Queue<Pair<String, Video>> notification;
    User(String username)
    {
        this.username = username;
        channels = new ArrayList<Channel>();
        isCreator = false;
        notification = new LinkedList<Pair<String, Video> >();
    }
}
