package com.spiegel.jobalign.redis;

import com.spiegel.jobalign.SchedulerTest;
import com.spiegel.jobalign.ShardedThreeSchedulerTest;
import com.spiegel.jobalign.TwoSchedulersTest;
import com.spiegel.jobalign.factory.RedisProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.redisson.Redisson;

/**
 * Created by Eidan on 4/22/2015.
 */
@RunWith(JUnit4.class)
public class RedisTest {

    private Redisson redisson = Redisson.create();

    @Test
    public void testRedissonExists() {
        RedisProvider redisProvider = new RedisProvider(redisson);

        Assert.assertEquals(0, redisProvider.getLong("test"));
        redisProvider.setLong("test", 1);
        Assert.assertEquals(1, redisProvider.getLong("test"));

        redisson.getAtomicLong("test").set(0);
    }

    @Test
    public void testScheduler() {
        RedisProvider redisProvider = new RedisProvider(redisson);
        SchedulerTest schedulerTest = new SchedulerTest(redisProvider, redisProvider);

        schedulerTest.testScheduler();
    }

    @Test
    public void testTwoSchedulers() {
        RedisProvider redisProvider = new RedisProvider(redisson);
        TwoSchedulersTest twoSchedulersTest = new TwoSchedulersTest(redisProvider, redisProvider);

        twoSchedulersTest.testScheduler();
    }

    @Test
    public void testShardedThreeSchedulers() {
        RedisProvider redisProvider = new RedisProvider(redisson);
        ShardedThreeSchedulerTest shardedThreeSchedulerTest = new ShardedThreeSchedulerTest(redisProvider, redisProvider);

        shardedThreeSchedulerTest.testScheduler();
    }
}
