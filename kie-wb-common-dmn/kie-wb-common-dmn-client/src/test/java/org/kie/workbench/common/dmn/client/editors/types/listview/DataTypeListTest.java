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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
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

    @Mock
    private DataTypeListItem.View listItemView;

    @Mock
    private DataTypeSelect typeSelect;

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
    public void testSetupItemsWithoutSubItems() {

        final DataType dataType1 = makeDataType("item", "iITem");
        final DataType dataType2 = makeDataType("item", "iITem");
        final List<DataType> dataTypes = Arrays.asList(dataType1, dataType2);

        dataTypeList.setupItems(dataTypes);

        verify(dataTypeList).makeTreeGridItems(eq(dataType1), eq(1));
        verify(dataTypeList).makeTreeGridItems(eq(dataType2), eq(1));
        verify(dataTypeList, times(2)).makeTreeGridItems(any(), anyInt());
        verify(view).setupListItems(anyListOf(DataTypeListItem.class));
    }

    @Test
    public void testSetupItemsWithSubItems() {

        final DataType subDataType3 = makeDataType("subItem3", "subItemType3");
        final DataType subDataType1 = makeDataType("subItem1", "subItemType1");
        final DataType subDataType2 = makeDataType("subItem2", "subItemType2", subDataType3);
        final DataType dataType = makeDataType("item", "iITem", subDataType1, subDataType2);
        final List<DataType> dataTypes = Collections.singletonList(dataType);

        dataTypeList.setupItems(dataTypes);

        verify(dataTypeList).makeTreeGridItems(eq(dataType), eq(1));
        verify(dataTypeList).makeTreeGridItems(eq(subDataType1), eq(2));
        verify(dataTypeList).makeTreeGridItems(eq(subDataType2), eq(2));
        verify(dataTypeList).makeTreeGridItems(eq(subDataType3), eq(3));
        verify(dataTypeList, times(4)).makeTreeGridItems(any(), anyInt());
        verify(view).setupListItems(anyListOf(DataTypeListItem.class));
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
        final List<DataType> subDataTypes = Arrays.asList(subDataType1, subDataType2);
        final List<DataType> dataTypesX = Arrays.asList(dataTypeX, dataTypeX, dataTypeX, dataTypeX);

        when(listItem.getDataType()).thenReturn(listItemDataType);
        when(listItem.getLevel()).thenReturn(listItemLevel);
        doReturn(dataTypesX).when(dataTypeList).makeTreeGridItems(subDataType1, listItemLevel + 1);
        doReturn(dataTypesX).when(dataTypeList).makeTreeGridItems(subDataType2, listItemLevel + 1);

        dataTypeList.refreshSubItems(listItem, subDataTypes);

        verify(view).addSubItems(eq(listItemDataType), listItemsCaptor.capture());

        assertEquals(8, listItemsCaptor.getValue().size());
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
