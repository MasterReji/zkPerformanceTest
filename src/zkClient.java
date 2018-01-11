
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class zkClient implements Runnable {
    private final String WATCH_NODE_0 = "/watch0";
    private final String WATCH_NODE_1 = "/watch1";
    private int myid = 0;
    private int nodecounter = 0;
    private int firstClient = 0;
    private ZKLink zkLink = null;
    private String watchNodePath;

    public zkClient(String zkHost, int id, int nodesPerClient, int firstClient) {
        zkLink = new ZKLink(zkHost, new ClientNodeWatcher());
        myid = id;
        nodecounter = nodesPerClient;
        this.firstClient = firstClient;
    }

    @Override
    public void run() {
        try {
            createNodes();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //  watchNodes();
    }

    /**
     * när watch0 raderas så skall alla klienter börja skapa sina noder. watch1:an är till för att radera noderna sedan
     */
    private void watchNodes() {
        if (firstClient == myid) {  //bara första clienten skapar noderna
            watchNodePath = zkLink.createNode(WATCH_NODE_0, false);
           //if (watchNodePath == null) System.out.println("Could not access zookeeper path: " + WATCH_NODE_0);
            watchNodePath = zkLink.createNode(WATCH_NODE_1, false);
            //if (watchNodePath == null) System.out.println("Could not access zookeeper path: " + WATCH_NODE_1);
        }
        zkLink.watchNode(WATCH_NODE_0, true);
        zkLink.watchNode(WATCH_NODE_1, true);
    }

    private synchronized void createNodes() throws InterruptedException {

        for (int i = nodecounter; i > 0; i--) {
            zkLink.createNode("/node" + myid + "_" + i, false);
        }
        if (firstClient == myid){
            watchNodePath =zkLink.createNode(WATCH_NODE_1, false);
        }
        zkLink.watchNode(WATCH_NODE_1, true);
    }

    private synchronized void deleteNodes() throws InterruptedException {
        for (int i = nodecounter; i > 0; i--) {
            zkLink.deleteNode("/node" + myid + "_" + i);
        }
        zkLink.close();
    }

    public class ClientNodeWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {

            final Event.EventType eventType = event.getType();
            if (Event.EventType.NodeDeleted.equals(eventType)) {
                if (event.getPath().equalsIgnoreCase(WATCH_NODE_0)) {  //Vår watch node
                    //System.out.println("Watch0 raderas");
                    try {
                        createNodes();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (event.getPath().equalsIgnoreCase(WATCH_NODE_1)) {
                   // System.out.println("Watch1 raderas");
                    try {
                        deleteNodes();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
