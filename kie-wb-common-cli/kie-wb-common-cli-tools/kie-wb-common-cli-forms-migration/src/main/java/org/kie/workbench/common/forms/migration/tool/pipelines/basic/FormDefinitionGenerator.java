/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.migration.tool.pipelines.basic;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.EmbedsForm;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.HasNestedForm;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.IsCRUDDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta;
import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.Result;
import org.kie.workbench.common.forms.migration.tool.pipelines.AbstractMigrationStep;
import org.kie.workbench.common.forms.migration.tool.pipelines.MigrationContext;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.BPMNFormAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.DataObjectFormAdapter;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;

public class FormDefinitionGenerator extends AbstractMigrationStep {

    private final Function<MigrationContext, DataObjectFormAdapter> dataObjectFormAdapterFunction;
    private final Function<MigrationContext, BPMNFormAdapter> bpmFormAdapterFunction;

    public FormDefinitionGenerator() {
        this(DataObjectFormAdapter::new, BPMNFormAdapter::new);
    }

    FormDefinitionGenerator(Function<MigrationContext, DataObjectFormAdapter> dataObjectFormAdapterFunction, Function<MigrationContext, BPMNFormAdapter> bpmFormAdapterFunction) {
        this.dataObjectFormAdapterFunction = dataObjectFormAdapterFunction;
        this.bpmFormAdapterFunction = bpmFormAdapterFunction;
    }

    @Override
    public void doExecute(MigrationContext migrationContext) {

        migrationContext.getSummaries().removeIf(this::basicSummaryCheck);

        DataObjectFormAdapter dataObjectAdapter = dataObjectFormAdapterFunction.apply(migrationContext);

        List<FormMigrationSummary> possibleDataObjectForms = dataObjectAdapter.migrateSummaries();

        if (!possibleDataObjectForms.isEmpty()) {
            arrangeNestedForms(possibleDataObjectForms, possibleDataObjectForms);
        }

        BPMNFormAdapter bpmFormAdapter = bpmFormAdapterFunction.apply(migrationContext);

        List<FormMigrationSummary> possibleBPMNForms = bpmFormAdapter.migrateSummaries();

        if (!possibleBPMNForms.isEmpty() && !possibleDataObjectForms.isEmpty()) {
            arrangeNestedForms(possibleBPMNForms, possibleDataObjectForms);
        }

        if (!migrationContext.getExtraSummaries().isEmpty() && !possibleDataObjectForms.isEmpty()) {
            arrangeNestedForms(migrationContext.getExtraSummaries(), possibleDataObjectForms);
        }
    }

    private void arrangeNestedForms(List<FormMigrationSummary> newForms, List<FormMigrationSummary> modelForms) {
        newForms.stream()
                .filter(summary -> Result.SUCCESS.equals(summary.getResult()) && summary.getNewForm() != null)
                .forEach(summary -> maybeArrangeNestedForms(summary, modelForms));
    }

    private void maybeArrangeNestedForms(FormMigrationSummary summary, List<FormMigrationSummary> modelForms) {
        final Form originalForm = summary.getOriginalForm().get();
        final FormDefinition formDefinition = summary.getNewForm().get();

        formDefinition.getFields()
                .stream()
                .filter(fieldDefinition -> fieldDefinition instanceof HasNestedForm)
                .map(fieldDefinition -> (HasNestedForm) fieldDefinition)
                .forEach(hasNestedForm -> {
                    modelForms.stream()
                            .filter(modelSummary -> modelSummary.getOriginalForm().getPath().getFileName().equals(hasNestedForm.getNestedForm()))
                            .findFirst()
                            .ifPresent(modelSummary -> {
                                hasNestedForm.setNestedForm(modelSummary.getNewForm().get().getId());
                                updateNestedFormsModelProperties(formDefinition, (FieldDefinition) hasNestedForm, modelSummary.getOriginalForm().get());
                            });
                });

        formDefinition.getFields()
                .stream()
                .filter(fieldDefinition -> fieldDefinition instanceof IsCRUDDefinition)
                .map(fieldDefinition -> (IsCRUDDefinition) fieldDefinition)
                .forEach(crudDefinition -> {
                    // update Creation Form
                    updateCrudDefinitionForms(modelForms,
                                              modelSummary -> modelSummary.getOriginalForm().getPath().getFileName().equals(crudDefinition.getCreationForm()),
                                              modelSummary -> crudDefinition.setCreationForm(modelSummary.getNewForm().get().getId()));

                    // update Edition Form
                    updateCrudDefinitionForms(modelForms,
                                              modelSummary -> modelSummary.getOriginalForm().getPath().getFileName().equals(crudDefinition.getEditionForm()),
                                              modelSummary -> crudDefinition.setEditionForm(modelSummary.getNewForm().get().getId()));

                    // update Table Columns Form
                    updateCrudDefinitionForms(modelForms,
                                              modelSummary -> {
                                                  Field originalField = originalForm.getField(((FieldDefinition) crudDefinition).getName());
                                                  return modelSummary.getOriginalForm().getPath().getFileName().equals(StringUtils.defaultIfBlank(originalField.getTableSubform(), originalField.getDefaultSubform()));
                                              },
                                              modelSummary -> {
                                                  FormDefinition modelFormDefinition = modelSummary.getNewForm().get();

                                                  modelFormDefinition.getFields().forEach(fieldDefinition -> {
                                                      if (!(fieldDefinition instanceof EmbedsForm)) {
                                                          crudDefinition.getColumnMetas().add(new TableColumnMeta(fieldDefinition.getLabel(), fieldDefinition.getBinding()));
                                                      }
                                                  });
                                                  updateNestedFormsModelProperties(formDefinition, (FieldDefinition) crudDefinition, modelSummary.getOriginalForm().get());
                                              });
                });
    }

    private void updateCrudDefinitionForms(List<FormMigrationSummary> modelForms, Predicate<FormMigrationSummary> filter, Consumer<FormMigrationSummary> action) {
        modelForms.stream()
                .filter(filter)
                .findFirst()
                .ifPresent(action);
    }

    private void updateNestedFormsModelProperties(FormDefinition newFormDefinition, FieldDefinition nestedFormField, Form nestedForm) {
        nestedFormField.setStandaloneClassName(nestedForm.getHolders().iterator().next().getClassName());

        FormModel model = newFormDefinition.getModel();

        ModelProperty modelProperty = model.getProperty(nestedFormField.getBinding());

        if (modelProperty != null) {
            model.getProperties().remove(modelProperty);
            ModelPropertyImpl newProperty = new ModelPropertyImpl(nestedFormField.getBinding(), nestedFormField.getFieldTypeInfo());

            newProperty.getMetaData().getEntries().addAll(modelProperty.getMetaData().getEntries());

            model.getProperties().add(newProperty);
        }
    }

    @Override
    public int getStep() {
        return 1;
    }

    @Override
    public String getName() {
        return "Basic jBPM Form Migration";
    }

    @Override
    public String getDescription() {
        return "Creation of new Form Definitions for based on old jBPM Forms: It copies basic settings such as basic layout, " +
                "form controls, bindings and I18n literals. There are some considerations: " +
                "\n- The existing jBPM Forms must be correct, that means that if there's a conflict some forms might not be migrated" +
                "\n- Forms non related to any jBPM process (forms that don't end with \"taskform\" suffix) should have " +
                "one and only one DataHolder of type Data Object or ClassName." +
                "\n- Forms related to jBPM processes (forms that end with \"taskform\" suffix) no longer support fields " +
                "with nested bindings (like client/name) caused by Data Object variables on the process, in the case " +
                "that nested bindings exist the affected fields will be replaced by a SubForm. That means that some new forms might be generated" +
                "\n- New forms doesn't support different bindings for input/output, if you want to use a single field for " +
                "input/output on your process please change the inputs/outputs on your process & form to use the same binding. If there's a case " +
                "where input/output aren't equal input will be chosen.";
    }
}
