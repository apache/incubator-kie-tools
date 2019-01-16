/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Optional;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;

import static org.drools.workbench.screens.scenariosimulation.model.FactMapping.getPropertyPlaceHolder;

/**
 * <b>Abstract</b> <code>Command</code> class to provide common methods used by <code>SetInstanceHeaderCommand</code> and <code>SetPropertyHeaderCommand</code> implementations
 */
public abstract class AbstractSetHeaderCommand extends AbstractScenarioSimulationCommand {

    public AbstractSetHeaderCommand() {
        super(true);
    }

    protected abstract void executeIfSelectedColumn(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn);

    @Override
    protected void internalExecute(ScenarioSimulationContext context) {
        getSelectedColumn(context).ifPresent(selectedColumn -> {
                                                 executeIfSelectedColumn(context, selectedColumn);
                                             }
        );
    }

    /**
     * Sets the instance header for a <code>ScenarioSimulationContext</code>.
     * @param context
     * @param selectedColumn
     */
    protected void setInstanceHeader(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn) {
        final ScenarioSimulationContext.Status status = context.getStatus();

        int columnIndex = context.getModel().getColumns().indexOf(selectedColumn);
        String className = status.getClassName();
        String canonicalClassName = getFullPackage(context) + className;
        FactIdentifier factIdentifier = setEditableHeadersAndGetFactIdentifier(context, selectedColumn, className, canonicalClassName);
        setInstanceHeaderMetaData(selectedColumn, className, factIdentifier);
        final ScenarioHeaderMetaData propertyHeaderMetaData = selectedColumn.getPropertyHeaderMetaData();
        setPropertyMetaData(propertyHeaderMetaData, getPropertyPlaceHolder(columnIndex), false, selectedColumn, ScenarioSimulationEditorConstants.INSTANCE.defineValidType());
        context.getModel().updateColumnInstance(columnIndex, selectedColumn);
    }

    /**
     * Returns an <code>Optional<ScenarioGridColumn></code> for a <code>ScenarioSimulationContext</code>.
     * @param context
     * @return
     */
    protected Optional<ScenarioGridColumn> getSelectedColumn(ScenarioSimulationContext context) {
        return Optional.ofNullable((ScenarioGridColumn) context.getModel().getSelectedColumn());
    }

    /**
     * Returns the full package <code>String</code> of a <code>ScenarioSimulationContext</code>.
     * @param context
     * @return
     */
    protected String getFullPackage(ScenarioSimulationContext context) {
        String fullPackage = context.getStatus().getFullPackage();
        if (!fullPackage.endsWith(".")) {
            fullPackage += ".";
        }
        return fullPackage;
    }

    /**
     * Sets the editable headers on a given <code>ScenarioGridColumn</code> and returns a <code>FactIdentifier</code>.
     * @param context
     * @param selectedColumn
     * @param className
     * @param canonicalClassName
     * @return
     */
    protected FactIdentifier setEditableHeadersAndGetFactIdentifier(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn, String className, String canonicalClassName) {
        final ScenarioSimulationModel.Type simulationModelType = context.getModel().getSimulation().get().getSimulationDescriptor().getType();
        selectedColumn.setEditableHeaders(!simulationModelType.equals(ScenarioSimulationModel.Type.DMN));
        String nameToUseForCreation = simulationModelType.equals(ScenarioSimulationModel.Type.DMN) ? className : selectedColumn.getInformationHeaderMetaData().getColumnId();
        return getFactIdentifierByColumnTitle(className, context).orElse(FactIdentifier.create(nameToUseForCreation, canonicalClassName));
    }

    /**
     * Sets the metadata for an instance header on a given <code>ScenarioGridColumn</code>.
     * @param scenarioGridColumn
     * @param className
     * @param factIdentifier
     */
    protected void setInstanceHeaderMetaData(ScenarioGridColumn scenarioGridColumn, String className, FactIdentifier factIdentifier) {
        scenarioGridColumn.getInformationHeaderMetaData().setTitle(className);
        scenarioGridColumn.setInstanceAssigned(true);
        scenarioGridColumn.setFactIdentifier(factIdentifier);
    }

    /**
     * Sets the title and readOnly setting of a property header and sets the place holder on a given <code>ScenarioGridColumn</code>.
     * @param propertyHeaderMetaData
     * @param title
     * @param readOnly
     * @param selectedColumn
     * @param placeHolder
     */
    protected void setPropertyMetaData(ScenarioHeaderMetaData propertyHeaderMetaData, String title, boolean readOnly, ScenarioGridColumn selectedColumn, String placeHolder) {
        propertyHeaderMetaData.setTitle(title);
        propertyHeaderMetaData.setReadOnly(readOnly);
        selectedColumn.setPlaceHolder(placeHolder);
    }
}