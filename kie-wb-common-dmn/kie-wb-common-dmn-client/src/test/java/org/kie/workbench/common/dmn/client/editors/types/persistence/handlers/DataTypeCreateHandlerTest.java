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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.kie.workbench.common.dmn.client.editors.types.common.DataType.TOP_LEVEL_PARENT_UUID;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.ABOVE;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.BELOW;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeCreateHandlerTest {

    @Mock
    private DataTypeStore dataTypeStore;

    @Mock
    private DataTypeManager dataTypeManager;

    @Mock
    private ItemDefinitionRecordEngine recordEngine;

    private DataTypeCreateHandler handler;

    @Before
    public void setup() {
        handler = spy(new DataTypeCreateHandler(dataTypeStore, dataTypeManager));
        handler.init(recordEngine);
    }

    @Test
    public void testAppend() {

        final DataType dataType = mock(DataType.class);
        final DataType updatedDataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final List<DataType> expectedAffectedDataTypes = asList(mock(DataType.class), mock(DataType.class));

        when(recordEngine.update(updatedDataType)).thenReturn(expectedAffectedDataTypes);
        doReturn(updatedDataType).when(handler).updateDataTypeProperties(dataType, TOP_LEVEL_PARENT_UUID, itemDefinition);

        final List<DataType> actualAffectedDataTypes = handler.append(dataType, itemDefinition);

        assertEquals(expectedAffectedDataTypes, actualAffectedDataTypes);
    }

    @Test
    public void testInsertNotNested() {

        final DataType dataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final List<DataType> expectedAffectedDataTypes = asList(mock(DataType.class), mock(DataType.class));
        final CreationType creationType = ABOVE;

        doReturn(expectedAffectedDataTypes).when(handler).insert(dataType, reference, creationType, itemDefinition);

        final List<DataType> actualAffectedDataTypes = handler.insert(dataType, reference, creationType, itemDefinition);

        assertEquals(expectedAffectedDataTypes, actualAffectedDataTypes);
    }

    @Test
    public void testInsertWhenAbsoluteParentExists() {

        final String uuid = "uuid";
        final String name = "name";
        final DataType dataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType absoluteParent = mock(DataType.class);
        final DataType updatedDataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final List<DataType> expectedAffectedDataTypes = asList(mock(DataType.class), mock(DataType.class));
        final List<DataType> siblings = new ArrayList<DataType>() {{
            add(dataType0);
            add(reference);
            add(dataType2);
        }};

        when(absoluteParent.getUUID()).thenReturn(uuid);
        when(reference.getName()).thenReturn(name);
        when(updatedDataType.getName()).thenReturn(name);
        when(absoluteParent.getSubDataTypes()).thenReturn(siblings);
        when(recordEngine.update(absoluteParent)).thenReturn(expectedAffectedDataTypes);
        doReturn(Optional.of(absoluteParent)).when(handler).lookupAbsoluteParent(reference);
        doReturn(updatedDataType).when(handler).updateDataTypeProperties(dataType, uuid, itemDefinition);

        final List<DataType> actualAffectedDataTypes = handler.insert(dataType, reference, BELOW, itemDefinition);

        verify(recordEngine).doUpdate(dataType, itemDefinition);

        assertEquals(asList(dataType0, reference, updatedDataType, dataType2), siblings);
        assertEquals(expectedAffectedDataTypes, actualAffectedDataTypes);
    }

    @Test
    public void testInsertWhenAbsoluteParentDoesNotExist() {

        final String parentUUID = "parentUUID";
        final DataType dataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final DataType updatedDataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);

        when(reference.getParentUUID()).thenReturn(parentUUID);
        doReturn(Optional.empty()).when(handler).lookupAbsoluteParent(reference);
        doReturn(updatedDataType).when(handler).updateDataTypeProperties(dataType, parentUUID, itemDefinition);

        final List<DataType> actualAffectedDataTypes = handler.insert(dataType, reference, BELOW, itemDefinition);
        final List<DataType> expectedAffectedDataTypes = emptyList();

        verify(recordEngine).doUpdate(updatedDataType, itemDefinition);

        assertEquals(expectedAffectedDataTypes, actualAffectedDataTypes);
    }

    @Test
    public void testInsertNestedWhenReferenceTypeIsNotDefault() {

        final String parentUUID = "parentUUID";
        final String type = "tCity";
        final DataType dataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final DataType updatedDataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final List<DataType> expectedAffectedDataTypes = asList(mock(DataType.class), mock(DataType.class));
        final List<DataType> referenceSubDataTypes = new ArrayList<>();

        when(reference.getUUID()).thenReturn(parentUUID);
        when(reference.getType()).thenReturn(type);
        when(reference.getSubDataTypes()).thenReturn(referenceSubDataTypes);
        when(dataTypeManager.withDataType(reference)).thenReturn(dataTypeManager);
        when(recordEngine.update(dataType)).thenReturn(expectedAffectedDataTypes);
        doReturn(updatedDataType).when(handler).updateDataTypeProperties(dataType, parentUUID, itemDefinition);

        final List<DataType> actualAffectedDataTypes = handler.insertNested(dataType, reference, itemDefinition);

        verify(dataTypeManager, never()).asStructure();
        assertEquals(singletonList(updatedDataType), referenceSubDataTypes);
        assertEquals(expectedAffectedDataTypes, actualAffectedDataTypes);
    }

    @Test
    public void testInsertNestedWhenReferenceTypeIsATopLevelDataType() {

        final String parentUUID = "parentUUID";
        final String type = "tCity";
        final DataType dataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final DataType topLevelReference = mock(DataType.class);
        final DataType updatedDataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final List<DataType> expectedAffectedDataTypes = asList(mock(DataType.class), mock(DataType.class));
        final List<DataType> referenceSubDataTypes = new ArrayList<>();

        when(reference.getUUID()).thenReturn(parentUUID);
        when(reference.getType()).thenReturn(type);
        when(topLevelReference.getSubDataTypes()).thenReturn(referenceSubDataTypes);
        when(topLevelReference.getName()).thenReturn(type);
        when(topLevelReference.getType()).thenReturn(BuiltInType.STRING.getName());
        when(dataTypeManager.withDataType(topLevelReference)).thenReturn(dataTypeManager);
        when(recordEngine.update(dataType)).thenReturn(expectedAffectedDataTypes);
        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(singletonList(topLevelReference));
        doReturn(updatedDataType).when(handler).updateDataTypeProperties(dataType, parentUUID, itemDefinition);

        final List<DataType> actualAffectedDataTypes = handler.insertNested(dataType, reference, itemDefinition);

        verify(dataTypeManager).asStructure();
        assertEquals(singletonList(updatedDataType), referenceSubDataTypes);
        assertEquals(expectedAffectedDataTypes, actualAffectedDataTypes);
    }

    @Test
    public void testInsertNestedWhenReferenceTypeIsDefault() {

        final String parentUUID = "parentUUID";
        final String type = BuiltInType.STRING.getName();
        final DataType dataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final DataType updatedDataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final List<DataType> expectedAffectedDataTypes = asList(mock(DataType.class), mock(DataType.class));
        final List<DataType> referenceSubDataTypes = new ArrayList<>();

        when(reference.getUUID()).thenReturn(parentUUID);
        when(reference.getType()).thenReturn(type);
        when(reference.getSubDataTypes()).thenReturn(referenceSubDataTypes);
        when(dataTypeManager.withDataType(reference)).thenReturn(dataTypeManager);
        when(recordEngine.update(dataType)).thenReturn(expectedAffectedDataTypes);
        doReturn(updatedDataType).when(handler).updateDataTypeProperties(dataType, parentUUID, itemDefinition);

        final List<DataType> actualAffectedDataTypes = handler.insertNested(dataType, reference, itemDefinition);

        verify(dataTypeManager).asStructure();
        assertEquals(singletonList(updatedDataType), referenceSubDataTypes);
        assertEquals(expectedAffectedDataTypes, actualAffectedDataTypes);
    }

    @Test
    public void testLookupAbsoluteParentWhenReferenceDoesNotHaveParent() {

        final DataType reference = mock(DataType.class);
        final String parentUUID = "parentUUID";

        when(reference.getParentUUID()).thenReturn(parentUUID);
        when(dataTypeStore.get(parentUUID)).thenReturn(null);

        final Optional<DataType> absoluteParent = handler.lookupAbsoluteParent(reference);

        assertFalse(absoluteParent.isPresent());
    }

    @Test
    public void testLookupAbsoluteParentWhenReferenceTypeIsStructure() {

        final DataType reference = mock(DataType.class);
        final DataType expectedParent = mock(DataType.class);
        final String parentUUID = "parentUUID";
        final String structure = "Structure";

        when(reference.getParentUUID()).thenReturn(parentUUID);
        when(expectedParent.getType()).thenReturn(structure);
        when(dataTypeStore.get(parentUUID)).thenReturn(expectedParent);
        when(dataTypeManager.structure()).thenReturn(structure);

        final Optional<DataType> actualParent = handler.lookupAbsoluteParent(reference);

        assertEquals(Optional.of(expectedParent), actualParent);
    }

    @Test
    public void testLookupAbsoluteParentWhenReferenceTypeIsNotStructure() {

        final DataType reference = mock(DataType.class);
        final DataType expectedParent = mock(DataType.class);
        final DataType tCityTopLevel = mock(DataType.class);
        final String parentUUID = "parentUUID";
        final String structure = "Structure";
        final String type = "tCity";

        when(reference.getParentUUID()).thenReturn(parentUUID);
        when(expectedParent.getType()).thenReturn(type);
        when(tCityTopLevel.getName()).thenReturn(type);
        when(dataTypeStore.get(parentUUID)).thenReturn(expectedParent);
        when(dataTypeManager.structure()).thenReturn(structure);
        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(singletonList(tCityTopLevel));

        final Optional<DataType> actualParent = handler.lookupAbsoluteParent(reference);

        assertEquals(Optional.of(tCityTopLevel), actualParent);
    }

    @Test
    public void testUpdateDataType() {

        final String parentUUID = "parentUUID";
        final DataType dataType = mock(DataType.class);
        final DataType expectedUpdateDataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final DataTypeManager dataTypeManagerDataType = mock(DataTypeManager.class);
        final DataTypeManager dataTypeManagerWithParentUUID = mock(DataTypeManager.class);
        final DataTypeManager dataTypeManagerWithNoName = mock(DataTypeManager.class);
        final DataTypeManager dataTypeManagerItemDefinition = mock(DataTypeManager.class);
        final DataTypeManager dataTypeManagerIndexedItemDefinition = mock(DataTypeManager.class);

        when(dataTypeManager.withDataType(dataType)).thenReturn(dataTypeManagerDataType);
        when(dataTypeManagerDataType.withParentUUID(parentUUID)).thenReturn(dataTypeManagerWithParentUUID);
        when(dataTypeManagerWithParentUUID.withNoName()).thenReturn(dataTypeManagerWithNoName);
        when(dataTypeManagerWithNoName.withItemDefinition(itemDefinition)).thenReturn(dataTypeManagerItemDefinition);
        when(dataTypeManagerItemDefinition.withIndexedItemDefinition()).thenReturn(dataTypeManagerIndexedItemDefinition);
        when(dataTypeManagerIndexedItemDefinition.get()).thenReturn(expectedUpdateDataType);

        final DataType actualUpdatedDataType = handler.updateDataTypeProperties(dataType, parentUUID, itemDefinition);

        assertEquals(expectedUpdateDataType, actualUpdatedDataType);
    }
}
