package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.DataAccessException;
import org.springframework.jdbc.myannotation.Nullable;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcTemplate extends JdbcAccessor implements JdbcOperations {

    private static final String RETURN_RESULT_SET_PREFIX = "#result-set-";

    private static final String RETURN_UPDATE_COUNT_PREFIX = "#update-count-";

    private boolean ignoreWarnings = true;

    private int fetchSize = -1;

    private int maxRows = -1;

    private int queryTimeout = -1;

    private boolean skipResultsProcessing = false;

    private boolean skipUndeclaredResults = false;

    private boolean resultsMapCaseInsensitive = false;

    public JdbcTemplate() {
    }

    public JdbcTemplate(DataSource dataSource) {
        setDataSource(dataSource);
        afterPropertiesSet();
    }

    public JdbcTemplate(DataSource dataSource, boolean lazyInit) {
        setDataSource(dataSource);
        setLaztInit(lazyInit);
        afterPropertiesSet();
    }

    public void setIgnoreWarnings(boolean ignoreWarnings) {
        this.ignoreWarnings = ignoreWarnings;
    }

    public boolean isIgnoreWarnings() {
        return this.ignoreWarnings;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public int getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public boolean isSkipResultsProcessing() {
        return skipResultsProcessing;
    }

    public void setSkipResultsProcessing(boolean skipResultsProcessing) {
        this.skipResultsProcessing = skipResultsProcessing;
    }

    public boolean isSkipUndeclaredResults() {
        return skipUndeclaredResults;
    }

    public void setSkipUndeclaredResults(boolean skipUndeclaredResults) {
        this.skipUndeclaredResults = skipUndeclaredResults;
    }

    public boolean isResultsMapCaseInsensitive() {
        return resultsMapCaseInsensitive;
    }

    public void setResultsMapCaseInsensitive(boolean resultsMapCaseInsensitive) {
        this.resultsMapCaseInsensitive = resultsMapCaseInsensitive;
    }

    //-----------------------------------------------------------------
    // Methods dealing with a plain java.sql.Connection
    //-----------------------------------------------------------------

    @Override
    @Nullable
    public <T> T execute(ConnectionCallback<T> action) throws DataAccessException {
        Assert.notNull(action, "Callback object must not be null");

        Connection con = DataSourceUtils.getConnection(obtainDataSource());
        try {
            // Create close-suppressing Connection proxy, also preparing returned Statements.
            Connection conToUse = createConnectionProxy(con);
            return action.doInConnection(conToUse);
        }
        catch (SQLException ex) {
            // Release Connection early, to avoid potential connection pool deadlock
            // in the case when the exception translator hasn't been initialized yet.
            String sql = getSql(action);
            DataSourceUtils.releaseConnection(con, getDataSource());
            con = null;
            throw translateException("ConnectionCallback", sql, ex);
        }
        finally {
            DataSourceUtils.releaseConnection(con, getDataSource());
        }
    }

    protected Connection createConnectionProxy(Connection con) {
        return (Connection) Proxy.newProxyInstance(
                ConnectionProxy.class.getClassLoader(),
                new Class<?>[]{ConnectionProxy.class},
                new CloseSuppressingInvocationHandler(con));
    }

    //-----------------------------------------------------------------
    // Methods dealing with static SQL (java.sql.Statement)
    //-----------------------------------------------------------------

    @Nullable
    private <T> T execute(StatementCallback<T> action, boolean closeResources) throws DataAccessException {
        Assert.notNull(action, "Callback object must not be null");

        Connection con = DataSourceUtils.getConnection(obtainDataSource());
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            applyStatementSettings(stmt);
            T result = action.doInStatement(stmt);
            handleWarnings(stmt);
            return result;
        }
        catch (SQLException ex) {
            // Release Connection early, to avoid potential connection pool deadlock
            // in the case when the exception translator hasn't been initialized yet.
            String sql = getSql(action);
            JdbcUtils.closeStatement(stmt);
            stmt = null;
            DataSourceUtils.releaseConnection(con, getDataSource());
            con = null;
            throw translateException("ConnectionCallback", sql, ex);
        }
        finally {
            if (closeResources) {
                JdbcUtils.closeStatement(stmt);
                DataSourceUtils.releaseConnection(con, getDataSource());
            }
        }
    }

    @Override
    @Nullable
    public <T> T execute(StatementCallback<T> action) throws DataAccessException {
        return execute(action, true);
    }

    @Override
    public void execute(final String sql) throws DataAccessException {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL statement [" + sql + "]");
        }

        /**
         * Callback to execute the statement.
         */
        class ExecuteStatementCallback implements StatementCallback<Object>, SqlProvider {
            @Override
            @Nullable
            public Object doInStatement(Statement stmt) throws SQLException {
                stmt.execute(sql);
                return null;
            }
            @Override
            public String getSql() {
                return sql;
            }
        }

        execute(new ExecuteStatementCallback(), true);
    }

    @Override
    @Nullable
    public <T> T query(final String sql, final ResultSetExtractor<T> rse) throws DataAccessException {
        Assert.notNull(sql, "SQL must not be null");
        Assert.notNull(rse, "ResultsSetExtractor must not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL query [" + sql + "]");
        }

        /**
         * Callback to execute the query.
         */
        class QueryStatementCallback implements StatementCallback<T>, SqlProvider {
            @Override
            @Nullable
            public T doInStatement(Statement stmt) throws SQLException {
                ResultSet rs = null;
                try {
                    rs = stmt.executeQuery(sql);
                    return rse.extractData(rs);
                }
                finally {
                    JdbcUtils.closeResultSet(rs);
                }
            }
            @Override
            public String getSql() {
                return sql;
            }
        }

        return execute(new QueryStatementCallback(), true);
    }

    @Override
    public void query(String sql, RowCallbackHandler rch) throws DataAccessException {
        query(sql, new RowCallbackHandlerResultSetExtractor(rch));
    }
}
