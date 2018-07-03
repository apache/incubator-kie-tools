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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshallerRegistry;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.JavaFormModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelMarshaller {

    private static final Logger logger = LoggerFactory.getLogger(ModelMarshaller.class);

    private FieldValueMarshallerRegistry registry;
    private Object model;
    private FormDefinition formDefinition;
    private BackendFormRenderingContext context;
    private String type;

    private Map<String, FieldValueMarshaller> marshallers = new HashMap<>();

    public ModelMarshaller(FieldValueMarshallerRegistry registry, Object model, FormDefinition formDefinition, BackendFormRenderingContext context) {
        this.registry = registry;
        this.model = model;
        this.formDefinition = formDefinition;
        this.context = context;

        if (formDefinition.getModel() instanceof JavaFormModel) {
            this.type = ((JavaFormModel) formDefinition.getModel()).getType();
        } else {
            throw new IllegalStateException("Cannot initialize a ModelMarshaller for model type " + formDefinition.getModel().getClass());
        }

        iterateFormFields(this::registerMarshaller);
    }

    private void registerMarshaller(FieldDefinition field) {
        FieldValueMarshaller marshaller = registry.getMarshaller(field);

        if (marshaller != null) {
            marshaller.init(readValue(field.getBinding()), field, formDefinition, context);
            marshallers.put(field.getBinding(), marshaller);
        }
    }

    private Object readValue(String property) {
        if (model != null) {
            try {
                if (PropertyUtils.getPropertyDescriptor(model, property) != null) {
                    return PropertyUtils.getProperty(model, property);
                }
            } catch (Exception e) {
                logger.warn("Error getting property '{}' from object '{}'", property, model);
                logger.warn("Caused by:", e);
            }
        }
        return null;
    }

    public Map<String, Object> toFlatValue() {
        Map<String, Object> result = new HashMap<>();

        if (model != null) {
            iterateFormFields(fieldDefinition -> {
                String binding = fieldDefinition.getBinding();
                FieldValueMarshaller marshaller = marshallers.get(binding);
                if (marshaller != null) {
                    result.put(binding, marshaller.toFlatValue());
                } else {
                    result.put(binding, readValue(binding));
                }
            });
        }
        return result;
    }

    public Object toRawValue(final Map<String, Object> values) {
        if (model == null) {
            model = newInstance();
        }

        iterateFormFields(fieldDefinition -> {
            String binding = fieldDefinition.getBinding();
            FieldValueMarshaller marshaller = marshallers.get(binding);

            Object flatValue = values.get(binding);

            if (marshaller != null) {
                writeValue(binding, marshaller.toRawValue(flatValue));
            } else {
                writeValue(binding, flatValue);
            }
        });

        return model;
    }

    private void writeValue(String property, Object value) {
        try {
            if (PropertyUtils.getPropertyDescriptor(model, property) != null) {
                BeanUtils.setProperty(model, property, value);
            }
        } catch (Exception e) {
            logger.warn("Error modifying object '{}': cannot set value '{}' to property '{}'", model, value, property);
            logger.warn("Caused by:", e);
        }
    }

    private Object newInstance() {
        Class clazz = null;
        try {
            clazz = context.getClassLoader().loadClass(type);
        } catch (ClassNotFoundException e) {
            // Maybe the nested class it is not on the classLoader context... let's try on the app classloader
            try {
                clazz = Class.forName(type);
            } catch (ClassNotFoundException e1) {
                logger.warn("Unable to find class '{}' on classLoader", type);
            }
        }
        if (clazz != null) {
            try {
                return ConstructorUtils.invokeConstructor(clazz, new Object[0]);
            } catch (Exception e) {
                logger.warn("Unable to create instance for class {}: ", type);
            }
        }
        throw new IllegalStateException("Unable to create instance for class " + type);
    }

    private void iterateFormFields(Consumer<FieldDefinition> consumer) {
        formDefinition.getFields().stream()
                .filter(fieldDefinition -> !StringUtils.isEmpty(fieldDefinition.getBinding()))
                .forEach(consumer);
    }

    public Object getModel() {
        return model;
    }
}
