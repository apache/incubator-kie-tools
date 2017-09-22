/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.service.shared.adf;

import java.util.ArrayList;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.engine.shared.FormBuildingService;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.util.PropertyValueExtractor;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.StaticModelFormRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.EntityRelationField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.HasNestedForm;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.IsCRUDDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.uberfire.commons.validation.PortablePreconditions;

@Dependent
public class DynamicFormModelGenerator {

    private FormBuildingService formBuildingService;

    private PropertyValueExtractor propertyValueExtractor;

    @Inject
    public DynamicFormModelGenerator(FormBuildingService formBuildingService,
                                     PropertyValueExtractor propertyValueExtractor) {
        this.formBuildingService = formBuildingService;
        this.propertyValueExtractor = propertyValueExtractor;
    }

    public StaticModelFormRenderingContext getContextForModel(Object model) {
        PortablePreconditions.checkNotNull("model",
                                           model);

        FormDefinition formDefinition = formBuildingService.generateFormForModel(model);

        if (formDefinition == null) {
            return null;
        }

        StaticModelFormRenderingContext context = new StaticModelFormRenderingContext();

        context.setModel(model);
        context.setRootForm(formDefinition);

        if (context.getModel() != null) {
            initNestedFormSettings(formDefinition,
                                   context.getModel(),
                                   context);
        } else {
            initNestedFormSettings(formDefinition,
                                   null,
                                   context);
        }

        return context;
    }

    private void initNestedFormSettings(final FormDefinition form,
                                        final Object model,
                                        final StaticModelFormRenderingContext context) {
        form.getFields().forEach(field -> {
            if (field instanceof HasNestedForm) {
                HasNestedForm hasNestedForm = (HasNestedForm) field;

                if (!context.getAvailableForms().containsKey(((HasNestedForm) field).getNestedForm())) {
                    addNestedForm(model,
                                  hasNestedForm.getNestedForm(),
                                  field.getName(),
                                  context);
                }
            } else if (field instanceof IsCRUDDefinition) {
                IsCRUDDefinition isCRUDDefinitionField = (IsCRUDDefinition) field;

                if (!context.getAvailableForms().containsKey(isCRUDDefinitionField.getCreationForm())) {
                    addNestedForm(field.getStandaloneClassName(),
                                  context);
                }

                if (isCRUDDefinitionField.getColumnMetas() == null || isCRUDDefinitionField.getColumnMetas().isEmpty()) {
                    if (isCRUDDefinitionField.getColumnMetas() == null) {
                        isCRUDDefinitionField.setColumnMetas(new ArrayList<>());
                    }

                    FormDefinition nestedForm = context.getAvailableForms().get(isCRUDDefinitionField.getCreationForm());

                    nestedForm.getFields().forEach(nestedField -> {
                        if (nestedField instanceof EntityRelationField) {
                            return;
                        }
                        isCRUDDefinitionField.getColumnMetas().add(new TableColumnMeta(nestedField.getLabel(),
                                                                                       nestedField.getBinding()));
                    });
                }
            }
        });
    }

    private void addNestedForm(final Object parentModel,
                               final String className,
                               final String fieldName,
                               final StaticModelFormRenderingContext context) {
        if (!context.getAvailableForms().containsKey(className)) {
            if (parentModel != null) {

                Object value = propertyValueExtractor.readPropertyValue(parentModel,
                                                                        fieldName);

                if (value != null) {
                    addNestedForm(value,
                                  context);
                } else {
                    addNestedForm(className,
                                  context);
                }
            } else {
                addNestedForm(className,
                              context);
            }
        }
    }

    private void addNestedForm(String className,
                               StaticModelFormRenderingContext context) {
        if (!context.getAvailableForms().containsKey(className)) {
            FormDefinition nested = formBuildingService.generateFormForClassName(className);
            context.getAvailableForms().put(className,
                                            nested);
            initNestedFormSettings(nested,
                                   null,
                                   context);
        }
    }

    private void addNestedForm(Object model,
                               StaticModelFormRenderingContext context) {
        if (!context.getAvailableForms().containsKey(model.getClass().getName())) {
            FormDefinition nested = formBuildingService.generateFormForModel(model);
            context.getAvailableForms().put(model.getClass().getName(),
                                            nested);
            initNestedFormSettings(nested,
                                   model,
                                   context);
        }
    }
}
