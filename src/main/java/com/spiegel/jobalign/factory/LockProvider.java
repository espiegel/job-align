package com.spiegel.jobalign.factory;

import java.util.concurrent.locks.Lock;

/**
 * Created by Eidan on 4/21/2015.
 */
public interface LockProvider {
    Lock getLock(String lockName);
}
