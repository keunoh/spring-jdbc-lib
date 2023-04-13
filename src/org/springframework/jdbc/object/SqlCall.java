package org.springframework.jdbc.object;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public abstract class SqlCall extends RdbmsOperation {

    private boolean function = false;

    private boolean sqlReadyForUse = false;

    @Nullable
    private String callString;

    @Nullable
    private CallableStatementCreatorFactory callableStatementFactory;

    public SqlCall() {

    }

    public SqlCall(DataSource ds, String sql) {
        setDataSource(ds);
        setSql(sql);
    }

    public void setFunction(boolean function) { this.function = function; }

    public boolean isFunction() { return this.function; }

    public void setSqlReadyForUse(boolean sqlReadyForUse) { this.sqlReadyForUse = sqlReadyForUse; }

    public boolean isSqlReadyForUse() { return this.sqlReadyForUse; }

    @Override
    protected final void compileInternal() {
        if (isSqlReadyForUse()) {
            this.callString = resolveSql();
        }
        else {
            StringBuilder callString = new StringBuilder(32);
            List<SqlParameter> parameters = getDeclareParameters();
            int parameterCount = 0;
            if (isFunction()) {
                callString.append("{? = call ").append(resolveSql()).append('(');
                parameterCount = -1;
            }
            else {
                callString.append("{call ").append(resolveSql()).append('(');
            }
            for (SqlParameter parameter : parameters) {
                if (!parameter.isResultsParameter()) {
                    if (parameterCount > 0) {
                        callString.append(", ");
                    }
                    if (parameterCount >= 0) {
                        callString.append('?');
                    }
                    parameterCount++;
                }
            }
            callString.append(")}");
            this.callString = callString.toString();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Compiled stored procedure. Call string is [" + this.callString + "]");
        }

        this.callableStatementFactory = new CallableStatementCreatorFactory(this.callString, getDeclareParameters());
        this.callableStatementFactory.setResultSetType(getResultSetType());
        this.callableStatementFactory.setUpdatableResults(isUpdatableResults());

        onCompileInternal();
    }

    protected void onCompileInternal() {

    }

    @Nullable
    public String getCallString() { return this.callString; }

    protected CallableStatementCreator newCallableStatementCreator(@Nullable Map<String, ?> inParams) {
        Assert.state(this.callableStatementFactory != null, "No CallableStatementFactory available");
        return this.callableStatementFactory.newCallableStatementCreator(inParams);
    }

    protected CallableStatementCreator newCallableStatementCreator(ParameterMapper inParamMapper) {
        Assert.state(this.callableStatementFactory != null, "No CallableStatementFactory available");
        return this.callableStatementFactory.newCallableStatementCreator(inParamMapper);
    }

    private class CallableStatementCreator {
    }
}

















