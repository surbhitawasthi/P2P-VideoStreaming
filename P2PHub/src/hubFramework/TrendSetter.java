package hubFramework;

import java.sql.*;
import java.util.ArrayList;
import java.util.TimerTask;

public class TrendSetter extends TimerTask
{

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
        System.out.println("In trend setter thread");
        Statement stm = connect();
        System.out.println("Conneted to db");
        try {
            String maxInViewInOneFrame = "SELECT MAX(viewsInOneFrame) FROM trendingtable;";
            String maxInLastViewedTime = "SELECT MAX(lastViewedTime) FROM trendingtable;";
            ResultSet r1 = stm.executeQuery(maxInLastViewedTime);
            r1.next();
            double maxView = r1.getDouble(1);
            ResultSet r2 = stm.executeQuery(maxInViewInOneFrame);
            r2.next();
            double maxTime = r2.getDouble(1);
            double alpha = 0.33;
            /*
                score = alpha * totalViews + (x/(MAX(viewsInTimeFrameColumn)*10*(y/(MAX(lastViewedTime)))*10))
                for all i in db
            */
            String scoreSetQuery = "update trendingtable set score=("+alpha+"*totalViews)+((viewsInOneFrame/"+maxView+")*10 * " +
                    "(lastViewedTime/"+maxTime+")*10);";
            String clearAllViewAndTime = "update trendingtable set viewsInOneFrame=0, lastViewedTime=0;";
            System.out.println("Setting trend score");
            stm.executeUpdate(scoreSetQuery);
            System.out.println("Clearing all views and time of views in frame");
            stm.executeUpdate(clearAllViewAndTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Out of trendsetter thread");

    }
}
