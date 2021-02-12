package es.job.v1.infrastructure;

import es.zk.client.Connection;
import es.zk.client.ServiceRegistrar;
import es.zk.client.ServiceSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by aseleznev on 02.02.2021.
 */
public class TaskExecutorLauncherImpl implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(TaskExecutorLauncherImpl.class.getName());

    private final ScheduledExecutorService launcher = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFutureLauncher;
    Connection zkConnection = new Connection();
    private ServiceRegistrar serviceRegistrar = new ServiceRegistrar();
    private ServiceSearcher serviceSearcher = new ServiceSearcher();
    private long delay;
    private long period;

    public void start() {
        log.info("start launcher {}", System.getenv("HOSTNAME"));
        scheduledFutureLauncher = launcher.scheduleWithFixedDelay(this, getDelay(), getPeriod(), TimeUnit.MILLISECONDS);
        try {
            zkConnection.connect("localhost");

        } catch (Exception e) {
            log.error("zk problem", e);
        }
    }

    public void run() {
        try {
            if (Thread.interrupted()) {
                scheduledFutureLauncher.cancel(false);
                return;
            }

            if (!zkConnection.isAvailable()) {
                zkConnection.connect("localhost");
            }

            if (zkConnection.getNodeChildren("/spu/services/test").size() < 10) {
                serviceRegistrar.register(zkConnection, "test");
            }

            System.out.println("search " + serviceSearcher.search(zkConnection, "test"));


        } catch (Exception e) {
            log.error("run problem", e);
        }
    }

    public void tearDown() {
        log.info("attempt to stop service launcher");
        launcher.shutdownNow();
        try {
            zkConnection.close();
        } catch (InterruptedException e) {
            log.error("tearDown problem", e);
        }
        log.info("service launcher stopped successfully");
    }




    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }




}
