package com.exasol;

public class ExaCompilationException extends Exception {
    public ExaCompilationException() { super(); }
    public ExaCompilationException(String message) { super(message); }
    public ExaCompilationException(String message, Throwable cause) { super(message, cause); }
    public ExaCompilationException(Throwable cause) { super(cause); }
}
