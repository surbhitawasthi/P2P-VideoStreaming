package Client.Utility;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class SubscribeService extends Service {

    int flag; // 1 to Subscribe, 0 to unsubscribe

    String ownerName, channelName;

    public SubscribeService(int flag, String ownerName, String channelName){

        this.flag = flag;
        this.ownerName = ownerName;
        this.channelName = channelName;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try{

                    if(flag == 0){

                    }else if(flag == 1){

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
