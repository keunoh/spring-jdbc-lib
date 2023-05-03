package org.springframework.jdbc.object;

import org.springframework.jdbc.core.ParameterMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public abstract class StoredProcedure extends SqlCall {

    protected StoredProcedure() {

    }

    protected StoredProcedure(DataSource ds, String name) {
        setDataSource(ds);
        setSql(name);
    }

    protected StoredProcedure(JdbcTemplate jdbcTemplate, String name) {
        setJdbcTemplate(jdbcTemplate);
        setSql(name);
    }

    @Override
    protected boolean allowsUnusedParameters() {
        return true;
    }

    @Override
    public void declareParameter(SqlParameter param) throws InvalidDataAccessApiUsageException {
        if (param.getName() == null) {
            throw new InvalidDataAccessApiUsageException("Parameters to stored procedures must have names as well as types");
        }
        super.declareParameter(param);
    }

    public Map<String, Object> execute(Object... inParams) {
        Map<String, Object> paramsToUse = new HashMap<>();
        validateParameters(inParams);
        int i = 0;
        for (SqlParameter sqlParameter : getDeclareParameters()) {
            if (sqlParameter.isInputValueProvided() && i < inParams.length) {
                paramsToUse.put(sqlParameter.getName(), inParams[i++]);
            }
        }
        return getJdbcTemplate().call(newCallableStatementCreator(paramsToUse), getDeclareParameters());
    }

    public Map<String, Object> execute(Map<String, ?> inParams) throws DataAccessException {
        validateParameters(inParams.values().toArray());
        return getJdbcTemplate().call(newCallableStatementCreator(inParams), getDeclareParameters());
    }

    public Map<String, Object> execute(ParameterMapper inParamMapper) throws DataAccessException {
        checkCompiled();
        return getJdbcTemplate().call(newCallableStatementCreator(inParamMapper), getDeclareParameters());
    }
}
