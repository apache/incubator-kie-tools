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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.NodeList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView.DATA_POSITION;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintEnumerationViewTest {

    @Mock
    private HTMLDivElement items;

    @Mock
    private HTMLAnchorElement addIcon;

    @Mock
    private HTMLDivElement addButtonContainer;

    @Mock
    private DataTypeConstraintEnumeration presenter;

    private DataTypeConstraintEnumerationView view;

    @Before
    public void setup() {
        view = spy(new DataTypeConstraintEnumerationView(items, addIcon, addButtonContainer));
        view.init(presenter);
    }

    @Test
    public void testOnAddIconClick() {
        view.onAddIconClick(mock(ClickEvent.class));
        verify(presenter).addEnumerationItem();
    }

    @Test
    public void testClear() {

        final Element element = mock(Element.class);
        items.firstChild = element;

        when(items.removeChild(element)).then(a -> {
            items.firstChild = null;
            return element;
        });

        view.clear();

        verify(items).removeChild(element);
    }

    @Test
    public void testAddItem() {
        final Element enumerationItem = mock(Element.class);
        final DragAndDropHelper helper = mock(DragAndDropHelper.class);
        items.childNodes = mock(NodeList.class);
        doReturn(helper).when(view).getDragAndDropHelper();

        view.addItem(enumerationItem);

        verify(items).appendChild(enumerationItem);
        verify(helper).refreshItemsPosition();
        verify(enumerationItem).setAttribute(DATA_POSITION, 0);
    }
}
