package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Deprecated
public abstract class BatchUpdateUtils {

    public static int[] executeBatchUpdate(
            String sql, final List<Object[]> batchArgs, final int[] columnTypes, JdbcOperations jdbcOperations) {

        if (batchArgs.isEmpty()) {
            return new int[0];
        }

        return jdbcOperations.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Object[] values = batchArgs.get(i);
                        setStatementParameters(values, ps, columnTypes);
                    }
                    @Override
                    public int getBatchSize() {
                        return batchArgs.size();
                    }
                });
    }

    protected static void setStatementParameters(Object[] values, PreparedStatement ps, @Nullable int[] columnTypes)
            throws SQLException {

        int colIndex = 0;
        for (Object value : values) {
            colIndex++;
            if (value instanceof SqlParameterValue) {
                SqlParameterValue paramValue = (SqlParameterValue) value;
                StatementCreatorUtils.setParameterValue(ps, colIndex, paramValue, paramValue.getValue());
            }
            else {
                int colType;
                if (columnTypes == null || columnTypes.length < colIndex) {
                    colType = SqlTypeValue.TYPE_UNKNOWN;
                }
                else {
                    colType = columnTypes[colIndex - 1];
                }
                StatementCreatorUtils.setParameterValue(ps, colIndex, colType, value);
            }
        }
    }
}
