package org.springframework.jdbc.core;

public class SqlReturnResultSet extends ResultSetSupportingSqlParameter {

    public SqlReturnResultSet(String name, ResultSetExtractor<?> rse) {
        super(name, 0, rse);
    }

    public SqlReturnResultSet(String name, RowCallbackHandler handler) {
        super(name, 0, handler);
    }

    public SqlReturnResultSet(String name, RowMapper<?> rm) {
        super(name, 0, rm);
    }

    @Override
    public boolean isResultParameter() {
        return true;
    }
}
