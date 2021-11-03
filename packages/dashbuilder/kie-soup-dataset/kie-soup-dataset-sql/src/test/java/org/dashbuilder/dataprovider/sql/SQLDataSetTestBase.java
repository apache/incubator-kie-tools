/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.sql.model.Column;
import org.dashbuilder.dataprovider.sql.model.Table;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFormatter;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.ExpenseReportsData;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.junit.After;
import org.junit.Before;

import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.dashbuilder.dataprovider.sql.SQLFactory.*;

public class SQLDataSetTestBase {

    DataSetManager dataSetManager = DataSetCore.get().getDataSetManager();
    DataSetDefRegistry dataSetDefRegistry = DataSetCore.get().getDataSetDefRegistry();
    DataSetFormatter dataSetFormatter = new DataSetFormatter();
    SQLDataSetProvider sqlDataSetProvider = SQLDataSetProvider.get();
    DataSetDefJSONMarshaller jsonMarshaller = DataSetCore.get().getDataSetDefJSONMarshaller();
    DatabaseTestSettings testSettings = createTestSettings();

    Connection conn;
    Column ID = column(COLUMN_ID, ColumnType.NUMBER, 4);
    Column CITY = column(COLUMN_CITY, ColumnType.LABEL, 50);
    Column DEPT = column(COLUMN_DEPARTMENT, ColumnType.LABEL, 50);
    Column EMPLOYEE = column(COLUMN_EMPLOYEE, ColumnType.LABEL, 50);
    Column DATE = column(COLUMN_DATE, ColumnType.DATE, 4);
    Column AMOUNT = column(COLUMN_AMOUNT, ColumnType.NUMBER, 4);
    Table EXPENSES = table("EXPENSE_REPORTS");

    protected DatabaseTestSettings getTestSettings() {
        return testSettings;
    }

    protected DatabaseTestSettings createTestSettings() {
        return DatabaseTestSettingsProvider.get();
    }

    public String getExpenseReportsDsetFile() {
        return testSettings.getExpenseReportsTableDsetFile();
    }

    protected static boolean _dbInfoPrinted = false;

    private void printDatabaseInfo() throws Exception {
        if (!_dbInfoPrinted) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("\n********************************************************************************************");
            System.out.println(String.format("Database: %s %s", meta.getDatabaseProductName(), meta.getDatabaseProductVersion()));
            System.out.println(String.format("Driver: %s %s", meta.getDriverName(), meta.getDriverVersion()));
            System.out.println("*********************************************************************************************\n");
            _dbInfoPrinted = true;
        }
    }

    @Before
    public void setUp() throws Exception {
        // Prepare the datasource to test
        SQLDataSourceLocator dataSourceLocator = testSettings.getDataSourceLocator();
        sqlDataSetProvider.setDataSourceLocator(dataSourceLocator);

        // Add SQL data sets support
        DataSetProviderRegistry dataSetProviderRegistry = DataSetCore.get().getDataSetProviderRegistry();
        dataSetProviderRegistry.registerDataProvider(sqlDataSetProvider);

        // Register the SQL data set
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource(getExpenseReportsDsetFile());
        String json = IOUtils.toString(fileURL, StandardCharsets.UTF_8);
        SQLDataSetDef def = (SQLDataSetDef) jsonMarshaller.fromJson(json);
        dataSetDefRegistry.registerDataSetDef(def);

        // Get a data source connection
        DataSource dataSource = dataSourceLocator.lookup(def);
        conn = dataSource.getConnection();
        printDatabaseInfo();

        // Create the expense reports table
        createTable(conn).table(EXPENSES)
                .columns(ID, CITY, DEPT, EMPLOYEE, DATE, AMOUNT)
                .primaryKey(ID).execute();

        // Populate the table
        populateDbTable();
    }

    @After
    public void tearDown() throws Exception {
        // Drop the expense reports table
        dropTable(conn).table(EXPENSES).execute();

        conn.close();
    }

    public void testAll() throws Exception {
        // To be implemented by subclasses
    }

    protected void populateDbTable() throws Exception {
        int rowCount = select(conn).from(EXPENSES).fetchCount();

        DataSet dataSet = ExpenseReportsData.INSTANCE.toDataSet();
        for (int i = 0; i < dataSet.getRowCount(); i++) {
            int id = ((Number) dataSet.getValueAt(i, 0)).intValue();
            insert(conn).into(EXPENSES)
                    .set(ID, rowCount + id)
                    .set(CITY, dataSet.getValueAt(i, 1))
                    .set(DEPT, dataSet.getValueAt(i, 2))
                    .set(EMPLOYEE, dataSet.getValueAt(i, 3))
                    .set(DATE, dataSet.getValueAt(i, 4))
                    .set(AMOUNT, dataSet.getValueAt(i, 5))
                    .execute();
        }
    }

    protected void printDataSet(DataSet dataSet) {
        System.out.print(dataSetFormatter.formatDataSet(dataSet, "{", "}", ",\n", "\"", "\"", ", ") + "\n\n");
    }
}
