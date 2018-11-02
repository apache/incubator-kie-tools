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

import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;

import static org.drools.workbench.screens.scenariosimulation.model.FactMapping.getPropertyPlaceHolder;

/**
 * <code>Command</code> to set the <i>instance</i> level header for a given column
 */
@Dependent
public class SetInstanceHeaderCommand extends AbstractCommand {

    private ScenarioGridModel model;
    private String fullPackage;
    private String className;

    public SetInstanceHeaderCommand() {
    }

    /**
     * @param model
     * @param fullPackage
     * @param className
     * @param scenarioGridPanel
     * @param scenarioGridLayer
     */
    public SetInstanceHeaderCommand(ScenarioGridModel model, String fullPackage, String className, ScenarioGridPanel scenarioGridPanel, ScenarioGridLayer scenarioGridLayer) {
        super(scenarioGridPanel, scenarioGridLayer);
        this.model = model;
        this.fullPackage = fullPackage;
        this.className = className;
    }

    @Override
    public void execute() {
        ScenarioGridColumn selectedColumn = (ScenarioGridColumn) model.getSelectedColumn();
        if (selectedColumn == null) {
            return;
        }
        int columnIndex = model.getColumns().indexOf(selectedColumn);
        if (!fullPackage.endsWith(".")) {
            fullPackage += ".";
        }
        String canonicalClassName = fullPackage + className;
        FactIdentifier factIdentifier = getFactIdentifierByColumnTitle(className).orElse(FactIdentifier.create(selectedColumn.getInformationHeaderMetaData().getColumnId(), canonicalClassName));
        final ScenarioHeaderMetaData informationHeaderMetaData = selectedColumn.getInformationHeaderMetaData();
        informationHeaderMetaData.setTitle(className);
        selectedColumn.setInstanceAssigned(true);
        final ScenarioHeaderMetaData propertyHeaderMetaData = selectedColumn.getPropertyHeaderMetaData();
        selectedColumn.setPlaceHolder(ScenarioSimulationEditorConstants.INSTANCE.defineValidType());
        propertyHeaderMetaData.setTitle(getPropertyPlaceHolder(columnIndex));
        propertyHeaderMetaData.setReadOnly(false);
        selectedColumn.setFactIdentifier(factIdentifier);
        model.updateColumnInstance(columnIndex, selectedColumn);
    }
}
