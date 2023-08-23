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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TreeListItemTest {

    @Mock
    private TreeListItem.View view;

    @Mock
    private HTMLElement viewElement;

    private TreeListItem treeListItem;

    @Before
    public void setup() {

        treeListItem = spy(new TreeListItem(view));
        when(view.getElement()).thenReturn(viewElement);
    }

    @Test
    public void testSetup() {

        treeListItem.setup();

        verify(view).init(treeListItem);
    }

    @Test
    public void testGetElement() {

        final Node actual = treeListItem.getElement();
        assertEquals(viewElement, actual);
    }

    @Test
    public void testGetSetDescription() {

        final String description = "Description.";
        treeListItem.setDescription(description);
        final String actual = treeListItem.getDescription();

        assertEquals(description, actual);
    }

    @Test
    public void testAddSubItem() {

        final TreeListSubItem subItem = mock(TreeListSubItem.class);
        treeListItem.addSubItem(subItem);
    }

    @Test
    public void testUpdateView() {

        treeListItem.updateView();

        verify(view).populate(treeListItem);
    }

    @Test
    public void testGetSetIsSelected() {

        doNothing().when(treeListItem).callOnIsSelectedChanged();
        treeListItem.setIsSelected(true);
        assertTrue(treeListItem.getIsSelected());

        treeListItem.setIsSelected(false);
        assertFalse(treeListItem.getIsSelected());

        verify(treeListItem, times(2)).callOnIsSelectedChanged();
    }

    @Test
    public void testCallOnIsSelectedChanged() {

        final Consumer consumer = mock(Consumer.class);
        doReturn(consumer).when(treeListItem).getOnIsSelectedChanged();

        treeListItem.callOnIsSelectedChanged();

        verify(consumer).accept(treeListItem);
    }

    @Test
    public void testCallOnIsSelectedChangedWhenConsumerIsNotSet() {

        final Consumer consumer = mock(Consumer.class);
        doReturn(null).when(treeListItem).getOnIsSelectedChanged();

        treeListItem.callOnIsSelectedChanged();

        verify(consumer, never()).accept(treeListItem);
    }
}