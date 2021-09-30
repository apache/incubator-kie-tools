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

import java.util.Arrays;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetGroupTest;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.DataSetTrimTest;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_AMOUNT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_CITY;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DATE;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DEPARTMENT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_EMPLOYEE;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_ID;
import static org.dashbuilder.dataset.filter.FilterFactory.AND;
import static org.dashbuilder.dataset.filter.FilterFactory.OR;
import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.dashbuilder.dataset.filter.FilterFactory.greaterOrEqualsTo;
import static org.dashbuilder.dataset.filter.FilterFactory.isNull;
import static org.dashbuilder.dataset.filter.FilterFactory.notEqualsTo;


public class SQLDataSetTrimTest extends SQLDataSetTestBase {

    @Override
    public void testAll() throws Exception {
        testTrim();
    }

    @Test
    public void testTrim() throws Exception {
        DataSetTrimTest subTest = new DataSetTrimTest();
        subTest.testTrim();
        subTest.testTrimGroup();
        subTest.testDuplicatedColumns();
    }
    @Test
    public void testTotalRowCountNonTrimmedFillingGroupBy() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                        .group(COLUMN_DEPARTMENT)
                        .column(COLUMN_ID)
                        .column(COLUMN_CITY)
                        .column(COLUMN_DEPARTMENT)
                        .column(COLUMN_EMPLOYEE)
                        .column(COLUMN_DATE)
                        .column(COLUMN_AMOUNT)
                        .rowNumber(10)
                        .rowOffset(0)
                        .buildLookup());
        assertThat(result.getRowCount()).isEqualTo(5);
        assertThat(result.getRowCountNonTrimmed()).isEqualTo(5);

        result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                        .group(COLUMN_DEPARTMENT)
                        .column(COLUMN_ID)
                        .column(COLUMN_CITY)
                        .column(COLUMN_DEPARTMENT)
                        .column(COLUMN_EMPLOYEE)
                        .column(COLUMN_DATE)
                        .column(COLUMN_AMOUNT)
                        .rowNumber(3)
                        .rowOffset(0)
                        .buildLookup());
        assertThat(result.getRowCount()).isEqualTo(3);
        assertThat(result.getRowCountNonTrimmed()).isEqualTo(5);

        result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                        .group(COLUMN_DEPARTMENT)
                        .column(COLUMN_ID)
                        .column(COLUMN_CITY)
                        .column(COLUMN_DEPARTMENT)
                        .column(COLUMN_EMPLOYEE)
                        .column(COLUMN_DATE)
                        .column(COLUMN_AMOUNT)
                        .rowNumber(5)
                        .rowOffset(5)
                        .buildLookup());
        assertThat(result.getRowCount()).isEqualTo(0);
        assertThat(result.getRowCountNonTrimmed()).isEqualTo(5);
    }

    @Test
    public void testPostFilterDisable() throws Exception {

        DataSetLookup lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                .filter(AND(
                        equalsTo(COLUMN_DEPARTMENT, Arrays.asList("Sales", "Management")),
                        OR(notEqualsTo(COLUMN_ID, 21), isNull(COLUMN_ID))))
                .filter(OR(isNull(COLUMN_AMOUNT), notEqualsTo(COLUMN_AMOUNT, 27), greaterOrEqualsTo(COLUMN_AMOUNT, 1)))
                .rowNumber(5).rowOffset(5)
                .buildLookup();
        DataSetGroup gOp = new DataSetGroup();
        ColumnGroup cg = new ColumnGroup(COLUMN_ID, COLUMN_ID);
        cg.setPostEnabled(false);
        gOp.setColumnGroup(cg);
        gOp.addGroupFunction(new GroupFunction(COLUMN_CITY, COLUMN_CITY, null));
        gOp.addGroupFunction(new GroupFunction(COLUMN_DEPARTMENT, COLUMN_DEPARTMENT, null));
        gOp.addGroupFunction(new GroupFunction(COLUMN_EMPLOYEE, COLUMN_EMPLOYEE, null));
        gOp.addGroupFunction(new GroupFunction(COLUMN_DATE, COLUMN_DATE, null));
        gOp.addGroupFunction(new GroupFunction(COLUMN_AMOUNT, COLUMN_AMOUNT, null));
        lookup.addOperation(gOp);
        DataSet result = dataSetManager.lookupDataSet(lookup);
        result.getDefinition();
        assertThat(result.getRowCount()).isEqualTo(5);
    }

}
