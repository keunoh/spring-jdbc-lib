package org.springframework.jdbc.object;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.myannotation.Nullable;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.*;

public abstract class RdbmsOperation implements InitializingBean {

    protected final Log logger = LogFactory.getLog(getClass());

    private JdbcTemplate jdbcTemplate = new JdbcTempalte();

    private int resultSetType = ResultSet.TYPE_FORWARD_ONLY;

    private boolean updatableResults = false;

    private boolean returnGeneratedKeys = false;

    @Nullable
    private String[] generatedKeysColumnNames;

    @Nullable
    private String sql;

    private final List<SqlParameter> declareParameters = new ArrayList<>();

    private volatile boolean compiled;

    private void setJdbcTemplate(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public JdbcTemplate getJdbcTemplate() { return this.jdbcTemplate; }

    public void setDataSource(DataSource dataSource) { this.jdbcTemplate.setDataSource(dataSource); }

    public void setFetchSize(int fetchSize) { this.jdbcTemplate.setFetchSize(fetchSize); }

    public void setMaxRows(int maxRows) { this.jdbcTemplate.setMaxRows(maxRows); }

    public void setQueryTimeout(int queryTimeout) { this.jdbcTemplate.setQueryTimeout(queryTimeout); }

    public void setResultSetType(int resultSetType) { this.resultSetType = resultSetType; }

    public int getResultSetType() { return this.resultSetType; }

    public void setUpdatableResults(boolean updatableResults) {
        if (isCompiled()) {
            throw new InvalidDataAccessApiUsageException(
                    "The updatableResults flag must be set before the operation is compiled");
            )
            this.updatableResults = updatableResults;
        }
    }

    public boolean isUpdatableResults() { return this.updatableResults; }

    public void setReturnGeneratedKeys(boolean returnGeneratedKeys) {
        if (isCompiled()) {
            throw new InvalidDataAccessApiUsageException(
                    "The returnGeneratedKeys flag must be set before the operation is compiled");
            )
            this.returnGeneratedKeys = returnGeneratedKeys;
        }
    }

    public boolean isReturnGeneratedKeys() { return this.returnGeneratedKeys; }

    public void setGeneratedKeysColumnNames(@Nullable String... names) {
        if (isCompiled()) {
            throw new InvalidDataAccessApiUsageException(
                    "The column names for the generated keys must be set before the operation is compiled");
            )
            this.generatedKeysColumnNames = generatedKeysColumnNames;
        }
    }

    private boolean isCompiled() {
        return true;
    }

    @Nullable
    public String[] getGeneratedKeysColumnNames() { return this.generatedKeysColumnNames; }

    public void setSql(@Nullable String sql) { this.sql = sql; }

    @Nullable
    public String getSql() { return this.sql; }

    protected String resolveSql() {
        String sql = getSql();
        Assert.state(sql != null, "No SQL set");
        return sql;
    }

    public void setTypes(int[] types) throws InvalidDataAccessApiUsageException {
        if (isCompiled()) {
            throw new InvalidDataAccessApiUsageException("Cannot add parameters once query is compiled");
        }
        if (types != null) {
            for (int type : types) {
                declareParameter(new SqlParameter(type));
            }
        }
    }

    public void declareParameter(SqlParameter param) throws InvalidDataAccessApiUsageException {
        if (isCompiled()) {
            throw new InvalidDataAccessApiUsageException("Cannot add parameters once the query is compiled");
        }
        this.declareParameters.add(param);
    }

    public void setParameters(SqlParameter... parameters) {
        if (isCompiled()) {
            throw new InvalidDataAccessApiUsageException("Cannot add parameters once the query is compiled");
        }
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] != null) {
                this.declareParameters.add(parameters[i]);
            }
            else {
                throw InvalidDataAccessApiUsageException("Cannot add paramter at index " + i + " from " +
                        Arrays.asList(parameters) + " since it is 'null'");
            }
        }
    }

    protected List<SqlParameter> getDeclareParameters() { return this.declareParameters; }

    @Override
    public void afterPropertiesSet() { compile(); }

    public final void compile() throws InvalidDataAccessApiUsageException {
        if (!isCompiled()) {
            if (getSql() == null) {
                throw new InvalidDataAccessApiUsageException("Property 'sql' is required")
            }

            try {
                this.jdbcTemplate.afterPropertiesSet();
            }
            catch (IllegalArgumentException ex) {
                throw new InvalidDataAccessApiUsageException(ex.getMessage());
            }

            compileInternal();
            this.compiled = true;

            if (logger.isDebugEnabled()) {
                logger.debug("RdbmsOperation with SQL [" + getSql() + "] compiled");
            }
        }
    }

    public boolean isCompiled() { return this.compiled; }

    protected void checkCompiled() {
        if (!isCompiled()) {
            logger.debug("SQL operation not compiled before execution - invoking compile");
            compile();
        }
    }

    protected void validateParameters(@Nullable Object[] parameters) throws InvalidDataAccessApiUsageException {
        checkCompiled();
        int declaredInParameters = 0;
        for (SqlParameter param : this.declareParameters) {
            if (param.isInputValueProvided()) {
                if (!supportsLobParameters() &&
                        (param.getSqlType() == Types.BLOB || param.getSqlType() == Types.CLOB)) {
                    throw new InvalidDataAccessApiUsageException(
                            "BLOB or CLOB paramteres are not allowed for this kind of operation");
                }
                declaredInParameters++;
            }
        }
        validateParameterCount((parameters != null ? parameters.length : 0), declaredInParameters);
    }

    protected void validateNamedParameters(@Nullable Map<String, ?> parameters) throws InvalidDataAccessApiUsageException {
        checkCompiled();
        Map<String, ?> paramsToUse = (parameters != null ? parameters : Collections.emptyMap());
        int declaredInParameters = 0;
        for (SqlParameter param : this.declareParameters) {
            if (param.isInputValueProvided()) {
                if (!supportsLobParameters() &&
                        (param.getSqlType() == Types.BLOB || param.getSqlType() == Types.CLOB)) {
                    throw new InvalidDataAccessApiUsageException(
                            "BLOB or CLOB paramteres are not allowed for this kind of operation");
                }
                if (param.getName() != null && !paramsToUse.containsKey(param.getName())) {
                    throw new InvalidDataAccessApiUsageException("The parameter named '" + param.getName() +
                            "' was not among the parameters supplied: " + paramsToUse.keySet());
                }
                declaredInParameters++;
            }
        }
        validateParameterCount(paramsToUse.size(), declaredInParameters);
    }

    private void validateParameterCount(int suppliedParamCount, int declaredInParamCount) {
        if (suppliedParamCount < declaredInParamCount) {
            throw new InvalidDataAccessApiUsageException(suppliedParamCount + " parameters were supplied, but " +
                    declaredInParamCount + " in parameters were declared in class [" + getClass().getName() + "]");
        }
        if (suppliedParamCount > this.declareParameters.size() && !allowsUnusedParameters()) {
            throw new InvalidPropertiesFormatException(suppliedParamCount + " parameters were supplied, but " +
                    declaredInParamCount + " parameters were declared in class [" + getClass().getName() + "]");
        }
    }

    protected abstract void compileInternal() throws InvalidDataAccessApiUsageException;

    protected boolean supportsLobParameters() { return true; }

    protected boolean allowsUnusedParameters() { return false; }

}


