package org.springframework.jdbc.core;

public interface DisposableSqlTypeValue extends SqlTypeValue {

    void cleanup();
}
