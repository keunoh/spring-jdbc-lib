package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SqlTypeValue {

    int TYPE_UNKNOWN = JdbcUtils.TYPE_UNKNOWN;

    void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType, @Nullable String typeName)
            throws SQLException;
}
