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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.impl.relations.SubFormFieldDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class SubFormFieldValueProcessor extends NestedFormFieldValueProcessor<SubFormFieldDefinition, Object, Map<String, Object>> {

    private static final Logger logger = LoggerFactory.getLogger( SubFormFieldValueProcessor.class );

    @Override
    public Class<SubFormFieldDefinition> getSupportedField() {
        return SubFormFieldDefinition.class;
    }

    @Override
    public Map<String, Object> toFlatValue( SubFormFieldDefinition field,
                                            Object rawValue,
                                            BackendFormRenderingContext context ) {

        FormDefinition nestedForm = context.getRenderingContext().getAvailableForms().get( field.getNestedForm() );

        Map<String, Object> nestedRawValues = new HashMap<>();

        prepareNestedRawValues( nestedRawValues, nestedForm, rawValue );

        return processor.readFormValues( nestedForm, nestedRawValues, context );
    }

    @Override
    public Object toRawValue( SubFormFieldDefinition field,
                              Map<String, Object> flatValue,
                              Object originalValue,
                              BackendFormRenderingContext context ) {

        FormDefinition nestedForm = context.getRenderingContext().getAvailableForms().get( field.getNestedForm() );

        Map<String, Object> nestedValues = processor.writeFormValues( nestedForm, flatValue, new HashMap<>(), context );

        return writeObjectValues( originalValue, nestedValues, field, context );
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
