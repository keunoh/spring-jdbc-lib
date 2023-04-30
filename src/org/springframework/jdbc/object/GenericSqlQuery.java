package org.springframework.jdbc.object;


import org.springframework.jdbc.myannotation.Nullable;

import javax.swing.tree.RowMapper;
import java.util.Map;

/**
 * A concrete variant of SqlQuery which can be configured with a RowMapper
 *
 * @param <T>
 */
public class GenericSqlQuery<T> extends SqlQuery<T> {

    @Nullable
    private RowMapper<T> rowMapper;

    @SuppressWarnings("rawtypes")
    @Nullable
    private Class<? extends RowMapper> rowMapperClass;

    /**
     * Set a specific RowMapper instance to use for this query.
     */
    public void setRowMapper(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }

    /**
     * Set a RowMapper class for this query, creating a fresh RowMapper instance per execution.
     */
    @SuppressWarnings("rawtypes")
    @Nullable
    public void setRowMapperClass(Class<? extends RowMapper> rowMapperClass) {
        this.rowMapperClass = rowMapperClass;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterProperteisSet();
        Assert.isTrue(this.rowMapper != null || this.rowMapperClass != null,
                "'rowMapper' or 'rowMapperClass' is required");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected RowMapper<T> newRowMapper(@Nullable Object[] parameters, @Nullable Map<?, ?> context) {
        if (this.rowMapper != null) {
            return this.rowMapper;
        }
        else {
            Assert.state(this.rowMapperClass != null, "No RowMapper set");
            return BeanUtils.instantiateClass(this.rowMapperClass);
        }
    }
}
