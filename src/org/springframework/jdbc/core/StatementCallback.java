package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.Nullable;

import java.sql.SQLException;
import java.sql.Statement;

@FunctionalInterface
public interface StatementCallback<T> {

    @Nullable
    T doInStatement(Statement stmt) throws SQLException, DataAccessException;
}
