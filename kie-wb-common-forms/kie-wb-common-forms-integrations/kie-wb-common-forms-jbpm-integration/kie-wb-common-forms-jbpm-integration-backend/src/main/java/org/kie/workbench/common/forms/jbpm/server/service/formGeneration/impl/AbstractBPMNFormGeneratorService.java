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

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.forms.adf.definitions.settings.ColSpan;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.layout.LayoutGenerator;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutColumnDefinition;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutSettings;
import org.kie.workbench.common.forms.commons.shared.layout.impl.StaticFormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.editor.service.backend.util.UIDGenerator;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.EntityRelationField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.HasNestedForm;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.IsCRUDDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.BPMNFormGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.FormGenerationResult;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormLayoutComponent;
import org.kie.workbench.common.forms.model.JavaFormModel;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public abstract class AbstractBPMNFormGeneratorService<SOURCE> implements BPMNFormGeneratorService<SOURCE> {

    private static final String HTML_COMPONENT = "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent";
    private static final String HTML_CODE_PARAMETER = "HTML_CODE";

    private static final String INPUTS = "<h3>Inputs:</h3>";
    private static final String OUTPUTS = "<h3>Outputs:</h3>";

    protected FieldManager fieldManager;

    public AbstractBPMNFormGeneratorService(FieldManager fieldManager) {
        this.fieldManager = fieldManager;
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

        if (rootForm.getLayoutTemplate() == null) {
            createFormLayout(rootForm);
        }

        context.setRootForm(rootForm);

        processFormDefinition(rootForm,
                              context);

        return new FormGenerationResult(context.getRootForm(),
                                        new ArrayList<>(context.getContextForms().values()));
    }

    protected void createFormLayout(FormDefinition form) {
        LayoutGenerator layoutGenerator = new LayoutGenerator();

        layoutGenerator.init(new LayoutColumnDefinition[]{new LayoutColumnDefinition(ColSpan.SPAN_12)});

        if (form.getFields().size() > 0) {
            boolean separeateInputsAndOutputs = form.getModel() instanceof TaskFormModel;

            boolean mightAddOtuputsLabel = form.getFields().get(0).getReadOnly();

            if (separeateInputsAndOutputs) {
                if (mightAddOtuputsLabel) {
                    layoutGenerator.addComponent(generateHTMLElement(INPUTS),
                                                 new LayoutSettings());
                } else {
                    layoutGenerator.addComponent(generateHTMLElement(OUTPUTS),
                                                 new LayoutSettings());
                }
            }

            for (FieldDefinition fieldDefinition : form.getFields()) {

                if (separeateInputsAndOutputs && mightAddOtuputsLabel && !fieldDefinition.getReadOnly()) {
                    mightAddOtuputsLabel = false;
                    layoutGenerator.addComponent(generateHTMLElement(OUTPUTS),
                                                 new LayoutSettings());
                }

                LayoutComponent fieldComponent = new LayoutComponent(StaticFormLayoutTemplateGenerator.DRAGGABLE_TYPE);
                fieldComponent.addProperty(FormLayoutComponent.FORM_ID,
                                           form.getId());
                fieldComponent.addProperty(FormLayoutComponent.FIELD_ID,
                                           fieldDefinition.getId());
                layoutGenerator.addComponent(fieldComponent,
                                             new LayoutSettings());
            }
        }

        form.setLayoutTemplate(layoutGenerator.build());
    }

    protected LayoutComponent generateHTMLElement(String content) {
        LayoutComponent htmlComponent = new LayoutComponent(HTML_COMPONENT);
        htmlComponent.addProperty(HTML_CODE_PARAMETER,
                                  content);
        return htmlComponent;
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

            // TODO: extract model properties & generate fields
            List<FieldDefinition> fields = extractModelFields(formModel,
                                                              context);

            form.getFields().addAll(fields);

            createFormLayout(form);

            processFormDefinition(form,
                                  context);
        }
        return form;
    }

    protected abstract FormDefinition createRootFormDefinition(GenerationContext<SOURCE> context);

    protected abstract List<FieldDefinition> extractModelFields(JavaFormModel formModel,
                                                                GenerationContext<SOURCE> context);

    protected FormDefinition findFormDefinitionForModelType(String modelType,
                                                            GenerationContext<SOURCE> context) {
        return context.getContextForms().get(modelType);
    }
}
