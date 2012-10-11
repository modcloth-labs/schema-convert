package com.modcloth.converters;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for converting integral SQL types supported
 * natively by Java to their textual representations.
 * 
 * @author modcloth
 *
 */
public class SqlTypeConverter {
    public final static Map<Integer, String> typeMap = initTypeMap();

    /**
     * Convert the given integral type to it's SQL name.
     * 
     * @param type the integral representation of the SQL type
     * @return the string representation of the SQL type
     */
    public static String typeToString(int type) {
        return typeMap.get(type);
    }

    /**
     * Creates the type map that links integer types with their
     * corresponding names.
     * 
     * @return the initialized type map
     */
    private static Map<Integer, String> initTypeMap() {
        final Map<Integer, String> map = new HashMap<Integer, String>();

        try {
            Field[] fields = Types.class.getFields();

            for (int i = 0; i < fields.length; i++) {
                map.put((Integer) fields[i].get(null), fields[i].getName());
            }
        } catch (IllegalAccessException iae) {
            System.out.println("Error: " + iae.getMessage());
        }
        return map;
    }
}
