package com.spiegel.jobalign;

import com.spiegel.jobalign.factory.DefaultKeyValueProvider;
import com.spiegel.jobalign.factory.DefaultLockProvider;
import com.spiegel.jobalign.factory.KeyValueProvider;
import com.spiegel.jobalign.factory.LockProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by eidan on 9/23/15.
 */
@RunWith(JUnit4.class)
public class ShardedThreeSchedulerTest {

    private Logger logger = LoggerFactory.getLogger(ShardedThreeSchedulerTest.class);
    private AtomicInteger count = new AtomicInteger();

    @Test
    public void testScheduler() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 3);
        int second = calendar.get(Calendar.SECOND);
        String cron = second + " * * * * ?";

        LockProvider lockProvider = new DefaultLockProvider();
        DefaultKeyValueProvider defaultKeyValueStore = new DefaultKeyValueProvider();

        BaseDistributedJob myJob1 = new MyJob(lockProvider, defaultKeyValueStore, cron);
        BaseDistributedJob myJob2 = new MyJob(lockProvider, defaultKeyValueStore, cron);
        BaseDistributedJob myJob3 = new MyJob(lockProvider, defaultKeyValueStore, cron);

        myJob1.schedule();
        myJob2.schedule();
        myJob3.schedule();

        Assert.assertEquals(0, count.get());

        try {
            Thread.sleep(5000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(5, count.get());
    }

    public class MyJob extends BaseDistributedJob {

        private final String cron;

        public MyJob(LockProvider lockProvider, KeyValueProvider keyValueProvider, String cron) {
            super(lockProvider, keyValueProvider);
            this.cron = cron;
        }

        @Override
        public void performJobLogic(int shardNumber) {
            logger.debug("Perform job logic for jobName = {} and shardNumber = {}", getJobName(), shardNumber);
            count.incrementAndGet();
        }

        @Override
        public String getCronExpression() {
            return cron;
        }

        @Override
        public int getNumberOfShards() {
            return 5;
        }
    }
}
