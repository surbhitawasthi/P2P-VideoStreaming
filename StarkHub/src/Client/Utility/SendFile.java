package Client.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class SendFile {
    public static final int BUFFER_SIZE=1024;
    public void sendFile(Socket sock, String path, ObjectOutputStream oos)
    {
        try {
            String file_name = path;
            File file = new File(file_name);
//            oos.writeObject(file.getName());        //  reads the name of file
            FileInputStream fis = new FileInputStream(file);
            byte [] buffer = new byte[BUFFER_SIZE];
            Integer bytesRead = 0;
            while ((bytesRead = fis.read(buffer)) > 0)
            {
                //System.out.println("bytesRead = "+bytesRead);
                oos.writeObject(bytesRead);
                oos.writeObject(Arrays.copyOf(buffer, buffer.length));
                oos.flush();
            }
            System.out.println("Send Function exiting");
            oos.flush();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
