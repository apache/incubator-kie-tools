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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.kie.workbench.common.forms.adf.definitions.settings.ColSpan;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.layout.LayoutGenerator;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutColumnDefinition;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutSettings;
import org.kie.workbench.common.forms.commons.shared.layout.impl.StaticFormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
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
import org.kie.workbench.common.forms.services.backend.util.UIDGenerator;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

public abstract class AbstractBPMNFormGeneratorService<SOURCE> implements BPMNFormGeneratorService<SOURCE> {

    protected static final Collection<String> bannedModelTypes = new ArrayList<>();

    private static final String HTML_COMPONENT = "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent";
    private static final String HTML_CODE_PARAMETER = "HTML_CODE";

    private static final String INPUTS = "<h3>Inputs:</h3>";
    private static final String OUTPUTS = "<h3>Outputs:</h3>";

    protected FieldManager fieldManager;

    static {
        bannedModelTypes.add(Object.class.getName());
    }

    public AbstractBPMNFormGeneratorService(FieldManager fieldManager) {
        this.fieldManager = fieldManager;
    }

    @Override
    public FormGenerationResult generateForms(JBPMFormModel formModel, SOURCE source) {

        if (formModel == null) {
            throw new IllegalArgumentException("FormModel cannot be null");
        }

        GenerationContext<SOURCE> context = new GenerationContext<>(formModel, source);

        FormDefinition rootForm = createRootFormDefinition(context);

        if (rootForm == null) {
            throw new IllegalStateException("Impossible to generate form for: " + formModel.getFormName());
        }

        processFormDefinition(rootForm, context);

        if (rootForm.getLayoutTemplate() == null) {
            createFormLayout(rootForm, getRootFormHeader());
        }

        context.setRootForm(rootForm);

        return new FormGenerationResult(context.getRootForm(),
                                        new ArrayList<>(context.getContextForms().values()));
    }

    protected void createFormLayout(FormDefinition form) {
        createFormLayout(form, null);
    }

    protected void createFormLayout(FormDefinition form, Supplier<LayoutComponent> headerSupplier) {
        LayoutGenerator layoutGenerator = new LayoutGenerator();

        layoutGenerator.init(new LayoutColumnDefinition[]{new LayoutColumnDefinition(ColSpan.SPAN_12)});

        if(headerSupplier != null) {
            layoutGenerator.addComponent(headerSupplier.get(), new LayoutSettings());
        }

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

    protected void processFormDefinition(final FormDefinition formDefinition, final GenerationContext<SOURCE> context) {
        List<FieldDefinition> fieldsToRemove = formDefinition.getFields().stream()
                .filter(field -> !processFieldDefinition(field, context))
                .collect(Collectors.toList());

        fieldsToRemove.stream().forEach(fieldDefinition -> removeField(fieldDefinition, formDefinition));
    }

    private void removeField(final FieldDefinition fieldDefinition, final FormDefinition formDefinition) {

        formDefinition.getFields().remove(fieldDefinition);

        LayoutTemplate layout = formDefinition.getLayoutTemplate();

        if(layout != null) {
            Optional<LayoutRow> optionalRow = layout.getRows().stream()
                    .filter(row -> rowContainsField(row, fieldDefinition))
                    .findAny();

            if(optionalRow.isPresent()) {
                LayoutRow row = optionalRow.get();
                Optional<LayoutColumn> optionalColumn  = getFieldColumn(row, fieldDefinition);

                if(optionalColumn.isPresent()) {
                    LayoutColumn column = optionalColumn.get();

                    LayoutComponent component = getFieldComponent(column, fieldDefinition).get();

                    column.getLayoutComponents().remove(component);

                    if(column.getLayoutComponents().isEmpty()) {
                        row.getLayoutColumns().remove(column);

                        if(row.getLayoutColumns().isEmpty()) {
                            layout.getRows().remove(row);
                        } else {
                            int span = Integer.decode(column.getSpan());

                            LayoutColumn firstColumn = row.getLayoutColumns().get(0);

                            int fistSpan = Integer.decode(firstColumn.getSpan());

                            LayoutColumn newFirstColumn = new LayoutColumn(String.valueOf(span + fistSpan), firstColumn.getHeight(), firstColumn.getProperties());

                            firstColumn.getLayoutComponents().forEach(newFirstColumn::add);
                            firstColumn.getRows().forEach(newFirstColumn::addRow);

                            Collections.replaceAll(row.getLayoutColumns(), firstColumn, newFirstColumn);
                        }
                    }
                }
            }
        }
    }

    protected boolean rowContainsField(final LayoutRow row, final FieldDefinition fieldDefinition) {
        if(!row.getLayoutColumns().isEmpty()) {
            return getFieldColumn(row, fieldDefinition).isPresent();
        }
        return false;
    }

    protected Optional<LayoutColumn> getFieldColumn(final LayoutRow row, final FieldDefinition fieldDefinition) {
        return row.getLayoutColumns().stream()
                .filter(column -> getFieldComponent(column, fieldDefinition).isPresent())
                .findAny();
    }

    protected Optional<LayoutComponent> getFieldComponent(final LayoutColumn column, final FieldDefinition fieldDefinition) {
        return column.getLayoutComponents().stream()
                .filter(component -> fieldDefinition.getId().equals(component.getProperties().get(FormLayoutComponent.FIELD_ID)))
                .findAny();
    }

    protected boolean processFieldDefinition(final FieldDefinition field, final GenerationContext<SOURCE> context) {
        if (field instanceof EntityRelationField) {
            try {
                if (field instanceof HasNestedForm) {

                    HasNestedForm nestedFormField = (HasNestedForm) field;

                    FormDefinition nestedForm = findFormDefinitionForModelType(field.getStandaloneClassName(), context);

                    if (nestedForm == null) {
                        nestedForm = createModelFormDefinition(field.getStandaloneClassName(), context);

                        verifyNestedForm(nestedForm, context);
                    }

                    nestedFormField.setNestedForm(nestedForm.getId());
                } else if (field instanceof IsCRUDDefinition) {
                    IsCRUDDefinition crudField = (IsCRUDDefinition) field;

                    FormDefinition nestedForm = findFormDefinitionForModelType(field.getStandaloneClassName(),
                                                                               context);

                    if (nestedForm == null) {
                        nestedForm = createModelFormDefinition(field.getStandaloneClassName(), context);

                        verifyNestedForm(nestedForm, context);

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
            } catch (Exception ex) {
                log("Something wrong happened processing FieldDefinition \'" + field.getName() + "\"", ex);
                return false;
            }
        }
        return true;
    }

    private void verifyNestedForm(final FormDefinition nestedForm, final GenerationContext<SOURCE> context) {
        if(nestedForm != null && nestedForm.getFields().isEmpty() && !supportsEmptyNestedForms()) {
            if(nestedForm != null) {
                context.getContextForms().remove(nestedForm.getId());
            }
            throw new RuntimeException("Not Supported empty Nested forms");
        }
    }

    protected FormDefinition createModelFormDefinition(final String modelType, final GenerationContext<SOURCE> context) {

        FormDefinition form = context.getContextForms().get(modelType);

        if (form == null) {

            if(bannedModelTypes.contains(modelType)) {
                throw new IllegalArgumentException("Cannot extract fields for '" + modelType + "'");
            }

            String modelName = modelType.substring(modelType.lastIndexOf(".") + 1);

            String formModelName = modelName;
            formModelName = formModelName.substring(0, 1).toLowerCase() + formModelName.substring(1);

            DataObjectFormModel formModel = new DataObjectFormModel(formModelName, modelType);

            form = new FormDefinition(formModel);

            form.setId(UIDGenerator.generateUID());
            form.setName(modelName);

            // TODO: extract model properties & generate fields
            List<FieldDefinition> fields = extractModelFields(formModel, context);

            List<FieldDefinition> fieldsToRemove = fields.stream()
                    .filter(field -> !processFieldDefinition(field, context))
                    .collect(Collectors.toList());

            fields.removeAll(fieldsToRemove);

            form.getFields().addAll(fields);

            createFormLayout(form);

            context.getContextForms().put(modelType, form);
        }
        return form;
    }

    protected abstract Supplier<LayoutComponent> getRootFormHeader();

    protected abstract boolean supportsEmptyNestedForms();

    protected abstract FormDefinition createRootFormDefinition(GenerationContext<SOURCE> context);

    protected abstract List<FieldDefinition> extractModelFields(JavaFormModel formModel, GenerationContext<SOURCE> context);

    protected abstract void log(String message, Exception ex);

    protected FormDefinition findFormDefinitionForModelType(String modelType, GenerationContext<SOURCE> context) {
        return context.getContextForms().get(modelType);
    }
}
