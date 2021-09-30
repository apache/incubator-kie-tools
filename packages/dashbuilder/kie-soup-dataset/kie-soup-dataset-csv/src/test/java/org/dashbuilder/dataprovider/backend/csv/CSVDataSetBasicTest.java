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
package org.dashbuilder.dataprovider.backend.csv;

import java.net.URL;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.csv.CSVDataSetProvider;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFormatter;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.junit.Before;
import org.junit.Test;

import static org.dashbuilder.dataset.Assertions.assertDataSetValues;
import static org.dashbuilder.dataset.filter.FilterFactory.lowerThan;
import static org.assertj.core.api.Assertions.assertThat;

public class CSVDataSetBasicTest {

    public static final String EXPENSE_REPORTS = "dataset_expense_reports";

    DataSetManager dataSetManager;
    DataSetDefRegistry dataSetDefRegistry;
    DataSetFormatter dataSetFormatter;
    DataSetProviderRegistry dataSetProviderRegistry;

    @Before
    public void setUp() throws Exception {
        dataSetDefRegistry = DataSetCore.get().getDataSetDefRegistry();
        dataSetManager = DataSetCore.get().getDataSetManager();
        dataSetProviderRegistry = DataSetCore.get().getDataSetProviderRegistry();
        dataSetProviderRegistry.registerDataProvider(CSVDataSetProvider.get());
        dataSetFormatter = new DataSetFormatter();

        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("expenseReports.csv");
        dataSetDefRegistry.registerDataSetDef(
                DataSetDefFactory.newCSVDataSetDef()
                        .uuid(EXPENSE_REPORTS)
                        .fileURL(fileURL.toString())
                        .label("id")
                        .label("office")
                        .label("department")
                        .label("author")
                        .date("date", "MM-dd-yyyy")
                        .number("amount", "#,###.##")
                        .separatorChar(';')
                        .quoteChar('\"')
                        .escapeChar('\\')
                        .buildDef());
    }

    @Test
    public void testLoadDataSet() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .buildLookup());

        //printDataSet(result);
        assertThat(result.getRowCount()).isEqualTo(50);
        assertThat(result.getColumns().size()).isEqualTo(6);
    }

    @Test
    public void testLookupDataSet() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSE_REPORTS)
                        .filter("amount", lowerThan(1000))
                        .group("department")
                        .column("department")
                        .column(AggregateFunctionType.COUNT, "#items")
                        .column("amount", AggregateFunctionType.SUM)
                        .buildLookup());

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"Engineering", "16.00", "6,547.56"},
                {"Services", "5.00", "2,504.50"},
                {"Sales", "8.00", "3,213.53"},
                {"Support", "6.00", "2,343.70"},
                {"Management", "11.00", "6,017.47"}
        }, 0);
    }

    private void printDataSet(DataSet dataSet) {
        System.out.print(dataSetFormatter.formatDataSet(dataSet, "{", "}", ",\n", "\"", "\"", ", ") + "\n\n");
    }
}
