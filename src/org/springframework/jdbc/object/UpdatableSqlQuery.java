package org.springframework.jdbc.object;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public abstract class UpdatableSqlQuery<T> extends SqlQuery<T> {

    public UpdatableSqlQuery() {
        setUpdatableResults(true);
    }

    public UpdatableSqlQuery(DataSource ds, String sql) {
        super(ds, sql);
        setUpdatableResults(true);
    }

    @Override
    protected RowMapper<T> newRowMapper(@Nullable Object[] parameters, @Nullable Map<?, ?> context) {
        return new MappingSqlQueryWithParameters.RowMapperImpl(context);
    }

    protected abstract T updateRow(ResultSet rs, int rowNum, @Nullable Map<?, ?> context) throws SQLException;

    protected class RowMapperImpl implements RowMapper<T> {
        @Nullable
        private final Map<?, ?> context;

        public MappingSqlQueryWithParameters.RowMapperImpl(@Nullable Map<?, ?> context) {
            this.context = context;
        }

        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            T result = updateRow(rs, rowNum, this.context);
            rs.updateRow();
            return result;
        }
    }
}
