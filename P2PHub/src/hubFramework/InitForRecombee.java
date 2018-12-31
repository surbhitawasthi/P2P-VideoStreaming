package hubFramework;

import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.AddItemProperty;
import com.recombee.api_client.api_requests.ResetDatabase;
import com.recombee.api_client.exceptions.ApiException;

public class InitForRecombee
{
    private static final String secretToken = "MGowrzpa9y03pMsec8BYePfIEgQLSwtfX01161reCWywx0EY7Eg8Ni7iK43dTLDj";
    private static final String database = "binary-warriors";

    public static void main(String args[])
    {
        RecombeeClient client = new RecombeeClient(database, secretToken); // connection to api

        try {
            client.send(new ResetDatabase()); //Clear everything from the database
            // adding properties to the database of items
            client.send(new AddItemProperty("likes", "int"));
            client.send(new AddItemProperty("comments", "int"));
            client.send(new AddItemProperty("views", "int"));
            System.out.println("Database ready");
        } catch (ApiException e){
            e.printStackTrace();
        }
    }
}
