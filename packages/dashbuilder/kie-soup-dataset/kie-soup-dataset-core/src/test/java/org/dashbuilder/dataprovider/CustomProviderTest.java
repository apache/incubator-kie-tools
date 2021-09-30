/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.dashbuilder.dataprovider;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomProviderTest {

    CustomDataSetProvider customProvider = spy(CustomDataSetProvider.get());
    DataSetProviderRegistry providerRegistry = DataSetCore.get().getDataSetProviderRegistry();
    DataSetDefRegistry dataSetDefRegistry = DataSetCore.get().getDataSetDefRegistry();
    DataSetManager dataSetManager = DataSetCore.get().getDataSetManager();
    DataSetDef customDef = new DataSetDef();

    @Before
    public void setUp() {
        providerRegistry.registerDataProvider(customProvider);

        customDef.setProvider(customProvider.getType());
        customDef.setUUID("test");
        dataSetDefRegistry.registerDataSetDef(customDef);
    }

    @Test
    public void testRegistry() throws Exception {
        DataSetProviderType type = providerRegistry.getProviderTypeByName("CUSTOM");
        assertEquals(customProvider.getType(), CustomDataSetProvider.TYPE);
        assertEquals(type, CustomDataSetProvider.TYPE);
    }

    @Test
    public void testMetadata() throws Exception {
        DataSetMetadata medatata = dataSetManager.getDataSetMetadata("test");

        verify(customProvider).getDataSetMetadata(customDef);
        assertEquals(medatata.getNumberOfColumns(), 1);
        assertEquals(medatata.getColumnId(0), "name");
    }

    @Test
    public void testLookup() throws Exception {
        DataSetLookup lookup = DataSetLookupFactory
                .newDataSetLookupBuilder().dataset("test")
                .buildLookup();

        DataSet dataSet = dataSetManager.lookupDataSet(lookup);

        verify(customProvider).lookupDataSet(customDef, lookup);
        assertEquals(dataSet.getRowCount(), 2);
        assertEquals(dataSet.getValueAt(0, 0), "david");
        assertEquals(dataSet.getValueAt(1, 0), "maciejs");
   }
}