/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.droolsdomain;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;
import org.kie.api.definition.type.TypeSafe;
import org.kie.api.remote.Remotable;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.DomainEditorBaseTest;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DroolsDataObjectEditorTest
                extends DomainEditorBaseTest {

    @Mock
    private DroolsDataObjectEditorView view;

    protected DroolsDataObjectEditor createObjectEditor() {
        DroolsDataObjectEditor objectEditor = new DroolsDataObjectEditor( view,
                handlerRegistry,
                dataModelerEvent,
                commandBuilder,
                validatorService );

        return objectEditor;
    }

    @Test
    public void loadDataObjectTest() {
        DroolsDataObjectEditor objectEditor = createObjectEditor();

        //The domain editors typically reacts upon DataModelerContext changes.
        //when the context changes the editor will typically be reloaded.
        objectEditor.onContextChange( context );

        DataObject dataObject = context.getDataObject();

        //the view should be populated with the values from the dataObject.
        verify( view, times( 1 ) ).setTypeSafe( "true" );
        verify( view, times( 1 ) ).setPropertyReactive( true );
        verify( view, times( 1 ) ).setRole( "EVENT" );
        verify( view, times( 1 ) ).setTimeStampField( "field2" );
        verify( view, times( 1 ) ).setDurationField( "field3" );
        verify( view, times( 1 ) ).setExpires( "1h" );
        verify( view, times( 1 ) ).setRemotable( true );

        assertFalse( objectEditor.isReadonly() );

    }

    @Test
    public void valuesChangeTest() {

        DroolsDataObjectEditor objectEditor = createObjectEditor();

        //load the editor
        objectEditor.onContextChange( context );

        //emulate the user input
        when( view.getTypeSafe() ).thenReturn( "false" );
        when( view.getPropertyReactive() ).thenReturn( false );
        when( view.getRole() ).thenReturn( null );
        when( view.getTimeStampField() ).thenReturn( NEW_FIELD_NAME );
        when( view.getDurationField() ).thenReturn( NEW_FIELD_NAME );
        when( view.getExpires() ).thenReturn( "100d" ); //new timer expression
        when( view.getRemotable() ).thenReturn( false );

        when( validationService.isTimerIntervalValid( "100d" ) ).thenReturn( true );

        //notify the presenter about the changes in the UI
        objectEditor.onTypeSafeChange();
        objectEditor.onPropertyReactiveChange();
        objectEditor.onRoleChange();
        objectEditor.onTimeStampFieldChange();
        objectEditor.onDurationFieldChange();
        objectEditor.onExpiresChange();
        objectEditor.onRemotableChange();

        //After the changes has been processed by the presenter the dataObject should have been populated with the new values.
        DataObject dataObject = context.getDataObject();

        assertEquals( "false", AnnotationValueHandler.getStringValue( dataObject, TypeSafe.class.getName(), "value" ) );
        assertNull( dataObject.getAnnotation( PropertyReactive.class.getName() ) );
        assertNull( dataObject.getAnnotation( Role.class.getName() ) );
        assertEquals( NEW_FIELD_NAME, AnnotationValueHandler.getStringValue( dataObject, Timestamp.class.getName(), "value" ) );
        assertEquals( NEW_FIELD_NAME, AnnotationValueHandler.getStringValue( dataObject, Duration.class.getName(), "value" ) );
        assertEquals( "100d", AnnotationValueHandler.getStringValue( dataObject, Expires.class.getName(), "value" ) );
        assertNull( dataObject.getAnnotation( Remotable.class.getName() ) );

        verify( dataModelerEvent, times( 7 ) ).fire( any( DataModelerEvent.class ) );

    }
}
