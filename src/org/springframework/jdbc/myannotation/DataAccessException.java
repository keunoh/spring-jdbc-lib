package org.springframework.jdbc.myannotation;

@SuppressWarnings("serial")
public abstract class DataAccessException extends NestedRuntimeException {

    public DataAccessException(String msg) {
        super(msg);
    }

    public DataAccessException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
