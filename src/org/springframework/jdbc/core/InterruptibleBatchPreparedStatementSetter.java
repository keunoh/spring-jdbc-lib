package org.springframework.jdbc.core;

public interface InterruptibleBatchPreparedStatementSetter extends BatchPreparedStatementSetter {

    boolean isBatchExhausted(int i);
}
