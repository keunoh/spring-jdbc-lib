package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionCallback<T> {

    @Nullable
    T doInConnection(Connection con) throws SQLException, DataAccessException;
}
