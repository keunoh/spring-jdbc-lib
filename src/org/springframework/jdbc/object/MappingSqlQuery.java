package org.springframework.jdbc.object;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Reusable query in which concrete subclasses must implement the abstract mapRow(ResultSet, int) method
 * to convert each row of the JDBC ResultSet into an object.
 * Simplifies MappingSqlQueryWithParameters API by dropping parameters and context. Most subclasses
 * won't care about parameters. If you don't use contextual information, subclass this instated of
 * mappingSqlQueryWithParameters.
 */
public abstract class MappingSqlQuery<T> extends MappingSqlQueryWithParameters<T> {

    /**
     * Constructor that allows use as a JavaBean
     */
    public MappingSqlQuery() {

    }

    /**
     * Convenient constructor with DataSource and SQL string.
     * Params: ds - the DataSource to use to obtain connections
     *         sql - the SQL to run
     */
    public MappingSqlQuery(DataSource ds, String sql) { super(ds, sql); }

    /**
     * This method is implemented to invoke the simpler mapRow template method, ignoring parameters.
     */
    @Override
    @Nullable
    protected final T mapRow(ResultSet rs, int rowNum, @Nullable Object[] parameters, @Nullable Map<?, ?> context)
        throws SQLException {

        return mapRow(rs, rowNum);
    }

    /**
     * Subclasses must implement this method to convert each row of the ResultSet into an object of the
     * result type.
     * Subclasses of this class, as opposed to direct subclasses of MappingSqlQueryWithParameters, don't
     * need to concern themselves with the parameters to the execute method of the query object.
     * Params: rs - the ResultSet we're working through
     *         rowNum - row number (from 0) we're up to
     * Returns: an object of the result type
     * Throws: SQLException - if there's an error extracting data. Subclasses can simply not catch
     * SQLExceptions, relying on the framework to clean up.
     */
    @Nullable
    protected abstract T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
