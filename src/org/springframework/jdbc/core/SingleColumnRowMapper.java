package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.Nullable;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SingleColumnRowMapper<T> implements RowMapper<T> {

    @Nullable
    private Class<?> requiredType;

    @Nullable
    private ConversionService conversionService = DefaultConversionService.getSharedInstance();

    public SingleColumnRowMapper() {

    }

    public SingleColumnRowMapper(Class<?> requiredType) {
        this.requiredType = requiredType;
    }

    public void setRequiredType(Class<?> requiredType) {
        this.requiredType = ClassUtils.resolvePrimitiveIfNecessary(requiredType);
    }

    public void setConversionService(@Nullable ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Validate column count.
        ResultSetMetaData rsmd = rs.getMetaData();
        int nrOfColumns = rsmd.getColumnCount();
        if (nrOfColumns != 1) {
            throw new IncorrectResultSetColmunCountException(1, nrOfColumns);
        }

        // Extract column value from JDBC ResultSet.
        Object result = getColumnValue(rs, 1, this.requiredType);
        if (result != null && this.requiredType != null && !this.requiredType.isInstance(result)) {
            //Extracted value does not match already: try to convert it.
            try {
                return (T) convertValueToRequiredType(result, this.requiredType);
            }
            catch (IllegalArgumentException ex) {
                throw new TypeMismatchDataAccessException(
                        "Type mismatch affecting row number " + rowNum + " and column type '" +
                        rsmd.getColumnTypeName(1) + "': " + ex.getMessage());
            }
        }
        return (T) result;
    }

    @Nullable
    private Object getColumnValue(ResultSet rs, int index, Class<?> requiredType) throws SQLException {
        if (requiredType != null) {
            return JdbcUtils.getResultSetValue(rs, index, requiredType);
        }
        else {
           // No required type specified -> perform default extraction.
            return getColumnValue(rs, index);
        }
    }

    @Nullable
    private Object getColumnValue(ResultSet rs, int index) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private Object convertValueToRequiredType(Object value, Class<?> requiredType) {
        if (String.class == requiredType) {
            return value.toString();
        }
        else if (Number.class.isAssignableFrom(requiredType)) {
            if (value instanceof Number) {
                // Convert original Number to target Number class.
                return NumberUtils.convertNumberToTargetClass(((Number) value), (Class<Number>) requiredType);
            }
            else {
                // Convert stringified value to target Number class.
                return NumberUtils.parseNumber(value.toString(), (Class<Number>) requiredType);
            }
        }
        else if (this.conversionService != null && this.conversionService.canConvert(value.getClass(), requiredType)) {
            return this.conversionService.convert(value, requiredType);
        }
        else {
            throw new IllegalArgumentException(
                    "Value [" + value + "] is of type [" + value.getClass().getName() +
                    "] and cannot be converted to required type [" + requiredType.getName() + "]");
        }
    }

    public static <T> SingleColumnRowMapper<T> newInstance(Class<T> requiredType) {
        return new SingleColumnRowMapper<>(requiredType);
    }

    public static <T> SingleColumnRowMapper<T> newInstance(
            Class<T> requiredType, @Nullable ConversionService conversionService) {

        SingleColumnRowMapper<T> rowMapper = newInstance(requiredType);
        rowMapper.setConversionService(conversionService);
        return rowMapper;
    }
}
