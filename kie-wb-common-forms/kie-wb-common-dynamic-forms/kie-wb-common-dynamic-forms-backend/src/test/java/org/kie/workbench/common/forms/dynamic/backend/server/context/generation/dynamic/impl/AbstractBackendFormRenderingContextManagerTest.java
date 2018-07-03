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
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;

import org.junit.After;
import org.junit.Before;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshallerRegistryImpl;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.TextAreaFormFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.models.MultipleSubFormFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.models.SubFormFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.DateMultipleInputFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.DateMultipleSelectorFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.LocalDateFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.model.Person;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.validation.impl.ContextModelConstraintsExtractorImpl;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractBackendFormRenderingContextManagerTest {

    protected Instance<FieldValueMarshaller<?, ?, ? extends FieldDefinition>> marshallersInstance;

    protected TestFieldManager fieldManager = new TestFieldManager();

    private FieldValueMarshallerRegistryImpl registry;

    protected BackendFormRenderingContextManagerImpl contextManager;

    protected BackendFormRenderingContext context;

    protected ClassLoader classLoader;

    protected Map<String, Object> formData;

    @Before
    public void initTest() {

        SubFormFieldValueMarshaller subFormFieldValueMarshaller = new SubFormFieldValueMarshaller();
        MultipleSubFormFieldValueMarshaller multipleSubFormFieldValueMarshaller = new MultipleSubFormFieldValueMarshaller();

        List<FieldValueMarshaller> marshallers = Arrays.asList(subFormFieldValueMarshaller,
                                                               multipleSubFormFieldValueMarshaller,
                                                               new DateMultipleInputFieldValueMarshaller(),
                                                               new DateMultipleSelectorFieldValueMarshaller(),
                                                               new LocalDateFieldValueMarshaller(),
                                                               new TextAreaFormFieldValueMarshaller());

        marshallersInstance = mock(Instance.class);

        when(marshallersInstance.iterator()).then(proc -> marshallers.iterator());

        registry = new FieldValueMarshallerRegistryImpl(marshallersInstance);

        subFormFieldValueMarshaller.setRegistry(registry);
        multipleSubFormFieldValueMarshaller.setRegistry(registry);

        contextManager = new BackendFormRenderingContextManagerImpl(registry, new ContextModelConstraintsExtractorImpl());

        formData = generateFormData();

        classLoader = mock(ClassLoader.class);

        long timestamp = contextManager.registerContext(getRootForm(), formData, classLoader, getNestedForms()).getTimestamp();

        context = contextManager.getContext(timestamp);

        assertNotNull("Context cannot be null", context);
    }

    protected abstract FormDefinition[] getNestedForms();

    protected abstract FormDefinition getRootForm();

    protected abstract Map<String, Object> generateFormData();

    protected void initContentMarshallerClassLoader(Class clazz,
                                                    boolean availableOnClassLoader) {
        if (availableOnClassLoader) {
            try {
                when(classLoader.loadClass(clazz.getName())).thenReturn((Class) Person.class);
            } catch (ClassNotFoundException e) {
                // Swallow
            }
        } else {
            try {
                when(classLoader.loadClass(clazz.getName())).thenThrow(ClassNotFoundException.class);
            } catch (ClassNotFoundException e) {
                // Swallow
            }
        }
    }

    @After
    public void afterTest() {
        contextManager.removeContext(context.getTimestamp());

        assertNull("There shouldn't be any context",
                   contextManager.getContext(context.getTimestamp()));
    }
}
