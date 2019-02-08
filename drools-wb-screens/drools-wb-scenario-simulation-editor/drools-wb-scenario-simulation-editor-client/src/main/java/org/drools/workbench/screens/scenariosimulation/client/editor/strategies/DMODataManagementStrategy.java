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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelView;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.utils.ScenarioSimulationSharedUtils;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.callbacks.Callback;

public class DMODataManagementStrategy extends AbstractDataManagementStrategy {

    private AsyncPackageDataModelOracleFactory oracleFactory;
    protected AsyncPackageDataModelOracle oracle;
    protected ScenarioSimulationContext scenarioSimulationContext;

    //Package for which this Scenario Simulation relates
    protected String packageName = "";

    public DMODataManagementStrategy(final AsyncPackageDataModelOracleFactory oracleFactory, final ScenarioSimulationContext scenarioSimulationContext) {
        this.oracleFactory = oracleFactory;
        this.scenarioSimulationContext = scenarioSimulationContext;
    }

    @Override
    public void populateRightPanel(final RightPanelView.Presenter rightPanelPresenter, final ScenarioGridModel scenarioGridModel) {
        // Execute only when oracle has been set
        if (oracle == null || oracle.getFactTypes().length == 0) {
            return;
        }
        // Retrieve the relevant facttypes
        List<String> factTypes = Arrays.asList(oracle.getFactTypes());

        // Split the DMO from the Simple Java types
        final Map<Boolean, List<String>> partitionedFactTypes = factTypes.stream()
                .collect(Collectors.partitioningBy(factType -> SIMPLE_CLASSES_MAP.keySet().contains(factType)));

        final List<String> dataObjectsTypes = partitionedFactTypes.get(false);
        final List<String> simpleJavaTypes = partitionedFactTypes.get(true);

        int expectedElements = dataObjectsTypes.size();
        // Instantiate a dataObjects container map
        final SortedMap<String, FactModelTree> dataObjectsFieldsMap = new TreeMap<>();

        // Instantiate a map of already assigned properties
        final Map<String, List<String>> propertiesToHide = getPropertiesToHide(scenarioGridModel);

        // Instantiate the aggregator callback
        Callback<FactModelTree> aggregatorCallback = aggregatorCallback(rightPanelPresenter, expectedElements, dataObjectsFieldsMap, propertiesToHide, scenarioGridModel);
        // Iterate over all dataObjects to retrieve their modelfields
        dataObjectsTypes.forEach(factType ->
                                         oracle.getFieldCompletions(factType, fieldCompletionsCallback(factType, aggregatorCallback)));
        populateSimpleJavaTypes(simpleJavaTypes, rightPanelPresenter, scenarioGridModel);
    }

    @Override
    public void manageScenarioSimulationModelContent(ObservablePath currentPath, ScenarioSimulationModelContent toManage) {
        model = toManage.getModel();
        oracle = oracleFactory.makeAsyncPackageDataModelOracle(currentPath,
                                                               model,
                                                               toManage.getDataModel());
    }

    @Override
    public boolean isADataType(String value) {
        return oracle != null && Arrays.asList(oracle.getFactTypes()).contains(value);
    }

    public AsyncPackageDataModelOracle getOracle() {
        return oracle;
    }

    /**
     * This <code>Callback</code> will receive <code>ModelField[]</code> from <code>AsyncPackageDataModelOracleFactory.getFieldCompletions(final String,
     * final Callback&lt;ModelField[]&gt;)</code>; build a <code>FactModelTree</code> from them, and send it to the
     * given <code>Callback&lt;FactModelTree&gt;</code> aggregatorCallback
     * @param factName
     * @param aggregatorCallback
     * @return
     */
    protected Callback<ModelField[]> fieldCompletionsCallback(String factName, Callback<FactModelTree> aggregatorCallback) {
        return result -> fieldCompletionsCallbackMethod(factName, result, aggregatorCallback);
    }

    /**
     * Actual code of the <b>fieldCompletionsCallback</b>; isolated for testing
     * @param factName
     * @param result
     * @param aggregatorCallback
     */
    protected void fieldCompletionsCallbackMethod(String factName, ModelField[] result, Callback<FactModelTree> aggregatorCallback) {
        FactModelTree toSend = getFactModelTree(factName, result);
        aggregatorCallback.callback(toSend);
    }

    protected void populateSimpleJavaTypes(List<String> simpleJavaTypes, RightPanelView.Presenter rightPanelPresenter, final ScenarioGridModel scenarioGridModel) {
        // Instantiate a simpleJavaTypes container map
        SortedMap<String, FactModelTree> simpleJavaTypeFieldsMap = new TreeMap<>();
        simpleJavaTypes.forEach(factType -> simpleJavaTypeFieldsMap.put(factType, getSimpleClassFactModelTree(SIMPLE_CLASSES_MAP.get(factType))));
        rightPanelPresenter.setSimpleJavaTypeFieldsMap(simpleJavaTypeFieldsMap);
        SortedMap<String, FactModelTree> simpleJavaTypeInstanceFieldsMap = getInstanceMap(simpleJavaTypeFieldsMap);
        rightPanelPresenter.setSimpleJavaInstanceFieldsMap(simpleJavaTypeInstanceFieldsMap);
        Set<String> simpleJavaTypeInstancesName = new HashSet<>(simpleJavaTypeFieldsMap.keySet());
        simpleJavaTypeInstancesName.addAll(simpleJavaTypeInstanceFieldsMap.keySet());
        scenarioGridModel.setSimpleJavaTypeInstancesName(simpleJavaTypeInstancesName);
    }

    protected SortedMap<String, FactModelTree> getInstanceMap(SortedMap<String, FactModelTree> sourceMap) {
        SortedMap<String, FactModelTree> toReturn = new TreeMap<>();
        // map instance name to base class
        if (model != null) {
            final SimulationDescriptor simulationDescriptor = model.getSimulation().getSimulationDescriptor();
            simulationDescriptor.getUnmodifiableFactMappings()
                    .stream()
                    .filter(factMapping -> !Objects.equals(FactMappingType.OTHER, factMapping.getExpressionIdentifier().getType()))
                    .forEach(factMapping -> {
                        String dataObjectName = factMapping.getFactIdentifier().getClassName();
                        if (dataObjectName.contains(".")) {
                            dataObjectName = dataObjectName.substring(dataObjectName.lastIndexOf(".") + 1);
                        }
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

    /**
     * Create a <code>FactModelTree</code> for a given <b>factName</b> populating it with the given
     * <code>ModelField[]</code>
     * @param factName
     * @param modelFields
     * @return
     * @implNote For the moment being, due to current implementation of <b>DMO</b>, it it not possible to retrieve <b>all</b>
     * the generic types of a class with more then one, but only the last one. So, for <code>Map</code>, the <b>key</b>
     * will allways be a <code>java.lang.String</code>
     */
    protected FactModelTree getFactModelTree(String factName, ModelField[] modelFields) {
        Map<String, String> simpleProperties = new HashMap<>();
        Map<String, List<String>> genericTypesMap = new HashMap<>();
        String factPackageName = packageName;
        String fullFactClassName = oracle.getFQCNByFactName(factName);
        if (fullFactClassName != null && fullFactClassName.contains(".")) {
            factPackageName = fullFactClassName.substring(0, fullFactClassName.lastIndexOf("."));
        }
        for (ModelField modelField : modelFields) {
            if (!modelField.getName().equals("this")) {
                String className = SIMPLE_CLASSES_MAP.containsKey(modelField.getClassName()) ? SIMPLE_CLASSES_MAP.get(modelField.getClassName()).getCanonicalName() : modelField.getClassName();
                simpleProperties.put(modelField.getName(), className);
                if (ScenarioSimulationSharedUtils.isCollection(className)) {
                    populateGenericTypeMap(genericTypesMap, factName, modelField.getName(), ScenarioSimulationSharedUtils.isList(className));
                }
            }
        }
        return new FactModelTree(factName, factPackageName, simpleProperties, genericTypesMap);
    }

    /**
     * Populate the given <code>Map</code> with the generic type(s) of given property.
     * If <code>isList</code> is false, the first generic will be <b>java.lang.String</b>
     * @param toPopulate
     * @param factName
     * @param propertyName
     * @param isList
     * @implNote due to current DMO implementation, it is not possible to retrive <b>all</b> generic types of a given class, but only the last one; for the moment being, the generic type
     * for <code>Map</code> will be <b>java.lang.String</b>
     */
    protected void populateGenericTypeMap(Map<String, List<String>> toPopulate, String factName, String propertyName, boolean isList) {
        List<String> genericTypes = new ArrayList<>();
        if (!isList) {
            genericTypes.add(String.class.getName());
        }
        String genericInfo = oracle.getParametricFieldType(factName, propertyName);
        String fullGenericInfoClassName = oracle.getFQCNByFactName(genericInfo);
        genericTypes.add(fullGenericInfoClassName);
        toPopulate.put(propertyName, genericTypes);
    }

    /**
     * This <code>Callback</code> will receive data from other callbacks and when the retrieved results get to the
     * expected ones it will recursively elaborate the map
     *
     * @param rightPanelPresenter
     * @param expectedElements
     * @param factTypeFieldsMap
     * @param propertiesToHide  key: the name of the Fact class (ex. Author), value: list of properties to hide from right panel
     * @param scenarioGridModel
     * @return
     */
    protected Callback<FactModelTree> aggregatorCallback(final RightPanelView.Presenter rightPanelPresenter, final int expectedElements, SortedMap<String, FactModelTree> factTypeFieldsMap, final Map<String, List<String>> propertiesToHide, final ScenarioGridModel scenarioGridModel) {
        return result -> aggregatorCallbackMethod(rightPanelPresenter, expectedElements, factTypeFieldsMap, propertiesToHide, scenarioGridModel, result);
    }

    /**
     * Actual code of the <b>aggregatorCallback</b>; isolated for testing
     *
     * @param rightPanelPresenter
     * @param expectedElements
     * @param factTypeFieldsMap
     * @param propertiesToHide  key: the name of the Fact class (ex. Author), value: list of properties to hide from right panel
     * @param scenarioGridModel
     * @param result
     */
    protected void aggregatorCallbackMethod(final RightPanelView.Presenter rightPanelPresenter, final int expectedElements, SortedMap<String, FactModelTree> factTypeFieldsMap, final Map<String, List<String>> propertiesToHide, final ScenarioGridModel scenarioGridModel, final FactModelTree result) {
        factTypeFieldsMap.put(result.getFactName(), result);
        if (factTypeFieldsMap.size() == expectedElements) {
            factTypeFieldsMap.values().forEach(factModelTree -> populateFactModelTree(factModelTree, factTypeFieldsMap, propertiesToHide));
            rightPanelPresenter.setDataObjectFieldsMap(factTypeFieldsMap);
            scenarioSimulationContext.setDataObjectFieldsMap(factTypeFieldsMap);
            SortedMap<String, FactModelTree> instanceFieldsMap = getInstanceMap(factTypeFieldsMap);
            rightPanelPresenter.setInstanceFieldsMap(instanceFieldsMap);
            Set<String> dataObjectsInstancesName = new HashSet<>(factTypeFieldsMap.keySet());
            dataObjectsInstancesName.addAll(instanceFieldsMap.keySet());
            scenarioGridModel.setDataObjectsInstancesName(dataObjectsInstancesName);
        }
    }

    /**
     * This method replace a <b>simple property</b> from the given <code>FactModelTree</code> and replace it with
     * an <b>expandable property</b> <code>FactModelTree</code> from the given  <b>factTypeFieldsMap</b>, if a matching
     * element exists.
     * @param toPopulate
     * @param factTypeFieldsMap
     * @param propertiesToHide key: the name of the Fact class (ex. Author), value: list of properties to hide from right panel
     */
    protected void populateFactModelTree(FactModelTree toPopulate, final SortedMap<String, FactModelTree> factTypeFieldsMap, final Map<String, List<String>> propertiesToHide) {
        List<String> toRemove = new ArrayList<>();
        toPopulate.getSimpleProperties().forEach((key, value) -> {
            if (factTypeFieldsMap.containsKey(value)) {
                toRemove.add(key);
                toPopulate.addExpandableProperty(key, factTypeFieldsMap.get(value).getFactName());
            }
        });
        if (propertiesToHide.containsKey(toPopulate.getFactName())) {
            toRemove.addAll(propertiesToHide.get(toPopulate.getFactName()));
        }
        toRemove.forEach(toPopulate::removeSimpleProperty);
    }
}
