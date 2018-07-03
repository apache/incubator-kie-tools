/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshallerRegistry;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.validation.ContextModelConstraintsExtractor;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SessionScoped
public class BackendFormRenderingContextManagerImpl implements BackendFormRenderingContextManager {

    private static final Logger logger = LoggerFactory.getLogger(BackendFormRenderingContextManagerImpl.class);

    protected Map<Long, BackendFormRenderingContextImpl> contexts = new HashMap<>();

    protected FieldValueMarshallerRegistry registry;

    protected ContextModelConstraintsExtractor constraintsExtractor;

    @Inject
    public BackendFormRenderingContextManagerImpl(FieldValueMarshallerRegistry registry,
                                                  ContextModelConstraintsExtractor constraintsExtractor) {
        this.registry = registry;
        this.constraintsExtractor = constraintsExtractor;
    }

    @Override
    public BackendFormRenderingContext registerContext(FormDefinition rootForm,
                                                       Map<String, Object> formData,
                                                       ClassLoader classLoader,
                                                       FormDefinition... nestedForms) {
        return registerContext(rootForm, formData, classLoader, new HashMap<String, String>(), nestedForms);
    }

    @Override
    public BackendFormRenderingContext registerContext(FormDefinition rootForm,
                                                       Map<String, Object> formData,
                                                       ClassLoader classLoader,
                                                       Map<String, String> params,
                                                       FormDefinition... nestedForms) {

        MapModelRenderingContext clientRenderingContext = new MapModelRenderingContext(String.valueOf(System.currentTimeMillis()));

        clientRenderingContext.setRootForm(rootForm);

        Arrays.stream(nestedForms).forEach(form -> clientRenderingContext.getAvailableForms().put(form.getId(),
                                                                                                  form));

        BackendFormRenderingContextImpl context = new BackendFormRenderingContextImpl(System.currentTimeMillis(),
                                                                                      clientRenderingContext,
                                                                                      formData,
                                                                                      classLoader,
                                                                                      params);

        Map<String, Object> clientFormData = new HashMap<>();

        rootForm.getFields().stream()
                .filter(fieldDefinition -> !StringUtils.isEmpty(fieldDefinition.getBinding()))
                .forEach(fieldDefinition -> {
                    Object value = formData.get(fieldDefinition.getBinding());
                    FieldValueMarshaller marshaller = registry.getMarshaller(fieldDefinition);
                    if(marshaller != null) {
                        marshaller.init(value, fieldDefinition, rootForm, context);
                        context.getRootFormMarshallers().put(fieldDefinition.getBinding(), marshaller);
                        value = marshaller.toFlatValue();
                    }
                    clientFormData.put(fieldDefinition.getBinding(), value);
                });

        constraintsExtractor.readModelConstraints(clientRenderingContext,
                                                  classLoader);

        clientRenderingContext.setModel(clientFormData);

        contexts.put(context.getTimestamp(),
                     context);

        return context;
    }

    @Override
    public BackendFormRenderingContext updateContextData(long timestamp,
                                                         Map<String, Object> formValues) {

        BackendFormRenderingContextImpl context = contexts.get(timestamp);

        if (context == null) {
            throw new IllegalArgumentException("Unable to find context with id '" + timestamp + "'");
        }

        FormDefinition rootForm = context.getRenderingContext().getRootForm();

        Map<String, Object> contextData = new HashMap<>();

        rootForm.getFields().stream()
                .filter(fieldDefinition -> !StringUtils.isEmpty(fieldDefinition.getBinding()))
                .forEach(fieldDefinition -> {
                    Object value = formValues.get(fieldDefinition.getBinding());

                    FieldValueMarshaller marshaller = context.getRootFormMarshallers().get(fieldDefinition.getBinding());
                    if(marshaller != null) {
                        value = marshaller.toRawValue(value);
                    }
                    contextData.put(fieldDefinition.getBinding(), value);
                });

        context.setFormData(contextData);

        return context;
    }

    @Override
    public BackendFormRenderingContext getContext(Long timestamp) {
        return contexts.get(timestamp);
    }

    @Override
    public boolean removeContext(Long timestamp) {
        return contexts.remove(timestamp) != null;
    }
}
