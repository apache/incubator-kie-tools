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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.models;

import java.util.Map;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.AbstractFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshallerRegistry;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;

@Dependent
public class SubFormFieldValueMarshaller extends AbstractFieldValueMarshaller<Object, Map<String, Object>, SubFormFieldDefinition> {

    private FieldValueMarshallerRegistry registry;

    private ModelMarshaller modelMarshaller;

    @Inject
    public void setRegistry(FieldValueMarshallerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void init(Object originalValue, SubFormFieldDefinition fieldDefinition, FormDefinition currentForm, BackendFormRenderingContext currentContext) {
        super.init(originalValue, fieldDefinition, currentForm, currentContext);

        modelMarshaller = new ModelMarshaller(registry, originalValue, context.getRenderingContext().getAvailableForms().get(fieldDefinition.getNestedForm()), context);
    }

    @Override
    public Map<String, Object> toFlatValue() {
        return modelMarshaller.toFlatValue();
    }

    @Override
    public Object toRawValue(Map<String, Object> flatValue) {
        return modelMarshaller.toRawValue(flatValue);
    }

    @Override
    public Class<SubFormFieldDefinition> getSupportedField() {
        return SubFormFieldDefinition.class;
    }

    @Override
    public Supplier<FieldValueMarshaller<Object, Map<String, Object>, SubFormFieldDefinition>> newInstanceSupplier() {
        return () -> {
            SubFormFieldValueMarshaller marshaller = new SubFormFieldValueMarshaller();
            marshaller.setRegistry(registry);
            return marshaller;
        };
    }
}
