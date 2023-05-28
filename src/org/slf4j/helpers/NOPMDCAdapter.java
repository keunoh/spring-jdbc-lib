package org.slf4j.helpers;

import org.slf4j.spi.MDCAdapter;

import java.util.Map;

public class NOPMDCAdapter implements MDCAdapter {

    public void clear() {}

    @Override
    public void put(String key, String val) {
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public void remove(String key) {

    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        return null;
    }

    @Override
    public void setContextMap(Map<String, String> contextMap) {

    }
}
