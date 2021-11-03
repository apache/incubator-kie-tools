/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

import org.assertj.core.api.Assertions;
import org.dashbuilder.dataprovider.sql.dialect.Dialect;
import org.dashbuilder.dataprovider.sql.model.Column;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.DataSetOpEngine;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static java.util.Arrays.asList;
import static org.dashbuilder.dataprovider.sql.JDBCUtils.DB2;
import static org.dashbuilder.dataprovider.sql.JDBCUtils.DEFAULT;
import static org.dashbuilder.dataprovider.sql.JDBCUtils.H2;
import static org.dashbuilder.dataprovider.sql.JDBCUtils.MYSQL;
import static org.dashbuilder.dataprovider.sql.JDBCUtils.ORACLE;
import static org.dashbuilder.dataprovider.sql.JDBCUtils.POSTGRES;
import static org.dashbuilder.dataprovider.sql.JDBCUtils.SQLSERVER;
import static org.dashbuilder.dataprovider.sql.JDBCUtils.SYBASE_ASE;
import static org.dashbuilder.dataprovider.sql.JDBCUtils.dialect;
import static org.dashbuilder.dataprovider.sql.JDBCUtils.executeQuery;
import static org.dashbuilder.dataprovider.sql.JDBCUtils.fixCase;
import static org.dashbuilder.dataprovider.sql.JDBCUtils.getColumns;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({JDBCUtils.class})
public class SQLDataSetMetadataLimitTest {

    @Parameters(name = "Dialect : {0}")
    public static Collection<Object[]> data() {
        return asList(new Object[][] {
                 {DB2, "FETCH FIRST %d ROWS ONLY"},
                 {DEFAULT, "LIMIT %d"},
                 {H2, "LIMIT %d"},
                 {MYSQL, "LIMIT %d"},
                 {ORACLE, "FETCH FIRST %d ROWS ONLY"},
                 {POSTGRES, "LIMIT %d"},
                 {SQLSERVER, "TOP %d"},
                 {SYBASE_ASE, "TOP %d"}
           });
    }
    
    @Mock
    Statement statement;
    
    @Mock
    ResultSet resultSet;

    @Mock
    DataSetOpEngine opEngine;

    @InjectMocks
    SQLDataSetProvider dataSetProvider;

    private final Dialect dialect;
    private final String limitAssertion;
    
    public SQLDataSetMetadataLimitTest(Dialect dialect, String limitAssertion) {
        this.dialect = dialect;
        this.limitAssertion = limitAssertion;
    }
    
    @Before
    public void setUp() throws Exception {
        mockStatic(JDBCUtils.class);

        final List<Column> dbColumns = new ArrayList<>();
        dbColumns.add(SQLFactory.column("dbWins1", ColumnType.LABEL, 10));
        dbColumns.add(SQLFactory.column("dbWins2", ColumnType.LABEL, 10));
        dbColumns.add(SQLFactory.column("dbWins3", ColumnType.LABEL, 10));

        dataSetProvider.setDataSourceLocator(new DatabaseTestSettings().getDataSourceLocator());
        ResultSetHandler resultSetHandler = new ResultSetHandler(resultSet, statement);

        when(dialect(any(Connection.class))).thenReturn(dialect);
        when(executeQuery(any(Connection.class), any())).thenReturn(resultSetHandler);
        when(getColumns(any(),any())).thenReturn(dbColumns);
        when(fixCase(any(Connection.class),eq("test"))).thenReturn("TEST");
        when(fixCase(any(Connection.class), eq("dbWins1"))).thenReturn("dbWins1");
        when(fixCase(any(Connection.class), eq("dbWins2"))).thenReturn("dbWins2");
        when(fixCase(any(Connection.class), eq("dbWins3"))).thenReturn("dbWins3");
    }

    @Test
    public void testGetColumnsWithLimitZero() throws Exception {
        DataSetMetadata metadata = dataSetProvider.getDataSetMetadata(
                DataSetDefFactory.newSQLDataSetDef()
                        .uuid("test")
                        .estimateSize(false)
                        .dbTable("test", true)
                        .buildDef());

        ArgumentCaptor<Connection> conn = forClass(Connection.class);
        ArgumentCaptor<String> sql = forClass(String.class);

        verifyStatic(JDBCUtils.class, atLeastOnce());
        executeQuery(conn.capture(), sql.capture());
        assertThat(sql.getValue().toUpperCase(), containsString(String.format(limitAssertion, 0)));
        
        List<String> columnIds = metadata.getColumnIds();
        assertEquals(3, columnIds.size());
    }

    @Test
    public void testPostProcessingDisabledWithLimit() throws Exception {
        postProcessingWithLimit(false, (sql, result) -> {
            assertThat(sql.toUpperCase(), containsString(String.format(limitAssertion, 1)));
            Assertions.assertThat(result.getRowCount()).isEqualTo(1);
        });
    }

    @Test
    public void testPostProcessingEnabledWithLimit() throws Exception {
        //  opEngine is only called when postProcessing is enabled
        when(opEngine.execute(any(DataSet.class), any())).then(dataSet -> dataSet.getArgument(0));
        postProcessingWithLimit(true, (sql, result) -> {
            assertThat(sql.toUpperCase(), not(containsString(String.format(limitAssertion, 1))));
            Assertions.assertThat(result.getRowCount()).isEqualTo(1);
            verify(opEngine).execute(any(DataSet.class), any());
        });
    }

    private void postProcessingWithLimit(boolean postProcessing, BiConsumer<String, DataSet> assertions) throws Exception {
        when(resultSet.next()).
                thenReturn(true).
                thenReturn(true).
                thenReturn(false);
        DataSetLookup lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .rowNumber(1)
                .buildLookup();

        DataSetGroup gOp = new DataSetGroup();
        ColumnGroup cg = new ColumnGroup("dbWins1", "dbWins1");
        cg.setPostEnabled(postProcessing);
        gOp.setColumnGroup(cg);
        gOp.addGroupFunction(new GroupFunction("dbWins2", "dbWins2", null));
        gOp.addGroupFunction(new GroupFunction("dbWins3", "dbWins3", null));
        lookup.addOperation(gOp);
        DataSet result = dataSetProvider.lookupDataSet(
                DataSetDefFactory.newSQLDataSetDef()
                        .dataSource("test")
                        .uuid("test")
                        .estimateSize(false)
                        .dbTable("test", true)
                        .buildDef(), lookup);

        ArgumentCaptor<Connection> conn = forClass(Connection.class);
        ArgumentCaptor<String> sql = forClass(String.class);

        verifyStatic(JDBCUtils.class, atLeastOnce());
        executeQuery(conn.capture(), sql.capture());

        assertions.accept(sql.getValue(), result);
    }
}
