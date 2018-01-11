
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class zkClient implements Runnable {
    private final String WATCH_NODE_0 = "/watch0";
    private int myId = 0;
    private int nodeCounter = 0;
    private int firstClient = 0;
    private ZKLink zkLink = null;
    private String watchNodePath;

    public zkClient(String zkHost, int id, int nodesPerClient, int firstClient) {
        zkLink = new ZKLink(zkHost, new ClientNodeWatcher());
        myId = id;
        nodeCounter = nodesPerClient;
        this.firstClient = firstClient;
    }

    @Override
    public void run() {
        if (myId == firstClient) {
            watchNodePath = zkLink.createNode(WATCH_NODE_0, false);
            if (watchNodePath == null)
                System.out.println("Could not access zookeeper path: " + WATCH_NODE_0);
        }
        zkLink.watchNode(WATCH_NODE_0, true);
        try {
            createNodes();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void createNodes() throws InterruptedException {
        for (int i = nodeCounter; i > 0; i--) {
            zkLink.createNode("/node" + myId + "_" + i, false);
        }
    }

    private synchronized void deleteNodes() throws InterruptedException {
        for (int i = nodeCounter; i > 0; i--) {
            zkLink.deleteNode("/node" + myId + "_" + i);
        }
        zkLink.close();
    }

    public class ClientNodeWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {

            final Event.EventType eventType = event.getType();
            if (Event.EventType.NodeDeleted.equals(eventType)) {
                if (event.getPath().equalsIgnoreCase(WATCH_NODE_0)) {  //Vår watch node
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
