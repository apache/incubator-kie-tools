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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.cm.roles;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.ChangeHandler;
import elemental2.dom.HTMLTableRowElement;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.KeyValueRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.kie.workbench.common.stunner.client.widgets.canvas.actions.IntegerTextBox;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.cm.roles.RolesEditorFieldRendererTest.ROLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class RolesListItemWidgetViewImplTest {

    public static final String ROLE_NAME = "role";

    private RolesListItemWidgetViewImpl tested;

    @Mock
    private VariableNameTextBox role;

    @Mock
    private IntegerTextBox cardinality;

    @Mock
    private EventSourceMock<NotificationEvent> notification;

    @Mock
    private Button deleteButton;

    @Captor
    private ArgumentCaptor<ChangeHandler> valueChangeHandler;

    @Mock
    private DataBinder<KeyValueRow> row;

    @Mock
    private RolesEditorWidgetView widget;

    @Mock
    private HTMLTableRowElement tableRow;

    @Before
    public void setUp() throws Exception {
        tested = spy(new RolesListItemWidgetViewImpl());
        tested.role = role;
        tested.cardinality = cardinality;
        tested.notification = notification;
        tested.deleteButton = deleteButton;
        tested.tableRow = tableRow;
        tested.row = row;
        tested.init();
        tested.setParentWidget(widget);
        when(widget.isDuplicateName(ROLE_NAME)).thenReturn(false);
        when(row.getModel()).thenReturn(ROLE);
        when(role.getText()).thenReturn(ROLE_NAME);
    }

    @Test
    public void init() {
        verify(role).setRegExp(eq(StringUtils.ALPHA_NUM_REGEXP), anyString(), anyString());
        verify(role).addChangeHandler(valueChangeHandler.capture());
        verify(cardinality).addChangeHandler(valueChangeHandler.capture());
        verify(deleteButton).setIcon(IconType.TRASH);

        //test handler
        ChangeHandler handler = valueChangeHandler.getValue();
        handler.onChange(null);
        verify(notification, never()).fire(any());
        verify(tested).notifyModelChanged();

        //now test with same value then should not call notifyModelChanged
        reset(tested);
        handler.onChange(null);
        verify(tested, never()).notifyModelChanged();

        //now test duplicating role name
        reset(tested);
        when(widget.isDuplicateName(ROLE_NAME)).thenReturn(true);
        handler.onChange(null);
        verify(notification).fire(any());
        verify(tested, never()).notifyModelChanged();
    }

    @Test
    public void getModel() {
        tested.getModel();
        verify(row).getModel();
    }

    @Test
    public void setModel() {
        tested.setModel(ROLE);
        verify(row).setModel(ROLE);
    }

    @Test
    public void getVariableType() {
        final Variable.VariableType variableType = tested.getVariableType();
        assertThat(variableType).isEqualByComparingTo(Variable.VariableType.PROCESS);
    }

    @Test
    public void setReadOnly() {
        tested.setReadOnly(true);
        verify(deleteButton).setEnabled(false);
        verify(role).setEnabled(false);
        verify(cardinality).setEnabled(false);
    }

    @Test
    public void isDuplicateName() {
        when(widget.isDuplicateName(ROLE_NAME)).thenReturn(true);
        final boolean duplicated = tested.isDuplicateName(ROLE_NAME);
        verify(widget).isDuplicateName(ROLE_NAME);
        assertThat(duplicated).isTrue();
    }

    @Test
    public void handleDeleteButton() {
        tested.handleDeleteButton();
        verify(widget).remove(ROLE);
    }

    @Test
    public void notifyModelChanged() {
        tested.notifyModelChanged();
        verify(widget).notifyModelChanged();
    }
}