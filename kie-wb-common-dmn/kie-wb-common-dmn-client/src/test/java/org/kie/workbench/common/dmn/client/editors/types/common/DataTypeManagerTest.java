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
import org.kie.workbench.common.dmn.api.definition.v1_1.ConstraintType;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.NameIsBlankErrorMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.NameIsDefaultTypeMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.NameIsNotUniqueErrorMessage;
import org.kie.workbench.common.dmn.client.editors.types.messages.DataTypeFlashMessage;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.validation.DataTypeNameValidator;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.commons.uuid.UUID;
import org.uberfire.mocks.EventSourceMock;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
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

    @Mock
    private DataTypeManagerStackStore typeStack;

    @Mock
    private EventSourceMock<DataTypeFlashMessage> flashMessageEvent;

    @Mock
    private NameIsBlankErrorMessage blankErrorMessage;

    @Mock
    private NameIsNotUniqueErrorMessage notUniqueErrorMessage;

    @Mock
    private NameIsDefaultTypeMessage nameIsDefaultTypeMessage;

    private DataTypeNameValidator dataTypeNameValidator;

    private DataTypeManager manager;

    @Before
    public void setup() {
        mockStatic(UUID.class);
        PowerMockito.when(UUID.uuid()).thenReturn("uuid");

        when(translationService.format(DataTypeManager_None)).thenReturn("--");
        when(translationService.format(DataTypeManager_Structure)).thenReturn("Structure");
        when(itemDefinitionStore.get("uuid")).thenReturn(mock(ItemDefinition.class));

        dataTypeNameValidator = spy(new DataTypeNameValidator(flashMessageEvent, blankErrorMessage, notUniqueErrorMessage, nameIsDefaultTypeMessage, dataTypeStore));
        manager = spy(new DataTypeManagerFake());
    }

    @Test
    public void testWithConstraintType() {

        final ConstraintType expectedConstraintType = ConstraintType.ENUMERATION;
        final DataType dataType = manager.from(makeDataType("uuid")).withConstraintType(expectedConstraintType.value()).get();
        final ConstraintType actualConstraintType = dataType.getConstraintType();

        assertEquals(expectedConstraintType, actualConstraintType);
    }

    @Test
    public void testWithParentUUID() {

        final String expectedParentUUID = "expectedParentUUID";
        final DataType dataType = manager.from(makeDataType("uuid")).withParentUUID(expectedParentUUID).get();
        final String actualParentUUID = dataType.getParentUUID();

        assertEquals(expectedParentUUID, actualParentUUID);
    }

    @Test
    public void testWithName() {

        final String expectedName = "expectedName";
        final DataType dataType = manager.from(makeDataType("uuid")).withName(expectedName).get();
        final String actualName = dataType.getName();

        assertEquals(expectedName, actualName);
    }

    @Test
    public void testWithType() {

        final String expectedType = "expectedType";
        final DataType dataType = manager.from(makeDataType("uuid")).withType(expectedType).get();
        final String actualType = dataType.getType();

        assertEquals(expectedType, actualType);
    }

    @Test
    public void testWithRefreshedSubDataTypes() {

        final String newType = "newType";
        final List<DataType> expectedDataTypes = asList(mock(DataType.class), mock(DataType.class));

        doReturn(expectedDataTypes).when(manager).makeExternalDataTypes(newType);

        final DataType dataType = manager.from(makeDataType("uuid")).withRefreshedSubDataTypes(newType).get();
        final List<DataType> actualDataTypes = dataType.getSubDataTypes();

        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testWithSubDataTypes() {
        final DataType topLevelDataType = mock(DataType.class);
        final DataType subLevelDataType = mock(DataType.class);
        final String subLevelDataTypeUuid = "subUuid";
        when(subLevelDataType.getUUID()).thenReturn(subLevelDataTypeUuid);

        final List<DataType> newSubDataTypes = singletonList(mock(DataType.class));

        when(topLevelDataType.getSubDataTypes()).thenReturn(singletonList(subLevelDataType));

        manager.withDataType(topLevelDataType);
        manager.withSubDataTypes(newSubDataTypes);

        verify(dataTypeStore).unIndex(subLevelDataTypeUuid);
        verify(itemDefinitionStore).unIndex(subLevelDataTypeUuid);
        verify(topLevelDataType).setSubDataTypes(newSubDataTypes);
    }

    @Test
    public void testMakeDataTypeFromItemDefinition() {

        final ItemDefinition simpleItemDefinitionFromMainItemDefinition = makeItem("name", "Text", true);
        final ItemDefinition simpleItemDefinitionFromStructureItemDefinition = makeItem("company", "Text", "\"Red\", \"Hat\"");
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
         */

        when(itemDefinitionUtils.getConstraintText(any())).thenCallRealMethod();
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
        assertEquals("", name.getConstraint());
        assertSame(tPerson.getUUID(), name.getParentUUID());
        assertEquals(0, name.getSubDataTypes().size());
        assertFalse(name.hasSubDataTypes());
        assertTrue(name.isList());

        assertEquals("uuid", address.getUUID());
        assertEquals("address", address.getName());
        assertEquals("tAddress", address.getType());
        assertEquals("", address.getConstraint());
        assertSame(tPerson.getUUID(), address.getParentUUID());
        assertEquals(1, address.getSubDataTypes().size());
        assertTrue(address.hasSubDataTypes());
        assertFalse(address.isList());

        assertEquals("uuid", street.getUUID());
        assertEquals("street", street.getName());
        assertEquals("Text", street.getType());
        assertEquals("", street.getConstraint());
        assertSame(address.getUUID(), street.getParentUUID());
        assertEquals(0, street.getSubDataTypes().size());
        assertFalse(street.hasSubDataTypes());
        assertFalse(street.isList());

        assertEquals("uuid", employee.getUUID());
        assertEquals("employee", employee.getName());
        assertEquals("Structure", employee.getType());
        assertEquals("", employee.getConstraint());
        assertSame(tPerson.getUUID(), address.getParentUUID());
        assertEquals(1, employee.getSubDataTypes().size());
        assertTrue(employee.hasSubDataTypes());
        assertFalse(employee.isList());

        assertEquals("uuid", company.getUUID());
        assertEquals("company", company.getName());
        assertEquals("Text", company.getType());
        assertEquals("\"Red\", \"Hat\"", company.getConstraint());
        assertSame(employee.getUUID(), company.getParentUUID());
        assertEquals(0, company.getSubDataTypes().size());
        assertFalse(company.hasSubDataTypes());
        assertFalse(company.isList());
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
    public void testMakeDataTypeFromNewType() {

        final DataType dataType = manager.fromNew().get();

        assertEquals("uuid", dataType.getUUID());
        assertEquals("--", dataType.getName());
        assertEquals("Any", dataType.getType());
        assertEquals("", dataType.getConstraint());
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
        doReturn(parent).when(manager).getDataType();

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
        doReturn(parent).when(manager).getDataType();

        final List<DataType> dataTypes = manager.makeExternalDataTypes(type);

        assertTrue(dataTypes.isEmpty());
    }

    @Test
    public void testCreateSubDataType() {

        final DataType expectedDataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final List<String> subDataTypeStack = singletonList("type");
        final String expectedParentUUID = "expectedParentUUID";

        doReturn(Optional.of(expectedParentUUID)).when(manager).getDataTypeUUID();
        doReturn(manager).when(manager).anotherManager();
        doReturn(manager).when(manager).newDataType();
        doReturn(manager).when(manager).withUUID();
        doReturn(manager).when(manager).withParentUUID(anyString());
        doReturn(manager).when(manager).withItemDefinition(any());
        doReturn(manager).when(manager).withItemDefinition(any());
        doReturn(manager).when(manager).withItemDefinitionName();
        doReturn(manager).when(manager).withItemDefinitionType();
        doReturn(manager).when(manager).withItemDefinitionConstraint();
        doReturn(manager).when(manager).withItemDefinitionCollection();
        doReturn(manager).when(manager).withTypeStack(any());
        doReturn(manager).when(manager).withItemDefinitionSubDataTypes();
        doReturn(manager).when(manager).withIndexedItemDefinition();
        doReturn(subDataTypeStack).when(manager).getSubDataTypeStack();
        doReturn(expectedDataType).when(manager).get();
        doReturn(expectedDataType).when(manager).get();

        final DataType actualDataType = manager.createSubDataType(itemDefinition);
        final InOrder inOrder = Mockito.inOrder(manager);

        inOrder.verify(manager).newDataType();
        inOrder.verify(manager).withUUID();
        inOrder.verify(manager).withParentUUID(expectedParentUUID);
        inOrder.verify(manager).withItemDefinition(itemDefinition);
        inOrder.verify(manager).withItemDefinitionName();
        inOrder.verify(manager).withItemDefinitionType();
        inOrder.verify(manager).withTypeStack(subDataTypeStack);
        inOrder.verify(manager).withItemDefinitionSubDataTypes();
        inOrder.verify(manager).withIndexedItemDefinition();

        assertEquals(expectedDataType, actualDataType);
    }

    @Test
    public void testGetStackTypeWhenDataTypeIsTopLevel() {

        final DataType parent = mock(DataType.class);
        final String type = "tCity";

        when(parent.isTopLevel()).thenReturn(true);
        when(parent.getName()).thenReturn(type);
        doReturn(parent).when(manager).getDataType();

        final Optional<String> stackType = manager.getStackType();

        assertEquals(type, stackType.get());
    }

    @Test
    public void testGetStackTypeWhenDataTypeIsNotTopLevelAndStructureType() {

        final DataType parent = mock(DataType.class);
        final String type = "Structure";

        when(parent.isTopLevel()).thenReturn(false);
        when(parent.getType()).thenReturn(type);
        doReturn(parent).when(manager).getDataType();

        final Optional<String> stackType = manager.getStackType();

        assertFalse(stackType.isPresent());
    }

    @Test
    public void testGetStackTypeWhenDataTypeIsNotTopLevelAndDefaultType() {

        final DataType parent = mock(DataType.class);
        final String type = BuiltInType.STRING.getName();

        when(parent.isTopLevel()).thenReturn(false);
        when(parent.getType()).thenReturn(type);
        doReturn(parent).when(manager).getDataType();

        final Optional<String> stackType = manager.getStackType();

        assertFalse(stackType.isPresent());
    }

    @Test
    public void testGetStackTypeWhenDataTypeIsNotTopLevelAndCustomType() {

        final DataType parent = mock(DataType.class);
        final String type = "tCity";

        when(parent.isTopLevel()).thenReturn(false);
        when(parent.getType()).thenReturn(type);
        doReturn(parent).when(manager).getDataType();

        final Optional<String> stackType = manager.getStackType();

        assertEquals(type, stackType.get());
    }

    @Test
    public void testIsTypeAlreadyRepresentedWhenIsReturnsTrue() {

        final DataType dataType = mock(DataType.class);
        final String uuid = "uuid";
        final String type = "tCity";

        when(typeStack.get(uuid)).thenReturn(singletonList(type));
        when(dataType.getUUID()).thenReturn(uuid);
        doReturn(dataType).when(manager).getDataType();

        assertTrue(manager.isTypeAlreadyRepresented(type));
    }

    @Test
    public void testIsTypeAlreadyRepresentedWhenIsReturnsFalse() {

        final DataType dataType = mock(DataType.class);
        final String uuid = "uuid";

        when(typeStack.get(uuid)).thenReturn(singletonList("tPerson"));
        when(dataType.getUUID()).thenReturn(uuid);
        doReturn(dataType).when(manager).getDataType();

        assertFalse(manager.isTypeAlreadyRepresented("tCity"));
    }

    @Test
    public void testGetSubDataTypeStack() {

        final DataType dataType = mock(DataType.class);
        final String uuid = "uuid";

        when(typeStack.get(uuid)).thenReturn(asList("tPerson", "tCompany"));
        when(dataType.getUUID()).thenReturn(uuid);
        when(dataType.getType()).thenReturn("tCity");
        doReturn(dataType).when(manager).getDataType();

        final List<String> actualTypeStack = manager.getSubDataTypeStack();
        final List<String> expectedTypeStack = asList("tPerson", "tCompany", "tCity");

        assertEquals(expectedTypeStack, actualTypeStack);
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

    @Test
    public void testWithUniqueNameWhenNameIsUnique() {

        final DataType dataType = makeDataType("uuid1");

        doReturn(dataType).when(manager).get();
        doReturn(asList(makeDataType("uuid2", "tPerson"), makeDataType("uuid3", "tCity"))).when(dataTypeNameValidator).siblings(dataType);

        final String actualDataTypeName = manager.withDataType(dataType).withUniqueName("tCompany").get().getName();
        final String expectedDataTypeName = "tCompany";

        assertEquals(expectedDataTypeName, actualDataTypeName);
    }

    @Test
    public void testAsStructure() {

        final DataType dataType = makeDataType("uuid");

        manager.withDataType(dataType).asStructure();

        verify(manager).withType("Structure");
    }

    @Test
    public void testWithUniqueNameWhenNameIsNotUnique() {

        final DataType dataType = makeDataType("uuid1");
        final DataType tPerson = makeDataType("uuid2", "tPerson");
        final DataType tCompany1 = makeDataType("uuid3", "tCompany");
        final DataType tCompany2 = makeDataType("uuid3", "tCompany - 2");
        final List<DataType> siblings = asList(tPerson, tCompany1, tCompany2);

        doReturn(dataType).when(manager).get();
        doReturn(siblings).when(dataTypeNameValidator).siblings(dataType);

        final String actualDataTypeName = manager.withDataType(dataType).withUniqueName("tCompany").get().getName();
        final String expectedDataTypeName = "tCompany - 3";

        assertEquals(expectedDataTypeName, actualDataTypeName);
    }

    private ItemDefinition makeItem(final String itemName,
                                    final String itemType,
                                    final boolean isCollection,
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
        when(itemDefinition.isIsCollection()).thenReturn(isCollection);

        return itemDefinition;
    }

    private ItemDefinition makeItem(final String itemName,
                                    final String itemType,
                                    final ItemDefinition... subItemDefinitions) {
        return makeItem(itemName, itemType, false, subItemDefinitions);
    }

    private ItemDefinition makeItem(final String itemName,
                                    final String itemType,
                                    final String constraint,
                                    final ItemDefinition... subItemDefinitions) {

        final ItemDefinition itemDefinition = makeItem(itemName, itemType, false, subItemDefinitions);
        final UnaryTests unaryTests = mock(UnaryTests.class);

        when(unaryTests.getText()).thenReturn(new Text(constraint));
        when(itemDefinition.getAllowedValues()).thenReturn(unaryTests);

        return itemDefinition;
    }

    private DataType makeDataType(final String uuid,
                                  final String name) {
        final DataType dataType = makeDataType(uuid);
        doReturn(name).when(dataType).getName();
        return dataType;
    }

    private DataType makeDataType(final String uuid) {
        final DataType dataType = spy(new DataType(null));
        doReturn(uuid).when(dataType).getUUID();
        return dataType;
    }

    class DataTypeManagerFake extends DataTypeManager {

        DataTypeManagerFake() {
            super(translationService, recordEngine, itemDefinitionStore, dataTypeStore, itemDefinitionUtils, dataTypeManagers, dataTypeNameValidator, typeStack);
        }

        DataTypeManager anotherManager() {
            return new DataTypeManagerFake();
        }
    }
}
