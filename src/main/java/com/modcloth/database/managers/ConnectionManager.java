package com.modcloth.database.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private final String driverName;
    private final String connectionUrl;

    /**
     * @param driverName the qualified class name of the appropriate database driver
     * @param connectionUrl the connection URL for the MySQL server
     */
    public ConnectionManager(String driverName, String connectionUrl) {
        this.driverName = driverName;
        this.connectionUrl = connectionUrl;
    }

    /**
     * Opens a connection to a MySQL database.
     * 
     * @return the opened connection
     */
    public Connection openConnection() {
        Connection connection = null;

        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(connectionUrl);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            try {
                if (connection != null) {
                    connection.close();
                    connection = null;
                }
            } catch (SQLException sqe) {
                System.out.println("Error: " + sqe.getMessage());
            }
        }
        return connection;
    }

    /**
     * Closes an opened database connection.
     * 
     * @param connection the connection to close
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException sqe) {
                System.out.println("Error: " + sqe.getMessage());
            }
        }
    }
}
