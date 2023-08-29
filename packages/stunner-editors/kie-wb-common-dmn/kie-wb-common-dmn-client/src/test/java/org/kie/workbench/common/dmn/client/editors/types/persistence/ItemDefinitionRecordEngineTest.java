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

package org.kie.workbench.common.dmn.client.editors.types.persistence;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeCreateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeDestroyHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeUpdateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionCreateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionDestroyHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionUpdateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.validation.DataTypeNameValidator;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.ABOVE;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.NESTED;
import static org.mockito.Mockito.doReturn;
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
    private ItemDefinitionCreateHandler itemDefinitionCreateHandler;

    @Mock
    private DataTypeDestroyHandler dataTypeDestroyHandler;

    @Mock
    private DataTypeUpdateHandler dataTypeUpdateHandler;

    @Mock
    private DataTypeCreateHandler dataTypeCreateHandler;

    @Mock
    private DataTypeNameValidator dataTypeNameValidator;

    private ItemDefinitionRecordEngine recordEngine;

    @Before
    public void setup() {
        recordEngine = spy(new ItemDefinitionRecordEngine(itemDefinitionStore, itemDefinitionDestroyHandler, itemDefinitionUpdateHandler, itemDefinitionCreateHandler, dataTypeDestroyHandler, dataTypeUpdateHandler, dataTypeCreateHandler, dataTypeNameValidator));
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

        when(dataType.isValid()).thenReturn(true);
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
    public void testUpdateWhenDataTypeIsNotValid() {

        final DataType dataType = mock(DataType.class);

        when(dataType.isValid()).thenReturn(false);

        assertThatThrownBy(() -> recordEngine.update(dataType))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("An invalid Data Type cannot be updated.");
    }

    @Test
    public void testDoUpdate() {

        final DataType dataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);

        recordEngine.doUpdate(dataType, itemDefinition);

        verify(dataTypeUpdateHandler).update(dataType);
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
    public void testDestroyWithoutDependentTypes() {

        final DataType dataType = mock(DataType.class);
        final List<DataType> dependentDataTypes = asList(mock(DataType.class), mock(DataType.class));

        when(dataTypeDestroyHandler.refreshDependentDataTypes(dataType)).thenReturn(dependentDataTypes);

        final List<DataType> actualDependentDataTypes = recordEngine.destroyWithoutDependentTypes(dataType);
        final List<DataType> expectedDependentDataTypes = singletonList(dataType);

        verify(dataTypeDestroyHandler).destroy(dataType);
        verify(itemDefinitionDestroyHandler).destroy(dataType, false);
        assertEquals(expectedDependentDataTypes, actualDependentDataTypes);
    }

    @Test
    public void testCreate() {

        final DataType dataType = mock(DataType.class);
        final List<DataType> expectedAffectedDataTypes = asList(mock(DataType.class), mock(DataType.class));
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);

        when(itemDefinitionCreateHandler.appendItemDefinition()).thenReturn(itemDefinition);
        when(dataTypeCreateHandler.append(dataType, itemDefinition)).thenReturn(expectedAffectedDataTypes);

        final List<DataType> actualAffectedDataTypes = recordEngine.create(dataType);

        assertEquals(expectedAffectedDataTypes, actualAffectedDataTypes);
    }

    @Test
    public void testCreateWithCreationTypeNotNested() {

        final DataType dataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final List<DataType> expectedAffectedDataTypes = asList(mock(DataType.class), mock(DataType.class));
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final CreationType creationType = ABOVE;

        when(itemDefinitionCreateHandler.insertSibling(dataType, reference, creationType)).thenReturn(itemDefinition);
        when(dataTypeCreateHandler.insertSibling(dataType, reference, creationType, itemDefinition)).thenReturn(expectedAffectedDataTypes);

        final List<DataType> actualAffectedDataTypes = recordEngine.create(dataType, reference, creationType);

        assertEquals(expectedAffectedDataTypes, actualAffectedDataTypes);
    }

    @Test
    public void testCreateWithCreationTypeNested() {

        final DataType dataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final List<DataType> expectedAffectedDataTypes = asList(mock(DataType.class), mock(DataType.class));
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);

        when(itemDefinitionCreateHandler.insertNested(dataType, reference)).thenReturn(itemDefinition);
        when(dataTypeCreateHandler.insertNested(dataType, reference, itemDefinition)).thenReturn(expectedAffectedDataTypes);

        final List<DataType> actualAffectedDataTypes = recordEngine.create(dataType, reference, NESTED);

        assertEquals(expectedAffectedDataTypes, actualAffectedDataTypes);
    }

    @Test
    public void testIsValidWhenItIsTrue() {

        final DataType dataType = mock(DataType.class);

        doReturn(true).when(dataTypeNameValidator).isValid(dataType);

        assertTrue(recordEngine.isValid(dataType));
    }

    @Test
    public void testIsValidWhenItIsFalse() {

        final DataType dataType = mock(DataType.class);

        doReturn(false).when(dataTypeNameValidator).isValid(dataType);

        assertFalse(recordEngine.isValid(dataType));
    }

    @Test
    public void testDoDestroy() {

        final DataType dataType = mock(DataType.class);

        recordEngine.doDestroy(dataType);

        verify(dataTypeDestroyHandler).destroy(dataType);
        verify(itemDefinitionDestroyHandler).destroy(dataType, true);
    }
}
