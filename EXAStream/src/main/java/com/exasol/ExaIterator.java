package com.exasol;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public interface ExaIterator {

    public long size() throws ExaIterationException;

    public boolean next() throws ExaIterationException;

    public void reset() throws ExaIterationException;

    public void emit(Object... values) throws ExaIterationException, ExaDataTypeException;

    public Integer getInteger(int column) throws ExaIterationException, ExaDataTypeException;

    public Integer getInteger(String name) throws ExaIterationException, ExaDataTypeException;

    public Long getLong(int column) throws ExaIterationException, ExaDataTypeException;

    public Long getLong(String name) throws ExaIterationException, ExaDataTypeException;

    public BigDecimal getBigDecimal(int column) throws ExaIterationException, ExaDataTypeException;

    public BigDecimal getBigDecimal(String name) throws ExaIterationException, ExaDataTypeException;

    public Double getDouble(int column) throws ExaIterationException, ExaDataTypeException;

    public Double getDouble(String name) throws ExaIterationException, ExaDataTypeException;

    public String getString(int column) throws ExaIterationException, ExaDataTypeException;

    public String getString(String name) throws ExaIterationException, ExaDataTypeException;

    public Boolean getBoolean(int column) throws ExaIterationException, ExaDataTypeException;

    public Boolean getBoolean(String name) throws ExaIterationException, ExaDataTypeException;

    public Date getDate(int column) throws ExaIterationException, ExaDataTypeException;

    public Date getDate(String name) throws ExaIterationException, ExaDataTypeException;

    public Timestamp getTimestamp(int column) throws ExaIterationException, ExaDataTypeException;

    public Timestamp getTimestamp(String name) throws ExaIterationException, ExaDataTypeException;

    public Object getValue(int column) throws ExaIterationException, ExaDataTypeException;

    // TODO What is this? Keep this public?
    public void setInsideRun(boolean inside);
}
