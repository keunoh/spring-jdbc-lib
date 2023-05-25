package org.slf4j.spi;

public interface LoggerFactoryBinder {
    public ILoggerFactory getLoggerFactory();

    public String getLoggerFactoryClassStr();
}
