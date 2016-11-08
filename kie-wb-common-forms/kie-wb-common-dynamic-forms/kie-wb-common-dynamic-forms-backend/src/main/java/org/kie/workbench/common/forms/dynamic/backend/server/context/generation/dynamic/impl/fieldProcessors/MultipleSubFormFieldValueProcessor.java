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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.impl.relations.MultipleSubFormFieldDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class MultipleSubFormFieldValueProcessor extends NestedFormFieldValueProcessor<MultipleSubFormFieldDefinition, List, List<Map<String, Object>>> {
    private static final Logger logger = LoggerFactory.getLogger( MultipleSubFormFieldValueProcessor.class );

    @Override
    public Class<MultipleSubFormFieldDefinition> getSupportedField() {
        return MultipleSubFormFieldDefinition.class;
    }

    @Override
    public List<Map<String, Object>> toFlatValue( MultipleSubFormFieldDefinition field,
                                                  List rawValues,
                                                  BackendFormRenderingContext context ) {

        final FormDefinition creationForm = context.getRenderingContext().getAvailableForms().get(
                field.getCreationForm() );

        final FormDefinition editionForm = context.getRenderingContext().getAvailableForms().get(
                field.getEditionForm() );

        final List<Map<String, Object>> nestedRawValues = new ArrayList<>();

        if ( rawValues != null ) {
            rawValues.forEach( nestedValue -> {
                Map<String, Object> nestedRawValue = new HashMap<>();

                nestedRawValues.add( nestedRawValue );

                prepareNestedRawValues( nestedRawValue, creationForm, nestedValue );

                prepareNestedRawValues( nestedRawValue, editionForm, nestedValue );

            } );
        }

        List<Map<String, Object>> nestedFormValues = new ArrayList<>();

        nestedRawValues.forEach( rawValue -> {
            Map<String, Object> formValue = processor.readFormValues( creationForm, rawValue, context );

            formValue.putAll( processor.readFormValues( editionForm, rawValue, context ) );

            formValue.put( MapModelRenderingContext.FORM_ENGINE_OBJECT_IDX, nestedFormValues.size() );

            formValue.put( MapModelRenderingContext.FORM_ENGINE_EDITED_OBJECT, Boolean.FALSE );

            nestedFormValues.add( formValue );
        } );

        return nestedFormValues;
    }

    @Override
    public List toRawValue( MultipleSubFormFieldDefinition field,
                            List<Map<String, Object>> flatValues,
                            List originalValues,
                            BackendFormRenderingContext context ) {

        final List originalObjects = originalValues != null ? originalValues : new ArrayList<>();

        List fieldValue = new ArrayList();

        flatValues.forEach( nestedObjectValues -> {
            if ( nestedObjectValues.containsKey( MapModelRenderingContext.FORM_ENGINE_OBJECT_IDX ) ) {
                int originalPosition = (Integer) nestedObjectValues.get( MapModelRenderingContext.FORM_ENGINE_OBJECT_IDX );
                boolean edited = Boolean.TRUE.equals( nestedObjectValues.get( MapModelRenderingContext.FORM_ENGINE_EDITED_OBJECT ) );

                if ( originalPosition < originalObjects.size() ) {
                    Object originalObject = originalObjects.get( originalPosition );
                    if ( edited ) {
                        originalObject = writeObjectValues( originalObject,
                                                            nestedObjectValues,
                                                            field,
                                                            context );
                    }
                    fieldValue.add( originalObject );
                }
            } else {
                fieldValue.add( writeObjectValues( null, nestedObjectValues, field, context ) );
            }

        } );
        return fieldValue;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
