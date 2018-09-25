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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.commons.uuid.UUID;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeManager_None;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeManager_Structure;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({UUID.class})
@RunWith(PowerMockRunner.class)
public class DataTypeManagerTest {

    @Mock
    private TranslationService translationService;

    @Mock
    private ItemDefinitionRecordEngine recordEngine;

    @Mock
    private ItemDefinitionStore itemDefinitionStore;

    @Mock
    private DataTypeStore dataTypeStore;

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Mock
    private ManagedInstance<DataTypeManager> dataTypeManagers;

    private DataTypeManager manager;

    @Before
    public void setup() {
        mockStatic(UUID.class);
        PowerMockito.when(UUID.uuid()).thenReturn("uuid");

        when(translationService.format(DataTypeManager_None)).thenReturn("--");
        when(translationService.format(DataTypeManager_Structure)).thenReturn("Structure");
        when(itemDefinitionStore.get("uuid")).thenReturn(mock(ItemDefinition.class));

        manager = spy(new DataTypeManagerFake());
    }

    @Test
    public void testWithParentUUID() {

        final String expectedParentUUID = "expectedParentUUID";
        final DataType dataType = manager.from(makeDataType()).withParentUUID(expectedParentUUID).get();
        final String actualParentUUID = dataType.getParentUUID();

        assertEquals(expectedParentUUID, actualParentUUID);
    }

    @Test
    public void testWithName() {

        final String expectedName = "expectedName";
        final DataType dataType = manager.from(makeDataType()).withName(expectedName).get();
        final String actualName = dataType.getName();

        assertEquals(expectedName, actualName);
    }

    @Test
    public void testWithType() {

        final String expectedType = "expectedType";
        final DataType dataType = manager.from(makeDataType()).withType(expectedType).get();
        final String actualType = dataType.getType();

        assertEquals(expectedType, actualType);
    }

    @Test
    public void testWithRefreshedSubDataTypes() {

        final String newType = "newType";
        final List<DataType> expectedDataTypes = asList(mock(DataType.class), mock(DataType.class));

        doReturn(expectedDataTypes).when(manager).makeExternalDataTypes(newType);

        final DataType dataType = manager.from(makeDataType()).withRefreshedSubDataTypes(newType).get();
        final List<DataType> actualDataTypes = dataType.getSubDataTypes();

        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testWithSubDataTypes() {
    }

    @Test
    public void testMakeDataTypeFromItemDefinition() {

        final ItemDefinition simpleItemDefinitionFromMainItemDefinition = makeItem("name", "Text");
        final ItemDefinition simpleItemDefinitionFromStructureItemDefinition = makeItem("company", "Text");
        final ItemDefinition structureItemDefinition = makeItem("employee", null, simpleItemDefinitionFromStructureItemDefinition);
        final ItemDefinition existingItemDefinition = makeItem("address", "tAddress");
        final ItemDefinition simpleItemDefinitionFromExistingItemDefinition = makeItem("street", "Text");
        final ItemDefinition existingItemDefinitionWithFields = makeItem("address", "tAddress", simpleItemDefinitionFromExistingItemDefinition);
        final ItemDefinition mainItemDefinition = makeItem("tPerson", null, simpleItemDefinitionFromMainItemDefinition, existingItemDefinition, structureItemDefinition);

        /* -------------------------------------------------------------------------------------------------------------
         * The Item Definition above has the following structure:
         *
         * - tPerson (null)                  # ItemDefinition with 'null' indicates that it has one or more sub DataType(s).
         *   - name (Text)                   #
         *   - address (tAddress)            # Since 'tAddress' is an existing custom DataType, we need to fetch its fields
         *     * street (Text)               # 'street' is a fetched field from 'tAddress'
         *   - employee (null)               # (again) ItemDefinition with 'null' indicates that it has one or more sub DataType(s).
         *     - company (Text)              #
         * -------------------------------------------------------------------------------------------------------------
         * */

        when(itemDefinitionUtils.findByName(any())).thenReturn(Optional.empty());
        when(itemDefinitionUtils.findByName(eq("tAddress"))).thenReturn(Optional.of(existingItemDefinitionWithFields));

        final DataType tPerson = manager.from(mainItemDefinition).get();

        assertEquals("uuid", tPerson.getUUID());
        assertEquals("tPerson", tPerson.getName());
        assertEquals("Structure", tPerson.getType());
        assertEquals(3, tPerson.getSubDataTypes().size());
        assertTrue(tPerson.hasSubDataTypes());

        final DataType name = tPerson.getSubDataTypes().get(0);
        final DataType address = tPerson.getSubDataTypes().get(1);
        final DataType street = address.getSubDataTypes().get(0);
        final DataType employee = tPerson.getSubDataTypes().get(2);
        final DataType company = employee.getSubDataTypes().get(0);

        verify(itemDefinitionStore).index(tPerson.getUUID(), mainItemDefinition);
        verify(itemDefinitionStore).index(address.getUUID(), existingItemDefinition);
        verify(itemDefinitionStore).index(street.getUUID(), simpleItemDefinitionFromExistingItemDefinition);
        verify(itemDefinitionStore).index(employee.getUUID(), structureItemDefinition);
        verify(itemDefinitionStore).index(company.getUUID(), simpleItemDefinitionFromStructureItemDefinition);

        verify(dataTypeStore).index(tPerson.getUUID(), tPerson);
        verify(dataTypeStore).index(address.getUUID(), address);
        verify(dataTypeStore).index(street.getUUID(), street);
        verify(dataTypeStore).index(employee.getUUID(), employee);
        verify(dataTypeStore).index(company.getUUID(), company);

        assertEquals("uuid", name.getUUID());
        assertEquals("name", name.getName());
        assertEquals("Text", name.getType());
        assertSame(tPerson.getUUID(), name.getParentUUID());
        assertEquals(0, name.getSubDataTypes().size());
        assertFalse(name.hasSubDataTypes());

        assertEquals("uuid", address.getUUID());
        assertEquals("address", address.getName());
        assertEquals("tAddress", address.getType());
        assertSame(tPerson.getUUID(), address.getParentUUID());
        assertEquals(1, address.getSubDataTypes().size());
        assertTrue(address.hasSubDataTypes());

        assertEquals("uuid", street.getUUID());
        assertEquals("street", street.getName());
        assertEquals("Text", street.getType());
        assertSame(address.getUUID(), street.getParentUUID());
        assertEquals(0, street.getSubDataTypes().size());
        assertFalse(street.hasSubDataTypes());

        assertEquals("uuid", employee.getUUID());
        assertEquals("employee", employee.getName());
        assertEquals("Structure", employee.getType());
        assertSame(tPerson.getUUID(), address.getParentUUID());
        assertEquals(1, employee.getSubDataTypes().size());
        assertTrue(employee.hasSubDataTypes());

        assertEquals("uuid", company.getUUID());
        assertEquals("company", company.getName());
        assertEquals("Text", company.getType());
        assertSame(employee.getUUID(), company.getParentUUID());
        assertEquals(0, company.getSubDataTypes().size());
        assertFalse(company.hasSubDataTypes());
    }

    @Test
    public void testMakeDataTypeFromBuiltInType() {

        final BuiltInType builtInType = BuiltInType.values()[0];
        final DataType dataType = manager.from(builtInType).get();

        assertEquals("uuid", dataType.getUUID());
        assertEquals("--", dataType.getName());
        assertEquals("number", dataType.getType());
        assertEquals(emptyList(), dataType.getSubDataTypes());
        assertFalse(dataType.hasSubDataTypes());
    }

    @Test
    public void testFromDataType() {

        final String uuid = "uuid";
        final String name = "name";
        final String type = "type";
        final List<DataType> subDataTypes = emptyList();
        final DataType dataType0 = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);

        when(dataType0.getUUID()).thenReturn(uuid);
        when(dataType0.getName()).thenReturn(name);
        when(dataType0.getType()).thenReturn(type);
        when(dataType0.getSubDataTypes()).thenReturn(subDataTypes);

        final DataType dataType = manager.from(dataType0).get();

        assertEquals(uuid, dataType.getUUID());
        assertEquals(name, dataType.getName());
        assertEquals(type, dataType.getType());
        assertEquals(emptyList(), dataType.getSubDataTypes());
        assertFalse(dataType.hasSubDataTypes());
    }

    @Test
    public void testMakeExternalDataTypesWhenTypeExists() {

        final DataType parent = mock(DataType.class);
        final ItemDefinition itemDefinition0 = makeItem("itemDefinition0", "itemDefinition0");
        final ItemDefinition itemDefinition1 = makeItem("itemDefinition1", "itemDefinition1");
        final ItemDefinition itemDefinition2 = makeItem("itemDefinition2", "itemDefinition2");
        final ItemDefinition itemDefinition3 = makeItem("itemDefinition3", "itemDefinition3");
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final String type = "type";
        final String parentUUID = "parentUUID";
        final List<ItemDefinition> itemDefinitions = asList(itemDefinition1, itemDefinition2, itemDefinition3);

        when(parent.getUUID()).thenReturn(parentUUID);
        when(itemDefinition0.getItemComponent()).thenReturn(itemDefinitions);
        when(itemDefinitionUtils.findByName(type)).thenReturn(Optional.of(itemDefinition0));

        doReturn(itemDefinition0).when(manager).getItemDefinitionWithItemComponent(itemDefinition0);
        doReturn(dataType1).when(manager).createSubDataType(itemDefinition1);
        doReturn(dataType2).when(manager).createSubDataType(itemDefinition2);
        doReturn(dataType3).when(manager).createSubDataType(itemDefinition3);

        final List<DataType> dataTypes = manager.makeExternalDataTypes(type);

        assertThat(dataTypes).containsExactly(dataType1, dataType2, dataType3);
    }

    @Test
    public void testMakeExternalDataTypesWhenTypeDoesNotExist() {

        final DataType parent = mock(DataType.class);
        final String type = "type";
        final String parentUUID = "parentUUID";

        when(parent.getUUID()).thenReturn(parentUUID);
        when(itemDefinitionUtils.findByName(type)).thenReturn(Optional.empty());

        final List<DataType> dataTypes = manager.makeExternalDataTypes(type);

        assertTrue(dataTypes.isEmpty());
    }

    @Test
    public void testCreateSubDataType() {

        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final String expectedParentUUID = "expectedParentUUID";

        when(itemDefinitionUtils.findByName(anyString())).thenReturn(Optional.empty());
        doReturn(Optional.of(expectedParentUUID)).when(manager).getDataTypeUUID();

        final DataType actualDataType = manager.createSubDataType(itemDefinition);
        final String actualParentUUID = actualDataType.getParentUUID();

        assertEquals(expectedParentUUID, actualParentUUID);
    }

    @Test
    public void testGetItemDefinitionWithItemComponent() {

        final ItemDefinition expectedItemDefinition = mock(ItemDefinition.class);
        final ItemDefinition tPerson = mock(ItemDefinition.class);
        final QName tPersonQName = mock(QName.class);
        final String tPersonValue = "tPersonRaw";

        when(tPerson.getTypeRef()).thenReturn(tPersonQName);
        when(tPersonQName.getLocalPart()).thenReturn(tPersonValue);
        when(itemDefinitionUtils.findByName(tPersonValue)).thenReturn(Optional.of(expectedItemDefinition));

        final ItemDefinition actualItemDefinition = manager.getItemDefinitionWithItemComponent(tPerson);

        assertEquals(expectedItemDefinition, actualItemDefinition);
    }

    private ItemDefinition makeItem(final String itemName,
                                    final String itemType,
                                    final ItemDefinition... subItemDefinitions) {

        final List<ItemDefinition> itemDefinitions = new ArrayList<>(Arrays.asList(subItemDefinitions));
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final Name name = mock(Name.class);
        final QName typeRefMock = mock(QName.class);
        final QName typeRef = subItemDefinitions.length == 0 ? typeRefMock : null;

        when(name.getValue()).thenReturn(itemName);
        when(typeRefMock.getLocalPart()).thenReturn(itemType);
        when(itemDefinition.getName()).thenReturn(name);
        when(itemDefinition.getItemComponent()).thenReturn(itemDefinitions);
        when(itemDefinition.getTypeRef()).thenReturn(typeRef);

        return itemDefinition;
    }

    private DataType makeDataType() {
        final DataType dataType = spy(new DataType(null));
        when(dataType.getUUID()).thenReturn("uuid");
        return dataType;
    }

    class DataTypeManagerFake extends DataTypeManager {

        DataTypeManagerFake() {
            super(translationService, recordEngine, itemDefinitionStore, dataTypeStore, itemDefinitionUtils, dataTypeManagers);
        }

        DataTypeManager anotherManager() {
            return new DataTypeManagerFake();
        }
    }
}
