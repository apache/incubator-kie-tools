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

package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain;

import javax.persistence.Table;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.DomainEditorBaseTest;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.JPADomainAnnotations;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.mockito.Mock;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class JPADataObjectEditorTest
                extends DomainEditorBaseTest {

    @Mock
    private JPADataObjectEditorView view;

    protected JPADataObjectEditor createObjectEditor() {
        JPADataObjectEditor objectEditor = new JPADataObjectEditor( view,
                handlerRegistry,
                dataModelerEvent,
                commandBuilder );
        //emulate the @PostConstruct method invocation.
        objectEditor.init();
        return objectEditor;
    }

    @Test
    public void loadDataObjectTest() {
        JPADataObjectEditor objectEditor = createObjectEditor();

        //The domain editors typically reacts upon DataModelerContext changes.
        //when the context changes the editor will typically be reloaded.
        objectEditor.onContextChange( context );

        verify( view, times( 2 ) ).loadPropertyEditorCategories( anyListOf( PropertyEditorCategory.class ) );
        assertFalse( objectEditor.isReadonly() );
    }

    @Test
    public void tableNameChangeTest() {
        JPADataObjectEditor objectEditor = createObjectEditor();

        //load the editor
        objectEditor.onContextChange( context );

        DataObject dataObject = context.getDataObject();

        //emulates user interaction
        objectEditor.onTableNameChange( "NewTableName" );

        //the field should have been updated according to the values entered on the ui
        assertEquals( "NewTableName", AnnotationValueHandler.getStringValue( dataObject, Table.class.getName(), "name" ) );
    }

    @Test
    public void changeToPersistableAndAddIdFieldTest() {
        //the data object must be set as persistable and the missing id field must be created.
        changeToPersistableTest( PopupActions.YES );
    }

    @Test
    public void changeToPersistableAndDontAddIdFieldTest() {
        //the data object must be set as persistable and the missing id field mustn't be created.
        changeToPersistableTest( PopupActions.NO );
    }

    @Test
    public void changeToPersistableAndCancelTest() {
        //All the action must be cancelled.
        changeToPersistableTest( PopupActions.CANCEL );
    }

    private void changeToPersistableTest( PopupActions action ) {

        JPADataObjectEditor objectEditor = createObjectEditor();

        //load the editor
        objectEditor.onContextChange( context );

        //by construction the currently loaded data object org.test.TestObject1 is not persistable.
        DataObject dataObject = context.getDataObject();

        //emulate user interaction setting TestObject1 as persistable

        objectEditor.onEntityFieldChange( "true" );

        //The confirmation popup should have been raised since the DataObject has no @Id
        verify( view, times( 1 ) ).showYesNoCancelPopup( anyString(), anyString(),
                any( Command.class ), anyString(), eq( ButtonType.PRIMARY ),
                any( Command.class ), anyString(), eq( ButtonType.DANGER ),
                any( Command.class ), anyString(), eq( ButtonType.DEFAULT ) );

        if ( action == PopupActions.YES ) {
            //emulate the YES response form user input and the corresponding verifications.
            objectEditor.doEntityFieldChange( true, true );

            //the object should have been marked as @Entity
            assertNotNull( dataObject.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_ENTITY_ANNOTATION ) );

            ObjectProperty idField = dataObject.getProperty( "id" );

            //the Identifier field should have been created.
            assertNotNull( idField );
            assertNotNull( idField.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_ID_ANNOTATION ) );
            assertNotNull( idField.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_SEQUENCE_GENERATOR_ANNOTATION ) );
            assertNotNull( idField.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_GENERATED_VALUE_ANNOTATION ) );

        } else if ( action == PopupActions.NO ) {
            //emulate the NO response form user input and the corresponding verifications.
            objectEditor.doEntityFieldChange( true, false );

            //the object should have been marked as @Entity
            assertNotNull( dataObject.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_ENTITY_ANNOTATION ) );

            //but the @Id field was not created
            assertNull( dataObject.getProperty( "id" ) );

        } else if ( action == PopupActions.CANCEL ) {
            //emulate the CANCEL response form user input and the corresponding verifications.

            //no @Entity annotation or id field should have been created.
            assertNull( dataObject.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_ENTITY_ANNOTATION ) );
            assertNull( dataObject.getProperty( "id" ) );
        }
    }
}

