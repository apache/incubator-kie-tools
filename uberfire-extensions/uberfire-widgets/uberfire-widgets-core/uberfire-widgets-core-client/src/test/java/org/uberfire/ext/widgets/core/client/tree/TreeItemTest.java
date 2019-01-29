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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TreeItemTest {

    public static final String ROOT_VALUE = "rootValue";
    public static final String ROOT_LABEL = "rootLabel";
    public static final String CONTAINER_VALUE = "containerValue";
    public static final String CONTAINER_LABEL = "containerLabel";
    public static final String ITEM_VALUE = "itemValue";
    public static final String ITEM_LABEL = "itemLabel";

    @Mock
    private IsWidget widget;

    @Mock
    private FlowPanel content;

    @Mock
    private FlowPanel treeContainer;

    @Mock
    private Tree<TreeItem> tree;

    private TreeItem testedRoot;
    private TreeItem testedContainer;
    private TreeItem testedItem;

    @Before
    public void setup() {
        final Element element = mock(Element.class);
        when(content.getElement()).thenReturn(element);
        final Style style = mock(Style.class);
        when(element.getStyle()).thenReturn(style);
        testedRoot = new TreeItem(TreeItem.Type.ROOT,
                                  ROOT_VALUE,
                                  ROOT_LABEL,
                                  widget,
                                  () -> content);
        testedRoot.setTree(tree);
        testedContainer = new TreeItem(TreeItem.Type.CONTAINER,
                                       CONTAINER_VALUE,
                                       CONTAINER_LABEL,
                                       widget,
                                       () -> content);
        testedItem = new TreeItem(TreeItem.Type.ITEM,
                                  ITEM_VALUE,
                                  ITEM_LABEL,
                                  widget,
                                  () -> content);
    }

    @Test
    public void testGetters() {
        assertEquals(TreeItem.State.CLOSE,
                     testedRoot.getState());
        assertEquals(TreeItem.Type.ROOT,
                     testedRoot.getType());
        assertEquals(ROOT_LABEL,
                     testedRoot.getLabel());
        assertEquals(ROOT_VALUE,
                     testedRoot.getUuid());
        assertEquals(TreeItem.State.CLOSE,
                     testedContainer.getState());
        assertEquals(TreeItem.Type.CONTAINER,
                     testedContainer.getType());
        assertEquals(CONTAINER_LABEL,
                     testedContainer.getLabel());
        assertEquals(CONTAINER_VALUE,
                     testedContainer.getUuid());
        assertEquals(TreeItem.State.NONE,
                     testedItem.getState());
        assertEquals(TreeItem.Type.ITEM,
                     testedItem.getType());
        assertEquals(ITEM_LABEL,
                     testedItem.getLabel());
        assertEquals(ITEM_VALUE,
                     testedItem.getUuid());
    }

    @Test
    public void testAddItem() {
        final TreeItem childTreeItem = mock(TreeItem.class);
        when(childTreeItem.getType()).thenReturn(TreeItem.Type.ITEM);
        final TreeItem treeItem1 = testedRoot.addItem(childTreeItem);
        assertEquals(treeItem1,
                     childTreeItem);
        verify(childTreeItem,
               times(1)).setTree(eq(tree));
        verify(childTreeItem,
               times(1)).setParentItem(eq(testedRoot));
        verify(content,
               times(1)).add(eq(childTreeItem));
    }

    @Test
    public void testAddContainer() {
        final TreeItem childTreeContainer = mock(TreeItem.class);
        when(childTreeContainer.getType()).thenReturn(TreeItem.Type.CONTAINER);
        final TreeItem treeItem1 = testedRoot.addItem(childTreeContainer);
        assertEquals(treeItem1,
                     childTreeContainer);
        verify(childTreeContainer,
               times(1)).setTree(eq(tree));
        verify(childTreeContainer,
               times(1)).setParentItem(eq(testedRoot));
        verify(content,
               times(1)).add(eq(childTreeContainer));
    }

    @Test
    public void testAddItemToContainer() {
        final TreeItem childTreeContainer = mock(TreeItem.class);
        when(childTreeContainer.getType()).thenReturn(TreeItem.Type.CONTAINER);
        final TreeItem childTreeItem = mock(TreeItem.class);
        when(childTreeItem.getType()).thenReturn(TreeItem.Type.ITEM);
        final TreeItem treeItem1 = testedRoot.addItem(childTreeContainer);
        final TreeItem treeItem2 = testedContainer.addItem(treeItem1);
        assertEquals(treeItem1,
                     childTreeContainer);
        assertEquals(treeItem2,
                     treeItem1);
        verify(childTreeContainer,
               times(1)).setTree(eq(tree));
        verify(childTreeContainer,
               times(1)).setParentItem(eq(testedRoot));
        verify(content,
               times(2)).add(eq(treeItem2));
        verify(treeItem1,
               times(1)).setTree(eq(tree));
    }

    @Test
    public void testInsertItem() {
        final TreeItem childTreeItem = mock(TreeItem.class);
        when(childTreeItem.getType()).thenReturn(TreeItem.Type.ITEM);
        final int index = 7;
        final TreeItem treeItem1 = testedRoot.insertItem(childTreeItem, index);
        assertEquals(treeItem1,
                     childTreeItem);
        verify(childTreeItem,
               times(1)).setTree(eq(tree));
        verify(childTreeItem,
               times(1)).setParentItem(eq(testedRoot));
        verify(content,
               times(1)).insert(eq(childTreeItem), eq(index));
    }

    @Test
    public void testInsertContainer() {
        final TreeItem childTreeContainer = mock(TreeItem.class);
        when(childTreeContainer.getType()).thenReturn(TreeItem.Type.CONTAINER);
        final int index = 10;
        final TreeItem treeItem1 = testedRoot.insertItem(childTreeContainer, index);
        assertEquals(treeItem1,
                     childTreeContainer);
        verify(childTreeContainer,
               times(1)).setTree(eq(tree));
        verify(childTreeContainer,
               times(1)).setParentItem(eq(testedRoot));
        verify(content,
               times(1)).insert(eq(childTreeContainer), eq(index));
    }

    @Test
    public void testInsertItemToContainer() {
        final TreeItem childTreeContainer = mock(TreeItem.class);
        when(childTreeContainer.getType()).thenReturn(TreeItem.Type.CONTAINER);
        final TreeItem childTreeItem = mock(TreeItem.class);
        when(childTreeItem.getType()).thenReturn(TreeItem.Type.ITEM);
        final int index1 = 17;
        final int index2 = 25;
        final TreeItem treeItem1 = testedRoot.insertItem(childTreeContainer, index1);
        final TreeItem treeItem2 = testedContainer.insertItem(treeItem1, index2);
        assertEquals(treeItem1,
                     childTreeContainer);
        assertEquals(treeItem2,
                     treeItem1);
        verify(childTreeContainer,
               times(1)).setTree(eq(tree));
        verify(childTreeContainer,
               times(1)).setParentItem(eq(testedRoot));
        verify(content,
               times(1)).insert(eq(treeItem2), eq(index1));
        verify(content,
               times(1)).insert(eq(treeItem2), eq(index2));
        verify(treeItem1,
               times(1)).setTree(eq(tree));
    }

    @Test
    public void testGetChildCount() {
        testedRoot.getChildCount();
        verify(content,
               times(1)).getWidgetCount();
    }

    @Test
    public void testRemoveItems() {
        testedRoot.removeItems();
        verify(content,
               times(1)).clear();
    }

    @Test
    public void testRemove() {
        Tree<TreeItem> tree = new Tree<>(() -> treeContainer);
        testedRoot.setTree(tree);
        tree.addItem(testedRoot);
        testedRoot.remove();
        verify(treeContainer,
               times(1)).remove(eq(testedRoot));
    }

    @Test
    public void testRemoveItem() {
        final TreeItem item = mock(TreeItem.class);
        when(item.getType()).thenReturn(TreeItem.Type.ITEM);
        testedRoot.addItem(item);
        testedRoot.removeItem(item);
        verify(content,
               times(1)).remove(eq(item));
    }

    @Test
    public void testRemoveItemFromParent() {
        final TreeItem childTreeContainer = mock(TreeItem.class);
        when(childTreeContainer.getType()).thenReturn(TreeItem.Type.CONTAINER);
        final TreeItem childTreeItem = mock(TreeItem.class);
        when(childTreeItem.getType()).thenReturn(TreeItem.Type.ITEM);
        testedRoot.addItem(childTreeContainer);
        testedRoot.removeItem(childTreeContainer);
        verify(content,
               times(1)).remove(eq(childTreeContainer));
    }

    @Test
    public void testGetItemByUuid() {
        final TreeItem treeItemTest = testedRoot.getItemByUuid(ROOT_VALUE);
        assertEquals(treeItemTest,
                     testedRoot);
    }

    @Test
    public void testGetItemByUuidChildren() {
        final TreeItem item = mock(TreeItem.class);
        when(item.getItemByUuid(ITEM_VALUE)).thenReturn(item);
        when(content.getWidgetCount()).thenReturn(1);
        when(content.getWidget(eq(0))).thenReturn(item);
        final TreeItem treeItemTest = testedRoot.getItemByUuid(ITEM_VALUE);
        assertEquals(treeItemTest,
                     item);
    }
}