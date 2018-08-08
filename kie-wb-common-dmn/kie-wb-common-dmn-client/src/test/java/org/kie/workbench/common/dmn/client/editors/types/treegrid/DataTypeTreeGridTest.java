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

package org.kie.workbench.common.dmn.client.editors.types.treegrid;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeTreeGridTest {

    @Mock
    private DataTypeTreeGrid.View view;

    @Mock
    private ManagedInstance<DataTypeTreeGridItem> treeGridItems;

    @Mock
    private DataTypeTreeGridItem.View gridItemView;

    @Mock
    private DataTypeSelect typeSelect;

    private DataTypeTreeGrid treeGrid;

    @Before
    public void setup() {
        treeGrid = spy(new DataTypeTreeGrid(view, treeGridItems) {

            @Override
            DataTypeTreeGridItem getGridItem() {
                return new DataTypeTreeGridItem(gridItemView, typeSelect);
            }
        });
    }

    @Test
    public void testSetupItemsWithoutSubItems() {

        final DataType dataType = makeDataType("item", "iITem");

        treeGrid.setupItems(dataType);

        verify(treeGrid).makeTreeGridItems(eq(dataType), eq(1));
        verify(treeGrid, times(1)).makeTreeGridItems(any(), anyInt());
        verify(view).setupGridItems(anyListOf(DataTypeTreeGridItem.class));
    }

    @Test
    public void testSetupItemsWithSubItems() {

        final DataType subDataType3 = makeDataType("subItem3", "subItemType3");
        final DataType subDataType1 = makeDataType("subItem1", "subItemType1");
        final DataType subDataType2 = makeDataType("subItem2", "subItemType2", subDataType3);
        final DataType dataType = makeDataType("item", "iITem", subDataType1, subDataType2);

        treeGrid.setupItems(dataType);

        verify(treeGrid).makeTreeGridItems(eq(dataType), eq(1));
        verify(treeGrid).makeTreeGridItems(eq(subDataType1), eq(2));
        verify(treeGrid).makeTreeGridItems(eq(subDataType2), eq(2));
        verify(treeGrid).makeTreeGridItems(eq(subDataType3), eq(3));
        verify(treeGrid, times(4)).makeTreeGridItems(any(), anyInt());
        verify(view).setupGridItems(anyListOf(DataTypeTreeGridItem.class));
    }

    @Test
    public void testMakeTreeGridItems() {

        final DataType item1 = makeDataType("item1", "iITem1");
        final DataType item2 = makeDataType("item2", "iITem2");
        final DataType item = makeDataType("item", "iITem", item1, item2);

        final List<DataTypeTreeGridItem> gridItems = treeGrid.makeTreeGridItems(item, 1);
        final DataTypeTreeGridItem gridItem0 = gridItems.get(0);
        final DataTypeTreeGridItem gridItem1 = gridItems.get(1);
        final DataTypeTreeGridItem gridItem2 = gridItems.get(2);

        assertEquals(3, gridItems.size());
        assertEquals(1, gridItem0.getLevel());
        assertEquals(2, gridItem1.getLevel());
        assertEquals(2, gridItem2.getLevel());
        assertEquals(item, gridItem0.getDataType());
        assertEquals(item1, gridItem1.getDataType());
        assertEquals(item2, gridItem2.getDataType());
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
