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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
import elemental2.dom.NodeList;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.ScrollHelper;
import org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModal;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage.Type.SUCCESS;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.ARROW_BUTTON_SELECTOR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.PARENT_UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.RIGHT_ARROW_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeSuccessfullyImportedMessage_StrongMessage;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DataTypeListViewTest {

    @Mock
    private HTMLDivElement listItems;

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
    private HTMLDivElement readOnlyMessage;

    @Mock
    private HTMLButtonElement readOnlyMessageCloseButton;

    @Mock
    private ScrollHelper scrollHelper;

    @Mock
    private DNDListComponent dndListComponent;

    @Mock
    private HTMLElement dndListComponentElement;

    @Mock
    private DataTypeList presenter;

    @Mock
    private HTMLButtonElement importDataObjectButton;

    @Mock
    private ImportDataObjectModal importDataObjectModal;

    @Mock
    private EventSourceMock<FlashMessage> flashMessageEvent;

    @Mock
    private TranslationService translationService;

    @Mock
    private HTMLButtonElement addButtonPlaceholder;

    @Mock
    private HTMLDivElement dataTypeButton;

    @Captor
    private ArgumentCaptor<FlashMessage> flashMessageCaptor;

    private DataTypeListView view;

    @Before
    public void setup() {

        when(presenter.getSearchBar()).thenReturn(searchBar);
        when(searchBar.getElement()).thenReturn(searchBarElement);
        when(presenter.getDNDListComponent()).thenReturn(dndListComponent);
        when(dndListComponent.getElement()).thenReturn(dndListComponentElement);

        placeholder.classList = mock(DOMTokenList.class);
        noDataTypesFound.classList = mock(DOMTokenList.class);
        listItems.classList = mock(DOMTokenList.class);
        dataTypeButton.classList = mock(DOMTokenList.class);
        listItems.childNodes = new NodeList<>();

        view = spy(new DataTypeListView(listItems, addButton, addButtonPlaceholder, dataTypeButton, placeholder, searchBarContainer, expandAll, collapseAll, noDataTypesFound, readOnlyMessage, readOnlyMessageCloseButton, scrollHelper, importDataObjectButton, importDataObjectModal, flashMessageEvent, translationService));
        view.init(presenter);

        doReturn(element).when(view).getElement();
    }

    @Test
    public void testInit() {
        // "view.init(..)" called in the setup.
        verify(searchBarContainer).appendChild(searchBarElement);
        verify(listItems).appendChild(dndListComponentElement);
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

        view.showNoDataTypesFound();

        verify(noDataTypesFound.classList).remove(HIDDEN_CSS_CLASS);
        verify(placeholder.classList).add(HIDDEN_CSS_CLASS);
        verify(listItems.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowListItems() {

        view.showListItems();

        verify(noDataTypesFound.classList).add(HIDDEN_CSS_CLASS);
        verify(placeholder.classList).add(HIDDEN_CSS_CLASS);
        verify(listItems.classList).remove(HIDDEN_CSS_CLASS);
        verify(dataTypeButton.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowPlaceHolder() {

        view.showPlaceHolder();

        verify(noDataTypesFound.classList).add(HIDDEN_CSS_CLASS);
        verify(placeholder.classList).remove(HIDDEN_CSS_CLASS);
        verify(listItems.classList).add(HIDDEN_CSS_CLASS);
        verify(dataTypeButton.classList).add(HIDDEN_CSS_CLASS);
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
        when(listItem1.getDragAndDropElement()).thenReturn(listItemElement1);
        when(listItem2.getDragAndDropElement()).thenReturn(listItemElement2);

        doNothing().when(view).cleanSubTypes(anyString());
        doNothing().when(view).hideItemElementIfParentIsCollapsed(any(), any());
        doNothing().when(view).showArrowIconIfDataTypeHasChildren(any());

        view.addSubItems(dataType, listItems);

        verify(view).hideItemElementIfParentIsCollapsed(listItemElement1, dataTypeRow);
        verify(view).hideItemElementIfParentIsCollapsed(listItemElement2, listItemElement1);
        verify(view).showArrowIconIfDataTypeHasChildren(dataType);

        verify(dataTypeRow.parentNode).insertBefore(listItemElement1, dataTypeRow.nextSibling);
        verify(listItemElement1.parentNode).insertBefore(listItemElement2, listItemElement1.nextSibling);
        verify(view).showOrHideNoCustomItemsMessage();
    }

    @Test
    public void testOnAddClick() {

        final ClickEvent event = mock(ClickEvent.class);

        view.onAddButtonClick(event);

        verify(scrollHelper).animatedScrollToBottom(listItems);
        verify(presenter).addDataType();
    }

    @Test
    public void testOnReadOnlyMessageCloseButtonClick() {

        final ClickEvent event = mock(ClickEvent.class);
        readOnlyMessage.classList = mock(DOMTokenList.class);

        view.onReadOnlyMessageCloseButtonClick(event);

        verify(readOnlyMessage.classList).add(HIDDEN_CSS_CLASS);
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
        verify(dndListComponent).setInitialHiddenPositionY(itemElement);
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
        elementNodeList.length = 1;

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
        elementNodeList.length = 0;

        when(dataType.getUUID()).thenReturn(uuid);
        when(listItems.querySelectorAll("[" + PARENT_UUID_ATTR + "=\"uuid\"]")).thenReturn(elementNodeList);
        when(listItems.querySelector("[" + UUID_ATTR + "=\"uuid\"]")).thenReturn(dataTypeRow);
        when(dataTypeRow.querySelector(ARROW_BUTTON_SELECTOR)).thenReturn(dataTypeRowArrow);

        view.showArrowIconIfDataTypeHasChildren(dataType);

        verify(arrowClassList).add(HIDDEN_CSS_CLASS);
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
        final Element lastElement = spy(new Element());
        final Element parentElement = mock(Element.class);
        final Element siblingElement = mock(Element.class);
        lastElement.parentNode = parentElement;
        lastElement.nextSibling = siblingElement;

        when(listItem.getDragAndDropElement()).thenReturn(listItemElement);
        doReturn(lastElement).when(view).getLastSubDataTypeElement(reference);

        view.insertBelow(listItem, reference);

        verify(parentElement).insertBefore(listItemElement, siblingElement);
        verify(view).setNewElementYPosition(lastElement, listItemElement);
    }

    @Test
    public void testInsertAbove() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final DataType reference = mock(DataType.class);

        final HTMLElement listItemElement = mock(HTMLElement.class);
        final Element element = spy(new Element());
        final Element parentElement = mock(Element.class);
        element.parentNode = parentElement;

        when(listItem.getDragAndDropElement()).thenReturn(listItemElement);
        doReturn(element).when(view).getDataTypeRow(reference);

        view.insertAbove(listItem, reference);

        verify(parentElement).insertBefore(listItemElement, element);
        verify(view).setNewElementYPosition(element, listItemElement);
    }

    @Test
    public void testGetLastSubDataTypeElementWithElement() {

        final String parentUUID = "parentUUID";
        final Element parentElement = makeElement(parentUUID);
        final NodeList<Element> children = spy(new NodeList<>());
        final Element child1 = makeElement("uuid1");
        final Element child2 = makeElement("uuid2");
        final Element child3 = makeElement("uuid3");

        child1.parentNode = parentElement;
        child2.parentNode = parentElement;

        when(dndListComponent.getPositionY(child1)).thenReturn(0);
        when(dndListComponent.getPositionY(child2)).thenReturn(0);
        when(dndListComponent.getPositionY(child3)).thenReturn(-1);
        doReturn(child1).when(children).getAt(0);
        doReturn(child2).when(children).getAt(1);
        doReturn(child3).when(children).getAt(2);
        children.length = 3;

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

    @Test
    public void testShowReadOnlyMessageWhenShowIsTrue() {

        readOnlyMessage.classList = mock(DOMTokenList.class);

        view.showReadOnlyMessage(true);

        verify(readOnlyMessage.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowReadOnlyMessageWhenShowIsFalse() {

        readOnlyMessage.classList = mock(DOMTokenList.class);

        view.showReadOnlyMessage(false);

        verify(readOnlyMessage.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testImportDataObjectsWhenListIsEmpty() {

        final List<DataObject> imported = mock(List.class);

        when(imported.isEmpty()).thenReturn(true);
        view.importDataObjects(imported);

        verify(presenter).importDataObjects(imported);
    }

    @Test
    public void testImportDataObjectsWhenListIsNotEmpty() {

        final List<DataObject> imported = mock(List.class);

        doNothing().when(view).fireSuccessfullyImportedData();
        when(imported.isEmpty()).thenReturn(false);
        view.importDataObjects(imported);

        verify(presenter).importDataObjects(imported);
        verify(view).fireSuccessfullyImportedData();
    }

    @Test
    public void testFireSuccessfullyImportedData() {

        final String translated = "translated";
        when(translationService.getTranslation(DataTypeSuccessfullyImportedMessage_StrongMessage)).thenReturn(translated);

        view.fireSuccessfullyImportedData();

        verify(flashMessageEvent).fire(flashMessageCaptor.capture());

        final FlashMessage flashMessage = flashMessageCaptor.getValue();

        assertEquals(SUCCESS, flashMessage.getType());
        assertEquals(translated, flashMessage.getStrongMessage());
        assertEquals("", flashMessage.getRegularMessage());
    }

    @Test
    public void testSetNewElementYPosition() {

        final HTMLElement elementReference = mock(HTMLElement.class);
        final HTMLElement newElement = mock(HTMLElement.class);

        when(dndListComponent.getPositionY(elementReference)).thenReturn(1);

        view.setNewElementYPosition(elementReference, newElement);

        verify(dndListComponent).setPositionY(newElement, 1);
    }

    @Test
    public void testShowImportDataObjectButton() {
        final DOMTokenList classList = mock(DOMTokenList.class);

        importDataObjectButton.classList = classList;

        view.showImportDataObjectButton();

        verify(classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideImportDataObjectButton() {
        final DOMTokenList classList = mock(DOMTokenList.class);

        importDataObjectButton.classList = classList;

        view.hideImportDataObjectButton();

        verify(classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testOnImportDataObjectClick() {

        final ClickEvent event = mock(ClickEvent.class);
        final List<String> list = mock(List.class);

        when(presenter.getExistingDataTypesNames()).thenReturn(list);

        view.onImportDataObjectClick(event);

        verify(importDataObjectModal).show(list);
    }

    private HTMLElement makeHTMLElement() {
        final HTMLElement element = mock(HTMLElement.class);
        element.parentNode = mock(Node.class);
        element.nextSibling = mock(Node.class);
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
