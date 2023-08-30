/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.adf.engine.shared.formGeneration;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.layout.LayoutGenerator;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.FormElementProcessor;
import org.kie.workbench.common.forms.adf.service.building.FieldStatusModifier;
import org.kie.workbench.common.forms.adf.service.building.FormGenerationResourcesProvider;
import org.kie.workbench.common.forms.adf.service.definitions.FormDefinitionSettings;
import org.kie.workbench.common.forms.adf.service.definitions.I18nSettings;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FormElement;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutColumnDefinition;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public abstract class AbstractFormGenerator implements FormGenerator {

    protected LayoutGenerator layoutGenerator;

    protected Map<Class<? extends FormElement>, FormElementProcessor> processors = new HashMap<>();

    protected Map<String, FormDefinitionSettings> formDefinitionSettings = new HashMap<>();

    protected Map<String, FieldStatusModifier> fieldModifiers = new HashMap<>();

    protected Map<String, String> fieldModifierReferences = new HashMap<>();

    public AbstractFormGenerator(LayoutGenerator layoutGenerator) {
        this.layoutGenerator = layoutGenerator;
    }

    protected void registerProcessor(FormElementProcessor processor) {
        if (processor != null) {
            processors.put(processor.getSupportedElementType(),
                           processor);
        }
    }

    protected void registerResources(FormGenerationResourcesProvider provider) {
        if (provider != null) {
            if (isValid(provider.getDefinitionSettings())) {
                formDefinitionSettings.putAll(provider.getDefinitionSettings());
            }
            if (isValid(provider.getFieldModifiers())) {
                fieldModifiers.putAll(provider.getFieldModifiers());
            }
            if (isValid(provider.getFieldModifierReferences())) {
                fieldModifierReferences.putAll(provider.getFieldModifierReferences());
            }
        }
    }

    private boolean isValid(Map map) {
        return map != null && !map.isEmpty();
    }

    @Override
    public FormDefinition generateFormForModel(Object model, FormElementFilter... filters) {

        FormDefinitionSettings settings = formDefinitionSettings.get(model.getClass().getName());

        if (settings != null) {
            return generateFormDefinition(settings,
                                          model,
                                          filters);
        }

        return null;
    }

    @Override
    public FormDefinition generateFormForClass(Class clazz, FormElementFilter... filters) {
        return generateFormForClassName(clazz.getName(), filters);
    }

    @Override
    public FormDefinition generateFormForClassName(String className, FormElementFilter... filters) {
        FormDefinitionSettings settings = formDefinitionSettings.get(className);
        if (settings != null) {
            return generateFormDefinition(settings,
                                          null,
                                          filters);
        }
        return null;
    }

    protected FormDefinition generateFormDefinition(FormDefinitionSettings settings,
                                                    Object model,
                                                    FormElementFilter... filters) {

        FormGenerationContext context = new FormGenerationContext(model,
                                                                  settings,
                                                                  getI18nHelper(settings.getI18nSettings()),
                                                                  filters);

        context.setFieldStatusModifierReferences(fieldModifierReferences);

        context.setFieldStatusModifiers(fieldModifiers);

        if (settings.getLayout() == null || settings.getLayout().getColumns() == null || settings.getLayout().getColumns().length == 0) {
            settings.setLayout(new LayoutDefinition(new LayoutColumnDefinition()));
        }

        layoutGenerator.init(settings.getLayout().getColumns());

        processElements(context);

        FormDefinition form = context.getFormDefinition();

        form.setLayoutTemplate(layoutGenerator.build());

        return form;
    }

    protected abstract I18nHelper getI18nHelper(I18nSettings settings);

    protected void processElements(FormGenerationContext context) {
        context.getFormDefinitionSettings().getFormElements().forEach(formElement -> {
            FormElementProcessor processor = processors.get(formElement.getClass());

            if (processor != null) {

                FormElementFilter filter  = context.getFilter(formElement.getName());

                if(filter == null || filter.getPredicate().test(context.getModel())) {
                    LayoutComponent layoutComponent = processor.processFormElement(formElement,
                                                                                   context);
                    if (layoutComponent != null) {
                        layoutGenerator.addComponent(layoutComponent,
                                                     formElement.getLayoutSettings());
                    }
                }
            }
        });
    }
}
