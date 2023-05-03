package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.Nullable;

import java.sql.CallableStatement;
import java.sql.SQLException;

public interface SqlReturnType {

    int TYPE_UNKNOWN = Integer.MIN_VALUE;

    Object getTypeValue(CallableStatement cs, int paramIndex, int sqlType, @Nullable String typeName)
            throws SQLException;
}
