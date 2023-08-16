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


package org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerationContext;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.FormElementProcessor;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.util.PropertyValueExtractor;
import org.kie.workbench.common.forms.adf.service.building.FieldStatusModifier;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.FormLayoutComponent;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public abstract class AbstractFieldElementProcessor implements FormElementProcessor<FieldElement> {

    protected FieldManager fieldManager;
    protected PropertyValueExtractor propertyValueExtractor;
    protected List<FieldInitializer> initializers = new ArrayList<>();

    public AbstractFieldElementProcessor(FieldManager fieldManager,
                                         PropertyValueExtractor propertyValueExtractor) {
        this.fieldManager = fieldManager;
        this.propertyValueExtractor = propertyValueExtractor;
    }

    protected void registerInitializer(FieldInitializer fieldInitializer) {
        initializers.add(fieldInitializer);
    }

    @Override
    public Class<FieldElement> getSupportedElementType() {
        return FieldElement.class;
    }

    @Override
    public LayoutComponent processFormElement(FieldElement element,
                                              FormGenerationContext context) {

        FieldDefinition field;

        if (element.getPreferredType().equals(FieldType.class)) {
            field = fieldManager.getDefinitionByDataType(element.getTypeInfo());
        } else {
            field = fieldManager.getDefinitionByFieldType(element.getPreferredType(),
                                                          element.getTypeInfo());
            if (field == null) {
                field = fieldManager.getDefinitionByDataType(element.getTypeInfo());
            }
        }

        if (field != null) {
            context.getFormDefinition().getFields().add(field);

            field.setId(element.getName());
            field.setName(element.getName());
            field.setBinding(element.getBinding());
            String label = context.getI18nHelper().getTranslation(element.getLabelKey());
            if (label == null || label.isEmpty()) {
                label = element.getName().substring(0,
                                                    1).toUpperCase() + element.getName().substring(1);
            }
            field.setLabel(label);

            String helpMessage = context.getI18nHelper().getTranslation(element.getHelpMessageKey());
            field.setHelpMessage(helpMessage);

            field.setRequired(element.isRequired());
            field.setReadOnly(element.isReadOnly());

            if (context.getModel() != null) {
                String fullFieldName = context.getFormDefinitionSettings().getModelType() + "." + element.getName();
                FieldStatusModifier initializer = context.getStatusModifierForFieldName(fullFieldName);
                if (initializer != null) {
                    initializer.modifyFieldStatus(field,
                                                  propertyValueExtractor.readPropertyValue(context.getModel(),
                                                                                           element.getName()));
                }
            }

            for (FieldInitializer fieldInitializer : initializers) {
                if (fieldInitializer.supports(field)) {
                    fieldInitializer.initialize(field,
                                                element,
                                                context);
                }
            }

            LayoutComponent component = new LayoutComponent("org.kie.workbench.common.forms.dynamic.client.rendering.FieldLayoutComponent");

            component.addProperty(FormLayoutComponent.FORM_ID,
                                  context.getFormDefinition().getId());
            component.addProperty(FormLayoutComponent.FIELD_ID,
                                  field.getId());
            return component;
        }

        return null;
    }
}
