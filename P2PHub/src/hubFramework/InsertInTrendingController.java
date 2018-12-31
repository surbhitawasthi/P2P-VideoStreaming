package hubFramework;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.Date;

public class InsertInTrendingController implements Runnable
{
    String channelName, videoName, username;
    public InsertInTrendingController(String username, String channelName, String videoName)
    {
        this.username = username;
        this.channelName = channelName;
        this.videoName = videoName;
    }

    private Statement connect() {
        Statement stm = null;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/starkhub", "surbhit", "awasthi@7");
            stm = conn.createStatement();

        } catch (SQLException ex) {
            System.out.println("SQL me error!!!");
            ex.printStackTrace();
        }
        return stm;
    }

    @Override
    public void run()
    {
        Statement stm  = connect();
        String updateQuery = "";
        Long lastViewedTime = ZonedDateTime.now().toInstant().toEpochMilli();
        System.out.println("In insert in trending controller");
        try {
            //TODO: use prepared statement
            updateQuery = "INSERT INTO trendingtable VALUES ('" + channelName + ":" + videoName
                    + "', 1, 1, " + lastViewedTime +
                    ", 10, '"+username+"');";//initial score is 10
            stm.executeUpdate(updateQuery);
            System.out.println("Trending table updated init");
        } catch (Exception e) {
            System.out.println("In catch");
            System.out.println(e.getMessage());
            updateQuery = "UPDATE trendingtable SET totalViews=totalViews+1, viewsInOneFrame=viewsInOneFrame+1, " +
                    "lastViewedTime="+lastViewedTime+" WHERE channelVideo='"+channelName+":"+videoName+"';";
            try {
                stm.executeUpdate(updateQuery);
                System.out.println("Trending table updated in catch");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
}
