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

import java.util.function.Predicate;

import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.model.Source;
import org.kie.workbench.common.forms.migration.legacy.model.DataHolder;
import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.pipelines.MigrationContext;
import org.kie.workbench.common.forms.migration.tool.util.FormsMigrationConstants;
import org.kie.workbench.common.forms.model.FormModel;

public class DataObjectFormAdapter extends AbstractFormAdapter {

    public DataObjectFormAdapter(MigrationContext migrationContext) {
        super(migrationContext);
    }

    @Override
    protected FormModel extractFormModel(FormMigrationSummary summary) {
        DataHolder dataHolder = summary.getOriginalForm().get().getHolders().iterator().next();

        return createModelForDO(dataHolder);
    }

    @Override
    protected Predicate<FormMigrationSummary> getFilter() {
        return summary -> {
            if (summary.getBaseFormName().endsWith(FormsMigrationConstants.BPMN_FORMS_SUFFIX)) {
                return false;
            }

            Form origin = summary.getOriginalForm().get();

            if (origin.getHolders().size() != 1) {
                fail(summary, "Wrong number of DataHolders (" + origin.getHolders().size() + ") for a Data Object form");
                return false;
            }

            DataHolder dataHolder = origin.getHolders().iterator().next();

            if (FormsMigrationConstants.DATA_HOLDER_TYPE_BASIC.equals(dataHolder.getType())) {
                fail(summary, "Invalid DataHolder type (" + dataHolder.getClassName() + ") for a Data Object form");
                return false;
            }

            return true;
        };
    }
}
