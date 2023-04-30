package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.Nullable;

import java.sql.CallableStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface CallableStatementCallback<T> {

    @Nullable
    T doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException;
}
