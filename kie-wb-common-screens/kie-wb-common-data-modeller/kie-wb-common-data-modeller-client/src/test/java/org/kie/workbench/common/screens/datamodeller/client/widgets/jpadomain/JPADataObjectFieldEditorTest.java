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

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.SequenceGeneratorValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.DomainEditorBaseTest;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.CascadeType;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.FetchMode;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.RelationType;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.mockito.Mock;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class JPADataObjectFieldEditorTest
                extends DomainEditorBaseTest {

    @Mock
    private JPADataObjectFieldEditorView view;

    protected JPADataObjectFieldEditor createFieldEditor() {
        JPADataObjectFieldEditor fieldEditor = new JPADataObjectFieldEditor( view,
                handlerRegistry,
                dataModelerEvent,
                commandBuilder );
        //emulate the @PostConstruct method invocation.
        fieldEditor.init();
        return fieldEditor;
    }

    @Test
    public void loadDataObjectFieldTest() {
        JPADataObjectFieldEditor fieldEditor = createFieldEditor();

        DataObject dataObject = context.getDataObject();
        ObjectProperty field = dataObject.getProperty( "field1" );
        //emulates selection of field1 in current context.
        context.setObjectProperty( field );

        //The domain editors typically reacts upon DataModelerContext changes.
        //when the context changes the editor will typically be reloaded.
        fieldEditor.onContextChange( context );

        verify( view, times( 2 ) ).loadPropertyEditorCategories( anyListOf( PropertyEditorCategory.class ) );
        assertFalse( fieldEditor.isReadonly() );
    }

    @Test
    public void valuesChangesTest() {
        JPADataObjectFieldEditor fieldEditor = createFieldEditor();

        DataObject dataObject = context.getDataObject();
        ObjectProperty field = dataObject.getProperty( "field1" );
        //emulates selection of field1 in current context.
        context.setObjectProperty( field );

        //The domain editors typically reacts upon DataModelerContext changes.
        //when the context changes the editor will typically be reloaded.
        fieldEditor.onContextChange( context );


        //emulates user interactions

        //changes related to the identifier category
        fieldEditor.onIdentifierFieldChange( createFieldInfo( JPADataObjectFieldEditorView.IDENTIFIER_FIELD,
                null ), "true" );

        fieldEditor.onGeneratedValueFieldChange(
                createFieldInfo( JPADataObjectFieldEditorView.GENERATED_VALUE_FIELD,
                        new Pair<String, Object>( "strategy", "SEQUENCE" ),
                        new Pair<String, Object>( "generator", "TheGeneratorName" ) ), "not_used" );

        fieldEditor.onSequenceGeneratorFieldChange(
                createFieldInfo( JPADataObjectFieldEditorView.SEQUENCE_GENERATOR_FIELD,
                        new Pair<String, Object>( SequenceGeneratorValueHandler.NAME, "TheGeneratorName" ),
                        new Pair<String, Object>( SequenceGeneratorValueHandler.SEQUENCE_NAME, "TheSequenceName" ),
                        new Pair<String, Object>( SequenceGeneratorValueHandler.INITIAL_VALUE, 1 ),
                        new Pair<String, Object>( SequenceGeneratorValueHandler.ALLOCATION_SIZE, 100 ) ), "not_used" );

        //the field should have been updated according to the values entered on the ui
        assertNotNull( field.getAnnotation( Id.class.getName() ) );
        assertNotNull( field.getAnnotation( GeneratedValue.class.getName() ) );
        assertEquals( "SEQUENCE", AnnotationValueHandler.getStringValue( field, GeneratedValue.class.getName(), "strategy" ) );
        assertEquals( "TheGeneratorName", AnnotationValueHandler.getStringValue( field, GeneratedValue.class.getName(), "generator" ) );
        assertNotNull( field.getAnnotation( SequenceGenerator.class.getName() ) );
        assertEquals( "TheGeneratorName", AnnotationValueHandler.getStringValue( field, SequenceGenerator.class.getName(), SequenceGeneratorValueHandler.NAME ) );
        assertEquals( "TheSequenceName", AnnotationValueHandler.getStringValue( field, SequenceGenerator.class.getName(), SequenceGeneratorValueHandler.SEQUENCE_NAME ) );
        assertEquals( 1, AnnotationValueHandler.getValue( field, SequenceGenerator.class.getName(), SequenceGeneratorValueHandler.INITIAL_VALUE ) );
        assertEquals( 100, AnnotationValueHandler.getValue( field, SequenceGenerator.class.getName(), SequenceGeneratorValueHandler.ALLOCATION_SIZE ) );

        //changes related to the column category
        fieldEditor.onColumnFieldChange(
                createFieldInfo( JPADataObjectFieldEditorView.COLUMN_NAME_FIELD,
                        new Pair<String, Object>( "name", "NewColumnName" ) ), "NewColumnName" );

        fieldEditor.onColumnFieldChange(
                createFieldInfo( JPADataObjectFieldEditorView.COLUMN_UNIQUE_FIELD,
                        new Pair<String, Object>( "unique", true ) ), "true" );

        fieldEditor.onColumnFieldChange(
                createFieldInfo( JPADataObjectFieldEditorView.COLUMN_NULLABLE_FIELD,
                        new Pair<String, Object>( "nullable", false ) ), "false" );

        fieldEditor.onColumnFieldChange(
                createFieldInfo( JPADataObjectFieldEditorView.COLUMN_INSERTABLE_FIELD,
                        new Pair<String, Object>( "insertable", false ) ), "false" );

        fieldEditor.onColumnFieldChange(
                createFieldInfo( JPADataObjectFieldEditorView.COLUMN_UPDATABLE_FIELD,
                        new Pair<String, Object>( "updatable", false ) ), "false" );

        //the field should have been updated according to the values entered on the ui
        assertNotNull( field.getAnnotation( Column.class.getName() ) );
        assertEquals( "NewColumnName", AnnotationValueHandler.getStringValue( field, Column.class.getName(), "name" ) );
        assertEquals( "true", AnnotationValueHandler.getStringValue( field, Column.class.getName(), "unique" ) );
        assertEquals( "false", AnnotationValueHandler.getStringValue( field, Column.class.getName(), "nullable" ) );
        assertEquals( "false", AnnotationValueHandler.getStringValue( field, Column.class.getName(), "insertable" ) );
        assertEquals( "false", AnnotationValueHandler.getStringValue( field, Column.class.getName(), "updatable" ) );

        //changes related to the relationship
        List<CascadeType> cascadeTypes = new ArrayList<CascadeType>( );
        cascadeTypes.add( CascadeType.ALL );
        fieldEditor.onRelationTypeFieldChange(
                createFieldInfo( JPADataObjectFieldEditorView.RELATIONSHIP_TYPE_FIELD,
                        new Pair<String, Object>( RelationshipAnnotationValueHandler.RELATION_TYPE , RelationType.ONE_TO_MANY ),
                        new Pair<String, Object>( RelationshipAnnotationValueHandler.CASCADE, cascadeTypes ),
                        new Pair<String, Object>( RelationshipAnnotationValueHandler.FETCH, FetchMode.EAGER ) ), "not_used") ;

        //the field should have been updated according to the values entered on the ui
        List<String> expectedCascadeTypes = new ArrayList<String>(  );
        expectedCascadeTypes.add( CascadeType.ALL.name() );
        assertNotNull( field.getAnnotation( OneToMany.class.getName() ) );
        assertEquals( expectedCascadeTypes, AnnotationValueHandler.getValue( field, OneToMany.class.getName(), RelationshipAnnotationValueHandler.CASCADE ) );
        assertEquals( FetchMode.EAGER.name(), AnnotationValueHandler.getValue( field, OneToMany.class.getName(), RelationshipAnnotationValueHandler.FETCH ) );
    }

    private DataModelerPropertyEditorFieldInfo createFieldInfo( String key, Pair<String, Object>... currentValues ) {
        DataModelerPropertyEditorFieldInfo fieldInfo = new DataModelerPropertyEditorFieldInfo( key, "not_used", null );
        if ( currentValues != null ) {
            for ( Pair<String, Object> value : currentValues ) {
                fieldInfo.setCurrentValue( value.getK1(), value.getK2() );
            }
        }
        fieldInfo.withKey( key );
        return fieldInfo;
    }
}