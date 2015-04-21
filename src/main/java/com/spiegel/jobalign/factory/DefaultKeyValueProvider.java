package com.spiegel.jobalign.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Eidan on 4/22/2015.
 */
public class DefaultKeyValueProvider implements KeyValueProvider {
    private Map<String, Long> longMap = new ConcurrentHashMap<>();

    @Override
    public long getLong(String name) {
        if(!longMap.containsKey(name)) {
            longMap.put(name, 0l);
        }
        return longMap.get(name);
    }

    @Override
    public void setLong(String name, long value) {
        longMap.put(name, value);
    }
}
