import com.sun.deploy.util.SessionState;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Launcher {

    public static void main(String[] args) {

/**
 * TOT_NO_OF_NODES must be 10 000
 * noOfClients must be 1 , 2 , 4, 5 , 10 , 50 ,100, 200,250,400,500,625,1000,
 *
 * */
        final int TOT_NO_OF_NODES = 100;
        int noOfClients = 500;
        int firstClient = noOfClients - 1;
        int nodesPerClient = TOT_NO_OF_NODES / noOfClients;
        String zkHost = "192.168.0.101:2181,192.168.0.101:2182,192.168.0.101:2183";
        //String zkHost = "10.200.59.205:2181,10.200.59.205:2182,10.200.59.205:2183";

        while (noOfClients-- != 0) {

            new Thread(new zkClient(zkHost, noOfClients, TOT_NO_OF_NODES, firstClient)).start();

        }
    }
}