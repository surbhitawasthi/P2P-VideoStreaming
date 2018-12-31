package Test;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class CheckSum {


    public static void main(String[] args) {
        String path = System.getProperty("user.home")+"/Videos/";
        File f = new File(path);
        File[] lst = f.listFiles();
        System.out.println(""+lst);
        for(File fl:lst) {

            if(fl.isFile()) {
                System.out.println("F:"+fl.getName());
                System.out.println(checkSum(fl.getAbsolutePath()));
            }
        }
    }

   static String checkSum(String path){
       String checksum = null;
       try {
           FileInputStream fis = new FileInputStream(path);
           MessageDigest md = MessageDigest.getInstance("MD5");

           //Using MessageDigest update() method to provide input
           byte[] buffer = new byte[52428800];
           int numOfBytesRead;
           while( (numOfBytesRead = fis.read(buffer)) != 0){
               md.update(buffer, 0, numOfBytesRead);
           }
           byte[] hash = md.digest();
           checksum = new BigInteger(1, hash).toString(16); //don't use this, truncates leading zero
       } catch (Exception ex) {
           ex.printStackTrace();
       }
       return checksum;
   }

}
