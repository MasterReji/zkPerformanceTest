
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.Collections;
import java.util.List;

public class zkClient implements Runnable {


    private final String TEST_NODE = "/test";
    private final String WATCH_NODE = "/watch";
    private String nodePath = null;
    private int myid = 0;
    ZKLink zkLink = null;
    private String myNodePath;
    private String watchNodePath;
    public zkClient(String zkHost, int n) {
        zkLink = new ZKLink(zkHost, new ClientNodeWatcher());
        myid=n;
    }


    @Override
    public void run() {
        watchNode();
    }

    /**
     * Method makeLeaderElection
     */
    public void watchNode() {
       // testNodePath = zkLink.createNode(TEST_NODE, false);  //skapar nod
      //  if (testNodePath == null) System.out.println("Could not access zookeeper path: " + TEST_NODE);
        watchNodePath = zkLink.createNode(WATCH_NODE, false);
        if (watchNodePath == null) System.out.println("Could not access zookeeper path: " + WATCH_NODE);
        zkLink.watchNode(WATCH_NODE, true);
        myNodePath = zkLink.createNode("/node"+myid, false);
        if(myNodePath == null) {
            System.out.println("Could not create " + myNodePath);
        }else
            System.out.println("Skapade: " + myNodePath);


    }

    public void sendWriteToNode() {
        System.out.println("Skriva test_"+myid +" i test-noden. med path: " + myNodePath);
        zkLink.setNodeData(myNodePath, "test_"+myid);
        System.out.println("Nu tillbaka till watch");
        /**/


        return;
    }

    public class ClientNodeWatcher implements Watcher {

        @Override
        public void process(WatchedEvent event) {

            final Event.EventType eventType = event.getType();
            if (Event.EventType.NodeDeleted.equals(eventType)) {
                if (event.getPath().equalsIgnoreCase(WATCH_NODE)) {  //VÅr watch node
                    System.out.println("vår watch försvann");
                    sendWriteToNode();
                }
            }
        }
    }

}
