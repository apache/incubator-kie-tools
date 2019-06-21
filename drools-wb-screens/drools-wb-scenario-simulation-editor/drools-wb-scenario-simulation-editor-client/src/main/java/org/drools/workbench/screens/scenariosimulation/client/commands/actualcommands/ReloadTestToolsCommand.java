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
import org.uberfire.client.mvp.PlaceStatus;

/**
 * <code>Command</code> to <b>reload</b> the <code>TestToolsView</code>, <b>eventually</b> showing it (if required by original event)
 */
@Dependent
public class ReloadTestToolsCommand extends AbstractScenarioSimulationCommand {

    public ReloadTestToolsCommand() {
        super(false);
    }

    @Override
    protected void internalExecute(ScenarioSimulationContext context) {
        final ScenarioSimulationContext.Status status = context.getStatus();
        if (context.getScenarioSimulationEditorPresenter() != null) {
            if (status.isOpenDock()) {
                context.getScenarioSimulationEditorPresenter().expandToolsDock(PlaceStatus.OPEN);
            }
            context.getScenarioSimulationEditorPresenter().reloadTestTools(status.isDisable());
        }
    }
}
