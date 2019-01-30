/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.explorer.tree;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.tree.Tree;
import org.uberfire.ext.widgets.core.client.tree.TreeItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TreeExplorerViewTest {

    static final String ITEM_UUID = "item";
    static final String PARENT_UUID = "parent";
    static final String NAME = "name";

    @Mock
    TreeExplorer presenter;

    @Mock
    TreeExplorerView.ViewBinder uiBinder;

    @Mock
    Tree<TreeItem> tree;

    @Mock
    HandlerRegistration handlerRegistration;

    @Mock
    TreeItem item;

    @Mock
    TreeItem parentItem;

    @Mock
    Widget widgetIcon;

    TreeExplorerView testedTreeExplorerView;

    @Before
    public void setup() {
        this.testedTreeExplorerView = new TreeExplorerView(presenter,
                                                           uiBinder,
                                                           tree,
                                                           handlerRegistration);
    }

    @Test
    public void testInit() {
        testedTreeExplorerView.init(presenter);
        verify(uiBinder,
               times(1)).createAndBindUi(eq(testedTreeExplorerView));
        verify(tree,
               times(1)).addSelectionHandler(any(SelectionHandler.class));
    }

    @Test
    public void testDestroy() {
        testedTreeExplorerView.destroy();
        verify(handlerRegistration,
               times(1)).removeHandler();
    }

    @Test
    public void removeItem() {
        when(tree.getItemByUuid(ITEM_UUID)).thenReturn(item);
        testedTreeExplorerView.removeItem(ITEM_UUID);
        verify(tree,
               times(1)).getItemByUuid(eq(ITEM_UUID));
        verify(item,
               times(1)).remove();
    }

    @Test
    public void removeNoItem() {
        testedTreeExplorerView.removeItem(ITEM_UUID);
        verify(tree, times(1)).getItemByUuid(eq(ITEM_UUID));
        verify(item, never()).remove();
    }

    @Test
    public void addItem() {
        testedTreeExplorerView.addItem(ITEM_UUID,
                                       NAME,
                                       widgetIcon,
                                       true,
                                       true);

        ArgumentCaptor<TreeItem> itemCaptor = ArgumentCaptor.forClass(TreeItem.class);

        verify(tree,
               times(1)).addItem(itemCaptor.capture());

        TreeItem item = itemCaptor.getValue();

        assertEquals(ITEM_UUID,
                     item.getUuid());
        assertEquals(NAME,
                     item.getLabel());
    }

    @Test
    public void addItemWithParent() {
        when(tree.getItemByUuid(PARENT_UUID)).thenReturn(parentItem);
        testedTreeExplorerView.addItem(ITEM_UUID,
                                       PARENT_UUID,
                                       NAME,
                                       widgetIcon,
                                       true,
                                       true);
        verify(parentItem,
               times(1)).addItem(eq(TreeItem.Type.CONTAINER),
                                 eq(ITEM_UUID),
                                 eq(NAME),
                                 eq(widgetIcon));
    }

    @Test
    public void isItemNameChanged() {
        TreeItem oldItem = mock(TreeItem.class);
        when(tree.getItemByUuid(ITEM_UUID)).thenReturn(oldItem);
        when(oldItem.getLabel()).thenReturn("OLD_ITEM");
        boolean isItemChanged = testedTreeExplorerView.isItemChanged(ITEM_UUID,
                                                                     PARENT_UUID,
                                                                     NAME);
        assertTrue(isItemChanged);
    }

    @Test
    public void isItemParentChanged() {
        TreeItem oldItem = mock(TreeItem.class);
        when(tree.getItemByUuid(ITEM_UUID)).thenReturn(oldItem);
        when(oldItem.getLabel()).thenReturn(NAME);
        when(item.getParentItem()).thenReturn(parentItem);
        when(oldItem.getParentItem()).thenReturn(parentItem);
        when(oldItem.getUuid()).thenReturn(ITEM_UUID);
        when(parentItem.getUuid()).thenReturn("PARENT_CHANGED");
        assertTrue(testedTreeExplorerView.isItemChanged(ITEM_UUID,
                                                        PARENT_UUID,
                                                        NAME));
    }

    @Test
    public void isNotItemChanged() {
        TreeItem oldItem = mock(TreeItem.class);
        when(tree.getItemByUuid(ITEM_UUID)).thenReturn(oldItem);
        when(oldItem.getLabel()).thenReturn(NAME);
        when(item.getParentItem()).thenReturn(parentItem);
        when(oldItem.getParentItem()).thenReturn(parentItem);
        when(oldItem.getUuid()).thenReturn(ITEM_UUID);
        when(parentItem.getUuid()).thenReturn(PARENT_UUID);
        assertFalse(testedTreeExplorerView.isItemChanged(ITEM_UUID,
                                                         PARENT_UUID,
                                                         NAME));
    }

    @Test
    public void testClear() {
        testedTreeExplorerView.clear();
        verify(tree,
               times(1)).clear();
    }

    @Test
    public void testIsContainer() {
        when(tree.getItemByUuid(PARENT_UUID)).thenReturn(parentItem);
        when(parentItem.getType()).thenReturn(TreeItem.Type.CONTAINER);
        assertTrue(testedTreeExplorerView.isContainer(PARENT_UUID));
    }

    @Test
    public void testNotIsContainer() {
        when(tree.getItemByUuid(ITEM_UUID)).thenReturn(item);
        when(item.getType()).thenReturn(TreeItem.Type.ITEM);
        assertFalse(testedTreeExplorerView.isContainer(ITEM_UUID));
    }

    @Test
    public void testSetSelectedItem() {
        when(tree.getItemByUuid(ITEM_UUID)).thenReturn(item);
        testedTreeExplorerView.setSelectedItem(ITEM_UUID);
        verify(tree,
               times(1)).setSelectedItem(eq(item),
                                         eq(false));
    }
}
