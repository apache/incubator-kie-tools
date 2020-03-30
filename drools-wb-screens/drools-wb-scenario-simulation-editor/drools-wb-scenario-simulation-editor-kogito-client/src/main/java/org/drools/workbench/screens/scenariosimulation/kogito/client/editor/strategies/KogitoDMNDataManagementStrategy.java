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
import jsinterop.base.Js;
import org.drools.workbench.scenariosimulation.kogito.marshaller.mapper.JsUtils;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.AbstractDMNDataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioNotificationEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.KogitoDMNService;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12UnmarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.workbench.events.NotificationEvent;

public class KogitoDMNDataManagementStrategy extends AbstractDMNDataManagementStrategy {

    private KogitoDMNService dmnTypeService;

    public KogitoDMNDataManagementStrategy(EventBus eventBus, KogitoDMNService dmnTypeService) {
        super(eventBus);
        this.dmnTypeService = dmnTypeService;
    }

    @Override
    protected void retrieveFactModelTuple(TestToolsView.Presenter testToolsPresenter, ScenarioSimulationContext context, GridWidget gridWidget, String dmnFilePath) {
        RemoteCallback<FactModelTuple> callback = getSuccessCallback(testToolsPresenter, context, gridWidget);
        String fileName = dmnFilePath.substring(dmnFilePath.lastIndexOf('/') + 1);
        final Path dmnPath = PathFactory.newPath(fileName, dmnFilePath);
        dmnTypeService.getDMNContent(dmnPath, dmnContent -> {
                                         DMN12UnmarshallCallback dmn12UnmarshallCallback = getDMN12UnmarshallCallback(callback);
                                         MainJs.unmarshall(dmnContent, "", dmn12UnmarshallCallback);
                                     },
                                     (message, throwable) -> {
                                         eventBus.fireEvent(new ScenarioNotificationEvent(ScenarioSimulationEditorConstants.INSTANCE.dmnPathErrorDetailedLabel(dmnFilePath),
                                                                                          NotificationEvent.NotificationType.ERROR));
                                         return false;
                                     });
    }

    private DMN12UnmarshallCallback getDMN12UnmarshallCallback(final RemoteCallback<FactModelTuple> callback) {
        return dmn12 -> {
            final JSITDefinitions jsitDefinitions = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn12));
            final FactModelTuple factModelTuple = dmnTypeService.getFactModelTuple(jsitDefinitions);
            callback.callback(factModelTuple);
        };
    }
}
