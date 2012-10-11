package com.modcloth.core;

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

            for (TableDefinition t: reader.read()) {
                if (optionParser.getDeleteAllTables()) {
                    dropTable(pgConnectionManager, t.getName());
                }

                if (optionParser.getArguments().contains(t.getName())) {
                    if (!optionParser.getDeleteAllTables()) {
                        dropTable(pgConnectionManager, t.getName());
                    }
                    new StatementExecutor(pgConnectionManager).executeStatement(t.toPostgresCreateSyntax());
                    for (String s : t.toPostgresIndexSyntax()) {
                        new StatementExecutor(pgConnectionManager).executeStatement(s);
                    }
                }
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
}
