package com.spiegel.jobalign.defaultprovider;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.spiegel.jobalign.Scheduler;
import com.spiegel.jobalign.SchedulerTest;
import com.spiegel.jobalign.ShardedThreeSchedulerTest;
import com.spiegel.jobalign.TwoSchedulersTest;
import com.spiegel.jobalign.factory.DefaultKeyValueProvider;
import com.spiegel.jobalign.factory.DefaultLockProvider;

/**
 * Created by eidan on 9/23/15.
 */
@RunWith(JUnit4.class)
public class DefaultProviderTest {

    @After
    public void teardown() {
        Scheduler.getInstance().reset();
    }
    
    @Test
    public void testScheduler() {
        SchedulerTest schedulerTest = new SchedulerTest(new DefaultLockProvider(), new DefaultKeyValueProvider());
        schedulerTest.testScheduler();
    }

    @Test
    public void testTwoSchedulers() {
        TwoSchedulersTest twoSchedulersTest = new TwoSchedulersTest(new DefaultLockProvider(), new DefaultKeyValueProvider());
        twoSchedulersTest.testScheduler();
    }

    @Test
    public void testShardedThreeSchedulers() {
        ShardedThreeSchedulerTest test = new ShardedThreeSchedulerTest(new DefaultLockProvider(), new DefaultKeyValueProvider());
        test.testScheduler();
    }
}
