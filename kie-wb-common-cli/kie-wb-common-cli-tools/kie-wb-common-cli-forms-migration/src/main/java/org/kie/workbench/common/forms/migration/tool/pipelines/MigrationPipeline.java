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

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.forms.migration.tool.pipelines.basic.FormDefinitionGenerator;

public class MigrationPipeline {

    private List<MigrationStep> steps = new ArrayList<>();

    public MigrationPipeline() {
        steps.add(new FormDefinitionGenerator());
    }

    public void migrate(MigrationContext migrationContext) {
        steps.forEach(migrationStep -> {
            migrationStep.execute(migrationContext);
        });
    }

    public String getAllInfo() {
        StringBuffer msg = new StringBuffer("The Forms migration has the following steps:\n");

        steps.forEach(step -> {
            msg
                    .append("\n")
                    .append(step.getName().toUpperCase())
                    .append("\n\n")
                    .append(step.getDescription())
                    .append("\n");
        });

        return msg.toString();
    }
}
