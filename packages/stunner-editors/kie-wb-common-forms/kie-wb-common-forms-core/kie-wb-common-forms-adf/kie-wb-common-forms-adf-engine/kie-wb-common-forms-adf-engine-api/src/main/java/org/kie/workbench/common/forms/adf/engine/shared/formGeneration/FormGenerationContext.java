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
import java.util.stream.Stream;

import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.forms.adf.service.building.FieldStatusModifier;
import org.kie.workbench.common.forms.adf.service.definitions.FormDefinitionSettings;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.impl.PortableJavaModel;

/**
 * Context to generate a {@link FormDefinition}
 */
public class FormGenerationContext {

    private FormDefinition formDefinition;

    private FormDefinitionSettings formDefinitionSettings;

    private Map<String, String> fieldStatusModifierReferences = new HashMap();

    private Map<String, FieldStatusModifier> fieldStatusModifiers = new HashMap<>();

    private Map<String, FormElementFilter> filters = new HashMap<>();

    private I18nHelper i18nHelper;

    private Object model;

    public FormGenerationContext(Object model,
                                 FormDefinitionSettings settings,
                                 I18nHelper i18nHelper,
                                 FormElementFilter... filters) {
        this.model = model;
        this.formDefinitionSettings = settings;
        this.i18nHelper = i18nHelper;

        this.formDefinition = new FormDefinition(new PortableJavaModel(settings.getModelType()));

        formDefinition.setName(settings.getModelType());
        formDefinition.setId(settings.getModelType());
        Stream.of(filters).forEach(filter -> this.filters.put(filter.getElementName(), filter));
    }

    public FormDefinition getFormDefinition() {
        return formDefinition;
    }

    public FormDefinitionSettings getFormDefinitionSettings() {
        return formDefinitionSettings;
    }

    public I18nHelper getI18nHelper() {
        return i18nHelper;
    }

    public void setFieldStatusModifierReferences(Map<String, String> fieldStatusModifierReferences) {
        this.fieldStatusModifierReferences = fieldStatusModifierReferences;
    }

    public void setFieldStatusModifiers(Map<String, FieldStatusModifier> fieldStatusModifiers) {
        this.fieldStatusModifiers = fieldStatusModifiers;
    }

    public FieldStatusModifier getStatusModifierForFieldName(String fieldName) {

        String modifierName = fieldStatusModifierReferences.get(fieldName);

        if (modifierName != null) {
            return fieldStatusModifiers.get(modifierName);
        }

        return null;
    }

    public FormElementFilter getFilter(String elementName) {
        return filters.get(elementName);
    }

    public Object getModel() {
        return model;
    }
}
