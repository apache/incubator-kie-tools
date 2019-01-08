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
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;

import static org.drools.workbench.screens.scenariosimulation.model.FactMapping.getPropertyPlaceHolder;

/**
 * <code>Command</code> to set the <i>instance</i> level header for a given column
 */
@Dependent
public class SetInstanceHeaderCommand extends AbstractScenarioSimulationCommand {

    public SetInstanceHeaderCommand() {
        super(true);
    }

    @Override
    protected void internalExecute(ScenarioSimulationContext context) {
        final ScenarioSimulationContext.Status status = context.getStatus();
        ScenarioGridColumn selectedColumn = (ScenarioGridColumn) context.getModel().getSelectedColumn();
        if (selectedColumn == null) {
            return;
        }
        int columnIndex = context.getModel().getColumns().indexOf(selectedColumn);
        String fullPackage = status.getFullPackage();
        if (!fullPackage.endsWith(".")) {
            fullPackage += ".";
        }
        String className = status.getClassName();
        String canonicalClassName = fullPackage + className;
        final ScenarioSimulationModel.Type simulationModelType = context.getModel().getSimulation().get().getSimulationDescriptor().getType();
        selectedColumn.setEditableHeaders(!simulationModelType.equals(ScenarioSimulationModel.Type.DMN));
        String nameToUseForCreation = simulationModelType.equals(ScenarioSimulationModel.Type.DMN) ? className : selectedColumn.getInformationHeaderMetaData().getColumnId();
        FactIdentifier factIdentifier = getFactIdentifierByColumnTitle(className, context).orElse(FactIdentifier.create(nameToUseForCreation, canonicalClassName));
        final ScenarioHeaderMetaData informationHeaderMetaData = selectedColumn.getInformationHeaderMetaData();
        informationHeaderMetaData.setTitle(className);
        selectedColumn.setInstanceAssigned(true);
        final ScenarioHeaderMetaData propertyHeaderMetaData = selectedColumn.getPropertyHeaderMetaData();
        selectedColumn.setPlaceHolder(ScenarioSimulationEditorConstants.INSTANCE.defineValidType());
        propertyHeaderMetaData.setTitle(getPropertyPlaceHolder(columnIndex));
        propertyHeaderMetaData.setReadOnly(false);
        selectedColumn.setFactIdentifier(factIdentifier);
        context.getModel().updateColumnInstance(columnIndex, selectedColumn);
    }
}
