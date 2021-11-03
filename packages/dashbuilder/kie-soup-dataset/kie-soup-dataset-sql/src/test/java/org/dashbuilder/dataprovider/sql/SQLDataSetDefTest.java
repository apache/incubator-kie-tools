/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.dataprovider.sql;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.sort.SortOrder;
import org.junit.Test;

import static org.dashbuilder.dataset.Assertions.assertDataSetValues;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_AMOUNT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DEPARTMENT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_EMPLOYEE;
import static org.dashbuilder.dataset.filter.FilterFactory.OR;
import static org.dashbuilder.dataset.filter.FilterFactory.greaterThan;
import static org.dashbuilder.dataset.filter.FilterFactory.lowerThan;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class SQLDataSetDefTest extends SQLDataSetTestBase {

    @Override
    public void testAll() throws Exception {
        if (!testSettings.isMonetDB()) {
            testAllColumns();
        }
        testSQLDataSet();
        testColumnSet();
        testColumnAlias();
        testFilters();
    }

    @Test
    public void testAllColumns() throws Exception {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("expenseReports_allcolumns.dset");
        String json = IOUtils.toString(fileURL, StandardCharsets.UTF_8);
        SQLDataSetDef def = (SQLDataSetDef) jsonMarshaller.fromJson(json);
        dataSetDefRegistry.registerDataSetDef(def);

        DataSetMetadata metadata = dataSetManager.getDataSetMetadata("expense_reports_allcolumns");
        assertThat(metadata.getNumberOfColumns()).isEqualTo(6);
        assertThat(metadata.getEstimatedSize()).isEqualTo(6350);
        assertThat(metadata.getNumberOfRows()).isEqualTo(50);
    }

    @Test
    public void testNoEstimateSize() throws Exception {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("expenseReports_allcolumns.dset");
        String json = IOUtils.toString(fileURL, StandardCharsets.UTF_8);
        SQLDataSetDef def = (SQLDataSetDef) jsonMarshaller.fromJson(json);
        def.setEstimateSize(false);
        dataSetDefRegistry.registerDataSetDef(def);

        DataSetMetadata metadata = dataSetManager.getDataSetMetadata("expense_reports_allcolumns");
        assertThat(metadata.getNumberOfColumns()).isEqualTo(6);
        assertThat(metadata.getEstimatedSize()).isEqualTo(0);
        assertThat(metadata.getNumberOfRows()).isEqualTo(0);
    }

    @Test
    public void testSQLDataSet() throws Exception {
        String testDataSetFile = testSettings.getExpenseReportsSqlDsetFile();
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource(testDataSetFile);
        String json = IOUtils.toString(fileURL, StandardCharsets.UTF_8);
        SQLDataSetDef def = (SQLDataSetDef) jsonMarshaller.fromJson(json);
        dataSetDefRegistry.registerDataSetDef(def);

        DataSetMetadata metadata = dataSetManager.getDataSetMetadata("expense_reports_sql");
        assertThat(metadata.getNumberOfColumns()).isEqualTo(3);
        assertThat(metadata.getNumberOfRows()).isEqualTo(6);
        assertThat(metadata.getEstimatedSize()).isEqualTo(342);

        final String uuid = "expense_reports_sql";
        DataSet dataSet = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(uuid)
                        .filter(COLUMN_AMOUNT, FilterFactory.lowerThan(1000))
                        .group(COLUMN_EMPLOYEE)
                        .column(COLUMN_EMPLOYEE)
                        .column(COLUMN_AMOUNT, AggregateFunctionType.SUM)
                        .sort(COLUMN_EMPLOYEE, SortOrder.ASCENDING)
                        .buildLookup());

        assertDataSetDefinition(dataSet, uuid);
        assertDataSetValues(dataSet, dataSetFormatter, new String[][]{
                {"Jamie Gilbeau", "792.59"},
                {"Roxie Foraker", "1,020.45"}
        }, 0);
    }

    @Test
    public void testColumnSet() throws Exception {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("expenseReports_columnset.dset");
        String json = IOUtils.toString(fileURL, StandardCharsets.UTF_8);
        SQLDataSetDef def = (SQLDataSetDef) jsonMarshaller.fromJson(json);
        dataSetDefRegistry.registerDataSetDef(def);

        DataSetMetadata metadata = dataSetManager.getDataSetMetadata("expense_reports_columnset");
        assertThat(metadata.getNumberOfColumns()).isEqualTo(4);
        if (!testSettings.isMonetDB()) {
            assertThat(metadata.getEstimatedSize()).isEqualTo(4300);
            assertThat(metadata.getNumberOfRows()).isEqualTo(50);
        }

        final String uuid = "expense_reports_columnset";
        DataSet dataSet = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(uuid)
                        .buildLookup());

        assertThat(dataSet.getColumns().size()).isEqualTo(4);
        assertThat(dataSet.getValueAt(0, 0)).isEqualTo("Engineering");
        assertThat(dataSet.getValueAt(0, 1)).isEqualTo("Roxie Foraker");
        assertThat(dataSet.getValueAt(0, 2)).isEqualTo(120.35d);
        assertThat(dataSetFormatter.formatValueAt(dataSet, 0, 3)).isEqualTo("12/11/15 12:00");
        assertDataSetDefinition(dataSet, uuid);
    }

    @Test
    public void testColumnAlias() throws Exception {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("expenseReports_columnalias.dset");
        String json = IOUtils.toString(fileURL, StandardCharsets.UTF_8);
        SQLDataSetDef def = (SQLDataSetDef) jsonMarshaller.fromJson(json);
        dataSetDefRegistry.registerDataSetDef(def);

        String uuid = "expense_reports_columnalias";
        DataSetMetadata metadata = dataSetManager.getDataSetMetadata(uuid);
        assertThat(metadata.getNumberOfColumns()).isEqualTo(3);
        assertThat(metadata.getColumnId(0)).isEqualTo("Id");
        assertThat(metadata.getColumnId(1)).isEqualTo("Employee");
        assertThat(metadata.getColumnId(2)).isEqualTo("Amount");

        DataSet dataSet = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(uuid)
                        .filter("id", FilterFactory.notNull())
                        .filter("amount", OR(greaterThan(0), lowerThan(1000)))
                        .group("employee")
                        .column("EMPLOYEE", "employee")
                        .column("id", AggregateFunctionType.COUNT, "id")
                        .column("AMOUNT", AggregateFunctionType.SUM, "amount")
                        .sort("id", SortOrder.ASCENDING)
                        .buildLookup());

        assertThat(dataSet.getColumns().size()).isEqualTo(3);
        assertNotNull(dataSet.getColumnById("ID"));
        assertNotNull(dataSet.getColumnById("EMPLOYEE"));
        assertNotNull(dataSet.getColumnById("AMOUNT"));
    }

    @Test
    public void testFilters() throws Exception {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("expenseReports_filtered.dset");
        String json = IOUtils.toString(fileURL, StandardCharsets.UTF_8);
        SQLDataSetDef def = (SQLDataSetDef) jsonMarshaller.fromJson(json);
        dataSetDefRegistry.registerDataSetDef(def);

        final String uuid = "expense_reports_filtered";
        DataSetMetadata metadata = dataSetManager.getDataSetMetadata(uuid);
        assertThat(metadata.getNumberOfColumns()).isEqualTo(5);
        if (!testSettings.isMonetDB()) {
            assertThat(metadata.getEstimatedSize()).isEqualTo(666);
            assertThat(metadata.getNumberOfRows()).isEqualTo(6);
        }

        DataSet dataSet = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(uuid)
                        .group(COLUMN_DEPARTMENT)
                        .column(COLUMN_DEPARTMENT)
                        .column(COLUMN_EMPLOYEE)
                        .column(COLUMN_AMOUNT, AggregateFunctionType.SUM)
                        .sort(COLUMN_DEPARTMENT, SortOrder.DESCENDING)
                        .buildLookup());

        assertDataSetDefinition(dataSet, uuid);
        assertDataSetValues(dataSet, dataSetFormatter, new String[][]{
                {"Services", "Jamie Gilbeau", "792.59"},
                {"Engineering", "Roxie Foraker", "2,120.55"}
        }, 0);
    }

    public static void assertDataSetDefinition(final DataSet dataSet, final String uuid) {
        assertThat(dataSet.getUUID()).isEqualTo(uuid);
        assertThat(dataSet.getDefinition()).isNotNull();
        assertThat(dataSet.getDefinition().getUUID()).isEqualTo(uuid);
    }
}
