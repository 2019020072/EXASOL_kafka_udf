package com.exasol;
import java.math.BigInteger;

public interface ExaMetadata {

    public String getDatabaseName();
    
    public String getDatabaseVersion();
    
    public String getScriptLanguage();
    
    public String getScriptName();
    
    public String getScriptCode();
    
    public String getSessionId();
    
    public long getStatementId();
    
    public long getNodeCount();
    
    public long getNodeId();
    
    public String getVmId();
    
    public BigInteger getMemoryLimit();
    
    public String getInputType();
    
    public long getInputColumnCount();
    
    public String getInputColumnName(int column) throws ExaIterationException;
    
    public Class getInputColumnType(int column) throws ExaIterationException;
    
    public String getInputColumnSqlType(int column) throws ExaIterationException;
    
    public long getInputColumnPrecision(int column) throws ExaIterationException;
    
    public long getInputColumnScale(int column) throws ExaIterationException;
    
    public long getInputColumnLength(int column) throws ExaIterationException;
    
    public String getOutputType();
    
    public long getOutputColumnCount();
    
    public String getOutputColumnName(int column) throws ExaIterationException;
    
    public Class getOutputColumnType(int column) throws ExaIterationException;
    
    public String getOutputColumnSqlType(int column) throws ExaIterationException;
    
    public long getOutputColumnPrecision(int column) throws ExaIterationException;
    
    public long getOutputColumnScale(int column) throws ExaIterationException;
    
    public long getOutputColumnLength(int column) throws ExaIterationException;

    public Class<?> importScript(String name) throws ExaCompilationException, ClassNotFoundException;

    // TODO What is this? Make this available?
    public String checkException();
}
