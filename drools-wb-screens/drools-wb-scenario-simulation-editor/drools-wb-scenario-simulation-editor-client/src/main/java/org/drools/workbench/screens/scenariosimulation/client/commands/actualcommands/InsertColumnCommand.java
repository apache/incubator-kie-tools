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

import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

/**
 * <code>Command</code> to <b>insert</b> a column.
 */
@Dependent
public class InsertColumnCommand extends AbstractScenarioSimulationCommand {

    public InsertColumnCommand() {
        super(true);
    }

    @Override
    protected void internalExecute(ScenarioSimulationContext context) {
        final ScenarioSimulationContext.Status status = context.getStatus();
        final List<GridColumn<?>> columns = context.getModel().getColumns();
        final ScenarioGridColumn selectedColumn = (ScenarioGridColumn) columns.get(status.getColumnIndex());
        final ScenarioHeaderMetaData selectedInformationHeaderMetaData = selectedColumn.getInformationHeaderMetaData();
        String columnGroup = selectedInformationHeaderMetaData.getColumnGroup();
        String originalInstanceTitle = selectedInformationHeaderMetaData.getTitle();
        FactMappingType factMappingType = FactMappingType.valueOf(columnGroup.toUpperCase());
        Map.Entry<String, String> validPlaceholders = context.getModel().getValidPlaceholders();
        boolean cloneInstance = status.isAsProperty() && selectedColumn.isInstanceAssigned();
        String instanceTitle = cloneInstance ? originalInstanceTitle : validPlaceholders.getKey();
        String propertyTitle = validPlaceholders.getValue();
        String placeHolder = ScenarioSimulationEditorConstants.INSTANCE.defineValidType();
        final ScenarioGridColumn scenarioGridColumnLocal = getScenarioGridColumnLocal(instanceTitle,
                                                                                      propertyTitle,
                                                                                      status.getColumnId(),
                                                                                      columnGroup,
                                                                                      factMappingType,
                                                                                      context.getScenarioGridPanel(),
                                                                                      context.getScenarioGridLayer(),
                                                                                      placeHolder);
        scenarioGridColumnLocal.setPropertyAssigned(false);
        if (cloneInstance) {
            scenarioGridColumnLocal.setFactIdentifier(selectedColumn.getFactIdentifier());
        }
        int columnPosition = -1;
        if (status.isAsProperty()) {
            columnPosition = status.isRight() ? status.getColumnIndex() + 1 : status.getColumnIndex();
        } else {
            GridData.Range instanceRange = context.getModel().getInstanceLimits(status.getColumnIndex());
            columnPosition = status.isRight() ? instanceRange.getMaxRowIndex() + 1 : instanceRange.getMinRowIndex();
        }
        scenarioGridColumnLocal.setInstanceAssigned(cloneInstance);
        scenarioGridColumnLocal.setPropertyAssigned(false);
        context.getModel().insertColumn(columnPosition, scenarioGridColumnLocal);
    }
}
