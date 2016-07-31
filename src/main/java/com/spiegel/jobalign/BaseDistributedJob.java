package com.spiegel.jobalign;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.redisson.Redisson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spiegel.jobalign.factory.KeyValueProvider;
import com.spiegel.jobalign.factory.LockProvider;
import com.spiegel.jobalign.factory.RedisProvider;

/**
 * Created by Eidan on 4/21/2015.
 */
public abstract class BaseDistributedJob {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

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

    /**
     * Perform job logic for a specific shardNumber. Shard numbers start from 0 to getNumberOfShards() - 1 (inclusive)
     * If your job isn't sharded then you shouldn't care about this parameter.
     * @param shardNumber Shard number of the job
     */
    public abstract void performJobLogic(int shardNumber);

    /**
     *
     * @return A cron expression following cron conventions
     */
    public abstract String getCronExpression();


    /**
     * @return Number of shards of this distributed job. Override this method
     * to set a different number. Default is 1.
     */
    public int getNumberOfShards() {
        return 1;
    }

    public final Runnable getRunnable() {
        return this::runDistributedJob;
    }

    public String getJobName() {
        return this.getClass().getName();
    }

    private String getJobName(int shardNumber) {
        return getJobName() + ":" + shardNumber;
    }

    public final void schedule() {
        if(lockProvider == null) {
            throw new IllegalStateException("You must set a LockProvider!");
        }
        if(keyValueProvider == null) {
            throw new IllegalStateException("You must set a KeyValueProvider!");
        }
        Scheduler.getInstance().scheduleCron(this);
    }

    private void runDistributedJob() {
        // Get the current timestamp
        long jobFiredTimeStamp = new Date().getTime();

        int numberOfShards = getNumberOfShards();
        for(int i = 0; i < numberOfShards; i++) {

            // Acquire the lock
            boolean lockAcquired = false;
            Lock lock = lockProvider.getLock(getJobLockName(i));

            try {
                lockAcquired = lock.tryLock(MAX_LOCK_ACQUISITION_IN_SECONDS, TimeUnit.SECONDS);

                if(lockAcquired) {
                    // Check the timestamp
                    long lockAcquisitionTimeStamp = new Date().getTime();
                    long lastJobTimeStamp = keyValueProvider.getLong(getJobTimestampName(i));

                    if(lastJobTimeStamp < jobFiredTimeStamp) {
                        LOGGER.debug("About to perform job logic for {}, jobTimeStamp = {}, jobFiredTimeStamp = {}, lockAcquisitionTimeStamp = {}",
                            getJobName(i), lastJobTimeStamp, jobFiredTimeStamp, lockAcquisitionTimeStamp);
                        // Perform job logic
                        performJobLogic(i);

                        // Write the timestamp
                        keyValueProvider.setLong(getJobTimestampName(i), lockAcquisitionTimeStamp);
                    } else {
                        LOGGER.debug("Job {} already performed, jobTimeStamp = {}, jobFiredTimeStamp = {}, lockAcquisitionTimeStamp = {}",
                            getJobName(i), lastJobTimeStamp, jobFiredTimeStamp, lockAcquisitionTimeStamp);
                    }
                }
            } catch(Exception e) {
                LOGGER.warn(e.getMessage());
            } finally {
                if(lockAcquired) {
                    lock.unlock();
                }
            }
        }
    }

    private String getJobLockName(int shardNumber) {
        return JOB_LOCK_NAME + ":" + shardNumber;
    }

    private String getJobTimestampName(int shardNumber) {
        return JOB_TIMESTAMP_NAME + ":" + shardNumber;
    }

    public final void setLockProvider(LockProvider lockProvider) {
        this.lockProvider = lockProvider;
    }

    public final void setKeyValueProvider(KeyValueProvider keyValueProvider) {
        this.keyValueProvider = keyValueProvider;
    }

    public final void setRedisson(Redisson redisson) {
        RedisProvider redisProvider = new RedisProvider(redisson);
        this.lockProvider = redisProvider;
        this.keyValueProvider = redisProvider;
    }
}
