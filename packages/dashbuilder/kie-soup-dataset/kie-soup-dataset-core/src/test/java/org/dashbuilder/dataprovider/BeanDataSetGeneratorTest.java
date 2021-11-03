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
package org.dashbuilder.dataprovider;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFormatter;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.junit.Before;
import org.junit.Test;

import static org.dashbuilder.dataset.Assertions.*;

public class BeanDataSetGeneratorTest {

    DataSetManager dataSetManager;
    DataSetDefRegistry dataSetDefRegistry;
    DataSetFormatter dataSetFormatter;
    DataSetDefJSONMarshaller jsonMarshaller;

    @Before
    public void setUp() throws Exception {
        dataSetManager = DataSetCore.get().getDataSetManager();
        dataSetDefRegistry = DataSetCore.get().getDataSetDefRegistry();
        dataSetFormatter = new DataSetFormatter();
        jsonMarshaller = DataSetCore.get().getDataSetDefJSONMarshaller();
    }

    @Test
    public void testGenerateDataSet() throws Exception {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("salesPerYear.dset");
        String json = IOUtils.toString(fileURL, StandardCharsets.UTF_8);
        DataSetDef def = jsonMarshaller.fromJson(json);
        dataSetDefRegistry.registerDataSetDef(def);
        DataSet result = dataSetManager.getDataSet("salesPerYear");

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][]{
                {"JANUARY", "1,000.00", "2,000.00", "3,000.00"},
                {"FEBRUARY", "1,400.00", "2,300.00", "2,000.00"},
                {"MARCH", "1,300.00", "2,000.00", "1,400.00"},
                {"APRIL", "900.00", "2,100.00", "1,500.00"},
                {"MAY", "1,300.00", "2,300.00", "1,600.00"},
                {"JUNE", "1,010.00", "2,000.00", "1,500.00"},
                {"JULY", "1,050.00", "2,400.00", "3,000.00"},
                {"AUGUST", "2,300.00", "2,000.00", "3,200.00"},
                {"SEPTEMBER", "1,900.00", "2,700.00", "3,000.00"},
                {"OCTOBER", "1,200.00", "2,200.00", "3,100.00"},
                {"NOVEMBER", "1,400.00", "2,100.00", "3,100.00"},
                {"DECEMBER", "1,100.00", "2,100.00", "4,200.00"}
        }, 0);
    }

    @Test
    public void testGenerateDataSetAdjusted() throws Exception {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("salesPerYearAdjusted.dset");
        String json = IOUtils.toString(fileURL, StandardCharsets.UTF_8);
        DataSetDef def = jsonMarshaller.fromJson(json);
        dataSetDefRegistry.registerDataSetDef(def);
        DataSet result = dataSetManager.getDataSet("salesPerYearAdjusted");

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][] {
                {"JANUARY", "900.00", "1,800.00", "2,700.00"},
                {"FEBRUARY", "1,260.00", "2,070.00", "1,800.00"},
                {"MARCH", "1,170.00", "1,800.00", "1,260.00"},
                {"APRIL", "810.00", "1,890.00", "1,350.00"},
                {"MAY", "1,170.00", "2,070.00", "1,440.00"},
                {"JUNE", "909.00", "1,800.00", "1,350.00"},
                {"JULY", "945.00", "2,160.00", "2,700.00"},
                {"AUGUST", "2,070.00", "1,800.00", "2,880.00"},
                {"SEPTEMBER", "1,710.00", "2,430.00", "2,700.00"},
                {"OCTOBER", "1,080.00", "1,980.00", "2,790.00"},
                {"NOVEMBER", "1,260.00", "1,890.00", "2,790.00"},
                {"DECEMBER", "990.00", "1,890.00", "3,780.00"}
        }, 0);
    }

    @Test
    public void testRetrieveColumnSubset() throws Exception {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("salesYear2014.dset");
        String json = IOUtils.toString(fileURL, StandardCharsets.UTF_8);
        DataSetDef def = jsonMarshaller.fromJson(json);
        dataSetDefRegistry.registerDataSetDef(def);
        DataSet result = dataSetManager.getDataSet("salesYear2014");

        //printDataSet(result);
        assertDataSetValues(result, dataSetFormatter, new String[][] {
                {"JANUARY", "3,000.00"},
                {"FEBRUARY", "2,000.00"},
                {"MARCH", "1,400.00"},
                {"APRIL", "1,500.00"},
                {"MAY", "1,600.00"},
                {"JUNE", "1,500.00"},
                {"JULY", "3,000.00"},
                {"AUGUST", "3,200.00"},
                {"SEPTEMBER", "3,000.00"},
                {"OCTOBER", "3,100.00"},
                {"NOVEMBER", "3,100.00"},
                {"DECEMBER", "4,200.00"}
        }, 0);
    }

    private void printDataSet(DataSet dataSet) {
        System.out.print(dataSetFormatter.formatDataSet(dataSet, "{", "}", ",\n", "\"", "\"", ", ") + "\n\n");
    }

}
