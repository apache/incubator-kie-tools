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
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.junit.Before;
import org.junit.Test;

import static org.dashbuilder.dataset.Assertions.assertDataSetValue;
import static org.dashbuilder.dataset.Assertions.assertDataSetValues;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_AMOUNT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_CITY;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DATE;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DEPARTMENT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_EMPLOYEE;
import static org.dashbuilder.dataset.group.AggregateFunctionType.AVERAGE;
import static org.dashbuilder.dataset.group.AggregateFunctionType.COUNT;
import static org.dashbuilder.dataset.group.AggregateFunctionType.MAX;
import static org.dashbuilder.dataset.group.AggregateFunctionType.MIN;
import static org.dashbuilder.dataset.group.AggregateFunctionType.SUM;
import static org.dashbuilder.dataset.group.DateIntervalType.QUARTER;
import static org.assertj.core.api.Assertions.assertThat;

public class DataSetNestedGroupTest {

    public static final String EXPENSE_REPORTS = "expense_reports";

    DataSetManager dataSetManager = DataSetCore.get().getDataSetManager();
    DataSetFormatter dataSetFormatter = new DataSetFormatter();

    @Before
    public void setUp() throws Exception {
        DataSet dataSet = ExpenseReportsData.INSTANCE.toDataSet();
        dataSet.setUUID(EXPENSE_REPORTS);
        dataSetManager.registerDataSet(dataSet);
    }

/*
     @Test
     public void testMultipleYearSplit() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .group(COLUMN_DATE).fixed(DateIntervalType.MONTH)
                .column(COLUMN_AMOUNT, SUM, "total")
                .column(COLUMN_DATE, COLUMN_AMOUNT, SUM, "total in {date}", DateIntervalType.YEAR)
                .buildLookup());

        printDataSet(result);
    }
*/

    @Test
    public void testGroupSelectionFilter() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .filter(COLUMN_AMOUNT, FilterFactory.greaterThan(500))
                        .group(COLUMN_DEPARTMENT).select("Engineering")
                        .group(COLUMN_CITY).select("Westford")
                        .buildLookup());

        //printDataSet(result);
        assertThat(result.getRowCount()).isEqualTo(1);
        assertDataSetValue(result, 0, 0, "26.00");
    }

    @Test
    public void testNestedGroupFromMultipleSelection() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .group(COLUMN_DEPARTMENT, "Department").select("Services", "Engineering")
                        .group(COLUMN_CITY, "City")
                        .column(COLUMN_CITY)
                        .column(COUNT, "Occurrences")
                        .column(COLUMN_AMOUNT, MIN, "min")
                        .column(COLUMN_AMOUNT, MAX, "max")
                        .column(COLUMN_AMOUNT, AVERAGE, "average")
                        .column(COLUMN_AMOUNT, SUM, "total")
                        .sort(COLUMN_CITY, "asc")
                        .buildLookup());

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"Barcelona", "6.00", "120.35", "1,100.10", "485.52", "2,913.14"},
                {"Brno", "4.00", "159.01", "800.24", "364.86", "1,459.45"},
                {"London", "3.00", "333.45", "868.45", "535.40", "1,606.20"},
                {"Madrid", "2.00", "800.80", "911.11", "855.96", "1,711.91"},
                {"Raleigh", "4.00", "209.55", "401.40", "284.38", "1,137.53"},
                {"Westford", "5.00", "1.10", "600.34", "265.29", "1,326.43"}
        }, 0);
    }

    @Test
    public void testNestedGroupRequiresSelection() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .group(COLUMN_DEPARTMENT, "Department")
                        .column(COLUMN_DEPARTMENT)
                        .group(COLUMN_CITY, COLUMN_CITY)
                        .sort(COLUMN_DEPARTMENT, "asc")
                        .buildLookup());

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"Engineering"},
                {"Management"},
                {"Sales"},
                {"Services"},
                {"Support"}
        }, 0);
    }

    @Test
    public void testNoResultsSelection() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .group(COLUMN_EMPLOYEE).select("Jerri Preble")
                        .group(COLUMN_DEPARTMENT).select("Engineering")
                        .group(COLUMN_CITY).select("Westford")
                        .group(COLUMN_DATE).fixed(DateIntervalType.MONTH, true)
                        .column(COLUMN_DATE)
                        .column(COLUMN_AMOUNT, SUM, "total")
                        .buildLookup());

        String intervalType = result.getColumnByIndex(0).getIntervalType();
        assertThat(intervalType).isNotEmpty();
        assertThat(DateIntervalType.getByName(intervalType)).isEqualTo(DateIntervalType.MONTH);

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"1", "0.00"},
                {"2", "0.00"},
                {"3", "0.00"},
                {"4", "0.00"},
                {"5", "0.00"},
                {"6", "0.00"},
                {"7", "0.00"},
                {"8", "0.00"},
                {"9", "0.00"},
                {"10", "0.00"},
                {"11", "0.00"},
                {"12", "0.00"}
        }, 0);
    }

    @Test
    public void testThreeNestedLevels() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .group(COLUMN_DEPARTMENT).select("Services", "Engineering")
                        .group(COLUMN_CITY).select("Madrid", "Barcelona")
                        .group(COLUMN_DATE).fixed(DateIntervalType.MONTH, true)
                        .column(COLUMN_DATE)
                        .column(COLUMN_AMOUNT, SUM, "total")
                        .buildLookup());

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"1", "0.00"},
                {"2", "0.00"},
                {"3", "0.00"},
                {"4", "0.00"},
                {"5", "0.00"},
                {"6", "911.11"},
                {"7", "800.80"},
                {"8", "152.25"},
                {"9", "300.00"},
                {"10", "340.34"},
                {"11", "900.10"},
                {"12", "1,220.45"}
        }, 0);
    }

    @Test
    public void testGroupByQuarter() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .group(COLUMN_DATE).fixed(QUARTER, true).select("1")
                        .buildLookup());

        //printDataSet(result);
        assertThat(result.getRowCount()).isEqualTo(14);
    }

/*    @Test
    public void testGroupJoin() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .group(COLUMN_DEPARTMENT)
                .group(COLUMN_CITY).select("Barcelona", "Brno").join()
                .group(COLUMN_DATE, "month").fixed(DateIntervalType.MONTH, true).join()
                .column(COLUMN_DEPARTMENT)
                .column(COLUMN_CITY)
                .column("month")
                .column(COLUMN_AMOUNT, SUM, "total")
                .buildLookup());

        //printDataSet(result);
    }
*/
    private void printDataSet(DataSet dataSet) {
        System.out.print(dataSetFormatter.formatDataSet(dataSet, "{", "}", ",\n", "\"", "\"", ", ") + "\n\n");
    }
}
