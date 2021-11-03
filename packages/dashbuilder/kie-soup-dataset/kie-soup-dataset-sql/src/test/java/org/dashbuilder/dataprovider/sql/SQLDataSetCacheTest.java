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

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: Review
@Ignore
public class SQLDataSetCacheTest extends SQLDataSetTestBase {

    @Test
    public void testDataSetNonCached() throws Exception {
        // A non cached DB always ask for content to the DB
        _testDataSetCache("noncached", 100);
    }

    @Test
    public void testDataSetStaticCache() throws Exception {
        // A cached (static) do not see the changed on the DB
        _testDataSetCache("static", 50);
    }

    @Override
    public void testAll() throws Exception {
        testDataSetNonCached();
        testDataSetStaticCache();
    }

    protected void _testDataSetCache(String scenario, int rows) throws Exception {

        // Register the data set definition
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("expenseReports_" + scenario + ".dset");
        String json = IOUtils.toString(fileURL, StandardCharsets.UTF_8);
        SQLDataSetDef def = (SQLDataSetDef) jsonMarshaller.fromJson(json);
        dataSetDefRegistry.registerDataSetDef(def);

        // Lookup the dataset (forces the caches to initialize)
        dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset("expense_reports_" + scenario)
                        .buildLookup());

        // Insert some extra rows into the database
        populateDbTable();

        // Ensure the data set is now outdated
        assertThat(sqlDataSetProvider.isDataSetOutdated(def)).isEqualTo(true);

        // Lookup the last database content
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset("expense_reports_" + scenario)
                        .buildLookup());

        assertThat(result.getRowCount()).isEqualTo(rows);
    }
}
