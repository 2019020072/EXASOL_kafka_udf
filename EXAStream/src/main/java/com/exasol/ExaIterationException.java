package com.exasol;

public class ExaIterationException extends Exception {
    public ExaIterationException() { super(); }
    public ExaIterationException(String message) { super(message); }
    public ExaIterationException(String message, Throwable cause) { super(message, cause); }
    public ExaIterationException(Throwable cause) { super(cause); }
}
