package es.zk.client;

import org.apache.zookeeper.KeeperException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceSearcher {

    private final ConcurrentHashMap<String, AtomicInteger> indexMap = new ConcurrentHashMap<>();

    public String search(Connection zkConnection, String name) throws KeeperException, InterruptedException {
        String result = "0";
        String service = "/spu/services/" + name;

        if (zkConnection.exists(service) == null) {
            return result;
        }

        List<String> instances = zkConnection.getNodeChildren(service);
        if (instances.isEmpty()) {
            return result;
        }

        AtomicInteger index = indexMap.get(name);
        if (index == null) {
            index = new AtomicInteger(0);
            indexMap.put(name, index);
        }

        int indexRoundRobin = Math.abs(index.getAndIncrement());


        String instance = instances.get(indexRoundRobin % instances.size());

        String numInstance = zkConnection.getData(service + "/" + instance);

        if (numInstance != null) {
            result = numInstance;
        }

        return result;
    }
}
