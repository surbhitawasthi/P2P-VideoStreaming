package test;

import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.*;
import com.recombee.api_client.bindings.RecommendationResponse;
import com.recombee.api_client.bindings.Recommendation;
import com.recombee.api_client.exceptions.ApiException;

import java.net.*;
import java.util.*;

public class BasicExample {
    public static void main(String[] args) {

        check();
        System.setProperty("http.proxySet", "true");
        System.setProperty("java.net.useSystemProxies", "true");
        System.setProperty("http.proxyHost", "172.31.100.29");
        System.setProperty("http.proxyPort", "3128");
        Authenticator.setDefault(new ProxyAuthenticator("edcguest", "edcguest"));

//        System.setProperty("http.proxyHost", "172.31.100.29");
//        System.setProperty("http.proxyPort", "3128");
//        System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");
//
//        System.setProperty("https.proxyHost", "172.31.100.29");
//        System.setProperty("https.proxyPort", "3128");
//        System.setProperty("https.nonProxyHosts", "localhost|127.0.0.1");
//
//        System.setProperty("http.proxyUser", "edcguest");
//        System.setProperty("http.proxyPassword", "edcguest");
//        System.setProperty("https.proxyUser", "edcguest");
//        System.setProperty("https.proxyPassword", "edcguest");
//
//        Authenticator.setDefault(new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                if (getRequestorType() == RequestorType.PROXY) {
//                    String prot = getRequestingProtocol().toLowerCase();
//                    System.out.println(prot);
//                    String host = System.getProperty(prot + ".proxyHost", "");
//                    String port = System.getProperty(prot + ".proxyPort", "");
//                    String user = System.getProperty(prot + ".proxyUser", "");
//                    String password = System.getProperty(prot + ".proxyPassword", "");
//                    if (getRequestingHost().equalsIgnoreCase(host)) {
//                        if (Integer.parseInt(port) == getRequestingPort()) {
//                            return new PasswordAuthentication(user, password.toCharArray());
//                        }
//                    }
//                }
//                return null;
//            }
//        });

        check();

        String secretToken = "MGowrzpa9y03pMsec8BYePfIEgQLSwtfX01161reCWywx0EY7Eg8Ni7iK43dTLDj";
        RecombeeClient client = new RecombeeClient("binary-warriors", secretToken);
        try {
            client.send(new ResetDatabase()); //Clear everything from the database
            final int NUM = 100;
            // Generate some random purchases of items by users
            final double PROBABILITY_PURCHASED = 0.1;
            Random r = new Random();
            ArrayList<Request> addPurchaseRequests = new ArrayList<Request>();
            for (int i = 0; i < NUM; i++)
                for (int j = 0; j < NUM; j++)
                    if (r.nextDouble() < PROBABILITY_PURCHASED) {

                        AddPurchase request = new AddPurchase(String.format("user-%s", i),String.format("item-%s", j))
                                .setCascadeCreate(true); // Use cascadeCreate parameter to create
                        // the yet non-existing users and items
                        addPurchaseRequests.add(request);
                    }

            System.out.println("Send purchases");
            //client.send(new Batch(addPurchaseRequests)); //Use Batch for faster processing of larger data

            // Get 5 recommendations for user 'user-25'
            RecommendationResponse recommendationResponse = client.send(new RecommendItemsToUser("user-25", 5));
            System.out.println("Recommended items:");
            for(Recommendation rec: recommendationResponse) System.out.println(rec.getId());

        } catch (ApiException e) {
            e.printStackTrace();
            //use fallback
        }
    }

    private static void check()
    {
        try {
            System.setProperty("java.net.useSystemProxies", "true");
            List<Proxy> l = ProxySelector.getDefault().select(
                    new URI("http://www.google.com/"));

            for (Iterator<Proxy> iter = l.iterator(); iter.hasNext();) {
                Proxy proxy = iter.next();
                System.out.println("proxy hostname : " + proxy.type());
                InetSocketAddress addr = (InetSocketAddress) proxy.address();

                if (addr == null) {
                    System.out.println("No Proxy");
                } else {
                    System.out.println("proxy hostname : " + addr.getHostName());
                    System.out.println("proxy port : " + addr.getPort());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
