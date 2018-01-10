
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

public class ZKLink {

    ZooKeeper zk;

    public ZKLink(String zkURL, zkClient.ClientNodeWatcher clientNodeWatcher) {
        try {
            zk = new ZooKeeper(zkURL, 1000000, clientNodeWatcher);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Write t node
    public void setNodeData(String nodePath, String newData) {

        try {
            byte[] data = newData.getBytes();
            zk.setData(nodePath, data, zk.exists(nodePath, true).getVersion());
            System.out.println("newData är: " + newData);
        } catch (KeeperException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    //read to node
    public String getNodeData(String path) {
        String data = "nada";
        try {
            byte[] bn = zk.getData(path, false, null);
            data = new String(bn, "UTF-8");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            return data;
        }
    }

    public void deleteNode(String nodePath) {
        try {
            if (zk.exists(nodePath, false) != null) {
                zk.delete(nodePath, zk.exists(nodePath, true).getVersion());
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public String createNode(String nodePath, boolean ephimeral) {

        try {
            if (zk.exists(nodePath, true) == null) {
                zk.create(nodePath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
               // System.out.println("nyckellängd: " +  nodePath.getBytes().length);
                //System.out.
            }
            else return null;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean watchNode(String node, boolean watch) {
        boolean watched = false;
        try {
            final Stat nodeStat = zk.exists(node, watch);

            if (nodeStat != null) {
                watched = true;
            }

        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return watched;
    }

    public void close() throws InterruptedException {
        zk.close();
    }
}
