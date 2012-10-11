package com.modcloth.database.managers;


/**
 * Manages Connections to a MySQL database.
 * 
 * @author modcloth
 *
 */
public class MysqlConnectionManager extends ConnectionManager {
    /**
     * @param connectionUrl the connection URL for the MySQL server
     */
    public MysqlConnectionManager(String connectionUrl) {
        super("com.mysql.jdbc.Driver", connectionUrl);
    }
}
