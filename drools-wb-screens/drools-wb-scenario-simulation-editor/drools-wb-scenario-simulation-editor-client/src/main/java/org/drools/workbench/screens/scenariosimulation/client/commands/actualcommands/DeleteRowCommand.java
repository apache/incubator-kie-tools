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
package org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridRow;

/**
 * <code>Command</code> to <b>delete</b> a row.
 */
@Dependent
public class DeleteRowCommand extends AbstractScenarioSimulationCommand {

    public DeleteRowCommand() {
        super(true);
    }

    @Override
    protected void internalExecute(ScenarioSimulationContext context) {
        context.getSelectedScenarioGridModel().deleteRow(context.getStatus().getRowIndex());
        if (context.getSelectedScenarioGridModel().getRows().isEmpty()) {
            context.getSelectedScenarioGridModel().insertRow(0, new ScenarioGridRow());
        }
    }
}
