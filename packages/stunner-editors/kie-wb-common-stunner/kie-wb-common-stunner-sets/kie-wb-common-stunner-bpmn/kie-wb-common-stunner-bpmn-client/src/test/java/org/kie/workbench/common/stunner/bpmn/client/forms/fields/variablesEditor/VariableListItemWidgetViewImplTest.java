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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.enterprise.event.Event;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMRect;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.MouseEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class VariableListItemWidgetViewImplTest {

    private static final String VARIABLE_NAME = "variableName";
    private static final String MODEL_TO_STRING = "modelToString";
    private static final String CUST_DATA_TYPE_NAME = "custDataTypeName";
    private static final String DATA_TYPE_NAME = "dataTypeName";
    private static final String VARIABLE_NEW_NAME = "variableNewName";
    private static final String MODEL_NEW_TO_STRING = "modelNewToString";

    @GwtMock
    private DataBinder<VariableRow> variableRow;

    @GwtMock
    private VariableNameTextBox name;

    @GwtMock
    private Button deleteButton;

    @Mock
    private CSSStyleDeclaration overlayDivCSS;

    @Mock
    private ComboBox tagNamesComboBox;

    private CustomDataTypeTextBox customDataType;

    private ValueListBox<String> dataType;

    private ComboBox dataTypeComboBox;

    private ComboBox processVarComboBox;

    private HTMLAnchorElement variableTagsSettings;

    private HTMLLabelElement tagCount;

    private CustomDataTypeTextBox customTagName;

    private ValueListBox<String> defaultTagNames;

    @GwtMock
    private KeyDownEvent keyDownEvent;

    @GwtMock
    private ClickEvent clickEvent;

    private Event<NotificationEvent> notification = mock(EventSourceMock.class);

    @Captor
    private ArgumentCaptor<KeyDownHandler> keyDownHandlerCaptor;

    @Captor
    private ArgumentCaptor<ClickHandler> clickHandlerCaptor;

    @Captor
    private ArgumentCaptor<ChangeHandler> changeHandlerCaptor;

    private VariableListItemWidgetViewImpl view;

    @Mock
    private VariablesEditorFieldRenderer parent;

    @Before
    public void setUp() throws Exception {
        parent = mock(VariablesEditorFieldRenderer.class);
        GwtMockito.initMocks(this);
        customDataType = mock(CustomDataTypeTextBox.class);
        dataType = mock(ValueListBox.class);
        dataTypeComboBox = mock(ComboBox.class);
        processVarComboBox = mock(ComboBox.class);
        variableTagsSettings = mock(HTMLAnchorElement.class);
        tagCount = mock(HTMLLabelElement.class);
        customTagName = mock(CustomDataTypeTextBox.class);
        defaultTagNames = mock(ValueListBox.class);
        tagNamesComboBox = mock(ComboBox.class);
        view = mock(VariableListItemWidgetViewImpl.class);
        view.variableRow = variableRow;
        view.name = name;
        view.deleteButton = deleteButton;
        view.tagNamesComboBox = tagNamesComboBox;
        view.customDataType = customDataType;
        view.dataType = dataType;
        view.dataTypeComboBox = dataTypeComboBox;
        view.notification = notification;
        view.variableTagsSettings = variableTagsSettings;
        view.tagCount = tagCount;
        view.customTagName = customTagName;
        view.defaultTagNames = defaultTagNames;
        view.tagNamesList = new ArrayList<>();
        doCallRealMethod().when(view).init();
        doCallRealMethod().when(view).getCustomDataType();
        doCallRealMethod().when(view).setCustomDataType(any());
        doCallRealMethod().when(view).getModel();
        doCallRealMethod().when(view).setModel(any(VariableRow.class));
        doCallRealMethod().when(view).getModelValue(any());
        doCallRealMethod().when(view).isDuplicateID(anyString());
        doCallRealMethod().when(view).setTextBoxModelValue(any(TextBox.class),
                                                           any());
        doCallRealMethod().when(view).setListBoxModelValue(any(),
                                                           any());
        doCallRealMethod().when(view).getDataTypeDisplayName();
        doCallRealMethod().when(view).setDataTypeDisplayName(any());
        doCallRealMethod().when(view).getVariableType();
        doCallRealMethod().when(view).setParentWidget(any());
        doCallRealMethod().when(view).handleDeleteButton(any());
        doCallRealMethod().when(view).handleCloseButton(any());
        doCallRealMethod().when(view).handleAcceptButton(any());
        doCallRealMethod().when(view).setReadOnly(anyBoolean());
        doCallRealMethod().when(view).notifyModelChanged();
        doCallRealMethod().when(view).setTagsNotEnabled();
        doCallRealMethod().when(view).openOverlayActions();
        doCallRealMethod().when(view).setTagSet(any());
        doCallRealMethod().when(view).getTagSet();

        doCallRealMethod().when(parent).getLastOverlayOpened();
        doCallRealMethod().when(parent).setLastOverlayOpened(any());

        VariableRow row = new VariableRow();
        doReturn(row).when(variableRow).getModel();
        view.setParentWidget(parent);
    }

    @Test
    public void testSetProcessVarCustomDataType() {
        VariableRow row = new VariableRow();
        row.setName(VARIABLE_NAME);
        row.setCustomDataType(CUST_DATA_TYPE_NAME);
        row.setDataTypeDisplayName(null);
        row.setVariableType(Variable.VariableType.PROCESS);
        doReturn(row).when(variableRow).getModel();
        view.setModel(row);
        verify(variableRow,
               times(1)).setModel(row);
        verify(deleteButton,
               times(1)).setIcon(IconType.TRASH);
        verify(customDataType,
               times(1)).setValue(CUST_DATA_TYPE_NAME);
        verify(dataType,
               times(1)).setValue(CUST_DATA_TYPE_NAME);
    }

    @Test
    public void testSetProcessVarDataType() {
        VariableRow row = new VariableRow();
        row.setName(VARIABLE_NAME);
        row.setCustomDataType(null);
        row.setDataTypeDisplayName(DATA_TYPE_NAME);
        row.setVariableType(Variable.VariableType.PROCESS);
        row.setTags(Arrays.asList("internal"));
        tagNamesComboBox.setTextBoxValue("internal");
        doReturn(row).when(variableRow).getModel();
        view.setModel(row);
        verify(variableRow,
               times(1)).setModel(row);
        verify(deleteButton,
               times(1)).setIcon(IconType.TRASH);
        verify(customDataType,
               never()).setValue(DATA_TYPE_NAME);
        verify(dataType,
               times(1)).setValue(DATA_TYPE_NAME);
        verify(tagNamesComboBox,
               times(1)).setTextBoxValue("internal");
    }

    @Test
    public void testSetTextBoxModelValueCustomDataType() {
        assertNull(view.getModel().getCustomDataType());
        view.setTextBoxModelValue(customDataType,
                                  "abc");
        assertEquals("abc",
                     view.getModel().getCustomDataType());
        assertEquals("abc",
                     view.getModelValue(dataType));
    }

    @Test
    public void testSetListBoxModelValueDataType() {
        assertNull(view.getModel().getDataTypeDisplayName());
        view.setListBoxModelValue(dataType,
                                  "abc");
        assertEquals("abc",
                     view.getModel().getDataTypeDisplayName());
        assertNull(view.getModel().getCustomDataType());
        assertEquals("abc",
                     view.getModelValue(dataType));
    }

    @Test
    public void testDataTypeHandlerSpace() {
        view.init();
        verify(customDataType,
               times(1)).addKeyDownHandler(keyDownHandlerCaptor.capture());
        KeyDownHandler handler = keyDownHandlerCaptor.getValue();
        doReturn(Integer.valueOf(' ')).when(keyDownEvent).getNativeKeyCode();
        handler.onKeyDown(keyDownEvent);
        verify(keyDownEvent,
               times(1)).preventDefault();
    }

    @Test
    public void testDataTypeHandlerAlphabetical() {
        view.init();
        verify(customDataType,
               times(1)).addKeyDownHandler(keyDownHandlerCaptor.capture());
        KeyDownHandler handler = keyDownHandlerCaptor.getValue();
        doReturn(Integer.valueOf('a')).when(keyDownEvent).getNativeKeyCode();
        handler.onKeyDown(keyDownEvent);
        verify(keyDownEvent,
               never()).preventDefault();
    }

    @Test
    public void testTagTypeHandlerSpace() {
        view.init();
        verify(customTagName,
               times(1)).addKeyDownHandler(keyDownHandlerCaptor.capture());
        KeyDownHandler handler = keyDownHandlerCaptor.getValue();
        doReturn(Integer.valueOf(' ')).when(keyDownEvent).getNativeKeyCode();
        handler.onKeyDown(keyDownEvent);
        verify(keyDownEvent,
               times(1)).preventDefault();
    }

    @Test
    public void testTagTypeHandlerAlphabetical() {
        view.init();
        verify(customTagName,
               times(1)).addKeyDownHandler(keyDownHandlerCaptor.capture());
        KeyDownHandler handler = keyDownHandlerCaptor.getValue();
        doReturn(Integer.valueOf('a')).when(keyDownEvent).getNativeKeyCode();
        handler.onKeyDown(keyDownEvent);
        verify(keyDownEvent,
               never()).preventDefault();
    }

    @Test
    public void testTagAnchorClick() {
        view.init();
        view.isOpen = true;
        view.openOverlayActions();
        // Verify it is now closed
        assertEquals(false, view.isOpen);

        view.isOpen = false;
        Button lastOverlay = mock(Button.class);
        parent.setLastOverlayOpened(lastOverlay);
        final HTMLDivElement overlayDiv = mock(HTMLDivElement.class);
        final Element lastNode = mock(Element.class);

        final DOMRect rect = mock(DOMRect.class);
        rect.left = 100;
        rect.top = 100;

        final DOMRect rect2 = mock(DOMRect.class);
        rect2.left = 150;
        rect2.top = 150;

        when(view.getNextElementSibling()).thenReturn(overlayDiv);
        when(view.getLastElementChild(overlayDiv)).thenReturn(lastNode);

        when(overlayDiv.getBoundingClientRect()).thenReturn(rect);
        when(tagCount.getBoundingClientRect()).thenReturn(rect2);

        overlayDiv.style = new elemental2.dom.CSSStyleDeclaration();
        overlayDiv.style.left = "100px";

        final HTMLDivElement tagsDiv = mock(HTMLDivElement.class);

        tagsDiv.style = new elemental2.dom.CSSStyleDeclaration();
        view.tagsDiv = tagsDiv;

        final Button closeButton = mock(Button.class);
        view.closeButton = closeButton;
        view.openOverlayActions();

        // verify last overlay was closed
        assertEquals(true, view.isOpen);
        assertEquals(parent.getLastOverlayOpened(), closeButton);
        // since first time call, top position must be null
        assertEquals(null, view.overlayTopPosition);

        view.openOverlayActions();
        // lass overlay should be now closed
        verify(parent, times(1)).closeLastOverlay();
        assertEquals(false, view.isOpen);
        // since closed lastOverlay opened must be null since it does not to be closed by other overlays being opened
        assertEquals(null, parent.getLastOverlayOpened());

        view.overlayTopPosition = "100px";
        view.openOverlayActions();
        // since opened, lastOverlay must be the close button
        assertEquals(closeButton, parent.getLastOverlayOpened());
        // verify the same top position remains
        assertEquals("100px", view.overlayTopPosition);
    }

    @Test
    public void testNameChangeHandlerWhenDuplicateID() {
        when(parent.isDuplicateID(VARIABLE_NEW_NAME)).thenReturn(true);
        prepareNameChange(VARIABLE_NEW_NAME, MODEL_NEW_TO_STRING);
        verify(parent).isDuplicateID(VARIABLE_NEW_NAME);
        verify(notification).fire(new NotificationEvent(StunnerFormsClientFieldsConstants.CONSTANTS.DuplicatedVariableIDError(VARIABLE_NEW_NAME),
                                                        NotificationEvent.NotificationType.ERROR));
        verify(name).setValue(VARIABLE_NAME);
    }

    @Test
    public void testNameChangeHandlerWhenDuplicateName() {
        when(parent.isDuplicateName(VARIABLE_NEW_NAME)).thenReturn(true);
        prepareNameChange(VARIABLE_NEW_NAME, MODEL_NEW_TO_STRING);
        verify(parent).isDuplicateName(VARIABLE_NEW_NAME);
        verify(notification).fire(new NotificationEvent(StunnerFormsClientFieldsConstants.CONSTANTS.DuplicatedVariableNameError(VARIABLE_NEW_NAME),
                                                        NotificationEvent.NotificationType.ERROR));
        verify(name).setValue(VARIABLE_NAME);
    }

    @Test
    public void testNameChangeHandlerWhenNotDuplicateAndNotBoundToNodes() {
        when(parent.isDuplicateName(VARIABLE_NEW_NAME)).thenReturn(false);
        when(parent.isDuplicateID(VARIABLE_NEW_NAME)).thenReturn(false);
        when(parent.isBoundToNodes(VARIABLE_NAME)).thenReturn(false);
        prepareNameChange(VARIABLE_NEW_NAME, MODEL_NEW_TO_STRING);
        verify(parent).isDuplicateName(VARIABLE_NEW_NAME);
        verify(parent).isDuplicateID(VARIABLE_NEW_NAME);
        verify(parent).isBoundToNodes(VARIABLE_NAME);
        verify(parent).notifyModelChanged();
    }

    @Test
    public void testNameChangeHandlerWhenNotDuplicateAndBoundToNodes() {
        when(parent.isDuplicateName(VARIABLE_NEW_NAME)).thenReturn(false);
        when(parent.isBoundToNodes(VARIABLE_NAME)).thenReturn(true);
        prepareNameChange(VARIABLE_NEW_NAME, MODEL_NEW_TO_STRING);
        verify(parent).isDuplicateName(VARIABLE_NEW_NAME);
        verify(parent).isBoundToNodes(VARIABLE_NAME);
        verify(name).setValue(VARIABLE_NAME);
    }

    private void prepareNameChange(String newName, String newToString) {
        doReturn(newName).when(name).getText();
        VariableRow model = mock(VariableRow.class);
        when(model.getName()).thenReturn(VARIABLE_NAME);
        when(model.toString()).thenReturn(MODEL_TO_STRING);
        doReturn(model).when(variableRow).getModel();
        view.setParentWidget(parent);
        view.init();
        view.setModel(model);
        when(model.toString()).thenReturn(newToString);
        verify(name).addChangeHandler(changeHandlerCaptor.capture());
        ChangeHandler handler = changeHandlerCaptor.getValue();
        handler.onChange(mock(ChangeEvent.class));
    }

    @Test
    public void testHandleDeleteButton() {
        VariableRow model = mock(VariableRow.class);
        when(view.getModel()).thenReturn(model);
        view.setParentWidget(parent);
        view.handleDeleteButton(null);
        verify(parent).removeVariable(model);
    }

    @Test
    public void testHandleCloseButton() {
        view.handleCloseButton(null);
        verify(variableTagsSettings, times(1)).dispatchEvent(any(MouseEvent.class));
    }

    @Test
    public void testHandleAcceptButton() {
        view.setParentWidget(parent);

        view.setTagSet(new HashSet<>());
        view.getTagSet().add("myCustomTag");
        when(tagNamesComboBox.getValue()).thenReturn("myCustomTag");
        view.handleAcceptButton(null);

        // since same tags, not call to render badges should happen
        verify(view, never()).renderTagElementsBadges();
        assertTrue(view.getTagSet().contains("myCustomTag"));

        // test no empty tags can be added
        when(tagNamesComboBox.getValue()).thenReturn("");
        view.handleAcceptButton(null);
        verify(view, never()).renderTagElementsBadges();

        assertFalse(view.getTagSet().contains(""));

        // since empty no call to render badges
        verify(view, never()).renderTagElementsBadges();

        when(tagNamesComboBox.getValue()).thenReturn("myCustomTag");
        view.getTagSet().clear();
        view.getTagSet().addAll(Arrays.asList("internal", "outer", "tracked"));
        view.removeButtons = new HashMap<>();
        view.handleAcceptButton(null);
        verify(view, times(1)).renderTagElementsBadges();
        assertTrue(view.getTagSet().contains("myCustomTag"));

        // test badges editing
        when(view.getPreviousCustomValue()).thenReturn("myCustom");

        view.getTagSet().clear();
        view.getTagSet().addAll(Arrays.asList("internal", "outer", "tracked"));

        view.handleAcceptButton(null);
        verify(view, times(2)).renderTagElementsBadges();
        assertTrue(view.getTagSet().contains("myCustomTag"));
        assertFalse(view.getTagSet().contains("myCustom"));

        // test editing and same value as last
        view.getTagSet().clear();
        view.getTagSet().addAll(Arrays.asList("internal", "outer", "tracked"));

        when(view.getPreviousCustomValue()).thenReturn("myCustomTag");

        view.handleAcceptButton(null);
        verify(view, times(3)).renderTagElementsBadges();
        assertTrue(view.getTagSet().contains("myCustomTag"));

        // test not included in default tag set
        when(tagNamesComboBox.getValue()).thenReturn("input");

        view.getTagSet().clear();
        view.getTagSet().addAll(Arrays.asList("output"));

        when(view.getPreviousCustomValue()).thenReturn("myCustomTagX");

        view.handleAcceptButton(null);
        verify(view, times(4)).renderTagElementsBadges();
        assertTrue(view.getTagSet().contains("output"));
    }

    @Test
    public void testSetReadOnlyTrue() {
        view.setReadOnly(true);
        verify(deleteButton,
               times(1)).setEnabled(false);
        verify(dataTypeComboBox,
               times(1)).setReadOnly(true);
        verify(name,
               times(1)).setEnabled(false);
    }

    @Test
    public void testSetReadOnlyFalse() {
        view.setReadOnly(false);
        verify(deleteButton,
               times(1)).setEnabled(true);
        verify(dataTypeComboBox,
               times(1)).setReadOnly(false);
        verify(name,
               times(1)).setEnabled(true);
    }

    @Test
    public void testIsDuplicateID() {
        String id = "expected_id";
        view.isDuplicateID(id);
        verify(parent).isDuplicateID(id);
    }
}
