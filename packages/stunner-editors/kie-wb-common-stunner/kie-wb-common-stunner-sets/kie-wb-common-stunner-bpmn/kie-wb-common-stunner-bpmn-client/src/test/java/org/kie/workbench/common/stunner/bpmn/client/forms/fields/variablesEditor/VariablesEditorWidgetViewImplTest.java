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
import java.util.List;

import javax.enterprise.event.Event;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class VariablesEditorWidgetViewImplTest {

    protected static final String VARIABLES = "employee:java.lang.String,reason:java.lang.String,performance:java.lang.String";

    protected static final List<String> DATATYPES = Arrays.asList("Boolean",
                                                                  "Float",
                                                                  "Integer",
                                                                  "Object",
                                                                  "String",
                                                                  "org.test.Itinerary",
                                                                  "org.test.Journey");

    protected static final List<String> DATATYPE_DISPLAYNAMES = Arrays.asList("Boolean",
                                                                              "Float",
                                                                              "Integer",
                                                                              "Object",
                                                                              "String",
                                                                              "Itinerary [org.test]",
                                                                              "Journey [org.test]");

    @Mock
    VariablesEditorWidgetView.Presenter presenter;

    @GwtMock
    private Button button;

    @Mock
    private TableCellElement nameth;

    @Mock
    private TableCellElement datatypeth;

    @Mock
    private RefreshFormPropertiesEvent refreshFormsEvent;

    @GwtMock
    private ListWidget<VariableRow, VariableListItemWidgetViewImpl> variableRows;

    @GwtMock
    private TableCellElement tagsth;

    @GwtMock
    private VariablesEditorWidgetViewImpl view;

    @Captor
    private ArgumentCaptor<List<VariableRow>> captor;

    @Captor
    private ArgumentCaptor<NotificationEvent> eventCaptor;

    private List<VariableRow> rows;

    protected Event<NotificationEvent> notification = mock(EventSourceMock.class);

    @Before
    public void setUp() {
        GwtMockito.initMocks(this);
        view.variableRows = variableRows;
        view.addVarButton = button;
        view.nameth = nameth;
        view.datatypeth = datatypeth;
        view.notification = notification;

        doCallRealMethod().when(view).setVariableRows(any(List.class));
        doCallRealMethod().when(view).init(any(VariablesEditorWidgetView.Presenter.class));
        doCallRealMethod().when(view).handleAddVarButton(any(ClickEvent.class));
        doCallRealMethod().when(view).getVariableRows();
        doCallRealMethod().when(view).getVariableWidget(anyInt());
        doCallRealMethod().when(view).getVariableRowsCount();
        doCallRealMethod().when(view).setValue(anyString(),
                                               anyBoolean());
        doCallRealMethod().when(view).doSetValue(anyString(),
                                                 anyBoolean(),
                                                 anyBoolean());
        doCallRealMethod().when(view).setDataTypes(anyListOf(String.class),
                                                   anyListOf(String.class));
        doCallRealMethod().when(view).setReadOnly(anyBoolean());
        doCallRealMethod().when(view).checkTagsNotEnabled();

        doCallRealMethod().when(view).setTagsth(any());
        doCallRealMethod().when(view).setTagsDisabled(anyBoolean());
        doCallRealMethod().when(view).addDataType(anyString(), anyString());
        doNothing().when(view).doAddDataType(anyString(), anyString());

        view.setTagsth(tagsth);

        rows = new ArrayList<VariableRow>();
        rows.add(new VariableRow(Variable.VariableType.PROCESS,
                                 "varName",
                                 null,
                                 null));
        rows.add(new VariableRow(Variable.VariableType.PROCESS,
                                 "varName2",
                                 null,
                                 null));
    }

    @Test
    public void testInit() {
        view.init(presenter);
        verify(button,
               times(1)).setIcon(IconType.PLUS);
    }

    @Test
    public void testHandleAddVarButton() {
        view.init(presenter);
        view.handleAddVarButton(mock(ClickEvent.class));
        verify(presenter,
               times(1)).addVariable();
    }

    @Test
    public void testGetVariableRows() {
        when(variableRows.getValue()).thenReturn(rows);
        assertEquals(rows,
                     view.getVariableRows());
    }

    @Test
    public void testGetVariablesCountEmpty() {
        when(variableRows.getValue()).thenReturn(new ArrayList<VariableRow>());
        assertEquals(0,
                     view.getVariableRowsCount());
    }

    @Test
    public void testGetVariablesCount() {
        when(variableRows.getValue()).thenReturn(rows);
        assertEquals(2,
                     view.getVariableRowsCount());
    }

    @Test
    public void testGetVariableWidget() {
        view.getVariableWidget(0);
        verify(variableRows).getComponent(0);
    }

    @Test
    public void testGetVariableWidget2() {
        view.getVariableWidget(123);
        verify(variableRows).getComponent(123);
    }

    @Test
    public void testSetValue() {
        view.init(presenter);
        view.setDataTypes(DATATYPES,
                          DATATYPE_DISPLAYNAMES);

        view.setValue(VARIABLES,
                      true);
        verify(view,
               times(1)).doSetValue(VARIABLES,
                                    true,
                                    false);
    }

    @Test
    public void testDoSetValue() {
        view.doSetValue(VARIABLES,
                        true,
                        false);
        verify(view,
               times(0)).initView();

        view.doSetValue(VARIABLES,
                        true,
                        true);
        verify(view,
               times(1)).initView();

        view.doSetValue(VARIABLES,
                        false,
                        true);
        verify(view,
               times(2)).initView();
    }

    @Test
    public void testSetValueNull() {
        view.setValue("MyValue", false);
        verify(view,
               times(0)).initView();
    }

    @Test
    public void testOnFormRefresh() {
        doCallRealMethod().when(view).onRefreshFormPropertiesEvent(any());
        view.onRefreshFormPropertiesEvent(refreshFormsEvent);
        verify(view,
               times(0)).initView();

        view.refreshFormPropertiesEvent = refreshFormsEvent;
        view.onRefreshFormPropertiesEvent(refreshFormsEvent);
        verify(view,
               times(0)).initView();
    }

    @Test
    public void testSetReadOnlyTrue() {
        view.setReadOnly(true);
        verify(button,
               times(1)).setEnabled(false);
        for (int i = 0; i < variableRows.getValue().size(); i++) {
            verify(view.getVariableWidget(i),
                   times(1)).setReadOnly(true);
        }
    }

    @Test
    public void testSetReadOnlyFalse() {
        view.setReadOnly(false);
        verify(button,
               times(1)).setEnabled(true);
        for (int i = 0; i < variableRows.getValue().size(); i++) {
            verify(view.getVariableWidget(i),
                   times(1)).setReadOnly(false);
        }
    }

    @Test
    public void testCheckTagsNotEnabled() {
        view.setTagsDisabled(false);
        view.checkTagsNotEnabled();
        verify(tagsth, never()).removeFromParent();

        view.setTagsDisabled(true);
        view.checkTagsNotEnabled();
        verify(tagsth, times(1)).removeFromParent();
    }

    @Test
    public void testAddDataType() {
        view.addDataType(null, "oldType");
        verify(view, times(0)).doAddDataType(anyString(), anyString());

        view.addDataType("", "oldType");
        verify(view, times(0)).doAddDataType(anyString(), anyString());

        view.addDataType("newType", "oldType");
        verify(view, times(1)).doAddDataType(anyString(), anyString());
    }
}
