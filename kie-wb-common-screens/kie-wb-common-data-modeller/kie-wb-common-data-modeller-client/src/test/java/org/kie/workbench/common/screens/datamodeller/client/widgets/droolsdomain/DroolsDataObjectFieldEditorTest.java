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
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.DomainEditorBaseTest;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DroolsDataObjectFieldEditorTest
                extends DomainEditorBaseTest {

    @Mock
    private DroolsDataObjectFieldEditorView view;

    protected DroolsDataObjectFieldEditor createFieldEditor() {
        DroolsDataObjectFieldEditor fieldEditor = new DroolsDataObjectFieldEditor( view,
                handlerRegistry,
                dataModelerEvent,
                commandBuilder );
        return fieldEditor;
    }

    @Test
    public void loadDataObjectFieldTest() {

        DroolsDataObjectFieldEditor fieldEditor = createFieldEditor();

        DataObject dataObject = context.getDataObject();
        ObjectProperty field1 = dataObject.getProperty( "field1" );
        //emulates selection of field1 in current context.
        context.setObjectProperty( field1 );;

        //The domain editors typically reacts upon DataModelerContext changes.
        //when the context changes the editor will typically be reloaded.
        fieldEditor.onContextChange( context );

        //the view should be populated with the values from the field.

        verify( view, times( 1 ) ).setEquals( true );
        verify( view, times( 1 ) ).setPosition( "0" );

        assertFalse( fieldEditor.isReadonly() );
    }

    @Test
    public void valuesChangesTest() {

        DroolsDataObjectFieldEditor fieldEditor = createFieldEditor();

        DataObject dataObject = context.getDataObject();
        ObjectProperty field1 = dataObject.getProperty( "field1" );
        //emulates selection of field1 in current context.
        context.setObjectProperty( field1 );;

        //The domain editors typically reacts upon DataModelerContext changes.
        //when the context changes the editor will typically be reloaded.
        fieldEditor.onContextChange( context );

        //emulate the user input.
        when( view.getEquals() ).thenReturn( false );
        when( view.getPosition() ).thenReturn( "1" );

        //notify the presenter about the changes in the UI
        fieldEditor.onEqualsChange();
        fieldEditor.onPositionChange();

        assertNull( field1.getAnnotation( Key.class.getName() ) );
        assertEquals( 1, AnnotationValueHandler.getValue( field1, Position.class.getName(), "value" ) );

        verify( dataModelerEvent, times( 2 ) ).fire( any( DataModelerEvent.class ) );
    }
}
