package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetExtractor<T> {

    @Nullable
    T extractData(ResultSet rs) throws SQLException, DataAccessException;
}
