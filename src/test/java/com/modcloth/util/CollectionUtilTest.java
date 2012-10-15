package com.modcloth.util;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CollectionUtilTest {
    private List<String> collection;
    
    @Before public void setUp() {
        collection = new LinkedList<String>();

        collection.add("one");
        collection.add("two");
        collection.add("three");
    }

    @Test public void testJoin() {
        assertEquals(CollectionUtil.join(collection, ","),
                "one,two,three");
    }

    @Test public void testNullCollection() {
        assertEquals(CollectionUtil.join(null, ","), "");
    }

    @Test public void testNullDelimiter() {
        assertEquals(CollectionUtil.join(collection, null),
            "onetwothree");
    }
}
