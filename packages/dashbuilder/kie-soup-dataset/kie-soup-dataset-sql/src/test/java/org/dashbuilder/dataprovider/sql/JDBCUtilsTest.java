/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.dataprovider.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.StringReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.dashbuilder.dataprovider.sql.model.Column;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.def.SQLDataSourceDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JDBCUtilsTest {

    @Mock
    Connection connection;
    
    @Mock
    Statement statement;

    @Mock
    ResultSet resultSet;
    
    @Mock
    ResultSetMetaData metaData;
    
    @Mock
    Clob clob;
    
    @Before
    public void setUp() throws Exception {
        when(connection.createStatement()).thenReturn(statement);
        when(resultSet.getMetaData()).thenReturn(metaData);
    }
    
    @Test
    public void testStatementClose() throws Exception {
        JDBCUtils.execute(connection, "sql");
        verify(statement).execute("sql");
        verify(statement).close();
    }    

    @Test
    public void testListDataSourceDefs() throws Exception {
        List<SQLDataSourceDef> defList = JDBCUtils.listDatasourceDefs();
        assertEquals(defList.size(), 0);
    }

    @Test
    public void testFixSQLCase() throws Exception {
        String sql = "SELECT \"ID\" FROM TABLE";
        String fix = JDBCUtils.changeCaseExcludeQuotes(sql, false);
        assertEquals(fix, "select \"ID\" from table");
    }

    @Test
    public void testSupportedTypes() throws Exception {
        when(metaData.getColumnCount()).thenReturn(35);
        when(metaData.getColumnType(1)).thenReturn(Types.CHAR);
        when(metaData.getColumnType(2)).thenReturn(Types.VARCHAR);
        when(metaData.getColumnType(3)).thenReturn(Types.NCHAR);
        when(metaData.getColumnType(4)).thenReturn(Types.NVARCHAR);
        when(metaData.getColumnType(5)).thenReturn(Types.BIT);
        when(metaData.getColumnType(6)).thenReturn(Types.BOOLEAN);
        when(metaData.getColumnType(7)).thenReturn(Types.LONGVARCHAR);
        when(metaData.getColumnType(8)).thenReturn(Types.LONGNVARCHAR);
        when(metaData.getColumnType(9)).thenReturn(Types.TINYINT);
        when(metaData.getColumnType(10)).thenReturn(Types.BIGINT);
        when(metaData.getColumnType(11)).thenReturn(Types.INTEGER);
        when(metaData.getColumnType(12)).thenReturn(Types.DECIMAL);
        when(metaData.getColumnType(13)).thenReturn(Types.DOUBLE);
        when(metaData.getColumnType(14)).thenReturn(Types.FLOAT);
        when(metaData.getColumnType(15)).thenReturn(Types.NUMERIC);
        when(metaData.getColumnType(16)).thenReturn(Types.REAL);
        when(metaData.getColumnType(17)).thenReturn(Types.SMALLINT);
        when(metaData.getColumnType(18)).thenReturn(Types.DATE);
        when(metaData.getColumnType(19)).thenReturn(Types.TIME);
        when(metaData.getColumnType(20)).thenReturn(Types.TIMESTAMP);
        when(metaData.getColumnType(21)).thenReturn(Types.VARBINARY);
        when(metaData.getColumnType(22)).thenReturn(Types.LONGVARBINARY);
        when(metaData.getColumnType(23)).thenReturn(Types.NULL);
        when(metaData.getColumnType(24)).thenReturn(Types.OTHER);
        when(metaData.getColumnType(25)).thenReturn(Types.JAVA_OBJECT);
        when(metaData.getColumnType(26)).thenReturn(Types.DISTINCT);
        when(metaData.getColumnType(27)).thenReturn(Types.STRUCT);
        when(metaData.getColumnType(28)).thenReturn(Types.ARRAY);
        when(metaData.getColumnType(29)).thenReturn(Types.BLOB);
        when(metaData.getColumnType(30)).thenReturn(Types.CLOB);
        when(metaData.getColumnType(31)).thenReturn(Types.REF);
        when(metaData.getColumnType(32)).thenReturn(Types.ROWID);
        when(metaData.getColumnType(33)).thenReturn(Types.SQLXML);
        when(metaData.getColumnType(34)).thenReturn(Types.DATALINK);

        List<Column> columns = JDBCUtils.getColumns(resultSet, null);
        assertEquals(columns.size(), 21);
        assertEquals(columns.get(0).getType(), ColumnType.LABEL);
        assertEquals(columns.get(1).getType(), ColumnType.LABEL);
        assertEquals(columns.get(2).getType(), ColumnType.LABEL);
        assertEquals(columns.get(4).getType(), ColumnType.LABEL);
        assertEquals(columns.get(5).getType(), ColumnType.LABEL);
        assertEquals(columns.get(6).getType(), ColumnType.TEXT);
        assertEquals(columns.get(7).getType(), ColumnType.TEXT);
        assertEquals(columns.get(8).getType(), ColumnType.NUMBER);
        assertEquals(columns.get(9).getType(), ColumnType.NUMBER);
        assertEquals(columns.get(10).getType(), ColumnType.NUMBER);
        assertEquals(columns.get(11).getType(), ColumnType.NUMBER);
        assertEquals(columns.get(12).getType(), ColumnType.NUMBER);
        assertEquals(columns.get(13).getType(), ColumnType.NUMBER);
        assertEquals(columns.get(14).getType(), ColumnType.NUMBER);
        assertEquals(columns.get(15).getType(), ColumnType.NUMBER);
        assertEquals(columns.get(16).getType(), ColumnType.NUMBER);
        assertEquals(columns.get(17).getType(), ColumnType.DATE);
        assertEquals(columns.get(18).getType(), ColumnType.DATE);
        assertEquals(columns.get(19).getType(), ColumnType.DATE);
        assertEquals(columns.get(20).getType(), ColumnType.TEXT);
    }
    
    @Test
    public void clobToStringTest() throws SQLException {
        String TEST = "TEST";
        StringReader testReader = new StringReader(TEST);
        when(clob.getCharacterStream()).thenReturn(testReader);
        assertEquals(TEST, JDBCUtils.clobToString(clob));
        assertTrue(JDBCUtils.clobToString(null).isEmpty());
        when(clob.getCharacterStream()).thenThrow(new RuntimeException());
        assertTrue(JDBCUtils.clobToString(clob).isEmpty());
    }
}
