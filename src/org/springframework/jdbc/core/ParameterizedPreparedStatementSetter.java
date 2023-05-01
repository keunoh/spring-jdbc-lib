package org.springframework.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface ParameterizedPreparedStatementSetter<T> {

    void setValues(PreparedStatement ps, T argument) throws SQLException;
}
