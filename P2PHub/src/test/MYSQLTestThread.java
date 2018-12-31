package test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MYSQLTestThread implements Runnable{

    Statement stm;
    public MYSQLTestThread(Statement stm)
    {
        this.stm = stm;
    }

    @Override
    public void run() {
        try {
            ResultSet rs = stm.executeQuery("select score from trendingtable where channelVideo='"+"aks"+"'");
            rs.next();
            System.out.println(rs.getInt(1));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
