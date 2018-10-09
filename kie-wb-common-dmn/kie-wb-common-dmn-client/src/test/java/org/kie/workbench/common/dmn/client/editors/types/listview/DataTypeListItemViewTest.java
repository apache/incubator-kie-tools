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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.CSSProperties.MarginLeftUnionType;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.NodeList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.PARENT_UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.DOWN_ARROW_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.FOCUSED_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.RIGHT_ARROW_CSS_CLASS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeListItemViewTest {

    @Mock
    private HTMLDivElement row;

    @Mock
    private HTMLElement level;

    @Mock
    private HTMLElement arrow;

    @Mock
    private HTMLElement nameText;

    @Mock
    private HTMLInputElement nameInput;

    @Mock
    private HTMLElement type;

    @Mock
    private HTMLButtonElement editButton;

    @Mock
    private HTMLButtonElement saveButton;

    @Mock
    private HTMLButtonElement closeButton;

    @Mock
    private DataTypeListItem presenter;

    @Captor
    private ArgumentCaptor<Integer> integerCaptor;

    @Mock
    private HTMLElement dataTypeListElement;

    @Mock
    private HTMLAnchorElement removeButton;

    @Mock
    private HTMLDivElement kebabMenu;

    private DataTypeListItemView view;

    @Before
    public void setup() {
        view = spy(new DataTypeListItemView(row, level, arrow, nameText, nameInput, type, editButton, saveButton, closeButton, removeButton, kebabMenu));
        view.init(presenter);

        doReturn(dataTypeListElement).when(view).dataTypeListElement();
    }

    @Test
    public void testSetDataType() {

        final DataType dataType = mock(DataType.class);

        doNothing().when(view).setupRowMetadata(dataType);
        doNothing().when(view).setupArrow(dataType);
        doNothing().when(view).setupIndentationLevel();
        doNothing().when(view).setupNameComponent(dataType);
        doNothing().when(view).setupActionButtons();

        view.setDataType(dataType);

        verify(view).setupRowMetadata(dataType);
        verify(view).setupArrow(dataType);
        verify(view).setupIndentationLevel();
        verify(view).setupNameComponent(dataType);
        verify(view).setupActionButtons();
    }

    @Test
    public void testSetupRowMetadata() {

        final DataType dataType = mock(DataType.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        when(dataType.getUUID()).thenReturn("1234");
        when(dataType.getParentUUID()).thenReturn("4567");
        row.classList = classList;

        view.setupRowMetadata(dataType);

        verify(row).setAttribute(UUID_ATTR, "1234");
        verify(row).setAttribute(PARENT_UUID_ATTR, "4567");
    }

    @Test
    public void testToggleArrowWhenTrue() {

        final DOMTokenList classList = mock(DOMTokenList.class);

        arrow.classList = classList;

        view.toggleArrow(true);

        verify(classList).remove("hidden");
    }

    @Test
    public void testToggleArrowWhenFalse() {

        final DOMTokenList classList = mock(DOMTokenList.class);

        arrow.classList = classList;

        view.toggleArrow(false);

        verify(classList).add("hidden");
    }

    @Test
    public void testSetupArrowWhenDataTypeHasSubTypes() {

        final DataType dataType = mock(DataType.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        when(dataType.hasSubDataTypes()).thenReturn(true);
        arrow.classList = classList;

        view.setupArrow(dataType);

        verify(view).toggleArrow(true);
    }

    @Test
    public void testSetupArrowWhenDataTypeDoesNotHaveSubTypes() {

        final DataType dataType = mock(DataType.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        when(dataType.hasSubDataTypes()).thenReturn(false);
        arrow.classList = classList;

        view.setupArrow(dataType);

        verify(view).toggleArrow(false);
    }

    @Test
    public void testSetupIndentationLevel() {

        final CSSStyleDeclaration style = mock(CSSStyleDeclaration.class);
        final MarginLeftUnionType margin = mock(MarginLeftUnionType.class);

        when(presenter.getLevel()).thenReturn(2);
        doReturn(margin).when(view).margin(anyInt());
        level.style = style;

        view.setupIndentationLevel();

        verify(view).margin(integerCaptor.capture());
        assertEquals(70, (int) integerCaptor.getValue());
        assertEquals(margin, level.style.marginLeft);
    }

    @Test
    public void testSetupDataTypeValues() {

        final DataType dataType = mock(DataType.class);
        final DOMTokenList classList = mock(DOMTokenList.class);
        final String name = "name";

        when(dataType.getName()).thenReturn(name);
        nameInput.classList = classList;

        view.setupNameComponent(dataType);

        verify(classList).add(HIDDEN_CSS_CLASS);
        assertEquals(name, nameText.textContent);
        assertEquals(name, nameInput.value);
    }

    @Test
    public void testOnArrowClickEvent() {
        view.onArrowClickEvent(mock(ClickEvent.class));

        verify(presenter).expandOrCollapseSubTypes();
    }

    @Test
    public void testOnRemoveButton() {
        view.onRemoveButton(mock(ClickEvent.class));

        verify(presenter).remove();
    }

    @Test
    public void testOnEditClick() {
        view.onEditClick(mock(ClickEvent.class));

        verify(presenter).enableEditMode();
    }

    @Test
    public void testOnSaveClick() {
        view.onSaveClick(mock(ClickEvent.class));

        verify(presenter).saveAndCloseEditMode();
    }

    @Test
    public void testOnCloseClick() {
        view.onCloseClick(mock(ClickEvent.class));

        verify(presenter).disableEditMode();
    }

    @Test
    public void testSetupSelectComponent() {

        final DataTypeSelect select = mock(DataTypeSelect.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);

        when(select.getElement()).thenReturn(htmlElement);
        type.innerHTML = "previous content";

        view.setupSelectComponent(select);

        assertFalse(type.innerHTML.contains("previous content"));
        verify(type).appendChild(htmlElement);
    }

    @Test
    public void testSetupActionButtons() {

        doNothing().when(view).showEditButton();

        view.setupActionButtons();

        verify(view).showEditButton();
    }

    @Test
    public void testShowEditButton() {

        final DOMTokenList editClassList = mock(DOMTokenList.class);
        final DOMTokenList saveClassList = mock(DOMTokenList.class);
        final DOMTokenList closeClassList = mock(DOMTokenList.class);

        editButton.classList = editClassList;
        saveButton.classList = saveClassList;
        closeButton.classList = closeClassList;

        view.showEditButton();

        verify(editClassList).remove(HIDDEN_CSS_CLASS);
        verify(saveClassList).add(HIDDEN_CSS_CLASS);
        verify(closeClassList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowSaveButton() {

        final DOMTokenList editClassList = mock(DOMTokenList.class);
        final DOMTokenList saveClassList = mock(DOMTokenList.class);
        final DOMTokenList closeClassList = mock(DOMTokenList.class);

        editButton.classList = editClassList;
        saveButton.classList = saveClassList;
        closeButton.classList = closeClassList;

        view.showSaveButton();

        verify(editClassList).add(HIDDEN_CSS_CLASS);
        verify(saveClassList).remove(HIDDEN_CSS_CLASS);
        verify(closeClassList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testExpand() {

        // Mock arrow element
        final HTMLElement arrow = mock(HTMLElement.class);
        final DOMTokenList arrowClassList = mock(DOMTokenList.class);

        doReturn(arrow).when(view).getArrow();
        doReturn(false).when(view).isCollapsed(any());
        arrow.classList = arrowClassList;

        // Mock Parent data type with 2 dependent elements
        final DataType parentDataType = mock(DataType.class);
        final String parentDataTypeUUID = "parentDataTypeUUID";
        final Element parentElement = mock(Element.class);
        final Element child1 = makeChildElement("child1UUID");
        final Element child2 = makeChildElement("child2UUID");
        final NodeList<Element> children = spy(new NodeList<>());

        when(parentDataType.getUUID()).thenReturn(parentDataTypeUUID);
        when(parentElement.getAttribute("data-row-uuid")).thenReturn(parentDataTypeUUID);
        doReturn(parentDataType).when(view).getDataType();
        doReturn(child1).when(children).getAt(0);
        doReturn(child2).when(children).getAt(1);
        mockDOMElementByUUID(parentDataTypeUUID, parentElement);
        mockDOMElementsByParentUUID(parentDataTypeUUID, children);
        children.length = 2;

        view.expand();

        verify(arrowClassList).add(DOWN_ARROW_CSS_CLASS);
        verify(arrowClassList).remove(RIGHT_ARROW_CSS_CLASS);
        verify(child1.classList).remove(HIDDEN_CSS_CLASS);
        verify(child2.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testCollapse() {

        // Mock arrow element
        final HTMLElement arrow = mock(HTMLElement.class);
        final DOMTokenList arrowClassList = mock(DOMTokenList.class);

        doReturn(arrow).when(view).getArrow();
        arrow.classList = arrowClassList;

        // Mock Parent data type with 2 dependent elements
        final DataType parentDataType = mock(DataType.class);
        final String parentDataTypeUUID = "parentDataTypeUUID";
        final Element parentElement = mock(Element.class);
        final Element child1 = makeChildElement("child1UUID");
        final Element child2 = makeChildElement("child2UUID");
        final NodeList<Element> children = spy(new NodeList<>());

        when(parentDataType.getUUID()).thenReturn(parentDataTypeUUID);
        when(parentElement.getAttribute("data-row-uuid")).thenReturn(parentDataTypeUUID);
        doReturn(parentDataType).when(view).getDataType();
        doReturn(child1).when(children).getAt(0);
        doReturn(child2).when(children).getAt(1);
        mockDOMElementByUUID(parentDataTypeUUID, parentElement);
        mockDOMElementsByParentUUID(parentDataTypeUUID, children);
        children.length = 2;

        view.collapse();

        verify(arrowClassList).remove(DOWN_ARROW_CSS_CLASS);
        verify(arrowClassList).add(RIGHT_ARROW_CSS_CLASS);
        verify(child1.classList).add(HIDDEN_CSS_CLASS);
        verify(child2.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testIsCollapsedWhenItIsRightArrow() {

        final HTMLElement arrow = mock(HTMLElement.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        arrow.classList = classList;
        when(classList.contains(eq(RIGHT_ARROW_CSS_CLASS))).thenReturn(true);
        doReturn(arrow).when(view).getArrow();

        assertTrue(view.isCollapsed());
    }

    @Test
    public void testIsCollapsedArrowWhenItIsNotRightArrow() {

        final HTMLElement arrow = mock(HTMLElement.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        arrow.classList = classList;
        when(classList.contains(RIGHT_ARROW_CSS_CLASS)).thenReturn(false);
        doReturn(arrow).when(view).getArrow();

        assertFalse(view.isCollapsed());
    }

    @Test
    public void testIsCollapsedWhenArrowIsARightArrow() {

        final DOMTokenList classList = mock(DOMTokenList.class);
        final HTMLElement arrow = mock(HTMLElement.class);
        arrow.classList = classList;
        when(classList.contains(RIGHT_ARROW_CSS_CLASS)).thenReturn(true);

        assertTrue(view.isCollapsed(arrow));
    }

    @Test
    public void testIsCollapsedWhenArrowIsNotARightArrow() {

        final DOMTokenList classList = mock(DOMTokenList.class);
        final HTMLElement arrow = mock(HTMLElement.class);
        arrow.classList = classList;
        when(classList.contains(RIGHT_ARROW_CSS_CLASS)).thenReturn(false);

        assertFalse(view.isCollapsed(arrow));
    }

    @Test
    public void testEnableFocusMode() {

        final DataType parentDataType = mock(DataType.class);
        final String parentDataTypeUUID = "parentDataTypeUUID";
        final Element parentElement = mock(Element.class);
        final Element child1 = makeChildElement("child1UUID");
        final Element child2 = makeChildElement("child2UUID");
        final NodeList<Element> children = spy(new NodeList<>());

        when(parentDataType.getUUID()).thenReturn(parentDataTypeUUID);
        when(parentElement.getAttribute("data-row-uuid")).thenReturn(parentDataTypeUUID);
        doReturn(parentDataType).when(view).getDataType();
        doReturn(child1).when(children).getAt(0);
        doReturn(child2).when(children).getAt(1);
        mockDOMElementByUUID(parentDataTypeUUID, parentElement);
        mockDOMElementsByParentUUID(parentDataTypeUUID, children);
        children.length = 2;
        parentElement.classList = mock(DOMTokenList.class);

        view.enableFocusMode();

        verify(parentElement.classList).add(FOCUSED_CSS_CLASS);
        verify(child1.classList).add(FOCUSED_CSS_CLASS);
        verify(child2.classList).add(FOCUSED_CSS_CLASS);
        verify(nameInput).select();
    }

    @Test
    public void testDisableFocusMode() {

        final DataType parentDataType = mock(DataType.class);
        final String parentDataTypeUUID = "parentDataTypeUUID";
        final Element parentElement = mock(Element.class);
        final Element child1 = makeChildElement("child1UUID");
        final Element child2 = makeChildElement("child2UUID");
        final NodeList<Element> children = spy(new NodeList<>());

        when(parentDataType.getUUID()).thenReturn(parentDataTypeUUID);
        when(parentElement.getAttribute("data-row-uuid")).thenReturn(parentDataTypeUUID);
        doReturn(parentDataType).when(view).getDataType();
        doReturn(child1).when(children).getAt(0);
        doReturn(child2).when(children).getAt(1);
        mockDOMElementByUUID(parentDataTypeUUID, parentElement);
        mockDOMElementsByParentUUID(parentDataTypeUUID, children);
        children.length = 2;
        parentElement.classList = mock(DOMTokenList.class);

        view.disableFocusMode();

        verify(parentElement.classList).remove(FOCUSED_CSS_CLASS);
        verify(child1.classList).remove(FOCUSED_CSS_CLASS);
        verify(child2.classList).remove(FOCUSED_CSS_CLASS);
    }

    @Test
    public void testGetName() {

        final String expectedName = "name";
        nameInput.value = expectedName;

        final String actualName = view.getName();

        assertEquals(expectedName, actualName);
    }

    @Test
    public void testShowDataTypeNameInput() {

        nameText.classList = mock(DOMTokenList.class);
        nameInput.classList = mock(DOMTokenList.class);

        view.showDataTypeNameInput();

        verify(nameText.classList).add(HIDDEN_CSS_CLASS);
        verify(nameInput.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideDataTypeNameInput() {

        final String expectedName = "name";

        nameInput.value = expectedName;

        nameText.classList = mock(DOMTokenList.class);
        nameInput.classList = mock(DOMTokenList.class);

        view.hideDataTypeNameInput();

        assertEquals(expectedName, nameText.textContent);
        verify(nameText.classList).remove(HIDDEN_CSS_CLASS);
        verify(nameInput.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideDataTypeNameInputWhenNameIsBlank() {

        final String expectedName = "-";

        nameInput.value = "";

        nameText.classList = mock(DOMTokenList.class);
        nameInput.classList = mock(DOMTokenList.class);

        view.hideDataTypeNameInput();

        assertEquals(expectedName, nameText.textContent);
        verify(nameText.classList).remove(HIDDEN_CSS_CLASS);
        verify(nameInput.classList).add(HIDDEN_CSS_CLASS);
    }

    public Element makeChildElement(final String uuid) {

        final Element element = mock(Element.class);

        element.classList = mock(DOMTokenList.class);

        // This element does not have any child.
        mockDOMElementsByParentUUID(uuid, new NodeList<>());
        when(element.getAttribute("data-row-uuid")).thenReturn(uuid);

        return element;
    }

    private void mockDOMElementByUUID(final String uuid,
                                      final Element element) {
        when(dataTypeListElement.querySelector("[data-row-uuid=\"" + uuid + "\"]")).thenReturn(element);
    }

    private void mockDOMElementsByParentUUID(final String parentUUID,
                                             final NodeList<Element> elements) {
        when(dataTypeListElement.querySelectorAll("[data-parent-row-uuid=\"" + parentUUID + "\"]")).thenReturn(elements);
    }
}
