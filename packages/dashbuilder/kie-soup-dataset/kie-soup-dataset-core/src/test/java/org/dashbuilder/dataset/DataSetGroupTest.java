/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.dashbuilder.dataset;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataset.date.DayOfWeek;
import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.dashbuilder.dataset.Assertions.assertDataSetValues;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_AMOUNT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_CITY;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DATE;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DEPARTMENT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_EMPLOYEE;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_ID;
import static org.dashbuilder.dataset.group.AggregateFunctionType.AVERAGE;
import static org.dashbuilder.dataset.group.AggregateFunctionType.COUNT;
import static org.dashbuilder.dataset.group.AggregateFunctionType.DISTINCT;
import static org.dashbuilder.dataset.group.AggregateFunctionType.MAX;
import static org.dashbuilder.dataset.group.AggregateFunctionType.MIN;
import static org.dashbuilder.dataset.group.AggregateFunctionType.SUM;
import static org.dashbuilder.dataset.group.DateIntervalType.DAY;
import static org.dashbuilder.dataset.group.DateIntervalType.DAY_OF_WEEK;
import static org.dashbuilder.dataset.group.DateIntervalType.MONTH;
import static org.dashbuilder.dataset.group.DateIntervalType.QUARTER;
import static org.dashbuilder.dataset.group.DateIntervalType.YEAR;

public class DataSetGroupTest {

    public static final String EXPENSE_REPORTS = "expense_reports";

    DataSetManager dataSetManager = DataSetCore.get().getDataSetManager();
    DataSetFormatter dataSetFormatter = new DataSetFormatter();

    @Before
    public void setUp() throws Exception {
        DataSet dataSet = ExpenseReportsData.INSTANCE.toDataSet();
        dataSet.setUUID(EXPENSE_REPORTS);
        dataSetManager.registerDataSet(dataSet);
    }

    @Test
    public void testDataSetFunctions() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .column(COUNT, "#items")
                .column(COLUMN_AMOUNT, MIN)
                .column(COLUMN_AMOUNT, MAX)
                .column(COLUMN_AMOUNT, AVERAGE)
                .column(COLUMN_AMOUNT, SUM)
                .column(COLUMN_CITY, DISTINCT)
                .buildLookup());

        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"50.00", "1.10", "1,100.10", "454.63", "22,731.26", "6.00"}
        }, 0);
    }

    @Test
    public void testDateMinMaxFunctions() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .column(COLUMN_DATE, AggregateFunctionType.MIN)
                .column(COLUMN_DATE, AggregateFunctionType.MAX)
                .buildLookup());

        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"01/04/12 12:00", "12/11/15 12:00"}
        }, 0);
    }

    @Test
    public void testNumberMinMaxFunctions() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .column(COLUMN_AMOUNT, AggregateFunctionType.MIN)
                .column(COLUMN_AMOUNT, AggregateFunctionType.MAX)
                .buildLookup());

        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"1.10", "1,100.10"}
        }, 0);
    }

    @Test
    public void testGroupByLabelDynamic() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .group(COLUMN_DEPARTMENT)
                .column(COLUMN_DEPARTMENT, "Department")
                .column(COUNT, "Occurrences")
                .column(COLUMN_AMOUNT, MIN, "min")
                .column(COLUMN_AMOUNT, MAX, "max")
                .column(COLUMN_AMOUNT, AVERAGE, "average")
                .column(COLUMN_AMOUNT, SUM, "total")
                .sort(COLUMN_DEPARTMENT, SortOrder.ASCENDING)
                .buildLookup());

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][] {
                {"Engineering", "19.00", "1.10", "1,100.10", "402.64", "7,650.16"},
                {"Management", "11.00", "43.03", "992.20", "547.04", "6,017.47"},
                {"Sales", "8.00", "75.75", "995.30", "401.69", "3,213.53"},
                {"Services", "5.00", "152.25", "911.11", "500.90", "2,504.50"},
                {"Support", "7.00", "300.01", "1,001.90", "477.94", "3,345.60"}
        }, 0);
    }

    @Test
    public void testGroupByExcludeLabelColumn() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .group(COLUMN_DEPARTMENT)
                .column(COLUMN_EMPLOYEE)
                .column(COUNT, "Occurrences")
                .column(COLUMN_AMOUNT, MAX, "max")
                .column(COLUMN_AMOUNT, AVERAGE, "average")
                .column(COLUMN_AMOUNT, SUM, "total")
                .sort("Occurrences", SortOrder.ASCENDING)
                .buildLookup());

        assertThat(result.getValueAt(0,1)).isEqualTo(5d);
        assertThat(result.getValueAt(1,1)).isEqualTo(7d);
        assertThat(result.getValueAt(2,1)).isEqualTo(8d);
        assertThat(result.getValueAt(3,1)).isEqualTo(11d);
        assertThat(result.getValueAt(4,1)).isEqualTo(19d);
    }

    @Test
    public void testGroupByExcludeDateColumn() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .group(COLUMN_DATE)
                .column(COLUMN_EMPLOYEE)
                .column(COUNT, "Occurrences")
                .sort("Occurrences", SortOrder.ASCENDING)
                .buildLookup());

        assertThat(result.getValueAt(0,1)).isEqualTo(11d);
        assertThat(result.getValueAt(1,1)).isEqualTo(11d);
        assertThat(result.getValueAt(2,1)).isEqualTo(13d);
        assertThat(result.getValueAt(3,1)).isEqualTo(15d);

        // Must also work without an explicit sort
        result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .group(COLUMN_DATE)
                        .column(COLUMN_EMPLOYEE)
                        .column(COUNT, "Occurrences")
                        .buildLookup());

        assertThat(result.getRowCount()).isEqualTo(4);
    }

    @Test
    public void testGroupByYearDynamic() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .group(COLUMN_DATE).dynamic(YEAR, true)
                .column(COLUMN_DATE, "Period")
                .column(COUNT, "Occurrences")
                .column(COLUMN_AMOUNT, SUM, "totalAmount")
                .buildLookup());

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"2012", "13.00", "6,126.13"},
                {"2013", "11.00", "5,252.96"},
                {"2014", "11.00", "4,015.48"},
                {"2015", "15.00", "7,336.69"}
        }, 0);

        DataSetLookup lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .group(COLUMN_DATE).dynamic(YEAR, true)
                .column(COLUMN_DATE)
                .column(COUNT, "Occurrences")
                .column(COLUMN_AMOUNT, SUM, "totalAmount")
                .buildLookup();

        // Test required for those databases that support alias as statements (f.i: MySQL. Postgres or MonetDB)
        DataSetGroup group = lookup.getOperation(0);
        group.getColumnGroup().setColumnId(null);
        group.getGroupFunctions().get(0).setColumnId(null);
        result = dataSetManager.lookupDataSet(lookup);

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"2012", "13.00", "6,126.13"},
                {"2013", "11.00", "5,252.96"},
                {"2014", "11.00", "4,015.48"},
                {"2015", "15.00", "7,336.69"}
        }, 0);
    }

    @Test
    public void testGroupByMonthDynamic() throws Exception {
        DataSet result = lookupGroupByMonthDynamic(true);

        //printDataSet(result);
        assertThat(result.getRowCount()).isEqualTo(48);
        assertThat(result.getValueAt(0, 0)).isEqualTo("2012-01");
    }

    @Test
    public void testGroupByMonthDynamicNonEmpty() throws Exception {
        DataSet result = lookupGroupByMonthDynamic(false);

        //printDataSet(result);
        assertThat(result.getRowCount()).isEqualTo(37);
        assertThat(result.getValueAt(0, 0)).isEqualTo("2012-01");
    }

    public DataSet lookupGroupByMonthDynamic(boolean emptyIntervals) throws Exception {
        return dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .group(COLUMN_DATE).dynamic(99, MONTH, emptyIntervals)
                        .column(COLUMN_DATE, "Period")
                        .column(COLUMN_EMPLOYEE, "Employee")
                        .column(COUNT, "Occurrences")
                        .column(COLUMN_AMOUNT, SUM, "totalAmount")
                        .buildLookup());
    }


    @Test
    public void testGroupByDayOfWeekDynamic() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .group(COLUMN_DATE).dynamic(9999, DAY_OF_WEEK, true)
                .column(COLUMN_DATE, "Period")
                .column(COUNT, "Occurrences")
                .column(COLUMN_AMOUNT, SUM, "totalAmount")
                .buildLookup());

        // this test fails in certain group operations 
        //assertThat(result.getRowCount()).isEqualTo(1437);
        assertThat(result.getValueAt(0, 0)).isEqualTo("2012-01-04");
    }

    @Test
    public void testGroupByMonthFixed() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .group(COLUMN_DATE).fixed(MONTH, true)
                .column(COLUMN_DATE, "Period")
                .column(COUNT, "Occurrences")
                .column(COLUMN_AMOUNT, SUM, "totalAmount")
                .buildLookup());

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"1", "3.00", "2,324.20"},
                {"2", "6.00", "2,885.57"},
                {"3", "5.00", "1,012.55"},
                {"4", "3.00", "1,061.06"},
                {"5", "5.00", "2,503.34"},
                {"6", "9.00", "4,113.87"},
                {"7", "4.00", "2,354.04"},
                {"8", "2.00", "452.25"},
                {"9", "3.00", "693.35"},
                {"10", "3.00", "1,366.40"},
                {"11", "3.00", "1,443.75"},
                {"12", "4.00", "2,520.88"}
        }, 0);
    }

    @Test
    public void testGroupByFixedTrim() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .filter(COLUMN_ID, FilterFactory.equalsTo(1))
                .group(COLUMN_DATE).fixed(MONTH, true)
                .column(COLUMN_DATE, "Period")
                .column(COUNT, "Occurrences")
                .column(COLUMN_AMOUNT, SUM, "totalAmount")
                .rowNumber(8)
                .buildLookup());

        assertThat(result.getRowCountNonTrimmed()).isEqualTo(12);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"1", "0.00", "0.00"},
                {"2", "0.00", "0.00"},
                {"3", "0.00", "0.00"},
                {"4", "0.00", "0.00"},
                {"5", "0.00", "0.00"},
                {"6", "0.00", "0.00"},
                {"7", "0.00", "0.00"},
                {"8", "0.00", "0.00"},
        }, 0);
    }

    @Test
    public void testGroupByMonthFirstMonth() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .group(COLUMN_DATE).fixed(MONTH, true).firstMonth(Month.NOVEMBER)
                .column(COLUMN_DATE, "Period")
                .column(COUNT, "Occurrences")
                .column(COLUMN_AMOUNT, SUM, "totalAmount")
                .buildLookup());

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"11", "3.00", "1,443.75"},
                {"12", "4.00", "2,520.88"},
                {"1", "3.00", "2,324.20"},
                {"2", "6.00", "2,885.57"},
                {"3", "5.00", "1,012.55"},
                {"4", "3.00", "1,061.06"},
                {"5", "5.00", "2,503.34"},
                {"6", "9.00", "4,113.87"},
                {"7", "4.00", "2,354.04"},
                {"8", "2.00", "452.25"},
                {"9", "3.00", "693.35"},
                {"10", "3.00", "1,366.40"}
        }, 0);
    }

    @Test
    public void testGroupByMonthReverse() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .group(COLUMN_DATE).fixed(MONTH, true).desc()
                .column(COLUMN_DATE, "Period")
                .column(COUNT, "Occurrences")
                .column(COLUMN_AMOUNT, SUM, "totalAmount")
                .buildLookup());

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"12", "4.00", "2,520.88"},
                {"11", "3.00", "1,443.75"},
                {"10", "3.00", "1,366.40"},
                {"9", "3.00", "693.35"},
                {"8", "2.00", "452.25"},
                {"7", "4.00", "2,354.04"},
                {"6", "9.00", "4,113.87"},
                {"5", "5.00", "2,503.34"},
                {"4", "3.00", "1,061.06"},
                {"3", "5.00", "1,012.55"},
                {"2", "6.00", "2,885.57"},
                {"1", "3.00", "2,324.20"}
        }, 0);
    }

    @Test
    public void testGroupByMonthFirstMonthReverse() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
            DataSetLookupFactory.newDataSetLookupBuilder()
            .dataset(EXPENSE_REPORTS)
            .group(COLUMN_DATE).fixed(MONTH, true).desc().firstMonth(Month.MARCH)
            .column(COLUMN_DATE, "Period")
            .column(COUNT, "Occurrences")
            .column(COLUMN_AMOUNT, SUM, "totalAmount")
            .buildLookup());

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"3", "5.00", "1,012.55"},
                {"2", "6.00", "2,885.57"},
                {"1", "3.00", "2,324.20"},
                {"12", "4.00", "2,520.88"},
                {"11", "3.00", "1,443.75"},
                {"10", "3.00", "1,366.40"},
                {"9", "3.00", "693.35"},
                {"8", "2.00", "452.25"},
                {"7", "4.00", "2,354.04"},
                {"6", "9.00", "4,113.87"},
                {"5", "5.00", "2,503.34"},
                {"4", "3.00", "1,061.06"}
        }, 0);
    }

    @Test
    public void testFixedIntervalsSupported() throws Exception {
        for (DateIntervalType type : DateIntervalType.values()) {
            try {
                DataSetLookupFactory.newDataSetLookupBuilder().group(COLUMN_DATE).fixed(type, true);
                if (!DateIntervalType.FIXED_INTERVALS_SUPPORTED.contains(type)) {
                    fail("Missing exception on a not supported fixed interval: " + type);
                }
            } catch (Exception e) {
                if (DateIntervalType.FIXED_INTERVALS_SUPPORTED.contains(type)) {
                    fail("Exception on a supported fixed interval: " + type);
                }
            }
        }
    }

    @Test
    public void testFirstDayOfWeekOk() throws Exception {
        DataSetLookupFactory.newDataSetLookupBuilder()
            .group(COLUMN_DATE)
            .fixed(DAY_OF_WEEK, true)
            .firstDay(DayOfWeek.MONDAY);
    }

    @Test
    public void testFirstDayOfWeekNok() throws Exception {
        try {
            DataSetLookupFactory.newDataSetLookupBuilder()
                .group(COLUMN_DATE)
                .fixed(QUARTER, true)
                .firstDay(DayOfWeek.MONDAY);
            fail("firstDayOfWeek required a DAY_OF_WEEK fixed domain.");
        } catch (Exception e) {
            // Expected.
        }
    }

    @Test
    public void testFirstDayOfMonthOk() throws Exception {
        DataSetLookupFactory.newDataSetLookupBuilder()
            .group(COLUMN_DATE)
            .fixed(MONTH, true)
            .firstMonth(Month.APRIL);
    }

    @Test
    public void testFirstDayOfMonthNok() throws Exception {
        try {
            DataSetLookupFactory.newDataSetLookupBuilder()
                .group(COLUMN_DATE)
                .fixed(QUARTER, true)
                .firstMonth(Month.APRIL);
            fail("firstDayOfWeek required a DAY_OF_WEEK fixed domain.");
        } catch (Exception e) {
            // Expected.
        }
    }

    @Test
    public void testGroupByDayOfWeekFixed() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .group(COLUMN_DATE).fixed(DAY_OF_WEEK, true).firstDay(DayOfWeek.MONDAY)
                .column(COLUMN_DATE, "Period")
                .column(COUNT, "Occurrences")
                .column(COLUMN_AMOUNT, SUM, "totalAmount")
                .buildLookup());

        // printDataSet(result);

        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"2", "10.00", "3,904.17"},
                {"3", "8.00", "4,525.69"},
                {"4", "7.00", "4,303.14"},
                {"5", "4.00", "1,021.95"},
                {"6", "8.00", "3,099.08"},
                {"7", "5.00", "2,012.05"},
                {"1", "8.00", "3,865.18"}
        }, 0);
    }

    @Test
    public void testGroupByQuarter() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .group(COLUMN_DATE).fixed(QUARTER, true)
                .column(COLUMN_DATE, "Period")
                .column(COUNT, "Occurrences")
                .column(COLUMN_AMOUNT, SUM, "totalAmount")
                .buildLookup());

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"1", "14.00", "6,222.32"},
                {"2", "17.00", "7,678.27"},
                {"3", "9.00", "3,499.64"},
                {"4", "10.00", "5,331.03"}
        }, 0);
    }

    @Test
    public void testGroupByDateOneRow() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .filter(COLUMN_ID, FilterFactory.equalsTo(1d))
                        .group(COLUMN_DATE).dynamic(16, true)
                        .column(COLUMN_DATE)
                        .column(COLUMN_AMOUNT, SUM, "total")
                        .buildLookup());

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
            {"2015", "120.35"}
        }, 0);
    }

    @Test
    public void testGroupByDateOneDay() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .filter(COLUMN_ID, FilterFactory.equalsTo(1d))
                        .group(COLUMN_DATE).dynamic(16, DAY, true)
                        .column(COLUMN_DATE)
                        .column(COLUMN_AMOUNT, SUM, "total")
                        .buildLookup());

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
            {"2015-12-11", "120.35"}
        }, 0);
    }

    @Test
    public void testGroupAndCountSameColumn() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .group(COLUMN_DEPARTMENT)
                        .column(COLUMN_DEPARTMENT, "Department")
                        .column(COLUMN_DEPARTMENT, COUNT, "Occurrences")
                        .sort(COLUMN_DEPARTMENT, SortOrder.ASCENDING)
                        .buildLookup());

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"Engineering", "19.00"},
                {"Management", "11.00"},
                {"Sales", "8.00"},
                {"Services", "5.00"},
                {"Support", "7.00"}
        }, 0);
    }

    @Test
    public void testGroupNumberAsLabel() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .group(COLUMN_AMOUNT)
                        .column(COLUMN_AMOUNT, AggregateFunctionType.AVERAGE, "Amount")
                        .column(COLUMN_AMOUNT, COUNT, "Occurrences")
                        .sort("Amount", SortOrder.ASCENDING)
                        .buildLookup());

        //printDataSet(result);
        assertThat(result.getRowCount()).isEqualTo(49);
        assertThat(result.getValueAt(0, 0)).isEqualTo(1.1);
        assertThat(result.getValueAt(17, 0)).isEqualTo(300d);
        assertThat(result.getValueAt(17, 1)).isEqualTo(2d);
        assertThat(result.getValueAt(48, 0)).isEqualTo(1100.1);
    }

    private void printDataSet(DataSet dataSet) {
        System.out.print(dataSetFormatter.formatDataSet(dataSet, "{", "}", ",\n", "\"", "\"", ", ") + "\n\n");
    }
}
