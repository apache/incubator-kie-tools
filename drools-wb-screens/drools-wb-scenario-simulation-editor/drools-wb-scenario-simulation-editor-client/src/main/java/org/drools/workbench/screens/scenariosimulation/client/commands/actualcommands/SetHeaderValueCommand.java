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

/**
 * <code>Command</code> to to set the <i>value</i> of a header' cell
 */
@Dependent
public class SetHeaderValueCommand extends AbstractScenarioSimulationCommand {

    public SetHeaderValueCommand() {
        super(true);
    }

    @Override
    protected void internalExecute(ScenarioSimulationContext context) throws Exception {
        final ScenarioSimulationContext.Status status = context.getStatus();
        final String headerValue = status.getCellValue();
        boolean isADataType = context.getScenarioSimulationEditorPresenter().getDataManagementStrategy() != null &&  context.getScenarioSimulationEditorPresenter().getDataManagementStrategy().isADataType(headerValue);
        if (context.getModel().validateHeaderUpdate(headerValue, status.getRowIndex(), status.getColumnIndex(), isADataType)) {
            context.getModel().updateHeader(status.getColumnIndex(), status.getRowIndex(), headerValue);
        } else {
            throw new Exception("Name \"" + headerValue + "\" cannot be used");
        }
    }
}
