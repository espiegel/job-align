package com.spiegel.jobalign.factory;

/**
 * Created by Eidan on 4/21/2015.
 */
public interface KeyValueProvider {
    long getLong(String name);
    void setLong(String name, long value);
}
