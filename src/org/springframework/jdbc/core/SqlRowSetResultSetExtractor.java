package org.springframework.jdbc.core;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlRowSetResultSetExtractor implements ResultSetExtractor<SqlRowSet> {

    private static final RowSetFactory rowSetFactory;

    static {
        try {
            rowSetFactory = RowSetProvider.newFactory();
        }
        catch (SQLException ex) {
            throw new IllegalStateException("Cannot create RowSetFactory through RowSetProvider", ex);
        }
    }

    @Override
    public SqlRowSet extractData(ResultSet rs) throws SQLException {
        return createSqlRowSet(rs);
    }

    protected SqlRowSet createSqlRowSet(ResultSet rs) throws SQLException {
        CachedRowSet rowSet = newCachedRowSet();
        rowSet.populate(rs);
        return new ResultSetWrappingSqlRowSet(rowSet);
    }

    protected CachedRowSet newCachedRowSet() throws SQLException {
        return rowSetFactory.createCachedRowSet();
    }
}
