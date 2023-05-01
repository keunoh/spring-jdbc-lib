package org.springframework.jdbc.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@FunctionalInterface
public interface ParameterMapper {

    Map<String, ?> createMap(Connection con) throws SQLException;
}
