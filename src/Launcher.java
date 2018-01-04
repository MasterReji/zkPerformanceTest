import com.sun.deploy.util.SessionState;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Launcher {

    public static void main(String[] args){

        String zkHost = "192.168.0.115:2181,192.168.0.115:2182,192.168.0.115:2183";
        //ClientNode cN = new ClientNode(zkHost);
        //cN.run();
        int n =100;

        while(n-- != 0) {

            final ExecutorService service = Executors.newSingleThreadExecutor();
            final Future<?> status = service.submit(new zkClient(zkHost , n));

            try {
                status.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                service.shutdown();
            }
        }
    }
}
