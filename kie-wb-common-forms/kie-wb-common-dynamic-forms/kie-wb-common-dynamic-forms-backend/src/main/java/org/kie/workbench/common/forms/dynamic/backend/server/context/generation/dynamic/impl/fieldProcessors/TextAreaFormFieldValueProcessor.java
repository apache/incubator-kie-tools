/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FieldValueProcessor;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;

@Dependent
public class TextAreaFormFieldValueProcessor implements FieldValueProcessor<TextAreaFieldDefinition, Object, String> {

    @Override
    public Class<TextAreaFieldDefinition> getSupportedField() {
        return TextAreaFieldDefinition.class;
    }

    @Override
    public String toFlatValue(TextAreaFieldDefinition field,
                              Object rawValue,
                              BackendFormRenderingContext context) {
        if(rawValue != null) {
            return rawValue.toString();
        }
        return null;
    }

    @Override
    public Object toRawValue(TextAreaFieldDefinition field,
                             String flatValue,
                             Object originalValue,
                             BackendFormRenderingContext context) {
        return flatValue;
    }
}
