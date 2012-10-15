package com.modcloth.converters;

import static org.junit.Assert.assertEquals;

import java.sql.Types;

import org.junit.Test;

public class SqlTypeConverterTest {
    @Test public void intTypeToString() {
        assertEquals(SqlTypeConverter.typeToString(Types.INTEGER), "INTEGER");
    }

    @Test public void charTypeToString() {
        assertEquals(SqlTypeConverter.typeToString(Types.CHAR), "CHAR");
    }

    @Test public void varcharTypeToString() {
        assertEquals(SqlTypeConverter.typeToString(Types.VARCHAR), "VARCHAR");
    }

    @Test public void decimalTypeToString() {
        assertEquals(SqlTypeConverter.typeToString(Types.DECIMAL), "DECIMAL");
    }

    @Test public void textTypeToString() {
        assertEquals(SqlTypeConverter.typeToString(Types.LONGVARCHAR), "LONGVARCHAR");
    }
}
