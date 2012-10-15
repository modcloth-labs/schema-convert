package com.modcloth.database;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Types;

import org.junit.Before;
import org.junit.Test;

import com.modcloth.database.TableDefinition.ColumnDefinition;

public class TableDefinitionTest {
    private TableDefinition table;

    @Before public void setUp() {
        table = new TableDefinition("table_one", ".*_sk$");

        table.addColumnDefinition(
            new ColumnDefinition("c_one", Types.INTEGER, "NO", 0, 0, "", "NO"));
        table.addColumnDefinition(
            new ColumnDefinition("table_sk", Types.INTEGER, "NO", 0, 0, "", "NO"));
        table.addColumnDefinition(
            new ColumnDefinition("c_two", Types.VARCHAR, "NO", 10, 0, "", "NO"));
    }

    @Test public void getSurrogateKeyNameTest() {
        assertEquals(table.getSurrogateKeyName(), "table_sk");
    }

    @Test public void getColumnNamesTest() {
        assertArrayEquals(table.getColumnNames().toArray(new String[0]),
            new String[] { "c_one", "table_sk", "c_two" });
    }

    @Test public void autoIncrementFlagNoTest() {
        ColumnDefinition cd = new ColumnDefinition("c_one", Types.INTEGER, "NO", 0, 0, "", "NO");

        assertFalse(cd.getIsAutoIncrement());
    }

    @Test public void autoIncrementFlagYesTest() {
        ColumnDefinition cd = new ColumnDefinition("c_one", Types.INTEGER, "NO", 0, 0, "", "YES");

        assertTrue(cd.getIsAutoIncrement());
    }

    @Test public void autoIncrementFlagNullTest() {
        ColumnDefinition cd = new ColumnDefinition("c_one", Types.INTEGER, "NO", 0, 0, "", null);

        assertFalse(cd.getIsAutoIncrement());
  }
}
