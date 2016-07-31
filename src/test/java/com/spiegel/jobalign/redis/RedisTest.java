package com.spiegel.jobalign.redis;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.redisson.Redisson;

import com.spiegel.jobalign.Scheduler;
import com.spiegel.jobalign.SchedulerTest;
import com.spiegel.jobalign.SchedulerTestFiresThreeTimes;
import com.spiegel.jobalign.ShardedThreeSchedulerTest;
import com.spiegel.jobalign.TwoSchedulersTest;
import com.spiegel.jobalign.factory.RedisProvider;

/**
 * Created by Eidan on 4/22/2015.
 */
@RunWith(JUnit4.class)
public class RedisTest {

    public static final String TEST_KEY = "test";
    private Redisson redisson = Redisson.create();
    private RedisProvider redisProvider;

    @Before
    public void init() {
        redisProvider = new RedisProvider(redisson);    
    }
    
    @After
    public void teardown() {
        Scheduler.getInstance().reset();
    }
    
    @Test
    public void testRedissonExists() {
        assertEquals(0, redisProvider.getLong(TEST_KEY));
        redisProvider.setLong(TEST_KEY, 1);
        assertEquals(1, redisProvider.getLong(TEST_KEY));

        redisson.getAtomicLong(TEST_KEY).set(0);
    }

    @Test
    public void testScheduler() {
        SchedulerTest schedulerTest = new SchedulerTest(redisProvider, redisProvider);
        schedulerTest.testScheduler();
    }

    @Test
    public void testTwoSchedulers() {
        TwoSchedulersTest twoSchedulersTest = new TwoSchedulersTest(redisProvider, redisProvider);
        twoSchedulersTest.testScheduler();
    }

    @Test
    public void testShardedThreeSchedulers() {
        ShardedThreeSchedulerTest shardedThreeSchedulerTest = new ShardedThreeSchedulerTest(redisProvider, redisProvider);
        shardedThreeSchedulerTest.testScheduler();
    }

    @Test
    public void testRecurringTestFiresThreeTimes() {
        SchedulerTestFiresThreeTimes test = new SchedulerTestFiresThreeTimes(redisProvider, redisProvider);
        test.testScheduler();
    }
}
