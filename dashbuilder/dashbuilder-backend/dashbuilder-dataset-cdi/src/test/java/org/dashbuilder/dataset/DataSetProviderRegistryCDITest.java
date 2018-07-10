/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import org.dashbuilder.dataprovider.*;
import org.dashbuilder.test.BaseCDITest;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@Ignore("see https://issues.jboss.org/browse/RHPAM-832")
public class DataSetProviderRegistryCDITest extends BaseCDITest {

    @Inject
    DataSetProviderRegistryCDI dataSetProviderRegistry;

    @Before
    public void setUp() {
        dataSetProviderRegistry.init();
    }

    @Test
    public void testRegistryDataSetDef() throws Exception {
        Set<DataSetProviderType> typeList = dataSetProviderRegistry.getAvailableTypes();
        assertTrue(typeList.contains(DataSetProviderType.STATIC));
        assertTrue(typeList.contains(DataSetProviderType.BEAN));
        assertTrue(typeList.contains(DataSetProviderType.CSV));
        assertTrue(typeList.contains(DataSetProviderType.SQL));
        assertTrue(typeList.contains(DataSetProviderType.ELASTICSEARCH));

        // In CDI contexts, DataSetProvider implementations are automatically registered
        assertTrue(typeList.contains(CustomDataSetProvider.TYPE));
        DataSetProvider customProvider = dataSetProviderRegistry.getDataSetProvider(CustomDataSetProvider.TYPE);
        assertNotNull(customProvider);
    }
}
