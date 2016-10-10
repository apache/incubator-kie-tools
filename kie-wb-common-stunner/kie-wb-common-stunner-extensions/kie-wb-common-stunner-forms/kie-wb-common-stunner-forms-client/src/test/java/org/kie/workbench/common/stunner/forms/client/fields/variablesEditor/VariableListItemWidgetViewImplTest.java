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

package org.kie.workbench.common.stunner.forms.client.fields.variablesEditor;

import com.google.gwt.event.dom.client.*;
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
import org.kie.workbench.common.stunner.forms.client.fields.model.Variable;
import org.kie.workbench.common.stunner.forms.client.fields.model.VariableRow;
import org.kie.workbench.common.stunner.forms.client.fields.widgets.ComboBox;
import org.kie.workbench.common.stunner.forms.client.fields.widgets.VariableNameTextBox;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.event.Event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class VariableListItemWidgetViewImplTest {

    private static final String VARIABLE_NAME = "variableName";
    private static final String CUST_DATA_TYPE_NAME = "custDataTypeName";
    private static final String DATA_TYPE_NAME = "dataTypeName";

    @GwtMock
    private DataBinder<VariableRow> variableRow;

    @GwtMock
    private VariableNameTextBox name;

    @GwtMock
    private Button deleteButton;

    private TextBox customDataType;

    private ValueListBox<String> dataType;

    private ComboBox dataTypeComboBox;

    private ComboBox processVarComboBox;

    @GwtMock
    private KeyDownEvent keyDownEvent;

    private Event<NotificationEvent> notification = mock( EventSourceMock.class );

    @Captor
    private ArgumentCaptor<KeyDownHandler> keyDownHandlerCaptor;

    @Captor
    private ArgumentCaptor<BlurHandler> blurHandlerCaptor;

    private VariableListItemWidgetViewImpl view;

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks( this );
        customDataType = mock( TextBox.class );
        dataType = mock( ValueListBox.class );
        dataTypeComboBox = mock( ComboBox.class );
        processVarComboBox = mock( ComboBox.class );
        view = mock( VariableListItemWidgetViewImpl.class );
        view.variableRow = variableRow;
        view.name = name;
        view.deleteButton = deleteButton;
        view.customDataType = customDataType;
        view.dataType = dataType;
        view.dataTypeComboBox = dataTypeComboBox;
        view.notification = notification;
        doCallRealMethod().when( view ).init();
        doCallRealMethod().when( view ).getCustomDataType();
        doCallRealMethod().when( view ).setCustomDataType( anyString() );
        doCallRealMethod().when( view ).getModel();
        doCallRealMethod().when( view ).setModel( any( VariableRow.class ) );
        doCallRealMethod().when( view ).getModelValue( any( ValueListBox.class ) );
        doCallRealMethod().when( view ).setTextBoxModelValue( any( TextBox.class ), anyString() );
        doCallRealMethod().when( view ).setListBoxModelValue( any( ValueListBox.class ), anyString() );
        doCallRealMethod().when( view ).getDataType();
        doCallRealMethod().when( view ).setDataType( anyString() );
        doCallRealMethod().when( view ).getVariableType();
        doCallRealMethod().when( view ).setParentWidget( any( VariablesEditorWidgetView.Presenter.class ) );
        doCallRealMethod().when( view ).isDuplicateName( anyString() );
        doCallRealMethod().when( view ).handleDeleteButton( any( ClickEvent.class ) );
        VariableRow row = new VariableRow();
        doReturn( row ).when( variableRow ).getModel();
    }

    @Test
    public void testSetProcessVarCustomDataType() {
        VariableRow row = new VariableRow();
        row.setName( VARIABLE_NAME );
        row.setCustomDataType( CUST_DATA_TYPE_NAME );
        row.setDataType( null );
        row.setVariableType( Variable.VariableType.PROCESS );
        doReturn( row ).when( variableRow ).getModel();
        view.setModel( row );
        verify( variableRow, times( 1 ) ).setModel( row );
        verify( deleteButton, times( 1 ) ).setIcon( IconType.TRASH );
        verify( customDataType, times( 1 ) ).setValue( CUST_DATA_TYPE_NAME );
        verify( dataType, times( 1 ) ).setValue( CUST_DATA_TYPE_NAME );
    }

    @Test
    public void testSetProcessVarDataType() {
        VariableRow row = new VariableRow();
        row.setName( VARIABLE_NAME );
        row.setCustomDataType( null );
        row.setDataType( DATA_TYPE_NAME );
        row.setVariableType( Variable.VariableType.PROCESS );
        doReturn( row ).when( variableRow ).getModel();
        view.setModel( row );
        verify( variableRow, times( 1 ) ).setModel( row );
        verify( deleteButton, times( 1 ) ).setIcon( IconType.TRASH );
        verify( customDataType, never() ).setValue( DATA_TYPE_NAME );
        verify( dataType, times( 1 ) ).setValue( DATA_TYPE_NAME );
    }

    @Test
    public void testSetTextBoxModelValueCustomDataType() {
        assertNull( view.getModel().getCustomDataType() );
        view.setTextBoxModelValue( customDataType, "abc" );
        assertEquals( "abc", view.getModel().getCustomDataType() );
        assertEquals( "abc", view.getModelValue( dataType ) );
    }

    @Test
    public void testSetListBoxModelValueDataType() {
        assertNull( view.getModel().getDataType() );
        view.setListBoxModelValue( dataType, "abc" );
        assertEquals( "abc", view.getModel().getDataType() );
        assertNull( view.getModel().getCustomDataType() );
        assertEquals( "abc", view.getModelValue( dataType ) );
    }

    @Test
    public void testDataTypeHandlerSpace() {
        view.init();
        verify( customDataType, times( 1 ) ).addKeyDownHandler( keyDownHandlerCaptor.capture() );
        KeyDownHandler handler = keyDownHandlerCaptor.getValue();
        doReturn( Integer.valueOf( ' ' ) ).when( keyDownEvent ).getNativeKeyCode();
        handler.onKeyDown( keyDownEvent );
        verify( keyDownEvent, times( 1 ) ).preventDefault();
    }

    @Test
    public void testDataTypeHandlerAlphabetical() {
        view.init();
        verify( customDataType, times( 1 ) ).addKeyDownHandler( keyDownHandlerCaptor.capture() );
        KeyDownHandler handler = keyDownHandlerCaptor.getValue();
        doReturn( Integer.valueOf( 'a' ) ).when( keyDownEvent ).getNativeKeyCode();
        handler.onKeyDown( keyDownEvent );
        verify( keyDownEvent, never() ).preventDefault();
    }

    @Test
    public void testNameBlurHandler() {
        VariablesEditorWidgetView.Presenter parent = mock( VariablesEditorWidgetView.Presenter.class );
        when( parent.isDuplicateName( anyString() ) ).thenReturn( true );
        doReturn( "anyName" ).when( name ).getText();
        view.setParentWidget( parent );
        view.init();
        verify( name, times( 1 ) ).addBlurHandler( blurHandlerCaptor.capture() );
        BlurHandler handler = blurHandlerCaptor.getValue();
        handler.onBlur( mock( BlurEvent.class ) );
        verify( parent, times( 1 ) ).isDuplicateName( "anyName" );
        verify( notification, times( 1 ) ).fire( new NotificationEvent( null, NotificationEvent.NotificationType.ERROR ) );
        verify( name, times( 1 ) ).setValue( "" );
    }

    @Test
    public void testHandleDeleteButton() {
        VariablesEditorWidgetView.Presenter widget = mock( VariablesEditorWidgetView.Presenter.class );
        VariableRow model = mock( VariableRow.class );
        when( view.getModel() ).thenReturn( model );
        view.setParentWidget( widget );
        view.handleDeleteButton( null );
        verify( widget ).removeVariable( model );
    }
}
