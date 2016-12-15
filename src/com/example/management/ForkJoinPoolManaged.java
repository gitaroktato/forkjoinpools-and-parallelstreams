package com.example.management;
import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ForkJoinPool;
/**
 * Created by Oresztesz_Margaritis on 12/15/2016.
 */
public class ForkJoinPoolManaged implements ForkJoinPoolMXBean {

    private final ForkJoinPool pool;

    public ForkJoinPoolManaged(ForkJoinPool pool) {
        this.pool = pool;
    }

    public ForkJoinPool getPool() {
        return pool;
    }

    @Override public int getActiveThreadCount() {
        return pool.getActiveThreadCount();
    }

    @Override public int getRunningThreadCount() {
        return pool.getRunningThreadCount();
    }

    @Override public int getQueuedSubmissionCount() {
        return pool.getQueuedSubmissionCount();
    }

    @Override public int getPoolSize() {
        return pool.getPoolSize();
    }

    @Override public long getStealCount() {
        return pool.getStealCount();
    }

    public void registerMBean()
            throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException,
            MBeanRegistrationException {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        mbs.registerMBean(this,
                new ObjectName("com.example:type=ForkJoinPoolManaged"));
    }
}
