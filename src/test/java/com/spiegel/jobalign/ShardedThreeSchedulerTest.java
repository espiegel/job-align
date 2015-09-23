package com.spiegel.jobalign;

import com.spiegel.jobalign.factory.KeyValueProvider;
import com.spiegel.jobalign.factory.LockProvider;
import org.junit.Assert;

/**
 * Created by eidan on 9/23/15.
 */
public class ShardedThreeSchedulerTest extends AbstractSchedulerTest {

    public ShardedThreeSchedulerTest(LockProvider lockProvider, KeyValueProvider keyValueProvider) {
        super(lockProvider, keyValueProvider);
    }

    public void testScheduler() {
        DistributedJobBuilder builder = new DistributedJobBuilder()
            .setLockProvider(getLockProvider())
            .setKeyValueProvider(getKeyValueProvider())
            .setCronExpression(getFutureCron(3))
            .setJobName(getClass().getName())
            .setJobLogic((shardNumber) -> count.incrementAndGet())
            .setShards(5);

        BaseDistributedJob myJob1 = builder.build();
        BaseDistributedJob myJob2 = builder.build();
        BaseDistributedJob myJob3 = builder.build();

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
}
