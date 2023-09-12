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
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeDestroyHandlerTest {

    @Mock
    private DataTypeStore dataTypeStore;

    @Mock
    private DataTypeManager dataTypeManager;

    @Mock
    private ItemDefinitionRecordEngine recordEngine;

    private DataTypeDestroyHandler handler;

    @Before
    public void setup() {
        handler = spy(new DataTypeDestroyHandler(dataTypeStore, dataTypeManager));
        handler.init(recordEngine);
    }

    @Test
    public void testDestroy() {

        final String uuid = "uuid";
        final String parentUUID = "parentUUID";
        final String childUUID1 = "childUUID1";
        final String childUUID2 = "childUUID2";
        final String grandchildUUID1 = "grandchildUUID1";
        final String grandchildUUID2 = "grandchildUUID2";
        final DataType[] grandchildren = {makeDataType(grandchildUUID1), makeDataType(grandchildUUID2)};
        final DataType[] children = {makeDataType(childUUID1), makeDataType(childUUID2, grandchildren)};
        final DataType dataType = makeDataType(uuid, children);
        final DataType parent = makeDataType(parentUUID, dataType);

        doReturn(parent).when(handler).parent(dataType);

        handler.destroy(dataType);

        verify(dataTypeStore).unIndex(uuid);
        verify(dataTypeStore).unIndex(childUUID1);
        verify(dataTypeStore).unIndex(childUUID2);
        verify(dataTypeStore).unIndex(grandchildUUID1);
        verify(dataTypeStore).unIndex(grandchildUUID2);
        assertEquals(emptyList(), parent.getSubDataTypes());
    }

    @Test
    public void testRefreshDependentDataTypes() {

        final DataType dataType = mock(DataType.class);
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);

        doReturn(asList(dataType0, dataType1)).when(handler).handleTopLevelDataTypes(dataType);
        doReturn(asList(dataType2, dataType3)).when(handler).handleNestedDataTypes(dataType);

        final List<DataType> expectedDataTypes = asList(dataType0, dataType1, dataType2, dataType3);
        final List<DataType> actualDataTypes = handler.refreshDependentDataTypes(dataType);

        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testHandleTopLevelDataTypesWhenDataTypeIsNotTopLevel() {

        final DataType dataType = mock(DataType.class);

        when(dataType.isTopLevel()).thenReturn(false);

        final List<DataType> expectedDependentDataTypes = emptyList();
        final List<DataType> actualDependentDataTypes = handler.handleTopLevelDataTypes(dataType);

        verify(recordEngine, never()).doDestroy(any());

        assertEquals(expectedDependentDataTypes, actualDependentDataTypes);
    }

    @Test
    public void testHandleTopLevelDataTypesWhenDataTypeIsTopLevel() {

        final DataType dataType = mock(DataType.class);
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataTypeParent0 = mock(DataType.class);
        final DataType dataTypeParent1 = mock(DataType.class);
        final String topLevelName = "name";

        when(dataType.isTopLevel()).thenReturn(true);
        when(dataType.getName()).thenReturn(topLevelName);
        when(dataType0.getType()).thenReturn(topLevelName);
        when(dataType1.getType()).thenReturn(topLevelName);
        when(dataType2.getType()).thenReturn(topLevelName);
        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(asList(dataType0, dataType1, dataType2));
        doReturn(dataTypeParent0).when(handler).parent(dataType0);
        doReturn(dataTypeParent1).when(handler).parent(dataType1);

        final List<DataType> expectedDependentDataTypes = asList(dataTypeParent0, dataTypeParent1, dataType2, dataType);
        final List<DataType> actualDependentDataTypes = handler.handleTopLevelDataTypes(dataType);

        verify(recordEngine).doDestroy(dataType0);
        verify(recordEngine).doDestroy(dataType1);
        verify(recordEngine).doDestroy(dataType2);

        assertEquals(expectedDependentDataTypes, actualDependentDataTypes);
    }

    @Test
    public void testHandleNestedDataTypes() {

        final DataType dataType = mock(DataType.class);
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final DataType dataTypeParent0 = mock(DataType.class);
        final DataType dataTypeParent1 = mock(DataType.class);
        final DataType dataTypeParent2 = mock(DataType.class);
        final DataType dataTypeParent3 = mock(DataType.class);
        final DataType topLevelDataType = mock(DataType.class);
        final String topLevelName = "name";
        final String topLevelType = "type";

        when(topLevelDataType.getName()).thenReturn(topLevelName);
        when(topLevelDataType.getType()).thenReturn(topLevelType);
        when(dataType0.getType()).thenReturn(topLevelName);
        when(dataType1.getType()).thenReturn(topLevelName);
        when(dataType2.getType()).thenReturn(topLevelName);
        when(dataType3.getType()).thenReturn(topLevelType);
        when(dataType3.isTopLevel()).thenReturn(true);
        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(asList(dataType0, dataType1, dataType2, dataType3));
        doReturn(dataTypeParent0).when(handler).parent(dataType0);
        doReturn(dataTypeParent1).when(handler).parent(dataType1);
        doReturn(dataTypeParent2).when(handler).parent(dataType2);
        doReturn(dataTypeParent3).when(handler).parent(dataType3);
        doReturn(Optional.of(topLevelDataType)).when(handler).getClosestTopLevelDataType(dataType);
        doReturn(false).when(handler).isStructure(topLevelDataType);

        final List<DataType> expectedDependentDataTypes = asList(topLevelDataType, dataType3, dataType0, dataType1, dataType2);
        final List<DataType> actualDependentDataTypes = handler.handleNestedDataTypes(dataType);

        assertEquals(expectedDependentDataTypes, actualDependentDataTypes);
    }

    private DataType makeDataType(final String uuid,
                                  final DataType... subDataTypes) {

        final DataType dataType = spy(new DataType(null));

        doReturn(uuid).when(dataType).getUUID();
        dataType.getSubDataTypes().addAll(asList(subDataTypes));

        return dataType;
    }
}
