package com.modcloth.database;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.modcloth.converters.PostgresTableConverter;

/**
 * Container for storing a standard SQL table definition.
 * 
 * @author modcloth
 *
 */
public class TableDefinition {
    private final String name;
    private final String surrogateKeyPattern;
    private final List<ColumnDefinition> columnDefinitions;
    private final List<IndexDefinition> indexDefinitions;

    /**
     * @param name the name of the table
     * @param surrogateKeyPattern the pattern with which to match potential surrogate keys
     */
    public TableDefinition(String name, String surrogateKeyPattern) {
        this.name = name;
        this.surrogateKeyPattern = surrogateKeyPattern;
        this.columnDefinitions = new LinkedList<ColumnDefinition>();
        this.indexDefinitions = new LinkedList<IndexDefinition>();
    }

    /**
     * @return the table's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the list of objects representing the columns in the table
     */
    public List<ColumnDefinition> getColumnDefinitions() {
        return columnDefinitions;
    }

    /**
     * @return the list of objects representing the indexes in the table
     */
    public List<IndexDefinition> getIndexDefinitions() {
        return indexDefinitions;
    }

    /**
     * Builds the list of indexes into a map containing the index name as a key
     * and the list of index-column sequences as the value.
     * 
     * @return the map of indexes
     */
    public Map<String, List<IndexDefinition>> getIndexesByName() {
        Map<String, List<IndexDefinition>> indexes = new HashMap<String, List<IndexDefinition>>();

        for (IndexDefinition i : getIndexDefinitions()) {
            if (i.getName() != null) {
                indexes.put(i.getName(), getIndexAsMultiColumnIndex(i.getName()));
            }
        }
        return indexes;
    }

    /**
     * Returns the list of IndexDefinitions that match the given name. If no IndexDefinition is found
     * that matches the indexName, an empty list is returned. If multiple IndexDefinitions are found
     * that match the indexName, they are returned sorted by their sequence number.
     * 
     * @param indexName the name of the index for which to search
     * @return the sorted list of IndexDefinitions
     */
    public List<IndexDefinition> getIndexAsSortedMultiColumnIndex(String indexName) {
        List<IndexDefinition> multiColumnIndex = getIndexAsMultiColumnIndex(indexName);

        Collections.sort(multiColumnIndex, new Comparator<IndexDefinition>() {
            public int compare(IndexDefinition left, IndexDefinition right) {
                if (left.getSequenceNumber() == right.getSequenceNumber()) {
                    return 0;
                }
                return left.getSequenceNumber() < right.getSequenceNumber() ? -1 : 1;
            }
        });

        return multiColumnIndex;
    }

    /**
     * Returns the list of IndexDefinitions that match the given name. If no IndexDefinition is found
     * that matches the indexName, returns an empty list. The order of the returned IndexDefinitions is
     * not guaranteed.
     * 
     * @param indexName the name of the index for which to search
     * @return the list of IndexDefinitions
     */
    public List<IndexDefinition> getIndexAsMultiColumnIndex(String indexName) {
        List<IndexDefinition> multiColumnIndex = new LinkedList<IndexDefinition>();

        for (IndexDefinition i : getIndexDefinitions()) {
            if (i.getName() != null && i.getName().equals(indexName)) {
                multiColumnIndex.add(i);
            }
        }
        return multiColumnIndex;
    }

    /**
     * Returns the table definition as its PostgreSQL-compatible SQL syntax.
     *
     * @return the SQL string for creating the table
     */
    public String toPostgresCreateSyntax() {
        return new PostgresTableConverter(this).convertToCreateTable();
    }

    /**
     * Returns the index definitions as there PostgreSQL-compatible SQL syntax.
     * 
     * @return The SQL strings for adding indexes to the table
     */
    public List<String> toPostgresIndexSyntax() {
        return new PostgresTableConverter(this).convertToCreateIndex();
    }

    /**
     * Returns the name of the surrogate key of the table. 
     * 
     * @return the name of the surrogate key
     */
    public String getSurrogateKeyName() {
        String keyName = null;

        for (ColumnDefinition cd : columnDefinitions) {
            if (Pattern.matches(surrogateKeyPattern, cd.getName())) {
                keyName = cd.getName();
            }
        }
        return keyName;
    }

    /**
     * Returns the name of the columns in the table.
     * 
     * @return a list of column names
     */
    public List<String> getColumnNames() {
        final List<String> names = new LinkedList<String>();

        for (ColumnDefinition cd : columnDefinitions) {
            names.add(cd.getName());
        }
        return names;
    }

    /**
     * Adds a column definition to the set of columns in the current table object.
     * 
     * @param columnDefinition the definition to be added to the table definition
     */
    protected void addColumnDefinition(ColumnDefinition columnDefinition) {
        this.columnDefinitions.add(columnDefinition);
    }

    /**
     * Adds an index definition to the set of indexes in the current table object.
     *
     * @param indexDefinition the definition to be added to the table definition
     */
    protected void addIndexDefinition(IndexDefinition indexDefinition) {
        if (indexDefinition.getColumnName() != null) {
          this.indexDefinitions.add(indexDefinition);
        }
    }

    /**
     * Container class to represent a standard SQL column definition.
     * 
     * @author modcloth
     *
     */
    public static class ColumnDefinition {
        private final String name;
        private final int type;
        private final boolean isNullable;
        private final int columnSize;
        private final int decimalDigits;
        private final String defaultValue;
        private final boolean isAutoIncrement;

        /**
         * @param name the name of the column
         * @param type the type of the column
         * @param nullable the nullability of the column
         * @param columnSize the size of the column, if any
         * @param decimalDigits the number of decimal digits held by the column, if any
         * @param defaultValue the default value of the column, if any
         * @param autoincrement the autoincrement flag for the column
         */
        public ColumnDefinition(String name, int type, String nullable, int columnSize,
                int decimalDigits, String defaultValue, String autoincrement) {
            this.name = name;
            this.type = type;
            this.isNullable = convertFlagToBoolean(nullable);
            this.columnSize = columnSize;
            this.decimalDigits = decimalDigits;
            this.defaultValue = defaultValue;
            this.isAutoIncrement = convertFlagToBoolean(autoincrement);
        }

        /**
         * @return the column's name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the column's type
         */
        public int getType() {
            return type;
        }

        /**
         * @return the column's nullability
         */
        public boolean getIsNullable() {
            return isNullable;
        }

        /**
         * @return the column's size
         */
        public int getColumnSize() {
            return columnSize;
        }

        /**
         * @return the column's decimal digits
         */
        public int getDecimalDigits() {
            return decimalDigits;
        }

        /**
         * @return the column's default value
         */
        public String getDefaultValue() {
            String tempDefault = null;

            if (defaultValue != null && !defaultValue.equals("")) {
                tempDefault = defaultValue;
            }
            return tempDefault;
        }

        /**
         * @return true if the column is auto increment, false if it is not
         */
        public boolean getIsAutoIncrement() {
            return isAutoIncrement;
        }

        /**
         * Converts a 'YES' or 'NO' flag into a boolean value.
         * 
         * @param flag the 'YES'/'NO' flag to be converted
         * @return true if the flag is 'YES', false if it is 'NO'
         */
        private boolean convertFlagToBoolean(String flag) {
            boolean result = false;

            if (flag != null && flag.toUpperCase().equals("YES")) {
                result = true;
            }
            return result;
        }
    }

    /**
     * Container class to represent a standard SQL index definition.
     * 
     * @author modcloth
     *
     */
    public static class IndexDefinition {
        private final String name;
        private final String columnName;
        private final boolean isUnique;
        private final Integer sequenceNumber;

        /**
         * @param name the name of the index
         * @param columnName the name of the column that the index covers
         * @param isUnique flag indication if the index is unique
         * @param sequenceNumber column number of multi-column indexes
         */
        public IndexDefinition(String name, String columnName, boolean nonUnique,
                Integer sequenceNumber) {
            this.name = name;
            this.columnName = columnName;
            this.isUnique = !nonUnique;
            this.sequenceNumber = sequenceNumber;
        }

        /**
         * @return the index name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the column name for the index
         */
        public String getColumnName() {
            return columnName;
        }

        /**
         * @return the uniqueness of the index
         */
        public boolean getIsUnique() {
            return isUnique;
        }

        /**
         * @return the sequence number of the index
         */
        public Integer getSequenceNumber() {
          return sequenceNumber;
        }
    }
}

