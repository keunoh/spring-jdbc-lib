package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface JdbcOperations {

    @Nullable
    <T> T execute(ConnectionCallback<T> action) throws DataAccessException;

    @Nullable
    <T> T execute(StatementCallback<T> action) throws DataAccessException;

    void execute(String sql) throws DataAccessException;

    @Nullable
    <T> T query(String sql, ResultSetExtractor<T> rse) throws DataAccessException;


    void query(String sql, RowCallbackHandler rch) throws DataAccessException;

    <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException;

    <T> Stream<T> queryForStream(String sql, RowMapper<T> rowMapper)  throws DataAccessException;

    @Nullable
    <T> T queryForObject(String sql, Class<T> requiredType) throws DataAccessException;

    Map<String, Object> queryForMap(String sql) throws DataAccessException;

    <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException;

    List<Map<String, Object>> queryForList(String sql) throws DataAccessException;

    SqlRowSet queryForRowSet(String sql) throws DataAccessException;

    int update(String sql) throws DataAccessException;

    int[] batchUpdate(String... sql) throws DataAccessException;

    @Nullable
    <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException;

    @Nullable
    <T> T query(PreparedStatementCreator psc, ResultSetExtractor<T> rse) throws DataAccessException;

    @Nullable
    <T> T query(String sql, @Nullable PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException;

    @Nullable
    <T> T query(String sql, Object[] args, int[] argTypes, ResultSetExtractor<T> rse) throws DataAccessException;

    @Deprecated
    @Nullable
    <T> T query(String sql, @Nullable Object[] args, ResultSetExtractor<T> rse) throws DataAccessException;

    @Nullable
    <T> T query(String sql, ResultSetExtractor<T> rse, @Nullable Object... args) throws DataAccessException;

    void query(PreparedStatementCreator psc, RowCallbackHandler rch) throws DataAccessException;

    void query(String sql, @Nullable PreparedStatementSetter pss, RowCallbackHandler rch) throws DataAccessException;

    void query(String sql, Object[] args, int[] argTypes, RowCallbackHandler rch) throws DataAccessException;

    @Deprecated
    void query(String sql, @Nullable Object[] args, RowCallbackHandler rch) throws DataAccessException;

    void query(String sql, RowCallbackHandler rch, @Nullable Object... args) throws DataAccessException;

    <T> List<T> query(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException;

    <T> List<T> query(String sql, @Nullable PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException;

    <T> List<T> query(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException;

    @Deprecated
    <T> List<T> query(String sql, @Nullable Object[] args, RowMapper<T> rowMapper) throws DataAccessException;

    <T> List<T> query(String sql, RowMapper<T> rowMapper, @Nullable Object... args) throws DataAccessException;

    <T> Stream<T> queryForStream(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException;

    <T> Stream<T> queryForStream(String sql, @Nullable PreparedStatementSetter pss, RowMapper<T> rowMapper)
            throws DataAccessException;

    <T> Stream<T> queryForStream(String sql, RowMapper<T> rowMapper, @Nullable Object... args)
            throws DataAccessException;

    @Nullable
    <T> T queryForObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper)
            throws DataAccessException;

    @Deprecated
    @Nullable
    <T> T queryForObject(String sql, @Nullable Object[] args, RowMapper<T> rowMapper) throws DataAccessException;

    @Nullable
    <T> T queryForObject(String sql, RowMapper<T> rowMapper, @Nullable Object... args) throws DataAccessException;

    @Nullable
    <T> T queryForObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType)
            throws DataAccessException;

    @Deprecated
    @Nullable
    <T> T queryForObject(String sql, @Nullable Object[] args, Class<T> requiredType) throws DataAccessException;

    @Nullable
    <T> T queryForObject(String sql, Class<T> requiredType, @Nullable Object... args) throws DataAccessException;

    Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes) throws DataAccessException;

    Map<String, Object> queryForMap(String sql, @Nullable Object... args) throws DataAccessException;

    <T> List<T> queryForList(String sql, Object[] args, int[] argTypes, Class<T> elementType)
            throws DataAccessException;

    @Deprecated
    <T> List<T> queryForList(String sql, @Nullable Object[] args, Class<T> elementType) throws DataAccessException;

    <T> List<T> queryForList(String sql, Class<T> elementType, @Nullable Object... args) throws DataAccessException;

    List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes) throws DataAccessException;

    List<Map<String, Object>> queryForList(String sql, @Nullable Object... args) throws DataAccessException;

    SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes) throws DataAccessException;

    SqlRowSet queryForRowSet(String sql, @Nullable Object... args) throws DataAccessException;

    int update(PreparedStatementCreator psc) throws DataAccessException;

    int update(PreparedStatementCreator psc, KeyHolder generatedKeyHolder) throws DataAccessException;

    int update(String sql, @Nullable PreparedStatementSetter pss) throws DataAccessException;

    int update(String sql, Object[] args, int[] argTypes) throws DataAccessException;

    int update(String sql, @Nullable Object... args) throws DataAccessException;

    int[] batchUpdate(String sql, BatchPreparedStatementSetter pss) throws DataAccessException;

    int[] batchUpdate(String sql, List<Object[]> batchArgs) throws DataAccessException;

    int[] batchUpdate(String sql, List<Object[]> batchArgs, int[] argTypes) throws DataAccessException;

    <T> int[][] batchUpdate(String sql, Collection<T> batchArgs, int batchSize,
                            ParameterizedPreparedStatementSetter<T> pss) throws DataAccessException;

    @Nullable
    <T> T execute(CallableStatementCreator csc, CallableStatementCallback<T> action) throws DataAccessException;

    @Nullable
    <T> T execute(String callString, CallableStatementCallback<T> action) throws DataAccessException;

    Map<String, Object> call(CallableStatementCreator csc, List<SqlParameter> declaredParameters)
            throws DataAccessException;
}
