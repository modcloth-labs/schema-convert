package com.modcloth.converters;

import java.sql.Types;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.modcloth.database.TableDefinition;
import com.modcloth.database.TableDefinition.ColumnDefinition;
import com.modcloth.database.TableDefinition.IndexDefinition;


/**
 * Uses a TableDefinition to generate SQL a 'CREATE TABLE' statement for a
 * PostgreSQL database.
 * 
 * Attempts to match standard SQL Types to appropriate PostgreSQL types.
 * Caveats: Converts MySQL TINYINT to PostgreSQL SMALLINT (in some cases it may
 * be more appropriate to use a BOOLEAN type).
 * 
 * @author modcloth
 *
 */
public class PostgresTableConverter {
    private final TableDefinition tableDefinition;

    /**
     * @param tableDefinition the TableDefinition from which the 'CREATE TABLE'
     * statement will be generated
     */
    public PostgresTableConverter(TableDefinition tableDefinition) {
        this.tableDefinition = tableDefinition;
    }

    /**
     * Converts the object's TableDefinition into the corresponding SQL
     * statements used to create the given table structure in PostgreSQL.
     * 
     * @return The SQL statement to create the table represented by the TableDefinition
     */
    public String convertToCreateTable() {
        final StringBuilder statement = new StringBuilder();
        final List<String> createStatements = new LinkedList<String>();

        statement.append("CREATE TABLE ").append(tableDefinition.getName()).append("(\n");
        for (ColumnDefinition cd : tableDefinition.getColumnDefinitions()) {
            createStatements.add(new PostgresColumnConverter(cd).convert());
        }
        statement.append(StringUtils.join(createStatements, ",\n"));
        return statement.append(")\n").toString();
    }

    /**
     * Converts the object's TableDefinition into the corresponding SQL
     * statements used to create the indexes on the table in PostgreSQL.
     * 
     * @return the SQL statements to create the indexes on the table
     */
    public List<String> convertToCreateIndex() {
        final List<String> createStmts = new LinkedList<String>();
        final Map<String, List<IndexDefinition>> indexes = tableDefinition.getIndexesByName();

        for (String i : indexes.keySet()) {
            if (i.equals("PRIMARY")) {
                createStmts.add(createPrimaryKeyStatement(tableDefinition, indexes.get(i)));
            } else {
                createStmts.add(createIndexStatement(tableDefinition, indexes.get(i)));
            }
        }
        return createStmts;
    }

    /**
     * Build the syntactically-correct ALTER TABLE statement to add a PRIMARY KEY
     * to the given table in PostgreSQL.
     * 
     * @param tableDefinition represents the schema definition of the table
     * @param indexDefinitions the the list of schema definitions of the primary index
     * @return the SQL statement to create the primary key
     */
    private String createPrimaryKeyStatement(TableDefinition tableDefinition, List<IndexDefinition> indexDefinitions) {
        StringBuilder stmt = new StringBuilder("ALTER TABLE ");
        List<String> columnNames = new LinkedList<String>();

        for (IndexDefinition i : indexDefinitions) {
            columnNames.add(i.getColumnName());
        }
        stmt.append(tableDefinition.getName()).append(" ADD PRIMARY KEY (").
                append(StringUtils.join(columnNames, ',')).append(")");
        return stmt.toString();
    }

    /**
     * Build the syntactically-correct CREATE INDEX statement to add an index
     * to a column in the given table in PostgreSQL.
     * 
     * @param tableDefinition represents the schema definition of the table
     * @param indexDefinitions the list of schema definitions of the index
     * @return the SQL statement to create the index
     */
    private String createIndexStatement(TableDefinition tableDefinition, List<IndexDefinition> indexDefinitions) {
        StringBuilder stmt = new StringBuilder("CREATE ");
        List<String> columnNames = new LinkedList<String>();

        for (IndexDefinition i : indexDefinitions) {
            columnNames.add(i.getColumnName());
        }

        if (indexDefinitions.get(0).getIsUnique()) {
            stmt.append("UNIQUE ");
        }
        stmt.append("INDEX ").append(indexDefinitions.get(0).getName()).append(" ON ").append(tableDefinition.getName()).
                append(" (").append(StringUtils.join(columnNames, ',')).append(")");
        return stmt.toString();
    }

    /**
     * Converts to a Postgres Column
     * 
     * @author modcloth
     */
    private class PostgresColumnConverter {
        private final ColumnDefinition columnDefinition;

        public PostgresColumnConverter(ColumnDefinition columnDefinition) {
            this.columnDefinition = columnDefinition;
        }

        /**
         * Converts the object's ColumnDefinition into the corresponding SQL
         * statements used to create the given column structure in PostgreSQL.
         * 
         * @return The SQL statement to create the column represented by the ColumnDefinition
         */
        public String convert() {
            final StringBuilder definition = new StringBuilder(columnDefinition.getName());

            if (columnDefinition.getIsAutoIncrement()) {
                definition.append(" SERIAL");
            } else {
                definition.append(" ").append(typeToString()).append(sizeToString());
                definition.append(isNullableToString()).append(defaultToString());
            }
            return definition.toString();
        }

        /**
         * Convert the type of the column to it's textual representation.
         * 
         * @return the textual representation of the column's type
         */
        private String typeToString() {
            String typeName = SqlTypeConverter.typeToString(columnDefinition.getType());

            if (typeName != null && (typeName.equals("BIT") || typeName.equals("TINYINT"))) {
                typeName = "BOOLEAN";
            } else if (typeName != null && typeName.equals("DOUBLE")) {
                typeName = "FLOAT8";
            } else if (typeName != null && typeName.equals("LONGVARCHAR")) {
                typeName = "TEXT";
            }
            return typeName;
        }

        /**
         * Convert the size of the column to it's textual representation.
         * 
         * @return the textual representation of the column's data size
         */
        private String sizeToString() {
            final int type = columnDefinition.getType();
            String result = "";

            if (type == Types.CHAR || type == Types.VARCHAR) {
                result = "(" + columnDefinition.getColumnSize() + ")";
            } else if (type == Types.DECIMAL) {
                result = "(" + columnDefinition.getColumnSize() + "," +
                        columnDefinition.getDecimalDigits()+ ")";
            }
            return result;
        }

        /**
         * Convert the nullability of the column to it's textual representation.
         * 
         * @return the textual representation of the column's nullability
         */
        private String isNullableToString() {
            return columnDefinition.getIsNullable() ? "" : " NOT NULL";
        }

        /**
         * Convert the default value of the column to it's textual representation.
         * 
         * @return the textual representation of the column's default value
         */
        private String defaultToString() {
            final String defaultValue = columnDefinition.getDefaultValue();

            return defaultValue == null ? "" : " DEFAULT '" + defaultValue + "'";
        }
    }
}
