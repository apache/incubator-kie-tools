/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.types.common;

import java.util.List;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataTypeUtilsTest {

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Mock
    private DataTypeStore dataTypeStore;

    @Mock
    private TranslationService translationService;

    @Mock
    private ItemDefinitionRecordEngine recordEngine;

    @Mock
    private ItemDefinitionStore itemDefinitionStore;

    @Mock
    private ManagedInstance<DataTypeManager> dataTypeManagers;

    @Mock
    private DataTypeManagerStackStore typeStack;

    private DataTypeManager dataTypeManager;

    private DataTypeUtils utils;

    @Before
    public void setup() {
        dataTypeManager = spy(new DataTypeManager(translationService, recordEngine, itemDefinitionStore, dataTypeStore, itemDefinitionUtils, dataTypeManagers, typeStack));
        utils = spy(new DataTypeUtils(dataTypeStore, dataTypeManager));
    }

    @Test
    public void testDefaultDataTypes() {

        final List<DataType> dataTypes = utils.defaultDataTypes();

        assertEquals(10, dataTypes.size());
        assertEquals("any", dataTypes.get(0).getType());
        assertEquals("boolean", dataTypes.get(1).getType());
        assertEquals("context", dataTypes.get(2).getType());
        assertEquals("date", dataTypes.get(3).getType());
        assertEquals("date and time", dataTypes.get(4).getType());
        assertEquals("days and time duration", dataTypes.get(5).getType());
        assertEquals("number", dataTypes.get(6).getType());
        assertEquals("string", dataTypes.get(7).getType());
        assertEquals("time", dataTypes.get(8).getType());
        assertEquals("years and months duration", dataTypes.get(9).getType());
    }

    @Test
    public void testCustomDataTypes() {

        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final List<DataType> expectedDataType = asList(dataType1, dataType2);

        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(expectedDataType);

        final List<DataType> actualDataTypes = utils.customDataTypes();

        assertEquals(expectedDataType, actualDataTypes);
    }
}
