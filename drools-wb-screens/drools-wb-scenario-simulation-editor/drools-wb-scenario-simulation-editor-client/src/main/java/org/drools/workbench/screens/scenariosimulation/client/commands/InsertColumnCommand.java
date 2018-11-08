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
package org.drools.workbench.screens.scenariosimulation.client.commands;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

/**
 * <code>Command</code> to <b>insert</b> a column.
 */
@Dependent
public class InsertColumnCommand extends AbstractCommand {

    private ScenarioGridModel model;
    private String columnId;
    private int columnIndex;
    protected boolean isRight;
    private boolean asProperty;

    public InsertColumnCommand() {
    }

    /**
     * @param model
     * @param columnId
     * @param columnIndex
     * @param isRight when <code>true</code>, column will be inserted to the right of the given index (i.e. at position columnIndex +1), otherwise to the left (i.e. at position columnIndex)
     * @param asProperty when <code>true</code>, column will use the <b>instance</b> header of the original one, so to create a new "property" header under the same instance
     * @param scenarioGridPanel
     * @param scenarioGridLayer
     */
    public InsertColumnCommand(ScenarioGridModel model, String columnId, int columnIndex, boolean isRight, boolean asProperty, ScenarioGridPanel scenarioGridPanel, ScenarioGridLayer scenarioGridLayer) {
        super(scenarioGridPanel, scenarioGridLayer);
        this.model = model;
        this.columnId = columnId;
        this.columnIndex = columnIndex;
        this.isRight = isRight;
        this.asProperty = asProperty;
    }

    @Override
    public void execute() {
        final List<GridColumn<?>> columns = model.getColumns();
        final ScenarioGridColumn selectedColumn = (ScenarioGridColumn) columns.get(columnIndex);
        final ScenarioHeaderMetaData selectedInformationHeaderMetaData = selectedColumn.getInformationHeaderMetaData();
        String columnGroup = selectedInformationHeaderMetaData.getColumnGroup();
        String originalInstanceTitle = selectedInformationHeaderMetaData.getTitle();
        FactMappingType factMappingType = FactMappingType.valueOf(columnGroup.toUpperCase());
        Map.Entry<String, String> validPlaceholders = model.getValidPlaceholders();
        boolean cloneInstance = asProperty && selectedColumn.isInstanceAssigned();
        String instanceTitle = cloneInstance ? originalInstanceTitle : validPlaceholders.getKey();
        String propertyTitle = validPlaceholders.getValue();
        String placeHolder = ScenarioSimulationEditorConstants.INSTANCE.defineValidType();
        final ScenarioGridColumn scenarioGridColumnLocal = getScenarioGridColumnLocal(instanceTitle,
                                                                                      propertyTitle,
                                                                                      columnId,
                                                                                      columnGroup,
                                                                                      factMappingType,
                                                                                      scenarioGridPanel,
                                                                                      scenarioGridLayer,
                                                                                      placeHolder);
        scenarioGridColumnLocal.setInstanceAssigned(cloneInstance);
        scenarioGridColumnLocal.setPropertyAssigned(false);
        if (cloneInstance) {
            scenarioGridColumnLocal.setFactIdentifier(selectedColumn.getFactIdentifier());
        }
        GridData.Range instanceRange = model.getInstanceLimits(columnIndex);
        int columnPosition = isRight ? instanceRange.getMaxRowIndex() + 1 : instanceRange.getMinRowIndex();
        model.insertColumn(columnPosition, scenarioGridColumnLocal);
    }
}
