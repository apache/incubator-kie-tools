/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.AbstractDMNDataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioNotificationEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.ScenarioSimulationKogitoDMNDataManager;
import org.drools.workbench.screens.scenariosimulation.kogito.client.services.ScenarioSimulationKogitoDMNMarshallerService;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;

import org.jboss.errai.common.client.api.ErrorCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.workbench.events.NotificationEvent;

public class KogitoDMNDataManagementStrategy extends AbstractDMNDataManagementStrategy {

    private ScenarioSimulationKogitoDMNDataManager dmnDataManager;
    private ScenarioSimulationKogitoDMNMarshallerService dmnMarshallerService;

    public KogitoDMNDataManagementStrategy(EventBus eventBus,
                                           ScenarioSimulationKogitoDMNDataManager dmnDataManager,
                                           ScenarioSimulationKogitoDMNMarshallerService dmnMarshallerService) {
        super(eventBus);
        this.dmnDataManager = dmnDataManager;
        this.dmnMarshallerService = dmnMarshallerService;
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

   private Callback<JSITDefinitions> getDMNContentCallback(final TestToolsView.Presenter testToolsPresenter,
                                                           final ScenarioSimulationContext context,
                                                           final GridWidget gridWidget) {
       return jsitDefinitions -> {
           final FactModelTuple factModelTuple = dmnDataManager.getFactModelTuple(jsitDefinitions);
           getSuccessCallback(testToolsPresenter, context, gridWidget).callback(factModelTuple);
       };
   }

    private ErrorCallback<Object> getDMNContentErrorCallback(String dmnFilePath) {
        return (message, throwable) -> {
            eventBus.fireEvent(new ScenarioNotificationEvent(ScenarioSimulationEditorConstants.INSTANCE.dmnPathErrorDetailedLabel(dmnFilePath,
                                                                                                                                  message.toString()),
                                                             NotificationEvent.NotificationType.ERROR,
                                                             false));
            return false;
        };
    }

}
