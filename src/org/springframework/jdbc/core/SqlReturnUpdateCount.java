package org.springframework.jdbc.core;

import java.sql.Types;

public class SqlReturnUpdateCount extends SqlParameter {

    public SqlReturnUpdateCount(String name) {
        super(name, Types.INTEGER);
    }

    @Override
    public boolean isInputValueProvided() {
        return false;
    }

    @Override
    public boolean isResultsParameter() {
        return true;
    }
}
