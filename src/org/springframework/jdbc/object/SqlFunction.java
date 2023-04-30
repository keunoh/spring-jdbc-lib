package org.springframework.jdbc.object;

import org.springframework.jdbc.myannotation.Nullable;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlFunction<T> extends MappingSqlQuery<T> {

    private final SingleColumnRowMapper<T> rowMapper = new SingleColumnRowMapper<>();

    public SqlFunction() {
        setRowsExpected(1);
    }

    public SqlFunction(DataSource ds, String sql) {
        setRowsExpected(1);
        setDataSource(ds);
        setSql(sql);
    }

    public SqlFunction(DataSource ds, String sql, int[] types) {
        setRowsExpected(1);
        setDataSource(ds);
        setSql(sql);
        setTypes(types);
    }

    public SqlFunction(DataSource ds, String sql, int[] types, Class<T> resultType) {
        setRowsExpected(1);
        setDataSource(ds);
        setSql(sql);
        setTypes(types);
        setResultType(resultType);
    }

    public void setResultType(Class<T> resultType) {
        this.rowMapper.setRequiredType(resultType);
    }

    @Nullable
    @Override
    protected T mapRow(ResultSet rs, int rowNum) throws SQLException {
        return this.rowMapper.mapRow(rs, rowNum);
    }

    public int run() {
        return run(new Object[0]);
    }

    /**
     * Convenient method to run the function with a single in argument.
     */
    public int run(int parameter) {
        return run(new Object[] {parameter});
    }

    public int run(Object... parameters) {
        Object obj = super.findObject(parameters);
        if (!(obj instanceof Number)) {
            throw new TypeMisMatchDataAccessException("Could not convert result object [" + obj + "] to int");
        }
        return ((Number) obj).intValue();
    }

    @Nullable
    public Object runGeneric() {
        return findObject((Object[]) null, null);
    }

    @Nullable
    public Object runGeneric(int parameter) {
        return findObject(parameter);
    }

    @Nullable
    public Object runGeneric(Object[] parameters) {
        return findObject(parameters);
    }
}
