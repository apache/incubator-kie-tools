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
package org.dashbuilder.dataset;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.junit.Before;
import org.junit.Test;

import static org.dashbuilder.dataset.Assertions.assertDataSetValue;
import static org.dashbuilder.dataset.Assertions.assertDataSetValues;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_AMOUNT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_CITY;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DATE;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DEPARTMENT;
import static org.dashbuilder.dataset.sort.SortOrder.ASCENDING;
import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;

public class DataSetSortTest {

    public static final String EXPENSE_REPORTS = "expense_reports_dataset";

    DataSetManager dataSetManager = DataSetCore.get().getDataSetManager();
    DataSetFormatter dataSetFormatter = new DataSetFormatter();
    DataSet dataSet;

    @Before
    public void setUp() throws Exception {
        dataSet = ExpenseReportsData.INSTANCE.toDataSet();
        dataSet.setUUID(EXPENSE_REPORTS);
        dataSetManager.registerDataSet(dataSet);
    }

    @Test
    public void testSortByString() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .sort(COLUMN_CITY, ASCENDING)
                        .buildLookup());

        //printDataSet(result);
        assertDataSetValue(result, 0, 1, "Barcelona");
        assertDataSetValue(result, 6, 1, "Brno");
        assertDataSetValue(result, 15, 1, "London");
        assertDataSetValue(result, 22, 1, "Madrid");
        assertDataSetValue(result, 28, 1, "Raleigh");
        assertDataSetValue(result, 41, 1, "Westford");
    }

    @Test
    public void testSortByNumber() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .sort(COLUMN_AMOUNT, ASCENDING)
                        .buildLookup());

        //printDataSet(result);
        assertDataSetValue(result, 0, 0, "23.00");
        assertDataSetValue(result, 49, 0, "2.00");
    }

    @Test
    public void testSortByDate() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .sort(COLUMN_DATE, ASCENDING)
                        .buildLookup());

        //printDataSet(result);
        assertDataSetValue(result, 0, 0, "50.00");
        assertDataSetValue(result, 49, 0, "1.00");
    }

    @Test
    public void testSortMultiple() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .sort(COLUMN_CITY, ASCENDING)
                        .sort(COLUMN_DEPARTMENT, ASCENDING)
                        .sort(COLUMN_AMOUNT, DESCENDING)
                        .buildLookup());

        //printDataSet(result);
        assertDataSetValue(result, 0, 0, "2.00");
        assertDataSetValue(result, 5, 0, "6.00");
        assertDataSetValue(result, 6, 0, "19.00");
        assertDataSetValue(result, 49, 0, "28.00");
    }

    @Test
    public void testGroupAndSort() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .group(COLUMN_DEPARTMENT)
                        .column(COLUMN_DEPARTMENT)
                        .column(COLUMN_AMOUNT, AggregateFunctionType.SUM, "total")
                        .sort("total", DESCENDING)
                        .buildLookup());

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"Engineering", "7,650.16"},
                {"Management", "6,017.47"},
                {"Support", "3,345.60"},
                {"Sales", "3,213.53"},
                {"Services", "2,504.50"}
        }, 0);
    }

    private void printDataSet(DataSet dataSet) {
        System.out.print(dataSetFormatter.formatDataSet(dataSet, "{", "}", ",\n", "\"", "\"", ", ") + "\n\n");
    }
}
