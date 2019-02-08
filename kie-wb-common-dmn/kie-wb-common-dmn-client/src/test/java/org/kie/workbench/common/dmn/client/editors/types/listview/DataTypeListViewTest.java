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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
import elemental2.dom.NodeList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.ScrollHelper;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.client.views.pfly.selectpicker.ElementHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.ARROW_BUTTON_SELECTOR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.PARENT_UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.RIGHT_ARROW_CSS_CLASS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

    @Mock
    private HTMLButtonElement addButton;

    @Mock
    private HTMLDivElement placeholder;

    @Mock
    private HTMLDivElement searchBarContainer;

    @Mock
    private HTMLAnchorElement expandAll;

    @Mock
    private HTMLAnchorElement collapseAll;

    @Mock
    private HTMLDivElement noDataTypesFound;

    @Mock
    private DataTypeSearchBar searchBar;

    @Mock
    private HTMLElement searchBarElement;

    @Mock
    private ScrollHelper scrollHelper;

    @Mock
    private DataTypeList presenter;

    private DataTypeListView view;

    @Before
    public void setup() {

        when(presenter.getSearchBar()).thenReturn(searchBar);
        when(searchBar.getElement()).thenReturn(searchBarElement);

        placeholder.classList = mock(DOMTokenList.class);
        noDataTypesFound.classList = mock(DOMTokenList.class);
        listItems.classList = mock(DOMTokenList.class);
        listItems.childNodes = new NodeList<>();

        view = spy(new DataTypeListView(listItems, collapsedDescription, expandedDescription, viewMore, viewLess, addButton, placeholder, searchBarContainer, expandAll, collapseAll, noDataTypesFound, scrollHelper));
        view.init(presenter);

        doReturn(element).when(view).getElement();
    }

    @Test
    public void testInit() {
        // "view.init(..)" called in the setup.
        verify(searchBarContainer).appendChild(searchBarElement);
    }

    @Test
    public void testShowOrHideNoCustomItemsMessageWhenListHasCustomDataType() {

        doReturn(true).when(view).hasCustomDataType();

        view.showOrHideNoCustomItemsMessage();

        verify(view).showListItems();
        verify(view, never()).showPlaceHolder();
    }

    @Test
    public void testShowOrHideNoCustomItemsMessageWhenListDoesNotHaveCustomDataType() {

        doReturn(false).when(view).hasCustomDataType();

        view.showOrHideNoCustomItemsMessage();

        verify(view).showPlaceHolder();
        verify(view, never()).showListItems();
    }

    @Test
    public void testExpandAll() {
        view.expandAll(mock(ClickEvent.class));

        verify(presenter).expandAll();
    }

    @Test
    public void testCollapseAll() {
        view.collapseAll(mock(ClickEvent.class));

        verify(presenter).collapseAll();
    }

    @Test
    public void testShowNoDataTypesFound() {

        noDataTypesFound.classList = mock(DOMTokenList.class);
        placeholder.classList = mock(DOMTokenList.class);
        listItems.classList = mock(DOMTokenList.class);

        view.showNoDataTypesFound();

        verify(noDataTypesFound.classList).remove(HIDDEN_CSS_CLASS);
        verify(placeholder.classList).add(HIDDEN_CSS_CLASS);
        verify(listItems.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowListItems() {

        noDataTypesFound.classList = mock(DOMTokenList.class);
        placeholder.classList = mock(DOMTokenList.class);
        listItems.classList = mock(DOMTokenList.class);

        view.showListItems();

        verify(noDataTypesFound.classList).add(HIDDEN_CSS_CLASS);
        verify(placeholder.classList).add(HIDDEN_CSS_CLASS);
        verify(listItems.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowPlaceHolder() {

        noDataTypesFound.classList = mock(DOMTokenList.class);
        placeholder.classList = mock(DOMTokenList.class);
        listItems.classList = mock(DOMTokenList.class);

        view.showPlaceHolder();

        verify(noDataTypesFound.classList).add(HIDDEN_CSS_CLASS);
        verify(placeholder.classList).remove(HIDDEN_CSS_CLASS);
        verify(listItems.classList).add(HIDDEN_CSS_CLASS);
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
        verify(view).showOrHideNoCustomItemsMessage();
    }

    @Test
    public void testCleanSubTypesByDataType() {

        final DataType dataType = mock(DataType.class);
        final String uuid = "uuid";

        doNothing().when(view).cleanSubTypes(anyString());
        when(dataType.getUUID()).thenReturn(uuid);

        view.cleanSubTypes(dataType);

        verify(view).cleanSubTypes(uuid);
    }

    @Test
    public void testCleanSubTypesByUUID() {

        final String parentUUID = "parentUUID";
        final String child1UUID = "child1UUID";
        final String child2UUID = "child2UUID";
        final Element parentElement = makeHTMLElement();
        final NodeList<Element> children = spy(new NodeList<>());
        final Element child1 = makeElement(child1UUID);
        final Element child2 = makeElement(child2UUID);
        final Element child3NoParent = makeElement("child3UUID");
        final Element child4Null = null;

        child2.parentNode = parentElement;
        child1.parentNode = parentElement;
        children.length = 4;
        doReturn(child1).when(children).getAt(0);
        doReturn(child2).when(children).getAt(1);
        doReturn(child3NoParent).when(children).getAt(2);
        doReturn(child4Null).when(children).getAt(3);

        mockDOMElementsByParentUUID(parentUUID, children);

        view.cleanSubTypes(parentUUID);

        verify(presenter).removeItem(child1UUID);
        verify(presenter).removeItem(child2UUID);
        verify(parentElement).removeChild(child1);
        verify(parentElement).removeChild(child2);
        verify(parentElement, never()).removeChild(child3NoParent);
        verify(parentElement, never()).removeChild(child4Null);
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

        when(this.listItems.querySelector("[data-row-uuid=\"" + dataTypeUUID + "\"]")).thenReturn(dataTypeRow);
        when(dataType.getUUID()).thenReturn(dataTypeUUID);
        when(listItem1.getElement()).thenReturn(listItemElement1);
        when(listItem2.getElement()).thenReturn(listItemElement2);

        doNothing().when(view).cleanSubTypes(anyString());
        doNothing().when(view).hideItemElementIfParentIsCollapsed(any(), any());
        doNothing().when(view).showArrowIconIfDataTypeHasChildren(any());

        mockStatic(ElementHelper.class);

        view.addSubItems(dataType, listItems);

        verify(view).hideItemElementIfParentIsCollapsed(listItemElement1, dataTypeRow);
        verify(view).hideItemElementIfParentIsCollapsed(listItemElement2, listItemElement1);
        verify(view).showArrowIconIfDataTypeHasChildren(dataType);

        verifyStatic();
        ElementHelper.insertAfter(listItemElement1, dataTypeRow);
        ElementHelper.insertAfter(listItemElement2, listItemElement1);
        verify(view).showOrHideNoCustomItemsMessage();
    }

    @Test
    public void testAddSubItem() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final HTMLElement element = mock(HTMLElement.class);

        when(listItem.getElement()).thenReturn(element);

        view.addSubItem(listItem);

        verify(listItems).appendChild(element);
        verify(view).showOrHideNoCustomItemsMessage();
    }

    @Test
    public void testOnAddClick() {

        final ClickEvent event = mock(ClickEvent.class);

        view.onAddClick(event);

        verify(scrollHelper).animatedScrollToBottom(listItems);
        verify(presenter).addDataType();
    }

    @Test
    public void testHideNoCustomItemsMessageWhenThereIsCustomItem() {

        final DataTypeListItem gridItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem gridItem2 = mock(DataTypeListItem.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);

        when(gridItem1.getElement()).thenReturn(element1);
        when(gridItem2.getElement()).thenReturn(element2);

        when(view.hasCustomDataType()).thenReturn(true);
        view.setupListItems(Arrays.asList(gridItem1, gridItem2));

        verify(listItems.classList).remove(HIDDEN_CSS_CLASS);
        verify(placeholder.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowNoCustomItemsMessageWhenThereIsNoCustomItem() {

        view.setupListItems(new ArrayList<>());

        verify(placeholder.classList).remove(HIDDEN_CSS_CLASS);
        verify(listItems.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideItemElementIfParentIsCollapsedWhenParentIsCollapsed() {

        final HTMLElement itemElement = mock(HTMLElement.class);
        final Element parent = mock(Element.class);
        final Element arrow = mock(Element.class);
        final DOMTokenList itemElementClassList = mock(DOMTokenList.class);
        final DOMTokenList parentClassList = mock(DOMTokenList.class);
        final DOMTokenList arrowClassList = mock(DOMTokenList.class);

        itemElement.classList = itemElementClassList;
        parent.classList = parentClassList;
        arrow.classList = arrowClassList;

        when(arrowClassList.contains(RIGHT_ARROW_CSS_CLASS)).thenReturn(true);
        when(parentClassList.contains(HIDDEN_CSS_CLASS)).thenReturn(false);
        when(parent.querySelector(ARROW_BUTTON_SELECTOR)).thenReturn(arrow);

        view.hideItemElementIfParentIsCollapsed(itemElement, parent);

        verify(itemElementClassList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideItemElementIfParentIsCollapsedWhenParentIsCollapsedByAnotherParent() {

        final HTMLElement itemElement = mock(HTMLElement.class);
        final Element parent = mock(Element.class);
        final Element arrow = mock(Element.class);
        final DOMTokenList itemElementClassList = mock(DOMTokenList.class);
        final DOMTokenList parentClassList = mock(DOMTokenList.class);
        final DOMTokenList arrowClassList = mock(DOMTokenList.class);

        itemElement.classList = itemElementClassList;
        parent.classList = parentClassList;
        arrow.classList = arrowClassList;

        when(arrowClassList.contains(RIGHT_ARROW_CSS_CLASS)).thenReturn(false);
        when(parentClassList.contains(HIDDEN_CSS_CLASS)).thenReturn(true);
        when(parent.querySelector(ARROW_BUTTON_SELECTOR)).thenReturn(arrow);

        view.hideItemElementIfParentIsCollapsed(itemElement, parent);

        verify(itemElementClassList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideItemElementIfParentIsCollapsedWhenParentIsNotCollapsed() {

        final HTMLElement itemElement = mock(HTMLElement.class);
        final Element parent = mock(Element.class);
        final Element arrow = mock(Element.class);
        final DOMTokenList itemElementClassList = mock(DOMTokenList.class);
        final DOMTokenList parentClassList = mock(DOMTokenList.class);
        final DOMTokenList arrowClassList = mock(DOMTokenList.class);

        itemElement.classList = itemElementClassList;
        parent.classList = parentClassList;
        arrow.classList = arrowClassList;

        when(arrowClassList.contains(RIGHT_ARROW_CSS_CLASS)).thenReturn(false);
        when(parentClassList.contains(HIDDEN_CSS_CLASS)).thenReturn(false);
        when(parent.querySelector(ARROW_BUTTON_SELECTOR)).thenReturn(arrow);

        view.hideItemElementIfParentIsCollapsed(itemElement, parent);

        verify(itemElementClassList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowArrowIconIfDataTypeHasChildrenWhenDataTypeHasChildren() {

        final DataType dataType = mock(DataType.class);
        final NodeList<Element> elementNodeList = new NodeList<>();
        final Element dataTypeRow = mock(Element.class);
        final Element dataTypeRowArrow = mock(Element.class);
        final DOMTokenList arrowClassList = mock(DOMTokenList.class);
        final String uuid = "uuid";

        dataTypeRowArrow.classList = arrowClassList;
        elementNodeList.length = 1d;

        when(dataType.getUUID()).thenReturn(uuid);
        when(listItems.querySelectorAll("[" + PARENT_UUID_ATTR + "=\"uuid\"]")).thenReturn(elementNodeList);
        when(listItems.querySelector("[" + UUID_ATTR + "=\"uuid\"]")).thenReturn(dataTypeRow);
        when(dataTypeRow.querySelector(ARROW_BUTTON_SELECTOR)).thenReturn(dataTypeRowArrow);

        view.showArrowIconIfDataTypeHasChildren(dataType);

        verify(arrowClassList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowArrowIconIfDataTypeHasChildrenWhenDataTypeDoesNotHaveChildren() {

        final DataType dataType = mock(DataType.class);
        final NodeList<Element> elementNodeList = new NodeList<>();
        final Element dataTypeRow = mock(Element.class);
        final Element dataTypeRowArrow = mock(Element.class);
        final DOMTokenList arrowClassList = mock(DOMTokenList.class);
        final String uuid = "uuid";

        dataTypeRowArrow.classList = arrowClassList;
        elementNodeList.length = 0d;

        when(dataType.getUUID()).thenReturn(uuid);
        when(listItems.querySelectorAll("[" + PARENT_UUID_ATTR + "=\"uuid\"]")).thenReturn(elementNodeList);
        when(listItems.querySelector("[" + UUID_ATTR + "=\"uuid\"]")).thenReturn(dataTypeRow);
        when(dataTypeRow.querySelector(ARROW_BUTTON_SELECTOR)).thenReturn(dataTypeRowArrow);

        view.showArrowIconIfDataTypeHasChildren(dataType);

        verify(arrowClassList).add(HIDDEN_CSS_CLASS);
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
        assertFalse(viewLess.hidden);
        assertTrue(viewMore.hidden);
    }

    @Test
    public void testCollapseDescription() {
        view.collapseDescription();

        assertFalse(collapsedDescription.hidden);
        assertTrue(expandedDescription.hidden);
        assertTrue(viewLess.hidden);
        assertFalse(viewMore.hidden);
    }

    @Test
    public void testRemoveItem() {

        final String uuid = "uuid";
        final DataType dataType = mock(DataType.class);
        final Node parentNode = mock(Node.class);
        final Element dataTypeElement = makeElement(uuid);

        when(dataType.getUUID()).thenReturn(uuid);
        doReturn(dataTypeElement).when(view).getDataTypeRow(dataType);
        doNothing().when(view).cleanSubTypes(anyString());
        dataTypeElement.parentNode = parentNode;

        view.removeItem(dataType);

        verify(presenter).removeItem(uuid);
        verify(view).cleanSubTypes(uuid);
        verify(parentNode).removeChild(dataTypeElement);
        verify(view).showOrHideNoCustomItemsMessage();
    }

    @Test
    public void testInsertBelow() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final DataType reference = mock(DataType.class);
        final HTMLElement listItemElement = mock(HTMLElement.class);
        final Element lastElement = mock(Element.class);

        when(listItem.getElement()).thenReturn(listItemElement);
        doReturn(lastElement).when(view).getLastSubDataTypeElement(reference);

        mockStatic(ElementHelper.class);

        view.insertBelow(listItem, reference);

        verifyStatic();
        ElementHelper.insertAfter(listItemElement, lastElement);
    }

    @Test
    public void testInsertAbove() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final DataType reference = mock(DataType.class);
        final HTMLElement listItemElement = mock(HTMLElement.class);
        final Element element = mock(Element.class);

        when(listItem.getElement()).thenReturn(listItemElement);
        doReturn(element).when(view).getDataTypeRow(reference);

        mockStatic(ElementHelper.class);

        view.insertAbove(listItem, reference);

        verifyStatic();
        ElementHelper.insertBefore(listItemElement, element);
    }

    @Test
    public void testGetLastSubDataTypeElementWithElement() {

        final String parentUUID = "parentUUID";
        final Element parentElement = makeElement(parentUUID);
        final NodeList<Element> children = spy(new NodeList<>());
        final Element child1 = makeElement("uuid1");
        final Element child2 = makeElement("uuid2");

        child1.parentNode = parentElement;
        child2.parentNode = parentElement;

        doReturn(child1).when(children).getAt(0);
        doReturn(child2).when(children).getAt(1);
        children.length = 2;

        mockDOMElementsByParentUUID(parentUUID, children);

        final Element lastElement = view.getLastSubDataTypeElement(parentElement);

        assertEquals(child2, lastElement);
    }

    @Test
    public void testGetLastSubDataTypeElementWithDataType() {

        final DataType dataType = mock(DataType.class);
        final Element element = mock(Element.class);
        final Element expectedElement = mock(Element.class);

        doReturn(element).when(view).getDataTypeRow(dataType);
        doReturn(expectedElement).when(view).getLastSubDataTypeElement(element);

        final Element actualElement = view.getLastSubDataTypeElement(dataType);

        assertEquals(expectedElement, actualElement);
    }

    private HTMLElement makeHTMLElement() {
        final HTMLElement element = mock(HTMLElement.class);
        element.parentNode = mock(Node.class);
        return element;
    }

    public Element makeElement(final String uuid) {

        final Element element = mock(Element.class);

        mockDOMElementsByParentUUID(uuid, new NodeList<>());
        when(element.getAttribute(UUID_ATTR)).thenReturn(uuid);

        return element;
    }

    private void mockDOMElementsByParentUUID(final String parentUUID,
                                             final NodeList<Element> rowElements) {
        when(listItems.querySelectorAll("[" + PARENT_UUID_ATTR + "=\"" + parentUUID + "\"]")).thenReturn(rowElements);
    }
}
