package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.Nullable;

public class SqlParameterValue extends SqlParameter {

    @Nullable
    private final Object value;

    public SqlParameterValue(int sqlType, @Nullable Object value) {
        super(sqlType);
        this.value = value;
    }

    public SqlParameterValue(int sqlType, @Nullable String typeName, @Nullable Object value) {
        super(sqlType, typeName);
        this.value = value;
    }

    public SqlParameterValue(int sqlType, int scale, @Nullable Object value) {
        super(sqlType, scale);
        this.value = value;
    }

    public SqlParameterValue(SqlParameter declaredParam, @Nullable Object value) {
        super(declaredParam);
        this.value = value;
    }

    @Nullable
    public Object getValue() {
        return this.value;
    }
}
