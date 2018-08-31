/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
import elemental2.dom.NodeList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.ElementHelper;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@PrepareForTest(ElementHelper.class)
@RunWith(PowerMockRunner.class)
public class DataTypeListViewTest {

    @Mock
    private HTMLDivElement listItems;

    @Mock
    private HTMLDivElement collapsedDescription;

    @Mock
    private HTMLDivElement expandedDescription;

    @Mock
    private HTMLAnchorElement viewMore;

    @Mock
    private HTMLAnchorElement viewLess;

    @Mock
    private HTMLElement element;

    private DataTypeListView view;

    @Before
    public void setup() {
        view = spy(new DataTypeListView(listItems, collapsedDescription, expandedDescription, viewMore, viewLess));
        doReturn(element).when(view).getElement();
    }

    @Test
    public void testSetupGridItems() {

        final DataTypeListItem gridItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem gridItem2 = mock(DataTypeListItem.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);

        when(gridItem1.getElement()).thenReturn(element1);
        when(gridItem2.getElement()).thenReturn(element2);

        view.setupListItems(Arrays.asList(gridItem1, gridItem2));

        verify(listItems).appendChild(eq(element1));
        verify(listItems).appendChild(eq(element2));
    }

    @Test
    public void testCleanSubTypes() {

        final String parentUUID = "parentUUID";
        final NodeList<Element> children = spy(new NodeList<>());
        final Element child1 = makeElement("child1UUID");
        final Element child2 = makeElement("child2UUID");

        doReturn(child1).when(children).getAt(0);
        doReturn(child2).when(children).getAt(1);
        children.length = 2;

        mockDOMElementsByParentUUID(parentUUID, children);

        view.cleanSubTypes(parentUUID);

        verify(child1).remove();
        verify(child2).remove();
    }

    @Test
    public void testAddSubItems() {

        final DataType dataType = mock(DataType.class);
        final String dataTypeUUID = "dataTypeUUID";
        final HTMLElement dataTypeRow = makeHTMLElement();
        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final HTMLElement listItemElement1 = makeHTMLElement();
        final HTMLElement listItemElement2 = makeHTMLElement();
        final List<DataTypeListItem> listItems = Arrays.asList(listItem1, listItem2);

        when(dataType.getUUID()).thenReturn(dataTypeUUID);
        when(element.querySelector("[data-row-uuid=\"" + dataTypeUUID + "\"]")).thenReturn(dataTypeRow);
        when(listItem1.getElement()).thenReturn(listItemElement1);
        when(listItem2.getElement()).thenReturn(listItemElement2);
        doNothing().when(view).cleanSubTypes(dataTypeUUID);
        mockStatic(ElementHelper.class);

        view.addSubItems(dataType, listItems);

        verifyStatic();
        ElementHelper.insertAfter(listItemElement1, dataTypeRow);
        ElementHelper.insertAfter(listItemElement2, listItemElement1);
    }

    @Test
    public void testSetup() {
        view.setup();

        verify(view).collapseDescription();
    }

    @Test
    public void testOnClickViewMore() {
        view.onClickViewMore(mock(ClickEvent.class));

        verify(view).expandDescription();
    }

    @Test
    public void testOnClickViewLess() {
        view.onClickViewLess(mock(ClickEvent.class));

        verify(view).collapseDescription();
    }

    @Test
    public void testExpandDescription() {
        view.expandDescription();

        assertTrue(collapsedDescription.hidden);
        assertFalse(expandedDescription.hidden);
    }

    @Test
    public void testCollapseDescription() {
        view.collapseDescription();

        assertFalse(collapsedDescription.hidden);
        assertTrue(expandedDescription.hidden);
    }

    private HTMLElement makeHTMLElement() {
        final HTMLElement element = mock(HTMLElement.class);
        element.parentNode = mock(Node.class);
        return element;
    }

    public Element makeElement(final String uuid) {

        final Element element = mock(Element.class);

        mockDOMElementsByParentUUID(uuid, new NodeList<>());
        when(element.getAttribute("data-row-uuid")).thenReturn(uuid);

        return element;
    }

    private void mockDOMElementsByParentUUID(final String parentUUID,
                                             final NodeList<Element> rowElements) {
        when(element.querySelectorAll("[data-parent-row-uuid=\"" + parentUUID + "\"]")).thenReturn(rowElements);
    }
}
