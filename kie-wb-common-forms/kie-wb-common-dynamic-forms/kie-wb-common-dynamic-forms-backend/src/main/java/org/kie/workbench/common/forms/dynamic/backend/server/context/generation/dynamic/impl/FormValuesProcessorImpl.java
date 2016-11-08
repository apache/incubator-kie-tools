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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.NestedFormFieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FormValuesProcessor;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;

@Dependent
public class FormValuesProcessorImpl implements FormValuesProcessor {

    protected Map<Class<? extends FieldDefinition>, FieldValueProcessor> fieldValueProcessors = new HashMap<>();

    @Inject
    public FormValuesProcessorImpl( Instance<FieldValueProcessor<? extends FieldDefinition, ?, ?>> installedProcessors ) {
        for ( FieldValueProcessor processor : installedProcessors ) {
            if ( processor instanceof NestedFormFieldValueProcessor ) {
                ( (NestedFormFieldValueProcessor) processor ).init( this );
            }
            fieldValueProcessors.put( processor.getSupportedField(), processor );
        }
    }

    @Override
    public Map<String, Object> readFormValues( FormDefinition form,
                                               Map<String, Object> rawValues,
                                               BackendFormRenderingContext context ) {
        final Map<String, Object> result = new HashMap<>();

        rawValues.forEach( ( String key, final Object value ) -> {

            FieldDefinition field = form.getFieldByBinding( key );

            Object fieldValue = value;

            if ( field != null ) {
                if ( value != null ) {
                    FieldValueProcessor processor = fieldValueProcessors.get( field.getClass() );

                    if ( processor != null ) {
                        fieldValue = processor.toFlatValue( field, value, context );
                    }
                }
                result.put( key, fieldValue );
            }
        } );

        return result;
    }

    @Override
    public Map<String, Object> writeFormValues( FormDefinition form,
                                                Map<String, Object> formValues,
                                                Map<String, Object> rawValues,
                                                BackendFormRenderingContext context ) {

        final Map<String, Object> result = new HashMap<>();

        formValues.forEach( ( key, value ) -> {

            FieldDefinition field = form.getFieldByBinding( key );

            if ( field != null ) {
                if ( value != null ) {
                    FieldValueProcessor processor = fieldValueProcessors.get( field.getClass() );

                    if ( processor != null ) {
                        value = processor.toRawValue( field, value, rawValues.get( key ), context );
                    }
                }
            }

            result.put( key, value );
        } );

        return result;
    }
}
