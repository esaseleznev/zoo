package es.zk.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;

public class Connection implements AutoCloseable {

    private ZooKeeper zoo;
    private boolean isAvailable = false;
    final CountDownLatch connectionLatch = new CountDownLatch(1);


    public void connect(String host) throws IOException,
            InterruptedException {

        zoo = new ZooKeeper(host, 30000, new Watcher() {
            public void process(WatchedEvent we) {
                if (we.getState() == KeeperState.SyncConnected) {
                    isAvailable = true;
                    connectionLatch.countDown();
                } else if (we.getState() == KeeperState.Expired || we.getState() == KeeperState.Disconnected) {
                    isAvailable = false;
                }
            }
        });

        connectionLatch.await();
    }

    public void close() throws InterruptedException {
        zoo.close();
    }

    public String create(String path, String data, CreateMode mode) throws KeeperException, InterruptedException {
        try {
            return zoo.create(path, data != null ? data.getBytes(StandardCharsets.UTF_8) : null, ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
        } catch (KeeperException e) {
            if (KeeperException.Code.NODEEXISTS == e.code()) {
                return path;
            } else {
                throw e;
            }
        }
    }

    public Stat exists(String path) throws
            KeeperException,InterruptedException {
        return zoo.exists(path, true);
    }

    public String getData(String path) {
        try {
            Stat stat = exists(path);
            if (stat != null) {
                byte[] b = zoo.getData(path, null,null);
                String data = new String(b, "UTF-8");
                return data;
            } else {
                System.out.println("Node does not exists");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void update(String path, String data) throws KeeperException, InterruptedException {
        Stat stat = exists(path);
        if (stat != null) {
            int version = stat.getVersion();
            zoo.setData(path, data.getBytes(StandardCharsets.UTF_8), version);
        }
    }

    public void delete(String path) throws KeeperException, InterruptedException {
        Stat stat = exists(path);
        if (stat != null) {
            int version = stat.getVersion();
            zoo.delete(path, version);
        }
    }

    public List<String> getNodeChildren(String path) throws KeeperException,
            InterruptedException {
        Stat stat = exists(path);
        List<String> children  = new ArrayList<>();

        if (stat != null) {
            children = zoo.getChildren(path, false);
        } else {
            System.out.println("Node does not exists");
        }
        return children;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

}
