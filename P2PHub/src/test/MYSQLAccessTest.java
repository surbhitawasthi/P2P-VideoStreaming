package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MYSQLAccessTest
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

    public static void main(String[] args) throws SQLException {
        MYSQLAccessTest obj = new MYSQLAccessTest();

        Statement s1 = obj.connect();
        Statement s2 = obj.connect();
        new Thread(new MYSQLTestThread(s2)).start();
        int rs = s1.executeUpdate("insert into trendingtable values ('mm', 0, 0, 0, 5);");
        System.out.printf("done update "+ rs);
    }
}
