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

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.KeyValueRow;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.cm.roles.RolesEditorFieldRendererTest.ROLE;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.cm.roles.RolesEditorFieldRendererTest.SERIALIZED_ROLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class RolesEditorWidgetViewImplTest {

    private RolesEditorWidgetViewImpl tested;

    @Mock
    private RolesEditorWidgetView.Presenter presenter;

    @Mock
    private Button addButton;

    @Mock
    private ListComponent<KeyValueRow, RolesListItemWidgetView> list;

    private List<KeyValueRow> roles;

    @Mock
    private RolesListItemWidgetView widget;

    @Mock
    private DataBinder<List<KeyValueRow>> binder;

    @Before
    public void setUp() throws Exception {
        tested = spy(new RolesEditorWidgetViewImpl());
        tested.addButton = addButton;
        tested.list = list;
        tested.binder = binder;
        tested.init(presenter);
        roles = spy(new ArrayList<>());
        roles.add(ROLE);
        when(presenter.deserialize(SERIALIZED_ROLE)).thenReturn(roles);
        when(presenter.serialize(any())).thenReturn(SERIALIZED_ROLE);
        when(list.getValue()).thenReturn(roles);
        when(list.getComponent(anyInt())).thenReturn(widget);
        when(binder.getModel()).thenReturn(roles);
    }

    @Test
    public void getSetValue() {
        String value = tested.getValue();
        assertThat(value).isNull();
        tested.setValue(SERIALIZED_ROLE);
        verify(tested).initView();
        verify(presenter).deserialize(SERIALIZED_ROLE);
        verify(binder).setModel(roles);
        verify(tested).setReadOnly(false);
    }

    @Test
    public void doSave() {
        tested.doSave();
        final ArgumentCaptor<List> rolesCaptor = ArgumentCaptor.forClass(List.class);
        verify(presenter).serialize(rolesCaptor.capture());
        assertThat(rolesCaptor.getValue()).isEqualTo(roles);
        verify(tested).setValue(SERIALIZED_ROLE, true);
    }

    @Test
    public void setReadOnly() {
        tested.setReadOnly(true);
        verify(addButton).setEnabled(false);
        verify(list).getComponent(0);
        verify(widget).setReadOnly(true);
    }

    @Test
    public void getRowsCount() {
        final int rowsCount = tested.getRowsCount();
        assertThat(rowsCount).isEqualTo(list.getValue().size());
    }

    @Test
    public void setRows() {
        tested.setRows(roles);
        verify(binder).setModel(roles);
    }

    @Test
    public void getRows() {
        assertThat(tested.getRows()).isEqualTo(roles);
    }

    @Test
    public void getWidget() {
        assertThat(tested.getWidget(0)).isEqualTo(widget);
    }

    @Test
    public void handleAddVarButton() {
        reset(roles);
        tested.handleAddVarButton();
        verify(roles).add(anyInt(), any(KeyValueRow.class));
        verify(tested).getWidget(roles.size() - 1);
        verify(widget).setParentWidget(tested);
    }

    @Test
    public void remove() {
        tested.remove(ROLE);
        verify(roles).remove(ROLE);
        verify(tested).doSave();
    }
}