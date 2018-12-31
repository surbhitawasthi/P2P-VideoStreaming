package Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ThumbTest {

    public static void main(String[] args) throws Exception {
        String path = "/home/aks/Videos/Berklee.mp4";
        String vidName = path.substring(path.lastIndexOf("/")+1);
        String firstPath = path.substring(0, path.lastIndexOf("/")+1);
        System.out.println(vidName+"\n"+firstPath);
        generateThumbnail("/home/aks/Videos/Berklee.mp4");
        System.exit(0);

        HashMap<String, hubFramework.Video> map = new HashMap<>();
        Iterator it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry keyVal = (Map.Entry) it.next();

        }
        for(Map.Entry<String, hubFramework.Video> entry: map.entrySet()){
            System.out.println(entry.getKey()+" "+entry.getValue());
        }


    }

    static void generateThumbnail(String path) throws Exception{
        String time = "00:00:20";
        String outPath = System.getProperty("user.home") + "/Desktop/out.png";
        String command = "ffmpeg "+" -ss "+time+" -i "+path+" -vf scale=-1:120  -vcodec png "+outPath;
        System.out.println(command);
        Runtime run = Runtime.getRuntime();
        Process p = run.exec(command);
        p.waitFor();

    }
}

