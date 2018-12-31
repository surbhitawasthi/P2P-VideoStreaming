package Client.DataClasses;

import java.io.Serializable;
import java.util.ArrayList;

// Channel class: For storing attributes and behaviour of Channels

public class Channel implements Serializable {

    protected String channelName;
    protected int numberOfVideos;
    protected String channelOwner;

    ArrayList<Video> videoList;

    // Constructor
    public Channel(String channelName, String channelOwner){
        this.channelName = channelName;
        this.channelOwner = channelOwner;
        videoList = new ArrayList<>();
    }

    // Accessor: channelName
    public String getChannelName() {
        return channelName;
    }

    // Modifier: channelName
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    // Accessor: numberOfVideos
    public int getNumberOfVideos() {
        return numberOfVideos;
    }

    // Modifier: numberOfVideos
    public void setNumberOfVideos(int numberOfVideos) {
        this.numberOfVideos = numberOfVideos;
    }

    // Accessor: channelName
    public String getChannelOwner() {
        return channelOwner;
    }

    // Modifier: channelOwner
    public void setChannelOwner(String channelOwner) {
        this.channelOwner = channelOwner;
    }

    // Accessor: videoList
    public ArrayList<Video> getVideoList() {
        return videoList;
    }

    // Modifier: videoList
    public void setVideoList(ArrayList<Video> videoList) {
        this.videoList = videoList;
    }
}
