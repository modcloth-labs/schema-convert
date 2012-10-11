package com.modcloth.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.modcloth.database.TableDefinition.ColumnDefinition;
import com.modcloth.database.TableDefinition.IndexDefinition;
import com.modcloth.database.managers.ConnectionManager;

/**
 * Reads the metadata for a given database using standard SQL constructs.
 * Stores the metadata as a generic list of TableDefinitions.
 * 
 * @author modcloth
 *
 */
public class MetaDataReader {
    private final ConnectionManager connectionManager;
    private final String dbName;
    private final String surrogateKeyPattern;

    /**
     * @param connectionManager the manager from which the connection to the database will be retrieved
     * @param dbName the name of the database from which the metadata will be read
     * @param surrogateKeyPattern the pattern with which to check for surrogate keys
     */
    public MetaDataReader(ConnectionManager connectionManager, String dbName, String surrogateKeyPattern) {
        this.connectionManager = connectionManager;
        this.dbName = dbName;
        this.surrogateKeyPattern = surrogateKeyPattern;
    }

    /**
     * Reads the table definitions for all tables in the object's database and maps
     * them to TableDefinitions.
     * 
     * @return a list of TableDefinitions representing all tables in the object's database
     */
    public List<TableDefinition> read() {
        final List<TableDefinition> tableDefinitions = new LinkedList<TableDefinition>();
        Connection connection = connectionManager.openConnection();

        if (connection != null) {
            DatabaseMetaData metaData = null;
            ResultSet resultSet = null;

            try {
                metaData = connection.getMetaData();
                resultSet = metaData.getTables(dbName, null, null, null);

                while (resultSet.next()) {
                    tableDefinitions.add(new TableDefinitionReader(metaData,
                            resultSet.getString("TABLE_NAME")).read());
                }
            } catch (SQLException sqe) {
                System.out.println("Error: " + sqe.getMessage());
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                } catch (SQLException sqe1) {
                    System.out.println("Error: " + sqe1.getMessage());
                }
            }
            connectionManager.closeConnection(connection);
        }
        return tableDefinitions;
    }

    /**
     * Reads the table structure of a given table and maps it to a TableDefinition. 
     * 
     * @author modcloth
     *
     */
    private class TableDefinitionReader {
        private final DatabaseMetaData metaData;
        private final String name;

        /**
         * @param metaData the metadata object from which the table structure will be read
         * @param name the name of the table for which the structure will be read
         */
        public TableDefinitionReader(DatabaseMetaData metaData, String name) {
            this.metaData = metaData;
            this.name = name;
        }

        /**
         * Reads the table definitions for a table in the object's database and maps
         * it to a TableDefinition.
         * 
         * @return a TableDefinition representing a table in the object's database
         */
        public TableDefinition read() {
            final TableDefinition tableDefinition = new TableDefinition(name, surrogateKeyPattern);
            ResultSet resultSet = null;

            try {
                resultSet = metaData.getColumns(dbName, null, name, null);
                while (resultSet.next()) {
                    tableDefinition.addColumnDefinition(new ColumnDefinition(
                            resultSet.getString("COLUMN_NAME"),
                            resultSet.getInt("DATA_TYPE"),
                            resultSet.getString("IS_NULLABLE"),
                            resultSet.getInt("COLUMN_SIZE"),
                            resultSet.getInt("DECIMAL_DIGITS"),
                            resultSet.getString("COLUMN_DEF"),
                            resultSet.getString("IS_AUTOINCREMENT")));
                }
                resultSet = metaData.getIndexInfo(dbName, null, name, false, true);
                while (resultSet.next()) {
                    tableDefinition.addIndexDefinition(new IndexDefinition(
                            resultSet.getString("INDEX_NAME"),
                            resultSet.getString("COLUMN_NAME"),
                            resultSet.getBoolean("NON_UNIQUE"),
                            resultSet.getInt("ORDINAL_POSITION")));
                }
            } catch (SQLException sqe) {
                System.out.println("Error: " + sqe.getMessage());
            }
            return tableDefinition;
        }
    }
}

