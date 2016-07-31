package com.spiegel.jobalign;

import static org.junit.Assert.assertEquals;

import com.spiegel.jobalign.factory.KeyValueProvider;
import com.spiegel.jobalign.factory.LockProvider;

/**
 * Created by Eidan on 7/31/2016.
 */
public class SchedulerTestFiresThreeTimes extends AbstractSchedulerTest {

    public SchedulerTestFiresThreeTimes(LockProvider lockProvider, KeyValueProvider keyValueProvider) {
        super(lockProvider, keyValueProvider);
    }

    public void testScheduler() {
        BaseDistributedJob myJob = new DistributedJobBuilder()
            .setLockProvider(getLockProvider())
            .setKeyValueProvider(getKeyValueProvider())
            .setCronExpression(getRecurringFutureCron(2, 6, 10))
            .setJobName(getClass().getName())
            .setJobLogic((shardNumber) -> count.incrementAndGet())
            .build();

        myJob.schedule();

        assertEquals(0, count.get());
        sleep(4000);
        assertEquals(1, count.get());
        sleep(4000);
        assertEquals(2, count.get());
        sleep(4000);
        assertEquals(3, count.get());
    }
}
