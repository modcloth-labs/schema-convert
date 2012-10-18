package com.modcloth.database;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.modcloth.database.TableDefinition.ColumnDefinition;
import com.modcloth.database.TableDefinition.IndexDefinition;

public class TableDefinitionTest {
    private TableDefinition table;
    private IndexDefinition idxOne;
    private IndexDefinition idxTwo;
    private IndexDefinition idxThree;

    @Before public void setUp() {
        table = new TableDefinition("table_one", ".*_sk$");

        table.addColumnDefinition(
            new ColumnDefinition("c_one", Types.INTEGER, "NO", 0, 0, "", "NO"));
        table.addColumnDefinition(
            new ColumnDefinition("table_sk", Types.INTEGER, "NO", 0, 0, "", "NO"));
        table.addColumnDefinition(
            new ColumnDefinition("c_two", Types.VARCHAR, "NO", 10, 0, "", "NO"));

        idxOne = new IndexDefinition("idx_one", "c_one", true, 1);
        idxTwo = new IndexDefinition("idx_two", "c_one", false, 1);
        idxThree = new IndexDefinition("idx_two", "c_two", false, 2);

        table.addIndexDefinition(idxOne);
        table.addIndexDefinition(idxTwo);
        table.addIndexDefinition(idxThree);
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

    @Test public void gettingUnsortedMultiColumnIndexes() {
        List<IndexDefinition> indexes = table.getIndexAsMultiColumnIndex("idx_two");

        assertTrue(indexes.contains(idxTwo));
        assertTrue(indexes.contains(idxThree));
    }

    @Test public void gettingSingleColumnMultiColumnIndex() {
        List<IndexDefinition> indexes = table.getIndexAsMultiColumnIndex("idx_one");

        assertTrue(indexes.contains(idxOne));
    }

    @Test public void nonMatchingMultiColumnIndexName() {
        List<IndexDefinition> indexes = table.getIndexAsMultiColumnIndex("idx_five");

        assertTrue(indexes.isEmpty());
    }

    @Test public void gettingSortedMultiColumnIndexes() {
        List<IndexDefinition> indexes = table.getIndexAsMultiColumnIndex("idx_two");

        assertArrayEquals(new IndexDefinition[] { idxTwo, idxThree }, indexes.toArray(new IndexDefinition[0]));
    }

    @Test public void gettingIndexesByName() {
        Map<String, List<IndexDefinition>> indexes = table.getIndexesByName();

        assertTrue(indexes.keySet().contains("idx_one"));
        assertTrue(indexes.keySet().contains("idx_two"));
        assertArrayEquals(new IndexDefinition[] { idxOne },
            indexes.get("idx_one").toArray(new IndexDefinition[0]));
        assertArrayEquals(new IndexDefinition[] { idxTwo, idxThree },
            indexes.get("idx_two").toArray(new IndexDefinition[0]));
    }
}
