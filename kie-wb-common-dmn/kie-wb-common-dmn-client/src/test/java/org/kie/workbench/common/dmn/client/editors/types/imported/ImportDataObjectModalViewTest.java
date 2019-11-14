/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.imported;

import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.Node;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeList;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ImportDataObjectModalViewTest {

    private ImportDataObjectModalView view;

    @Mock
    private HTMLDivElement header;

    @Mock
    private HTMLDivElement body;

    @Mock
    private HTMLDivElement footer;

    @Mock
    private TreeList treeList;

    @Mock
    private HTMLElement noteText;

    @Mock
    private HTMLLabelElement noteLabel;

    @Mock
    private HTMLDivElement itemsContainer;

    @Mock
    private HTMLAnchorElement clearSelection;

    @Mock
    private ManagedInstance<TreeListItem> items;

    @Mock
    private HTMLButtonElement buttonImport;

    @Mock
    private HTMLButtonElement buttonCancel;

    @Mock
    private ImportDataObjectModal presenter;

    @Mock
    private Node treeListElement;

    @Captor
    private ArgumentCaptor<List<TreeListItem>> itemsCaptor;

    @Before
    public void setup() {
        view = spy(new ImportDataObjectModalView(header,
                                                 body,
                                                 footer,
                                                 treeList,
                                                 noteText,
                                                 noteLabel,
                                                 itemsContainer,
                                                 clearSelection,
                                                 items,
                                                 buttonImport,
                                                 buttonCancel));

        view.init(presenter);

        when(treeList.getElement()).thenReturn(treeListElement);
    }

    @Test
    public void testGetHeader() {

        final String text = "The text";
        header.textContent = text;

        final String actual = view.getHeader();

        assertEquals(actual, text);
    }

    @Test
    public void testOnButtonCancelClicked() {

        view.onButtonCancelClicked(null);

        verify(presenter).hide();
    }

    @Test
    public void testOnButtonImportClicked() {

        final List<DataObject> selectedItems = mock(List.class);
        doReturn(selectedItems).when(view).getSelectedItems();
        view.onButtonImportClicked(null);

        verify(presenter).hide(selectedItems);
    }

    @Test
    public void testGetSelectedItems() {

        final TreeListItem listItem1 = mock(TreeListItem.class);
        final DataObject dataObject1 = mock(DataObject.class);
        final TreeListItem listItem2 = mock(TreeListItem.class);
        final DataObject dataObject2 = mock(DataObject.class);

        when(listItem1.getDataSource()).thenReturn(dataObject1);
        when(listItem2.getDataSource()).thenReturn(dataObject2);

        final List<TreeListItem> selectedItems = Arrays.asList(listItem1, listItem2);

        when(treeList.getSelectedItems()).thenReturn(selectedItems);

        final List<DataObject> actual = view.getSelectedItems();

        assertEquals(2, actual.size());
        assertTrue(actual.contains(dataObject1));
        assertTrue(actual.contains(dataObject2));
    }

    @Test
    public void testOnClearSelectionClicked() {

        view.onClearSelectionClicked(null);

        doNothing().when(view).refresh();
        verify(treeList).refresh();
        verify(view).refresh();
    }

    @Test
    public void testAddItems() {

        final String d1Name = "ClassOne";
        final String d2Name = "ClassTwo";
        final String d3Name = "ClassThree";
        final DataObject d1 = new DataObject(d1Name);
        final DataObject d2 = new DataObject(d2Name);
        final DataObject d3 = new DataObject(d3Name);

        final List<DataObject> dataObjects = Arrays.asList(d1, d2, d3);

        final TreeListItem list1 = mock(TreeListItem.class);
        final TreeListItem list2 = mock(TreeListItem.class);
        final TreeListItem list3 = mock(TreeListItem.class);
        doReturn(list1).when(view).createTreeListItem(d1);
        doReturn(list2).when(view).createTreeListItem(d2);
        doReturn(list3).when(view).createTreeListItem(d3);

        view.addItems(dataObjects);

        verify(itemsContainer).appendChild(treeListElement);

        verify(treeList).populate(itemsCaptor.capture());

        final List<TreeListItem> dataObjectItems = itemsCaptor.getValue();

        assertEquals(3, dataObjectItems.size());
        assertTrue(dataObjectItems.contains(list1));
        assertTrue(dataObjectItems.contains(list2));
        assertTrue(dataObjectItems.contains(list3));
    }

    @Test
    public void testCreateTreeListItem() {

        final TreeListItem item = mock(TreeListItem.class);

        when(items.get()).thenReturn(item);

        final String myClass = "my class";
        final DataObject data = new DataObject(myClass);

        final TreeListItem actual = view.createTreeListItem(data);

        assertEquals(item, actual);

        verify(item).setDescription(myClass);
    }

    @Test
    public void testRefresh() {

        doNothing().when(view).removeTreeList();

        view.refresh();

        verify(treeList).refresh();
        verify(itemsContainer).appendChild(treeListElement);
    }

    @Test
    public void testClear() {

        doNothing().when(view).removeTreeList();

        view.clear();

        verify(view).removeTreeList();
        verify(treeList).clear();
    }

    @Test
    public void testRemoveTreeList() {

        when(itemsContainer.contains(treeListElement)).thenReturn(true);

        view.removeTreeList();

        verify(itemsContainer).removeChild(treeListElement);
    }

    @Test
    public void testRemoveTreeListWhenIsNotPresent() {

        when(itemsContainer.contains(treeListElement)).thenReturn(false);

        view.removeTreeList();

        verify(itemsContainer, never()).removeChild(treeListElement);
    }
}