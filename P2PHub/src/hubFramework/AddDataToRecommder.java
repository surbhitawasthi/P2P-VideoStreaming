package hubFramework;

import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.*;
import com.recombee.api_client.bindings.RecommendationResponse;
import com.recombee.api_client.bindings.Recommendation;
import com.recombee.api_client.exceptions.ApiException;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class AddDataToRecommder implements Runnable
{
//    String videoName, username, likes, comments, views;
//        public AddDataToRecommder(String videoName, String username, String likes, String comments, String views) {
//        this.videoName = videoName;
//        this.username = username;
//        this.likes = likes;
//        this.comments = comments;
//        this.views = views;
//    }
    String usernameOfUserWhoClicked;
    Video video;
    private final String secretToken = "MGowrzpa9y03pMsec8BYePfIEgQLSwtfX01161reCWywx0EY7Eg8Ni7iK43dTLDj";
    private final String database = "binary-warriors";

    AddDataToRecommder(Video video, String usernameOfUserWhoClicked)
    {
        this.video = video;
        this.usernameOfUserWhoClicked = usernameOfUserWhoClicked;
    }

    @Override
    public void run()
    {
        System.out.println("In adding data to recommder");
        RecombeeClient client = new RecombeeClient(database, secretToken);

        try {
            //client.send(new ResetDatabase()); //Clear everything from the database

            String videoName = video.ownerName+":"+video.channelName+":"+video.videoName;

            //String b64encodedVideoName = Base64.getEncoder().encodeToString(videoName.getBytes());

            final SetItemValues req = new SetItemValues(videoName, //itemId
                    //values:
                    new HashMap<String, Object>() {{
                        put("likes", video.numberOfLikes);
                        put("comments", video.numberOfComments);
                        put("views", video.numberOfViews);
                    }}
            ).setCascadeCreate(true);  // Use cascadeCreate for creating item
            // with given itemId, if it doesn't exist;
            //requests.add(req);

            client.send(req); // Send catalog to the recommender system



            //AddPurchase reqForPurchase = new AddPurchase(usernameOfUserWhoClicked,videoName).setCascadeCreate(true); //use cascadeCreate to create the users

            AddPurchase reqForPurchase = new AddPurchase(usernameOfUserWhoClicked,videoName).setCascadeCreate(true);

            client.send(reqForPurchase); // Send purchases to the recommender system
            System.out.println("Done purchase and added item");

        } catch (ApiException e) {
            e.printStackTrace();
            //Use fallback
        }
    }
}
