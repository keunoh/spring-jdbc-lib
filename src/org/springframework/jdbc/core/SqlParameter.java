package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SqlParameter {

    @Nullable
    private String name;

    private final int sqlType;

    @Nullable
    private String typeName;

    @Nullable
    private Integer scale;

    public SqlParameter(int sqlType) {
        this.sqlType = sqlType;
    }

    public SqlParameter(int sqlType, @Nullable String typeName) {
        this.sqlType = sqlType;
        this.typeName = typeName;
    }

    public SqlParameter(int sqlType, Integer scale) {
        this.sqlType = sqlType;
        this.scale = scale;
    }

    public SqlParameter(String name, int sqlType) {
        this.name = name;
        this.sqlType = sqlType;
    }

    public SqlParameter(String name, int sqlType, @Nullable String typeName) {
        this.name = name;
        this.sqlType = sqlType;
        this.typeName = typeName;
    }

    public SqlParameter(String name, int sqlType, Integer scale) {
        this.name = name;
        this.sqlType = sqlType;
        this.scale = scale;
    }

    public SqlParameter(SqlParameter otherParam) {
        Assert.notNull(otherParam, "SqlParameter object must not be null");
        this.name = otherParam.name;
        this.sqlType = otherParam.sqlType;
        this.typeName = otherParam.typeName;
        this.scale = otherParam.scale;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    @Nullable
    public int getSqlType() {
        return this.sqlType;
    }

    @Nullable
    public String getTypeName() {
        return this.typeName;
    }

    @Nullable
    public Integer getScale() {
        return this.scale;
    }

    public boolean isInputValueProvided() {
        return true;
    }

    public boolean isResultsParameter() {
        return false;
    }

    public static List<SqlParameter> sqlTypesToAnonymousParameterList(@Nullable int... types) {
        if (types == null) {
            return new ArrayList<>();
        }
        List<SqlParameter> result = new ArrayList<>(types.length);
        for (int type : types) {
            result.add(new SqlParameter(type));
        }
        return result;
    }
}
