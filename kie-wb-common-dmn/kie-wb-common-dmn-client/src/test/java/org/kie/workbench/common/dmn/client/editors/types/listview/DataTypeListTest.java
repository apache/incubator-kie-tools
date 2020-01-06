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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.dmn.api.editors.types.DataObjectProperty;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeStackHash;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar;
import org.kie.workbench.common.widgets.client.kogito.IsKogito;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.common.DataType.TOP_LEVEL_PARENT_UUID;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeListTest {

    @Mock
    private DataTypeList.View view;

    @Mock
    private ManagedInstance<DataTypeListItem> listItems;

    @Mock
    private DataTypeListItem treeGridItem;

    @Mock
    private DataTypeManager dataTypeManager;

    @Mock
    private DataTypeSearchBar searchBar;

    @Mock
    private Consumer<DataTypeListItem> listItemConsumer;

    @Mock
    private DNDListComponent dndListComponent;

    @Mock
    private DNDDataTypesHandler dndDataTypesHandler;

    @Mock
    private IsKogito isKogito;

    private DataTypeStore dataTypeStore;

    private DataTypeStackHash dataTypeStackHash;

    @Captor
    private ArgumentCaptor<List<DataTypeListItem>> listItemsCaptor;

    private DataTypeList dataTypeList;

    @Before
    public void setup() {
        dataTypeStore = new DataTypeStore();
        dataTypeStackHash = new DataTypeStackHash(dataTypeStore);
        dataTypeList = spy(new DataTypeList(view,
                                            listItems,
                                            dataTypeManager,
                                            searchBar,
                                            dndListComponent,
                                            dataTypeStackHash,
                                            dndDataTypesHandler,
                                            isKogito));
        when(listItems.get()).thenReturn(treeGridItem);
    }

    @Test
    public void testSetup() {

        final BiConsumer<Element, Element> consumer = (a, b) -> {/* Nothing. */};

        doReturn(consumer).when(dataTypeList).getOnDropDataType();

        dataTypeList.setup();

        verify(view).init(dataTypeList);
        verify(view).showImportDataObjectButton();
        verify(dndDataTypesHandler).init(dataTypeList);
        verify(dndListComponent).setOnDropItem(consumer);
    }

    @Test
    public void testSetupViewWhenIsKogito() {

        final BiConsumer<Element, Element> consumer = (a, b) -> {/* Nothing. */};

        doReturn(consumer).when(dataTypeList).getOnDropDataType();
        when(isKogito.get()).thenReturn(true);

        dataTypeList.setup();

        verify(view).init(dataTypeList);
        verify(view).hideImportDataObjectButton();
        verify(dndDataTypesHandler).init(dataTypeList);
        verify(dndListComponent).setOnDropItem(consumer);
    }

    @Test
    public void testGetOnDropDataType() {

        final Element e1 = mock(Element.class);
        final Element e2 = mock(Element.class);

        dataTypeList.getOnDropDataType().accept(e1, e2);

        verify(dndDataTypesHandler).onDropDataType(e1, e2);
    }

    @Test
    public void testGetElement() {

        final HTMLElement htmlElement = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(htmlElement);

        assertEquals(htmlElement, dataTypeList.getElement());
    }

    @Test
    public void testSetupItems() {

        final DataType dataType1 = makeDataType("item", "iITem");
        final DataType dataType2 = makeDataType("item", "iITem");
        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final List<DataType> dataTypes = asList(dataType1, dataType2);
        final List<DataTypeListItem> listItems = asList(listItem1, listItem2);

        doReturn(listItems).when(dataTypeList).makeDataTypeListItems(dataTypes);

        dataTypeList.setupItems(dataTypes);

        final InOrder inOrder = Mockito.inOrder(dndListComponent, dataTypeList, view);

        inOrder.verify(dndListComponent).clear();
        inOrder.verify(dataTypeList).makeDataTypeListItems(dataTypes);
        inOrder.verify(dndListComponent).refreshItemsPosition();
        inOrder.verify(view).showOrHideNoCustomItemsMessage();
        inOrder.verify(view).showReadOnlyMessage(false);
        inOrder.verify(dataTypeList).collapseItemsInTheFirstLevel();

        assertEquals(listItems, dataTypeList.getItems());
    }

    @Test
    public void testCollapseItemsInTheFirstLevel() {

        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem4 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> listItems = asList(listItem1, listItem2, listItem3, listItem4);

        when(listItem1.getLevel()).thenReturn(1);
        when(listItem2.getLevel()).thenReturn(2);
        when(listItem3.getLevel()).thenReturn(1);
        when(listItem4.getLevel()).thenReturn(2);
        doReturn(listItems).when(dataTypeList).getItems();

        dataTypeList.collapseItemsInTheFirstLevel();

        verify(listItem1).collapse();
        verify(listItem2, never()).collapse();
        verify(listItem3).collapse();
        verify(listItem4, never()).collapse();
    }

    @Test
    public void testExpandAllWhenSearchBarIsEnabledEnabled() {

        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> listItems = asList(listItem1, listItem2, listItem3);

        when(searchBar.isEnabled()).thenReturn(true);
        doReturn(listItems).when(dataTypeList).getItems();

        dataTypeList.expandAll();

        verify(listItem1, never()).expand();
        verify(listItem2, never()).expand();
        verify(listItem3, never()).expand();
    }

    @Test
    public void testExpandAllWhenSearchBarIsDisabledEnabled() {

        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> listItems = asList(listItem1, listItem2, listItem3);

        when(searchBar.isEnabled()).thenReturn(false);
        doReturn(listItems).when(dataTypeList).getItems();

        dataTypeList.expandAll();

        verify(listItem1).expand();
        verify(listItem2).expand();
        verify(listItem3).expand();
    }

    @Test
    public void testCollapseAllWhenSearchBarIsEnabledEnabled() {

        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> listItems = asList(listItem1, listItem2, listItem3);

        when(searchBar.isEnabled()).thenReturn(true);
        doReturn(listItems).when(dataTypeList).getItems();

        dataTypeList.collapseAll();

        verify(listItem1, never()).collapse();
        verify(listItem2, never()).collapse();
        verify(listItem3, never()).collapse();
    }

    @Test
    public void testCollapseAllWhenSearchBarIsDisabledEnabled() {

        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> listItems = asList(listItem1, listItem2, listItem3);

        when(searchBar.isEnabled()).thenReturn(false);
        doReturn(listItems).when(dataTypeList).getItems();

        dataTypeList.collapseAll();

        verify(listItem1).collapse();
        verify(listItem2).collapse();
        verify(listItem3).collapse();
    }

    @Test
    public void testMakeDataTypeListItemsWithoutSubItems() {

        final DataType dataType1 = makeDataType("item", "iITem");
        final DataType dataType2 = makeDataType("item", "iITem");
        final List<DataType> dataTypes = asList(dataType1, dataType2);

        dataTypeList.makeDataTypeListItems(dataTypes);

        verify(dataTypeList).makeTreeListItems(eq(dataType1), eq(1));
        verify(dataTypeList).makeTreeListItems(eq(dataType2), eq(1));
        verify(dataTypeList, times(2)).makeTreeListItems(any(), anyInt());
    }

    @Test
    public void testMakeDataTypeListItemsWithSubItems() {

        final DataType subDataType3 = makeDataType("subItem3", "subItemType3");
        final DataType subDataType1 = makeDataType("subItem1", "subItemType1");
        final DataType subDataType2 = makeDataType("subItem2", "subItemType2", subDataType3);
        final DataType dataType = makeDataType("item", "iITem", subDataType1, subDataType2);
        final List<DataType> dataTypes = singletonList(dataType);

        dataTypeList.makeDataTypeListItems(dataTypes);

        verify(dataTypeList).makeTreeListItems(eq(dataType), eq(1));
        verify(dataTypeList).makeTreeListItems(eq(subDataType1), eq(2));
        verify(dataTypeList).makeTreeListItems(eq(subDataType2), eq(2));
        verify(dataTypeList).makeTreeListItems(eq(subDataType3), eq(3));
        verify(dataTypeList, times(4)).makeTreeListItems(any(), anyInt());
    }

    @Test
    public void testMakeTreeListItems() {

        final DataType item1 = makeDataType("item1", "iITem1");
        final DataType item2 = makeDataType("item2", "iITem2");
        final DataType item3 = makeDataType("item", "iITem", item1, item2);

        final List<DataTypeListItem> listItems = dataTypeList.makeTreeListItems(item3, 1);

        verify(dataTypeList).makeTreeListItems(item3, 1);
        verify(dataTypeList).makeTreeListItems(item1, 2);
        verify(dataTypeList).makeTreeListItems(item2, 2);
        assertEquals(3, listItems.size());
    }

    @Test
    public void testRefreshSubItems() {

        final DataTypeListItem listItem0 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final DataTypeManager dataTypeManager1 = mock(DataTypeManager.class);
        final DataTypeManager dataTypeManager2 = mock(DataTypeManager.class);
        final DataTypeManager dataTypeManager3 = mock(DataTypeManager.class);
        final ArrayList<Object> items = new ArrayList<>();
        final int level = 1;

        when(listItem0.getLevel()).thenReturn(level);
        when(listItem0.getDataType()).thenReturn(dataType0);
        when(listItem1.getDataType()).thenReturn(dataType1);
        when(listItem2.getDataType()).thenReturn(dataType2);
        when(listItem3.getDataType()).thenReturn(dataType3);
        when(dataTypeManager.from(dataType1)).thenReturn(dataTypeManager1);
        when(dataTypeManager.from(dataType2)).thenReturn(dataTypeManager2);
        when(dataTypeManager.from(dataType3)).thenReturn(dataTypeManager3);
        doReturn(singletonList(listItem1)).when(dataTypeList).makeTreeListItems(dataType1, level + 1);
        doReturn(singletonList(listItem2)).when(dataTypeList).makeTreeListItems(dataType2, level + 1);
        doReturn(singletonList(listItem3)).when(dataTypeList).makeTreeListItems(dataType3, level + 1);
        doReturn(items).when(dataTypeList).getItems();

        dataTypeList.refreshSubItemsFromListItem(listItem0, asList(dataType1, dataType2, dataType3));

        verify(view).cleanSubTypes(eq(dataType0));
        verify(view).addSubItems(eq(dataType0), listItemsCaptor.capture());
        verify(dataTypeManager1).withIndexedItemDefinition();
        verify(dataTypeManager2).withIndexedItemDefinition();
        verify(dataTypeManager3).withIndexedItemDefinition();

        final List<DataTypeListItem> actualItems = listItemsCaptor.getValue();
        final List<DataTypeListItem> expectedItems = asList(listItem1, listItem2, listItem3);

        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testMakeListItem() {

        final DataTypeListItem expectedListItem = mock(DataTypeListItem.class);

        doCallRealMethod().when(dataTypeList).makeListItem();
        when(listItems.get()).thenReturn(expectedListItem);

        final DataTypeListItem actualListItem = dataTypeList.makeListItem();

        verify(expectedListItem).init(eq(dataTypeList));
        assertEquals(expectedListItem, actualListItem);
    }

    @Test
    public void testRemoveItemByDataType() {

        final DataType dataType = mock(DataType.class);
        final String uuid = "uuid";

        doNothing().when(dataTypeList).removeItem(anyString());
        when(dataType.getUUID()).thenReturn(uuid);

        dataTypeList.removeItem(dataType);

        verify(dataTypeList).removeItem(uuid);
        verify(view).removeItem(dataType);
    }

    @Test
    public void testRemoveItemByUUID() {

        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataTypeListItem dataTypeListItem0 = mock(DataTypeListItem.class);
        final DataTypeListItem dataTypeListItem1 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> items = new ArrayList<>(asList(dataTypeListItem0, dataTypeListItem1));

        when(dataType0.getUUID()).thenReturn("012");
        when(dataType1.getUUID()).thenReturn("345");
        when(dataTypeListItem0.getDataType()).thenReturn(dataType0);
        when(dataTypeListItem1.getDataType()).thenReturn(dataType1);
        when(dataTypeList.getItems()).thenReturn(items);

        dataTypeList.removeItem("012");

        final List expected = singletonList(dataTypeListItem1);
        final List<DataTypeListItem> actual = dataTypeList.getItems();

        assertEquals(expected, actual);
    }

    @Test
    public void testFindItemWhenItemExists() {

        final String uuid1 = "uuid1";
        final String uuid2 = "uuid2";
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataTypeListItem dataTypeListItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem dataTypeListItem2 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> existingItems = new ArrayList<>(asList(dataTypeListItem1, dataTypeListItem2));

        when(dataType1.getUUID()).thenReturn(uuid1);
        when(dataType2.getUUID()).thenReturn(uuid2);
        when(dataTypeListItem1.getDataType()).thenReturn(dataType1);
        when(dataTypeListItem2.getDataType()).thenReturn(dataType2);
        when(dataTypeList.getItems()).thenReturn(existingItems);

        final Optional<DataTypeListItem> item = dataTypeList.findItem(dataType1);

        assertEquals(dataTypeListItem1, item.get());
    }

    @Test
    public void testFindItemWhenItemDoesNotExist() {

        final String uuid1 = "uuid1";
        final String uuid2 = "uuid2";
        final String uuid3 = "uuid3";
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final DataTypeListItem dataTypeListItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem dataTypeListItem2 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> existingItems = new ArrayList<>(asList(dataTypeListItem1, dataTypeListItem2));

        when(dataType1.getUUID()).thenReturn(uuid1);
        when(dataType2.getUUID()).thenReturn(uuid2);
        when(dataType3.getUUID()).thenReturn(uuid3);
        when(dataTypeListItem1.getDataType()).thenReturn(dataType1);
        when(dataTypeListItem2.getDataType()).thenReturn(dataType2);
        when(dataTypeList.getItems()).thenReturn(existingItems);

        final Optional<DataTypeListItem> item = dataTypeList.findItem(dataType3);

        assertFalse(item.isPresent());
    }

    @Test
    public void testRefreshItemsByUpdatedDataTypes() {

        final String uuid1 = "uuid1";
        final String uuid2 = "uuid2";
        final String uuid3 = "uuid3";
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final List<DataType> subDataTypes = asList(dataType2, dataType3);
        final List<DataType> existingItems = new ArrayList<>(asList(dataType1, dataType2, dataType3));

        doReturn(Optional.of(listItem)).when(dataTypeList).findItem(dataType1);
        doReturn(Optional.empty()).when(dataTypeList).findItem(dataType2);
        doReturn(Optional.empty()).when(dataTypeList).findItem(dataType3);
        doNothing().when(dataTypeList).refreshSubItemsFromListItem(any(), anyListOf(DataType.class));
        when(dataType1.getUUID()).thenReturn(uuid1);
        when(dataType2.getUUID()).thenReturn(uuid2);
        when(dataType3.getUUID()).thenReturn(uuid3);
        when(dataType1.getSubDataTypes()).thenReturn(subDataTypes);

        dataTypeList.refreshItemsByUpdatedDataTypes(existingItems);

        verify(listItem).refresh();
        verify(dataTypeList).refreshSubItemsFromListItem(listItem, subDataTypes);
        verify(dndListComponent).consolidateYPosition();
        verify(dndListComponent).refreshItemsPosition();
        verify(searchBar).refresh();
    }

    @Test
    public void testAddDataType() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final DataType dataType = mock(DataType.class);

        when(dataTypeManager.fromNew()).thenReturn(dataTypeManager);
        when(dataTypeManager.get()).thenReturn(dataType);
        doReturn(listItem).when(dataTypeList).makeListItem(dataType);

        dataTypeList.addDataType();

        verify(searchBar).reset();
        verify(dataType).create();
        verify(view).showOrHideNoCustomItemsMessage();
        verify(listItem).refresh();
        verify(listItem).enableEditMode();
        verify(dndListComponent).refreshItemsCSSAndHTMLPosition();
        verify(listItem).enableEditMode();
    }

    @Test
    public void testAddDataTypeWithDefinedDataType() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final DataType dataType = mock(DataType.class);

        doReturn(listItem).when(dataTypeList).makeListItem(dataType);

        dataTypeList.addDataType(dataType, false);

        verify(searchBar).reset();
        verify(dataType).create();
        verify(view).showOrHideNoCustomItemsMessage();
        verify(listItem).refresh();
        verify(listItem, never()).enableEditMode();
        verify(dndListComponent).refreshItemsCSSAndHTMLPosition();
    }

    @Test
    public void testAddDataTypeWithDefinedDataTypeAndEditMode() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final DataType dataType = mock(DataType.class);

        doReturn(listItem).when(dataTypeList).makeListItem(dataType);

        dataTypeList.addDataType(dataType, true);

        verify(searchBar).reset();
        verify(dataType).create();
        verify(view).showOrHideNoCustomItemsMessage();
        verify(listItem).refresh();
        verify(listItem).enableEditMode();
        verify(dndListComponent).refreshItemsCSSAndHTMLPosition();
    }

    @Test
    public void testInsertBelow() {

        final DataType dataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final DataTypeListItem listItem = mock(DataTypeListItem.class);

        doReturn(listItem).when(dataTypeList).makeListItem(dataType);
        when(listItem.getDataType()).thenReturn(dataType);

        dataTypeList.insertBelow(dataType, reference);

        verify(view).insertBelow(listItem, reference);
        verify(dataTypeList).refreshItemsByUpdatedDataTypes(singletonList(dataType));
    }

    @Test
    public void testInsertAbove() {

        final DataType dataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final DataTypeListItem listItem = mock(DataTypeListItem.class);

        doReturn(listItem).when(dataTypeList).makeListItem(dataType);

        dataTypeList.insertAbove(dataType, reference);

        verify(view).insertAbove(listItem, reference);
        verify(dndListComponent).consolidateYPosition();
        verify(dndListComponent).refreshItemsPosition();
    }

    @Test
    public void testMakeListItemWithDataType() {

        final DataType dataType = mock(DataType.class);
        final DataTypeListItem expectedListItem = mock(DataTypeListItem.class);

        doReturn(expectedListItem).when(dataTypeList).makeListItem();
        doReturn(new ArrayList<>()).when(dataTypeList).getItems();

        final DataTypeListItem actualListItem = dataTypeList.makeListItem(dataType);
        final List<DataTypeListItem> actualItems = dataTypeList.getItems();
        final List expectedItems = singletonList(expectedListItem);

        verify(expectedListItem).setupDataType(dataType, 1);
        assertEquals(expectedListItem, actualListItem);
        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testShowNoDataTypesFound() {
        dataTypeList.showNoDataTypesFound();

        verify(view).showNoDataTypesFound();
    }

    @Test
    public void testShowListItems() {
        dataTypeList.showListItems();

        verify(view).showOrHideNoCustomItemsMessage();
    }

    @Test
    public void testEnableEditMode() {

        final String dataTypeHash = "tCity.name";
        final DataTypeListItem listItem = mock(DataTypeListItem.class);

        doReturn(Optional.of(listItem)).when(dataTypeList).findItemByDataTypeHash(dataTypeHash);

        dataTypeList.enableEditMode(dataTypeHash);

        verify(listItem).enableEditMode();
    }

    @Test
    public void testInsertNestedField() {

        final String dataTypeHash = "tCity.name";
        final DataTypeListItem listItem = mock(DataTypeListItem.class);

        doReturn(Optional.of(listItem)).when(dataTypeList).findItemByDataTypeHash(dataTypeHash);

        dataTypeList.insertNestedField(dataTypeHash);

        verify(listItem).insertNestedField();
    }

    @Test
    public void testFireListItemUpdateCallbacks() {

        final String dataTypeHash = "tCity.name";
        final DataTypeListItem listItem = mock(DataTypeListItem.class);

        doReturn(Optional.of(listItem)).when(dataTypeList).findItemByDataTypeHash(dataTypeHash);

        dataTypeList.registerDataTypeListItemUpdateCallback(listItemConsumer);
        dataTypeList.fireOnDataTypeListItemUpdateCallback(dataTypeHash);

        verify(listItemConsumer).accept(listItem);
    }

    @Test
    public void testFindItemByDataTypeHashWhenListItemIsFound() {

        final DataTypeListItem tCity = listItem(makeDataType("001", "tCity", TOP_LEVEL_PARENT_UUID));
        final DataTypeListItem tCityId = listItem(makeDataType("002", "id", "001"));
        final DataTypeListItem tCityName = listItem(makeDataType("003", "name", "001"));

        doReturn(asList(tCity, tCityId, tCityName)).when(dataTypeList).getItems();

        final Optional<DataTypeListItem> item = dataTypeList.findItemByDataTypeHash("tCity.name");

        assertTrue(item.isPresent());
        assertEquals(item.get(), tCityName);
    }

    @Test
    public void testFindItemByDataTypeHashWhenListItemIsNotFound() {

        doReturn(emptyList()).when(dataTypeList).getItems();

        final Optional<DataTypeListItem> item = dataTypeList.findItemByDataTypeHash("tCity.name");

        assertFalse(item.isPresent());
    }

    @Test
    public void testOnDataTypeEditModeToggleStartEditing() {

        final DataTypeListItem currentEditingItem = mock(DataTypeListItem.class);
        final DataTypeEditModeToggleEvent event = new DataTypeEditModeToggleEvent(true, currentEditingItem);

        dataTypeList.onDataTypeEditModeToggle(event);

        final DataTypeListItem actual = dataTypeList.getCurrentEditingItem();

        verify(searchBar).reset();
        assertEquals(currentEditingItem, actual);
    }

    @Test
    public void testOnDataTypeEditModeToggleStopEditing() {

        final DataTypeListItem currentEditingItem = mock(DataTypeListItem.class);
        final DataTypeEditModeToggleEvent event = new DataTypeEditModeToggleEvent(false, currentEditingItem);

        dataTypeList.onDataTypeEditModeToggle(event);

        final DataTypeListItem actual = dataTypeList.getCurrentEditingItem();

        verify(searchBar).reset();
        assertEquals(null, actual);
    }

    @Test
    public void testOnDataTypeEditModeToggleChangedCurrentEditingItem() {

        final DataTypeListItem currentEditingItem = mock(DataTypeListItem.class);
        final DataTypeListItem previousEditingItem = mock(DataTypeListItem.class);
        final List<DataTypeListItem> listItems = asList(currentEditingItem, previousEditingItem);

        doReturn(listItems).when(dataTypeList).getItems();

        final DataTypeEditModeToggleEvent event = new DataTypeEditModeToggleEvent(true, currentEditingItem);

        dataTypeList.setCurrentEditingItem(previousEditingItem);

        dataTypeList.onDataTypeEditModeToggle(event);

        final DataTypeListItem actual = dataTypeList.getCurrentEditingItem();

        verify(searchBar).reset();
        assertEquals(currentEditingItem, actual);
        verify(previousEditingItem).disableEditMode();
    }

    @Test
    public void testGetListElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(view.getListItems()).thenReturn(expectedElement);

        final HTMLElement actualElement = dataTypeList.getListItems();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testImportDataObjects() {

        final DataObject present = mock(DataObject.class);
        final DataObject notPresent = mock(DataObject.class);
        final List<DataObject> selectedDataObjects = asList(present, notPresent);
        final DataType presentDataType = mock(DataType.class);
        final DataType notPresentDataType = mock(DataType.class);
        final String notPresentClass = "not.present";
        final String importedPresentClass = "org.something.MyClass";
        final DataType existingDataType = mock(DataType.class);

        doReturn(presentDataType).when(dataTypeList).createNewDataType(present);
        doReturn(notPresentDataType).when(dataTypeList).createNewDataType(notPresent);
        doReturn(Optional.of(existingDataType)).when(dataTypeList).findDataTypeByName(importedPresentClass);
        doReturn(Optional.empty()).when(dataTypeList).findDataTypeByName(notPresentClass);
        doNothing().when(dataTypeList).replace(existingDataType, presentDataType);
        doNothing().when(dataTypeList).insertProperties(present);
        doNothing().when(dataTypeList).insertProperties(notPresent);
        doNothing().when(dataTypeList).insert(notPresentDataType);
        doNothing().when(dataTypeList).removeFullQualifiedNames(selectedDataObjects);

        when(notPresent.getClassType()).thenReturn(notPresentClass);
        when(present.getClassType()).thenReturn(importedPresentClass);

        dataTypeList.importDataObjects(selectedDataObjects);

        verify(dataTypeList).findDataTypeByName(importedPresentClass);
        verify(dataTypeList).replace(existingDataType, presentDataType);
        verify(dataTypeList).insertProperties(present);
        verify(dataTypeList, never()).insert(presentDataType);

        verify(dataTypeList).insert(notPresentDataType);
        verify(dataTypeList).insertProperties(notPresent);

        verify(dataTypeList).removeFullQualifiedNames(selectedDataObjects);
    }

    @Test
    public void testInsertProperties() {

        final DataObject dataObject = mock(DataObject.class);
        final String myImportedClass = "org.MyClass";
        final DataType existingDt = mock(DataType.class);
        final DataTypeListItem dtListItem = mock(DataTypeListItem.class);
        final Optional<DataTypeListItem> dtListItemOptional = Optional.of(dtListItem);
        final DataObjectProperty property1 = mock(DataObjectProperty.class);
        final DataObjectProperty property2 = mock(DataObjectProperty.class);
        final List<DataObjectProperty> properties = Arrays.asList(property1, property2);
        final DataType property1DataType = mock(DataType.class);
        final DataType property2DataType = mock(DataType.class);

        when(dataObject.getClassType()).thenReturn(myImportedClass);
        when(dataObject.getProperties()).thenReturn(properties);

        doReturn(Optional.of(existingDt)).when(dataTypeList).findDataTypeByName(myImportedClass);
        doReturn(dtListItemOptional).when(dataTypeList).findItem(existingDt);
        doReturn(property1DataType).when(dataTypeList).createNewDataType(property1);
        doReturn(property2DataType).when(dataTypeList).createNewDataType(property2);

        dataTypeList.insertProperties(dataObject);

        verify(dtListItem).insertNestedField(property1DataType);
        verify(dtListItem).insertNestedField(property2DataType);
    }

    @Test
    public void testRemoveFullQualifiedNames() {

        final String do1Class = "something.class1";
        final String do2Class = "something.class2";
        final String do3Class = "something.class3";
        final String extractedName1 = "class1";
        final String extractedName2 = "class2";
        final String extractedName3 = "class3";
        final String builtName1 = "name1";
        final String builtName2 = "name2";
        final String builtName3 = "name3";
        final DataObject do1 = createDataObject(do1Class);
        final DataObject do2 = createDataObject(do2Class);
        final DataObject do3 = createDataObject(do3Class);
        final HashMap<String, Integer> namesCount = new HashMap<>();
        final HashMap<String, String> renamed = new HashMap<>();
        namesCount.put("trash", 0);
        renamed.put("trash.from.previous", "previous");

        doReturn(namesCount).when(dataTypeList).getImportedNamesOccurrencesCount();
        doReturn(renamed).when(dataTypeList).getRenamedImportedDataTypes();

        final List<DataObject> imported = Arrays.asList(do1, do2, do3);
        doReturn(extractedName1).when(dataTypeList).extractName(do1Class);
        doReturn(extractedName2).when(dataTypeList).extractName(do2Class);
        doReturn(extractedName3).when(dataTypeList).extractName(do3Class);
        doReturn(builtName1).when(dataTypeList).buildName(extractedName1, namesCount);
        doReturn(builtName2).when(dataTypeList).buildName(extractedName2, namesCount);
        doReturn(builtName3).when(dataTypeList).buildName(extractedName3, namesCount);

        doNothing().when(dataTypeList).updatePropertiesReferences(imported, renamed);

        dataTypeList.removeFullQualifiedNames(imported);

        verify(dataTypeList).extractName(do1Class);
        verify(dataTypeList).buildName(extractedName1, namesCount);
        assertTrue(renamed.containsKey(do1Class));
        assertEquals(builtName1, renamed.get(do1Class));
        verify(do1).setClassType(builtName1);

        verify(dataTypeList).extractName(do2Class);
        verify(dataTypeList).buildName(extractedName2, namesCount);
        assertTrue(renamed.containsKey(do2Class));
        assertEquals(builtName2, renamed.get(do2Class));
        verify(do2).setClassType(builtName2);

        verify(dataTypeList).extractName(do3Class);
        verify(dataTypeList).buildName(extractedName3, namesCount);
        assertTrue(renamed.containsKey(do3Class));
        assertEquals(builtName3, renamed.get(do3Class));
        verify(do3).setClassType(builtName3);

        assertEquals(3, renamed.size());

        verify(dataTypeList).updatePropertiesReferences(imported, renamed);
    }

    private DataObject createDataObject(final String className) {
        final DataObject dataObject = mock(DataObject.class);
        when(dataObject.getClassType()).thenReturn(className);
        return dataObject;
    }

    @Test
    public void testExtractName() {

        final String name1 = "org.java.SomeClass";
        final String expected1 = "SomeClass";

        final String actual1 = dataTypeList.extractName(name1);
        assertEquals(expected1, actual1);

        final String name2 = "SomeOtherClass";
        final String expected2 = "SomeOtherClass";

        final String actual2 = dataTypeList.extractName(name2);
        assertEquals(expected2, actual2);
    }

    @Test
    public void testBuildName() {

        final String name = "MyClass";
        final String differentName = "SomeOtherClass";
        final HashMap<String, Integer> namesCount = new HashMap<>();

        final String occurrence0 = dataTypeList.buildName(name, namesCount);
        assertEquals(name, occurrence0);

        final String occurrence1 = dataTypeList.buildName(name, namesCount);
        assertEquals(name + DataTypeList.NAME_SEPARATOR + "1", occurrence1);

        final String occurrence2 = dataTypeList.buildName(name, namesCount);
        assertEquals(name + DataTypeList.NAME_SEPARATOR + "2", occurrence2);

        final String differentOccurrence0 = dataTypeList.buildName(differentName, namesCount);
        assertEquals(differentName, differentOccurrence0);

        final String differentOccurrence1 = dataTypeList.buildName(differentName, namesCount);
        assertEquals(differentName + DataTypeList.NAME_SEPARATOR + "1", differentOccurrence1);
    }

    @Test
    public void testUpdatePropertiesReferences() {

        final List<DataObject> imported = new ArrayList<>();
        final HashMap<String, String> renamed = new HashMap<>();

        final String propertyType1 = "type";
        final String propertyNewType1 = "type-1";
        final String uniqueType = "uniqueType";

        renamed.put(propertyType1, propertyNewType1);

        final DataObjectProperty prop1 = mock(DataObjectProperty.class);
        final DataObjectProperty prop2 = mock(DataObjectProperty.class);
        when(prop1.getType()).thenReturn(propertyType1);
        when(prop2.getType()).thenReturn(uniqueType);

        doReturn(true).when(dataTypeList).isPropertyTypePresent(uniqueType, imported);
        doReturn(true).when(dataTypeList).isPropertyTypePresent(propertyNewType1, imported);

        final DataObject do1 = new DataObject();
        do1.setProperties(Arrays.asList(prop1, prop2));
        imported.add(do1);

        dataTypeList.updatePropertiesReferences(imported, renamed);

        verify(prop1).setType(propertyNewType1);
        verify(prop2).setType(uniqueType);
        verify(dataTypeList).isPropertyTypePresent(propertyNewType1, imported);
        verify(dataTypeList).isPropertyTypePresent(uniqueType, imported);
    }

    @Test
    public void testIsPropertyTypePresent() {

        final String someBuiltInType = BuiltInType.STRING.getName();
        final String anImportedType = "SomeImportedType";
        final String unknownType = "UnknownType";
        final DataObject dataObject = mock(DataObject.class);
        when(dataObject.getClassType()).thenReturn(anImportedType);

        final List<DataObject> imported = Arrays.asList(dataObject);

        boolean isPresent = dataTypeList.isPropertyTypePresent(someBuiltInType, imported);
        assertTrue("Built-in type is present", isPresent);

        isPresent = dataTypeList.isPropertyTypePresent(anImportedType, imported);
        assertTrue("Imported type is present", isPresent);

        isPresent = dataTypeList.isPropertyTypePresent(unknownType, imported);
        assertFalse("Type not imported or not built-in is not present", isPresent);
    }

    @Test
    public void testInsert() {

        final DataType newDataType = mock(DataType.class);

        doNothing().when(dataTypeList).addDataType(newDataType, false);

        dataTypeList.insert(newDataType);

        verify(dataTypeList).addDataType(newDataType, false);
    }

    @Test
    public void testReplace() {

        final DataType newDataType = mock(DataType.class);
        final DataType existing = mock(DataType.class);

        doNothing().when(dataTypeList).insert(newDataType);

        dataTypeList.replace(existing, newDataType);

        verify(dndDataTypesHandler).deleteKeepingReferences(existing);

        verify(dataTypeList).insert(newDataType);
    }

    @Test
    public void testCreateNewDataTypeFromProperty() {

        final DataObjectProperty dataProperty = mock(DataObjectProperty.class);
        final String propertyName = "name";
        final String propertyType = "type";
        final DataType newType = mock(DataType.class);
        when(dataProperty.getProperty()).thenReturn(propertyName);
        when(dataProperty.getType()).thenReturn(propertyType);

        when(dataTypeManager.fromNew()).thenReturn(dataTypeManager);
        when(dataTypeManager.withType(propertyType)).thenReturn(dataTypeManager);
        when(dataTypeManager.get()).thenReturn(newType);

        final DataType actual = dataTypeList.createNewDataType(dataProperty);

        assertEquals(newType, actual);

        verify(newType).setName(propertyName);
    }

    @Test
    public void testCreateNewDataTypeFromDataObject() {

        final DataObject dataObject = mock(DataObject.class);
        final DataType dataType = mock(DataType.class);
        final String structure = "structure";
        final String classType = "classType";
        when(dataObject.getClassType()).thenReturn(classType);

        when(dataTypeManager.structure()).thenReturn(structure);
        when(dataTypeManager.fromNew()).thenReturn(dataTypeManager);
        when(dataTypeManager.withType(structure)).thenReturn(dataTypeManager);
        when(dataTypeManager.get()).thenReturn(dataType);

        final DataType actual = dataTypeList.createNewDataType(dataObject);
        assertEquals(dataType, actual);

        verify(dataType).setName(classType);
    }

    @Test
    public void testFindDataTypeByName() {

        final String name = "tName";

        final Optional<DataType> type = Optional.of(mock(DataType.class));
        when(dataTypeManager.getTopLevelDataTypeWithName(name)).thenReturn(type);

        final Optional<DataType> actual = dataTypeList.findDataTypeByName(name);

        verify(dataTypeManager).getTopLevelDataTypeWithName(name);
        assertEquals(type, actual);
    }

    @Test
    public void testDisableEditModeForChildren() {

        final DataTypeListItem dataTypeListItem = mock(DataTypeListItem.class);
        final DataType dataType = mock(DataType.class);
        final String uuid = "uuid";

        final DataType notChildDataType = mock(DataType.class);
        final DataTypeListItem notChildItem = mock(DataTypeListItem.class);
        when(notChildDataType.getParentUUID()).thenReturn("other_uuid");
        when(notChildItem.getDataType()).thenReturn(notChildDataType);

        final DataType childDataType1 = mock(DataType.class);
        final DataTypeListItem child1 = mock(DataTypeListItem.class);
        when(child1.getDataType()).thenReturn(childDataType1);
        when(childDataType1.getParentUUID()).thenReturn(uuid);

        final DataType childDataType2 = mock(DataType.class);
        final DataTypeListItem child2 = mock(DataTypeListItem.class);
        when(child2.getDataType()).thenReturn(childDataType2);
        when(childDataType2.getParentUUID()).thenReturn(uuid);

        when(dataType.getUUID()).thenReturn(uuid);
        when(dataTypeListItem.getDataType()).thenReturn(dataType);

        final List<DataTypeListItem> list = asList(child1, notChildItem, child2);

        doReturn(list).when(dataTypeList).getItems();

        dataTypeList.disableEditModeForChildren(dataTypeListItem);

        verify(child1).disableEditMode();
        verify(child2).disableEditMode();
        verify(notChildItem, never()).disableEditMode();
    }

    private DataTypeListItem listItem(final DataType dataType) {
        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        when(listItem.getDataType()).thenReturn(dataType);
        return listItem;
    }

    private DataType makeDataType(final String name,
                                  final String type,
                                  final DataType... subDataTypes) {

        final DataType dataType = makeDataType("default", name, TOP_LEVEL_PARENT_UUID);

        when(dataType.getType()).thenReturn(type);
        when(dataType.getSubDataTypes()).thenReturn(asList(subDataTypes));

        return dataType;
    }

    private DataType makeDataType(final String uuid,
                                  final String name,
                                  final String parentUUID) {

        final DataType dataType = mock(DataType.class);

        when(dataType.getUUID()).thenReturn(uuid);
        when(dataType.getName()).thenReturn(name);
        when(dataType.getParentUUID()).thenReturn(parentUUID);

        dataTypeStore.index(dataType.getUUID(), dataType);

        return dataType;
    }
}
