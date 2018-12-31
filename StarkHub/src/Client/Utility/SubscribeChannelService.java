package Client.Utility;

import Client.Login.Main;
import Client.UI.MainPageController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/*
        Initilaised when Subscribe Channel button is Clicked
 */

public class SubscribeChannelService extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try{

                    File f = new File(System.getProperty("user.home")+"/starkhub/"+ Main.USERNAME+"/subscriptions/list");
                    if(!f.exists()){
                        f.createNewFile();
                    }

                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
                    oos.writeObject(MainPageController.subscribedChannelMap);
                    oos.close();

                    System.out.println("Subscribed channel map serialised");

                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
