package com.modcloth.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class OptionParserTest {
    private OptionParser parser;
    private List<String> arguments;

    @Before public void setUp() {
        parser = new OptionParser();
        arguments = new LinkedList<String>();

        arguments.add("--my-url");
        arguments.add("mysql_url");
        arguments.add("--pg-url");
        arguments.add("pgsql_url");
        arguments.add("--my-db");
        arguments.add("mod_dw");
        arguments.add("--key-pattern");
        arguments.add("^id$");
        arguments.add("--use-small-ints");
        arguments.add("--delete-tables");
        arguments.add("table_one");
        arguments.add("table_two");
    }

    @Test public void testMyUrl() {
        boolean result = parser.parse(arguments.toArray(new String[0]));

        assertEquals(parser.getMysqlUrl(), "mysql_url");
        assertTrue(result);
    }

    @Test public void testPgUrl() {
        boolean result = parser.parse(arguments.toArray(new String[0]));

        assertEquals(parser.getPostgresUrl(), "pgsql_url");
        assertTrue(result);
    }

    @Test public void testMyDb() {
        boolean result = parser.parse(arguments.toArray(new String[0]));

        assertEquals(parser.getSourceDbName(), "mod_dw");
        assertTrue(result);
    }

    @Test public void testKeyPattern() {
        boolean result = parser.parse(arguments.toArray(new String[0]));

        assertEquals(parser.getKeyPattern(), "^id$");
        assertTrue(result);
    }

    @Test public void testDeleteTables() {
        parser.parse(arguments.toArray(new String[0]));

        assertTrue(parser.getDeleteAllTables());
    }

    @Test public void testDefaultDeleteTables() {
        arguments.remove("--delete-tables");
        parser.parse(arguments.toArray(new String[0]));

        assertFalse(parser.getDeleteAllTables());
    }

    @Test public void testArguments() {
        boolean result = parser.parse(arguments.toArray(new String[0]));

        assertArrayEquals(parser.getArguments().toArray(new String[0]),
                new String[] { "table_one", "table_two" });
        assertTrue(result);
    }
}
