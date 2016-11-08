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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl;

import java.util.Arrays;
import java.util.List;
import javax.enterprise.inject.Instance;

import org.kie.workbench.common.forms.commons.layout.FormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.commons.layout.impl.DynamicFormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.fieldInitializers.EnumSelectorFieldInitializer;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.fieldInitializers.FieldInitializer;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.fieldInitializers.MultipleSubFormFieldInitializer;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.fieldInitializers.SubFormFieldInitializer;
;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.processors.FieldAnnotationProcessor;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.processors.ListBoxFieldAnnotationProcessor;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.processors.RadioGroupFieldAnnotationProcessor;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.processors.SliderAnnotationProcessor;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.processors.TextAreaAnnotationProcessor;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.StaticModelFormRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.service.impl.fieldProviders.ListBoxFieldProvider;
import org.kie.workbench.common.forms.service.impl.fieldProviders.RadioGroupFieldProvider;
import org.kie.workbench.common.forms.service.impl.fieldProviders.SliderFieldProvider;
import org.kie.workbench.common.forms.service.impl.fieldProviders.TextAreaFieldProvider;
import org.kie.workbench.common.forms.service.mock.TestFieldManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class AbstractFormRenderingContextGeneratorTest<T> {

    protected Instance<FieldAnnotationProcessor<? extends FieldDefinition>> annotationProcessors;

    protected Instance<FieldInitializer<? extends FieldDefinition>> fieldInitializers;

    protected FormLayoutTemplateGenerator layoutTemplateGenerator;

    protected FormRenderingContextGeneratorImpl service;

    protected StaticModelFormRenderingContext context;

    public void init() {
        final List<FieldAnnotationProcessor> processors = Arrays.asList(
                new ListBoxFieldAnnotationProcessor( new ListBoxFieldProvider() {
                    {
                        registerFields();
                    }
                } ),
                new RadioGroupFieldAnnotationProcessor( new RadioGroupFieldProvider() {
                    {
                        registerFields();
                    }
                } ),
                new SliderAnnotationProcessor( new SliderFieldProvider() {
                    {
                        registerFields();
                    }
                } ),
                new TextAreaAnnotationProcessor( new TextAreaFieldProvider() {
                    {
                        registerFields();
                    }
                } ) );

        final List<FieldInitializer> initializers = Arrays.asList( new SubFormFieldInitializer(),
                                                                   new MultipleSubFormFieldInitializer(),
                                                                   new EnumSelectorFieldInitializer() );

        annotationProcessors = mock( Instance.class );
        when( annotationProcessors.iterator() ).then( inv -> processors.iterator() );


        fieldInitializers = mock( Instance.class );
        when( fieldInitializers.iterator() ).then( inv -> initializers.iterator() );

        layoutTemplateGenerator = new DynamicFormLayoutTemplateGenerator();

        service = new FormRenderingContextGeneratorImpl( annotationProcessors,
                                                         fieldInitializers,
                                                         layoutTemplateGenerator,
                                                         new TestFieldManager() );
    }

    public void initTest( T model, int expectedFields ) {

        assertNotNull( "Model cannot be null", model );

        context = service.createContext( model );

        assertNotNull( "Context cannot be null", context );
        assertNotNull( "Context must have a root form", context.getRootForm() );
        assertFalse( "Context must have at least one form", context.getAvailableForms().isEmpty() );

        FormDefinition form = context.getRootForm();

        assertNotNull( "Form must contain fields", form.getFields() );
        assertFalse( "Form must contain fields", form.getFields().isEmpty() );

        assertEquals( "Form should have " + expectedFields + " fields", expectedFields, form.getFields().size() );

        for ( FieldDefinition field : form.getFields() ) {
            assertNotNull( "Field should have an ID!", field.getId() );
            assertNotNull( "Field should have a name!", field.getName() );
            assertNotNull( "Field should have a label!", field.getLabel() );
            assertNotNull( "Field should have a model!", field.getBinding() );
        }
    }

}
