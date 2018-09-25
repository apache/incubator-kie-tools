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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
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
    private ManagedInstance<DataTypeListItem> treeGridItems;

    @Mock
    private DataTypeListItem treeGridItem;

    @Captor
    private ArgumentCaptor<List<DataTypeListItem>> listItemsCaptor;

    private DataTypeList dataTypeList;

    @Before
    public void setup() {
        dataTypeList = spy(new DataTypeList(view, treeGridItems));
        when(treeGridItems.get()).thenReturn(treeGridItem);
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
    public void testCollapseFirstLevel() {

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

        verify(dataTypeList).makeTreeGridItems(eq(dataType1), eq(1));
        verify(dataTypeList).makeTreeGridItems(eq(dataType2), eq(1));
        verify(dataTypeList, times(2)).makeTreeGridItems(any(), anyInt());
    }

    @Test
    public void testMakeDataTypeListItemsWithSubItems() {

        final DataType subDataType3 = makeDataType("subItem3", "subItemType3");
        final DataType subDataType1 = makeDataType("subItem1", "subItemType1");
        final DataType subDataType2 = makeDataType("subItem2", "subItemType2", subDataType3);
        final DataType dataType = makeDataType("item", "iITem", subDataType1, subDataType2);
        final List<DataType> dataTypes = Collections.singletonList(dataType);

        dataTypeList.makeDataTypeListItems(dataTypes);

        verify(dataTypeList).makeTreeGridItems(eq(dataType), eq(1));
        verify(dataTypeList).makeTreeGridItems(eq(subDataType1), eq(2));
        verify(dataTypeList).makeTreeGridItems(eq(subDataType2), eq(2));
        verify(dataTypeList).makeTreeGridItems(eq(subDataType3), eq(3));
        verify(dataTypeList, times(4)).makeTreeGridItems(any(), anyInt());
    }

    @Test
    public void testMakeTreeGridItems() {

        final DataType item1 = makeDataType("item1", "iITem1");
        final DataType item2 = makeDataType("item2", "iITem2");
        final DataType item3 = makeDataType("item", "iITem", item1, item2);

        final List<DataTypeListItem> listItems = dataTypeList.makeTreeGridItems(item3, 1);

        verify(dataTypeList).makeTreeGridItems(item3, 1);
        verify(dataTypeList).makeTreeGridItems(item1, 2);
        verify(dataTypeList).makeTreeGridItems(item2, 2);
        assertEquals(3, listItems.size());
    }

    @Test
    public void testRefreshSubItems() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final DataType listItemDataType = makeDataType("item0", "iITem0");
        final int listItemLevel = 3;
        final DataType subDataType1 = makeDataType("item1", "iITem1");
        final DataType subDataType2 = makeDataType("item2", "iITem2");
        final DataType dataTypeX = makeDataType("itemX", "iITemX");
        final List<DataType> subDataTypes = asList(subDataType1, subDataType2);
        final List<DataType> dataTypesX = asList(dataTypeX, dataTypeX, dataTypeX, dataTypeX);

        when(listItem.getDataType()).thenReturn(listItemDataType);
        when(listItem.getLevel()).thenReturn(listItemLevel);
        doReturn(dataTypesX).when(dataTypeList).makeTreeGridItems(subDataType1, listItemLevel + 1);
        doReturn(dataTypesX).when(dataTypeList).makeTreeGridItems(subDataType2, listItemLevel + 1);
        doReturn(new ArrayList<>()).when(dataTypeList).getItems();

        dataTypeList.refreshSubItemsFromListItem(listItem, subDataTypes);

        verify(dataTypeList).refreshItemsList(eq(subDataTypes), listItemsCaptor.capture());
        verify(view).addSubItems(eq(listItemDataType), listItemsCaptor.capture());

        assertEquals(8, listItemsCaptor.getValue().size());
    }

    @Test
    public void testRefreshItems() {

        final String uuid1 = "uuid1";
        final String uuid2 = "uuid2";
        final String uuid3 = "uuid3";
        final DataType existingDataType1 = mock(DataType.class);
        final DataType existingDataType2 = mock(DataType.class);
        final DataType newDataType1 = mock(DataType.class);
        final DataType newDataType3 = mock(DataType.class);
        final DataTypeListItem existingDataTypeListItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem existingDataTypeListItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem newDataTypeListItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem newDataTypeListItem3 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> existingItems = new ArrayList<>(asList(existingDataTypeListItem1, existingDataTypeListItem2));
        final List<DataTypeListItem> newItems = new ArrayList<>(asList(newDataTypeListItem1, newDataTypeListItem3));
        final List<DataType> subDataTypes = new ArrayList<>(asList(newDataType1, newDataType3));

        when(existingDataType1.getUUID()).thenReturn(uuid1);
        when(existingDataType2.getUUID()).thenReturn(uuid2);
        when(newDataType1.getUUID()).thenReturn(uuid1);
        when(newDataType3.getUUID()).thenReturn(uuid3);
        when(existingDataTypeListItem1.getDataType()).thenReturn(existingDataType1);
        when(existingDataTypeListItem2.getDataType()).thenReturn(existingDataType2);
        when(newDataTypeListItem1.getDataType()).thenReturn(newDataType1);
        when(newDataTypeListItem3.getDataType()).thenReturn(newDataType3);
        when(dataTypeList.getItems()).thenReturn(existingItems);

        dataTypeList.refreshItemsList(subDataTypes, newItems);

        final List<DataTypeListItem> expectedItems = asList(existingDataTypeListItem2, newDataTypeListItem1, newDataTypeListItem3);
        final List<DataTypeListItem> actualItems = dataTypeList.getItems();

        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testMakeGridItem() {

        final DataTypeListItem expectedListItem = mock(DataTypeListItem.class);

        doCallRealMethod().when(dataTypeList).makeGridItem();
        when(treeGridItems.get()).thenReturn(expectedListItem);

        final DataTypeListItem actualListItem = dataTypeList.makeGridItem();

        verify(expectedListItem).init(eq(dataTypeList));
        assertEquals(expectedListItem, actualListItem);
    }

    @Test
    public void testRemoveItem() {

        final DataType dataType = mock(DataType.class);

        dataTypeList.removeItem(dataType);

        verify(view).removeItem(dataType);
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
    public void testRefreshUpdatedItems() {

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
    }

    private DataType makeDataType(final String name,
                                  final String type,
                                  final DataType... subDataTypes) {
        final DataType dataType = mock(DataType.class);

        when(dataType.getName()).thenReturn(name);
        when(dataType.getType()).thenReturn(type);
        when(dataType.getSubDataTypes()).thenReturn(asList(subDataTypes));

        return dataType;
    }
}
