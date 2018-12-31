package hubFramework;



import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;

/*
        Structure of video object received from HUB
 */

public class Video implements Serializable {

    protected String videoName;
    protected String ownerName;
    protected String pathOfVideo;
    protected String channelName;

    protected String alternatePathOfVideo;

    protected int numberOfLikes, numberOfDislikes, numberOfComments, numberOfViews;
    protected String thumbnailPath;
    protected ArrayList<String> tags;

    Video(String pathOfVideo, String ownerName, String thumbnailPath, ArrayList<String> tags, String channelName) throws FileNotFoundException {
        this.pathOfVideo = pathOfVideo;
        videoName = pathOfVideo.substring(pathOfVideo.lastIndexOf("/"));
        this.ownerName = ownerName;
        numberOfComments = numberOfLikes = numberOfDislikes = numberOfViews = 0;
        this.thumbnailPath = thumbnailPath;
        this.tags = tags;
        this.channelName = channelName;
        alternatePathOfVideo = "";
    }

    public String getAlternatePathOfVideo() {
        return alternatePathOfVideo;
    }

    public void setAlternatePathOfVideo(String alternatePathOfVideo) {
        this.alternatePathOfVideo = alternatePathOfVideo;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPathOfVideo() {
        return pathOfVideo;
    }

    public void setPathOfVideo(String pathOfVideo) {
        this.pathOfVideo = pathOfVideo;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public int getNumberOfDislikes() {
        return numberOfDislikes;
    }

    public void setNumberOfDislikes(int numberOfDislikes) {
        this.numberOfDislikes = numberOfDislikes;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public int getNumberOfViews() {
        return numberOfViews;
    }

    public void setNumberOfViews(int numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
