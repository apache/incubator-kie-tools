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

import java.util.ArrayList;
import java.util.Date;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetColumnTest;
import org.dashbuilder.dataset.DataSetFilterTest;
import org.dashbuilder.dataset.DataSetGroupTest;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.DataSetNestedGroupTest;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.junit.Test;

import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.dashbuilder.dataprovider.sql.SQLFactory.*;
import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class SQLTableDataSetLookupTest extends SQLDataSetTestBase {

    @Override
    public void testAll() throws Exception {
        testNullValues();
        testCurrentDate();
        testAvoidDuplicatedGroupColumn();
        testDataSetTrim();
        testDataSetColumns();
        testDataSetFilter();
        testDataSetGroup();
        testDataSetGroupByHour();
        testDataSetNestedGroup();
        testEmptyArguments();
    }

    public void insertExtraRow(String city, String dept, String employee, Date date, Double amount) throws Exception {
        insert(conn).into(EXPENSES)
                .set(ID, 9999)
                .set(CITY, city)
                .set(DEPT, dept)
                .set(EMPLOYEE, employee)
                .set(DATE, date)
                .set(AMOUNT, amount)
                .execute();
    }

    public void deleteExtraRow() throws Exception {
        delete(conn)
                .from(EXPENSES)
                .where((ID.equalsTo(9999)))
                .execute();
    }

    @Test
    public void testNullValues() throws Exception {
        try {
            insertExtraRow(null, null, null, null, null);
            DataSet result = dataSetManager.lookupDataSet(
                    DataSetLookupFactory.newDataSetLookupBuilder()
                            .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                            .filter(equalsTo(ID.getName(), 9999))
                            .buildLookup());

            assertThat(result.getRowCount()).isEqualTo(1);
            assertThat(result.getValueAt(0, 1)).isNull();
            assertThat(result.getValueAt(0, 2)).isNull();
            assertThat(result.getValueAt(0, 3)).isNull();
            assertThat(result.getValueAt(0, 4)).isNull();
            // Skip next since some DBs like Mysql return the current date when the value inserted is null,
            // assertThat(result.getValueAt(0, 5)).isNull();
        }
        finally {
            deleteExtraRow();
        }
    }

    @Test
    public void testCurrentDate() throws Exception {
        try {
            Date currentDate = new Date();
            insertExtraRow(null, null, null, currentDate, null);

            DataSet result = dataSetManager.lookupDataSet(
                    DataSetLookupFactory.newDataSetLookupBuilder()
                            .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                            .filter(equalsTo(ID.getName(), 9999))
                            .buildLookup());

            // Seconds comparison is enough as there are some DBs that either leave out nanos or round them adding an extra second.
            Date fromDb = (Date) result.getValueAt(0, 5);
            long seconds1 = currentDate.toInstant().getEpochSecond();
            long seconds2 = fromDb.toInstant().getEpochSecond();
            assertTrue(seconds1 == seconds2 || seconds1 == seconds2-1);
        }
        finally {
            deleteExtraRow();
        }
    }

    @Test
    public void testAvoidDuplicatedGroupColumn() throws Exception {

        // In some DBs (MonetDB for instance), duplicated columns in "group by" fails
        dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                        .group(COLUMN_DEPARTMENT)
                        .column("Department")
                        .column(COLUMN_AMOUNT, SUM)
                        .rowNumber(3)
                        .rowOffset(0)
                        .buildLookup());
    }

    @Test
    public void testDataSetTrim() throws Exception {

        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                        .rowNumber(10)
                        .buildLookup());

        assertThat(result.getRowCount()).isEqualTo(10);
        assertThat(result.getValueAt(0, 0)).isEqualTo(1d);
        assertThat(result.getValueAt(9, 0)).isEqualTo(10d);

        result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                        .rowNumber(10)
                        .rowOffset(40)
                        .buildLookup());

        assertThat(result.getRowCount()).isEqualTo(10);
        assertThat(result.getValueAt(0, 0)).isEqualTo(41d);
        assertThat(result.getValueAt(9, 0)).isEqualTo(50d);

        result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                        .group(DEPT.getName())
                        .column(DEPT.getName())
                        .column(AMOUNT.getName(), SUM)
                        .rowNumber(3)
                        .rowOffset(0)
                        .buildLookup());

        assertThat(result.getRowCount()).isEqualTo(3);
        assertThat(result.getRowCountNonTrimmed()).isEqualTo(5);

        result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                        .filter(CITY.getName(), equalsTo("Barcelona"))
                        .rowNumber(3)
                        .rowOffset(0)
                        .buildLookup());

        assertThat(result.getRowCount()).isEqualTo(3);
        assertThat(result.getRowCountNonTrimmed()).isEqualTo(6);
    }

    @Test
    public void testDataSetGroupByHour() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                        .filter(ID.getName(), FilterFactory.AND(
                                FilterFactory.greaterOrEqualsTo(40),
                                FilterFactory.lowerOrEqualsTo(41)))
                        .group(DATE.getName()).dynamic(9999, DateIntervalType.HOUR, true)
                        .column(DATE.getName())
                        .buildLookup());

        assertThat(result.getRowCount()).isEqualTo(25);
        assertThat(result.getValueAt(0,0)).isEqualTo("2012-06-12 12");
    }

    @Test
    public void testDataSetColumns() throws Exception {
        DataSetColumnTest subTest = new DataSetColumnTest();
        subTest.testDataSetLookupColumns();
        subTest.testDataSetMetadataColumns();
    }

    @Test
    public void testDataSetGroup() throws Exception {
        DataSetGroupTest subTest = new DataSetGroupTest();
        subTest.testDataSetFunctions();
        subTest.testDateMinMaxFunctions();
        subTest.testNumberMinMaxFunctions();
        subTest.testGroupByLabelDynamic();
        subTest.testGroupByExcludeLabelColumn();
        subTest.testGroupByExcludeDateColumn();
        subTest.testGroupByYearDynamic();
        subTest.testGroupByMonthDynamic();
        subTest.testGroupByMonthDynamicNonEmpty();
        subTest.testGroupByDayOfWeekDynamic();
        subTest.testGroupByFixedTrim();
        subTest.testGroupByMonthReverse();
        subTest.testGroupByMonthFixed();
        subTest.testGroupByMonthFirstMonth();
        subTest.testGroupByMonthFirstMonthReverse();
        subTest.testGroupByQuarter();
        subTest.testGroupByDateOneRow();
        subTest.testGroupByDateOneDay();
        subTest.testGroupAndCountSameColumn();
        subTest.testGroupNumberAsLabel();
    }

    @Test(expected = Exception.class)
    public void testGroupByDayOfWeekFixed() throws Exception {
        // Not supported in SQL data sets
        DataSetGroupTest subTest = new DataSetGroupTest();
        subTest.testGroupByDayOfWeekFixed();
    }

    @Test
    public void testDataSetNestedGroup() throws Exception {
        DataSetNestedGroupTest subTest = new DataSetNestedGroupTest();
        subTest.testGroupSelectionFilter();
        if (!testSettings.isMonetDB()) {
            subTest.testNestedGroupFromMultipleSelection();
        }
        subTest.testNestedGroupRequiresSelection();
        subTest.testThreeNestedLevels();
        subTest.testNoResultsSelection();
    }

    @Test
    public void testDataSetFilter() throws Exception {
        DataSetFilterTest subTest = new DataSetFilterTest();
        subTest.testColumnTypes();
        subTest.testFilterByString();
        subTest.testFilterByDate();
        subTest.testFilterByNumber();
        subTest.testFilterMultiple();
        subTest.testFilterUntilToday();
        subTest.testANDExpression();
        subTest.testNOTExpression();
        subTest.testORExpression();
        subTest.testORExpressionMultilple();
        subTest.testLogicalExprNonEmpty();
        subTest.testCombinedExpression();
        subTest.testCombinedExpression2();
        subTest.testCombinedExpression3();
        subTest.testLikeOperatorNonCaseSensitive();
        subTest.testInOperator();
        subTest.testNotInOperator();

        // Skip this test since MySQL,SQLServer & Sybase are non case sensitive by default
        if (!testSettings.isMySQL() && !testSettings.isMariaDB() && !testSettings.isSqlServer()&& !testSettings.isSybase()) {
            subTest.testLikeOperatorCaseSensitive();
        }
    }

    /**
     * When a function does not receive an expected argument(s),
     * the function must be ruled out from the lookup call.
     *
     * See https://issues.jboss.org/browse/DASHBUILDE-90
     */
    @Test
    public void testEmptyArguments() throws Exception {
        try {
            insertExtraRow(null, null, null, null, null);
            assertThat(dataSetManager.lookupDataSet(
                    DataSetLookupFactory.newDataSetLookupBuilder()
                            .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                            .filter(equalsTo(CITY.getName(), (Comparable) null))
                            .buildLookup()).getRowCount()).isEqualTo(1);

            assertThat(dataSetManager.lookupDataSet(
                    DataSetLookupFactory.newDataSetLookupBuilder()
                            .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                            .filter(equalsTo(CITY.getName(), new ArrayList<Comparable>()))
                            .buildLookup()).getRowCount()).isEqualTo(51);

            assertThat(dataSetManager.lookupDataSet(
                    DataSetLookupFactory.newDataSetLookupBuilder()
                            .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                            .filter(notEqualsTo(CITY.getName(), null))
                            .buildLookup()).getRowCount()).isEqualTo(50);

            assertThat(dataSetManager.lookupDataSet(
                    DataSetLookupFactory.newDataSetLookupBuilder()
                            .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                            .filter(between(AMOUNT.getName(), null, null))
                            .buildLookup()).getRowCount()).isEqualTo(51);

            assertThat(dataSetManager.lookupDataSet(
                    DataSetLookupFactory.newDataSetLookupBuilder()
                            .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                            .filter(in(CITY.getName(), null))
                            .buildLookup()).getRowCount()).isEqualTo(51);

            assertThat(dataSetManager.lookupDataSet(
                    DataSetLookupFactory.newDataSetLookupBuilder()
                            .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                            .filter(in(CITY.getName(), new ArrayList<Comparable>()))
                            .buildLookup()).getRowCount()).isEqualTo(51);

            assertThat(dataSetManager.lookupDataSet(
                    DataSetLookupFactory.newDataSetLookupBuilder()
                            .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                            .filter(notIn(CITY.getName(), new ArrayList<Comparable>()))
                            .buildLookup()).getRowCount()).isEqualTo(51);
        }
        finally {
            deleteExtraRow();
        }
    }
}
