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
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.model.DefaultFieldTypeInfo;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith( MockitoJUnitRunner.class )
public class SimpleFieldTypesFormValuesProcessorImplTest extends AbstractFormValuesProcessorImplTest {

    @Test
    public void readSimpleData() {
        Map<String, Object> result = formValuesProcessor.readFormValues( renderingContext.getRootForm(),
                                                                         formData,
                                                                         context );

        assertNotNull( "Result cannot be null ", result );
        assertTrue( "Result cannot be empty ", !result.isEmpty() );

        formData.forEach( ( key, value ) -> {
            assertTrue( "Processed map must contain value for field '" + key + "'", result.containsKey( key ) );
            assertNotNull( "Processed map must contain value for field '" + key + "'", result.get( key ) );
            assertEquals( "Processed value must be equal to formValue", value, result.get( key ) );
        } );
    }

    @Test
    public void testSimpleDataForm() {
        Map<String, Object> formValues = new HashMap<>();

        Date date = new Date();
        date.setTime( date.getTime() + 5000 );

        formValues.put( "string", "newString" );
        formValues.put( "integer", 3 );
        formValues.put( "date", date );
        formValues.put( "boolean", Boolean.FALSE );

        Map<String, Object> result = formValuesProcessor.writeFormValues( renderingContext.getRootForm(),
                                                                          formValues,
                                                                          context.getFormData(),
                                                                          context );

        assertNotNull( "Result cannot be null ", result );
        assertTrue( "Result cannot be empty ", !result.isEmpty() );

        formValues.forEach( ( key, value ) -> {
            assertTrue( "Processed map must contain value for field '" + key + "'", result.containsKey( key ) );
            assertNotNull( "Processed map must contain value for field '" + key + "'", result.get( key ) );
            assertEquals( "Processed value must be equal to formValue", value, result.get( key ) );
            assertNotEquals( "Processed value must not be equal to the original value",
                             value,
                             context.getFormData().get( key ) );
        } );
    }

    @Override
    protected MapModelRenderingContext generateRenderingContext() {
        FormDefinition form = new FormDefinition();
        FieldDefinition field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( String.class.getName() ) );

        field.setName( "string" );
        field.setBinding( "string" );

        form.getFields().add( field );

        field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( Integer.class.getName() ) );
        ;

        field.setName( "integer" );
        field.setBinding( "integer" );

        form.getFields().add( field );

        field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( Date.class.getName() ) );

        field.setName( "date" );
        field.setBinding( "date" );

        form.getFields().add( field );

        field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( Boolean.class.getName() ) );

        field.setName( "boolean" );
        field.setBinding( "boolean" );

        form.getFields().add( field );

        MapModelRenderingContext context = new MapModelRenderingContext();
        context.setRootForm( form );

        return context;
    }

    @Override
    protected Map<String, Object> generateFormData() {
        Map<String, Object> data = new HashMap<>();

        data.put( "string", "string" );
        data.put( "integer", 1 );
        data.put( "date", new Date() );
        data.put( "boolean", Boolean.TRUE );

        return data;
    }
}
