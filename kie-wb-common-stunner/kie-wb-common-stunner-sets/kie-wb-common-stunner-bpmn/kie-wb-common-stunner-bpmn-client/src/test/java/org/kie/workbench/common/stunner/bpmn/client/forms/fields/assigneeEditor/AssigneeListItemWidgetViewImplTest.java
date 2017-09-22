/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor;

import javax.enterprise.event.Event;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssigneeRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssigneeListItemWidgetViewImplTest {

    private static final String ASSIGNEE_NAME = "assigneeName";
    private static final String CUSTOM_NAME = "customName";

    @GwtMock
    private DataBinder<AssigneeRow> assigneeRow;

    @GwtMock
    private Button deleteButton;

    private TextBox customName;

    private ValueListBox<String> name;

    private ComboBox nameComboBox;

    @GwtMock
    private KeyDownEvent keyDownEvent;

    private Event<NotificationEvent> notification = mock(EventSourceMock.class);

    @Captor
    private ArgumentCaptor<KeyDownHandler> keyDownHandlerCaptor;

    @Captor
    private ArgumentCaptor<BlurHandler> blurHandlerCaptor;

    private AssigneeListItemWidgetViewImpl view;

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks(this);
        customName = mock(TextBox.class);
        name = mock(ValueListBox.class);
        nameComboBox = mock(ComboBox.class);
        view = mock(AssigneeListItemWidgetViewImpl.class);
        view.assigneeRow = assigneeRow;
        view.name = name;
        view.deleteButton = deleteButton;
        view.customName = customName;
        view.name = name;
        view.nameComboBox = nameComboBox;
        view.notification = notification;
        doCallRealMethod().when(view).init();
        doCallRealMethod().when(view).getCustomName();
        doCallRealMethod().when(view).setCustomName(anyString());
        doCallRealMethod().when(view).getModel();
        doCallRealMethod().when(view).setModel(any(AssigneeRow.class));
        doCallRealMethod().when(view).getModelValue(any(ValueListBox.class));
        doCallRealMethod().when(view).setTextBoxModelValue(any(TextBox.class),
                                                           anyString());
        doCallRealMethod().when(view).setListBoxModelValue(any(ValueListBox.class),
                                                           anyString());
        doCallRealMethod().when(view).getName();
        doCallRealMethod().when(view).setName(anyString());
        doCallRealMethod().when(view).setParentWidget(any(AssigneeEditorWidgetView.Presenter.class));
        doCallRealMethod().when(view).isDuplicateName(anyString());
        doCallRealMethod().when(view).handleDeleteButton(any(ClickEvent.class));
        AssigneeRow row = new AssigneeRow();
        doReturn(row).when(assigneeRow).getModel();
    }

    @Test
    public void testSetAssigneeCustomName() {
        AssigneeRow row = new AssigneeRow();
        row.setName(ASSIGNEE_NAME);
        row.setCustomName(CUSTOM_NAME);
        row.setName(null);
        doReturn(row).when(assigneeRow).getModel();
        view.setModel(row);
        verify(assigneeRow,
               times(1)).setModel(row);
        verify(deleteButton,
               times(1)).setIcon(IconType.TRASH);
        verify(customName,
               times(1)).setValue(CUSTOM_NAME);
        verify(name,
               times(1)).setValue(CUSTOM_NAME);
    }

    @Test
    public void testSetAssigneeName() {
        AssigneeRow row = new AssigneeRow();
        row.setName(ASSIGNEE_NAME);
        row.setCustomName(null);
        row.setName(ASSIGNEE_NAME);
        doReturn(row).when(assigneeRow).getModel();
        view.setModel(row);
        verify(assigneeRow,
               times(1)).setModel(row);
        verify(deleteButton,
               times(1)).setIcon(IconType.TRASH);
        verify(customName,
               never()).setValue(ASSIGNEE_NAME);
        verify(name,
               times(1)).setValue(ASSIGNEE_NAME);
    }

    @Test
    public void testSetTextBoxModelValueCustomName() {
        assertNull(view.getModel().getCustomName());
        view.setTextBoxModelValue(customName,
                                  "abc");
        assertEquals("abc",
                     view.getModel().getCustomName());
        assertEquals("abc",
                     view.getModelValue(name));
    }

    @Test
    public void testSetListBoxModelValueName() {
        assertNull(view.getModel().getName());
        view.setListBoxModelValue(name,
                                  "abc");
        assertEquals("abc",
                     view.getModel().getName());
        assertNull(view.getModel().getCustomName());
        assertEquals("abc",
                     view.getModelValue(name));
    }

    @Test
    public void testNameHandlerSpace() {
        view.init();
        verify(customName,
               times(1)).addKeyDownHandler(keyDownHandlerCaptor.capture());
        KeyDownHandler handler = keyDownHandlerCaptor.getValue();
        doReturn(Integer.valueOf(' ')).when(keyDownEvent).getNativeKeyCode();
        handler.onKeyDown(keyDownEvent);
        verify(keyDownEvent,
               times(1)).preventDefault();
    }

    @Test
    public void testNameHandlerAlphabetical() {
        view.init();
        verify(customName,
               times(1)).addKeyDownHandler(keyDownHandlerCaptor.capture());
        KeyDownHandler handler = keyDownHandlerCaptor.getValue();
        doReturn(Integer.valueOf('a')).when(keyDownEvent).getNativeKeyCode();
        handler.onKeyDown(keyDownEvent);
        verify(keyDownEvent,
               never()).preventDefault();
    }

    @Test
    public void testHandleDeleteButton() {
        AssigneeEditorWidgetView.Presenter widget = mock(AssigneeEditorWidgetView.Presenter.class);
        AssigneeRow model = mock(AssigneeRow.class);
        when(view.getModel()).thenReturn(model);
        view.setParentWidget(widget);
        view.handleDeleteButton(null);
        verify(widget).removeAssignee(model);
    }
}
