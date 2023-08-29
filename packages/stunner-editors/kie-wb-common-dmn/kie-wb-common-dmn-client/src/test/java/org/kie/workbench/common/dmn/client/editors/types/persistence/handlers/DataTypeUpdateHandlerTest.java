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

package org.kie.workbench.common.dmn.client.editors.types.persistence.handlers;

import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
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
import static org.mockito.ArgumentMatchers.any;
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

        when(dataTypeManager.withDataType(any())).thenCallRealMethod();
        when(dataTypeManager.getTypeName()).thenCallRealMethod();
    }

    @Test
    public void testUpdateWhenDataTypeIsStructure() {

        final DataType dataType = mock(DataType.class);

        doReturn(true).when(handler).isStructure(dataType);
        when(dataTypeManager.from(dataType)).thenReturn(dataTypeManager);

        handler.update(dataType);

        verify(dataTypeManager, never()).withSubDataTypes(emptyList());
    }

    @Test
    public void testUpdateWhenDataTypeIsNotStructure() {

        final DataType dataType = mock(DataType.class);

        doReturn(false).when(handler).isStructure(dataType);
        when(dataTypeManager.from(dataType)).thenReturn(dataTypeManager);

        handler.update(dataType);

        verify(dataTypeManager).withSubDataTypes(emptyList());
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

        final List<DataType> expectedDataTypes = asList(dataType0, dataType1, dataType2, topLevelDataType);
        final List<DataType> actualDataTypes = handler.handleNestedDataTypeFieldUpdate(dataType);

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

        final List<DataType> expectedDataTypes = asList(dataType1, dataType2, topLevelDataType);
        final List<DataType> actualDataTypes = handler.handleNestedDataTypeFieldUpdate(dataType);

        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testUpdateAllChildrenWithTheNewTypeNameWhenDataTypeIsNotTopLevel() {

        final DataType topLevelDataType = mock(DataType.class);
        final String oldItemDefinitionName = "oldItemDefinitionName";

        when(topLevelDataType.isTopLevel()).thenReturn(false);

        final List<DataType> expectedDependentDataTypes = emptyList();
        final List<DataType> actualDependentDataTypes = handler.updateAllChildrenWithTheNewTypeName(topLevelDataType, oldItemDefinitionName);

        verify(handler, never()).refreshSubDataType(any(), Mockito.<String>any());

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
        doNothing().when(handler).refreshSubDataType(any(), Mockito.<String>any());

        final List<DataType> expectedDependentDataTypes = asList(dataType0, dataType1, dataType2, dataType3, topLevelDataType);
        final List<DataType> actualDependentDataTypes = handler.updateAllChildrenWithTheNewTypeName(topLevelDataType, oldItemDefinitionName);

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
    }
}
