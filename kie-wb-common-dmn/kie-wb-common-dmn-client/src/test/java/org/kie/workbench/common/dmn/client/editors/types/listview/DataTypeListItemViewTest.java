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
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.NodeList;
import elemental2.dom.Text;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponent;
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
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_Constraints;
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

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeListItemViewTest {

    @Mock
    private HTMLDivElement row;

    @Mock
    private HTMLElement arrow;

    @Mock
    private HTMLElement nameText;

    @Mock
    private HTMLElement constraintText;

    @Mock
    private HTMLInputElement nameInput;

    @Mock
    private HTMLElement type;

    @Mock
    private HTMLDivElement constraint;

    @Mock
    private HTMLDivElement collectionContainer;

    @Mock
    private HTMLDivElement collectionYes;

    @Mock
    private HTMLDivElement constraintContainer;

    @Mock
    private HTMLButtonElement editButton;

    @Mock
    private HTMLButtonElement saveButton;

    @Mock
    private HTMLButtonElement closeButton;

    @Mock
    private DataTypeListItem presenter;

    @Mock
    private HTMLElement dataTypeListElement;

    @Mock
    private HTMLAnchorElement removeButton;

    @Mock
    private HTMLAnchorElement insertFieldAbove;

    @Mock
    private HTMLAnchorElement insertFieldBelow;

    @Mock
    private HTMLAnchorElement insertNestedField;

    @Mock
    private HTMLDivElement kebabMenu;

    @Mock
    private TranslationService translationService;

    private DataTypeListItemView view;

    @Before
    public void setup() {
        view = spy(new DataTypeListItemView(row, arrow, nameText, constraintText, nameInput, type, collectionContainer, collectionYes, constraint, constraintContainer, editButton, saveButton, closeButton, removeButton, insertFieldAbove, insertFieldBelow, insertNestedField, kebabMenu, translationService));
        view.init(presenter);

        doReturn(dataTypeListElement).when(view).dataTypeListElement();
    }

    @Test
    public void testSetDataType() {

        final DataType dataType = mock(DataType.class);

        doNothing().when(view).setupRowMetadata(dataType);
        doNothing().when(view).setupArrow(dataType);
        doNothing().when(view).setupIndentationLevel();
        doNothing().when(view).setupReadOnly(dataType);
        doNothing().when(view).setupActionButtons();

        view.setDataType(dataType);

        verify(view).setupRowMetadata(dataType);
        verify(view).setupArrow(dataType);
        verify(view).setupIndentationLevel();
        verify(view).setupReadOnly(dataType);
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
        verify(view).setupRowCSSClass(dataType);
    }

    @Test
    public void testSetupRowCSSClassWhenDataTypeHasSubDataTypes() {

        final DataType dataType = mock(DataType.class);

        row.classList = mock(DOMTokenList.class);
        when(dataType.hasSubDataTypes()).thenReturn(true);

        view.setupRowCSSClass(dataType);

        verify(row.classList).add("has-sub-data-types");
    }

    @Test
    public void testSetupRowCSSClassWhenDataTypeDoesNotHaveSubDataTypes() {

        final DataType dataType = mock(DataType.class);

        row.classList = mock(DOMTokenList.class);
        when(dataType.hasSubDataTypes()).thenReturn(false);

        view.setupRowCSSClass(dataType);

        verify(row.classList).remove("has-sub-data-types");
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

        final int indentationLevel = 3;
        final NodeList<Element> levelElements = spy(new NodeList<>());
        final Element element0 = mock(Element.class);
        final Element element1 = mock(Element.class);

        levelElements.length = 2;
        doReturn(element0).when(levelElements).getAt(0);
        doReturn(element1).when(levelElements).getAt(1);

        when(presenter.getLevel()).thenReturn(indentationLevel);
        when(row.querySelectorAll(".nesting-level")).thenReturn(levelElements);

        view.setupIndentationLevel();

        verify(element0).setAttribute("style", "margin-left: 105px");
        verify(element1).setAttribute("style", "margin-left: 105px");
    }

    @Test
    public void testSetupReadOnly() {

        final DataType dataType = mock(DataType.class);
        final DOMTokenList classList = mock(DOMTokenList.class);
        final String name = "name";
        final String constraint = "constraint";

        doNothing().when(view).setName(name);
        doNothing().when(view).setConstraint(constraint);
        when(dataType.getName()).thenReturn(name);
        when(dataType.getConstraint()).thenReturn(constraint);
        nameInput.classList = classList;

        view.setupReadOnly(dataType);

        verify(classList).add(HIDDEN_CSS_CLASS);
        verify(view).setName(name);
        verify(view).setConstraint(constraint);
    }

    @Test
    public void testSetName() {

        final String name = "name";

        view.setName(name);

        assertEquals(name, nameText.textContent);
        assertEquals(name, nameInput.value);
    }

    @Test
    public void testSetConstraintWhenConstraintIsNull() {

        final String constraint = null;
        constraintText.classList = mock(DOMTokenList.class);

        view.setConstraint(constraint);

        assertEquals("", constraintText.textContent);
        verify(constraintText.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testSetConstraintWhenConstraintIsBlank() {

        final String constraint = "";
        constraintText.classList = mock(DOMTokenList.class);

        view.setConstraint(constraint);

        assertEquals("", constraintText.textContent);
        verify(constraintText.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testSetConstraintWhenConstraintIsPresent() {

        final String constraint = "(1..20)";
        constraintText.classList = mock(DOMTokenList.class);
        when(translationService.format(DataTypeListItemView_Constraints, constraint)).thenReturn("Constraints: " + constraint);

        view.setConstraint(constraint);

        assertEquals("Constraints: (1..20)", constraintText.textContent);
        verify(constraintText.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testOnArrowClickEvent() {
        view.onArrowClickEvent(mock(ClickEvent.class));

        verify(presenter).expandOrCollapseSubTypes();
    }

    @Test
    public void testOnInsertFieldAbove() {
        view.onInsertFieldAbove(mock(ClickEvent.class));

        verify(presenter).insertFieldAbove();
    }

    @Test
    public void testOnInsertFieldBelow() {
        view.onInsertFieldBelow(mock(ClickEvent.class));

        verify(presenter).insertFieldBelow();
    }

    @Test
    public void testOnInsertNestedField() {
        view.onInsertNestedField(mock(ClickEvent.class));

        verify(presenter).insertNestedField();
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
    public void testSetupConstraintComponent() {

        final DataTypeConstraint constraintComponent = mock(DataTypeConstraint.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);

        when(constraintComponent.getElement()).thenReturn(htmlElement);
        constraint.innerHTML = "previous content";

        view.setupConstraintComponent(constraintComponent);

        assertFalse(constraint.innerHTML.contains("previous content"));
        verify(constraint).appendChild(htmlElement);
    }

    @Test
    public void testSetupCollectionComponent() {

        final SmallSwitchComponent switchComponent = mock(SmallSwitchComponent.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);
        final Text collectionTextNode = mock(Text.class);

        when(switchComponent.getElement()).thenReturn(htmlElement);
        doReturn(collectionTextNode).when(view).collectionTextNode();
        collectionContainer.innerHTML = "previous content";

        view.setupCollectionComponent(switchComponent);

        assertFalse(collectionContainer.innerHTML.contains("previous content"));
        verify(collectionContainer).appendChild(collectionTextNode);
        verify(collectionContainer).appendChild(htmlElement);
    }

    @Test
    public void testShowConstraintContainer() {
        constraintContainer.classList = mock(DOMTokenList.class);

        view.showConstraintContainer();

        verify(constraintContainer.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideConstraintContainer() {

        constraintContainer.classList = mock(DOMTokenList.class);

        view.hideConstraintContainer();

        verify(constraintContainer.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowCollectionContainer() {
        collectionContainer.classList = mock(DOMTokenList.class);

        view.showCollectionContainer();

        verify(collectionContainer.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideCollectionContainer() {

        collectionContainer.classList = mock(DOMTokenList.class);

        view.hideCollectionContainer();

        verify(collectionContainer.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowCollectionYesLabel() {
        collectionYes.classList = mock(DOMTokenList.class);

        view.showCollectionYesLabel();

        verify(collectionYes.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideCollectionYesLabel() {

        collectionYes.classList = mock(DOMTokenList.class);

        view.hideCollectionYesLabel();

        verify(collectionYes.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowConstraintTextWhenConstraintTextIsNull() {
        constraintText.classList = mock(DOMTokenList.class);
        constraintText.textContent = null;

        view.showConstraintText();

        verify(constraintText.classList, never()).remove(anyString());
    }

    @Test
    public void testShowConstraintTextWhenConstraintTextIsBlank() {
        constraintText.classList = mock(DOMTokenList.class);
        constraintText.textContent = "";

        view.showConstraintText();

        verify(constraintText.classList, never()).remove(anyString());
    }

    @Test
    public void testShowConstraintTextWhenConstraintTextIsPresent() {
        constraintText.classList = mock(DOMTokenList.class);
        constraintText.textContent = "Constraint: (1..30)";

        view.showConstraintText();

        verify(constraintText.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideConstraintText() {
        constraintText.classList = mock(DOMTokenList.class);

        view.hideConstraintText();

        verify(constraintText.classList).add(HIDDEN_CSS_CLASS);
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
