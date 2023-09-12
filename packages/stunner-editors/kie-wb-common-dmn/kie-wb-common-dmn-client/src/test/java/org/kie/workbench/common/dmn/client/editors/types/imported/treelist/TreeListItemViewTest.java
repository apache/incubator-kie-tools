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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TreeListItemViewTest {

    @Mock
    private HTMLDivElement itemHeader;

    @Mock
    private HTMLDivElement itemsContainer;

    @Mock
    private HTMLDivElement itemDetails;

    @Mock
    private HTMLDivElement expandContainer;

    @Mock
    private HTMLElement expand;

    @Mock
    private HTMLInputElement checkbox;

    @Mock
    private HTMLDivElement root;

    private TreeListItemView itemView;

    @Before
    public void setup() {

        itemView = spy(new TreeListItemView(itemHeader,
                                            itemsContainer,
                                            itemDetails,
                                            expandContainer,
                                            expand,
                                            checkbox,
                                            root));

        doNothing().when(itemView).showElement(any());
        doNothing().when(itemView).hideElement(any());
    }

    @Test
    public void testInit() {
        final TreeListItem presenter = mock(TreeListItem.class);
        itemView.init(presenter);
        final TreeListItem actual = itemView.getPresenter();
        assertEquals(actual, presenter);
    }

    @Test
    public void testOnCheckboxChanged() {

        final TreeListItem presenter = mock(TreeListItem.class);
        itemView.init(presenter);
        checkbox.checked = true;

        itemView.onCheckboxChanged(null);

        verify(presenter).setIsSelected(true);

        checkbox.checked = false;

        itemView.onCheckboxChanged(null);

        verify(presenter).setIsSelected(false);
    }

    @Test
    public void testOnClickCheckbox() {

        final ClickEvent event = mock(ClickEvent.class);
        doReturn(checkbox).when(itemView).getTarget(event);

        itemView.onClick(event);

        verify(itemView, never()).showElement(itemsContainer);
        verify(itemView, never()).hideElement(itemsContainer);
    }

    @Test
    public void testPopulate() {

        final List<TreeListSubItem> subItems = new ArrayList<>();
        final String itemDescription = "Item Description";
        final TreeListItem item = mock(TreeListItem.class);
        when(item.getIsSelected()).thenReturn(true);
        when(item.getDescription()).thenReturn(itemDescription);
        when(item.getSubItems()).thenReturn(subItems);

        doNothing().when(itemView).setExpandVisibility(item);

        itemView.populate(item);

        assertTrue(checkbox.checked);
        assertEquals(itemDescription, itemDetails.textContent);
        verify(itemView).addSubItems(item);
        verify(itemView).setExpandVisibility(item);
    }

    @Test
    public void testSetExpandVisibility() {

        final TreeListItem item = mock(TreeListItem.class);
        final List<TreeListSubItem> subItems = new ArrayList<>();
        when(item.getSubItems()).thenReturn(subItems);

        itemView.setExpandVisibility(item);

        verify(itemView).hideElement(expand);

        subItems.add(mock(TreeListSubItem.class));

        itemView.setExpandVisibility(item);

        verify(itemView).showElement(expand);
    }

    @Test
    public void testAddSubItems() {

        final TreeListItem item = mock(TreeListItem.class);
        final TreeListSubItem subItem1 = mock(TreeListSubItem.class);
        final Node element1 = mock(Node.class);
        when(subItem1.getElement()).thenReturn(element1);
        final TreeListSubItem subItem2 = mock(TreeListSubItem.class);
        final Node element2 = mock(Node.class);
        when(subItem2.getElement()).thenReturn(element2);
        final List<TreeListSubItem> subItems = Arrays.asList(subItem1, subItem2);

        when(item.getSubItems()).thenReturn(subItems);

        itemView.addSubItems(item);

        verify(itemsContainer).appendChild(element2);
        verify(itemsContainer).appendChild(element2);
    }
}