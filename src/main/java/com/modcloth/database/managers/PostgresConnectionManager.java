package com.modcloth.database.managers;


/**
 * Manages Connections to a PostgreSQL database.
 * 
 * @author modcloth
 *
 */
public class PostgresConnectionManager extends ConnectionManager {
    /**
     * @param connectionUrl the connection URL for the PostgreSQL server
     */
    public PostgresConnectionManager(String connectionUrl) {
        super("org.postgresql.Driver", connectionUrl);
    }
}
