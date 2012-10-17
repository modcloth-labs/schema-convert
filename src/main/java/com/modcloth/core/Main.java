package com.modcloth.core;

import java.util.List;

import com.modcloth.database.MetaDataReader;
import com.modcloth.database.StatementExecutor;
import com.modcloth.database.TableDefinition;
import com.modcloth.database.managers.ConnectionManager;
import com.modcloth.database.managers.MysqlConnectionManager;
import com.modcloth.database.managers.PostgresConnectionManager;

/**
 * Main
 * 
 * @author modcloth
 *
 */
public class Main {
    /**
     * Program main
     * 
     * @param args program arguments
     */
    public static void main(String[] args) {
        OptionParser optionParser = new OptionParser();

        if (optionParser.parse(args)) {
            MysqlConnectionManager myConnectionManager = new MysqlConnectionManager(optionParser.getMysqlUrl());
            PostgresConnectionManager pgConnectionManager = new PostgresConnectionManager(optionParser.getPostgresUrl());
            MetaDataReader reader = new MetaDataReader(myConnectionManager, optionParser.getSourceDbName(), optionParser.getKeyPattern());
            List<TableDefinition> tableDefinitions = reader.read();

            if (optionParser.getTablesOnly() || (!optionParser.getTablesOnly() && !optionParser.getIndexesOnly())) {
                createTables(pgConnectionManager, tableDefinitions, optionParser.getDeleteAllTables(), optionParser.getArguments());
            }

            if(optionParser.getIndexesOnly() || (!optionParser.getTablesOnly() && !optionParser.getIndexesOnly())) {
                createTableIndexes(pgConnectionManager, tableDefinitions, optionParser.getArguments());
            }
        } else {
            System.err.println("Unable to parse arguments");
            System.exit(1);
        }
    }

    /**
     * Drops a table if it exists
     * 
     * @param connectionManager manages connections for the database in which the table will be dropped
     * @param name the name of the table to drop
     */
    public static void dropTable(ConnectionManager connectionManager, String name) {
        new StatementExecutor(connectionManager).executeStatement("DROP TABLE IF EXISTS " + name);
    }

    /**
     * Create the given tables
     * 
     * @param connectionManager manages the connection to the database for which the tables will be created
     * @param tableDefinitions collection of the table definitions which will be used to generate the tables
     * @param deleteAllTables flag indicating whether all existing tables in the database should be dropped
     * @param tableNames list of the table names that will be created
     */
    public static void createTables(ConnectionManager connectionManager, List<TableDefinition> tableDefinitions,
            boolean deleteAllTables, List<String> tableNames) {

        for (TableDefinition t: tableDefinitions) {
            if (deleteAllTables) {
                dropTable(connectionManager, t.getName());
            }

            if (tableNames.contains(t.getName())) {
                if (!deleteAllTables) {
                    dropTable(connectionManager, t.getName());
                }
                new StatementExecutor(connectionManager).executeStatement(t.toPostgresCreateSyntax());
            }
        }
    }

    /**
     * Create the indexes on all given tables.
     * 
     * @param connectionManager manages the connection to the database for which indexes will be created
     * @param tableDefinitions definitions of the tables for which indexes will be created
     */
    public static void createTableIndexes(ConnectionManager connectionManager, List<TableDefinition> tableDefinitions,
            List<String> tableNames) {

        for (TableDefinition t: tableDefinitions) {
            if (tableNames.contains(t.getName())) {
                for (String s : t.toPostgresIndexSyntax()) {
                    new StatementExecutor(connectionManager).executeStatement(s);
                }
            }
        }
    }
}
