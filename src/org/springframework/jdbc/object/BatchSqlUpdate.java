package org.springframework.jdbc.object;

import org.w3c.dom.stylesheets.LinkStyle;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * SqlUpdate subclass that performs batch update operations. Encapsulates queuing up records to be
 * updated, and adds them as a single batch once flush is called or the given batch size has been met.
 * Note that this class is a non-thread-safe object, in contrat to all other JDBC operations objects in this
 * package. You need to create a new instacne of it for each use, or call reset before reuse within the same
 * thread.
 */
public class BatchSqlUpdate extends SqlUpdate {
    /**
     * Default number of inserts to accumulate before committing a batch (5000).
     */
    public static final int DEFAULT_BATCH_SIZE = 5000;

    private int batchSize = DEFAULT_BATCH_SIZE;

    private boolean trackRowsAffected = true;

    private final Deque<Object[]> parameterQueue = new ArrayDeque<>();

    private final List<Integer> rowsAffected = new ArrayList<>();

    /**
     * Constructor to allow use as a JavaBean. DataSource and SQL must be supplied before compilation
     * and use.
     */
    public BatchSqlUpdate() { super(); }

    /**
     * Construct an update object with a given DataSource and SQL.
     * Params: ds - the DataSource to use to obtain connections
     *         sql - the SQL statement to execute
     */
    public BatchSqlUpdate(DataSource ds, String sql) { super(ds, sql); }

    /**
     * Construct an update object with a given DataSource, SQL and anonymous parameters.
     * Params: ds - the DataSource to use to obtain connections
     *         sql - the SQL statement to execute
     *         types - the SQL types of the parameters, as defined in the java.sql.Types class
     */
    public BatchSqlUpdate(DataSource ds, String sql, int[] types) { super(ds, sql, types); }

    /**
     * Construct an update object with a given DataSource, SQL, anonymous parameters and specifying the
     * maximum number of rows that may be affected.
     * Params: ds - the DataSource to use to obtain connections
     * sql - the SQL statement to execute
     * types - the SQL types of the parameters, as defined in the java.sql.Types class
     * batchSize - the number of statements that will trigger an automatic intermediate flush
     */
    public BatchSqlUpdate(DataSource ds, String sql, int[] types, int batchSize) {
        super(ds, sql, types);
        setBatchSize(batchSize);
    }

    /**
     * Set the number of statements that will trigger an automatic intermediate flush. update class or the
     * given statement parameters will be queued until the batch size is met, at which point it will empty
     * the queue and execute the batch.
     * You can also flush already queued statements with an explicit flush call. note that yu need to this
     * after queueing all parameters to guarantee that all statements have een flushed.
     */
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }

    /**
     * Set whether to track the rows affected by batch updates performed by this operation object.
     * Default is "true". Turn this off to save toe memory needed for the list of row counts.
     */
    public void setTrackRowsAffected(boolean trackRowsAffected) { this.trackRowsAffected = trackRowsAffected; }

    /**
     * BatchSqlUpdate does not support BLOB or CLOB parameters.
     */
    @Override
    protected boolean supportsLobParameters() { return false; }

    /**
     * Overridden version of update that adds the given statement parameters to the queue rather than
     * executing them immediately. All other update methods of the SqlUpdate base class go through this
     * method and will thus behave similarly.
     * You need to call flush to actually execute the batch. If the specified batch size is reached, an implicit
     * flush will happen; you still need to finally call flush to flush all statements.
     * Params: params - array of parameter objects
     * Returns: the number of rows affected by the update (always -1, meaning "not applicable", as the
     * statement is not  actually executed by this method)
     */
    @Override
    public int update(Object... params) throws DataAccessException {
        validateParameters(params);
        this.parameterQueue.add(params.clone());

        if (this.parameterQueue.size() == this.batchSize) {
            if (logger.isDebugEnabled()) {
                logger.debug("Triggering auto-flush because queue reached batch size of " + this.batchSize);
            }
            flush();
        }

        return -1;
    }

    /**
     * Trigger ant queued update operations to be added as final batch.
     * Returns: an array of the number of rows affected by each statement
     */
    public int[] flush() {
        if (this.parameterQueue.isEmpty()) {
            return new int[0];
        }

        int[] rowsAffected = getJdbcTemplate().batchUpdate(
                resolveSql(),
                new BatchPreparedStatementSetter() {
                    @Override
                    public int getBatchSize() { return parameterQueue.size(); }
                    @Override
                    public void setValues(PreparedStatement ps, int index) throws SQLException {
                        Object[] params = parameterQueue.removeFirst();
                        newPreparedStatementSetter(params).setValues(ps);
                    }
                });

        for (int rowCount : rowsAffected) {
            checkRowsAffected(rowCount);
            if (this.trackRowsAffected) {
                this.rowsAffected.add(rowCount);
            }
        }

        return rowsAffected;
    }

    /**
     * Return the current number of statements or statement parameters in the queue.
     */
    public int getQueueCount() { return this.parameterQueue.size(); }

    /**
     * Return the number of already executed statements.
     */
    public int getExecutionCount() { return this.rowsAffected.size(); }

    /**
     * Return the number of affected rows for all already executed statements. Accumulates all of flush's
     * return values until reset is invoked.
     * Returns: an array of the number of rows affected by each statement
     */
    public int[] getRowsAffected() {
        int[] result = new int[this.rowsAffected.size()];
        for (int i = 0; i < this.rowsAffected.size(); i++) {
            result[i] = this.rowsAffected.get(i);
        }
        return result;
    }

    /**
     * Reset the statement parameter queue, the rows affected cache, and the execution count.
     */
    public void reset() {
        this.parameterQueue.clear();
        this.rowsAffected.clear();
    }

}












