package com.modcloth.converters;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.modcloth.database.TableDefinition;
import com.modcloth.database.TableDefinition.ColumnDefinition;
import com.modcloth.database.TableDefinition.IndexDefinition;

@RunWith(MockitoJUnitRunner.class)
public class PostgresTableConverterTest {
    private PostgresTableConverter converter;
    private List<ColumnDefinition> columns;
    private List<IndexDefinition> indexes;

    @Mock private TableDefinition table;

    @Before public void setUp() {
        columns = new LinkedList<ColumnDefinition>();
        indexes = new LinkedList<IndexDefinition>();

        when(table.getName()).thenReturn("test_table");
        when(table.getColumnDefinitions()).thenReturn(columns);
        when(table.getIndexDefinitions()).thenReturn(indexes);

        converter = new PostgresTableConverter(table);
    }

    @Test public void convertSerialColumn() {
        columns.add(new ColumnDefinition("id", Types.INTEGER, "NO", 0, 0, null, "YES"));

        assertEquals(converter.convertToCreateTable().replaceAll("\\n", ""),
            "CREATE TABLE test_table(id SERIAL)");
    }

    @Test public void convertVarcharColumn() {
        columns.add(new ColumnDefinition("column_one", Types.VARCHAR, "YES", 40, 0, null, "NO"));

        assertEquals(converter.convertToCreateTable().replaceAll("\\n", ""),
            "CREATE TABLE test_table(column_one VARCHAR(40))");
    }

    @Test public void convertDecimalColumn() {
        columns.add(new ColumnDefinition("column_one", Types.DECIMAL, "YES", 8, 2, null, "NO"));

        assertEquals(converter.convertToCreateTable().replaceAll("\\n", ""),
            "CREATE TABLE test_table(column_one DECIMAL(8,2))");
    }

    @Test public void convertTextColumn() {
        columns.add(new ColumnDefinition("column_one", Types.LONGVARCHAR, "YES", 0, 0, null, "NO"));

        assertEquals(converter.convertToCreateTable().replaceAll("\\n", ""),
            "CREATE TABLE test_table(column_one TEXT)");
    }

    @Test public void convertPrimaryIndexTest() {
        indexes.add(new IndexDefinition("PRIMARY", "id", false, 1));

        assertArrayEquals(converter.convertToCreateIndex().toArray(new String[0]),
            new String[] {"ALTER TABLE test_table ADD PRIMARY KEY (id)"});
    }

    @Test public void convertMultiColumnIndex() {
        indexes.add(new IndexDefinition("idx_one", "column_one", true, 1));
        indexes.add(new IndexDefinition("idx_two", "column_two", true, 2));

        assertArrayEquals(converter.convertToCreateIndex().toArray(new String[0]),
            new String[] {"CREATE INDEX idx_one ON test_table (column_one)"});
    }

    @Test public void convertUniqueIndex() {
        indexes.add(new IndexDefinition("idx_one", "column_one", false, 1));

        assertArrayEquals(converter.convertToCreateIndex().toArray(new String[0]),
            new String[] {"CREATE UNIQUE INDEX idx_one ON test_table (column_one)"});
    }
}
