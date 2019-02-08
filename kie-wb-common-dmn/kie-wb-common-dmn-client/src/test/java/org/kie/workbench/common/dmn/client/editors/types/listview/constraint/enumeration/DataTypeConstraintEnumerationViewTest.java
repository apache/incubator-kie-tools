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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintEnumerationViewTest {

    @Mock
    private HTMLDivElement items;

    @Mock
    private HTMLAnchorElement addIcon;

    @Mock
    private DataTypeConstraintEnumeration presenter;

    private DataTypeConstraintEnumerationView view;

    @Before
    public void setup() {
        view = new DataTypeConstraintEnumerationView(items, addIcon);
        view.init(presenter);
    }

    @Test
    public void testOnAddIconClick() {
        view.onAddIconClick(mock(ClickEvent.class));
        verify(presenter).addEnumerationItem();
    }

    @Test
    public void testClear() {
        items.innerHTML = "something";
        view.clear();
        assertTrue(items.innerHTML.isEmpty());
    }

    @Test
    public void testAddItem() {
        final Element enumerationItem = mock(Element.class);
        view.addItem(enumerationItem);
        verify(items).appendChild(enumerationItem);
    }
}
