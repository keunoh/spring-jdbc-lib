package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.Nullable;

import java.sql.*;
import java.util.*;

public class PreparedStatementCreatorFactory {

    private final String sql;

    private final List<SqlParameter> declaredParameters;

    private int resultSetType = ResultSet.TYPE_FORWARD_ONLY;

    private boolean updatableResults = false;

    private boolean returnGeneratedKeys = false;

    @Nullable
    private String[] generatedKeysColumnNames;

    public PreparedStatementCreatorFactory(String sql) {
        this.sql = sql;
        this.declaredParameters = new ArrayList<>();
    }

    public PreparedStatementCreatorFactory(String sql, int... types) {
        this.sql = sql;
        this.declaredParameters = SqlParameter.sqlTypesToAnonymousParameterList(types);
    }

    public PreparedStatementCreatorFactory(String sql, List<SqlParameter> declaredParameters) {
        this.sql = sql;
        this.declaredParameters = declaredParameters;
    }

    public final String getSql() {
        return this.sql;
    }

    public void addParameter(SqlParameter param) {
        this.declaredParameters.add(param);
    }

    public void setResultSetType(int resultSetType) {
        this.resultSetType = resultSetType;
    }

    public void setUpdatableResults(boolean updatableResults) {
        this.updatableResults = updatableResults;
    }

    public void setReturnGeneratedKeys(boolean returnGeneratedKeys) {
        this.returnGeneratedKeys = returnGeneratedKeys;
    }

    public void setGeneratedKeysColumnNames(String... names) {
        this.generatedKeysColumnNames = names;
    }

    public PreparedStatementSetter newPreparedStatementSetter(@Nullable List<?> params) {
        return new PreparedStatementCreatorImpl(params != null ? params : Collections.emptyList());
    }

    public PreparedStatementSetter newPreparedStatementSetter(@Nullable Object[] params) {
        return new PreparedStatementCreatorImpl(params != null ? Arrays.asList(params) : Collections.emptyList());
    }

    public PreparedStatementCreator newPreparedStatementCreator(@Nullable List<?> params) {
        return new PreparedStatementCreatorImpl(params != null ? params : Collections.emptyList());
    }

    public PreparedStatementCreator newPreparedStatementCreator(@Nullable Object[] params) {
        return new PreparedStatementCreatorImpl(params != null ? Arrays.asList(params) : Collections.emptyList());
    }

    public PreparedStatementCreator newPreparedStatementCreator(String sqlToUse, @Nullable Object[] params) {
        return new PreparedStatementCreatorImpl(
                sqlToUse, params != null ? Arrays.asList(params) : Collections.emptyList());
    }

    private class PreparedStatementCreatorImpl
            implements PreparedStatementCreator, PreparedStatementSetter, SqlProvider, ParameterDisposer {

        private final String actualSql;

        private final List<?> parameters;

        public PreparedStatementCreatorImpl(List<?> parameters) {
            this(sql, parameters);
        }

        public PreparedStatementCreatorImpl(String actualSql, List<?> parameters) {
            this.actualSql = actualSql;
            this.parameters = parameters;
            if (parameters.size() != declaredParameters.size()) {
                // Account for named parameters being used multiple times
                Set<String> names = new HashSet<>();
                for (int i = 0; i < parameters.size(); i++) {
                    Object param = parameters.get(i);
                    if (param instanceof SqlParameterValue) {
                        names.add(((SqlParameterValue) param).getName());
                    }
                    else {
                        names.add("Parameter #" + i);
                    }
                }
                if (names.size() != declaredParameters.size()) {
                    throw new InvalidDataAccessApiUsageException(
                            "SQL [" + sql + "]: given " + names.size() +
                            " parameters but expected " + declaredParameters.size());
                }
            }
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            PreparedStatement ps;
            if (generatedKeysColumnNames != null || returnGeneratedKeys) {
                if (generatedKeysColumnNames != null) {
                    ps = con.prepareStatement(this.actualSql, generatedKeysColumnNames);
                }
                else {
                    ps = con.prepareStatement(this.actualSql, PreparedStatement.RETURN_GENERATED_KEYS);
                }
            }
            else if (resultSetType == ResultSet.TYPE_FORWARD_ONLY && !updatableResults) {
                ps = con.prepareStatement(this.actualSql);
            }
            else {
                ps = con.prepareStatement(this.actualSql, resultSetType,
                    updatableResults ? ResultSet.CONCUR_UPDATABLE : ResultSet.CONCUR_READ_ONLY);
            }
            setValues(ps);
            return ps;
        }

        @Override
        public void setValues(PreparedStatement ps) throws SQLException {
            // Set arguments: Does nothing if there are no parameters.
            int sqlColIndx = 1;
            for (int i = 0; i < this.parameters.size(); i++) {
                Object in = this.parameters.get(i);
                SqlParameter declaredParameter;
                // SqlParameterValue overrides declared parameter meta-data, in particular for
                // independence from the declared parameter position in case of named parameters.
                if (in instanceof SqlParameterValue) {
                    SqlParameterValue paramValue = (SqlParameterValue) in;
                    in = paramValue.getValue();
                    declaredParameter = paramValue;
                }
                else {
                    if (declaredParameters.size() <= i) {
                        throw new InvalidDataAccessApiUsageException(
                                "SQL [" + sql + "]: unable to access parameter number " + (i + 1) +
                                " given only " + declaredParameters.size() + " parameters");
                    }
                    declaredParameter = declaredParameters.get(i);
                }
                if (in instanceof Iterable && declaredParameter.getSqlType() != Types.ARRAY) {
                    Iterable<?> entries = (Iterable<?>) in;
                    for (Object entry : entries) {
                        if (entry instanceof Object[]) {
                            Object[] valueArray = (Object[]) entry;
                            for (Object argValue : valueArray) {
                                StatementCreatorUtils.setParameterValue(ps, sqlColIndx++, declaredParameter, argValue);
                            }
                        }
                        else {
                            StatementCreatorUtils.setParameterValue(ps, sqlColIndx++, declaredParameter, entry);
                        }
                    }
                }
                else {
                    StatementCreatorUtils.setParameterValue(ps, sqlColIndx++, declaredParameter, in);
                }
            }
        }

        @Override
        public String getSql() {
            return sql;
        }

        @Override
        public void cleanupParameters() {
            StatementCreatorUtils.cleanupParameters(this.parameters);
        }

        @Override
        public String toString() {
            return "PreparedStatementCreator: sql=[" + sql + "]; parameters=" + this.parameters;
        }
    }
}
