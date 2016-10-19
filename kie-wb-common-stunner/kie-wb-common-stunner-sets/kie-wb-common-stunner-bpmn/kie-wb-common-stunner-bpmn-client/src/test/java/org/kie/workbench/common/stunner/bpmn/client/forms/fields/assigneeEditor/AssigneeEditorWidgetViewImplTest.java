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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor;

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
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssigneeRow;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class AssigneeEditorWidgetViewImplTest {

    @Mock
    AssigneeEditorWidgetView.Presenter presenter;

    @GwtMock
    private Button button;

    @Mock
    private TableCellElement nameth;

    @GwtMock
    private ListWidget<AssigneeRow, AssigneeListItemWidgetViewImpl> assigneeRows;

    private AssigneeEditorWidgetViewImpl view;

    @Captor
    private ArgumentCaptor<List<AssigneeRow>> captor;

    @Captor
    private ArgumentCaptor<NotificationEvent> eventCaptor;

    private List<AssigneeRow> rows;

    protected Event<NotificationEvent> notification = mock( EventSourceMock.class );

    @Before
    public void setUp() {
        GwtMockito.initMocks( this );
        view = GWT.create( AssigneeEditorWidgetViewImpl.class );
        view.assigneeRows = assigneeRows;
        view.addButton = button;
        view.nameth = nameth;
        view.notification = notification;
        doCallRealMethod().when( view ).setAssigneeRows( any( List.class ) );
        doCallRealMethod().when( view ).init( any( AssigneeEditorWidgetView.Presenter.class ) );
        doCallRealMethod().when( view ).handleAddButton( any( ClickEvent.class ) );
        doCallRealMethod().when( view ).getAssigneeRows();
        doCallRealMethod().when( view ).getAssigneeWidget( anyInt() );
        doCallRealMethod().when( view ).getAssigneeRowsCount();
        rows = new ArrayList<AssigneeRow>();
        rows.add( new AssigneeRow( "user4", null ) );
        rows.add( new AssigneeRow( "user1", null ) );
    }

    @Test
    public void testInit() {
        view.init( presenter );
        verify( button, times( 1 ) ).setIcon( IconType.PLUS );
    }

    @Test
    public void testHandleAddVarButton() {
        view.init( presenter );
        view.handleAddButton( mock( ClickEvent.class ) );
        verify( presenter, times( 1 ) ).addAssignee();
    }

    @Test
    public void testGetAssigneeRows() {
        when( assigneeRows.getValue() ).thenReturn( rows );
        assertEquals( rows, view.getAssigneeRows() );
    }

    @Test
    public void testGetAssigneeCountEmpty() {
        when( assigneeRows.getValue() ).thenReturn( new ArrayList<AssigneeRow>() );
        assertEquals( 0, view.getAssigneeRowsCount() );
    }

    @Test
    public void testGetAssigneeCount() {
        when( assigneeRows.getValue() ).thenReturn( rows );
        assertEquals( 2, view.getAssigneeRowsCount() );
    }

    @Test
    public void testGetAssigneeWidget() {
        view.getAssigneeWidget( 0 );
        verify( assigneeRows ).getComponent( 0 );
    }

    @Test
    public void testGetAssigneeWidget2() {
        view.getAssigneeWidget( 123 );
        verify( assigneeRows ).getComponent( 123 );
    }
}
