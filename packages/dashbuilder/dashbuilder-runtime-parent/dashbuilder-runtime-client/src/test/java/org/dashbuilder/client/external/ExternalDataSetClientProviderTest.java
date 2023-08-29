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

package org.dashbuilder.client.external;

import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.dataset.json.ExternalDataSetJSONParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExternalDataSetClientProviderTest {

    private ExternalDataSetJSONParser externalParser;

    private ExternalDataSetClientProvider provider;

    @Before
    public void prepare() {
        externalParser = new ExternalDataSetJSONParser(null);
        provider = new ExternalDataSetClientProvider();
    }

    @Test
    public void accumulateDataSetTest() {
        var provider = new ExternalDataSetClientProvider();

        var def = new ExternalDataSetDef();
        def.setAccumulate(true);

        var existingDs = externalParser.parseDataSet("[[2], [1]]");
        var ds = externalParser.parseDataSet("[[3]]");

        existingDs.setDefinition(def);
        ds.setDefinition(def);
        provider.accumulateDataSet(ds, existingDs);

        assertEquals(3, ds.getRowCount());
    }

    @Test
    public void accumulateDataSetOverflowTest() {
        var def = new ExternalDataSetDef();
        def.setAccumulate(true);
        def.setCacheMaxRows(2);

        var existingDs = externalParser.parseDataSet("[[2], [1]]");
        var ds = externalParser.parseDataSet("[[3]]");

        existingDs.setDefinition(def);
        ds.setDefinition(def);
        provider.accumulateDataSet(ds, existingDs);

        assertEquals(2, ds.getRowCount());
        assertEquals(3.0, ds.getValueAt(0, 0));
        assertEquals(2.0, ds.getValueAt(1, 0));
    }

    @Test(expected = RuntimeException.class)
    public void accumulateIncompatibleDataSetsTest() {
        var def = new ExternalDataSetDef();
        def.setAccumulate(true);
        def.setCacheMaxRows(2);

        var existingDs = externalParser.parseDataSet("[[1]]");
        var ds = externalParser.parseDataSet("[[\"a\", 1]]");

        existingDs.setDefinition(def);
        ds.setDefinition(def);

        provider.accumulateDataSet(ds, existingDs);
    }

}
