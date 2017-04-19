/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.forms.commons.layout.FormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.editor.service.backend.util.UIDGenerator;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.EntityRelationField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.HasNestedForm;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.IsCRUDDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.BPMNFormGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.FormGenerationResult;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.JavaModel;
import org.kie.workbench.common.forms.service.FieldManager;

public abstract class AbstractBPMNFormGeneratorService<SOURCE> implements BPMNFormGeneratorService<SOURCE> {

    protected FieldManager fieldManager;

    protected FormLayoutTemplateGenerator layoutTemplateGenerator;

    public AbstractBPMNFormGeneratorService(FieldManager fieldManager,
                                            FormLayoutTemplateGenerator layoutTemplateGenerator) {
        this.fieldManager = fieldManager;
        this.layoutTemplateGenerator = layoutTemplateGenerator;
    }

    @Override
    public FormGenerationResult generateForms(JBPMFormModel formModel,
                                              SOURCE source) {

        if (formModel == null) {
            throw new IllegalArgumentException("FormModel cannot be null");
        }

        GenerationContext<SOURCE> context = new GenerationContext<>(formModel,
                                                                    source);

        FormDefinition rootForm = createRootFormDefinition(context);

        if (rootForm == null) {
            throw new IllegalStateException("Impossible to generate form for: " + formModel.getFormName());
        }

        context.setRootForm(rootForm);

        processFormDefinition(rootForm,
                              context);

        return new FormGenerationResult(context.getRootForm(),
                                        new ArrayList<>(context.getContextForms().values()));
    }

    protected void processFormDefinition(final FormDefinition formDefinition,
                                         final GenerationContext<SOURCE> context) {
        formDefinition.getFields().forEach(field -> {
            processFieldDefinition(field,
                                   context);
        });
    }

    protected void processFieldDefinition(FieldDefinition field,
                                          GenerationContext<SOURCE> context) {
        if (field instanceof EntityRelationField) {

            if (field instanceof HasNestedForm) {

                HasNestedForm nestedFormField = (HasNestedForm) field;

                FormDefinition nestedForm = findFormDefinitionForModelType(field.getStandaloneClassName(),
                                                                           context);

                if (nestedForm == null) {
                    nestedForm = createModelFormDefinition(field.getStandaloneClassName(),
                                                           context);
                }

                nestedFormField.setNestedForm(nestedForm.getId());
            } else if (field instanceof IsCRUDDefinition) {
                IsCRUDDefinition crudField = (IsCRUDDefinition) field;

                FormDefinition nestedForm = findFormDefinitionForModelType(field.getStandaloneClassName(),
                                                                           context);

                if (nestedForm == null) {
                    nestedForm = createModelFormDefinition(field.getStandaloneClassName(),
                                                           context);
                    crudField.setCreationForm(nestedForm.getId());
                    crudField.setEditionForm(nestedForm.getId());

                    List<TableColumnMeta> tableColumnMetas = new ArrayList<>();

                    nestedForm.getFields().forEach(nestedField -> {
                        tableColumnMetas.add(new TableColumnMeta(nestedField.getLabel(),
                                                                 nestedField.getBinding()));
                    });

                    crudField.setColumnMetas(tableColumnMetas);
                }
            }
        }
    }

    protected FormDefinition createModelFormDefinition(String modelType,
                                                       GenerationContext<SOURCE> context) {
        FormDefinition form = context.getContextForms().get(modelType);
        if (form == null) {

            String modelName = modelType.substring(modelType.lastIndexOf(".") + 1);

            String formModelName = modelName;
            formModelName = formModelName.substring(0,
                                                    1).toLowerCase() + formModelName.substring(1);

            DataObjectFormModel formModel = new DataObjectFormModel(formModelName,
                                                                    modelType);

            form = new FormDefinition(formModel);

            context.getContextForms().put(modelType,
                                          form);

            form.setId(UIDGenerator.generateUID());
            form.setName(modelName);

            List<FieldDefinition> fields = extractModelFields(formModel,
                                                              context);

            form.getFields().addAll(fields);

            layoutTemplateGenerator.generateLayoutTemplate(form);

            processFormDefinition(form,
                                  context);
        }
        return form;
    }

    protected abstract FormDefinition createRootFormDefinition(GenerationContext<SOURCE> context);

    protected abstract List<FieldDefinition> extractModelFields(JavaModel formModel,
                                                                GenerationContext<SOURCE> context);

    protected FormDefinition findFormDefinitionForModelType(String modelType,
                                                            GenerationContext<SOURCE> context) {
        return context.getContextForms().get(modelType);
    }
}
