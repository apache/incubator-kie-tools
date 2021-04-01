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

import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.models.AbstractScesimGridModel;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenterData;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;

/**
 * Abstract class to provide common methods to be used by actual implementations.
 */
public abstract class AbstractDataManagementStrategy implements DataManagementStrategy {

    protected ScenarioSimulationModel model;
    protected ResultHolder factModelTreeHolder = new ResultHolder();

    @Override
    public void setModel(ScenarioSimulationModel model) {
        this.model = model;
    }

    public static FactModelTree getSimpleClassFactModelTree(String simpleClass, String canonicalName) {
        Map<String, FactModelTree.PropertyTypeName> simpleProperties = new HashMap<>();
        simpleProperties.put(VALUE, new FactModelTree.PropertyTypeName(canonicalName));
        String packageName = canonicalName.substring(0, canonicalName.lastIndexOf('.'));
        String factClassName = canonicalName.substring(canonicalName.lastIndexOf('.') + 1);
        FactModelTree toReturn = FactModelTree.ofDMO(simpleClass, packageName, simpleProperties, new HashMap<>(), factClassName);
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
     * @param abstractScesimGridModel
     * @return
     */
    public <T extends AbstractScesimModel<E>, E extends AbstractScesimData> Map<String, List<List<String>>> getPropertiesToHide(AbstractScesimGridModel<T, E> abstractScesimGridModel) {
        final Map<String, List<List<String>>> toReturn = new HashMap<>();
        final ScenarioGridColumn selectedColumn = (ScenarioGridColumn) abstractScesimGridModel.getSelectedColumn();
        if (selectedColumn != null && selectedColumn.isInstanceAssigned()) {
            toReturn.put(selectedColumn.getInformationHeaderMetaData().getTitle(), getPropertiesToHide(selectedColumn, abstractScesimGridModel));
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
     * @param abstractScesimGridModel
     * @return
     */
    protected <T extends AbstractScesimModel<E>, E extends AbstractScesimData> List<List<String>> getPropertiesToHide(ScenarioGridColumn selectedColumn, AbstractScesimGridModel<T, E> abstractScesimGridModel) {
        List<List<String>> toReturn = new ArrayList<>();
        if (!selectedColumn.isPropertyAssigned()) {
            abstractScesimGridModel.getAbstractScesimModel().ifPresent(simulation -> {
                final ScesimModelDescriptor simulationDescriptor = simulation.getScesimModelDescriptor();
                List<ScenarioGridColumn> instanceColumns = abstractScesimGridModel.getInstanceScenarioGridColumns(selectedColumn);
                toReturn.addAll(instanceColumns.stream()
                                        .filter(ScenarioGridColumn::isPropertyAssigned)
                                        .map(instanceColumn -> abstractScesimGridModel.getColumns().indexOf(instanceColumn))
                                        .map(columnIndex -> {
                                            List<String> propertyNameElements = simulationDescriptor.getFactMappingByIndex(columnIndex).getExpressionElementsWithoutClass()
                                                    .stream()
                                                    .map(ExpressionElement::getStep)
                                                    .collect(Collectors.toList());
                                            if (propertyNameElements.isEmpty()) {
                                                propertyNameElements.add(VALUE);
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
    public void storeData(final FactModelTuple factModelTuple,
                          final TestToolsView.Presenter testToolsPresenter,
                          final ScenarioSimulationContext context,
                          final GridWidget gridWidget) {
        // Instantiate a map of already assigned properties
        final Map<String, List<List<String>>> propertiesToHide = getPropertiesToHide(context.getAbstractScesimGridModelByGridWidget(gridWidget));
        final SortedMap<String, FactModelTree> visibleFacts = factModelTuple.getVisibleFacts();
        final Map<Boolean, List<Map.Entry<String, FactModelTree>>> partitionBy = visibleFacts.entrySet().stream()
                .collect(Collectors.partitioningBy(stringFactModelTreeEntry -> stringFactModelTreeEntry.getValue().isSimple()));
        final SortedMap<String, FactModelTree> complexDataObjects = new TreeMap<>(partitionBy.get(false).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        final SortedMap<String, FactModelTree> simpleDataObjects = new TreeMap<>(partitionBy.get(true).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        // Update context
        SortedMap<String, FactModelTree> dataObjectFieldsMap = new TreeMap<>();
        dataObjectFieldsMap.putAll(visibleFacts);
        dataObjectFieldsMap.putAll(factModelTuple.getHiddenFacts());
        context.setDataObjectFieldsMap(dataObjectFieldsMap);
        // Update model
        // Avoid Collections.emptySortedMap() due to "The method emptySortedMap() is undefined for the type Collections" error
        SortedMap<String, FactModelTree> instanceFieldsMap = new TreeMap<>();
        SortedMap<String, FactModelTree> simpleJavaTypeInstanceFieldsMap = new TreeMap<>();
        if (GridWidget.SIMULATION.equals(gridWidget)) {
            instanceFieldsMap = getInstanceMap(complexDataObjects);
            simpleJavaTypeInstanceFieldsMap = getInstanceMap(simpleDataObjects);
            Set<String> dataObjectsInstancesName = new HashSet<>(visibleFacts.keySet());
            dataObjectsInstancesName.addAll(instanceFieldsMap.keySet());
            context.setDataObjectsInstancesName(dataObjectsInstancesName);
            Set<String> simpleJavaTypeInstancesName = new HashSet<>(simpleDataObjects.keySet());
            simpleJavaTypeInstancesName.addAll(simpleJavaTypeInstanceFieldsMap.keySet());
            context.getAbstractScesimGridModelByGridWidget(gridWidget).setSimpleJavaTypeInstancesName(simpleJavaTypeInstancesName);
        }
        // Update right panel
        TestToolsPresenterData testToolsPresenterData = new TestToolsPresenterData(complexDataObjects,
                                                                                   simpleDataObjects,
                                                                                   instanceFieldsMap,
                                                                                   simpleJavaTypeInstanceFieldsMap,
                                                                                   factModelTuple.getHiddenFacts(),
                                                                                   propertiesToHide,
                                                                                   gridWidget);
        testToolsPresenter.populateTestTools(testToolsPresenterData);
    }

    /**
     * Returns a <code>Map</code> of the <b>instances</b> as defined in the grid and the mapped <code>FactModelTree</code>
     * @param sourceMap
     * @return
     */
    public SortedMap<String, FactModelTree> getInstanceMap(SortedMap<String, FactModelTree> sourceMap) {
        SortedMap<String, FactModelTree> toReturn = new TreeMap<>();
        // map instance name to base class
        if (model != null) {
            final ScesimModelDescriptor simulationDescriptor = model.getSimulation().getScesimModelDescriptor();
            final ScenarioSimulationModel.Type type = model.getSettings().getType();
            simulationDescriptor.getUnmodifiableFactMappings()
                    .stream()
                    .filter(factMapping -> !Objects.equals(FactMappingType.OTHER, factMapping.getExpressionIdentifier().getType()))
                    .forEach(factMapping -> {
                        String dataObjectName = ScenarioSimulationModel.Type.DMN.equals(type) ?
                                factMapping.getFactIdentifier().getClassName() :
                                factMapping.getFactIdentifier().getClassNameWithoutPackage().replace("$", ".");
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

    public static class ResultHolder {

        FactModelTuple factModelTuple;

        public FactModelTuple getFactModelTuple() {
            return factModelTuple;
        }

        public void setFactModelTuple(FactModelTuple factModelTuple) {
            this.factModelTuple = factModelTuple;
        }
    }
}
