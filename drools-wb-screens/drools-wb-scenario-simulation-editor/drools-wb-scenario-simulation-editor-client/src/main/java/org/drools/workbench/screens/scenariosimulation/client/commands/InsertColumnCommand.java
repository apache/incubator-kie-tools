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

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;

/**
 * <code>Command</code> to <b>insert</b> a column.
 */
@Dependent
public class InsertColumnCommand extends AbstractCommand {

    private ScenarioGridModel model;
    private String columnId;
    private int columnIndex;
    boolean isRight;

    public InsertColumnCommand() {
    }

    /**
     * @param model
     * @param columnId
     * @param columnIndex
     * @param isRight when <code>true</code>, column will be inserted to the right of the given index (i.e. at position columnIndex +1), otherwise to the left (i.e. at position columnIndex)
     * @param scenarioGridPanel
     * @param scenarioGridLayer
     */
    public InsertColumnCommand(ScenarioGridModel model, String columnId, int columnIndex, boolean isRight, ScenarioGridPanel scenarioGridPanel, ScenarioGridLayer scenarioGridLayer) {
        super(scenarioGridPanel, scenarioGridLayer);
        this.model = model;
        this.columnId = columnId;
        this.columnIndex = columnIndex;
        this.isRight = isRight;
    }

    @Override
    public void execute() {
        String columnGroup = ((ScenarioGridColumn) model.getColumns().get(columnIndex)).getInformationHeaderMetaData().getColumnGroup();
        int columnPosition = isRight ? columnIndex + 1 : columnIndex;
        FactMappingType factMappingType = FactMappingType.valueOf(columnGroup.toUpperCase());
        String columnTitle = FactMapping.getPlaceHolder(factMappingType, model.nextColumnCount());
        model.insertColumn(columnPosition, getScenarioGridColumnLocal(columnTitle,
                                                                      columnId,
                                                                      columnGroup,
                                                                      factMappingType,
                                                                      scenarioGridPanel,
                                                                      scenarioGridLayer,
                                                                      ScenarioSimulationEditorConstants.INSTANCE.defineValidType()));
    }
}
