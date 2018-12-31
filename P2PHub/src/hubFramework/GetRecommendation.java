package hubFramework;

import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.RecommendItemsToUser;
import com.recombee.api_client.bindings.Recommendation;
import com.recombee.api_client.bindings.RecommendationResponse;
import com.recombee.api_client.exceptions.ApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class GetRecommendation
{
    String usernmae;
    private final String database = "binary-warriors";
    private final String secretToken = "MGowrzpa9y03pMsec8BYePfIEgQLSwtfX01161reCWywx0EY7Eg8Ni7iK43dTLDj";

    public GetRecommendation(String username)
    {
        this.usernmae = username;
    }

    public ArrayList<String> generate()
    {
        ArrayList<String> result = new ArrayList<String>();
        RecombeeClient client = new RecombeeClient(database, secretToken);
        try{
            RecommendationResponse recommendationResponse = client.send(new RecommendItemsToUser(usernmae, 4));
            System.out.println("Recommended items:");
            for(Recommendation rec: recommendationResponse)
            {
                String t = rec.getId();
                //byte[] b64decoded = Base64.getDecoder().decode(t);
                //result.add(new String(b64decoded));
                result.add(t);
                System.out.println(t);
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return result;
    }
}
