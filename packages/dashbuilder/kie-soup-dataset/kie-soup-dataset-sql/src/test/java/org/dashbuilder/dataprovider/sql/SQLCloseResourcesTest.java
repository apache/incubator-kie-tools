package org.dashbuilder.dataprovider.sql;

import org.dashbuilder.dataprovider.sql.dialect.Dialect;
import org.dashbuilder.dataprovider.sql.model.Column;
import org.dashbuilder.dataprovider.sql.model.Select;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JDBCUtils.class)
public class SQLCloseResourcesTest {

    @Mock
    Connection connection;

    @Mock
    Statement statement;

    @Mock
    ResultSet resultSet;

    @Mock
    Dialect dialect;

    @Mock
    Select select;

    @Mock
    SQLDataSourceLocator dataSourceLocator;

    @Mock
    DataSource dataSource;

    @Mock
    ResultSetMetaData metaData;

    SQLDataSetProvider sqlDataSetProvider;
    ResultSetHandler resultSetHandler;
    SQLDataSetDef dataSetDef = new SQLDataSetDef();

    @Before
    public void setUp() throws Exception {
        resultSetHandler = new ResultSetHandler(resultSet, statement);
        dataSetDef.setDataSource("test");
        dataSetDef.setDbSQL("test");

        sqlDataSetProvider = SQLDataSetProvider.get();
        sqlDataSetProvider.setDataSourceLocator(dataSourceLocator);
        when(dataSourceLocator.lookup(any(SQLDataSetDef.class))).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);

        PowerMockito.mockStatic(JDBCUtils.class);
        when(JDBCUtils.dialect(connection)).thenReturn(dialect);
        when(JDBCUtils.executeQuery(any(Connection.class), any())).thenReturn(resultSetHandler);
    }

    @Test
    public void testGetColumns() throws Exception {
        sqlDataSetProvider._getColumns(dataSetDef, connection);
        verify(statement).close();
        verify(resultSet).close();
    }

    @Test
    public void testLookup() throws Exception {
        DataSetLookup dataSetLookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .column("column1")
                .buildLookup();

        when(JDBCUtils.getColumns(resultSet, null)).thenReturn(Arrays.asList(new Column("column1")));
        sqlDataSetProvider.lookupDataSet(dataSetDef, dataSetLookup);
        verify(resultSet, times(3)).close();
        verify(statement, times(3)).close();
        verify(connection).close();
    }
}