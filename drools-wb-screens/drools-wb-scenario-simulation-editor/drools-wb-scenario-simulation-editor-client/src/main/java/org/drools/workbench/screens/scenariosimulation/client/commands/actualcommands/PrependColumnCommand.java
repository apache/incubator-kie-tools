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

import java.util.Map;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;

/**
 * <code>Command</code> to <b>prepend</b> (i.e. put in the first position) a column to a given <i>group</i>
 */
@Dependent
public class PrependColumnCommand extends AbstractScenarioSimulationCommand {

    public PrependColumnCommand() {
        super(true);
    }

    @Override
    protected void internalExecute(ScenarioSimulationContext context) {
        final ScenarioSimulationContext.Status status = context.getStatus();
        final int index = context.getModel().getFirstIndexLeftOfGroup(status.getColumnGroup());
        FactMappingType factMappingType = FactMappingType.valueOf(status.getColumnGroup().toUpperCase());
        Map.Entry<String, String> validPlaceholders = context.getModel().getValidPlaceholders();
        String instanceTitle = validPlaceholders.getKey();
        String propertyTitle = validPlaceholders.getValue();
        final ScenarioGridColumn scenarioGridColumnLocal = getScenarioGridColumnLocal(instanceTitle,
                                                                                      propertyTitle,
                                                                                      status.getColumnId(),
                                                                                      status.getColumnGroup(),
                                                                                      factMappingType,
                                                                                      context.getScenarioGridPanel(),
                                                                                      context.getScenarioGridLayer(),
                                                                                      ScenarioSimulationEditorConstants.INSTANCE.defineValidType());
        context.getModel().insertColumn(index, scenarioGridColumnLocal);
    }
}
