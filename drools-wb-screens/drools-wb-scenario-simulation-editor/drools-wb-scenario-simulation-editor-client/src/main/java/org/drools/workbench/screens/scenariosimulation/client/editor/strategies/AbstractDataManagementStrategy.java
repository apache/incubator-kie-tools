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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;

/**
 * Abstract class to provide common methods to be used by actual implementations.
 */
public abstract class AbstractDataManagementStrategy implements DataManagementStrategy {

    protected ScenarioSimulationModel model;
    protected ScenarioSimulationContext scenarioSimulationContext;
    protected ResultHolder factModelTreeHolder = new ResultHolder();

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
        FactModelTree toReturn = new FactModelTree(key, packageName, simpleProperties, new HashMap<>());
        toReturn.setSimple(true);
        return toReturn;
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
    protected Map<String, List<List<String>>> getPropertiesToHide(ScenarioGridModel scenarioGridModel) {
        final Map<String, List<List<String>>> toReturn = new HashMap<>();
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
    protected List<List<String>> getPropertiesToHide(ScenarioGridColumn selectedColumn, ScenarioGridModel scenarioGridModel) {
        List<List<String>> toReturn = new ArrayList<>();
        if (!selectedColumn.isPropertyAssigned()) {
            scenarioGridModel.getSimulation().ifPresent(simulation -> {
                final SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
                List<ScenarioGridColumn> instanceColumns = scenarioGridModel.getInstanceScenarioGridColumns(selectedColumn);
                toReturn.addAll(instanceColumns.stream()
                                        .filter(ScenarioGridColumn::isPropertyAssigned)
                                        .map(instanceColumn -> scenarioGridModel.getColumns().indexOf(instanceColumn))
                                        .map(columnIndex -> {
                                            List<String> propertyNameElements = simulationDescriptor.getFactMappingByIndex(columnIndex).getExpressionElementsWithoutClass()
                                                    .stream()
                                                    .map(ExpressionElement::getStep)
                                                    .collect(Collectors.toList());
                                            if (propertyNameElements.isEmpty()) {
                                                propertyNameElements.add("value");
                                            }
                                            return Collections.unmodifiableList(propertyNameElements);
                                        })
                                        .collect(Collectors.toList()));
            });
        }
        return toReturn;
    }

    /**
     * Store data in required target objects
     */
    protected void storeData(final FactModelTuple factModelTuple, final TestToolsView.Presenter testToolsPresenter, final ScenarioGridModel scenarioGridModel) {
        // Instantiate a map of already assigned properties
        final Map<String, List<List<String>>> propertiesToHide = getPropertiesToHide(scenarioGridModel);
        final SortedMap<String, FactModelTree> visibleFacts = factModelTuple.getVisibleFacts();
        final Map<Boolean, List<Map.Entry<String, FactModelTree>>> partitionBy = visibleFacts.entrySet().stream()
                .collect(Collectors.partitioningBy(stringFactModelTreeEntry -> stringFactModelTreeEntry.getValue().isSimple()));
        final SortedMap<String, FactModelTree> complexDataObjects = new TreeMap<>(partitionBy.get(false).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        final SortedMap<String, FactModelTree> simpleDataObjects = new TreeMap<>(partitionBy.get(true).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        final SortedMap<String, FactModelTree> instanceFieldsMap = getInstanceMap(complexDataObjects);
        final SortedMap<String, FactModelTree> simpleJavaTypeInstanceFieldsMap = getInstanceMap(simpleDataObjects);
        // Update right panel
        testToolsPresenter.setDataObjectFieldsMap(complexDataObjects);
        testToolsPresenter.setSimpleJavaTypeFieldsMap(simpleDataObjects);
        testToolsPresenter.setInstanceFieldsMap(instanceFieldsMap);
        testToolsPresenter.setSimpleJavaInstanceFieldsMap(simpleJavaTypeInstanceFieldsMap);

        testToolsPresenter.setHiddenFieldsMap(factModelTuple.getHiddenFacts());
        testToolsPresenter.hideProperties(propertiesToHide);
        // Update context
        SortedMap<String, FactModelTree> context = new TreeMap<>();
        context.putAll(visibleFacts);
        context.putAll(factModelTuple.getHiddenFacts());
        scenarioSimulationContext.setDataObjectFieldsMap(context);
        // Update model
        Set<String> dataObjectsInstancesName = new HashSet<>(visibleFacts.keySet());
        dataObjectsInstancesName.addAll(instanceFieldsMap.keySet());
        scenarioGridModel.setDataObjectsInstancesName(dataObjectsInstancesName);
        Set<String> simpleJavaTypeInstancesName = new HashSet<>(simpleDataObjects.keySet());
        simpleJavaTypeInstancesName.addAll(simpleJavaTypeInstanceFieldsMap.keySet());
        scenarioGridModel.setSimpleJavaTypeInstancesName(simpleJavaTypeInstancesName);
    }

    /**
     * Returns a <code>Map</code> of the <b>instances</b> as defined in the grid and the mapped <code>FactModelTree</code>
     * @param sourceMap
     * @return
     */
    protected SortedMap<String, FactModelTree> getInstanceMap(SortedMap<String, FactModelTree> sourceMap) {
        SortedMap<String, FactModelTree> toReturn = new TreeMap<>();
        // map instance name to base class
        if (model != null) {
            final SimulationDescriptor simulationDescriptor = model.getSimulation().getSimulationDescriptor();
            simulationDescriptor.getUnmodifiableFactMappings()
                    .stream()
                    .filter(factMapping -> !Objects.equals(FactMappingType.OTHER, factMapping.getExpressionIdentifier().getType()))
                    .forEach(factMapping -> {
                        String dataObjectName = factMapping.getFactIdentifier().getClassNameWithoutPackage();
                        final String instanceName = factMapping.getFactAlias();
                        if (!instanceName.equals(dataObjectName)) {
                            final FactModelTree factModelTree = sourceMap.get(dataObjectName);
                            if (factModelTree != null) {
                                toReturn.put(instanceName, factModelTree);
                            }
                        }
                    });
        }
        return toReturn;
    }

    static protected class ResultHolder {

        FactModelTuple factModelTuple;

        public FactModelTuple getFactModelTuple() {
            return factModelTuple;
        }

        public void setFactModelTuple(FactModelTuple factModelTuple) {
            this.factModelTuple = factModelTuple;
        }
    }
}
