package es.job.v1.infrastructure;

/**
 * Created by aseleznev on 02.02.2021.
 */
public class TaskLauncherFactoryImpl {

    private long delay;
    private long period;


    public TaskExecutorLauncherImpl getInstance() {
        TaskExecutorLauncherImpl launcher = new TaskExecutorLauncherImpl();
        launcher.setPeriod(getPeriod());
        launcher.setDelay(getDelay());
        return launcher;
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
