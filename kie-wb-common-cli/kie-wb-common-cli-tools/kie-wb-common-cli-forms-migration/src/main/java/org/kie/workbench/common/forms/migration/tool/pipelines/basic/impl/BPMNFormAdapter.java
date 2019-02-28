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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.kie.api.io.ResourceType;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.migration.legacy.model.DataHolder;
import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.legacy.services.FieldTypeManager;
import org.kie.workbench.common.forms.migration.legacy.services.impl.FieldTypeBuilder;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.Resource;
import org.kie.workbench.common.forms.migration.tool.bpmn.BPMNAnalyzer;
import org.kie.workbench.common.forms.migration.tool.bpmn.BPMNProcess;
import org.kie.workbench.common.forms.migration.tool.pipelines.MigrationContext;
import org.kie.workbench.common.forms.migration.tool.util.FormsMigrationConstants;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.services.backend.util.UIDGenerator;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

public class BPMNFormAdapter extends AbstractFormAdapter {

    protected List<JBPMFormModel> workspaceBPMNFormModels = new ArrayList<>();

    public BPMNFormAdapter(MigrationContext migrationContext) {
        super(migrationContext);

        readWorkspaceBPMNModels();
    }

    protected void readWorkspaceBPMNModels() {
        BPMNAnalyzer analyzer = new BPMNAnalyzer();

        Files.walkFileTree(Paths.convert(migrationContext.getWorkspaceProject().getRootPath()), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(org.uberfire.java.nio.file.Path visitedPath, BasicFileAttributes attrs) throws IOException {

                org.uberfire.backend.vfs.Path visitedVFSPath = Paths.convert(visitedPath);
                String fileName = visitedVFSPath.getFileName();
                File file = visitedPath.toFile();

                if (file.isFile() && isBPMNFile(fileName)) {
                    try {
                        BPMNProcess process = analyzer.read(migrationContext.getMigrationServicesCDIWrapper().getIOService().newInputStream(visitedPath));
                        if (process != null) {
                            workspaceBPMNFormModels.addAll(process.getFormModels());
                        } else {
                            migrationContext.getSystem().console().format(FormsMigrationConstants.BPMN_PARSING_ERROR, FormsMigrationConstants.WARNING, fileName);
                        }
                    } catch (Exception ex) {
                        migrationContext.getSystem().console().format(FormsMigrationConstants.BPMN_PARSING_ERROR, FormsMigrationConstants.WARNING, fileName);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    static boolean isBPMNFile(final String fileName) {
        return ResourceType.getResourceType("BPMN2").matchesExtension(fileName);
    }

    @Override
    protected void doCreateFormDefinition(FormModel formModel, FormMigrationSummary formSummary) {

        Form originalForm = formSummary.getOriginalForm().get();

        Set<DataHolder> objectDataHolders = originalForm.getHolders()
                .stream()
                .filter(dataHolder -> !dataHolder.getType().equals(FormsMigrationConstants.DATA_HOLDER_TYPE_BASIC))
                .collect(Collectors.toSet());

        if (!objectDataHolders.isEmpty()) {
            FieldTypeManager fieldTypeManager = FieldTypeManager.get();

            objectDataHolders.forEach(dataHolder -> {
                Set<Field> dataHolderFields = originalForm.getFieldsForDataHolder(dataHolder);

                if (dataHolderFields.isEmpty()) {
                    return;
                }

                FormModel newFormModel = createModelForDO(dataHolder);

                String formName = formSummary.getBaseFormName() + "-" + dataHolder.getUniqeId();

                String formAssetName = formName + "." + FormsMigrationConstants.NEW_FOMRS_EXTENSION;

                FormDefinition newFormDefinition = new FormDefinition(newFormModel);
                newFormDefinition.setId(UIDGenerator.generateUID());
                newFormDefinition.setName(formName);

                migrateFields(dataHolderFields, newFormDefinition, formSummary);

                dataHolderFields.forEach(field -> field.setMovedToForm(formName));

                Path newFormPath = Paths.convert(formSummary.getOriginalForm().getPath()).getParent().resolve(formAssetName);

                FormMigrationSummary extraSummary = new FormMigrationSummary(formSummary.getOriginalForm());
                extraSummary.setBaseFormName(formName);
                extraSummary.setNewFormResource(new Resource<>(newFormDefinition, Paths.convert(newFormPath)));

                migrationContext.getExtraSummaries().add(extraSummary);

                Field field = new Field();
                field.setFieldType(fieldTypeManager.getTypeByCode(FieldTypeBuilder.SUBFORM));
                field.setId(System.currentTimeMillis());
                field.setFieldName(dataHolder.getUniqeId());
                field.setInputBinding(dataHolder.getInputId());
                field.setOutputBinding(dataHolder.getOuputId());
                field.setLabel(new HashMap<>());
                field.getLabel().put(FormsMigrationConstants.DEFAULT_LANG, dataHolder.getUniqeId());
                field.setPosition(originalForm.getFormFields().size() + 1000);
                field.setDefaultSubform(newFormDefinition.getId());
                field.setSourceLink(formName);
                field.setBag(dataHolder.getClassName());
                field.setForm(originalForm);
                originalForm.getFormFields().add(field);
            });
        }
        super.doCreateFormDefinition(formModel, formSummary);
    }

    @Override
    protected Predicate<FormMigrationSummary> getFilter() {
        return summary -> summary.getBaseFormName().endsWith(FormsMigrationConstants.BPMN_FORMS_SUFFIX);
    }

    @Override
    protected FormModel extractFormModel(FormMigrationSummary summary) {

        JBPMFormModel formModel = workspaceBPMNFormModels
                .stream()
                .filter(bpmnFormModel -> bpmnFormModel.getFormName().equals(summary.getBaseFormName()))
                .findAny()
                .orElse(null);

        return formModel;
    }
}
