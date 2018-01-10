
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class zkClient implements Runnable {
    private final String WATCH_NODE_0 = "/watch0";
    private final String WATCH_NODE_1 = "/watch1";
    private int myid = 0;
    private int nodecounter = 0;
    private int nodesPerClient = 0;
    private ZKLink zkLink = null;
    private String watchNodePath;

    public zkClient(String zkHost, int id, int nodesPerClient) {
        zkLink = new ZKLink(zkHost, new ClientNodeWatcher());
        myid=id;
        nodecounter = nodesPerClient;
        this.nodesPerClient = nodesPerClient;
    }

    @Override
    public void run() {
        watchNodes();
    }

    /**
     * när watch0 raderas så skall alla klienter börja skapa sina noder. watch1:an är till för att radera noderna sedan
     * */
    private void watchNodes() {
        watchNodePath = zkLink.createNode(WATCH_NODE_0, false);
       // if (watchNodePath == null) System.out.println("Could not access zookeeper path: " + WATCH_NODE_0);
        watchNodePath = zkLink.createNode(WATCH_NODE_1, false);
        //if (watchNodePath == null) System.out.println("Could not access zookeeper path: " + WATCH_NODE_1);

        zkLink.watchNode(WATCH_NODE_0, true);
        zkLink.watchNode(WATCH_NODE_1, true);
    }

    private void createNodes() {
        for(int i = nodecounter; i > 0; i--) {
            zkLink.createNode("/node"+ myid + "_" + i, false);
        }
        System.out.println("nu funkar den lilla sharmutan");
    }

    private void deleteNodes() throws InterruptedException {
        for(int i = nodecounter; i > 0; i--) {
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
                    System.out.println("Watch0 raderas");
                    createNodes();
                }
                else if(event.getPath().equalsIgnoreCase(WATCH_NODE_1)){
                    System.out.println("Watch1 raderas");
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
