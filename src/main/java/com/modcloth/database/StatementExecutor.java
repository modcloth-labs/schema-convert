package com.modcloth.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.modcloth.database.managers.ConnectionManager;

/**
 * Executes a SQL statement against the connection provided by the
 * classes ConnectionManager.
 * 
 * @author modcloth
 *
 */
public class StatementExecutor {
    final private ConnectionManager connectionManager;

    /**
     * @param connectionManager the manager from which the connection for the
     * statement will be retrieved
     */
    public StatementExecutor(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Executes the given SQL statement against the database connection
     * provided by the object's ConnectionManager.
     * 
     * @param sqlStatement the SQL string to be executed
     */
    public void executeStatement(String sqlStatement) {
        Connection connection = connectionManager.openConnection();
        Statement statement = null;

        if (connection != null) {
            try {
                statement = connection.createStatement();
                statement.execute(sqlStatement);
            } catch (SQLException sqe) {
                System.out.println("Error: " + sqe.getMessage());
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException sqe1) {
                        System.out.println("Error: " + sqe1.getMessage());
                    }
                }
            } finally {
                connectionManager.closeConnection(connection);
            }
        }
    }
}
