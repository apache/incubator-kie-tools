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

package org.kie.workbench.common.forms.migration.tool.pipelines;

import java.util.function.Function;

import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.legacy.services.FormSerializationManager;
import org.kie.workbench.common.forms.migration.legacy.services.impl.FormSerializationManagerImpl;
import org.kie.workbench.common.forms.migration.tool.Resource;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.uberfire.backend.vfs.Path;

public abstract class AbstractMigrationStep implements MigrationStep {

    @Override
    public void execute(MigrationContext migrationContext) {
        migrationContext.getSystem().console().format("Starting %s\n", getName());
        doExecute(migrationContext);
        persist(migrationContext);
        migrationContext.getSystem().console().format("Finished %s\n", getName());
    }

    private void persist(MigrationContext migrationContext) {
        FormSerializationManager oldFormSerializer = new FormSerializationManagerImpl();

        FormDefinitionSerializer formDefinitionSerializer = migrationContext.getFormCDIWrapper().getFormDefinitionSerializer();

        Function<Resource<Form>, String> originaldFormSerializationFunction = formResource -> {
            Form originalForm = formResource.get();
            originalForm.setMigrationStep(getStep());
            return oldFormSerializer.generateFormXML(originalForm, migrationContext.getSystem());
        };

        Function<Resource<FormDefinition>, String> newFormSerializationFuncion = formResource -> formDefinitionSerializer.serialize(formResource.get());

        migrationContext.getSummaries().forEach(summary -> {
            if (summary.getResult().isSuccess()) {
                serializeResource(summary.getOriginalForm().getPath(), originaldFormSerializationFunction.apply(summary.getOriginalForm()), migrationContext);
                serializeResource(summary.getNewForm().getPath(), newFormSerializationFuncion.apply(summary.getNewForm()), migrationContext);
            }
        });

        migrationContext.getExtraSummaries().forEach(summary -> {
            serializeResource(summary.getNewForm().getPath(), newFormSerializationFuncion.apply(summary.getNewForm()), migrationContext);
        });
    }

    private void serializeResource(Path path, String content, MigrationContext context) {
        context.getMigrationServicesCDIWrapper().write(path, content, "Migration Step #" + getStep() + ": " + getName());
    }

    protected abstract void doExecute(MigrationContext migrationContext);
}
