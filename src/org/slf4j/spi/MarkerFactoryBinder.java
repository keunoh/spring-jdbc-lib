package org.slf4j.spi;

public interface MarkerFactoryBinder {
    public IMarkerFactory getMarkerFactory();

    public String getMarkerFactoryClassStr();
}
