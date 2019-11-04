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

package org.kie.workbench.common.dmn.client.editors.types.imported.treelist;

import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TreeListItemTest {

    @Mock
    private TreeListItem.View view;

    @Mock
    private HTMLElement viewElement;

    private TreeListItem treeListItem;

    @Before
    public void setup() {

        treeListItem = new TreeListItem(view);
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

        treeListItem.setIsSelected(true);
        assertTrue(treeListItem.getIsSelected());

        treeListItem.setIsSelected(false);
        assertFalse(treeListItem.getIsSelected());
    }
}