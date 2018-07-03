/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling;

import java.util.function.Supplier;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;

@Dependent
public class TextAreaFormFieldValueMarshaller extends AbstractFieldValueMarshaller<Object, String, TextAreaFieldDefinition> {

    @Override
    public Class<TextAreaFieldDefinition> getSupportedField() {
        return TextAreaFieldDefinition.class;
    }

    @Override
    public String toFlatValue() {
        if (originalValue != null) {
            return originalValue.toString();
        }
        return null;
    }

    @Override
    public Object toRawValue(String flatValue) {
        return flatValue;
    }

    @Override
    public Supplier<FieldValueMarshaller<Object, String, TextAreaFieldDefinition>> newInstanceSupplier() {
        return TextAreaFormFieldValueMarshaller::new;
    }
}
