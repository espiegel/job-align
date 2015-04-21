package com.spiegel.jobalign;

import com.spiegel.jobalign.factory.KeyValueProvider;
import com.spiegel.jobalign.factory.LockProvider;
import com.spiegel.jobalign.factory.RedisProvider;
import org.redisson.Redisson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Created by Eidan on 4/21/2015.
 */
public abstract class BaseDistributedJob {

    private Logger mLogger = LoggerFactory.getLogger(this.getClass());

    private static final String KEY_JOB_LOCK = "keyJobLock";
    private static final String KEY_JOB_TIMESTAMP = "keyJobTimestamp";
    private static final int MAX_LOCK_ACQUISITION_IN_SECONDS = 10;

    private final String JOB_TIMESTAMP_NAME = KEY_JOB_TIMESTAMP + ":" + getJobName();
    private final String JOB_LOCK_NAME = KEY_JOB_LOCK + ":" + getJobName();

    private LockProvider lockProvider;
    private KeyValueProvider keyValueProvider;

    public BaseDistributedJob() {
    }

    public BaseDistributedJob(LockProvider lockProvider, KeyValueProvider keyValueProvider) {
        this.lockProvider = lockProvider;
        this.keyValueProvider = keyValueProvider;
    }

    public void schedule() {
        if(lockProvider == null) {
            throw new IllegalStateException("You must set a LockProvider!");
        }
        if(keyValueProvider == null) {
            throw new IllegalStateException("You must set a KeyValueProvider!");
        }
        Scheduler.getInstance().scheduleCron(this);
    }

    public abstract void performJobLogic();
    public abstract String getCronExpression();

    public Runnable getRunnable() {
        return this::runDistributedJob;
    }

    public String getJobName() {
        return this.getClass().getName();
    }

    private void runDistributedJob() {
        // Get the current timestamp
        long jobFiredTimeStamp = new Date().getTime();

        // Acquire the lock
        boolean lockAcquired = false;
        Lock lock = lockProvider.getLock(JOB_LOCK_NAME);

        try {
            lockAcquired = lock.tryLock(MAX_LOCK_ACQUISITION_IN_SECONDS, TimeUnit.SECONDS);

            if(lockAcquired) {
                // Check the timestamp
                long lockAcquisitionTimeStamp = new Date().getTime();
                long lastJobTimeStamp = keyValueProvider.getLong(JOB_TIMESTAMP_NAME);

                if(lastJobTimeStamp < jobFiredTimeStamp) {
                    mLogger.debug("About to perform job logic for {}, jobTimeStamp = {}, jobFiredTimeStamp = {}, lockAcquisitionTimeStamp = {}",
                            getJobName(), lastJobTimeStamp, jobFiredTimeStamp, lockAcquisitionTimeStamp);
                    // Perform job logic
                    performJobLogic();

                    // Write the timestamp
                    keyValueProvider.setLong(JOB_TIMESTAMP_NAME, lockAcquisitionTimeStamp);
                } else {
                    mLogger.debug("Job {} already performed, jobTimeStamp = {}, jobFiredTimeStamp = {}, lockAcquisitionTimeStamp = {}",
                            getJobName(), lastJobTimeStamp, jobFiredTimeStamp, lockAcquisitionTimeStamp);
                }
            }
        } catch(Exception e) {
            mLogger.warn(e.getMessage());
        } finally {
            if(lockAcquired) {
                lock.unlock();
            }
        }
    }

    public void setLockProvider(LockProvider lockProvider) {
        this.lockProvider = lockProvider;
    }

    public void setKeyValueProvider(KeyValueProvider keyValueProvider) {
        this.keyValueProvider = keyValueProvider;
    }

    public void setRedisson(Redisson redisson) {
        RedisProvider redisProvider = new RedisProvider(redisson);
        this.lockProvider = redisProvider;
        this.keyValueProvider = redisProvider;
    }
}
