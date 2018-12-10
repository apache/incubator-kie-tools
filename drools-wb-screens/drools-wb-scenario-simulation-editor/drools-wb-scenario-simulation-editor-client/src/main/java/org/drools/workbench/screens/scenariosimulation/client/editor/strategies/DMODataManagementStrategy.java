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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.drools.workbench.screens.scenariosimulation.client.models.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelView;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.callbacks.Callback;

public class DMODataManagementStrategy implements DataManagementStrategy {

    private AsyncPackageDataModelOracleFactory oracleFactory;
    protected AsyncPackageDataModelOracle oracle;

    //Package for which this Scenario Simulation relates
    protected String packageName = "";

    private ScenarioSimulationModel model;

    public DMODataManagementStrategy(final AsyncPackageDataModelOracleFactory oracleFactory) {
        this.oracleFactory = oracleFactory;
    }

    @Override
    public void populateRightPanel(final RightPanelView.Presenter rightPanelPresenter, final ScenarioGridModel scenarioGridModel) {
        // Instantiate a container map
        SortedMap<String, FactModelTree> factTypeFieldsMap = new TreeMap<>();
        // Execute only when oracle has been set
        if (oracle == null) {
            if (rightPanelPresenter != null) {
                rightPanelPresenter.setDataObjectFieldsMap(factTypeFieldsMap);
            }
            return;
        }
        // Retrieve the relevant facttypes
        String[] factTypes = oracle.getFactTypes();
        if (factTypes.length == 0) {  // We do not have to set nothing
            if (rightPanelPresenter != null) {
                rightPanelPresenter.setDataObjectFieldsMap(factTypeFieldsMap);
            }
            return;
        }
        // Instantiate the aggregator callback
        Callback<FactModelTree> aggregatorCallback = aggregatorCallback(rightPanelPresenter, factTypes.length, factTypeFieldsMap, scenarioGridModel);
        // Iterate over all facttypes to retrieve their modelfields
        for (String factType : factTypes) {
            oracle.getFieldCompletions(factType, fieldCompletionsCallback(factType, aggregatorCallback));
        }
    }

    @Override
    public void manageScenarioSimulationModelContent(ObservablePath currentPath, ScenarioSimulationModelContent toManage) {
        model = toManage.getModel();
        oracle = oracleFactory.makeAsyncPackageDataModelOracle(currentPath,
                                                               model,
                                                               toManage.getDataModel());
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

    /**
     * Create a <code>FactModelTree</code> for a given <b>factName</b> populating it with the given
     * <code>ModelField[]</code>
     * @param factName
     * @param modelFields
     * @return
     */
    protected FactModelTree getFactModelTree(String factName, ModelField[] modelFields) {
        Map<String, String> simpleProperties = new HashMap<>();
        for (ModelField modelField : modelFields) {
            if (!modelField.getName().equals("this")) {
                simpleProperties.put(modelField.getName(), modelField.getClassName());
            }
        }
        String factPackageName = packageName;
        String fullFactClassName = oracle.getFQCNByFactName(factName);
        if (fullFactClassName != null && fullFactClassName.contains(".")) {
            factPackageName = fullFactClassName.substring(0, fullFactClassName.lastIndexOf("."));
        }
        return new FactModelTree(factName, factPackageName, simpleProperties);
    }

    /**
     * This <code>Callback</code> will receive data from other callbacks and when the retrieved results get to the
     * expected ones it will recursively elaborate the map
     * @param rightPanelPresenter
     * @param expectedElements
     * @param factTypeFieldsMap
     * @return
     */
    protected Callback<FactModelTree> aggregatorCallback(final RightPanelView.Presenter rightPanelPresenter, final int expectedElements, SortedMap<String, FactModelTree> factTypeFieldsMap, final ScenarioGridModel scenarioGridModel) {
        return result -> aggregatorCallbackMethod(rightPanelPresenter, expectedElements, factTypeFieldsMap, scenarioGridModel, result);
    }

    /**
     * Actual code of the <b>aggregatorCallback</b>; isolated for testing
     * @param rightPanelPresenter
     * @param expectedElements
     * @param factTypeFieldsMap
     * @param scenarioGridModel
     * @param result
     */
    protected void aggregatorCallbackMethod(final RightPanelView.Presenter rightPanelPresenter, final int expectedElements, SortedMap<String, FactModelTree> factTypeFieldsMap, final ScenarioGridModel scenarioGridModel, final FactModelTree result) {
        factTypeFieldsMap.put(result.getFactName(), result);
        if (factTypeFieldsMap.size() == expectedElements) {
            factTypeFieldsMap.values().forEach(factModelTree -> populateFactModelTree(factModelTree, factTypeFieldsMap));
            rightPanelPresenter.setDataObjectFieldsMap(factTypeFieldsMap);
            SortedMap<String, FactModelTree> instanceFieldsMap = new TreeMap<>();
            // map instance name top data model class
            if (model != null) {
                final SimulationDescriptor simulationDescriptor = model.getSimulation().getSimulationDescriptor();
                simulationDescriptor.getUnmodifiableFactMappings().forEach(factMapping -> {
                    String dataObjectName = factMapping.getFactIdentifier().getClassName();
                    if (dataObjectName.contains(".")) {
                        dataObjectName = dataObjectName.substring(dataObjectName.lastIndexOf(".") + 1);
                    }
                    final String instanceName = factMapping.getFactAlias();
                    if (!instanceName.equals(dataObjectName)) {
                        final FactModelTree factModelTree = factTypeFieldsMap.get(dataObjectName);
                        if (factModelTree != null) {
                            instanceFieldsMap.put(instanceName, factModelTree);
                        }
                    }
                });
            }
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
     */
    protected void populateFactModelTree(FactModelTree toPopulate, final SortedMap<String, FactModelTree> factTypeFieldsMap) {
        List<String> toRemove = new ArrayList<>();
        toPopulate.getSimpleProperties().forEach((key, value) -> {
            if (factTypeFieldsMap.containsKey(value)) {
                toRemove.add(key);
                toPopulate.addExpandableProperty(key, factTypeFieldsMap.get(value).getFactName());
            }
        });
        toRemove.forEach(toPopulate::removeSimpleProperty);
    }
}
