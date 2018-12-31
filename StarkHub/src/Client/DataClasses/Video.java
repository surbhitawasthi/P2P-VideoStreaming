package Client.DataClasses;

import javafx.scene.image.Image;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;

// Video class: For storing attributes an behaviours of Video

public class Video implements Serializable {

    protected String videoName;
    protected ArrayList<String> tags;
    protected String videoPath;
    protected int numberOfLikes = 0;
    protected int numberOfComments = 0;
    protected ArrayList<Comment> comments;
    protected String thumbnailPath;


    // Constructor
    public Video(String _videoName, ArrayList<String> _tags, String _videoPath){
        this.tags = _tags;
        this.videoName = _videoName;
        this.videoPath = _videoPath;
    }

    // Accessor: videoName
    public String getVideoName(){
        return this.videoName;
    }

    // Accessor: videoPath
    public String getVideoPath(){
        return this.videoPath;
    }

    // Accessor: tags
    public ArrayList<String> getTags(){
        return this.tags;
    }

    public void setTags(ArrayList<String > _tags){
        this.tags = _tags;
    }

    public void addComment(String userName, String content){
        Comment newComment = new Comment(userName, content);
        comments.add(newComment);
    }

    // Accessor: thumbnail
    public Image getThumbnail() throws Exception{
        return new Image(new FileInputStream(thumbnailPath));
    }

    // Accessor: thumbnailPath
    public String getThumbnailPath(){
        return this.thumbnailPath;
    }

    public void setThumbnailPath(String path){
        this.thumbnailPath = path;
    }
}
