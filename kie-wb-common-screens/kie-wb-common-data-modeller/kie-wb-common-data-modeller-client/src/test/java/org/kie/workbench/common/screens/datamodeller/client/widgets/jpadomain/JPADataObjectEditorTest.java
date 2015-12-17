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

import javax.persistence.Entity;
import javax.persistence.Table;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.DomainEditorBaseTest;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.mockito.Mock;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;

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
    public void valuesChangesTest() {
        JPADataObjectEditor objectEditor = createObjectEditor();

        //load the editor
        objectEditor.onContextChange( context );

        DataObject dataObject = context.getDataObject();

        //emulates user interaction
        objectEditor.onEntityFieldChange( "true" );
        objectEditor.onTableNameChange( "NewTableName" );

        //the field should have been updated according to the values entered on the ui
        assertNotNull( dataObject.getAnnotation( Entity.class.getName() ) );
        assertEquals( "NewTableName", AnnotationValueHandler.getStringValue( dataObject, Table.class.getName(), "name" ) );
    }

}

