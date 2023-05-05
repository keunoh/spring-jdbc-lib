package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.Nullable;

public class SqlOutParameter extends ResultSetSupportingSqlParameter {

    @Nullable
    private SqlReturnType sqlReturnType;

    public SqlOutParameter(String name, int sqlType) {
        super(name, sqlType);
    }

    public SqlOutParameter(String name, int sqlType, int scale, SqlReturnType sqlReturnType) {
        super(name, sqlType, scale);
    }

    public SqlOutParameter(String name, int sqlType, @Nullable String typeName) {
        super(name, sqlType, typeName);
    }

    public SqlOutParameter(String name, int sqlType, @Nullable String typeName, @Nullable SqlReturnType sqlReturnType) {
        super(name, sqlType, typeName);
        this.sqlReturnType = sqlReturnType;
    }

    public SqlOutParameter(String name, int sqlType, ResultSetExtractor<?> rse) {
        super(name, sqlType, rse);
    }

    public SqlOutParameter(String name, int sqlType, RowCallbackHandler rch) {
        super(name, sqlType, rch);
    }

    public SqlOutParameter(String name, int sqlType, RowMapper<?> rm) {
        super(name, sqlType, rm);
    }

    @Nullable
    public SqlReturnType getSqlReturnType() {
        return sqlReturnType;
    }

    public boolean isReturnTypeSupported() {
        return (this.sqlReturnType != null);
    }
}
