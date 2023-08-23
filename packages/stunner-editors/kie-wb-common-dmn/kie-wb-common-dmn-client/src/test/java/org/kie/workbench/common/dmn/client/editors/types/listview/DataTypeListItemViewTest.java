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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.Element.OnclickFn;
import elemental2.dom.Event;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.NodeList;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraint;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.mockito.Mock;
import org.uberfire.client.workbench.ouia.OuiaAttribute;
import org.uberfire.client.workbench.ouia.OuiaComponentIdAttribute;
import org.uberfire.client.workbench.ouia.OuiaComponentTypeAttribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.NAME_DATA_FIELD;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.PARENT_UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.DOWN_ARROW_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.FOCUSED_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.RIGHT_ARROW_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_AddRowBelow;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_ArrowKeysTooltip;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_Cancel;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_Edit;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_Remove;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_Save;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.doubleThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
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
    private HTMLElement arrow;

    @Mock
    private HTMLElement nameText;

    @Mock
    private HTMLInputElement nameInput;

    @Mock
    private HTMLElement type;

    @Mock
    private HTMLDivElement constraint;

    @Mock
    private HTMLDivElement listContainer;

    @Mock
    private HTMLDivElement listYes;

    @Mock
    private HTMLButtonElement editButton;

    @Mock
    private HTMLButtonElement saveButton;

    @Mock
    private HTMLButtonElement closeButton;

    @Mock
    private HTMLButtonElement addDataTypeRow;

    @Mock
    private HTMLButtonElement removeButton;

    @Mock
    private DataTypeListItem presenter;

    @Mock
    private HTMLElement dataTypeListElement;

    @Mock
    private HTMLElement dragAndDropElement;

    @Mock
    private TranslationService translationService;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

    @Mock
    private Consumer<OuiaAttribute> ouiaRendererMock;

    private DataTypeListItemView view;

    @Before
    public void setup() {
        when(readOnlyProvider.isReadOnlyDiagram()).thenReturn(false);
        view = spy(new DataTypeListItemView(row, translationService, readOnlyProvider));
        view.init(presenter);
        when(view.ouiaAttributeRenderer()).thenReturn(ouiaRendererMock);

        when(presenter.getDragAndDropElement()).thenReturn(dragAndDropElement);
        doReturn(dataTypeListElement).when(presenter).getDragAndDropListElement();
    }

    @Test
    public void testSetDataType() {

        final DataType dataType = mock(DataType.class);

        doNothing().when(view).setupRowMetadata(dataType);
        doNothing().when(view).setupArrow(dataType);
        doNothing().when(view).setupReadOnly(dataType);
        doNothing().when(view).setupActionButtons();
        doNothing().when(view).setupEventHandlers();
        doNothing().when(view).setupShortcutsTooltips();

        view.setDataType(dataType);

        verify(view).setupRowMetadata(dataType);
        verify(view).setupArrow(dataType);
        verify(view).setupReadOnly(dataType);
        verify(view).setupActionButtons();
        verify(view).setupEventHandlers();
    }

    @Test
    public void testSetupRowMetadata() {

        final DataType dataType = mock(DataType.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        when(dataType.getUUID()).thenReturn("1234");
        when(dataType.getParentUUID()).thenReturn("4567");
        dragAndDropElement.classList = classList;

        view.setupRowMetadata(dataType);

        verify(dragAndDropElement).setAttribute(UUID_ATTR, "1234");
        verify(dragAndDropElement).setAttribute(PARENT_UUID_ATTR, "4567");
        verify(view).setupRowCSSClass(dataType);
    }

    @Test
    public void testSetupSubDataTypesCSSClassWhenDataTypeHasSubDataTypes() {

        final DataType dataType = mock(DataType.class);

        dragAndDropElement.classList = mock(DOMTokenList.class);
        when(dataType.hasSubDataTypes()).thenReturn(true);

        view.setupSubDataTypesCSSClass(dataType);

        verify(dragAndDropElement.classList).add("has-sub-data-types");
    }

    @Test
    public void testSetupSubDataTypesCSSClassWhenDataTypeDoesNotHaveSubDataTypes() {

        final DataType dataType = mock(DataType.class);

        dragAndDropElement.classList = mock(DOMTokenList.class);
        when(dataType.hasSubDataTypes()).thenReturn(false);

        view.setupSubDataTypesCSSClass(dataType);

        verify(dragAndDropElement.classList).remove("has-sub-data-types");
    }

    @Test
    public void testSetupReadOnlyCSSClassWhenDataTypeHasSubDataTypes() {

        final DataType dataType = mock(DataType.class);

        dragAndDropElement.classList = mock(DOMTokenList.class);
        when(dataType.isReadOnly()).thenReturn(true);

        view.setupReadOnlyCSSClass(dataType);

        verify(dragAndDropElement.classList).add("read-only");
    }

    @Test
    public void testSetupReadOnlyCSSClassWhenIsReadOnlyDiagram() {

        final DataType dataType = mock(DataType.class);

        dragAndDropElement.classList = mock(DOMTokenList.class);
        when(dataType.isReadOnly()).thenReturn(false);
        when(readOnlyProvider.isReadOnlyDiagram()).thenReturn(true);

        view.setupReadOnlyCSSClass(dataType);

        verify(dragAndDropElement.classList).add("read-only");
    }

    @Test
    public void testSetupReadOnlyCSSClassWhenDataTypeDoesNotHaveSubDataTypes() {

        final DataType dataType = mock(DataType.class);

        dragAndDropElement.classList = mock(DOMTokenList.class);
        when(dataType.isReadOnly()).thenReturn(false);

        view.setupReadOnlyCSSClass(dataType);

        verify(dragAndDropElement.classList).remove("read-only");
    }

    @Test
    public void testToggleArrowWhenTrue() {

        final DOMTokenList classList = mock(DOMTokenList.class);

        doReturn(arrow).when(view).getArrow();
        arrow.classList = classList;

        view.toggleArrow(true);

        verify(classList).remove("hidden");
    }

    @Test
    public void testToggleArrowWhenFalse() {

        final DOMTokenList classList = mock(DOMTokenList.class);

        doReturn(arrow).when(view).getArrow();
        arrow.classList = classList;

        view.toggleArrow(false);

        verify(classList).add("hidden");
    }

    @Test
    public void testSetupArrowWhenDataTypeHasSubTypes() {

        final DataType dataType = mock(DataType.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        doReturn(arrow).when(view).getArrow();
        when(dataType.hasSubDataTypes()).thenReturn(true);
        arrow.classList = classList;

        view.setupArrow(dataType);

        verify(view).toggleArrow(true);
    }

    @Test
    public void testSetupArrowWhenDataTypeDoesNotHaveSubTypes() {

        final DataType dataType = mock(DataType.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        doReturn(arrow).when(view).getArrow();
        when(dataType.hasSubDataTypes()).thenReturn(false);
        arrow.classList = classList;

        view.setupArrow(dataType);

        verify(view).toggleArrow(false);
    }

    @Test
    public void testSetupReadOnly() {

        final DataType dataType = mock(DataType.class);
        final DOMTokenList classList = mock(DOMTokenList.class);
        final String name = "name";
        final String constraint = "constraint";

        doNothing().when(view).setName(name);
        doReturn(nameInput).when(view).getNameInput();
        when(dataType.getName()).thenReturn(name);
        when(dataType.getConstraint()).thenReturn(constraint);
        nameInput.classList = classList;

        view.setupReadOnly(dataType);

        verify(classList).add(HIDDEN_CSS_CLASS);
        verify(view).setName(name);
    }

    @Test
    public void testSetName() {

        final String name = "name";

        doReturn(nameInput).when(view).getNameInput();
        doReturn(nameText).when(view).getNameText();

        view.setName(name);

        assertEquals(name, nameText.textContent);
        assertEquals(name, nameInput.value);
    }

    @Test
    public void testSetupShortcutsTooltips() {

        final Element editButton = mock(Element.class);
        final Element saveButton = mock(Element.class);
        final Element addDataTypeRow = mock(Element.class);
        final Element closeButton = mock(Element.class);
        final Element removeButton = mock(Element.class);
        final Element arrow = mock(Element.class);
        final String arrowKeysTooltip = "arrowKeysTooltip";
        final String dataTypeListItemViewEditKey = "Edit (Ctrl + E)";
        final String dataTypeListItemViewSaveKey = "Save (Ctrl + S)";
        final String dataTypeListItemViewAddRowBelowKey = "Add row below (Ctrl + B)";
        final String dataTypeListItemViewRemoveKey = "Remove (Ctrl + Backspace)";
        final String dataTypeListItemViewCancelKey = "Cancel (Esc)";

        doReturn(editButton).when(view).getEditButton();
        doReturn(saveButton).when(view).getSaveButton();
        doReturn(addDataTypeRow).when(view).getAddDataTypeRowButton();
        doReturn(closeButton).when(view).getCloseButton();
        doReturn(removeButton).when(view).getRemoveButton();
        doReturn(arrow).when(view).getArrow();
        when(translationService.format(DataTypeListItemView_ArrowKeysTooltip)).thenReturn(arrowKeysTooltip);
        when(translationService.format(DataTypeListItemView_Edit)).thenReturn(dataTypeListItemViewEditKey);
        when(translationService.format(DataTypeListItemView_Save)).thenReturn(dataTypeListItemViewSaveKey);
        when(translationService.format(DataTypeListItemView_AddRowBelow)).thenReturn(dataTypeListItemViewAddRowBelowKey);
        when(translationService.format(DataTypeListItemView_Remove)).thenReturn(dataTypeListItemViewRemoveKey);
        when(translationService.format(DataTypeListItemView_Cancel)).thenReturn(dataTypeListItemViewCancelKey);

        view.setupShortcutsTooltips();

        verify(editButton).setAttribute("data-title", dataTypeListItemViewEditKey);
        verify(saveButton).setAttribute("data-title", dataTypeListItemViewSaveKey);
        verify(addDataTypeRow).setAttribute("data-title", dataTypeListItemViewAddRowBelowKey);
        verify(removeButton).setAttribute("data-title", dataTypeListItemViewRemoveKey);
        verify(closeButton).setAttribute("data-title", dataTypeListItemViewCancelKey);
        verify(arrow).setAttribute("data-title", arrowKeysTooltip);
        verify(view).setupTooltips();
    }

    @Test
    public void testSetupTooltips() {

        view.setupTooltips();
    }

    @Test
    public void testSetupEventHandlers() {

        final Element editButton = mock(Element.class);
        final Element saveButton = mock(Element.class);
        final Element closeButton = mock(Element.class);
        final Element arrow = mock(Element.class);
        final Element addDataTypeRow = mock(Element.class);
        final Element removeButton = mock(Element.class);
        final OnclickFn onEditAction = mock(OnclickFn.class);
        final OnclickFn onSaveAction = mock(OnclickFn.class);
        final OnclickFn onCloseAction = mock(OnclickFn.class);
        final OnclickFn onArrowClickAction = mock(OnclickFn.class);
        final OnclickFn onAddDataTypeRowAction = mock(OnclickFn.class);
        final OnclickFn onRemoveButtonAction = mock(OnclickFn.class);

        doReturn(editButton).when(view).getEditButton();
        doReturn(saveButton).when(view).getSaveButton();
        doReturn(closeButton).when(view).getCloseButton();
        doReturn(arrow).when(view).getArrow();
        doReturn(addDataTypeRow).when(view).getAddDataTypeRowButton();
        doReturn(removeButton).when(view).getRemoveButton();
        doReturn(onEditAction).when(view).getOnEditAction();
        doReturn(onSaveAction).when(view).getOnSaveAction();
        doReturn(onCloseAction).when(view).getOnCloseAction();
        doReturn(onArrowClickAction).when(view).getOnArrowClickAction();
        doReturn(onAddDataTypeRowAction).when(view).getOnAddDataTypeRowAction();
        doReturn(onRemoveButtonAction).when(view).getOnRemoveButtonAction();

        editButton.onclick = null;
        saveButton.onclick = null;
        closeButton.onclick = null;
        arrow.onclick = null;
        addDataTypeRow.onclick = null;
        removeButton.onclick = null;

        view.setupEventHandlers();

        assertEquals(onEditAction, editButton.onclick);
        assertEquals(onSaveAction, saveButton.onclick);
        assertEquals(onCloseAction, closeButton.onclick);
        assertEquals(onArrowClickAction, arrow.onclick);
        assertEquals(onAddDataTypeRowAction, addDataTypeRow.onclick);
        assertEquals(onRemoveButtonAction, removeButton.onclick);
    }

    @Test
    public void testOnArrowClickEvent() {

        final OnclickFn action = view.getOnArrowClickAction();

        assertTrue((Boolean) action.onInvoke(mock(Event.class)));

        verify(presenter).expandOrCollapseSubTypes();
    }

    @Test
    public void testOnAddDataTypeRowAction() {
        final OnclickFn action = view.getOnAddDataTypeRowAction();

        assertTrue((Boolean) action.onInvoke(mock(Event.class)));

        verify(presenter).addDataTypeRow();
    }

    @Test
    public void testOnRemoveButton() {
        final OnclickFn action = view.getOnRemoveButtonAction();

        assertTrue((Boolean) action.onInvoke(mock(Event.class)));

        verify(presenter).remove();
    }

    @Test
    public void testOnEditClick() {
        final OnclickFn action = view.getOnEditAction();

        assertTrue((Boolean) action.onInvoke(mock(Event.class)));

        verify(presenter).enableEditMode();
    }

    @Test
    public void testOnSaveClick() {
        final OnclickFn action = view.getOnSaveAction();

        assertTrue((Boolean) action.onInvoke(mock(Event.class)));

        verify(presenter).saveAndCloseEditMode();
        verify(ouiaRendererMock).accept(any(OuiaComponentIdAttribute.class));
    }

    @Test
    public void testOnCloseClick() {
        final OnclickFn action = view.getOnCloseAction();

        assertTrue((Boolean) action.onInvoke(mock(Event.class)));

        verify(presenter).disableEditMode();
    }

    @Test
    public void testSetupSelectComponent() {

        final DataTypeSelect select = mock(DataTypeSelect.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);
        final Element element = mock(Element.class);

        type.firstChild = element;
        doReturn(type).when(view).getType();
        when(select.getElement()).thenReturn(htmlElement);
        when(type.removeChild(element)).then(a -> {
            type.firstChild = null;
            return element;
        });

        view.setupSelectComponent(select);

        verify(type).removeChild(element);
        verify(type).appendChild(htmlElement);
    }

    @Test
    public void testSetupConstraintComponent() {

        final DataTypeConstraint constraintComponent = mock(DataTypeConstraint.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);
        final Element element = mock(Element.class);

        constraint.firstChild = element;
        doReturn(constraint).when(view).getConstraintContainer();
        when(constraintComponent.getElement()).thenReturn(htmlElement);
        when(constraint.removeChild(element)).then(a -> {
            constraint.firstChild = null;
            return element;
        });

        view.setupConstraintComponent(constraintComponent);

        verify(constraint).removeChild(element);
        verify(constraint).appendChild(htmlElement);
    }

    @Test
    public void testSetupListComponent() {

        final SmallSwitchComponent switchComponent = mock(SmallSwitchComponent.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);
        final Element element = mock(Element.class);

        listContainer.firstChild = element;
        when(switchComponent.getElement()).thenReturn(htmlElement);
        doReturn(listContainer).when(view).getListCheckBoxContainer();
        when(listContainer.removeChild(element)).then(a -> {
            listContainer.firstChild = null;
            return element;
        });

        view.setupListComponent(switchComponent);

        verify(listContainer).removeChild(element);
        verify(listContainer).appendChild(htmlElement);
    }

    @Test
    public void testShowCollectionContainer() {

        doReturn(listContainer).when(view).getListContainer();
        listContainer.classList = mock(DOMTokenList.class);

        view.showListContainer();

        verify(listContainer.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideCollectionContainer() {

        doReturn(listContainer).when(view).getListContainer();
        listContainer.classList = mock(DOMTokenList.class);

        view.hideListContainer();

        verify(listContainer.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowCollectionYesLabel() {

        doReturn(listYes).when(view).getListYes();
        listYes.classList = mock(DOMTokenList.class);

        view.showListYesLabel();

        verify(listYes.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideCollectionYesLabel() {

        doReturn(listYes).when(view).getListYes();
        listYes.classList = mock(DOMTokenList.class);

        view.hideListYesLabel();

        verify(listYes.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testSetupActionButtons() {

        doNothing().when(view).showEditButton();

        view.setupActionButtons();

        verify(view).showEditButton();
    }

    @Test
    public void testShowEditButton() {

        editButton.classList = mock(DOMTokenList.class);
        addDataTypeRow.classList = mock(DOMTokenList.class);
        removeButton.classList = mock(DOMTokenList.class);
        saveButton.classList = mock(DOMTokenList.class);
        closeButton.classList = mock(DOMTokenList.class);

        doReturn(editButton).when(view).getEditButton();
        doReturn(addDataTypeRow).when(view).getAddDataTypeRowButton();
        doReturn(removeButton).when(view).getRemoveButton();
        doReturn(saveButton).when(view).getSaveButton();
        doReturn(closeButton).when(view).getCloseButton();

        view.showEditButton();

        verify(editButton.classList).remove(HIDDEN_CSS_CLASS);
        verify(addDataTypeRow.classList).remove(HIDDEN_CSS_CLASS);
        verify(removeButton.classList).remove(HIDDEN_CSS_CLASS);
        verify(saveButton.classList).add(HIDDEN_CSS_CLASS);
        verify(closeButton.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowSaveButton() {

        editButton.classList = mock(DOMTokenList.class);
        addDataTypeRow.classList = mock(DOMTokenList.class);
        removeButton.classList = mock(DOMTokenList.class);
        saveButton.classList = mock(DOMTokenList.class);
        closeButton.classList = mock(DOMTokenList.class);

        doReturn(editButton).when(view).getEditButton();
        doReturn(addDataTypeRow).when(view).getAddDataTypeRowButton();
        doReturn(removeButton).when(view).getRemoveButton();
        doReturn(saveButton).when(view).getSaveButton();
        doReturn(closeButton).when(view).getCloseButton();

        view.showSaveButton();

        verify(editButton.classList).add(HIDDEN_CSS_CLASS);
        verify(addDataTypeRow.classList).add(HIDDEN_CSS_CLASS);
        verify(removeButton.classList).add(HIDDEN_CSS_CLASS);
        verify(saveButton.classList).remove(HIDDEN_CSS_CLASS);
        verify(closeButton.classList).remove(HIDDEN_CSS_CLASS);
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
        // item 0 - yPosition: 0
        // item 1 - yPosition: 1
        // child1 and child2 yPosition need to be greater than 0 and less than 1
        verify(presenter).setPositionY(eq(child1), doubleThat(d -> d > 0.3 && d < 0.6));
        verify(presenter).setPositionY(eq(child2), doubleThat(d -> d > 0.6 && d < 1.0));
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
        doReturn(nameInput).when(view).getNameInput();
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
        doReturn(nameInput).when(view).getNameInput();

        final String actualName = view.getName();

        assertEquals(expectedName, actualName);
    }

    @Test
    public void testIsOnFocusModeWhenItReturnsTrue() {

        final DataType dataType = mock(DataType.class);
        final Element dataTypeRow = mock(Element.class);

        dataTypeRow.classList = mock(DOMTokenList.class);

        doReturn(dataType).when(view).getDataType();
        doReturn(dataTypeRow).when(view).getRowElement(dataType);

        when(dataTypeRow.classList.contains(FOCUSED_CSS_CLASS)).thenReturn(true);

        assertTrue(view.isOnFocusMode());
    }

    @Test
    public void testIsOnFocusModeWhenItReturnsFalse() {

        final DataType dataType = mock(DataType.class);
        final Element dataTypeRow = mock(Element.class);

        dataTypeRow.classList = mock(DOMTokenList.class);

        doReturn(dataType).when(view).getDataType();
        doReturn(dataTypeRow).when(view).getRowElement(dataType);

        when(dataTypeRow.classList.contains(FOCUSED_CSS_CLASS)).thenReturn(false);

        assertFalse(view.isOnFocusMode());
    }

    @Test
    public void testShowDataTypeNameInput() {

        nameText.classList = mock(DOMTokenList.class);
        nameInput.classList = mock(DOMTokenList.class);

        doReturn(nameText).when(view).getNameText();
        doReturn(nameInput).when(view).getNameInput();
        doNothing().when(view).showLabels();

        view.showDataTypeNameInput();

        verify(nameText.classList).add(HIDDEN_CSS_CLASS);
        verify(nameInput.classList).remove(HIDDEN_CSS_CLASS);
        verify(view).showLabels();
    }

    @Test
    public void testHideDataTypeNameInput() {

        final String expectedName = "name";

        nameInput.value = expectedName;

        nameText.classList = mock(DOMTokenList.class);
        nameInput.classList = mock(DOMTokenList.class);
        doReturn(nameText).when(view).getNameText();
        doReturn(nameInput).when(view).getNameInput();
        doNothing().when(view).hideLabels();

        view.hideDataTypeNameInput();

        assertEquals(expectedName, nameText.textContent);
        verify(nameText.classList).remove(HIDDEN_CSS_CLASS);
        verify(nameInput.classList).add(HIDDEN_CSS_CLASS);
        verify(view).hideLabels();
    }

    @Test
    public void testHideDataTypeNameInputWhenNameIsBlank() {

        final String expectedName = "-";

        nameInput.value = "";
        nameText.classList = mock(DOMTokenList.class);
        nameInput.classList = mock(DOMTokenList.class);

        doReturn(nameText).when(view).getNameText();
        doReturn(nameInput).when(view).getNameInput();
        doNothing().when(view).hideLabels();

        view.hideDataTypeNameInput();

        assertEquals(expectedName, nameText.textContent);
        verify(nameText.classList).remove(HIDDEN_CSS_CLASS);
        verify(nameInput.classList).add(HIDDEN_CSS_CLASS);
        verify(view).hideLabels();
    }

    @Test
    public void testShowLabels() {

        final Element element1 = mock(Element.class);
        final Element element2 = mock(Element.class);
        final NodeList<Element> labels = spy(new NodeList<>());

        element1.classList = mock(DOMTokenList.class);
        element2.classList = mock(DOMTokenList.class);

        labels.length = 2;
        doReturn(element1).when(labels).getAt(0);
        doReturn(element2).when(labels).getAt(1);
        doReturn(labels).when(view).getLabels();

        view.showLabels();

        verify(element1.classList).remove(HIDDEN_CSS_CLASS);
        verify(element2.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideLabels() {

        final Element element1 = mock(Element.class);
        final Element element2 = mock(Element.class);
        final NodeList<Element> labels = spy(new NodeList<>());

        element1.classList = mock(DOMTokenList.class);
        element2.classList = mock(DOMTokenList.class);

        labels.length = 2;
        doReturn(element1).when(labels).getAt(0);
        doReturn(element2).when(labels).getAt(1);
        doReturn(labels).when(view).getLabels();

        view.hideLabels();

        verify(element1.classList).add(HIDDEN_CSS_CLASS);
        verify(element2.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testGetConstraintContainer() {
        final Element element = mock(Element.class);
        doReturn(element).when(view).querySelector("constraint-container");
        assertEquals(element, view.getConstraintContainer());
    }

    @Test
    public void testGetCollectionContainer() {
        final Element element = mock(Element.class);
        doReturn(element).when(view).querySelector("list-container");
        assertEquals(element, view.getListContainer());
    }

    @Test
    public void testGetCollectionYes() {
        final Element element = mock(Element.class);
        doReturn(element).when(view).querySelector("list-yes");
        assertEquals(element, view.getListYes());
    }

    @Test
    public void testGetEditButton() {
        final Element element = mock(Element.class);
        doReturn(element).when(view).querySelector("edit-button");
        assertEquals(element, view.getEditButton());
    }

    @Test
    public void testGetSaveButton() {
        final Element element = mock(Element.class);
        doReturn(element).when(view).querySelector("save-button");
        assertEquals(element, view.getSaveButton());
    }

    @Test
    public void testGetCloseButton() {
        final Element element = mock(Element.class);
        doReturn(element).when(view).querySelector("close-button");
        assertEquals(element, view.getCloseButton());
    }

    @Test
    public void testGetRemoveButton() {
        final Element element = mock(Element.class);
        doReturn(element).when(view).querySelector("remove-button");
        assertEquals(element, view.getRemoveButton());
    }

    @Test
    public void tesGetAddDataTypeRowButton() {
        final Element element = mock(Element.class);
        doReturn(element).when(view).querySelector("add-data-type-row-button");
        assertEquals(element, view.getAddDataTypeRowButton());
    }

    @Test
    public void testGetLabels() {

        final Element element1 = mock(Element.class);
        final Element element2 = mock(Element.class);
        final HTMLElement viewElement = mock(HTMLElement.class);
        final NodeList<Element> labels = spy(new NodeList<>());

        element1.classList = mock(DOMTokenList.class);
        element2.classList = mock(DOMTokenList.class);

        labels.length = 2;
        doReturn(element1).when(labels).getAt(0);
        doReturn(element2).when(labels).getAt(0);
        doReturn(viewElement).when(view).getElement();
        when(viewElement.querySelectorAll(".data-type-label")).thenReturn(labels);

        assertEquals(labels, view.getLabels());
    }

    @Test
    public void testGetArrow() {
        final Element element = mock(Element.class);
        doReturn(element).when(view).querySelector("arrow-button");
        assertEquals(element, view.getArrow());
    }

    @Test
    public void testGetNameText() {
        final Element element = mock(Element.class);
        doReturn(element).when(view).querySelector("name-text");
        assertEquals(element, view.getNameText());
    }

    @Test
    public void testGetNameInput() {
        final HTMLInputElement element = mock(HTMLInputElement.class);
        doReturn(element).when(view).querySelector(NAME_DATA_FIELD);
        assertEquals(element, view.getNameInput());
    }

    @Test
    public void testGetType() {
        final Element element = mock(Element.class);
        doReturn(element).when(view).querySelector("type");
        assertEquals(element, view.getType());
    }

    @Test
    public void testOuiaComponentType() {
        assertEquals(view.ouiaComponentType(), new OuiaComponentTypeAttribute("dmn-data-type-item"));
    }

    @Test
    public void testOuiaComponentId_unknown() {
        when(presenter.getDataType()).thenReturn(null);
        assertEquals(view.ouiaComponentId(), new OuiaComponentIdAttribute("unknown"));
    }

    @Test
    public void testOuiaComponentId() {
        final String typeName = "tCar";
        final DataType dataTypeMock = mock(DataType.class);
        when(dataTypeMock.getName()).thenReturn(typeName);
        when(presenter.getDataType()).thenReturn(dataTypeMock);
        assertEquals(view.ouiaComponentId(), new OuiaComponentIdAttribute(typeName));
    }

    @Test
    public void testOuiaRenderer() {
        doCallRealMethod().when(view).ouiaAttributeRenderer();
        view.initOuiaComponentAttributes();

        verify(row).setAttribute(OuiaComponentTypeAttribute.COMPONENT_TYPE, "dmn-data-type-item");
        verify(row).setAttribute(OuiaComponentIdAttribute.COMPONENT_ID, "unknown");
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
