package org.springframework.jdbc.myannotation;

@SuppressWarnings("serial")
public abstract class NonTransientDataAccessException extends DataAccessException {

    public NonTransientDataAccessException(String msg) {
        super(msg);
    }

    public NonTransientDataAccessException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
