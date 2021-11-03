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
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataprovider.csv.CSVDataSetProvider;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.dataset.def.CSVDataSetDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CSVDataSetDefJSONTest {
    
    DataSetDefJSONMarshaller jsonMarshaller;
    DataSetManager dataSetManager;
    DataSetProviderRegistry dataSetProviderRegistry;

    @Before
    public void setUp() {
        dataSetProviderRegistry = DataSetCore.get().getDataSetProviderRegistry();
        dataSetProviderRegistry.registerDataProvider(CSVDataSetProvider.get());
        jsonMarshaller = DataSetCore.get().getDataSetDefJSONMarshaller();
        dataSetManager = DataSetCore.get().getDataSetManager();
    }
    
    @Test
    public void testParseCSVJson() throws Exception {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("expenseReports.dset");
        String json = IOUtils.toString(fileURL, StandardCharsets.UTF_8);

        DataSetDef def = jsonMarshaller.fromJson(json);
        assertThat(def.getProvider()).isEqualTo(DataSetProviderType.CSV);
        assertThat(def.isPublic()).isEqualTo(true);
        assertThat(def.isPushEnabled()).isEqualTo(true);
        assertThat(def.getPushMaxSize()).isEqualTo(1024);

        CSVDataSetDef csvDef = (CSVDataSetDef) def;
        assertThat(csvDef.getSeparatorChar()).isEqualTo(';');
        assertThat(csvDef.getEscapeChar()).isEqualTo('\\');
        assertThat(csvDef.getQuoteChar()).isEqualTo('\"');
        assertThat(csvDef.getDatePattern()).isEqualTo("MM-dd-yyyy");
        assertThat(csvDef.getNumberPattern()).isEqualTo("#,###.##");

        assertThat(csvDef.getPattern("date")).isEqualTo("MM-dd-yyyy");
        assertThat(csvDef.getPattern("amount")).isEqualTo("#,###.##");

        assertThat(csvDef.getColumnById("id").getColumnType()).isEqualTo(ColumnType.LABEL);
        assertThat(csvDef.getColumnById("amount").getColumnType()).isEqualTo(ColumnType.NUMBER);
        assertThat(csvDef.getColumnById("date").getColumnType()).isEqualTo(ColumnType.DATE);
    }
}
