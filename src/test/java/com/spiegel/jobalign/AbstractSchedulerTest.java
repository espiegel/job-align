package com.spiegel.jobalign;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spiegel.jobalign.factory.KeyValueProvider;
import com.spiegel.jobalign.factory.LockProvider;

/**
 * Created by eidan on 9/23/15.
 */
public abstract class AbstractSchedulerTest {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
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
        int second = getFutureSecond(seconds);
        return second + " * * * * ?";
    }

    private int getFutureSecond(int seconds) {
        LocalTime now = LocalTime.now();
        LocalTime future = now.plusSeconds(seconds);
        
        return future.getSecond();
    }
    
    public String getRecurringFutureCron(Integer... seconds) {
        if (seconds != null && seconds.length > 0) {

            String secondsString = Stream.of(seconds)
                    .sorted()
                    .map(this::getFutureSecond)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            
            return secondsString + " * * * * ?";
        } else {
            return "0 * * * * ?";
        }
    }

    void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch(InterruptedException e) {
            LOGGER.warn("Error in sleep", e);
        }
    }
}
