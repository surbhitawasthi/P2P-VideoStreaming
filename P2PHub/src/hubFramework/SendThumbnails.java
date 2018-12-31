package hubFramework;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SendThumbnails implements Runnable
{
    HashMap<String, Video> videos;
    String ip;
    SendThumbnails(HashMap<String, Video> videos, String ip)
    {
        this.videos = videos;
        this.ip = ip;
    }

    @Override
    public void run()
    {
        Socket socket = null;
        ObjectOutputStream thumbnail = null;
        ObjectInputStream objectInputStream = null;
        try {
            socket = new Socket(ip, 11234);
            thumbnail = new ObjectOutputStream(socket.getOutputStream());
            //thumbnail object for writing and reading any value from peer
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            thumbnail.writeInt(videos.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            for(Map.Entry<String, Video> entry: videos.entrySet())
            {
                System.out.println("Sending "+entry.getValue().videoName+".png");
                thumbnail.writeUTF(entry.getValue().videoName+".png");
                System.out.println("Path = "+entry.getValue().thumbnailPath);
                sendFile(entry.getValue().thumbnailPath, thumbnail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                thumbnail.close();
                objectInputStream.close();
            }catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // sendFile function
    private void sendFile(String path, ObjectOutputStream oos)
    {

        try {
            System.out.println("In send file");

            String file_name = path;
            System.out.println("Input path= " + path);
            File file = new File(file_name);

            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            Integer bytesRead = 0;
            System.out.println("Sending file");
            while ((bytesRead = fis.read(buffer)) > 0) {
                //System.out.println("BytesRead = " + bytesRead);
                oos.writeObject(bytesRead);
                //System.out.println("These many bytes are send");
                oos.writeObject(Arrays.copyOf(buffer, buffer.length));
                //System.out.println("Going for next iteration");
            }

            oos.flush();
            System.out.println("File sent");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
