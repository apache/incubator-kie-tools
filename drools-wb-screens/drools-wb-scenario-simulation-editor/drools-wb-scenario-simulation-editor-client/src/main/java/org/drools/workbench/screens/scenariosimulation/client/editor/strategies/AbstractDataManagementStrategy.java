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
package org.drools.workbench.screens.scenariosimulation.client.editor.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionElement;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;

/**
 * Abstract class to provide common methods to be used by actual implementations.
 */
public abstract class AbstractDataManagementStrategy implements DataManagementStrategy {

    protected ScenarioSimulationModel model;

    @Override
    public void setModel(ScenarioSimulationModel model) {
        this.model = model;
    }

    protected static FactModelTree getSimpleClassFactModelTree(Class clazz) {
        String key = clazz.getSimpleName();
        Map<String, String> simpleProperties = new HashMap<>();
        String fullName = clazz.getCanonicalName();
        simpleProperties.put("value", fullName);
        String packageName = fullName.substring(0, fullName.lastIndexOf("."));
        return new FactModelTree(key, packageName, simpleProperties, new HashMap<>());
    }

    /**
     * This method returns a <code>Map</code> with the properties of a given <b>Type</b> (Fact, class, other dmn defined) instance,
     * to be hidden from the right panel.
     * key: the name of the Fact class (ex. Author), value: list of properties to hide from right panel
     * If click happen on an already assigned property, <b>all</b> all the properties of given type should be shown;
     * if, instead, click is on an unassigned property, the already assigned properties must be hidden.
     * (e.g. inside GIVEN there is an "Author" group; if clicking on "books" property header, the <b>value</b> of the <code>Map</code> returned by this method is an <b>empty</b> <code>List</code>;
     * if click is on an unassigned property column, the <b>value</b> of the <code>Map</code> returned by this method is a <code>List</code>.
     * with all the <b>already assigned</b> Author's properties)
     * @param scenarioGridModel
     * @return
     */
    protected Map<String, List<String>> getPropertiesToHide(ScenarioGridModel scenarioGridModel) {
        final Map<String, List<String>> toReturn = new HashMap<>();
        final ScenarioGridColumn selectedColumn = (ScenarioGridColumn) scenarioGridModel.getSelectedColumn();
        if (selectedColumn != null) {
            if (selectedColumn.isInstanceAssigned()) {
                toReturn.put(selectedColumn.getInformationHeaderMetaData().getTitle(), getPropertiesToHide(selectedColumn, scenarioGridModel));
            }
        }
        return toReturn;
    }

    /**
     * This method returns a <code>List</code> with the properties of a given <b>Type</b> (Fact, class, other dmn defined) instance,
     * to be hidden from the right panel for the selected column.
     * <p>
     * If click happen on an already assigned property, <b>all</b> all the properties of given type should be shown;
     * if, instead, click is on an unassigned property, the already assigned properties must be hidden.
     * (e.g. inside GIVEN there is an "Author" group; if clicking on "books" property header, this method returns  an <b>empty</b> <code>List</code>;
     * if click is on an unassigned property column, this method returns a <code>List</code>.
     * with all the <b>already assigned</b> Author's properties)
     * @param selectedColumn
     * @param scenarioGridModel
     * @return
     */
    protected List<String> getPropertiesToHide(ScenarioGridColumn selectedColumn, ScenarioGridModel scenarioGridModel) {
        List<String> toReturn = new ArrayList<>();
        if (!selectedColumn.isPropertyAssigned()) {
            scenarioGridModel.getSimulation().ifPresent(simulation -> {
                final SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
                List<ScenarioGridColumn> instanceColumns = scenarioGridModel.getInstanceScenarioGridColumns(selectedColumn);
                toReturn.addAll(instanceColumns.stream()
                                        .filter(ScenarioGridColumn::isPropertyAssigned)
                                        .map(instanceColumn ->  scenarioGridModel.getColumns().indexOf(instanceColumn))
                                        .map(columnIndex -> {
                                            final List<ExpressionElement> expressionElements = simulationDescriptor.getFactMappingByIndex(columnIndex).getExpressionElements();
                                            expressionElements.get(expressionElements.size() -1);
                                            return expressionElements.get(expressionElements.size() -1).getStep();
                                        })
                                        .collect(Collectors.toList()));
            });
        }
        return toReturn;
    }
}
