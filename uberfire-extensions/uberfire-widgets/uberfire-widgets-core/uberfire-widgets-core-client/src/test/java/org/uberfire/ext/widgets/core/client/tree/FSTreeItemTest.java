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
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.uberfire.ext.widgets.core.client.tree.FSTreeItem.FSType;

@RunWith(GwtMockitoTestRunner.class)
public class FSTreeItemTest {

    public static final String ROOT_VALUE = "root";
    public static final String ROOT_LABEL = "root";

    public static final String FOLDER_VALUE = "folder";
    public static final String FOLDER_LABEL = "folder";

    public static final String ITEM_VALUE = "item";
    public static final String ITEM_LABEL = "item";

    @Mock
    private UIObject uiObject;

    @Mock
    private IsWidget widget;

    @Mock
    private FlowPanel content;

    @Mock
    private FlowPanel item;

    @Mock
    private Tree<FSTreeItem> tree;

    private FSTreeItem testedRoot;
    private FSTreeItem testedContainer;
    private FSTreeItem testedItem;

    @Before
    public void setup() {
        final Element element = mock(Element.class);
        when(content.getElement()).thenReturn(element);
        when(element.getTitle()).thenReturn("title");
        final Style styleItem = mock(Style.class);
        when(element.getStyle()).thenReturn(styleItem);

        when(uiObject.getTitle()).thenReturn("TITLE");

        final Style style = mock(Style.class);
        when(element.getStyle()).thenReturn(style);

        testedRoot = new FSTreeItem(FSTreeItem.FSType.ROOT,
                                    ROOT_VALUE,
                                    () -> content);
        testedRoot.setTree(tree);

        testedContainer = new FSTreeItem(FSTreeItem.FSType.FOLDER,
                                         FOLDER_VALUE,
                                         () -> content);

        testedItem = new FSTreeItem(FSTreeItem.FSType.ITEM,
                                    ITEM_VALUE,
                                    () -> content);
    }

    @Test
    public void testGetters() {
        assertEquals(TreeItem.State.CLOSE,
                     testedRoot.getState());
        assertEquals(FSTreeItem.FSType.ROOT,
                     testedRoot.getFSType());
        assertEquals(ROOT_LABEL,
                     testedRoot.getLabel());
        assertEquals(ROOT_VALUE,
                     testedRoot.getUuid());

        assertEquals(TreeItem.State.CLOSE,
                     testedContainer.getState());
        assertEquals(FSTreeItem.FSType.FOLDER,
                     testedContainer.getFSType());
        assertEquals(FOLDER_LABEL,
                     testedContainer.getLabel());
        assertEquals(FOLDER_VALUE,
                     testedContainer.getUuid());

        assertEquals(TreeItem.State.NONE,
                     testedItem.getState());
        assertEquals(FSTreeItem.FSType.ITEM,
                     testedItem.getFSType());
        assertEquals(ITEM_LABEL,
                     testedItem.getLabel());
        assertEquals(ITEM_VALUE,
                     testedItem.getUuid());
    }

    @Test
    public void testAddItem() {
        final FSTreeItem childTreeItem = mock(FSTreeItem.class);
        when(childTreeItem.getFSType()).thenReturn(FSType.ITEM);
        final FSTreeItem treeItem1 = testedRoot.addItem(childTreeItem);
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
        final FSTreeItem childTreeContainer = mock(FSTreeItem.class);
        when(childTreeContainer.getFSType()).thenReturn(FSTreeItem.FSType.FOLDER);
        final FSTreeItem treeItem1 = testedRoot.addItem(childTreeContainer);
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
        final FSTreeItem childTreeContainer = mock(FSTreeItem.class);
        when(childTreeContainer.getFSType()).thenReturn(FSType.FOLDER);

        final FSTreeItem childTreeItem = mock(FSTreeItem.class);
        when(childTreeItem.getFSType()).thenReturn(FSType.ITEM);

        final FSTreeItem treeItem1 = testedRoot.addItem(childTreeContainer);

        final FSTreeItem treeItem2 = testedContainer.addItem(treeItem1);

        assertEquals(treeItem1,
                     childTreeContainer);
        assertEquals(treeItem2,
                     treeItem1);

        verify(childTreeContainer,
               times(1)).setTree(eq(tree));
        verify(childTreeContainer,
               times(1)).setParentItem(eq(testedRoot));
        verify(content,
               times(2)).add(treeItem2);
        verify(treeItem1,
               times(1)).setTree(eq(tree));
    }
}