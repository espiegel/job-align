package com.spiegel.jobalign;

import com.spiegel.jobalign.factory.KeyValueProvider;
import com.spiegel.jobalign.factory.LockProvider;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by eidan on 9/23/15.
 */
public abstract class AbstractSchedulerTest {
    protected final AtomicInteger count = new AtomicInteger();

    private final LockProvider lockProvider;
    private final KeyValueProvider keyValueProvider;

    public AbstractSchedulerTest(LockProvider lockProvider, KeyValueProvider keyValueProvider) {
        this.lockProvider = lockProvider;
        this.keyValueProvider = keyValueProvider;
    }

    public LockProvider getLockProvider() {
        return lockProvider;
    }

    public KeyValueProvider getKeyValueProvider() {
        return keyValueProvider;
    }

    /**
     *
     * @param seconds number of seconds
     * @return returns a cron expression thats 'seconds' seconds in the future
     */
    public String getFutureCron(int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, seconds);
        int second = calendar.get(Calendar.SECOND);
        return second+" * * * * ?";
    }
}
