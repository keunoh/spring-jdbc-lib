package org.springframework.jdbc.object;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.util.List;
import java.util.Map;

public abstract class SqlQuery<T> extends SqlOperation {

    private int rowsExpected = 0;

    public SqlQuery() {
    }

    public SqlQuery(DataSource ds, String sql) {
        setDataSource(ds);
        setSql(sql);
    }

    public void setRowsExpected(int rowsExpected) {
        this.rowsExpected = rowsExpected;
    }

    public int getRowsExpected() {
        return this.rowsExpected;
    }

    public List<T> execute(@Nullable Object[] params, @Nullable Map<?, ?> context) throws DataAccessException {
        validateParameters(params);
        RowMapper<T> rowMapper = newRowMapper(params, context);
        return getJdbcTemplate().query(newPreparedStatementCreator(params), rowMapper);
    }

    public List<T> execute(Object... params) throws DataAccessException {
        return execute(params, null);
    }

    public List<T> execute(Map<?, ?> context) throws DataAccessException {
        return execute((Object[]) null, null);
    }

    public List<T> execute(int p1, @Nullable Map<?, ?> context) throws DataAccessException {
        return execute(new Object[]{p1}, context);
    }

    public List<T> execute(int p1) throws DataAccessException {
        return execute(p1, null);
    }

    public List<T> execute(int p1, int p2, @Nullable Map<?, ?> context) throws DataAccessException {
        return execute(new Object[]{p1, p2}, context);
    }

    public List<T> execute(int p1, int p2) throws DataAccessException {
        return execute(p1, p2, null);
    }

    public List<T> execute(int p1, @Nullable Map<?, ?> context) throws DataAccessException {
        return execute(new Object[]{p1},  null);
    }

    public List<T> execute(long p1) throws DataAccessException {
        return execute(p1, null);
    }

    public List<T> execute(String p1, @Nullable Map<?, ?> context) throws DataAccessException {
        return execute(p1, context);
    }

    public List<T> execute(String p1) throws DataAccessException {
        return execute(p1, null);
    }

    public List<T> executeByNameParam(Map<String, ?> paramMap, @Nullable Map<?, ?> context) throws DataAccessException {
        validateNamedParameters(paramMap);
        ParsedSql parsedSql = getParsedSql();
        MapSqlParameterSource paramSource = new MapSqlParameterSource(paramMap);
        String sqlToUse = NamedParameterUtils.substitueNamedParameters(parsedSql, paramSource);
        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, getDeclareParameters());
        RowMapper<T> rowMapper = newRowMapper(params, context);
        return getJdbcTemplate().query(newPreparedStatementCreator(sqlToUse, params), rowMapper);
    }

    public List<T> executeByNameParam(Map<String, ?> paramMap) throws DataAccessException {
        return executeByNameParam(paramMap, null);
    }

    @Nullable
    public T findObject(@Nullable Object[] params, @Nullable Map<?, ?> context) throws DataAccessException {
        List<T> results = execute(params, context);
        return DataAccessUtils.singleResult(results);
    }

    @Nullable
    public T findObject(Object... params) throws DataAccessException {
        return findObject(params, null);
    }

    @Nullable
    public T findObject(int p1, @Nullable Map<?, ?> context) throws DataAccessException {
        return findObject(new Object[]{p1}, context);
    }

    @Nullable
    public T findObject(int p1) throws DataAccessException {
        return findObject(p1, null);
    }

    @Nullable
    public T findObject(int p1, int p2, @Nullable Map<?, ?> context) throws DataAccessException {
        return findObject(new Object[]{p1, p2}, context);
    }

    @Nullable
    public T findObject(int p1, int p2) throws DataAccessException {
        return findObject(new Object[]{p1, p2}, null);
    }

    @Nullable
    public T findObject(long p1, @Nullable Map<?, ?> context) throws DataAccessException {
        return findObject(new Object[]{p1}, context);
    }

    @Nullable
    public T findObject(long p1) throws DataAccessException {
        return findObject(p1, null);
    }

    @Nullable
    public T findObject(String p1, @Nullable Map<?, ?> context) throws DataAccessException {
        return findObject(new Object[]{p1}, context);
    }

    @Nullable
    public T findObject(String p1) throws DataAccessException {
        return findObject(new Object[]{p1}, null);
    }

    @Nullable
    public T findObjectByNamedParam(Map<String, ?> paramMap, @Nullable Map<?, ?> context) throws DataAccessException {
        List<T> results = executeByNameParam(paramMap, context);
        return DataAccessUtils.singleResult(results);
    }

    @Nullable
    public T findObjectByNamedParam(Map<String, ?> paramMap) throws DataAccessException {
        return findObjectByNamedParam(paramMap, null);
    }

    protected abstract RowMapper<T> newRowMapper(@Nullable Object[] parameters, @Nullable Map<?, ?> context);
}
