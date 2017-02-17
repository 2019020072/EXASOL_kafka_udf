package com.exasol;

public class ExaDataTypeException extends Exception {
    public ExaDataTypeException() { super(); }
    public ExaDataTypeException(String message) { super(message); }
    public ExaDataTypeException(String message, Throwable cause) { super(message, cause); }
    public ExaDataTypeException(Throwable cause) { super(cause); }
}
