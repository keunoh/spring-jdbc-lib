package org.springframework.jdbc.object;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.util.Map;

/**
 * Reusable operation object representing an SQL update.
 * This class provides a number of update methods, analogous to rhe execute methods of query objects.
 * This clas is concrete. Although it can be subclassed (for example to add a custom update method) it can
 * easily be parameterized by setting SQ and declaring parameters.
 * Like all RdbmsOperation classes that ship with the Spring framework, SqlQuery instances are thread-safe
 * after their initialization is complete. That is after they are constructed and configured their setter
 * methods, that can be used safely form multiple thread.
 */
public class SqlUpdate extends SqlOperation {

    /**
     * Maximum number of rows the update may affect. If more are affected, an exception will be thrown.
     */
    private int maxRowsAffected = 0;

    /**
     * An exact number of rows that must be affected. Ignored if 0.
     */
    private int requiredRowsAffected = 0;

    /**
     * Constructor to allow use as a JavaBean. DataSource and SQL must be supplied before compilation
     * and use.
     */
    public SqlUpdate() {

    }

    /**
     * Construct an update object with a given DatSource, SQLl nad anonymous parameters.
     * Prams: ds - the DataSource to use to obtain connections
     *         sql - the SQL statement to execute
     *         types - the SQL types of the parameters, as defined in the java.sql.Types class
     */
    public SqlUpdate(DataSource ds, String sql, int[] types) {
        setDataSource(ds);
        setSql(sql);
        setTypes(types);
    }

    /**
     * Construct an update object with a given DataSource, SQL, anonymous parameters and specifying the
     * maximum number of rows that may be affected.
     * Params: ds - the DataSource to use to obtain connections
     * sql - the SQL statement to execute
     * types - the SQL types of the parameters, as defined in the java.sql.Types class
     * maxRowsAffected - the maximum number of rows that may be affected by the update
     */
    public SqlUpdate(DataSource ds, String sql, int[] types, int maxRowsAffected) {
        setDataSource(ds);
        setSql(sql);
        setTypes(types);
        this.maxRowsAffected = maxRowsAffected;
    }

    /**
     * Set the maximum number of rows that may be affectd by this update. The default value is 0. which
     * does not limit the number of rows affected.
     * Params: maxRowsAffected - the maximum number of rows that can be affected by this update
     * without this class's update method considering it an error
     */
    public void setMaxRowsAffected(int maxRowsAffected) { this.maxRowsAffected = maxRowsAffected; }

    /**
     * Set the exact number of rows that must be affected by this update. The default value is 0, which
     * allows any number of rows to be affected.
     * This is an alternative to setting the maximum number of rows that must be affected.
     * Params: requiredRowsAffected - the exact number of rows that must be affected by this update
     * without this class's update method considering it an error
     */
    public void setRequiredRowsAffected(int requiredRowsAffected) { this.requiredRowsAffected = requiredRowsAffected; }

    /**
     * Check the given number of affected row against the specified maximum number or required
     * number.
     * Params: rowsAffected - the number of affected rows
     */
    protected void checkRowsAffected(int rowsAffected) throws JdbcUpdateAffectedIncorrectNumberOfRowsException {
        if (this.maxRowsAffected > 0 && rowsAffected > this.maxRowsAffected) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(resolveSql(), this.maxRowsAffected, rowsAffected);
        }
        if (this.requiredRowsAffected > 0 && rowsAffected != this.requiredRowsAffected) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(resolveSql(), this.requiredRowsAffected, rowsAffected);
        }
    }

    /**
     * Generic method to execute the update given parameters. All other update methods invoke this
     * method.
     * Params: params- array of parameters objects
     * Returns: the number of rows affected by the update
     */
    public int update(Object... params) throws DataAccessException {
        validateParamters(params);
        int rowsAffected = getJdbcTemplate().update(newPreparedStatementCreator(params));
        checkRowsAffected(rowsAffected);
        return rowsAffected;
    }

    /**
     * Method to execute the update iven arguments and retrieve the generated keys using a KeyHolder.
     * Params: params -array of parameter objects
     *         generatedKeyHolder - the KeyHolder that will hold the generated keys
     * Returns: the number of rows affected by the update
     */
    public int update(Object[] params, KeyHolder generatedKeyHolder) throws DataAccessException {
        if (!isReturnGeneratedKeys() && getGeneratedKeysColumnNames() == null) {
            throw new InvalidDataAccesApiUsageExcetpion(
                    "The update method taking a keyHOlder should only be used when generated keys have " +
                    "been configured by calling either 'setReturnGeneratedKeys' or " +
                    "'setGeneratedKeysColumnsNames'.");
        }
        validateParamters(params);
        int rowsAffected = getJdbcTemplate().update(newPreparedStatementCreator(params), generatedKeyHolder);
        checkRowsAffected(rowsAffected);
        return rowsAffected;
    }

    /**
     * Convenience method to execute an update with no parameters.
     */
    public int update() throws DataAccessException {
        return update(new Object[0]);
    }

    /**
     * Convenience method to execute an update given one int arg.
     */
    public int update(int p1) throws DataAccessException {
        return update(new Object[] {p1});
    }

    /**
     * Convenience method to execute an update given two in arg.
     */
    public int update(int p1, int p2) throws DataAccessException {
        return update(new Object[]{p1, p2});
    }

    /**
     * Convenience method to execute an update given one long arg.
     */
    public int update(long p1) throws DataAccessException {
        return update(new Object[] {p1});
    }

    /**
     * Convenience method to execute an update given one String arg.
     */
    public int update(String p) throws DataAccessException {
        return update(new Object[]{p});
    }

    /**
     *  Convenience method to execute an update given two String args.
     */
    public int update(String p1, String p2) throws DataAccessException {
        return update(new Object[] {p1, p2});
    }

    /**
     * Generic method to execute the update given named parameters. All other update methods invoke
     * this method.
     * Params: paraMap - a Map of parameter name to parameter object, matching named parameters
     * specified in the SQL statement
     * Returns: the number of rows affected by the update
     */
    public int updateByNameParam(Map<String, ?> paramMap) throws DataAccessException {
        validateParamters(params);
        ParseSql parseSql = getParseSql();
        MapSqlParameterSource paramSource = new MapSqlParameterSource(paramMap);
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parseSql, paramSource);
        Object[] params = NamedParameterUtils.buildValueArray(parseSql, paramSource, getDeclaredParameters());
        int rowsAffected = getJdbcTemplate().update(newPreparedStatementCreator(sqlToUse, params));
        checkRowsAffected(rowsAffected);
        return rowsAffected;
    }

    /**
     * Method to execute the update given arguments and retrieve the generated keys using a keyHOlder.
     * Params: paramMap -a Map of parameter name to parameter object, matching named parameters
     *         specified in the SQL statement
     *         generatedKeyHolder - the KeHolder that will old the generated keys
     * Returns: the number of rows affected by the update
     */
    public int updateByNameParam(Map<String, ?> paramMap, KeyHolder generatedKeyHolder) throws DataAccessException {
        validateParamters(params);
        ParseSql parseSql = getParseSql();
        MapSqlParameterSource paramSource = new MapSqlParameterSource(paramMap);
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parseSql, paramSource);
        Object[] params = NamedParameterUtils.buildValueArray(parseSql, paramSource, getDeclaredParameters());
        int rowsAffected = getJdbcTemplate().update(newPreparedStatementCreator(sqlToUse, params), generatedKeyHolder);
        checkRowsAffected(rowsAffected);
        return rowsAffected;
    }

}
