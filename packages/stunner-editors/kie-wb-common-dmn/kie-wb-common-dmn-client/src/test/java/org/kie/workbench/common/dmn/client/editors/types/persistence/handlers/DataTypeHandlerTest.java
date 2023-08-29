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

import java.util.ArrayList;
import java.util.Arrays;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeHandlerTest {

    @Mock
    private DataTypeStore dataTypeStore;

    @Mock
    private DataTypeManager dataTypeManager;

    private DataTypeHandler handler;

    @Before
    public void setup() {
        handler = spy(new DataTypeHandler(dataTypeStore, dataTypeManager));
    }

    @Test
    public void testInit() {

        final ItemDefinitionRecordEngine expectedRecordEngine = mock(ItemDefinitionRecordEngine.class);

        handler.init(expectedRecordEngine);

        final ItemDefinitionRecordEngine actualRecordEngine = handler.getRecordEngine();

        assertEquals(expectedRecordEngine, actualRecordEngine);
    }

    @Test
    public void testGetClosestTopLevelDataType() {

        /* -------------------------------------------------------------------------------------------------------------
         * Data Type structure:
         * -------------------------------------------------------------------------------------------------------------
         * (dataType4) - tCity (Structure)
         * (dataType0)    - name (Text)
         * (dataType5)    - street (Structure)
         * (dataType1)        - name (Text)
         *
         * (dataType6) - tPerson (Structure)
         * (dataType2)    - name (Text)
         * (dataType3)    - city (tCity)
         * -------------------------------------------------------------------------------------------------------------
         * */

        final DataType dataType0 = makeDataType("name", "Text", false);
        final DataType dataType1 = makeDataType("name", "Text", false);
        final DataType dataType2 = makeDataType("name", "Text", false);
        final DataType dataType3 = makeDataType("city", "tCity", false);
        final DataType dataType5 = makeDataType("street", "Structure", false, dataType1);
        final DataType dataType4 = makeDataType("tCity", "Structure", true, dataType0, dataType5);
        final DataType dataType6 = makeDataType("tPerson", "Structure", true, dataType2, dataType3);

        doReturn(dataType4).when(handler).parent(dataType0);
        doReturn(dataType4).when(handler).parent(dataType5);
        doReturn(dataType5).when(handler).parent(dataType1);
        doReturn(dataType6).when(handler).parent(dataType2);
        doReturn(dataType6).when(handler).parent(dataType3);
        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(asList(dataType4, dataType6));

        final DataType closestTopLevelDataTypeForDataType1 = handler.getClosestTopLevelDataType(dataType1).orElseThrow(UnsupportedOperationException::new);
        final DataType closestTopLevelDataTypeForDataType3 = handler.getClosestTopLevelDataType(dataType3).orElseThrow(UnsupportedOperationException::new);

        assertEquals(dataType4, closestTopLevelDataTypeForDataType1);
        assertEquals(dataType6, closestTopLevelDataTypeForDataType3);
    }

    @Test
    public void testGetClosestTopLevelDataTypeWhenDataTypeParentCannotBeFound() {

        final DataType dataType = makeDataType();

        doReturn(null).when(handler).parent(dataType);

        final Optional<DataType> closestTopLevelDataType = handler.getClosestTopLevelDataType(dataType);

        assertFalse(closestTopLevelDataType.isPresent());
    }

    @Test
    public void testGetSubDataTypesByType() {

        final String type = "tCity";
        final DataType dataType0 = makeDataType("tPerson", "Structure");
        final DataType dataType1 = makeDataType("tCity", "Structure");
        final DataType dataType2 = makeDataType("city1", "tCity");
        final DataType dataType3 = makeDataType("city2", "tCity");
        final DataType dataType4 = makeDataType("tCompany", "Structure");

        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(asList(dataType0, dataType1, dataType2, dataType3, dataType4));

        final List<DataType> expectedDataTypes = asList(dataType2, dataType3);
        final List<DataType> actualDataTypes = handler.getSubDataTypesByType(type);

        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testForEachSubDataTypesByType() {

        final String type = "tCity";
        final DataType dataType0 = makeDataType("tPerson", "Structure");
        final DataType dataType1 = makeDataType("tCity", "Structure");
        final DataType dataType2 = makeDataType("city1", "tCity");
        final DataType dataType3 = makeDataType("city2", "tCity");
        final DataType dataType4 = makeDataType("tCompany", "Structure");

        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(asList(dataType0, dataType1, dataType2, dataType3, dataType4));

        final List<DataType> actualDataTypes = new ArrayList<>();
        final List<DataType> expectedDataTypes = asList(dataType2, dataType3);

        final List<DataType> forEachSubDataTypesByType = handler.forEachSubDataTypesByType(type, actualDataTypes::add);

        assertEquals(expectedDataTypes, actualDataTypes);
        assertEquals(expectedDataTypes, forEachSubDataTypesByType);
    }

    @Test
    public void testForEachSubDataTypesByTypeOrName() {

        final String type = "tCompany";
        final DataType dataType0 = makeDataType("RedHat", "tCompany");
        final DataType dataType1 = makeDataType("tPerson", "Structure");
        final DataType dataType2 = makeDataType("tCity", "Structure");
        final DataType dataType3 = makeDataType("city1", "tCity");
        final DataType dataType4 = makeDataType("city2", "tCity");
        final DataType dataType5 = makeDataType("tCompany", "Structure");

        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(asList(dataType0, dataType1, dataType2, dataType3, dataType4, dataType5));

        final List<DataType> actualDataTypes = new ArrayList<>();
        final List<DataType> expectedDataTypes = asList(dataType0, dataType5);

        final List<DataType> forEachSubDataTypesByType = handler.forEachSubDataTypesByTypeOrName(type, actualDataTypes::add);

        assertEquals(expectedDataTypes, actualDataTypes);
        assertEquals(expectedDataTypes, forEachSubDataTypesByType);
    }

    @Test
    public void testParent() {

        final DataType dataType = makeDataType();
        final DataType expectedParent = makeDataType();
        final String parentUUID = "parentUUID";

        when(dataType.getParentUUID()).thenReturn(parentUUID);
        when(dataTypeStore.get(parentUUID)).thenReturn(expectedParent);

        final DataType actualParent = handler.parent(dataType);

        assertEquals(expectedParent, actualParent);
    }

    @Test
    public void testIsStructureWhenItReturnsTrue() {

        final DataType dataType = mock(DataType.class);
        final String structure = "Structure";

        when(dataTypeManager.structure()).thenReturn(structure);
        when(dataType.getType()).thenReturn(structure);

        assertTrue(handler.isStructure(dataType));
    }

    @Test
    public void testIsStructureWhenItReturnsFalse() {

        final DataType dataType = mock(DataType.class);

        when(dataTypeManager.structure()).thenReturn("Structure");
        when(dataType.getType()).thenReturn("tCity");

        assertFalse(handler.isStructure(dataType));
    }

    private DataType makeDataType(final String name,
                                  final String type,
                                  final boolean isTopLevel,
                                  final DataType... dataTypes) {
        final DataType dataType = makeDataType(name, type);
        when(dataType.isTopLevel()).thenReturn(isTopLevel);
        when(dataType.getSubDataTypes()).thenReturn(Arrays.asList(dataTypes));
        return dataType;
    }

    private DataType makeDataType(final String name,
                                  final String type) {
        final DataType dataType = makeDataType();
        when(dataType.getName()).thenReturn(name);
        when(dataType.getType()).thenReturn(type);
        return dataType;
    }

    private DataType makeDataType() {
        return mock(DataType.class);
    }
}
