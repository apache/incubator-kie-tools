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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;

import org.junit.Before;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.MultipleSubFormFieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.SubFormFieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.model.Person;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.service.mock.TestFieldManager;

import static org.mockito.Mockito.*;

public abstract class AbstractFormValuesProcessorImplTest {

    protected Instance<FieldValueProcessor<? extends FieldDefinition, ?, ?>> fieldValueProcessors;

    protected TestFieldManager fieldManager = new TestFieldManager();

    protected FormValuesProcessorImpl formValuesProcessor;

    protected BackendFormRenderingContext context;

    protected ClassLoader classLoader;

    protected Map<String, Object> formData;

    protected MapModelRenderingContext renderingContext;

    @Before
    public void initTest() {

        List<FieldValueProcessor> processors = Arrays.asList( new SubFormFieldValueProcessor(), new MultipleSubFormFieldValueProcessor() );

        fieldValueProcessors = mock( Instance.class );
        when( fieldValueProcessors.iterator() ).then( proc -> processors.iterator() );

        formValuesProcessor = new FormValuesProcessorImpl( fieldValueProcessors );

        renderingContext = generateRenderingContext();

        formData = generateFormData();

        classLoader = mock( ClassLoader.class );

        context = new BackendFormRenderingContextImpl( System.currentTimeMillis(),
                                                       renderingContext,
                                                       formData,
                                                       classLoader );
    }

    protected abstract MapModelRenderingContext generateRenderingContext();

    protected abstract Map<String, Object> generateFormData();

    protected void initContentMarshallerClassLoader( Class clazz, boolean availableOnClassLoader ) {
        if ( availableOnClassLoader ) {
            try {
                when( classLoader.loadClass( clazz.getName() ) ).thenReturn( (Class) Person.class );
            } catch ( ClassNotFoundException e ) {
                // Swallow
            }
        } else {
            try {
                when( classLoader.loadClass( clazz.getName() ) ).thenThrow( ClassNotFoundException.class );
            } catch ( ClassNotFoundException e ) {
                // Swallow
            }
        }
    }
}
