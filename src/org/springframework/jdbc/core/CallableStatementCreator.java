package org.springframework.jdbc.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface CallableStatementCreator {
    CallableStatement createCallableStatement(Connection con) throws SQLException;
}
