package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.ConversionService;
import org.springframework.jdbc.myannotation.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.myannotation.Nullable;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class BeanPropertyRowMapper<T> implements RowMapper<T> {

    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    private Class<T> mappedClass;

    private boolean checkFullyPopulated = false;

    private boolean primitiveDefaultedForNullValue = false;

    @Nullable
    private ConversionService conversionService = DefaultConversionService.getSharedInstance();

    @Nullable
    private Map<String, PropertyDescriptor> mappedFields;

    @Nullable
    private Set<String> mappedProperties;

    public BeanPropertyRowMapper() {

    }

    public BeanPropertyRowMapper(Class<T> mappedClass) {
        initialize(mappedClass);
    }

    public BeanPropertyRowMapper(Class<T> mappedClass, boolean checkFullyPopulated) {
        initialize(mappedClass);
        this.checkFullyPopulated = checkFullyPopulated;
    }

    public void setMappedClass(Class<T> mappedClass) {
        if (this.mappedClass == null) {
            initialize(mappedClass);
        }
        else {
            if (this.mappedClass != mappedClass) {
                throw new InvalidDataAccessApiUsageException("The mapped class can not be reassigned to map to " +
                        mappedClass + " since it is already providing mapping for " + this.mappedClass);
            }
        }
    }

    @Nullable
    public final Class<T> getMappedClass() {
        return this.mappedClass;
    }

    public void setCheckFullyPopulated(boolean checkFullyPopulated) {
        this.checkFullyPopulated = checkFullyPopulated;
    }

    public boolean isCheckFullyPopulated() {
        return this.checkFullyPopulated;
    }

    public void setPrimitiveDefaultedForNullValue(boolean primitiveDefaultedForNullValue) {
        this.primitiveDefaultedForNullValue = primitiveDefaultedForNullValue;
    }

    public boolean isPrimitiveDefaultedForNullValue() {
        return this.primitiveDefaultedForNullValue;
    }

    public void setConversionService(@Nullable ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Nullable
    public ConversionService getConversionService() {
        return this.conversionService;
    }

    protected void initialize(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
        this.mappedFields = new HashMap<>();
        this.mappedProperties = new HashSet<>();

        for (PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(mappedClass)) {
            if (pd.getWriteMethod() != null) {
                String lowerCaseName = lowerCaseName(pd.getName());
                this.mappedFields.put(lowerCaseName, pd);
                String underscoreName = underscoreName(pd.getName());
                if (!lowerCaseName.equals(underscoreName)) {
                    this.mappedFields.put(underscoreName, pd);
                }
                this.mappedProperties.add(pd.getName());
            }
        }
    }

    protected void suppressProperty(String propertyName) {
        if (this.mappedFields != null) {
            this.mappedFields.remove(lowerCaseName(propertyName));
            this.mappedFields.remove(underscoreName(propertyName));
        }
    }

    protected String lowerCaseName(String name) {
        return name.toLowerCase(Locale.US);
    }

    protected String underscoreName(String name) {
        if (!StringUtils.hasLength(name)) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(name.charAt(0)));
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append('_').append(Character.toLowerCase(c));
            }
            else {
                result.append(c);
            }
        }
        return result.toString();
    }

    @Override
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        BeanWrapperImpl bw = new BeanWrapperImpl();
        initBeanWrapper(bw);

        T mappedObject = constructMappedInstance(rs, bw);
        bw.setBeanInstance(mappedObject);

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Set<String> populatedProperties = (isCheckFullyPopulated() ? new HashSet<>() : null);

        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(rsmd, index);
            String field = lowerCaseName(StringUtils.delete(column, " "));
            PropertyDescriptor pd = (this.mappedFields != null ? this.mappedFields.get(field) : null);
            if (pd != null) {
                try {
                    Object value = getColumnValue(rs, index, pd);
                    if (rowNumber == 0 && logger.isDebugEnabled()) {
                        logger.debug("Mapping column '" + column + "' to property '" + pd.getName() +
                                "' of type '" + ClassUtils.getQualifiedName(pd.getPropertyType()) + "'");
                    }
                    try {
                        bw.setPropertyValue(pd.getName(), value);
                    }
                    catch (TypeMismatchException ex) {
                        if (value == null && this.primitiveDefaultedForNullValue) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Intercepted TypeMismatchException for row " + rowNumber +
                                        " and column '" + column + "' with null value when setting property '" +
                                        pd.getName() + "' of type '" +
                                        ClassUtils.getQualifiedName(pd.getPropertyType()) +
                                        "' on object: " + mappedObject, ex);
                            }
                        }
                        else {
                            throw ex;
                        }
                    }
                    if (populatedProperties != null) {
                        populatedProperties.add(pd.getName());
                    }
                }
                catch (NotWritablePropertyException ex) {
                    throw new DataRetrievalFailureException(
                            "Unable to map column '" + column + "' to property '" + pd.getName() + "'", ex);
                }
            }
        }

        if (populatedProperties != null && !populatedProperties.equals(this.mappedProperties)) {
            throw new InvalidDataAccessApiUsageException("Given ResultSet does not contain all fields " +
                    "necessary to populate object of " + this.mappedClass + ": " + this.mappedProperties);
        }

        return mappedObject;
    }

    protected T constructMappedInstance(ResultSet rs, TypeConverter tc) throws SQLException {
        Assert.state(this.mappedClass != null, "Mapped class was not specified");
        return BeanUtils.instantiateClass(this.mappedClass);
    }

    protected void initBeanWrapper(BeanWrapper bw) {
        ConversionService cs = getConversionService();
        if (cs != null) {
            bw.setConversionService(cs);
        }
    }

    @Nullable
    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
    }

    @Nullable
    protected Object getColumnValue(ResultSet rs, int index, Class<?> paramType) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index, paramType);
    }

    public static <T> BeanPropertyRowMapper<T> newInstance(Class<T> mappedClass) {
        return new BeanPropertyRowMapper<>(mappedClass);
    }

    public static <T> BeanPropertyRowMapper<T> newInstance(
            Class<T> mappedClass, @Nullable ConversionService conversionService) {
        BeanPropertyRowMapper<T> rowMapper = newInstance(mappedClass);
        rowMapper.setConversionService(conversionService);
        return rowMapper;
    }
}
