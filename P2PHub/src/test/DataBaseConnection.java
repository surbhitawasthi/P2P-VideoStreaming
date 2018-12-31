package test;

import java.sql.*;
import java.util.logging.Logger;

public class DataBaseConnection
{
    public static void main(String[] args)
    {
        try {
            Statement stm = connect();

        } catch (Exception e) {

        }
    }
    static Statement connect() {
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
}
