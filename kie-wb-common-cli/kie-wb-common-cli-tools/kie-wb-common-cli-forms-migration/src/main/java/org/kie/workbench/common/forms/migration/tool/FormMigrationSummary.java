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

package org.kie.workbench.common.forms.migration.tool;

import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.tool.util.FormsMigrationConstants;
import org.kie.workbench.common.forms.model.FormDefinition;

public class FormMigrationSummary {

    private String baseFormName;

    private Result result = Result.SUCCESS;

    private Resource<Form> originalForm;
    private Resource<FormDefinition> newResource;

    public FormMigrationSummary(Resource<Form> originalForm) {
        this.originalForm = originalForm;

        baseFormName = originalForm.getPath().getFileName();

        if (baseFormName.endsWith("." + FormsMigrationConstants.LEGACY_FOMRS_EXTENSION)) {
            baseFormName = baseFormName.substring(0, baseFormName.lastIndexOf("."));
        }
    }

    public void setBaseFormName(String baseFormName) {
        this.baseFormName = baseFormName;
    }

    public String getBaseFormName() {
        return baseFormName;
    }

    public Resource<FormDefinition> getNewForm() {
        return newResource;
    }

    public void setNewFormResource(Resource<FormDefinition> newResource) {
        this.newResource = newResource;
    }

    public Resource<Form> getOriginalForm() {
        return originalForm;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
