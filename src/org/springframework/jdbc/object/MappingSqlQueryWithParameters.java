package org.springframework.jdbc.object;

import org.springframework.jdbc.myannotation.Nullable;

import javax.sql.DataSource;
import javax.swing.tree.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Reusable RDBMS query in which concrete subclasses must implement the abstract mapRow(ResultSet, int)
 * method to map each row of the JDBC ResultSet into an object.
 * Such manual mapping is usually preferable to "automatic" mapping using reflection, which can become
 * complex in non-trivial cases. For example, the present class allows different objects to be used for
 * different rows (for example, if as subclass is indicated). It allows computed fields to be set. And there's no
 * need for ResultSet columns to have the same names as bean properties. The Pareto Principle in action:
 * going the extra mile to automate the extraction process makes the framework much more complex and
 * delivers little real benefit.
 * Subclasses can be constructed providing SQL, parameter types and a DatSource. SQL will often vary
 * between subclasses
 * Type parameters: <T> - the result type
 */
public abstract class MappingSqlQueryWithParameters<T> extends SqlQuery<T> {

    /**
     * Constructor to allow use as a JavaBean.
     */
    public MappingSqlQueryWithParameters() {
    }

    /**
     * Convenient constructor with DataSource and SQL string.
     * Params: ds - the DatSource to use to get connections
     *         sql - the SQL to run
     */
    public MappingSqlQueryWithParameters(DataSource ds, String sql) { super(ds, sql); }

    /**
     * Implementation of protected abstract method. This invokes the subclass's implementation of the
     * mapRow() method
     */
    @Override
    protected RowMapper<T> newRowMapper(@Nullable Object[] parameters, @Nullable Map<?, ?> context) {
        return new RowMapperImpl(parameters, context);
    }

    /**
     * Subclasses must implement this method to convert each row of the ResultSet into an object of the
     * result type.
     * Params: rs - he ResultSet we're working through
     * rowNum - row number (from 0) we're up to
     * parameters - to the query (passed to the execute() method). Subclasses are rarely
     * interested in these. it can be null if there are no parameters.
     * context - passed to the execute() method. It can be null if no contextual information is
     * need.
     * Returns: an object of the result type
     * Throws: SQLException - if there's an error extracting data. Subclasses cna simply not catch
     * SQLExceptions, relying on the framework to clean up.
     */
    @Nullable
    protected abstract T mapRow(ResultSet rs, int rowNum, @Nullable Object[] parameters, @Nullable Map<?, ?> context)
        throws SQLException;

    /**
     * Implementation of RowMapper that class he enclosing class's mapRow method for each row.
     */
    protected class RowMapperImpl implements RowMapper<T> {

        @Nullable
        private final Object[] params;

        @Nullable
        private final Map<?, ?> context;

        /**
         * Use an array results. More efficient if we know how many results to expect.
         */
        public RowMapperImpl(@Nullable Object[] parameters, @Nullable Map<?, ?> context) {
            this.params = parameters;
            this.context = context;
        }

        @Override
        @Nullable
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            return MappingSqlQueryWithParameters.this.mapRow(rs, rowNum, this.params, this.context);
        }
    }
}
