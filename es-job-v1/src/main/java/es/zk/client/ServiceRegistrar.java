package es.zk.client;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class ServiceRegistrar {
    public String register(Connection zkConnection, String name) throws Exception {
        String service = "/spu/services/" + name;

        if (zkConnection.exists("/spu") == null) {
            zkConnection.create("/spu", null, CreateMode.PERSISTENT);
        }

        if (zkConnection.exists("/spu/services") == null) {
            zkConnection.create("/spu/services", null, CreateMode.PERSISTENT);
        }

        if (zkConnection.exists(service) == null) {
            zkConnection.create(service, null, CreateMode.PERSISTENT);
        }

        String nodePath = zkConnection.create(service + "/i", null, CreateMode.EPHEMERAL_SEQUENTIAL);
        String node = nodePath.split("/")[4];

        return getServiceNum(zkConnection, service, node, 0);

    }

    private String getServiceNum(Connection zkConnection, String service, String node, int attempt) throws Exception {

        attempt++;

        if (attempt > 20) {
            throw new Exception("the number of service registration attempts exceeded 20 - " + service);
        }


        List<String> children = zkConnection.getNodeChildren(service);

        java.util.Collections.sort(children);

        LinkedHashSet<Integer> sequence = new LinkedHashSet<>();
        for (int i = 0; i < children.size(); i++) {
            sequence.add(i);
        }

        for (int i = 0; i < children.size(); i++) {
            String current = children.get(i);
            if (!node.equals(current)) {
                String lastNumStr = zkConnection.getData(service + "/" + current);
                if (lastNumStr == null) {
                    Thread.sleep(500);
                    return getServiceNum(zkConnection, service, node, attempt);
                }
                Integer lastNum = Integer.valueOf(lastNumStr);
                System.out.println(current + "=" + lastNumStr);
                sequence.remove(lastNum);
            } else {
                for (Integer s : sequence) {
                    System.out.println(s);
                }
                Iterator iterator = sequence.iterator();
                Integer num = (Integer) iterator.next();
                String numStr = num.toString();
                zkConnection.update(service + "/" + current, numStr);
                return numStr;
            }
        }
        return null;
    }
}
