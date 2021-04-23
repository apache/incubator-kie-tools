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
package org.drools.workbench.screens.scenariosimulation.client.models;

import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

public class ScenarioGridModel extends AbstractScesimGridModel<Simulation, Scenario> {

    private ScenarioGridModel() {
    }

    public ScenarioGridModel(boolean isMerged) {
        super(isMerged);
    }

    @Override
    public GridWidget getGridWidget() {
        return GridWidget.SIMULATION;
    }

    @Override
    public Range getInstanceLimits(int columnIndex) {
        return getInstanceLimits(columnIndex, 1);
    }

    /**
     * This method <i>insert</i> a row to the grid and populate it with values taken from given <code>Scenario</code>
     * @param row
     */
    @Override
    public void insertRowGridOnly(final int rowIndex,
                                  final GridRow row, final Scenario scenario) {
        insertRowGridOnly(rowIndex, row);
        scenario.getUnmodifiableFactMappingValues().forEach(value -> {
            FactIdentifier factIdentifier = value.getFactIdentifier();
            ExpressionIdentifier expressionIdentifier = value.getExpressionIdentifier();
            if (value.getRawValue() == null || value.getRawValue() instanceof String) { // Let' put a placeholder
                String stringValue = (String) value.getRawValue();
                int columnIndex = abstractScesimModel.getScesimModelDescriptor().getIndexByIdentifier(factIdentifier, expressionIdentifier);
                final FactMapping factMappingByIndex = abstractScesimModel.getScesimModelDescriptor().getFactMappingByIndex(columnIndex);
                String placeHolder = ((ScenarioGridColumn) columns.get(columnIndex)).getPlaceHolder();
                setCell(rowIndex, columnIndex, () -> {
                    ScenarioGridCell newCell = new ScenarioGridCell(new ScenarioGridCellValue(stringValue, placeHolder));
                    if (ScenarioSimulationSharedUtils.isCollection((factMappingByIndex.getClassName()))) {
                        newCell.setListMap(ScenarioSimulationSharedUtils.isList((factMappingByIndex.getClassName())));
                    }
                    return newCell;
                });
            } else {
                throw new UnsupportedOperationException("Only string is supported at the moment");
            }
        });
        updateIndexColumn();
    }

    protected void commonAddRow(int rowIndex) {
        commonAddRow(rowIndex, 1);
        updateIndexColumn();
    }

}