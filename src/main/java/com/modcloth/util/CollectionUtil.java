package com.modcloth.util;

import java.util.Collection;

/**
 * Utilities and convenience methods for processing collections of objects.
 * 
 * @author modcloth
 *
 */
public class CollectionUtil {
    /**
     * Returns a concatenation of the objects in the collection, separated by the given delimiter.
     * Converts the objects to their string representations by calling toString() on each object.
     * 
     * @param collection the collection of objects to join
     * @param delimiter the delimiter with which the collection will be joined
     * @return the joined string of objects
     */
    public static String join(Collection<? extends Object> collection, String delimiter) {
        final StringBuilder result = new StringBuilder();

        if (collection != null) {
            int counter = 0;

            for (Object o : collection) {
                result.append(o.toString());
                if (counter++ < (collection.size() - 1)) {
                    result.append(delimiter);
                }
            }
        }
        return result.toString();
    }
}
