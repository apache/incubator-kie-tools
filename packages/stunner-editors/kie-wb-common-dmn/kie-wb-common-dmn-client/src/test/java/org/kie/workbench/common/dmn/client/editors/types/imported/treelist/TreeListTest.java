/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.types.imported.treelist;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TreeListTest {

    @Mock
    private TreeList.View view;

    @Mock
    private HTMLElement element;

    @Mock
    private TreeListItem itemOne;

    @Mock
    private TreeListItem itemTwo;

    @Mock
    private TreeListItem itemThree;

    private TreeList treeList;

    @Before
    public void setup() {
        treeList = spy(new TreeList(view));

        final List<TreeListItem> currentItems = Arrays.asList(itemOne, itemTwo, itemThree);
        doReturn(currentItems).when(treeList).getCurrentItems();

        when(view.getElement()).thenReturn(element);
    }

    @Test
    public void testPopulate() {

        doNothing().when(treeList).refresh();

        final List items = mock(List.class);
        treeList.populate(items);

        verify(treeList).refresh();
    }

    @Test
    public void testRefresh() {

        treeList.refresh();

        verify(treeList).clear();
        verify(itemOne).updateView();
        verify(itemTwo).updateView();
        verify(itemThree).updateView();
        verify(view).add(itemOne);
        verify(view).add(itemTwo);
        verify(view).add(itemThree);
    }

    @Test
    public void testClear() {

        treeList.clear();
        verify(view).clear();
    }

    @Test
    public void testGetElement() {

        final Node actual = treeList.getElement();
        assertEquals(element, actual);
    }

    @Test
    public void testClearSelection() {

        treeList.clearSelection();

        verify(itemOne).setIsSelected(false);
        verify(itemTwo).setIsSelected(false);
        verify(itemThree).setIsSelected(false);
    }

    @Test
    public void testGetSelectedItems() {

        when(itemThree.getIsSelected()).thenReturn(true);

        final List<TreeListItem> selectedItems = treeList.getSelectedItems();
        assertTrue(selectedItems.contains(itemThree));
        assertFalse(selectedItems.contains(itemOne));
        assertFalse(selectedItems.contains(itemTwo));
    }

    @Test
    public void testSelectionChanged() {

        final TreeListItem treeListItem = mock(TreeListItem.class);
        doNothing().when(treeList).callOnSelectionChanged();

        treeList.selectionChanged(treeListItem);

        verify(treeList).callOnSelectionChanged();
    }

    @Test
    public void testCallOnSelectionChanged() {

        final List selectedItems = mock(List.class);
        final Consumer consumer = mock(Consumer.class);
        doReturn(consumer).when(treeList).getOnSelectionChanged();
        doReturn(selectedItems).when(treeList).getSelectedItems();

        treeList.callOnSelectionChanged();

        verify(consumer).accept(selectedItems);
    }

    @Test
    public void testCallOnSelectionChangedWhenConsumerIsNotSet() {

        final List selectedItems = mock(List.class);
        final Consumer consumer = mock(Consumer.class);
        doReturn(null).when(treeList).getOnSelectionChanged();
        doReturn(selectedItems).when(treeList).getSelectedItems();

        treeList.callOnSelectionChanged();

        verify(consumer, never()).accept(selectedItems);
    }
}