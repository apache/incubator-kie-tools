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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.model.Person;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.model.DefaultFieldTypeInfo;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.impl.relations.SubFormFieldDefinition;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith( MockitoJUnitRunner.class )
public class NestedFormFormValuesProcessorImplTest extends AbstractFormValuesProcessorImplTest {

    protected Person model;

    @Test
    public void testReadNestedData() {
        Map<String, Object> result = formValuesProcessor.readFormValues( renderingContext.getRootForm(),
                                                                         formData,
                                                                         context );

        assertNotNull( "Result cannot be null ", result );
        assertTrue( "Result must contain only one entry", result.size() == 1 );

        assertTrue( "Processed map must contain value for field 'person'", result.containsKey( "person" ) );
        assertNotNull( "Processed map must contain value for field 'person'", result.get( "person" ) );

        Map<String, Object> person = (Map<String, Object>) result.get( "person" );

        assertEquals( "Name must be 'Ned'", model.getName(), person.get( "name" ) );
        assertEquals( "LastName must be 'Stark'", model.getLastName(), person.get( "lastName" ) );

        Date birthday = (Date) person.get( "birthday" );

        assertEquals( "Date must be equal", model.getBirthday(), birthday );
    }

    @Test
    public void testWriteNestedModelWithExistingObject() {

        Date date = new Date();
        date.setTime( date.getTime() + 5000 );

        Map<String, Object> personValues = new HashMap<>();
        personValues.put( "name", "John" );
        personValues.put( "lastName", "Snow" );
        personValues.put( "birthday", date );

        Map<String, Object> formValues = new HashMap<>();

        formValues.put( "person", personValues );

        Map<String, Object> result = formValuesProcessor.writeFormValues( renderingContext.getRootForm(),
                                                                          formValues,
                                                                          context.getFormData(),
                                                                          context );

        assertNotNull( "Result cannot be null ", result );
        assertTrue( "Result must contain only one entry", result.size() == 1 );

        assertTrue( "Processed map must contain value for field 'person'", result.containsKey( "person" ) );
        assertNotNull( "Processed map must contain value for field 'person'", result.get( "person" ) );

        Object value = result.get( "person" );

        assertNotNull( "Value must not be null", value );
        assertTrue( "Value must be a Person object", value instanceof Person );
        assertEquals( "Value must be the same object that model", model, value );

        assertEquals( "Name must be 'John'", "John", model.getName() );
        assertEquals( "LastName must be 'Snow'", "Snow", model.getLastName() );

    }

    @Test
    public void testWriteNestedModelWithoutModelContentMarshaller() {
        testWriteNestedModelWithoutModel( true );
    }

    @Test
    public void testWriteNestedModelWithoutModelClasspath() {
        testWriteNestedModelWithoutModel( false );
    }

    protected void testWriteNestedModelWithoutModel( boolean classOnContentMarshaller ) {
        try {
            initContentMarshallerClassLoader( Person.class, classOnContentMarshaller );

            formData.remove( "person" );


            Map<String, Object> personValues = new HashMap<>();
            personValues.put( "id", 5555 );
            personValues.put( "name", "John" );
            personValues.put( "lastName", "Snow" );
            Date date = new Date();
            personValues.put( "birthday", date );

            Map<String, Object> formValues = new HashMap<>();

            formValues.put( "person", personValues );

            Map<String, Object> result = formValuesProcessor.writeFormValues( renderingContext.getRootForm(),
                                                                              formValues,
                                                                              context.getFormData(),
                                                                              context );

            assertNotNull( "Result cannot be null ", result );
            assertTrue( "Result must contain only one entry", result.size() == 1 );

            assertTrue( "Processed map must contain value for field 'person'", result.containsKey( "person" ) );
            assertNotNull( "Processed map must contain value for field 'person'", result.get( "person" ) );

            Object value = result.get( "person" );

            assertNotNull( "Value must not be null", value );
            assertTrue( "Value must be a Person object", value instanceof Person );

            Person personValue = (Person) value;

            assertEquals( "Name must be '5555'", new Integer( 5555 ), personValue.getId() );
            assertEquals( "Name must be 'John'", "John", personValue.getName() );
            assertEquals( "LastName must be 'Snow'", "Snow", personValue.getLastName() );
            assertEquals( "Date must be equal", date, personValue.getBirthday() );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }


    @Override
    protected MapModelRenderingContext generateRenderingContext() {
        FormDefinition form = new FormDefinition();
        FieldDefinition field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( Person.class.getName() ) );

        field.setName( "person" );
        field.setBinding( "person" );

        SubFormFieldDefinition subForm = (SubFormFieldDefinition) field;
        subForm.setNestedForm( Person.class.getName() );

        form.getFields().add( field );

        MapModelRenderingContext context = new MapModelRenderingContext();
        context.setRootForm( form );

        form = new FormDefinition();
        form.setId( Person.class.getName() );

        field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( String.class.getName() ) );
        field.setName( "name" );
        field.setBinding( "name" );
        form.getFields().add( field );

        field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( String.class.getName() ) );
        field.setName( "lastName" );
        field.setBinding( "lastName" );
        form.getFields().add( field );

        field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( Date.class.getName() ) );
        field.setName( "birthday" );
        field.setBinding( "birthday" );
        form.getFields().add( field );

        context.getAvailableForms().put( form.getId(), form );

        return context;
    }

    @Override
    protected Map<String, Object> generateFormData() {

        model = new Person( 1000, "Ned", "Stark", new Date() );

        Map<String, Object> data = new HashMap<>();

        data.put( "person", model );

        return data;
    }
}
