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

package org.kie.workbench.common.dmn.client.editors.types.persistence;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeDestroyHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeUpdateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionDestroyHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionUpdateHandler;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ItemDefinitionRecordEngineTest {

    @Mock
    private ItemDefinitionStore itemDefinitionStore;

    @Mock
    private ItemDefinitionDestroyHandler itemDefinitionDestroyHandler;

    @Mock
    private ItemDefinitionUpdateHandler itemDefinitionUpdateHandler;

    @Mock
    private DataTypeDestroyHandler dataTypeDestroyHandler;

    @Mock
    private DataTypeUpdateHandler dataTypeUpdateHandler;

    private ItemDefinitionRecordEngine recordEngine;

    @Before
    public void setup() {
        recordEngine = spy(new ItemDefinitionRecordEngine(itemDefinitionStore, itemDefinitionDestroyHandler, itemDefinitionUpdateHandler, dataTypeDestroyHandler, dataTypeUpdateHandler));
    }

    @Test
    public void testInit() {
        recordEngine.init();

        verify(dataTypeDestroyHandler).init(recordEngine);
        verify(dataTypeUpdateHandler).init(recordEngine);
    }

    @Test
    public void testUpdate() {

        final String uuid = "uuid";
        final String nameValue = "nameValue";
        final DataType dataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final Name name = mock(Name.class);
        final List<DataType> expectedDependentDataTypes = asList(mock(DataType.class), mock(DataType.class));

        when(dataType.getUUID()).thenReturn(uuid);
        when(itemDefinitionStore.get(uuid)).thenReturn(itemDefinition);
        when(itemDefinition.getName()).thenReturn(name);
        when(name.getValue()).thenReturn(nameValue);
        when(dataTypeUpdateHandler.refreshDependentDataTypes(dataType, nameValue)).thenReturn(expectedDependentDataTypes);

        final List<DataType> actualDependentDataTypes = recordEngine.update(dataType);

        verify(recordEngine).doUpdate(dataType, itemDefinition);
        assertEquals(expectedDependentDataTypes, actualDependentDataTypes);
    }

    @Test
    public void testDoUpdate() {

        final DataType dataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);

        recordEngine.doUpdate(dataType, itemDefinition);

        verify(dataTypeUpdateHandler).update(dataType, itemDefinition);
        verify(itemDefinitionUpdateHandler).update(dataType, itemDefinition);
    }

    @Test
    public void testDestroy() {

        final DataType dataType = mock(DataType.class);
        final List<DataType> expectedDependentDataTypes = asList(mock(DataType.class), mock(DataType.class));

        when(dataTypeDestroyHandler.refreshDependentDataTypes(dataType)).thenReturn(expectedDependentDataTypes);

        final List<DataType> actualDependentDataTypes = recordEngine.destroy(dataType);

        verify(recordEngine).doDestroy(dataType);
        assertEquals(expectedDependentDataTypes, actualDependentDataTypes);
    }

    @Test
    public void testDoDestroy() {

        final DataType dataType = mock(DataType.class);

        recordEngine.doDestroy(dataType);

        verify(dataTypeDestroyHandler).destroy(dataType);
        verify(itemDefinitionDestroyHandler).destroy(dataType);
    }
}
