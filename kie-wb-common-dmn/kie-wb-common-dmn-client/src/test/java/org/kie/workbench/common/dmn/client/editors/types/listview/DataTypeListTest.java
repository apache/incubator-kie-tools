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
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeStackHash;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static freemarker.template.utility.Collections12.singletonList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
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

    private DataTypeStore dataTypeStore;

    private DataTypeStackHash dataTypeStackHash;

    @Captor
    private ArgumentCaptor<List<DataTypeListItem>> listItemsCaptor;

    private DataTypeList dataTypeList;

    @Before
    public void setup() {
        dataTypeStore = new DataTypeStore();
        dataTypeStackHash = new DataTypeStackHash(dataTypeStore);
        dataTypeList = spy(new DataTypeList(view, listItems, dataTypeManager, searchBar, dataTypeStackHash));
        when(listItems.get()).thenReturn(treeGridItem);
    }

    @Test
    public void testSetup() {
        dataTypeList.setup();

        verify(view).init(dataTypeList);
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

        final InOrder inOrder = Mockito.inOrder(dataTypeList);

        inOrder.verify(dataTypeList).setListItems(listItems);
        inOrder.verify(dataTypeList).setupViewItems();
        inOrder.verify(dataTypeList).collapseItemsInTheFirstLevel();
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
    public void testSetViewItems() {

        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> listItems = asList(listItem1, listItem2);

        doReturn(listItems).when(dataTypeList).getItems();

        dataTypeList.setupViewItems();

        verify(view).setupListItems(listItems);
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
        final List<DataType> dataTypes = Collections.singletonList(dataType);

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

        verify(dataType).create();
        verify(view).addSubItem(listItem);
        verify(listItem).enableEditMode();
    }

    @Test
    public void testInsertBelow() {

        final DataType dataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final DataTypeListItem listItem = mock(DataTypeListItem.class);

        doReturn(listItem).when(dataTypeList).makeListItem(dataType);

        dataTypeList.insertBelow(dataType, reference);

        verify(view).insertBelow(listItem, reference);
    }

    @Test
    public void testInsertAbove() {

        final DataType dataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final DataTypeListItem listItem = mock(DataTypeListItem.class);

        doReturn(listItem).when(dataTypeList).makeListItem(dataType);

        dataTypeList.insertAbove(dataType, reference);

        verify(view).insertAbove(listItem, reference);
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
