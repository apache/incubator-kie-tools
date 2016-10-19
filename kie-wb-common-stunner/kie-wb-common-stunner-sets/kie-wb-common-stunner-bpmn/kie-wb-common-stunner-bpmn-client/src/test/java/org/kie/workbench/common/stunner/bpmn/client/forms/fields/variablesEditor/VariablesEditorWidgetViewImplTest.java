/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.event.Event;

import com.google.gwt.core.client.GWT;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class VariablesEditorWidgetViewImplTest {

    @Mock
    VariablesEditorWidgetView.Presenter presenter;

    @GwtMock
    private Button button;

    @Mock
    private TableCellElement nameth;

    @Mock
    private TableCellElement datatypeth;

    @GwtMock
    private ListWidget<VariableRow, VariableListItemWidgetViewImpl> variableRows;

    private VariablesEditorWidgetViewImpl view;

    @Captor
    private ArgumentCaptor<List<VariableRow>> captor;

    @Captor
    private ArgumentCaptor<NotificationEvent> eventCaptor;

    private List<VariableRow> rows;

    protected Event<NotificationEvent> notification = mock( EventSourceMock.class );

    @Before
    public void setUp() {
        GwtMockito.initMocks( this );
        view = GWT.create( VariablesEditorWidgetViewImpl.class );
        view.variableRows = variableRows;
        view.addVarButton = button;
        view.nameth = nameth;
        view.datatypeth = datatypeth;
        view.notification = notification;
        doCallRealMethod().when( view ).setVariableRows( any( List.class ) );
        doCallRealMethod().when( view ).init( any( VariablesEditorWidgetView.Presenter.class ) );
        doCallRealMethod().when( view ).handleAddVarButton( any( ClickEvent.class ) );
        doCallRealMethod().when( view ).getVariableRows();
        doCallRealMethod().when( view ).getVariableWidget( anyInt() );
        doCallRealMethod().when( view ).getVariableRowsCount();
        rows = new ArrayList<VariableRow>();
        rows.add( new VariableRow( Variable.VariableType.PROCESS, "varName", null, null ) );
        rows.add( new VariableRow( Variable.VariableType.PROCESS, "varName2", null, null ) );
    }

    @Test
    public void testInit() {
        view.init( presenter );
        verify( button, times( 1 ) ).setIcon( IconType.PLUS );
    }

    @Test
    public void testHandleAddVarButton() {
        view.init( presenter );
        view.handleAddVarButton( mock( ClickEvent.class ) );
        verify( presenter, times( 1 ) ).addVariable();
    }

    @Test
    public void testGetVariableRows() {
        when( variableRows.getValue() ).thenReturn( rows );
        assertEquals( rows, view.getVariableRows() );
    }

    @Test
    public void testGetVariablesCountEmpty() {
        when( variableRows.getValue() ).thenReturn( new ArrayList<VariableRow>() );
        assertEquals( 0, view.getVariableRowsCount() );
    }

    @Test
    public void testGetVariablesCount() {
        when( variableRows.getValue() ).thenReturn( rows );
        assertEquals( 2, view.getVariableRowsCount() );
    }

    @Test
    public void testGetVariableWidget() {
        view.getVariableWidget( 0 );
        verify( variableRows ).getComponent( 0 );
    }

    @Test
    public void testGetVariableWidget2() {
        view.getVariableWidget( 123 );
        verify( variableRows ).getComponent( 123 );
    }
}
