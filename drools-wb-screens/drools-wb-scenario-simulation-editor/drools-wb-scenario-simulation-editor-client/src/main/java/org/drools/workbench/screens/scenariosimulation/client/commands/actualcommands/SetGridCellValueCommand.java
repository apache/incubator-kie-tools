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

import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;

/**
 * <code>Command</code> to set the <i>value</i> of a grid' cell
 */
public class SetGridCellValueCommand extends AbstractScenarioGridCommand {

    public SetGridCellValueCommand(GridWidget gridWidget) {
        super(gridWidget);
    }

    @Override
    protected void internalExecute(ScenarioSimulationContext context) {
        final ScenarioSimulationContext.Status status = context.getStatus();
        AbstractScesimModel<AbstractScesimData> abstractScesimModel = context.getAbstractScesimModelByGridWidget(gridWidget);
        int columnIndex = status.getColumnIndex();
        FactMapping factMapping = abstractScesimModel.getScesimModelDescriptor().getFactMappingByIndex(columnIndex);
        ScenarioGridColumn selectedColumn = (ScenarioGridColumn) context.getAbstractScesimGridModelByGridWidget(gridWidget).getColumns().get(columnIndex);
        String placeholder = ScenarioSimulationUtils.getPlaceHolder(selectedColumn.isInstanceAssigned(),
                                                                    selectedColumn.isPropertyAssigned(),
                                                                    factMapping.getFactMappingValueType(),
                                                                    factMapping.getClassName());
        context.getAbstractScesimGridModelByGridWidget(gridWidget).setCellValue(status.getRowIndex(),
                                                                                columnIndex,
                                                                                new ScenarioGridCellValue(status.getGridCellValue(),
                                                                                                          placeholder));
        context.getAbstractScesimGridModelByGridWidget(gridWidget).resetError(status.getRowIndex(), columnIndex);
    }
}
