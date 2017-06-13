/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.core.client.tree;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TreeTest {

    @Mock
    private IsWidget widget;

    @Mock
    private FlowPanel container;

    @Mock
    private FlowPanel content;

    private Tree<TreeItem> testedTree;

    @Before
    public void setup() {
        testedTree = new Tree<TreeItem>(() -> container);
    }

    @Test
    public void testIsEmpty() {
        assertEquals(testedTree.isEmpty(),
                     true);
    }

    @Test
    public void testAddItem() {
        final TreeItem item = mock(TreeItem.class);
        testedTree.addItem(item);
        item.setTree(testedTree);
        verify(container,
               times(1)).add(eq(item));
    }

    @Test
    public void testGetItem() {
        final TreeItem item1 = mock(TreeItem.class);
        when(container.getWidgetCount()).thenReturn(1);
        when(container.getWidget(eq(0))).thenReturn(item1);
        testedTree.addItem(item1);
        verify(container,
               times(1)).add(eq(item1));
        assertEquals(testedTree.getItem(0),
                     item1);
    }

    @Test
    public void testGetItemByUuuid() {
        final TreeItem item1 = mock(TreeItem.class);
        when(item1.getUuid()).thenReturn("test");
        when(item1.getItemByUuid("test")).thenReturn(item1);
        when(container.getWidgetCount()).thenReturn(1);
        when(container.getWidget(eq(0))).thenReturn(item1);

        testedTree.addItem(item1);
        verify(container,
               times(1)).add(eq(item1));
        assertEquals(testedTree.getItem(0),
                     item1);

        TreeItem item = testedTree.getItemByUuid("test");
        assertEquals(item1,
                     item);
    }

    @Test
    public void testGetItemByUuuidWithParent() {
        final TreeItem item1 = mock(TreeItem.class);
        when(item1.getUuid()).thenReturn("test");
        when(item1.getItemByUuid("test")).thenReturn(item1);
        final TreeItem parent = mock(TreeItem.class);
        when(parent.getUuid()).thenReturn("parent");
        when(parent.getChild(0)).thenReturn(item1);
        when(parent.getChildCount()).thenReturn(1);
        when(parent.getItemByUuid("parent")).thenReturn(parent);
        when(container.getWidgetCount()).thenReturn(1);
        when(container.getWidget(eq(0))).thenReturn(item1);

        testedTree.addItem(parent);

        verify(container,
               times(1)).add(eq(parent));
        assertEquals(testedTree.getItem(0),
                     item1);

        TreeItem item = testedTree.getItemByUuid("test");
        assertEquals(item1,
                     item);
    }

    @Test
    public void testRemoveItem() {
        final TreeItem item1 = mock(TreeItem.class);
        when(container.getWidgetCount()).thenReturn(1);
        when(container.getWidget(eq(0))).thenReturn(item1);
        testedTree.addItem(item1);
        testedTree.removeItem(item1);
        verify(container,
               times(1)).remove(item1);
    }

    @Test
    public void testGetItems() {
        final TreeItem item1 = mock(TreeItem.class);
        when(container.getWidgetCount()).thenReturn(1);
        when(container.getWidget(eq(0))).thenReturn(item1);
        testedTree.addItem(item1);
        testedTree.getItems().iterator().hasNext();
        verify(container,
               times(1)).getWidgetCount();

        final int[] idx = {0};
        final TreeItem[] item = new TreeItem[1];
        testedTree.getItems().forEach(i -> {
                                          item[0] = i;
                                          idx[0]++;
                                      }
        );
        assertEquals(idx[0],
                     1);
        assertEquals(item[0],
                     item1);
    }

    @Test
    public void testGetSelectedItem() {
        final TreeItem item1 = mock(TreeItem.class);
        when(container.getWidgetCount()).thenReturn(1);
        when(container.getWidget(eq(0))).thenReturn(item1);
        testedTree.addItem(item1);
        testedTree.setSelectedItem(item1);
        TreeItem itemGet = testedTree.getSelectedItem();
        assertEquals(itemGet,
                     item1);
    }
}