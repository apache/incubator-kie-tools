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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.client.editors.types.DataTypeChangedEvent;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.confirmation.DataTypeConfirmation;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraint;
import org.kie.workbench.common.dmn.client.editors.types.listview.validation.DataTypeNameFormatValidator;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.BOOLEAN;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.CONTEXT;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.STRING;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.ABOVE;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.BELOW;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.NESTED;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeListItemTest {

    @Mock
    private DataTypeListItem.View view;

    @Mock
    private DataTypeSelect dataTypeSelectComponent;

    @Mock
    private DataTypeConstraint dataTypeConstraintComponent;

    @Mock
    private SmallSwitchComponent dataTypeListComponent;

    @Mock
    private DataType dataType;

    @Mock
    private ItemDefinitionStore itemDefinitionStore;

    @Mock
    private DataTypeList dataTypeList;

    @Mock
    private DataTypeConfirmation confirmation;

    @Mock
    private EventSourceMock<DataTypeEditModeToggleEvent> editModeToggleEvent;

    @Mock
    private DataTypeNameFormatValidator nameFormatValidator;

    @Mock
    private EventSourceMock<DataTypeChangedEvent> dataTypeChangedEvent;

    @Captor
    private ArgumentCaptor<DataTypeEditModeToggleEvent> eventArgumentCaptor;

    private String structure = "Structure";

    private DataTypeManager dataTypeManager;

    private DataTypeListItem listItem;

    @Before
    public void setup() {

        dataTypeManager = spy(new DataTypeManager(null, null, itemDefinitionStore, null, null, null, null, null));
        listItem = spy(new DataTypeListItem(view, dataTypeSelectComponent, dataTypeConstraintComponent, dataTypeListComponent, dataTypeManager, confirmation, nameFormatValidator, editModeToggleEvent, dataTypeChangedEvent));
        listItem.init(dataTypeList);

        doReturn(structure).when(dataTypeManager).structure();
    }

    @Test
    public void testSetup() {
        listItem.setup();

        verify(view).init(listItem);
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(expectedElement);

        HTMLElement actualElement = listItem.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testSetupDataType() {

        final DataType expectedDataType = this.dataType;
        final int expectedLevel = 1;

        listItem.setupDataType(expectedDataType, expectedLevel);

        final InOrder inOrder = inOrder(listItem);
        inOrder.verify(listItem).setupSelectComponent();
        inOrder.verify(listItem).setupListComponent();
        inOrder.verify(listItem).setupConstraintComponent();
        inOrder.verify(listItem).setupView();

        assertEquals(expectedDataType, listItem.getDataType());
        assertEquals(expectedLevel, listItem.getLevel());
    }

    @Test
    public void testSetupConstraintComponent() {

        final DataType dataType = mock(DataType.class);
        doReturn(dataType).when(listItem).getDataType();

        listItem.setupConstraintComponent();

        verify(dataTypeConstraintComponent).init(listItem);
        verify(listItem).refreshConstraintComponent();
    }

    @Test
    public void testSetupListComponentWhenDataTypeIsList() {

        final DataType dataType = mock(DataType.class);
        final boolean isList = true;

        when(dataType.isList()).thenReturn(isList);
        doReturn(dataType).when(listItem).getDataType();

        listItem.setupListComponent();

        verify(dataTypeListComponent).setValue(isList);
        verify(listItem).refreshListYesLabel();
    }

    @Test
    public void testSetupListComponentWhenDataTypeIsNotList() {

        final DataType dataType = mock(DataType.class);
        final boolean isList = false;

        when(dataType.isList()).thenReturn(isList);
        doReturn(dataType).when(listItem).getDataType();

        listItem.setupListComponent();

        verify(dataTypeListComponent).setValue(isList);
        verify(listItem).refreshListYesLabel();
    }

    @Test
    public void testSetupSelectComponent() {

        final DataType dataType = mock(DataType.class);
        doReturn(dataType).when(listItem).getDataType();

        listItem.setupSelectComponent();

        verify(dataTypeSelectComponent).init(listItem, dataType);
    }

    @Test
    public void testSetupView() {

        final DataType dataType = mock(DataType.class);
        when(listItem.getDataType()).thenReturn(dataType);

        listItem.setupView();

        verify(view).setupSelectComponent(dataTypeSelectComponent);
        verify(view).setupConstraintComponent(dataTypeConstraintComponent);
        verify(view).setupListComponent(dataTypeListComponent);
        verify(view).setDataType(dataType);
    }

    @Test
    public void testExpandOrCollapseSubTypesWhenViewIsCollapsed() {

        when(view.isCollapsed()).thenReturn(true);

        listItem.expandOrCollapseSubTypes();

        verify(listItem).expand();
    }

    @Test
    public void testExpandOrCollapseSubTypesWhenViewIsNotCollapsed() {

        when(view.isCollapsed()).thenReturn(false);

        listItem.expandOrCollapseSubTypes();

        verify(listItem).collapse();
    }

    @Test
    public void testCollapse() {

        listItem.collapse();

        verify(view).collapse();
    }

    @Test
    public void testExpand() {

        listItem.expand();

        verify(view).expand();
    }

    @Test
    public void testRefreshSubItems() {

        final DataType dataType = mock(DataType.class);
        final List<DataType> dataTypes = singletonList(dataType);

        listItem.refreshSubItems(dataTypes);

        verify(dataTypeList).refreshSubItemsFromListItem(listItem, dataTypes);
        verify(view).enableFocusMode();
        verify(view).toggleArrow(anyBoolean());
    }

    @Test
    public void testEnableEditMode() {

        final DataType dataType = mock(DataType.class);
        final String expectedName = "name";
        final String expectedType = "type";
        final String expectedConstraint = "constraint";
        final boolean expectedIsList = true;
        final String expectedConstraintType = "";

        doReturn(dataType).when(listItem).getDataType();
        when(dataType.getName()).thenReturn(expectedName);
        when(dataType.getType()).thenReturn(expectedType);
        when(dataType.getConstraint()).thenReturn(expectedConstraint);
        when(dataType.isList()).thenReturn(expectedIsList);
        when(view.isOnFocusMode()).thenReturn(false);

        listItem.enableEditMode();

        assertEquals(expectedName, listItem.getOldName());
        assertEquals(expectedType, listItem.getOldType());
        assertEquals(expectedConstraint, listItem.getOldConstraint());
        assertEquals(expectedIsList, listItem.getOldIsList());
        assertEquals(expectedConstraintType, listItem.getOldConstraintType());

        verify(view).showSaveButton();
        verify(view).showDataTypeNameInput();
        verify(view).enableFocusMode();
        verify(view).hideListYesLabel();
        verify(view).showListContainer();
        verify(view).hideKebabMenu();
        verify(dataTypeSelectComponent).enableEditMode();
        verify(dataTypeConstraintComponent).enableEditMode();
        verify(editModeToggleEvent).fire(eventArgumentCaptor.capture());

        assertTrue(eventArgumentCaptor.getValue().isEditModeEnabled());
    }

    @Test
    public void testEnableEditModeWhenDataTypeListItemIsAlreadyOnEditMode() {

        when(view.isOnFocusMode()).thenReturn(true);

        listItem.enableEditMode();

        verify(view, never()).showSaveButton();
        verify(view, never()).showDataTypeNameInput();
        verify(view, never()).enableFocusMode();
        verify(view, never()).hideListYesLabel();
        verify(view, never()).showListContainer();
        verify(view, never()).hideKebabMenu();
        verify(dataTypeSelectComponent, never()).enableEditMode();
        verify(dataTypeConstraintComponent, never()).enableEditMode();
        verify(editModeToggleEvent, never()).fire(any());
    }

    @Test
    public void testDisableEditMode() {

        when(view.isOnFocusMode()).thenReturn(true);
        doNothing().when(listItem).discardNewDataType();
        doNothing().when(listItem).closeEditMode();

        listItem.disableEditMode();

        verify(listItem).discardNewDataType();
        verify(listItem).closeEditMode();
    }

    @Test
    public void testDisableEditModeWhenDataTypeListItemIsNotOnEditMode() {

        when(view.isOnFocusMode()).thenReturn(false);

        listItem.disableEditMode();

        verify(listItem, never()).discardNewDataType();
        verify(listItem, never()).closeEditMode();
    }

    @Test
    public void testSaveAndCloseEditModeWhenDataTypeIsValid() {

        final DataType dataType = spy(makeDataType());
        final DataType updatedDataType = spy(makeDataType());
        final Command doSaveAndCloseCommand = mock(Command.class);
        final Command doDisableEditMode = mock(Command.class);

        doReturn(dataType).when(listItem).getDataType();
        doReturn(updatedDataType).when(listItem).updateProperties(dataType);
        doReturn(true).when(updatedDataType).isValid();
        doReturn(doSaveAndCloseCommand).when(listItem).doValidateDataTypeNameAndSave(updatedDataType);
        doReturn(doDisableEditMode).when(listItem).doDisableEditMode();

        listItem.saveAndCloseEditMode();

        verify(confirmation).ifDataTypeDoesNotHaveLostSubDataTypes(updatedDataType, doSaveAndCloseCommand, doDisableEditMode);
    }

    @Test
    public void testDoDisableEditMode() {
        doNothing().when(listItem).disableEditMode();

        listItem.doDisableEditMode().execute();

        verify(listItem).disableEditMode();
    }

    @Test
    public void testSaveAndCloseEditModeWhenDataTypeIsNotValid() {

        final DataType dataType = spy(makeDataType());
        final DataType updatedDataType = spy(makeDataType());

        doReturn(dataType).when(listItem).getDataType();
        doReturn(updatedDataType).when(listItem).updateProperties(dataType);
        doReturn(false).when(updatedDataType).isValid();

        listItem.saveAndCloseEditMode();

        verify(confirmation, never()).ifDataTypeDoesNotHaveLostSubDataTypes(any(), any(), any());
        verify(listItem).discardDataTypeProperties();
    }

    @Test
    public void testDoValidateDataTypeNameAndSave() {

        final DataType dataType = spy(makeDataType());
        final Command saveAndCloseEditMode = mock(Command.class);

        doReturn(saveAndCloseEditMode).when(listItem).doSaveAndCloseEditMode(dataType);

        listItem.doValidateDataTypeNameAndSave(dataType).execute();

        verify(nameFormatValidator).ifIsValid(dataType, saveAndCloseEditMode);
    }

    @Test
    public void testDoSaveAndCloseEditMode() {

        final DataType dataType = spy(makeDataType());
        final List<DataType> updatedDataTypes = singletonList(makeDataType());
        final String referenceDataTypeHash = "referenceDataTypeHash";
        final String newDataTypeHash = "newDataTypeHash";

        doReturn(updatedDataTypes).when(listItem).persist(dataType);
        doReturn(dataType).when(listItem).getDataType();
        doReturn(newDataTypeHash).when(listItem).getNewDataTypeHash(dataType, referenceDataTypeHash);
        when(dataTypeList.calculateParentHash(dataType)).thenReturn(referenceDataTypeHash);

        listItem.doSaveAndCloseEditMode(dataType).execute();

        verify(dataTypeList).refreshItemsByUpdatedDataTypes(updatedDataTypes);
        verify(listItem).closeEditMode();
        verify(dataTypeList).fireOnDataTypeListItemUpdateCallback(newDataTypeHash);
        verify(listItem).insertNewFieldIfDataTypeIsStructure(newDataTypeHash);
        verify(listItem).fireDataChangedEvent();
    }

    @Test
    public void testInsertNewFieldIfDataTypeIsStructureWhenDataTypeIsStructure() {

        final DataType dataType = mock(DataType.class);
        final String hash = "hash";

        when(dataTypeSelectComponent.getValue()).thenReturn(structure);
        when(dataType.getSubDataTypes()).thenReturn(emptyList());
        doReturn(dataType).when(listItem).getDataType();
        doNothing().when(listItem).insertNestedField();

        listItem.insertNewFieldIfDataTypeIsStructure(hash);

        verify(dataTypeList).insertNestedField(hash);
    }

    @Test
    public void testInsertNewFieldIfDataTypeIsStructureWhenDataTypeIsStructureButHasFields() {

        final DataType dataType = mock(DataType.class);
        final String hash = "hash";

        when(dataTypeSelectComponent.getValue()).thenReturn(structure);
        when(dataType.getSubDataTypes()).thenReturn(asList(mock(DataType.class), mock(DataType.class)));
        doReturn(dataType).when(listItem).getDataType();

        listItem.insertNewFieldIfDataTypeIsStructure(hash);

        verify(dataTypeList, never()).insertNestedField(anyString());
    }

    @Test
    public void testInsertNewFieldIfDataTypeIsStructureWhenDataTypeIsNotStructure() {

        final DataType dataType = mock(DataType.class);
        final String hash = "hash";

        when(dataTypeSelectComponent.getValue()).thenReturn("String");
        when(dataType.getSubDataTypes()).thenReturn(emptyList());
        doReturn(dataType).when(listItem).getDataType();

        listItem.insertNewFieldIfDataTypeIsStructure(hash);

        verify(dataTypeList, never()).insertNestedField(anyString());
    }

    @Test
    public void testPersist() {

        final String uuid = "uuid";
        final DataType dataType = spy(makeDataType());
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final List<DataType> subDataTypes = singletonList(makeDataType());
        final List<DataType> affectedDataTypes = emptyList();

        when(itemDefinitionStore.get(uuid)).thenReturn(itemDefinition);
        when(dataTypeSelectComponent.getSubDataTypes()).thenReturn(subDataTypes);
        doReturn(uuid).when(dataType).getUUID();
        doReturn(affectedDataTypes).when(dataType).update();

        listItem.persist(dataType);

        final InOrder inOrder = inOrder(dataTypeManager, dataType);

        inOrder.verify(dataTypeManager).from(dataType);
        inOrder.verify(dataTypeManager).withSubDataTypes(subDataTypes);
        inOrder.verify(dataTypeManager).get();
        inOrder.verify(dataType).update();
    }

    @Test
    public void testDiscardNewDataType() {

        final DataType dataType = spy(makeDataType());
        final List<DataType> subDataTypes = Collections.emptyList();

        doNothing().when(listItem).setupSelectComponent();
        doNothing().when(listItem).setupListComponent();
        doNothing().when(listItem).refreshSubItems(subDataTypes);
        doReturn(subDataTypes).when(dataType).getSubDataTypes();
        doReturn(dataType).when(listItem).discardDataTypeProperties();

        listItem.discardNewDataType();

        verify(view).setDataType(dataType);
        verify(listItem).setupListComponent();
        verify(listItem).setupSelectComponent();
        verify(listItem).setupConstraintComponent();
        verify(listItem).refreshSubItems(subDataTypes);
    }

    @Test
    public void testDiscardDataTypeProperties() {

        final DataType dataType = spy(makeDataType());
        final String expectedName = "name";
        final String expectedType = "type";
        final String expectedConstraint = "constraint";
        final String expectedConstraintType = "enumeration";
        final boolean expectedIsList = true;

        doReturn(dataType).when(listItem).getDataType();
        doReturn(expectedName).when(listItem).getOldName();
        doReturn(expectedType).when(listItem).getOldType();
        doReturn(expectedConstraint).when(listItem).getOldConstraint();
        doReturn(expectedIsList).when(listItem).getOldIsList();
        doReturn(expectedConstraintType).when(listItem).getOldConstraintType();

        listItem.discardDataTypeProperties();

        assertEquals(expectedName, dataType.getName());
        assertEquals(expectedType, dataType.getType());
        assertEquals(expectedConstraint, dataType.getConstraint());
        assertEquals(expectedIsList, dataType.isList());
        assertEquals(expectedConstraintType, dataType.getConstraintType().value());
    }

    @Test
    public void testCloseEditMode() {

        doReturn(dataType).when(listItem).getDataType();

        listItem.closeEditMode();

        verify(view).showEditButton();
        verify(view).hideDataTypeNameInput();
        verify(view).disableFocusMode();
        verify(view).hideListContainer();
        verify(view).showKebabMenu();
        verify(listItem).refreshListYesLabel();
        verify(dataTypeSelectComponent).disableEditMode();
        verify(dataTypeConstraintComponent).disableEditMode();
        verify(editModeToggleEvent).fire(eventArgumentCaptor.capture());

        assertFalse(eventArgumentCaptor.getValue().isEditModeEnabled());
    }

    @Test
    public void testRefreshListYesLabelWhenDataTypeIsList() {

        doReturn(dataType).when(listItem).getDataType();
        when(dataType.isList()).thenReturn(true);

        listItem.refreshListYesLabel();

        verify(view).showListYesLabel();
    }

    @Test
    public void testRefreshListYesLabelWhenDataTypeIsNotList() {

        doReturn(dataType).when(listItem).getDataType();
        when(dataType.isList()).thenReturn(false);

        listItem.refreshListYesLabel();

        verify(view).hideListYesLabel();
    }

    @Test
    public void testUpdateProperties() {

        final DataType dataType = spy(makeDataType());
        final String uuid = "uuid";
        final String expectedName = "name";
        final String expectedType = "type";
        final String expectedConstraint = "constraint";
        final boolean expectedList = true;
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);

        when(dataType.getUUID()).thenReturn(uuid);
        when(itemDefinitionStore.get(uuid)).thenReturn(itemDefinition);
        when(view.getName()).thenReturn(expectedName);
        when(dataTypeSelectComponent.getValue()).thenReturn(expectedType);
        when(dataTypeConstraintComponent.getValue()).thenReturn(expectedConstraint);
        when(dataTypeListComponent.getValue()).thenReturn(expectedList);
        when(dataTypeManager.get()).thenReturn(dataType);

        final DataType updatedDataType = listItem.updateProperties(dataType);

        assertEquals(expectedName, updatedDataType.getName());
        assertEquals(expectedType, updatedDataType.getType());
        assertEquals(expectedConstraint, updatedDataType.getConstraint());
        assertEquals(expectedList, updatedDataType.isList());
    }

    @Test
    public void testRefresh() {

        final DataType dataType = spy(makeDataType());
        final String expectedConstraint = "constraint";
        final String expectedName = "name";

        doReturn(expectedConstraint).when(dataType).getConstraint();
        doReturn(expectedName).when(dataType).getName();
        doReturn(dataType).when(listItem).getDataType();

        listItem.refresh();

        verify(dataTypeSelectComponent).refresh();
        verify(dataTypeSelectComponent).init(listItem, dataType);
        verify(dataTypeConstraintComponent).refreshView();
        verify(view).setName(expectedName);
        verify(listItem).setupListComponent();
        verify(listItem).setupConstraintComponent();
    }

    @Test
    public void testRemove() {

        final DataType dataType = mock(DataType.class);
        final Command command = mock(Command.class);

        doReturn(dataType).when(listItem).getDataType();
        doReturn(command).when(listItem).doRemove();

        listItem.remove();

        verify(confirmation).ifIsNotReferencedDataType(dataType, command);
    }

    @Test
    public void testDoRemove() {

        final DataType dataType = mock(DataType.class);
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final List<DataType> destroyedDataTypes = new ArrayList<>(asList(dataType0, dataType1, dataType2, dataType3));
        final List<DataType> removedDataTypes = asList(dataType1, dataType2);

        doReturn(destroyedDataTypes).when(dataType).destroy();
        doReturn(removedDataTypes).when(listItem).removeTopLevelDataTypes(destroyedDataTypes);
        doReturn(dataType).when(listItem).getDataType();

        listItem.doRemove().execute();

        verify(dataTypeList).refreshItemsByUpdatedDataTypes(asList(dataType0, dataType3));
        verify(listItem).fireDataChangedEvent();
    }

    @Test
    public void testRemoveTopLevelDataTypesWhenItemDataTypeIsDestroyedDataType() {

        final DataType dataType = mock(DataType.class);
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);

        doReturn(dataType).when(listItem).getDataType();

        when(listItem.getDataType()).thenReturn(dataType);

        when(dataType.getUUID()).thenReturn("012");
        when(dataType.getName()).thenReturn("tCity");
        when(dataType.getType()).thenReturn("Structure");
        when(dataType.isTopLevel()).thenReturn(true);

        when(dataType0.getUUID()).thenReturn("345");
        when(dataType0.getName()).thenReturn("tCidade");
        when(dataType0.getType()).thenReturn("tCity");
        when(dataType0.isTopLevel()).thenReturn(true);

        when(dataType1.getUUID()).thenReturn("678");
        when(dataType1.getName()).thenReturn("tCompany");
        when(dataType1.getType()).thenReturn("Structure");
        when(dataType1.isTopLevel()).thenReturn(true);

        final List<DataType> actualDataTypes = listItem.removeTopLevelDataTypes(asList(dataType0, dataType1));
        final List<DataType> expectedDataTypes = singletonList(dataType0);

        verify(dataTypeList).removeItem(dataType0);
        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testRemoveTopLevelDataTypesWhenItemDataTypeIsAReferenceToDestroyedDataType() {

        final String uuid = "uuid";
        final DataType dataType = mock(DataType.class);
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);

        doReturn(dataType).when(listItem).getDataType();

        when(listItem.getDataType()).thenReturn(dataType);
        when(dataType.getUUID()).thenReturn(uuid);

        when(dataType.getUUID()).thenReturn("012");
        when(dataType.getName()).thenReturn("tCity");
        when(dataType.getType()).thenReturn("Structure");
        when(dataType.isTopLevel()).thenReturn(true);

        when(dataType0.getUUID()).thenReturn("012");
        when(dataType0.getName()).thenReturn("tCity");
        when(dataType0.getType()).thenReturn("Structure");
        when(dataType0.isTopLevel()).thenReturn(true);

        when(dataType1.getUUID()).thenReturn("678");
        when(dataType1.getName()).thenReturn("tCompany");
        when(dataType1.getType()).thenReturn("Structure");
        when(dataType1.isTopLevel()).thenReturn(true);

        final List<DataType> actualDataTypes = listItem.removeTopLevelDataTypes(asList(dataType0, dataType1));
        final List<DataType> expectedDataTypes = singletonList(dataType0);

        verify(dataTypeList).removeItem(dataType0);
        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testInsertFieldAboveWhenTheNewDataTypeIsTopLevel() {

        final DataType newDataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final List<DataType> updatedDataTypes = asList(mock(DataType.class), mock(DataType.class));
        final String referenceDataTypeHash = "tDataType.id";
        final String newDataTypeHash = "tDataType.name";

        when(newDataType.isTopLevel()).thenReturn(true);
        when(newDataType.create(reference, ABOVE)).thenReturn(updatedDataTypes);
        when(dataTypeList.calculateParentHash(reference)).thenReturn(referenceDataTypeHash);
        doReturn(newDataTypeHash).when(listItem).getNewDataTypeHash(newDataType, referenceDataTypeHash);
        doReturn(dataTypeManager).when(dataTypeManager).fromNew();
        doReturn(newDataType).when(dataTypeManager).get();
        doReturn(reference).when(listItem).getDataType();

        listItem.insertFieldAbove();

        verify(listItem).closeEditMode();
        verify(listItem).enableEditModeAndUpdateCallbacks(newDataTypeHash);
        verify(dataTypeList).insertAbove(newDataType, reference);
    }

    @Test
    public void testInsertFieldAboveWhenTheNewDataTypeIsNotTopLevel() {

        final DataType newDataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final List<DataType> updatedDataTypes = asList(mock(DataType.class), mock(DataType.class));
        final String referenceDataTypeHash = "tDataType.id";
        final String newDataTypeHash = "tDataType.name";

        when(newDataType.isTopLevel()).thenReturn(false);
        when(newDataType.create(reference, ABOVE)).thenReturn(updatedDataTypes);
        when(dataTypeList.calculateParentHash(reference)).thenReturn(referenceDataTypeHash);
        doReturn(newDataTypeHash).when(listItem).getNewDataTypeHash(newDataType, referenceDataTypeHash);
        doReturn(dataTypeManager).when(dataTypeManager).fromNew();
        doReturn(newDataType).when(dataTypeManager).get();
        doReturn(reference).when(listItem).getDataType();

        listItem.insertFieldAbove();

        verify(listItem).closeEditMode();
        verify(listItem).enableEditModeAndUpdateCallbacks(newDataTypeHash);
        verify(dataTypeList).refreshItemsByUpdatedDataTypes(updatedDataTypes);
    }

    @Test
    public void testInsertFieldBelowWhenTheNewDataTypeIsTopLevel() {

        final DataType newDataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final List<DataType> updatedDataTypes = asList(mock(DataType.class), mock(DataType.class));
        final String referenceDataTypeHash = "tDataType.id";
        final String newDataTypeHash = "tDataType.name";

        when(newDataType.isTopLevel()).thenReturn(true);
        when(newDataType.create(reference, BELOW)).thenReturn(updatedDataTypes);
        when(dataTypeList.calculateParentHash(reference)).thenReturn(referenceDataTypeHash);
        doReturn(newDataTypeHash).when(listItem).getNewDataTypeHash(newDataType, referenceDataTypeHash);
        doReturn(dataTypeManager).when(dataTypeManager).fromNew();
        doReturn(newDataType).when(dataTypeManager).get();
        doReturn(reference).when(listItem).getDataType();

        listItem.insertFieldBelow();

        verify(listItem).closeEditMode();
        verify(listItem).enableEditModeAndUpdateCallbacks(newDataTypeHash);
        verify(dataTypeList).insertBelow(newDataType, reference);
    }

    @Test
    public void testInsertFieldBelowWhenTheNewDataTypeIsNotTopLevel() {

        final DataType newDataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final List<DataType> updatedDataTypes = asList(mock(DataType.class), mock(DataType.class));
        final String referenceDataTypeHash = "tDataType.id";
        final String newDataTypeHash = "tDataType.name";

        when(newDataType.isTopLevel()).thenReturn(false);
        when(newDataType.create(reference, BELOW)).thenReturn(updatedDataTypes);
        when(dataTypeList.calculateParentHash(reference)).thenReturn(referenceDataTypeHash);
        doReturn(newDataTypeHash).when(listItem).getNewDataTypeHash(newDataType, referenceDataTypeHash);
        doReturn(dataTypeManager).when(dataTypeManager).fromNew();
        doReturn(newDataType).when(dataTypeManager).get();
        doReturn(reference).when(listItem).getDataType();

        listItem.insertFieldBelow();

        verify(listItem).closeEditMode();
        verify(listItem).enableEditModeAndUpdateCallbacks(newDataTypeHash);
        verify(dataTypeList).refreshItemsByUpdatedDataTypes(updatedDataTypes);
    }

    @Test
    public void testInsertNestedField() {

        final DataType newDataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final List<DataType> updatedDataTypes = asList(mock(DataType.class), mock(DataType.class));
        final String referenceDataTypeHash = "tDataType.id";
        final String newDataTypeHash = "tDataType.id.value";

        when(newDataType.create(reference, NESTED)).thenReturn(updatedDataTypes);
        when(dataTypeList.calculateHash(reference)).thenReturn(referenceDataTypeHash);
        doReturn(newDataTypeHash).when(listItem).getNewDataTypeHash(newDataType, referenceDataTypeHash);
        doReturn(dataTypeManager).when(dataTypeManager).fromNew();
        doReturn(newDataType).when(dataTypeManager).get();
        doReturn(reference).when(listItem).getDataType();

        listItem.insertNestedField();

        verify(dataTypeList).refreshItemsByUpdatedDataTypes(updatedDataTypes);
        verify(listItem).enableEditModeAndUpdateCallbacks(newDataTypeHash);
    }

    @Test
    public void testGetNewDataTypeHashWhenReferenceDataTypeHashIsNotEmpty() {

        final DataType newDataType = mock(DataType.class);
        final String newDataTypeName = "value";
        final String referenceDataTypeHash = "tDataType.id";

        when(newDataType.getName()).thenReturn(newDataTypeName);

        final String actualHash = listItem.getNewDataTypeHash(newDataType, referenceDataTypeHash);
        final String expectedHash = "tDataType.id.value";

        assertEquals(expectedHash, actualHash);
    }

    @Test
    public void testGetNewDataTypeHashWhenReferenceDataTypeHashIsEmpty() {

        final DataType newDataType = mock(DataType.class);
        final String newDataTypeName = "value";
        final String referenceDataTypeHash = "";

        when(newDataType.getName()).thenReturn(newDataTypeName);

        final String actualHash = listItem.getNewDataTypeHash(newDataType, referenceDataTypeHash);
        final String expectedHash = "value";

        assertEquals(expectedHash, actualHash);
    }

    @Test
    public void testRefreshConstraintComponentWhenSelectedTypeIsStructure() {

        when(dataTypeSelectComponent.getValue()).thenReturn(structure);

        listItem.refreshConstraintComponent();

        verify(dataTypeConstraintComponent).disable();
    }

    @Test
    public void testRefreshConstraintComponentWhenSelectedTypeIsBoolean() {

        when(dataTypeSelectComponent.getValue()).thenReturn(BOOLEAN.getName());

        listItem.refreshConstraintComponent();

        verify(dataTypeConstraintComponent).disable();
    }

    @Test
    public void testRefreshConstraintComponentWhenSelectedTypeIsContext() {

        when(dataTypeSelectComponent.getValue()).thenReturn(CONTEXT.getName());

        listItem.refreshConstraintComponent();

        verify(dataTypeConstraintComponent).disable();
    }

    @Test
    public void testRefreshConstraintComponentWhenSelectedTypeIsALIst() {

        when(dataTypeListComponent.getValue()).thenReturn(true);

        listItem.refreshConstraintComponent();

        verify(dataTypeConstraintComponent).disable();
    }

    @Test
    public void testRefreshConstraintComponentWhenSelectedTypeIsNotBooleanNeitherStructure() {

        when(dataTypeSelectComponent.getValue()).thenReturn(STRING.getName());

        listItem.refreshConstraintComponent();

        verify(dataTypeConstraintComponent).enable();
    }

    @Test
    public void testEnableEditModeAndUpdateCallbacks() {

        final String dataTypeHash = "dataTypeHash";

        listItem.enableEditModeAndUpdateCallbacks(dataTypeHash);

        verify(dataTypeList).enableEditMode(dataTypeHash);
        verify(dataTypeList).fireOnDataTypeListItemUpdateCallback(dataTypeHash);
    }

    @Test
    public void testIsReadOnlyWhenItReturnsTrue() {

        doReturn(dataType).when(listItem).getDataType();
        when(dataType.isReadOnly()).thenReturn(true);

        assertTrue(listItem.isReadOnly());
    }

    @Test
    public void testIsReadOnlyWhenItReturnsFalse() {

        doReturn(dataType).when(listItem).getDataType();
        when(dataType.isReadOnly()).thenReturn(false);

        assertFalse(listItem.isReadOnly());
    }

    private DataType makeDataType() {
        return new DataType(null);
    }
}
