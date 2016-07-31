package com.spiegel.jobalign.defaultprovider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.spiegel.jobalign.Scheduler;
import com.spiegel.jobalign.SchedulerTest;
import com.spiegel.jobalign.SchedulerTestFiresThreeTimes;
import com.spiegel.jobalign.ShardedThreeSchedulerTest;
import com.spiegel.jobalign.TwoSchedulersTest;
import com.spiegel.jobalign.factory.DefaultKeyValueProvider;
import com.spiegel.jobalign.factory.DefaultLockProvider;
import com.spiegel.jobalign.factory.KeyValueProvider;
import com.spiegel.jobalign.factory.LockProvider;

/**
 * Created by eidan on 9/23/15.
 */
@RunWith(JUnit4.class)
public class DefaultProviderTest {

    private LockProvider lockProvider;
    private KeyValueProvider keyValueProvider;

    @Before
    public void init() {
        lockProvider = new DefaultLockProvider();
        keyValueProvider = new DefaultKeyValueProvider();
    }
    
    @After
    public void teardown() {
        Scheduler.getInstance().reset();
    }
    
    @Test
    public void testScheduler() {
        SchedulerTest schedulerTest = new SchedulerTest(lockProvider, keyValueProvider);
        schedulerTest.testScheduler();
    }

    @Test
    public void testTwoSchedulers() {
        TwoSchedulersTest twoSchedulersTest = new TwoSchedulersTest(lockProvider, keyValueProvider);
        twoSchedulersTest.testScheduler();
    }

    @Test
    public void testShardedThreeSchedulers() {
        ShardedThreeSchedulerTest test = new ShardedThreeSchedulerTest(lockProvider, keyValueProvider);
        test.testScheduler();
    }
    
    @Test
    public void testRecurringTestFiresThreeTimes() {
        SchedulerTestFiresThreeTimes test = new SchedulerTestFiresThreeTimes(lockProvider, keyValueProvider);
        test.testScheduler();
    }
}
