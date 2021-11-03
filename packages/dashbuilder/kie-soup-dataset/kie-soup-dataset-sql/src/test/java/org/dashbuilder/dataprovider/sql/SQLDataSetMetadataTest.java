/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.dashbuilder.dataprovider.sql.SQLDataSetProvider;
import org.dashbuilder.dataprovider.sql.model.Column;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SQLDataSetMetadataTest {

    @Spy
    SQLDataSetProvider dataSetProvider = SQLDataSetProvider.get();

    @Before
    public void setUp() throws Exception {
        final List<Column> dbColumns = new ArrayList<Column>();
        dbColumns.add(SQLFactory.column("dbWins1", ColumnType.LABEL, 10));
        dbColumns.add(SQLFactory.column("dbWins2", ColumnType.LABEL, 10));
        dbColumns.add(SQLFactory.column("dbWins3", ColumnType.LABEL, 10));

        dataSetProvider.setDataSourceLocator(new DatabaseTestSettings().getDataSourceLocator());

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return 0;
            }
        }).when(dataSetProvider)._getRowCount(any(DataSetMetadata.class), any(SQLDataSetDef.class), any(Connection.class));

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return dbColumns;
            }
        }).when(dataSetProvider)._getColumns(any(SQLDataSetDef.class), any(Connection.class));
    }

    @Test()
    public void testRetrieveAllColumns() throws Exception {
        DataSetMetadata metadata = dataSetProvider.getDataSetMetadata(
                DataSetDefFactory.newSQLDataSetDef()
                        .uuid("test")
                        .dbTable("test", true)
                        .buildDef());

        List<String> columnIds = metadata.getColumnIds();
        assertEquals(columnIds.size(), 3);
        assertEquals(columnIds.get(0), "dbWins1");
        assertEquals(columnIds.get(1), "dbWins2");
        assertEquals(columnIds.get(2), "dbWins3");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoColumnsDeclared() throws Exception {
        DataSetMetadata metadata = dataSetProvider.getDataSetMetadata(
                DataSetDefFactory.newSQLDataSetDef()
                        .uuid("test")
                        .dbTable("test", false)
                        .buildDef());

        List<String> columnIds = metadata.getColumnIds();
        assertEquals(columnIds.size(), 2);
        assertEquals(columnIds.get(0), "dbWins1");
        assertEquals(columnIds.get(1), "dbWins2");
    }

    @Test
    public void testColumnsMustMatchDb() throws Exception {
        DataSetMetadata metadata = dataSetProvider.getDataSetMetadata(
                DataSetDefFactory.newSQLDataSetDef()
                .uuid("test")
                .label("dbwins1")
                .label("DBWINS2")
                .dbTable("test", false)
                .buildDef());

        List<String> columnIds = metadata.getColumnIds();
        assertEquals(columnIds.size(), 2);
        assertEquals(columnIds.get(0), "dbWins1");
        assertEquals(columnIds.get(1), "dbWins2");
    }
}
