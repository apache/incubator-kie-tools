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
import org.dashbuilder.dataset.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;

import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DataSetTrimTest {

    public static final String EXPENSE_REPORTS = "expense_reports";

    DataSetManager dataSetManager = DataSetCore.get().getDataSetManager();

    @Before
    public void setUp() throws Exception {
        DataSet dataSet = ExpenseReportsData.INSTANCE.toDataSet();
        dataSet.setUUID(EXPENSE_REPORTS);
        dataSetManager.registerDataSet(dataSet);
    }

    @Test
    public void testTrim() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .rowNumber(10)
                .buildLookup());

        assertThat(result.getColumns().size()).isEqualTo(6);
        assertThat(result.getRowCount()).isEqualTo(10);
    }

    @Test
    public void testTrimGroup() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .group(COLUMN_DEPARTMENT)
                        .column(COLUMN_CITY)
                        .column(COLUMN_DEPARTMENT)
                        .column(COLUMN_AMOUNT, AggregateFunctionType.SUM)
                        .rowNumber(10)
                        .buildLookup());

        assertThat(result.getColumns().size()).isEqualTo(3);
        assertThat(result.getRowCount()).isEqualTo(5);
        assertThat(result.getRowCountNonTrimmed()).isEqualTo(5);
    }

    @Test
    public void testDuplicatedColumns() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(EXPENSE_REPORTS)
                .column(COLUMN_CITY, "city1")
                .column(COLUMN_CITY, "city2")
                .rowNumber(10)
                .sort(COLUMN_CITY, SortOrder.ASCENDING)
                .buildLookup());

        assertThat(result.getColumns().size()).isEqualTo(2);
        assertThat(result.getRowCount()).isEqualTo(10);
    }
}
