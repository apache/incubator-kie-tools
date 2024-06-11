/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.AbstractDMNDataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.ScenarioSimulationKogitoDMNDataManager;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.model.KogitoDMNModel;
import org.drools.workbench.screens.scenariosimulation.kogito.client.services.ScenarioSimulationKogitoDMNMarshallerService;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.workbench.events.NotificationEvent;

public class KogitoDMNDataManagementStrategy extends AbstractDMNDataManagementStrategy {

    private final ScenarioSimulationKogitoDMNDataManager dmnDataManager;
    private final ScenarioSimulationKogitoDMNMarshallerService dmnMarshallerService;
    private final ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter;


    public KogitoDMNDataManagementStrategy(ScenarioSimulationKogitoDMNDataManager dmnDataManager,
                                           ScenarioSimulationKogitoDMNMarshallerService scenarioSimulationKogitoDMNMarshallerService,
                                           ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter) {
        super(scenarioSimulationEditorPresenter.getEventBus());
        this.dmnDataManager = dmnDataManager;
        this.dmnMarshallerService = scenarioSimulationKogitoDMNMarshallerService;
        this.scenarioSimulationEditorPresenter = scenarioSimulationEditorPresenter;
    }

    @Override
    protected void retrieveFactModelTuple(final TestToolsView.Presenter testToolsPresenter,
                                          final ScenarioSimulationContext context,
                                          final GridWidget gridWidget) {
        String dmnFileName = dmnFilePath.substring(dmnFilePath.lastIndexOf('/') + 1);
        final Path dmnPath = PathFactory.newPath(dmnFileName, dmnFilePath);
        dmnMarshallerService.getDMNContent(dmnPath,
                                           getDMNContentCallback(testToolsPresenter, context, gridWidget),
                                           getDMNContentErrorCallback(dmnFilePath));
   }

   private Callback<KogitoDMNModel> getDMNContentCallback(final TestToolsView.Presenter testToolsPresenter,
                                                          final ScenarioSimulationContext context,
                                                          final GridWidget gridWidget) {
       return kogitoDMNModel -> {
           final FactModelTuple factModelTuple = dmnDataManager.getFactModelTuple(kogitoDMNModel);
           getSuccessCallback(testToolsPresenter, context, gridWidget).callback(factModelTuple);
       };
   }

    private ErrorCallback<Object> getDMNContentErrorCallback(String dmnFilePath) {
        return (message, throwable) -> {
            scenarioSimulationEditorPresenter.sendNotification(
                    ScenarioSimulationEditorConstants.INSTANCE.dmnPathErrorDetailedLabel(dmnFilePath, message.toString()),
                    NotificationEvent.NotificationType.ERROR,
                    false);
            scenarioSimulationEditorPresenter.expandSettingsDock();
            return false;
        };
    }

}
