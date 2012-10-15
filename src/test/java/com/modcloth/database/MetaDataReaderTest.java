package com.modcloth.database;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.modcloth.database.managers.ConnectionManager;

@RunWith(MockitoJUnitRunner.class)
public class MetaDataReaderTest {
    private MetaDataReader reader;

    @Mock private ConnectionManager manager;
    @Mock private Connection connection;
    @Mock private DatabaseMetaData metaData;
    @Mock private ResultSet tableRs;

    @Mock private ResultSet tbRs1;
    @Mock private ResultSet tbRs2;

    @Mock private ResultSet inRs1;
    @Mock private ResultSet inRs2;

    @Before public void setUp() {
        reader = new MetaDataReader(manager, "db_name", "^t1_c1$");
    }

    @Test public void testEndToEndConvert() throws SQLException {
        when(manager.openConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getTables("db_name", null, null, null)).thenReturn(tableRs);
        when(tableRs.next()).thenReturn(true, true, false);
        when(tableRs.getString("TABLE_NAME")).thenReturn("tb_1", "tb_2");

        // read list of tables
        when(metaData.getColumns("db_name", null, "tb_1", null)).thenReturn(tbRs1);
        when(metaData.getColumns("db_name", null, "tb_2", null)).thenReturn(tbRs2);

        when(metaData.getIndexInfo("db_name", null, "tb_1", false, true)).thenReturn(inRs1);
        when(metaData.getIndexInfo("db_name", null, "tb_2", false, true)).thenReturn(inRs2);

        // read first table
        when(tbRs1.next()).thenReturn(true, true, false);
        when(tbRs1.getString("COLUMN_NAME")).thenReturn("t1_c1", "t1_c2");
        when(tbRs1.getInt("DATA_TYPE")).thenReturn(Types.INTEGER, Types.VARCHAR);
        when(tbRs1.getString("IS_NULLABLE")).thenReturn("NO", "YES");
        when(tbRs1.getInt("COLUMN_SIZE")).thenReturn(0, 20);
        when(tbRs1.getInt("DECIMAL_DIGITS")).thenReturn(0, 0);
        when(tbRs1.getString("COLUMN_DEF")).thenReturn("", "");
        when(tbRs1.getString("IS_AUTOINCREMENT")).thenReturn("YES", "NO");

        // read second table
        when(tbRs2.next()).thenReturn(true, true, true, false);
        when(tbRs2.getString("COLUMN_NAME")).thenReturn("t2_c1", "t2_c2", "t2_c3");
        when(tbRs2.getInt("DATA_TYPE")).thenReturn(Types.INTEGER, Types.TINYINT, Types.DECIMAL);
        when(tbRs2.getString("IS_NULLABLE")).thenReturn("NO", "NO", "NO");
        when(tbRs2.getInt("COLUMN_SIZE")).thenReturn(0, 0, 8);
        when(tbRs2.getInt("DECIMAL_DIGITS")).thenReturn(0, 0, 2);
        when(tbRs2.getString("COLUMN_DEF")).thenReturn("", "", "0.0");
        when(tbRs2.getString("IS_AUTOINCREMENT")).thenReturn("NO", "NO", "NO");

        // read first table indexes
        when(inRs1.next()).thenReturn(true, false);
        when(inRs1.getString("INDEX_NAME")).thenReturn("PRIMARY");
        when(inRs1.getString("COLUMN_NAME")).thenReturn("t1_c1");
        when(inRs1.getBoolean("NON_UNIQUE")).thenReturn(true);
        when(inRs1.getInt("ORDINAL_POSITION")).thenReturn(1);
        
        // read second table indexes
        when(inRs2.next()).thenReturn(true, false);
        when(inRs2.getString("INDEX_NAME")).thenReturn("t2_in1");
        when(inRs2.getString("COLUMN_NAME")).thenReturn("t2_c1");
        when(inRs2.getBoolean("NON_UNIQUE")).thenReturn(false);
        when(inRs2.getInt("ORDINAL_POSITION")).thenReturn(1);

        List<String> creates = new LinkedList<String>();
        List<String> indexes = new LinkedList<String>();
        for (TableDefinition t: reader.read()) {
            creates.add(t.toPostgresCreateSyntax().replaceAll("\\n", ""));
            indexes.addAll(t.toPostgresIndexSyntax());
        }

        assertArrayEquals(creates.toArray(new String[0]),
            new String[] {
                "CREATE TABLE tb_1(t1_c1 SERIAL,t1_c2 VARCHAR(20))",
                "CREATE TABLE tb_2(t2_c1 INTEGER NOT NULL,t2_c2 BOOLEAN NOT NULL,t2_c3 DECIMAL(8,2) NOT NULL DEFAULT '0.0')"
            });

        assertArrayEquals(indexes.toArray(new String[0]),
            new String[] {
                "ALTER TABLE tb_1 ADD PRIMARY KEY (t1_c1)",
                "CREATE UNIQUE INDEX t2_in1 ON tb_2 (t2_c1)"
        });
    }
}
