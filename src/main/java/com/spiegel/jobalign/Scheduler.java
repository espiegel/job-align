package com.spiegel.jobalign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Eidan on 4/22/2015.
 */
public final class Scheduler {

    private static int poolSize = 4;
    private static Scheduler scheduler;

    private Logger mLogger = LoggerFactory.getLogger(this.getClass());
    private Set<String> mScheduledTasks;
    private ThreadPoolTaskScheduler mThreadPoolTaskScheduler;


    public static synchronized Scheduler getInstance() {
        if(scheduler == null) {
            scheduler = new Scheduler();
        }
        return scheduler;
    }

    private Scheduler() {
        mThreadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        mThreadPoolTaskScheduler.setPoolSize(poolSize);
        mThreadPoolTaskScheduler.initialize();
        mScheduledTasks = new HashSet<>();
    }

    public synchronized void scheduleCron(BaseDistributedJob job) {
        mLogger.debug("Scheduling task name {} with cron = {}", job.getJobName(), job.getCronExpression());
        scheduleCron(job.getJobName(), job.getRunnable(), new CronTrigger(job.getCronExpression()));
    }

    private synchronized void scheduleCron(String taskName, Runnable task, Trigger trigger) {
        if(!mScheduledTasks.contains(taskName)) {
            mScheduledTasks.add(taskName);
            mThreadPoolTaskScheduler.schedule(task, trigger);
        }
    }

    public static void setMaxPoolSize(int size) {
        poolSize = size;
    }
}