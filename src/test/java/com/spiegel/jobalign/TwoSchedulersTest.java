package com.spiegel.jobalign;

import static org.junit.Assert.assertEquals;

import com.spiegel.jobalign.factory.KeyValueProvider;
import com.spiegel.jobalign.factory.LockProvider;

/**
 * Created by Eidan on 4/22/2015.
 */
public class TwoSchedulersTest extends AbstractSchedulerTest {

    public TwoSchedulersTest(LockProvider lockProvider, KeyValueProvider keyValueProvider) {
        super(lockProvider, keyValueProvider);
    }

    public void testScheduler() {
        DistributedJobBuilder builder = new DistributedJobBuilder()
            .setLockProvider(getLockProvider())
            .setKeyValueProvider(getKeyValueProvider())
            .setCronExpression(getFutureCron(3))
            .setJobName(getClass().getName())
            .setJobLogic((shardNumber) -> count.incrementAndGet());

        BaseDistributedJob myJob1 = builder.build();
        BaseDistributedJob myJob2 = builder.build();

        myJob1.schedule();
        myJob2.schedule();

        assertEquals(0, count.get());
        sleep(5000);
        assertEquals(1, count.get());
    }
}
