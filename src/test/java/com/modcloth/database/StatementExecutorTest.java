package com.modcloth.database;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.modcloth.database.managers.ConnectionManager;

@RunWith(MockitoJUnitRunner.class)
public class StatementExecutorTest {
    private StatementExecutor executor;

    @Mock private ConnectionManager manager;
    @Mock private Connection connection;
    @Mock private Statement statement;

    @Before public void setUp() throws SQLException {
        executor = new StatementExecutor(manager);
    }

    @Test public void executeStatementTest() throws SQLException {
        when(manager.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
      
        executor.executeStatement("statement");
        verify(statement).execute("statement");
        verify(manager).closeConnection(connection);
    }

    @Test public void nullConnectionTest() {
        when(manager.openConnection()).thenReturn(null);

        executor.executeStatement("statement");
        verify(manager, never()).closeConnection(connection);
    }

    @Test public void nullStatementTest() throws SQLException {
        when(manager.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenThrow(new SQLException(""));

        executor.executeStatement("statement");
        verify(statement, never()).close();
    }

    @Test public void failedStatementCreationTest() throws SQLException {
        when(manager.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenThrow(new SQLException(""));

        executor.executeStatement("statement");
        verify(manager).closeConnection(connection);
    }

    @Test public void failedStatementExecutionTest() throws SQLException {
        when(manager.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute("statement")).thenThrow(new SQLException(""));
        when(statement.isClosed()).thenReturn(false);

        executor.executeStatement("statement");
        verify(statement).close();
        verify(manager).closeConnection(connection);
    }

    @Test public void closingClosedStatementTest() throws SQLException {
        when(manager.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute("statement")).thenThrow(new SQLException(""));
        when(statement.isClosed()).thenReturn(true);
  
        executor.executeStatement("statement");
        verify(statement, never()).close();
    }

    @Test public void failedClosingStatementTest() throws SQLException {
        when(manager.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute("statement")).thenThrow(new SQLException(""));
        when(statement.isClosed()).thenReturn(false);
        doThrow(new SQLException("")).when(statement).close();

        executor.executeStatement("statement");
        verify(manager).closeConnection(connection);
    }
}
