package com.spiegel.jobalign.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Eidan on 4/22/2015.
 */
public class DefaultLockProvider implements LockProvider {
    private Map<String, Lock> locks = new HashMap<>();

    @Override
    public Lock getLock(String lockName) {
        synchronized(this) {
            if(!locks.containsKey(lockName)) {
                locks.put(lockName, new ReentrantLock(false));
            }
            return locks.get(lockName);
        }
    }
}
