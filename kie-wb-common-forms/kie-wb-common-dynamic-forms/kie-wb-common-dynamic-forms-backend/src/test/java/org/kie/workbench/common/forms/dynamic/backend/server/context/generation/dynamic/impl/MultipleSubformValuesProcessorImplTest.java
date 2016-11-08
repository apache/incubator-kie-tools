/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.model.Person;
import org.kie.workbench.common.forms.model.DefaultFieldTypeInfo;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.impl.relations.MultipleSubFormFieldDefinition;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith( MockitoJUnitRunner.class )
public class MultipleSubformValuesProcessorImplTest extends AbstractFormValuesProcessorImplTest {

    protected SimpleDateFormat sdf = new SimpleDateFormat( "dd-MM-yyyy" );

    protected List<Person> persons;

    @Test
    public void testReadNestedData() {
        doReadNestedData();
    }

    protected Map<String, Object> doReadNestedData() {
        Map<String, Object> result = formValuesProcessor.readFormValues( renderingContext.getRootForm(),
                                                                         formData,
                                                                         context );

        assertNotNull( "Result cannot be null ", result );
        assertTrue( "Result must contain only one entry", result.size() == 1 );

        assertTrue( "Processed map must contain value for field 'persons'", result.containsKey( "persons" ) );
        assertNotNull( "Processed map must contain value for field 'persons'", result.get( "persons" ) );

        List<Map<String, Object>> personMaps = (List<Map<String, Object>>) result.get( "persons" );

        assertEquals( "There must be 4 persons", 4, personMaps.size() );

        for ( int i = 0; i < personMaps.size(); i++ ) {
            Person person = persons.get( i );

            Map<String, Object> personMap = personMaps.get( i );

            assertEquals( "Id must be equal", person.getId(), personMap.get( "id" ) );
            assertEquals( "Name must be equal", person.getName(), personMap.get( "name" ) );
            assertEquals( "LastName must be equal", person.getLastName(), personMap.get( "lastName" ) );
            assertEquals( "Birthday must be equal", person.getBirthday(), personMap.get( "birthday" ) );
        }

        return result;
    }

    @Test
    public void testEditExistingObjects() {
        Map<String, Object> existingValues = doReadNestedData();

        List<Map<String, Object>> personMaps = (List<Map<String, Object>>) existingValues.get( "persons" );

        String[] names = new String[]{"Tyrion", "Jaime", "Cersei", "Tywin"};

        for ( int i = 0; i < personMaps.size(); i++ ) {
            Map<String, Object> person = personMaps.get( i );

            person.put( "name", names[i] );
            person.put( "lastName", "Lannister" );
            person.put( MapModelRenderingContext.FORM_ENGINE_EDITED_OBJECT, Boolean.TRUE );
        }

        Map<String, Object> result = formValuesProcessor.writeFormValues( renderingContext.getRootForm(),
                                                                          existingValues,
                                                                          context.getFormData(),
                                                                          context );

        assertNotNull( "Result cannot be null ", result );
        assertTrue( "Result must contain only one entry", result.size() == 1 );

        assertTrue( "Processed map must contain value for field 'person'", result.containsKey( "persons" ) );
        assertNotNull( "Processed map must contain value for field 'person'", result.get( "persons" ) );
        assertTrue( "Persons must be a List", result.get( "persons" ) instanceof List );

        List value = (List) result.get( "persons" );

        assertEquals( "There should be 4 persons", 4, value.size() );

        for ( int i = 0; i < persons.size(); i++ ) {
            assertEquals( "Name must be equal", names[i], persons.get( i ).getName() );
            assertEquals( "LastName must be equal", "Lannister", persons.get( i ).getLastName() );
        }
    }

    @Test
    public void testRemovingExistingInstances() {

        Map<String, Object> existingValues = doReadNestedData();

        List<Map<String, Object>> personMaps = (List<Map<String, Object>>) existingValues.get( "persons" );

        personMaps.remove( 0 );
        personMaps.remove( 0 );

        Map<String, Object> result = formValuesProcessor.writeFormValues( renderingContext.getRootForm(),
                                                                          existingValues,
                                                                          context.getFormData(),
                                                                          context );

        assertNotNull( "Result cannot be null ", result );
        assertTrue( "Result must contain only one entry", result.size() == 1 );

        assertTrue( "Processed map must contain value for field 'person'", result.containsKey( "persons" ) );
        assertNotNull( "Processed map must contain value for field 'person'", result.get( "persons" ) );
        assertTrue( "Persons must be a List", result.get( "persons" ) instanceof List );

        List<Person> value = (List) result.get( "persons" );

        assertEquals( "There should be 2 persons", 2, value.size() );

        String[] names = new String[]{"Rob", "John"};

        for ( int i = 0; i < value.size(); i++ ) {
            assertEquals( "Name must be equal", names[i], value.get( i ).getName() );
        }
    }

    @Test
    public void testWriteNestedModelWithoutModelContentMarshaller() {
        testCreateNestedModels( true );
    }

    @Test
    public void testCreateInstancesWithClassOnClassPath() {
        testCreateNestedModels( false );
    }

    protected void testCreateNestedModels( boolean classOnContentMarshaller ) {
        try {
            initContentMarshallerClassLoader( Person.class, classOnContentMarshaller );

            Map<String, Object> formValues = doReadNestedData();

            List<Map<String, Object>> personMaps = (List<Map<String, Object>>) formValues.get( "persons" );

            Map<String, Object> bran = new HashMap<>();
            bran.put( "id", 4 );
            bran.put( "name", "Bran" );
            bran.put( "lastName", "Stark" );
            bran.put( "birthday", sdf.parse( "14-01-2000" ) );

            Map<String, Object> sansa = new HashMap<>();
            sansa.put( "id", 5 );
            sansa.put( "name", "Sansa" );
            sansa.put( "lastName", "Stark" );
            sansa.put( "birthday", sdf.parse( "14-11-2005" ) );

            personMaps.add( bran );
            personMaps.add( sansa );

            Map<String, Object> result = formValuesProcessor.writeFormValues( renderingContext.getRootForm(),
                                                                              formValues,
                                                                              context.getFormData(),
                                                                              context );

            assertNotNull( "Result cannot be null ", result );
            assertTrue( "Result must contain only one entry", result.size() == 1 );

            assertTrue( "Processed map must contain value for field 'person'", result.containsKey( "persons" ) );
            assertNotNull( "Processed map must contain value for field 'person'", result.get( "persons" ) );
            assertTrue( "Persons must be a List", result.get( "persons" ) instanceof List );

            List<Person> value = (List) result.get( "persons" );

            assertEquals( "There should be 6 persons", 6, value.size() );

            String[] names = new String[]{"Ned", "Catelyn", "Rob", "John", "Bran", "Sansa"};

            for ( int i = 0; i < value.size(); i++ ) {
                assertEquals( "Name must be equal", names[i], value.get( i ).getName() );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }


    @Override
    protected MapModelRenderingContext generateRenderingContext() {

        FormDefinition creationForm = new FormDefinition();
        creationForm.setId( "person-creation" );

        FieldDefinition field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( Long.class.getName() ) );
        field.setName( "id" );
        field.setBinding( "id" );
        creationForm.getFields().add( field );

        field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( String.class.getName() ) );
        field.setName( "name" );
        field.setBinding( "name" );
        creationForm.getFields().add( field );

        field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( String.class.getName() ) );
        field.setName( "lastName" );
        field.setBinding( "lastName" );
        creationForm.getFields().add( field );

        field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( Date.class.getName() ) );
        field.setName( "birthday" );
        field.setBinding( "birthday" );
        creationForm.getFields().add( field );

        FormDefinition editionForm = new FormDefinition();
        editionForm.setId( "person-edition" );

        field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( Long.class.getName() ) );
        field.setName( "id" );
        field.setBinding( "id" );
        editionForm.getFields().add( field );

        field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( String.class.getName() ) );
        field.setName( "name" );
        field.setBinding( "name" );
        editionForm.getFields().add( field );

        field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( String.class.getName() ) );
        field.setName( "lastName" );
        field.setBinding( "lastName" );
        editionForm.getFields().add( field );

        field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( Date.class.getName() ) );
        field.setName( "birthday" );
        field.setBinding( "birthday" );
        editionForm.getFields().add( field );


        FormDefinition personListForm = new FormDefinition();
        personListForm.setId( "form" );

        field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( Person.class.getName(),
                                                                                 true,
                                                                                 false ) );
        field.setName( "persons" );
        field.setBinding( "persons" );

        MultipleSubFormFieldDefinition multpleSubForm = (MultipleSubFormFieldDefinition) field;

        multpleSubForm.setCreationForm( creationForm.getId() );
        multpleSubForm.setEditionForm( editionForm.getId() );

        personListForm.getFields().add( field );

        MapModelRenderingContext context = new MapModelRenderingContext();
        context.setRootForm( personListForm );
        context.getAvailableForms().put( creationForm.getId(), creationForm );
        context.getAvailableForms().put( editionForm.getId(), editionForm );

        return context;
    }

    @Override
    protected Map<String, Object> generateFormData() {

        persons = new ArrayList<>();

        try {
            persons.add( new Person( 0, "Ned", "Stark", sdf.parse( "24-02-1981" ) ) );
            persons.add( new Person( 1, "Catelyn", "Stark", sdf.parse( "04-05-1983" ) ) );
            persons.add( new Person( 2, "Rob", "Stark", sdf.parse( "12-04-2013" ) ) );
            persons.add( new Person( 3, "John", "Snow", sdf.parse( "21-05-2015" ) ) );
        } catch ( ParseException e ) {
            // swallow
        }

        Map<String, Object> data = new HashMap<>();

        data.put( "persons", persons );

        return data;
    }
}
