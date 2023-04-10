package org.springframework.jdbc.object;

/**
 * Concrete implementation making it possible to define the RDBMS stored procedures in an application
 * context without writing a custom Java implementation class.
 * This implementation does not provide a typed method for invocation so executions must use one of the
 * generic StoredProcedureExecute(java.util.Map) or StoredProcedure.execute(org.springframework.
 * jdbc.core.ParameterMapper) methods.
 */
public class GenericStoredProcedure extends StoredProcedure {
}
