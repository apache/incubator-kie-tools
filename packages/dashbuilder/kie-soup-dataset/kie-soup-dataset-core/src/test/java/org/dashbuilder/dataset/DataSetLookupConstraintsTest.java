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
import org.junit.Before;
import org.junit.Test;

import static org.dashbuilder.dataset.ColumnType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.dashbuilder.dataset.Assertions.*;

public class DataSetLookupConstraintsTest {

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
    public void testEmptyConstraints() throws Exception {

        DataSetLookupConstraints constraints = new DataSetLookupConstraints()
                .setColumnTypes(null)
                .setGroupRequired(false);

        DataSetLookup lookup = constraints.newDataSetLookup(dataSet.getMetadata());
        assertThat(constraints.check(lookup)).isNull();

        DataSet result = dataSetManager.lookupDataSet(lookup);
        assertThat(result.getColumns().size()).isEqualTo(6);
        assertThat(result.getRowCount()).isEqualTo(50);
    }

    @Test
    public void testGroupWithOneCalculation() throws Exception {
        DataSetLookupConstraints constraints = new DataSetLookupConstraints()
                .setColumnTypes(new ColumnType[] {LABEL, NUMBER})
                .setGroupRequired(true);

        DataSetLookup lookup = constraints.newDataSetLookup(dataSet.getMetadata());
        assertThat(constraints.check(lookup)).isNull();

        DataSet result = dataSetManager.lookupDataSet(lookup);
        assertDataSetValues(result, dataSetFormatter, new String[][] {
                {"Barcelona", "21.00"},
                {"Madrid", "57.00"},
                {"Brno", "153.00"},
                {"Westford", "234.00"},
                {"Raleigh", "481.00"},
                {"London", "329.00"}
        }, 0);
    }

    @Test
    public void testGroupWithLabels() throws Exception {
        DataSetLookupConstraints constraints = new DataSetLookupConstraints()
                .setColumnTypes(new ColumnType[] {LABEL, LABEL})
                .setGroupRequired(true);

        DataSetLookup lookup = constraints.newDataSetLookup(dataSet.getMetadata());
        assertThat(constraints.check(lookup)).isNull();

        DataSet result = dataSetManager.lookupDataSet(lookup);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"Barcelona", "Engineering"},
                {"Madrid", "Services"},
                {"Brno", "Support"},
                {"Westford", "Engineering"},
                {"Raleigh", "Management"},
                {"London", "Engineering"}
        }, 0);
    }

    @Test
    public void testGroupMultipleColumns() throws Exception {
        DataSetLookupConstraints constraints = new DataSetLookupConstraints()
                .setColumnTypes(new ColumnType[] {LABEL, NUMBER, NUMBER, NUMBER})
                .setGroupRequired(true);

        DataSetLookup lookup = constraints.newDataSetLookup(dataSet.getMetadata());
        assertThat(constraints.check(lookup)).isNull();

        DataSet result = dataSetManager.lookupDataSet(lookup);
        assertDataSetValues(result, dataSetFormatter, new String[][] {
                 {"Barcelona", "21.00", "2,913.14", "21.00"},
                 {"Madrid", "57.00", "2,453.36", "57.00"},
                 {"Brno", "153.00", "4,659.24", "153.00"},
                 {"Westford", "234.00", "3,594.97", "234.00"},
                 {"Raleigh", "481.00", "4,970.78", "481.00"},
                 {"London", "329.00", "4,139.77", "329.00"}
         }, 0);
    }

    private void printDataSet(DataSet dataSet) {
        System.out.print(dataSetFormatter.formatDataSet(dataSet, "{", "}", ",\n", "\"", "\"", ", ") + "\n\n");
    }
}
