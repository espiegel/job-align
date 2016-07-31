package com.spiegel.jobalign;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

/**
 * Created by Eidan on 4/22/2015.
 */
public final class Scheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);
    private static int poolSize = 4;
    private static Scheduler scheduler;

    private Set<String> scheduledTasks;
    private final ScheduledExecutorService scheduledExecutorService;

    public static synchronized Scheduler getInstance() {
        if(scheduler == null) {
            scheduler = new Scheduler();
        }
        return scheduler;
    }

    private Scheduler() {
        scheduledExecutorService = Executors.newScheduledThreadPool(poolSize);
        scheduledTasks = new HashSet<>();
    }

    public synchronized void scheduleCron(BaseDistributedJob job) {
        scheduleCron(job.getJobName(), job.getRunnable(), job.getCronExpression());
    }

    public synchronized void reset() {
        try {
            scheduledExecutorService.awaitTermination(1, TimeUnit.MILLISECONDS);
            scheduledTasks.clear();
        } catch (InterruptedException e) {
            LOGGER.error("Error resetting scheduler", e);
        }
    }
    
    private synchronized void scheduleCron(String taskName, Runnable task, String cron) {
        if(!scheduledTasks.contains(taskName)) {
            LOGGER.debug("Scheduling task name {} with cron = {}", taskName, cron);
            scheduledTasks.add(taskName);
            
            scheduleTask(task, cron);
        } else {
            LOGGER.debug("scheduledTasks already contains taskName = {}", taskName);
        }
    }

    private void scheduleTask(Runnable task, String cron) {
        final CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
        final ExecutionTime executionTime = ExecutionTime.forCron(cronParser.parse(cron));
        final DateTime now = DateTime.now();
        
        // TODO: We will need this future later in order to cancel a running task
        ScheduledFuture future = scheduledExecutorService.schedule(
                new JobWrapper(task, executionTime, () -> scheduleTask(task, cron)),
                executionTime.timeToNextExecution(now).getMillis(),
                TimeUnit.MILLISECONDS);
    }
    
    public static void setMaxPoolSize(int size) {
        poolSize = size;
    }
    
    private static class JobWrapper implements Runnable {
        private final Runnable task;
        private final ExecutionTime executionTime;
        private final Runnable callback;
        
        JobWrapper(Runnable task, ExecutionTime executionTime, Runnable callback) {
            this.task = task;
            this.executionTime = executionTime;
            this.callback = callback;
        }
        
        @Override
        public void run() {
            task.run();
            callback.run();
        }
        
        public Runnable getTask() {
            return task;
        }

        public ExecutionTime getExecutionTime() {
            return executionTime;
        }
    }
}