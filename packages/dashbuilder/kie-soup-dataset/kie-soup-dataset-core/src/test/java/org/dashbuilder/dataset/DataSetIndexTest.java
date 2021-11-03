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
import org.dashbuilder.dataset.engine.SharedDataSetOpEngine;
import org.dashbuilder.dataset.engine.index.DataSetIndex;
import org.dashbuilder.dataset.engine.index.stats.DataSetIndexStats;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.junit.Before;
import org.junit.Test;

import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_AMOUNT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_CITY;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DEPARTMENT;
import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.assertj.core.api.Assertions.assertThat;

public class DataSetIndexTest {

    public static final String EXPENSE_REPORTS = "expense_reports_dataset";

    /**
     * Group by department and count occurrences
     */
    DataSetLookup groupByDeptAndCount = DataSetLookupFactory.newDataSetLookupBuilder()
            .dataset(EXPENSE_REPORTS)
            .group(COLUMN_DEPARTMENT, "Department")
            .column(AggregateFunctionType.COUNT, "occurrences")
            .buildLookup();

    /**
     * Group by department and sum the amount
     */
    DataSetLookup groupByDeptAndSum = DataSetLookupFactory.newDataSetLookupBuilder()
            .dataset(EXPENSE_REPORTS)
            .group(COLUMN_DEPARTMENT, "Department")
            .column(COLUMN_AMOUNT, AggregateFunctionType.AVERAGE)
            .buildLookup();

    /**
     * Filter by city & department
     */
    DataSetLookup filterByCityAndDept = DataSetLookupFactory.newDataSetLookupBuilder()
            .dataset(EXPENSE_REPORTS)
            .filter(COLUMN_CITY, equalsTo("Barcelona"))
            .filter(COLUMN_DEPARTMENT, equalsTo("Engineering"))
            .buildLookup();

    /**
     * Sort by amount in ascending order
     */
    DataSetLookup sortByAmountAsc = DataSetLookupFactory.newDataSetLookupBuilder()
            .dataset(EXPENSE_REPORTS)
            .sort(COLUMN_AMOUNT, "asc")
            .buildLookup();

    /**
     * Sort by amount in descending order
     */
    DataSetLookup sortByAmountDesc = DataSetLookupFactory.newDataSetLookupBuilder()
            .dataset(EXPENSE_REPORTS)
            .sort(COLUMN_AMOUNT, "desc")
            .buildLookup();

    SharedDataSetOpEngine dataSetOpEngine = DataSetCore.get().getSharedDataSetOpEngine();

    @Before
    public void setUp() throws Exception {
        DataSet dataSet = ExpenseReportsData.INSTANCE.toDataSet();
        dataSet.setUUID(EXPENSE_REPORTS);
        dataSetOpEngine.getIndexRegistry().put(dataSet);
    }

    @Test
    public void testGroupPerformance() throws Exception {

        // Apply two different group operations and measure the elapsed time.
        long begin = System.nanoTime();
        int lookupTimes = 1000;
        for (int i = 0; i < lookupTimes; i++) {
            dataSetOpEngine.execute(EXPENSE_REPORTS, groupByDeptAndCount.getOperationList());
            dataSetOpEngine.execute(EXPENSE_REPORTS, groupByDeptAndSum.getOperationList());
        }
        long time = System.nanoTime() - begin;

        // Check out the resulting stats
        DataSetIndex dataSetIndex = dataSetOpEngine.getIndexRegistry().get(EXPENSE_REPORTS);
        DataSetIndexStats stats = dataSetIndex.getStats();
        DataSet dataSet = dataSetIndex.getDataSet();
        System.out.println(stats.toString("\n"));

        // Assert the reuse of group operations and aggregate calculations is working.
        assertThat(stats.getNumberOfGroupOps()).isEqualTo(1);
        assertThat(stats.getNumberOfAggFunctions()).isEqualTo(10);

        // The build time should be shorter than the overall lookup time.
        assertThat(stats.getBuildTime()).isLessThan(time);

        // The reuse rate must reflect the number of times the lookups are being reused.
        assertThat(stats.getReuseRate()).isGreaterThanOrEqualTo(lookupTimes - 1);

        // The index size must not be greater than the 20% of the dataset's size
        assertThat(stats.getIndexSize()).isLessThan(dataSet.getEstimatedSize() / 5);
    }

    @Test
    public void testFilterPerformance() throws Exception {
        // Apply a filter operation and measure the elapsed time.
        long begin = System.nanoTime();
        int lookupTimes = 1000;
        for (int i = 0; i < lookupTimes; i++) {
            dataSetOpEngine.execute(EXPENSE_REPORTS, filterByCityAndDept.getOperationList());
        }
        long time = System.nanoTime() - begin;

        // Check out the resulting stats
        DataSetIndex dataSetIndex = dataSetOpEngine.getIndexRegistry().get(EXPENSE_REPORTS);
        DataSetIndexStats stats = dataSetIndex.getStats();
        DataSet dataSet = dataSetIndex.getDataSet();

        System.out.println(stats.toString("\n"));

        // Assert reuse is working.
        assertThat(stats.getNumberOfFilterOps()).isEqualTo(2);

        // The build time should be shorter than the overall lookup time.
        assertThat(stats.getBuildTime()).isLessThan(time);

        // The reuse rate must reflect the number of times the lookups are being reused.
        assertThat(stats.getReuseRate()).isGreaterThanOrEqualTo(lookupTimes - 1);

        // The index size must not be greater than the 20% of the dataset's size
        assertThat(stats.getIndexSize()).isLessThan(dataSet.getEstimatedSize() / 5);
    }

    @Test
    public void testSortPerformance() throws Exception {

        // Apply the same sort operation several times and measure the elapsed time.
        long begin = System.nanoTime();
        int lookupTimes = 1000;
        for (int i = 0; i < lookupTimes; i++) {
            dataSetOpEngine.execute(EXPENSE_REPORTS, sortByAmountAsc.getOperationList());
            dataSetOpEngine.execute(EXPENSE_REPORTS, sortByAmountDesc.getOperationList());
        }
        long time = System.nanoTime() - begin;

        // Check out the resulting stats
        DataSetIndex dataSetIndex = dataSetOpEngine.getIndexRegistry().get(EXPENSE_REPORTS);
        DataSetIndexStats stats = dataSetIndex.getStats();
        DataSet dataSet = dataSetIndex.getDataSet();

        System.out.println(stats.toString("\n"));

        // Assert the reuse of sort operations is working.
        assertThat(stats.getNumberOfSortOps()).isEqualTo(2);

        // The build time should be shorter than the overall lookup time.
        assertThat(stats.getBuildTime()).isLessThan(time);

        // The reuse rate must reflect the number of times the lookups are being reused.
        assertThat(stats.getReuseRate()).isGreaterThanOrEqualTo(lookupTimes - 1);

        // The index size must not be greater than the 20% of the dataset's size
        assertThat(stats.getIndexSize()).isLessThan(dataSet.getEstimatedSize() / 5);
    }
}
