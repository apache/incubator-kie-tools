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

package org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl;

import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.model.Source;
import org.kie.workbench.common.forms.migration.legacy.model.DataHolder;
import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.Resource;
import org.kie.workbench.common.forms.migration.tool.Result;
import org.kie.workbench.common.forms.migration.tool.pipelines.MigrationContext;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.FieldAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.FormAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.UnSupportedFieldAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.decorators.DecoratorFieldAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields.CharacterFieldAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields.CheckBoxFieldAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields.DatesFieldAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields.DecimalTextFieldAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields.DocumentFieldAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields.InputTextFieldAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields.IntegerTextFieldAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields.MultipleSubformFieldAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields.MultiplesValuesAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields.RadioGroupFieldAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields.SelectBoxFieldAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields.SubformFieldAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields.TextAreaFieldAdapter;
import org.kie.workbench.common.forms.migration.tool.util.FormsMigrationConstants;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.java.nio.file.Path;

public abstract class AbstractFormAdapter implements FormAdapter {

    protected Map<String, FieldAdapter> adaptersRegistry = new HashMap<>();
    protected Map<String, UnSupportedFieldAdapter> unSupportedAdapters = new HashMap<>();

    protected MigrationContext migrationContext;

    public AbstractFormAdapter(MigrationContext migrationContext) {
        this.migrationContext = migrationContext;

        registerAdapter(new InputTextFieldAdapter());
        registerAdapter(new CharacterFieldAdapter());
        registerAdapter(new TextAreaFieldAdapter());
        registerAdapter(new IntegerTextFieldAdapter());
        registerAdapter(new DecimalTextFieldAdapter());
        registerAdapter(new CheckBoxFieldAdapter());
        registerAdapter(new DatesFieldAdapter());
        registerAdapter(new SelectBoxFieldAdapter());
        registerAdapter(new RadioGroupFieldAdapter());
        registerAdapter(new SubformFieldAdapter());
        registerAdapter(new MultipleSubformFieldAdapter());
        registerAdapter(new DecoratorFieldAdapter());
        registerAdapter(new MultiplesValuesAdapter());
        registerAdapter(new DocumentFieldAdapter());
    }

    protected void registerAdapter(final FieldAdapter adapter) {
        Stream.of(adapter.getLegacyFieldTypeCodes()).forEach(code -> adaptersRegistry.put(code, adapter));

        if(adapter instanceof UnSupportedFieldAdapter) {
            UnSupportedFieldAdapter unSupportedFieldAdapter = (UnSupportedFieldAdapter) adapter;
            Stream.of(unSupportedFieldAdapter.getLegacySupportedFieldTypeCodes()).forEach(code -> unSupportedAdapters.put(code, unSupportedFieldAdapter));
        }
    }

    @Override
    public List<FormMigrationSummary> migrateSummaries() {
        List<FormMigrationSummary> filteredSummaries = migrationContext.getSummaries()
                .stream()
                .filter(getFilter())
                .collect(Collectors.toList());

        if (filteredSummaries != null) {
            filteredSummaries.forEach(this::migrate);
        }

        return filteredSummaries;
    }

    protected abstract Predicate<FormMigrationSummary> getFilter();

    protected void migrate(FormMigrationSummary formSummary) {

        info("Starting migration for Form [" + formSummary.getOriginalForm().getPath().getFileName() + "]");

        FormModel formModel = extractFormModel(formSummary);

        if (formModel == null) {
            fail(formSummary, "Impossible to identify a valid Model for new form. This might mean that the form " +
                    "has a reference to a missing Data Object or an unexisting BPM Process / Task");
            return;
        }

        doCreateFormDefinition(formModel, formSummary);

        info("Form succesfully migrated");
    }

    protected void doCreateFormDefinition(FormModel formModel, FormMigrationSummary formSummary) {
        Form originalForm = formSummary.getOriginalForm().get();

        FormDefinition newFormDefinition = new FormDefinition(formModel);

        newFormDefinition.setId(String.valueOf(originalForm.getId()));
        newFormDefinition.setName(formSummary.getBaseFormName());

        Path originalPath = Paths.convert(formSummary.getOriginalForm().getPath());

        Path newPath = originalPath.getParent().resolve(formSummary.getBaseFormName() + "." + FormsMigrationConstants.NEW_FOMRS_EXTENSION);

        formSummary.setNewFormResource(new Resource<>(newFormDefinition, Paths.convert(newPath)));

        migrateFields(originalForm.getFormFields(), newFormDefinition, formSummary);
    }

    protected void migrateFields(final Set<Field> fields, final FormDefinition newForm, final FormMigrationSummary formSummary) {

        LayoutHelper helper = new LayoutHelper();

        fields.forEach(originalField -> {
            if (!StringUtils.isEmpty(originalField.getMovedToForm())) {
                return;
            }

            if (!Boolean.TRUE.equals(originalField.getGroupWithPrevious())) {
                helper.newRow();
            }

            String originalTypeCode = originalField.getFieldType().getCode();
            FieldAdapter adapter = adaptersRegistry.get(originalTypeCode);
            if (adapter == null) {
                // trying a backup adapter
                UnSupportedFieldAdapter unSupportedFieldAdapter = unSupportedAdapters.get(originalTypeCode);
                if(unSupportedFieldAdapter != null) {
                    warn("Problems migrating field '" + originalField.getFieldName() + "': the original field has an unsupported field type '" + originalTypeCode + "'. It will be added on the new Form as a '" + unSupportedFieldAdapter.getNewFieldType() + "'");
                    unSupportedFieldAdapter.parseField(originalField, formSummary, newForm, helper::add);
                } else {
                    warn("Cannot migrate field '" + originalField.getFieldName() + "': Unsupported field type '" + originalTypeCode + "'");

                    Formatter formatter = new Formatter();
                    formatter.format(FormsMigrationConstants.UNSUPORTED_FIELD_HTML_TEMPLATE, originalField.getFieldName(), originalTypeCode);

                    LayoutComponent component = new LayoutComponent(FormsMigrationConstants.HTML_COMPONENT);
                    component.addProperty(FormsMigrationConstants.HTML_CODE_PARAMETER, formatter.toString());
                    formatter.close();

                    helper.add(component);
                }
            } else {
                try {
                    adapter.parseField(originalField, formSummary, newForm, helper::add);
                } catch (Exception ex) {
                    warn("Cannot migrate field '" + originalField.getFieldName() + "': Unexpected error, see message for details");
                    ex.printStackTrace(migrationContext.getSystem().err());
                }
            }
        });

        newForm.setLayoutTemplate(helper.build());
    }

    protected abstract FormModel extractFormModel(FormMigrationSummary summary);

    protected FormModel createModelForDO(DataHolder dataHolder) {
        String className = dataHolder.getClassName();

        String modelName = className.substring(className.lastIndexOf(".") + 1);

        DataObjectFormModel formModel = new DataObjectFormModel(modelName, className);

        if (Boolean.TRUE.equals(Boolean.parseBoolean(dataHolder.getSupportedType()))) {
            formModel.setSource(Source.EXTERNAL);
        }

        return formModel;
    }

    protected void info(String message) {
        migrationContext.getSystem().out().println(FormsMigrationConstants.INFO + message);
    }

    protected void fail(FormMigrationSummary summary, String message) {
        summary.setResult(Result.FAILURE);
        migrationContext.getSystem().err().println(FormsMigrationConstants.ERROR + "Form [" + summary.getOriginalForm().getPath().getFileName() + "] cannot be migrated: " + message);
    }

    protected void warn(String message) {
        migrationContext.getSystem().out().println(FormsMigrationConstants.WARNING + message);
    }
}
