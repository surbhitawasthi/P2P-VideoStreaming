package Client.Utility;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/*
        Has methods to save files on local system form a remote server
 */


public class SaveFile {
    //    Socket sock;
//    SaveFile(Socket sock){
//        this.sock=sock;
//    }
    public static final int BUFFER_SIZE=1024;
    public void saveFile( String path, ObjectInputStream ois) throws Exception {   //

        System.out.println("in saveFile Function...");
        FileOutputStream fos = null;

        byte [] buffer = new byte[BUFFER_SIZE];
        // 1. Read file name.
//        System.out.println("Waiting to read name of file through  Object o =ois.readObject();");
//        Object o = ois.readObject();
//        System.out.println("Reading from ObjectInputstream completed(Path read)");
//        System.out.println("Path= "+o.toString());
        Object o=null;

        // 2. Read file to the end.
        fos = new FileOutputStream(path);
        Integer bytesRead = 0;
        System.out.println("Going into File reading loop...");
        int c=0;
        do
        {
            //System.out.println("File recieve iteration = "+c++);
            o = ois.readObject();
            if (!(o instanceof Integer))
            {
                throwException("Something is wrong");
            }
            //System.out.println("o = ois.readObject(); THIS LINE WORKS");
            bytesRead = (Integer)o;
            //System.out.println("bytesRead = "+bytesRead);
            o = ois.readObject();

            if (!(o instanceof byte[]))
            {
                throwException("Something is wrong");
            }
            buffer = (byte[])o;
            // 3. Write data to output file.
            //System.out.println("Ready to write file");
            fos.write(buffer, 0, bytesRead);
            //System.out.println("Writing file");
        } while (bytesRead == BUFFER_SIZE);
        System.out.println("File transfer success");
        fos.close();

    }
    public static void throwException(String message) throws Exception {
        throw new Exception(message);
    }
}