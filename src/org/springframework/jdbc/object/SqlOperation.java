package org.springframework.jdbc.object;


public abstract class SqlOperation extends RdbmsOperation {

    @Nullable
    private PreparedStatementCreatorFactory preparedStatementFactory;

    @Nullable
    private ParseSql cachedSql;

    private final Object parsedSqlMonitor = new Object();

    @Override
    protected final void compileInternal() {
        this.preparedStatementFactory = new PreparedStatementCreatorFactory(resolveSql(), getDeclareParameters());
        this.preparedStatementFactory.setResultSetType(getResultSetType());
        this.preparedStatementFactory.setUpdatableResults(isUpdatableResults());
        this.preparedStatementFactory.setReturnGeneratedKeys(isReturnGeneratedKeys());
        if (getGeneratedKeysColumnNames() != null) {
            this.preparedStatementFactory.setGeneratedKeysColumnNames(getGeneratedKeysColumnNames());
        }

        onCompileInternal();
    }

    protected void onCompileInternal() {

    }

    protected ParsedSql getParsedSql() {
        synchronized (this.parsedSqlMonitor) {
            if (this.cachedSql == null) {
                this.cachedSql = NamedParameterUtils.parseSqlStatement(resolveSql());
            }
            return this.cachedSql;
        }
    }

    protected final PreparedStatementSetter newPreparedStatementSetter(@Nullable Object[] params) {
        Assert.state(this.preparedStatementFactory != null, "No PreparedStatementFactory available");
        return this.preparedStatementFactory.newPreparedStatementSetter(params);
    }

    protected final PreparedStatementCreator newPreparedStatementCreator(@Nullable Object[] params) {
        Assert.state(this.preparedStatementFactory != null, "No PreparedStatementFactory available");
        return this.preparedStatementFactory.newPreparedStatementCreator(params);
    }

    protected final PreparedStatementCreator newPreparedStatementCreator(String sqlToUse, @Nullable Object[] params) {
        Assert.state(this.preparedStatementFactory != null, "No PreparedStatementFactory available");
        return this.preparedStatementFactory.newPreparedStatementCreator(sqlToUse, params);
    }
}
