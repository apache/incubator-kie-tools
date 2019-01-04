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

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelView;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;

public class DMNDataManagementStrategy implements DataManagementStrategy {

    private final Caller<DMNTypeService> dmnTypeService;
    private Path currentPath;
    private ScenarioSimulationModel model;
    private ResultHolder factModelTreeHolder = new ResultHolder();

    public DMNDataManagementStrategy(Caller<DMNTypeService> dmnTypeService) {
        this.dmnTypeService = dmnTypeService;
    }

    @Override
    public void populateRightPanel(final RightPanelView.Presenter rightPanelPresenter, final ScenarioGridModel scenarioGridModel) {
        String dmnFilePath = model.getSimulation().getSimulationDescriptor().getDmnFilePath();
        if(factModelTreeHolder.getFactModelTuple() != null) {
            getSuccessCallback(rightPanelPresenter).callback(factModelTreeHolder.getFactModelTuple());
        }
        else {
            dmnTypeService.call(getSuccessCallback(rightPanelPresenter),
                                getErrorCallback(rightPanelPresenter))
                    .retrieveType(currentPath, dmnFilePath);
        }
    }

    @Override
    public void manageScenarioSimulationModelContent(ObservablePath currentPath, ScenarioSimulationModelContent toManage) {
        this.currentPath = currentPath.getOriginal();
        model = toManage.getModel();
    }

    private RemoteCallback<FactModelTuple> getSuccessCallback(RightPanelView.Presenter rightPanelPresenter) {
        return factMappingTuple -> {
            factModelTreeHolder.setFactModelTuple(factMappingTuple);
            final SortedMap<String, FactModelTree> visibleFacts = factMappingTuple.getVisibleFacts();
            final Map<Boolean, List<Map.Entry<String, FactModelTree>>> partitionBy = visibleFacts.entrySet().stream()
                    .collect(Collectors.partitioningBy(stringFactModelTreeEntry -> stringFactModelTreeEntry.getValue().isSimple()));
            final SortedMap<String, FactModelTree> complexDataObjects = new TreeMap<>(partitionBy.get(false).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            final SortedMap<String, FactModelTree> simpleDataObjects = new TreeMap<>(partitionBy.get(true).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            rightPanelPresenter.setDataObjectFieldsMap(complexDataObjects);
            rightPanelPresenter.setSimpleJavaTypeFieldsMap(simpleDataObjects);
            rightPanelPresenter.setHiddenFieldsMap(factMappingTuple.getHiddenFacts());
        };
    }

    private ErrorCallback<Object> getErrorCallback(RightPanelView.Presenter rightPanelPresenter) {
        return (error, exception) -> {
            rightPanelPresenter.setDataObjectFieldsMap(new TreeMap<>());
            return false;
        };
    }

    static private class ResultHolder {
        FactModelTuple factModelTuple;

        public FactModelTuple getFactModelTuple() {
            return factModelTuple;
        }

        public void setFactModelTuple(FactModelTuple factModelTuple) {
            this.factModelTuple = factModelTuple;
        }
    }
}
