package test;

public class SubstringTest
{
    public static void main(String[] args) {
        String name = "surbhit:awasthi:aks";
        System.out.println(name.substring(0,name.indexOf(":"))+"\n"+name.substring(name.indexOf(":")+1,name.lastIndexOf(":"))+"\n"+
                name.substring(name.lastIndexOf(":")+1));

//        String url = "/home/surbhit/starkhub/surbhit/premium/hello.mp4";
//        String vid_url="http://"+"172.31.132.111/";
    }
}
