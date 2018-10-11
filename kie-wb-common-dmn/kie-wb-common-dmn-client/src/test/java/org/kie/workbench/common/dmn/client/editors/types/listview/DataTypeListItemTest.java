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
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.ABOVE;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.BELOW;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.NESTED;
import static org.mockito.Matchers.anyBoolean;
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
    private DataType dataType;

    @Mock
    private ItemDefinitionStore itemDefinitionStore;

    @Captor
    private ArgumentCaptor<DataType> dataTypeCaptor;

    @Mock
    private DataTypeList dataTypeList;

    private DataTypeManager dataTypeManager;

    private DataTypeListItem listItem;

    @Before
    public void setup() {
        dataTypeManager = spy(new DataTypeManager(null, null, itemDefinitionStore, null, null, null, null, null));
        listItem = spy(new DataTypeListItem(view, dataTypeSelectComponent, dataTypeManager));
        listItem.init(dataTypeList);
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
        inOrder.verify(listItem).setupView();

        assertEquals(expectedDataType, listItem.getDataType());
        assertEquals(expectedLevel, listItem.getLevel());
    }

    @Test
    public void testSetupSelectComponent() {

        final DataType dataType = mock(DataType.class);
        when(listItem.getDataType()).thenReturn(dataType);

        listItem.setupSelectComponent();

        verify(dataTypeSelectComponent).init(listItem, dataType);
    }

    @Test
    public void testSetupView() {

        final DataType dataType = mock(DataType.class);
        when(listItem.getDataType()).thenReturn(dataType);

        listItem.setupView();

        verify(view).setupSelectComponent(dataTypeSelectComponent);
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

        doReturn(dataType).when(listItem).getDataType();
        when(dataType.getName()).thenReturn(expectedName);
        when(dataType.getType()).thenReturn(expectedType);

        listItem.enableEditMode();

        assertEquals(expectedName, listItem.getOldName());
        assertEquals(expectedType, listItem.getOldType());

        verify(view).showSaveButton();
        verify(view).showDataTypeNameInput();
        verify(view).enableFocusMode();
        verify(dataTypeSelectComponent).enableEditMode();
    }

    @Test
    public void testDisableEditMode() {

        doNothing().when(listItem).discardNewDataType();
        doNothing().when(listItem).closeEditMode();

        listItem.disableEditMode();

        verify(listItem).discardNewDataType();
        verify(listItem).closeEditMode();
    }

    @Test
    public void testSaveAndCloseEditModeWhenDataTypeIsValid() {

        final DataType dataType = spy(makeDataType());
        final DataType updatedDataType = spy(makeDataType());
        final List<DataType> updatedDataTypes = singletonList(makeDataType());

        doReturn(dataType).when(listItem).getDataType();
        doReturn(updatedDataType).when(listItem).update(dataType);
        doReturn(true).when(updatedDataType).isValid();
        doReturn(updatedDataTypes).when(listItem).persist(updatedDataType);

        listItem.saveAndCloseEditMode();

        verify(dataTypeList).refreshItemsByUpdatedDataTypes(updatedDataTypes);
        verify(listItem).closeEditMode();
    }

    @Test
    public void testSaveAndCloseEditModeWhenDataTypeIsNotValid() {

        final DataType dataType = spy(makeDataType());
        final DataType updatedDataType = spy(makeDataType());
        final List<DataType> updatedDataTypes = singletonList(makeDataType());

        doReturn(dataType).when(listItem).getDataType();
        doReturn(updatedDataType).when(listItem).update(dataType);
        doReturn(false).when(updatedDataType).isValid();

        listItem.saveAndCloseEditMode();

        verify(dataTypeList, never()).refreshItemsByUpdatedDataTypes(updatedDataTypes);
        verify(listItem, never()).closeEditMode();
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
        final String expectedName = "name";
        final String expectedType = "type";

        doReturn(subDataTypes).when(dataType).getSubDataTypes();
        doReturn(dataType).when(listItem).getDataType();
        doReturn(expectedName).when(listItem).getOldName();
        doReturn(expectedType).when(listItem).getOldType();

        listItem.discardNewDataType();

        verify(view).setDataType(dataTypeCaptor.capture());
        verify(listItem).setupSelectComponent();
        verify(listItem).refreshSubItems(subDataTypes);

        final DataType dataTypeCaptorValue = dataTypeCaptor.getValue();

        assertEquals(expectedName, dataTypeCaptorValue.getName());
        assertEquals(expectedType, dataTypeCaptorValue.getType());
    }

    @Test
    public void testCloseEditMode() {

        listItem.closeEditMode();

        verify(view).showEditButton();
        verify(view).hideDataTypeNameInput();
        verify(view).disableFocusMode();
        verify(dataTypeSelectComponent).disableEditMode();
    }

    @Test
    public void testUpdate() {

        final DataType dataType = spy(makeDataType());
        final String uuid = "uuid";
        final String expectedName = "name";
        final String expectedType = "type";
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);

        when(dataType.getUUID()).thenReturn(uuid);
        when(itemDefinitionStore.get(uuid)).thenReturn(itemDefinition);
        when(view.getName()).thenReturn(expectedName);
        when(dataTypeSelectComponent.getValue()).thenReturn(expectedType);
        when(dataTypeManager.get()).thenReturn(dataType);

        final DataType updatedDataType = listItem.update(dataType);

        assertEquals(expectedName, updatedDataType.getName());
        assertEquals(expectedType, updatedDataType.getType());
    }

    @Test
    public void testRefresh() {

        final DataType dataType = spy(makeDataType());
        final String expectedName = "expectedName";

        doReturn(expectedName).when(dataType).getName();
        doReturn(dataType).when(listItem).getDataType();

        listItem.refresh();

        verify(dataTypeSelectComponent).refresh();
        verify(dataTypeSelectComponent).init(listItem, dataType);
        verify(view).setName(expectedName);
    }

    @Test
    public void testRemoveWhenDataTypeIsTopLevelNode() {

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

        listItem.remove();

        verify(dataTypeList).refreshItemsByUpdatedDataTypes(asList(dataType0, dataType3));
    }

    @Test
    public void testRemoveTopLevelDataTypes() {

        final DataType dataType = mock(DataType.class);
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);

        when(listItem.getDataType()).thenReturn(dataType);
        when(dataType.getName()).thenReturn("tPerson");
        when(dataType0.getName()).thenReturn("tPerson");
        when(dataType0.isTopLevel()).thenReturn(true);
        when(dataType1.getType()).thenReturn("tPerson");
        when(dataType1.isTopLevel()).thenReturn(false);
        when(dataType2.getType()).thenReturn("tCity");
        when(dataType2.isTopLevel()).thenReturn(true);
        when(dataType3.getName()).thenReturn("tCity");
        when(dataType3.isTopLevel()).thenReturn(false);

        final List<DataType> actualDataTypes = listItem.removeTopLevelDataTypes(asList(dataType0, dataType1, dataType2, dataType3));
        final List<DataType> expectedDataTypes = singletonList(dataType0);

        verify(dataTypeList).removeItem(dataType0);
        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testInsertFieldAboveWhenTheNewDataTypeIsTopLevel() {

        final DataType newDataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final List<DataType> updatedDataTypes = asList(mock(DataType.class), mock(DataType.class));

        when(newDataType.isTopLevel()).thenReturn(true);
        when(newDataType.create(reference, ABOVE)).thenReturn(updatedDataTypes);
        doReturn(dataTypeManager).when(dataTypeManager).fromNew();
        doReturn(newDataType).when(dataTypeManager).get();
        doReturn(reference).when(listItem).getDataType();

        listItem.insertFieldAbove();

        verify(listItem).closeEditMode();
        verify(dataTypeList).insertAbove(newDataType, reference);
    }

    @Test
    public void testInsertFieldAboveWhenTheNewDataTypeIsNotTopLevel() {

        final DataType newDataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final List<DataType> updatedDataTypes = asList(mock(DataType.class), mock(DataType.class));

        when(newDataType.isTopLevel()).thenReturn(false);
        when(newDataType.create(reference, ABOVE)).thenReturn(updatedDataTypes);
        doReturn(dataTypeManager).when(dataTypeManager).fromNew();
        doReturn(newDataType).when(dataTypeManager).get();
        doReturn(reference).when(listItem).getDataType();

        listItem.insertFieldAbove();

        verify(listItem).closeEditMode();
        verify(dataTypeList).refreshItemsByUpdatedDataTypes(updatedDataTypes);
    }

    @Test
    public void testInsertFieldBelowWhenTheNewDataTypeIsTopLevel() {

        final DataType newDataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final List<DataType> updatedDataTypes = asList(mock(DataType.class), mock(DataType.class));

        when(newDataType.isTopLevel()).thenReturn(true);
        when(newDataType.create(reference, BELOW)).thenReturn(updatedDataTypes);
        doReturn(dataTypeManager).when(dataTypeManager).fromNew();
        doReturn(newDataType).when(dataTypeManager).get();
        doReturn(reference).when(listItem).getDataType();

        listItem.insertFieldBelow();

        verify(listItem).closeEditMode();
        verify(dataTypeList).insertBelow(newDataType, reference);
    }

    @Test
    public void testInsertFieldBelowWhenTheNewDataTypeIsNotTopLevel() {

        final DataType newDataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final List<DataType> updatedDataTypes = asList(mock(DataType.class), mock(DataType.class));

        when(newDataType.isTopLevel()).thenReturn(false);
        when(newDataType.create(reference, BELOW)).thenReturn(updatedDataTypes);
        doReturn(dataTypeManager).when(dataTypeManager).fromNew();
        doReturn(newDataType).when(dataTypeManager).get();
        doReturn(reference).when(listItem).getDataType();

        listItem.insertFieldBelow();

        verify(listItem).closeEditMode();
        verify(dataTypeList).refreshItemsByUpdatedDataTypes(updatedDataTypes);
    }

    @Test
    public void testInsertNestedField() {

        final DataType newDataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final List<DataType> updatedDataTypes = asList(mock(DataType.class), mock(DataType.class));

        when(newDataType.create(reference, NESTED)).thenReturn(updatedDataTypes);
        doReturn(dataTypeManager).when(dataTypeManager).fromNew();
        doReturn(newDataType).when(dataTypeManager).get();
        doReturn(reference).when(listItem).getDataType();

        listItem.insertNestedField();

        verify(dataTypeList).refreshItemsByUpdatedDataTypes(updatedDataTypes);
    }

    private DataType makeDataType() {
        return new DataType(null);
    }
}
