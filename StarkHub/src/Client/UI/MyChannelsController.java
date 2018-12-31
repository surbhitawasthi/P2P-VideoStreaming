package Client.UI;

import Client.DataClasses.Channel;
import Client.DataClasses.Video;
import Client.Login.Main;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MyChannelsController implements Initializable {

    HashMap<String,Channel> map;
    VBox vBox;

    @FXML
    ScrollPane  clientScrollPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try{
            init();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void init() throws Exception{
        map = MainPageController.myChannelMap;
        System.out.println("MyChannelMap: "+map);
        vBox = new VBox(10);
        vBox.setPadding(new Insets(10));
//
//        File[] thumbList = new File(System.getProperty("user.home")+"/starkhub/"+ Main.USERNAME+"/thumbnails/").listFiles();
//        // Name of thumbnail: videoname.png (not videoName.mp4.png)

        // TODO: GET Number of likes, comments, views from hub




        for(Map.Entry<String, Channel> entry: map.entrySet()){

            Socket sock = new Socket(Main.HUB_IP,1111);
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
            ObjectInputStream ois= new ObjectInputStream(sock.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

            dout.writeUTF("#GETSTATOFCHANNEL");
            dout.writeUTF(Main.USERNAME);

            Channel c = entry.getValue();
            System.out.println("Channel: "+c.getChannelName());

            AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/myChannelChannelItem.fxml"));
            Label chanName = (Label)pane.getChildren().get(0);
            Label likeslbl, cmtlbl, subslbl, viewslbl, ratlbl;
            likeslbl = (Label)pane.getChildren().get(1);
            cmtlbl = (Label)pane.getChildren().get(2);
            subslbl = (Label)pane.getChildren().get(3);
            viewslbl = (Label)pane.getChildren().get(4);
            ratlbl = (Label)pane.getChildren().get(5);

//            dout.writeUTF("#GETSTATOFCHANNEL");
//            dout.writeUTF(Main.USERNAME);
            dout.writeUTF(c.getChannelName());

            double channelRating = dis.readDouble();
            System.out.println(""+c.getChannelName()+" : rat "+channelRating);
            int totalLikes = dis.readInt();
            System.out.println(""+c.getChannelName()+" : likes: "+totalLikes);
            int totalComments = dis.readInt();
            System.out.println(""+c.getChannelName()+" : comnt "+totalComments);
            int totalSubscribers = dis.readInt();
            System.out.println(""+c.getChannelName()+" : subs "+totalSubscribers);
            int totalViews = dis.readInt();
            System.out.println(""+c.getChannelName()+" : views "+totalViews);

            chanName.setText(c.getChannelName());
            likeslbl.setText("Likes: "+totalLikes);
            cmtlbl.setText("Comments: "+totalComments);
            subslbl.setText("Subscribers: "+totalSubscribers);
            viewslbl.setText("Views: "+totalViews);
            ratlbl.setText(""+channelRating*10);


            vBox.getChildren().add(pane);

            ArrayList<hubFramework.Video> list = (ArrayList<hubFramework.Video>) ois.readObject();
            HashMap<String, hubFramework.Video> videonamemap = new HashMap<>();

            System.out.println("list: "+list);

            for(hubFramework.Video v:list){
                videonamemap.put(v.getVideoName(), v);
            }

            System.out.println("VideonameMap: "+videonamemap);

            AnchorPane content = FXMLLoader.load(getClass().getResource("../Layouts/myChannelItemContainer.fxml"));
            JFXListView<AnchorPane> listView = (JFXListView<AnchorPane>) content.getChildren().get(0);

            System.out.println("VideoList:"+ c.getVideoList());
            for(Video v: c.getVideoList() ){
                System.out.println(""+v.getVideoName());
                System.out.println(""+videonamemap.containsKey(v.getVideoName()));
            }

            for(Video v: c.getVideoList()) {

                AnchorPane item = FXMLLoader.load(getClass().getResource("../Layouts/myChannelItem.fxml"));
                ImageView imgv = (ImageView) (item.getChildren().get(0));
                Label videoNameLabel = (Label) (item.getChildren().get(1));
                Label likesLabel = (Label) (item.getChildren().get(2));
                Label commentsLabel = (Label) (item.getChildren().get(3));
                Label viewsLabel = (Label) (item.getChildren().get(4));

                hubFramework.Video vid = videonamemap.get(v.getVideoName());

                try {
                    imgv.setImage(new Image(new FileInputStream(new File(v.getThumbnailPath()))));
                    videoNameLabel.setText(v.getVideoName());
                    likesLabel.setText("Likes: " + vid.getNumberOfLikes());
                    commentsLabel.setText("Comments: " + vid.getNumberOfComments());
                    viewsLabel.setText("Views: " + vid.getNumberOfViews());
                }catch (Exception e){
                    e.getCause();
                    e.printStackTrace();
                }

                listView.getItems().add(item);
            }
            vBox.getChildren().add(content);
            // TODO: Setup on UI
        }

        clientScrollPane.setContent(vBox);

    }


}
