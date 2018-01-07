import com.sun.deploy.util.SessionState;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Launcher {

    public static void main(String[] args){

/**
 * TOT_NO_OF_NODES must be 10 000
 * noOfClients must be 1 , 2 , 4, 5 , 10 , 50 ,100, 200,250,400,500,625,1000,
 *
 * */
        final int TOT_NO_OF_NODES = 10;
        int noOfClients = 5;
        int nodesPerClient = TOT_NO_OF_NODES/noOfClients;

        String zkHost = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
        //ClientNode cN = new ClientNode(zkHost);
        //cN.run();


        while(noOfClients-- != 0) {

            final ExecutorService service = Executors.newSingleThreadExecutor();
            final Future<?> status = service.submit(new zkClient(zkHost , noOfClients, nodesPerClient));

            try {
                status.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                service.shutdown();
            }
        }
    }
}
