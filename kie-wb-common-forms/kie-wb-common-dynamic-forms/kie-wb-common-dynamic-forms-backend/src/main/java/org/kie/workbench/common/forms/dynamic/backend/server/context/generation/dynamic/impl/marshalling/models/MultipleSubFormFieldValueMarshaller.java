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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.AbstractFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshallerRegistry;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;

@Dependent
public class MultipleSubFormFieldValueMarshaller extends AbstractFieldValueMarshaller<List<Object>, List<Map<String, Object>>, MultipleSubFormFieldDefinition> {

    private FieldValueMarshallerRegistry registry;

    @Inject
    public void setRegistry(FieldValueMarshallerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void init(List<Object> originalValue, MultipleSubFormFieldDefinition fieldDefinition, FormDefinition currentForm, BackendFormRenderingContext currentContext) {
        super.init(originalValue, fieldDefinition, currentForm, currentContext);

        if (originalValue == null) {
            originalValue = new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> toFlatValue() {
        FormDefinition nestedForm = context.getRenderingContext().getAvailableForms().get(fieldDefinition.getCreationForm());

        return originalValue.stream()
                .map(value -> new ModelMarshaller(registry, value, nestedForm, context))
                .map(this::toFlatValue)
                .collect(Collectors.toList());
    }

    private Map<String, Object> toFlatValue(ModelMarshaller modelMarshaller) {
        Map<String, Object> formValue = modelMarshaller.toFlatValue();

        formValue.put(MapModelRenderingContext.FORM_ENGINE_OBJECT_IDX, originalValue.indexOf(modelMarshaller.getModel()));

        formValue.put(MapModelRenderingContext.FORM_ENGINE_EDITED_OBJECT, Boolean.FALSE);

        return formValue;
    }

    @Override
    public List<Object> toRawValue(List<Map<String, Object>> flatValues) {
        List<Object> result = new ArrayList<>();

        FormDefinition creationForm = context.getRenderingContext().getAvailableForms().get(fieldDefinition.getCreationForm());
        FormDefinition editionForm = context.getRenderingContext().getAvailableForms().get(fieldDefinition.getEditionForm());

        flatValues.stream().forEach(flatValue -> {
            boolean edited = Boolean.TRUE.equals(flatValue.get(MapModelRenderingContext.FORM_ENGINE_EDITED_OBJECT));

            if (flatValue.get(MapModelRenderingContext.FORM_ENGINE_OBJECT_IDX) != null) {
                int originalPosition = (Integer) flatValue.get(MapModelRenderingContext.FORM_ENGINE_OBJECT_IDX);

                if(originalPosition < originalValue.size()) {
                    Object value = originalValue.get(originalPosition);

                    if (edited) {
                        ModelMarshaller marshaller = new ModelMarshaller(registry, value, editionForm, context);

                        value = marshaller.toRawValue(flatValue);
                    }

                    result.add(value);
                }
            } else {

                ModelMarshaller marshaller = new ModelMarshaller(registry, null, creationForm, context);

                Object value = marshaller.toRawValue(flatValue);

                if (edited && !editionForm.equals(creationForm)) {
                    marshaller = new ModelMarshaller(registry, value, editionForm, context);
                    value = marshaller.toRawValue(flatValue);
                }

                result.add(value);
            }
        });

        return result;
    }

    @Override
    public Class<MultipleSubFormFieldDefinition> getSupportedField() {
        return MultipleSubFormFieldDefinition.class;
    }

    @Override
    public Supplier<FieldValueMarshaller<List<Object>, List<Map<String, Object>>, MultipleSubFormFieldDefinition>> newInstanceSupplier() {
        return () -> {
            MultipleSubFormFieldValueMarshaller marshaller = new MultipleSubFormFieldValueMarshaller();
            marshaller.setRegistry(registry);
            return marshaller;
        };
    }
}
