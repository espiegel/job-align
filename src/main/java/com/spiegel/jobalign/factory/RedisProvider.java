package com.spiegel.jobalign.factory;

import org.redisson.Redisson;

import java.util.concurrent.locks.Lock;

/**
 * Created by Eidan on 4/22/2015.
 */
public class RedisProvider implements LockProvider, KeyValueProvider {

    private final Redisson redisson;

    public RedisProvider(Redisson redisson) {
        this.redisson = redisson;
    }

    @Override
    public long getLong(String name) {
        return redisson.getAtomicLong(name).get();
    }

    @Override
    public void setLong(String name, long value) {
        redisson.getAtomicLong(name).set(value);
    }

    @Override
    public Lock getLock(String lockName) {
        return redisson.getLock(lockName);
    }
}
