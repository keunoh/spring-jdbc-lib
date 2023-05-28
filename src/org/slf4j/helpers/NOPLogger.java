package org.slf4j.helpers;

public class NOPLogger extends MarkerIgnoringBase {

    private static final long serialVersionUID = -517220405410904473L;

    public static final NOPLogger NOP_LOGGER = new NOPLogger();

    protected NOPLogger() {}

    public String getName() {
        return "NOP";
    }

    /**
     * Always returns false.
     * @return always false
     */
    final public boolean isTraceEnabled() {
        return false;
    }

    /** A NOP implementation. */
    final public void trace(String msg) {
        // NOP
    }

    /** A NOP implementation.  */
    final public void trace(String format, Object arg) {
        // NOP
    }

    /** A NOP implementation.  */
    public final void trace(String format, Object arg1, Object arg2) {
        // NOP
    }

    /** A NOP implementation.  */
    public final void trace(String format, Object... argArray) {
        // NOP
    }

    /** A NOP implementation. */
    final public void trace(String msg, Throwable t) {
        // NOP
    }

    /**
     * Always returns false.
     * @return always false
     */
    final public boolean isDebugEnabled() {
        return false;
    }

    /** A NOP implementation. */
    final public void debug(String msg) {
        // NOP
    }

    /** A NOP implementation.  */
    final public void debug(String format, Object arg) {
        // NOP
    }

    /** A NOP implementation.  */
    public final void debug(String format, Object arg1, Object arg2) {
        // NOP
    }

    /** A NOP implementation.  */
    public final void debug(String format, Object... argArray) {
        // NOP
    }

    /** A NOP implementation. */
    final public void debug(String msg, Throwable t) {
        // NOP
    }

    /**
     * Always returns false.
     * @return always false
     */
    final public boolean isInfoEnabled() {
        // NOP
        return false;
    }

    /** A NOP implementation. */
    final public void info(String msg) {
        // NOP
    }

    /** A NOP implementation. */
    final public void info(String format, Object arg1) {
        // NOP
    }

    /** A NOP implementation. */
    final public void info(String format, Object arg1, Object arg2) {
        // NOP
    }

    /** A NOP implementation.  */
    public final void info(String format, Object... argArray) {
        // NOP
    }

    /** A NOP implementation. */
    final public void info(String msg, Throwable t) {
        // NOP
    }

    /**
     * Always returns false.
     * @return always false
     */
    final public boolean isWarnEnabled() {
        return false;
    }

    /** A NOP implementation. */
    final public void warn(String msg) {
        // NOP
    }

    /** A NOP implementation. */
    final public void warn(String format, Object arg1) {
        // NOP
    }

    /** A NOP implementation. */
    final public void warn(String format, Object arg1, Object arg2) {
        // NOP
    }

    /** A NOP implementation.  */
    public final void warn(String format, Object... argArray) {
        // NOP
    }

    /** A NOP implementation. */
    final public void warn(String msg, Throwable t) {
        // NOP
    }

    /** A NOP implementation. */
    final public boolean isErrorEnabled() {
        return false;
    }

    /** A NOP implementation. */
    final public void error(String msg) {
        // NOP
    }

    /** A NOP implementation. */
    final public void error(String format, Object arg1) {
        // NOP
    }

    /** A NOP implementation. */
    final public void error(String format, Object arg1, Object arg2) {
        // NOP
    }

    /** A NOP implementation.  */
    public final void error(String format, Object... argArray) {
        // NOP
    }

    /** A NOP implementation. */
    final public void error(String msg, Throwable t) {
        // NOP
    }
}
