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

import com.google.gwt.core.client.GWT;
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
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the data get/set methods of AssigneeListItemWidget
 */
@RunWith(MockitoJUnitRunner.class)
public class AssigneeListItemWidgetTest {

    ValueListBox<String> name;

    TextBox customName;

    ComboBox nameComboBox;

    @GwtMock
    Button deleteButton;

    @GwtMock
    DataBinder<AssigneeRow> assignee;

    @Captor
    ArgumentCaptor<String> regExpCaptor;

    //@Spy  // - cannot make Spy because of GWT error
    //@InjectMocks // - cannot InjectMocks because of GWT error
    private AssigneeListItemWidgetViewImpl widget;

    @Before
    public void initTestCase() {
        GwtMockito.initMocks(this);
        name = mock(ValueListBox.class);
        customName = mock(TextBox.class);
        nameComboBox = mock(ComboBox.class);
        widget = GWT.create(AssigneeListItemWidgetViewImpl.class);
        AssigneeRow assigneeRow = new AssigneeRow();
        widget.name = name;
        widget.customName = customName;
        widget.nameComboBox = nameComboBox;
        widget.deleteButton = deleteButton;
        widget.assigneeRow = assignee;
        Mockito.doCallRealMethod().when(widget).setTextBoxModelValue(any(TextBox.class),
                                                                     anyString());
        Mockito.doCallRealMethod().when(widget).setListBoxModelValue(any(ValueListBox.class),
                                                                     anyString());
        Mockito.doCallRealMethod().when(widget).getModelValue(any(ValueListBox.class));
        Mockito.doCallRealMethod().when(widget).setNames(any(ListBoxValues.class));
        Mockito.doCallRealMethod().when(widget).setName(any(String.class));
        Mockito.doCallRealMethod().when(widget).getName();
        Mockito.doCallRealMethod().when(widget).setCustomName(any(String.class));
        Mockito.doCallRealMethod().when(widget).getCustomName();
        Mockito.doCallRealMethod().when(widget).init();
        Mockito.doCallRealMethod().when(widget).setModel(any(AssigneeRow.class));
        when(widget.getModel()).thenReturn(assigneeRow);
    }

    @Test
    public void testInitWidget() {
        widget.init();
        verify(widget,
               times(1)).init();
        verify(nameComboBox,
               times(1)).init(widget,
                              true,
                              name,
                              customName,
                              false,
                              false,
                              AssigneeListItemWidgetView.CUSTOM_PROMPT,
                              AssigneeListItemWidgetView.ENTER_TYPE_PROMPT);
        verify(customName,
               times(1)).addKeyDownHandler(any(KeyDownHandler.class));
    }

    @Test
    public void testSetTextBoxModelValue() {
        widget.setTextBoxModelValue(customName,
                                    "Georgina");
        verify(widget,
               times(1)).setCustomName("Georgina");
    }

    @Test
    public void testSetListBoxModelValue() {
        widget.setListBoxModelValue(name,
                                    "user3");
        verify(widget,
               times(1)).setName("user3");
    }

    @Test
    public void testSetModel() {
        widget.setModel(new AssigneeRow());
        verify(deleteButton).setIcon(IconType.TRASH);
        verify(widget).getCustomName();
        verify(widget).getName();
    }

    @Test
    public void testSetGetCustomName() {
        String customName = "Victoria";
        widget.setTextBoxModelValue(widget.customName,
                                    customName);
        String returnedCustomName = widget.getCustomName();
        assertEquals(customName,
                     returnedCustomName);
        String returnedCustomName2 = widget.getModelValue(widget.name);
        assertEquals(customName,
                     returnedCustomName2);
    }

    @Test
    public void testSetGetName() {
        String sName = "user2";
        widget.setListBoxModelValue(widget.name,
                                    sName);
        String returnedName1 = widget.getName();
        assertEquals(sName,
                     returnedName1);
        String returnedName2 = widget.getModelValue(widget.name);
        assertEquals(sName,
                     returnedName2);
    }

    @Test
    public void testSetNames() {
        ListBoxValues nameListBoxValues = new ListBoxValues(null,
                                                            null,
                                                            null);
        String sCustomName = "Julia";
        widget.setCustomName(sCustomName);
        widget.setNames(nameListBoxValues);
        verify(nameComboBox).setListBoxValues(nameListBoxValues);
        verify(nameComboBox).addCustomValueToListBoxValues(sCustomName,
                                                           "");
    }
}
