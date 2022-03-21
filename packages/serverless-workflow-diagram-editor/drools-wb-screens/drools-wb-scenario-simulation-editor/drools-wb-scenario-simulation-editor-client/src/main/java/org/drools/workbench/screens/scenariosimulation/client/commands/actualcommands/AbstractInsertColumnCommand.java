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

import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;

public abstract class AbstractInsertColumnCommand extends AbstractScenarioGridCommand {

    public AbstractInsertColumnCommand(GridWidget gridWidget) {
        super(gridWidget);
    }

    public void commonInsertColumnCommand(ScenarioSimulationContext context, ScenarioSimulationContext.Status status, int index) {
        FactMappingType factMappingType = FactMappingType.valueOf(status.getColumnGroup().toUpperCase());
        Map.Entry<String, String> validPlaceholders = context.getAbstractScesimGridModelByGridWidget(gridWidget).getValidPlaceholders();
        String instanceTitle = validPlaceholders.getKey();
        String propertyTitle = validPlaceholders.getValue();
        final ScenarioGridColumn scenarioGridColumnLocal = getScenarioGridColumnLocal(instanceTitle,
                                                                                      propertyTitle,
                                                                                      status.getColumnId(),
                                                                                      status.getColumnGroup(),
                                                                                      factMappingType,
                                                                                      context.getScenarioHeaderTextBoxSingletonDOMElementFactory(gridWidget),
                                                                                      context.getScenarioCellTextAreaSingletonDOMElementFactory(gridWidget),
                                                                                      ScenarioSimulationEditorConstants.INSTANCE.defineValidType());
        context.getAbstractScesimGridModelByGridWidget(gridWidget).insertColumn(index, scenarioGridColumnLocal);
        context.getSelectedScenarioGridLayer().ifPresent(layer -> layer.getScenarioGrid().selectCurrentHeaderCellGroup());
    }
}
