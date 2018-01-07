
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class zkClient implements Runnable {
    private final String WATCH_NODE_0 = "/watch0";
    private final String WATCH_NODE_1 = "/watch1";
    private String nodePath = null;
    private int myid = 0;
    private int nodecounter = 0;
    private int nodesPerClient = 0;
    ZKLink zkLink = null;
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
    public void watchNodes() {
        watchNodePath = zkLink.createNode(WATCH_NODE_0, false);
        if (watchNodePath == null) System.out.println("Could not access zookeeper path: " + WATCH_NODE_0);
        watchNodePath = zkLink.createNode(WATCH_NODE_1, false);
        if (watchNodePath == null) System.out.println("Could not access zookeeper path: " + WATCH_NODE_1);

        zkLink.watchNode(WATCH_NODE_0, true);
        zkLink.watchNode(WATCH_NODE_1, true);
    }

    public void createNodes() {
        while(nodecounter-- > 0)
            zkLink.createNode("/node"+ myid + "_" + nodecounter, false);
        nodecounter = nodesPerClient; //counter återställs till ursprungsvärdet
    }
    private void deleteNodes() {
        while(nodecounter-- > 0){
          zkLink.deleteNode("/node" + myid + "_" + nodecounter);
          System.out.println("/node" + myid + "_" + nodecounter);
        }
        nodecounter = nodesPerClient; //counter återställs till ursprungsvärdet
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
                if(event.getPath().equalsIgnoreCase(WATCH_NODE_1)){
                    System.out.println("Wach1 raderas");
                    deleteNodes();
                }
            }
        }
    }
}
