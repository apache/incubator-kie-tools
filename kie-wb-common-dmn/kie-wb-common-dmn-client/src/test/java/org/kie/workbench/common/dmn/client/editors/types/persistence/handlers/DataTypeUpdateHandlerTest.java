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

package org.kie.workbench.common.dmn.client.editors.types.persistence.handlers;

import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeUpdateHandlerTest {

    @Mock
    private ItemDefinitionStore itemDefinitionStore;

    @Mock
    private DataTypeStore dataTypeStore;

    @Mock
    private DataTypeManager dataTypeManager;

    @Mock
    private ItemDefinitionRecordEngine recordEngine;

    private DataTypeUpdateHandler handler;

    @Before
    public void setup() {
        handler = spy(new DataTypeUpdateHandler(itemDefinitionStore, dataTypeStore, dataTypeManager));
        handler.init(recordEngine);
    }

    @Test
    public void testUpdateWhenDataTypeSubTypesIsNotRefresh() {

        final DataType dataType = mock(DataType.class);
        final String structure = "Structure";
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);

        when(dataType.getType()).thenReturn("type");
        when(itemDefinition.getTypeRef()).thenReturn(null);
        when(dataTypeManager.structure()).thenReturn(structure);

        handler.update(dataType, itemDefinition);

        verify(dataTypeManager, never()).from(any(DataType.class));
        verify(dataTypeManager, never()).withRefreshedSubDataTypes(anyString());
    }

    @Test
    public void testUpdateWhenDataTypeIsStructure() {

        final DataType dataType = mock(DataType.class);
        final String name = "tCity";
        final String structure = "Structure";
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);

        when(dataType.getName()).thenReturn(name);
        when(dataType.getType()).thenReturn(structure);
        when(itemDefinition.getTypeRef()).thenReturn(null);
        when(dataTypeManager.structure()).thenReturn(structure);
        when(dataTypeManager.from(any(DataType.class))).thenReturn(dataTypeManager);
        when(dataTypeManager.withRefreshedSubDataTypes(anyString())).thenReturn(dataTypeManager);

        handler.update(dataType, itemDefinition);

        final InOrder inOrder = Mockito.inOrder(dataTypeManager);

        inOrder.verify(dataTypeManager).from(dataType);
        inOrder.verify(dataTypeManager).withRefreshedSubDataTypes(name);
    }

    @Test
    public void testUpdateWhenItemDefinitionIsStructure() {

        final DataType dataType = mock(DataType.class);
        final String name = "tCity";
        final String structure = "Structure";
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);

        when(dataType.getName()).thenReturn(name);
        when(dataType.getType()).thenReturn("type");
        when(itemDefinition.getTypeRef()).thenReturn(mock(QName.class));
        when(dataTypeManager.structure()).thenReturn(structure);
        when(dataTypeManager.from(any(DataType.class))).thenReturn(dataTypeManager);
        when(dataTypeManager.withRefreshedSubDataTypes(anyString())).thenReturn(dataTypeManager);

        handler.update(dataType, itemDefinition);

        final InOrder inOrder = Mockito.inOrder(dataTypeManager);

        inOrder.verify(dataTypeManager).from(dataType);
        inOrder.verify(dataTypeManager).withRefreshedSubDataTypes(name);
    }

    @Test
    public void testRefreshDependentDataTypes() {

        final DataType dataType = mock(DataType.class);
        final String oldItemDefinitionName = "oldItemDefinitionName";
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);

        doReturn(asList(dataType0, dataType1)).when(handler).handleTopLevelDataTypeUpdate(dataType, oldItemDefinitionName);
        doReturn(asList(dataType2, dataType3)).when(handler).handleNestedDataTypeFieldUpdate(dataType);

        final List<DataType> expectedDataTypes = asList(dataType0, dataType1, dataType2, dataType3);
        final List<DataType> actualDataTypes = handler.refreshDependentDataTypes(dataType, oldItemDefinitionName);

        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testHandleTopLevelDataTypeUpdate() {

        final DataType dataType = mock(DataType.class);
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final List<DataType> expectedDataTypes = asList(dataType0, dataType1);
        final String oldItemDefinitionName = "oldItemDefinitionName";

        doReturn(expectedDataTypes).when(handler).updateAllChildrenWithTheNewTypeName(dataType, oldItemDefinitionName);

        final List<DataType> actualDataTypes = handler.handleTopLevelDataTypeUpdate(dataType, oldItemDefinitionName);

        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testHandleNestedDataTypeFieldUpdateWhenDataTypeIsStructure() {

        final DataType dataType = mock(DataType.class);
        final DataType topLevelDataType = mock(DataType.class);
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final List<DataType> dependentDataTypes = asList(dataType0, dataType1, dataType2);
        final String name = "name";

        when(topLevelDataType.getName()).thenReturn(name);
        doReturn(Optional.of(topLevelDataType)).when(handler).getClosestTopLevelDataType(dataType);
        doReturn(dependentDataTypes).when(handler).handleTopLevelDataTypeUpdate(topLevelDataType, name);
        doReturn(true).when(handler).isStructure(dataType);
        doNothing().when(handler).refreshSubDataTypes(any(), anyString());

        final List<DataType> expectedDataTypes = asList(dataType0, dataType1, dataType2, topLevelDataType);
        final List<DataType> actualDataTypes = handler.handleNestedDataTypeFieldUpdate(dataType);

        verify(handler).refreshSubDataTypes(topLevelDataType, name);
        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testHandleNestedDataTypeFieldUpdateWhenDataTypeIsNotStructure() {

        final DataType dataType = mock(DataType.class);
        final DataType topLevelDataType = mock(DataType.class);
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final String name = "name";
        final String type = "type";

        when(dataType0.getType()).thenReturn(name);
        when(dataType1.getType()).thenReturn(type);
        when(dataType2.getName()).thenReturn(type);
        when(topLevelDataType.getName()).thenReturn(name);
        when(topLevelDataType.getType()).thenReturn(type);
        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(asList(dataType0, dataType1, dataType2));
        doReturn(Optional.of(topLevelDataType)).when(handler).getClosestTopLevelDataType(dataType);
        doReturn(false).when(handler).isStructure(dataType);
        doNothing().when(handler).refreshSubDataTypes(any(), anyString());

        final List<DataType> expectedDataTypes = asList(dataType1, dataType2, topLevelDataType);
        final List<DataType> actualDataTypes = handler.handleNestedDataTypeFieldUpdate(dataType);

        verify(handler).refreshSubDataTypes(topLevelDataType, name);
        verify(handler).refreshSubDataTypes(dataType1, type);
        verify(handler).refreshSubDataTypes(dataType2, type);
        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testUpdateAllChildrenWithTheNewTypeNameWhenDataTypeIsNotTopLevel() {

        final DataType topLevelDataType = mock(DataType.class);
        final String oldItemDefinitionName = "oldItemDefinitionName";

        when(topLevelDataType.isTopLevel()).thenReturn(false);

        final List<DataType> expectedDependentDataTypes = emptyList();
        final List<DataType> actualDependentDataTypes = handler.updateAllChildrenWithTheNewTypeName(topLevelDataType, oldItemDefinitionName);

        verify(handler, never()).refreshSubDataTypes(any(), anyString());
        verify(handler, never()).refreshSubDataType(any(), anyString());

        assertEquals(expectedDependentDataTypes, actualDependentDataTypes);
    }

    @Test
    public void testUpdateAllChildrenWithTheNewTypeNameWhenDataTypeIsTopLevel() {

        final DataType topLevelDataType = mock(DataType.class);
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final String oldItemDefinitionName = "oldItemDefinitionName";
        final String topLevelName = "topLevelName";

        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(asList(dataType0, dataType1, dataType2, dataType3));
        when(topLevelDataType.getName()).thenReturn(topLevelName);
        when(dataType0.getType()).thenReturn(oldItemDefinitionName);
        when(dataType1.getType()).thenReturn(oldItemDefinitionName);
        when(dataType2.getType()).thenReturn(oldItemDefinitionName);
        when(dataType3.getType()).thenReturn(oldItemDefinitionName);
        when(topLevelDataType.isTopLevel()).thenReturn(true);
        doNothing().when(handler).refreshSubDataTypes(any(), anyString());
        doNothing().when(handler).refreshSubDataType(any(), anyString());

        final List<DataType> expectedDependentDataTypes = asList(dataType0, dataType1, dataType2, dataType3, topLevelDataType);
        final List<DataType> actualDependentDataTypes = handler.updateAllChildrenWithTheNewTypeName(topLevelDataType, oldItemDefinitionName);

        verify(handler).refreshSubDataTypes(topLevelDataType, topLevelName);
        verify(handler).refreshSubDataType(dataType0, topLevelName);
        verify(handler).refreshSubDataType(dataType1, topLevelName);
        verify(handler).refreshSubDataType(dataType2, topLevelName);
        verify(handler).refreshSubDataType(dataType3, topLevelName);

        assertEquals(expectedDependentDataTypes, actualDependentDataTypes);
    }

    @Test
    public void testRefreshSubDataType() {

        final DataType dataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final String newType = "newType";
        final String type = "type";
        final String uuid = "uuid";

        when(dataType.getType()).thenReturn(type);
        when(dataType.getUUID()).thenReturn(uuid);
        when(itemDefinitionStore.get(uuid)).thenReturn(itemDefinition);
        when(dataTypeManager.from(dataType)).thenReturn(dataTypeManager);
        when(dataTypeManager.withType(newType)).thenReturn(dataTypeManager);

        handler.refreshSubDataType(dataType, newType);

        final InOrder inOrder = Mockito.inOrder(dataTypeManager, dataTypeManager, recordEngine, handler);

        inOrder.verify(dataTypeManager).from(dataType);
        inOrder.verify(dataTypeManager).withType(newType);
        inOrder.verify(recordEngine).doUpdate(dataType, itemDefinition);
        inOrder.verify(handler).refreshSubDataTypes(dataType, type);
    }
}