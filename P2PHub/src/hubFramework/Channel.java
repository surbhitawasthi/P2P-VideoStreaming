package hubFramework;

import java.io.Serializable;
import java.util.ArrayList;

//For each user this data type will contain the following information
public class Channel implements Serializable
{
    protected String channelName;
    //protected int channelPopularityIndex;
    protected int totalNoOfLikes, totalNoOfComments, totalNoOfViews;
    protected int channelSubscribers;
    protected ArrayList<String> subscriberName;
    protected ArrayList<Video> videos;

    Channel(String name)
    {
        channelName = name;
        totalNoOfViews = 0;
        //channelPopularityIndex = 0;
        channelSubscribers = 0;
        totalNoOfLikes = 0;
        totalNoOfComments = 0;
        subscriberName = new ArrayList<String>();
        videos = new ArrayList<Video>();
    }
}
