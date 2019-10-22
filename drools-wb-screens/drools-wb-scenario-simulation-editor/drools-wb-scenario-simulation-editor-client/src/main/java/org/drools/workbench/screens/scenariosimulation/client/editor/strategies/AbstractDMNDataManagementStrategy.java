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

import java.util.TreeMap;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.events.UnsupportedDMNEvent;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

public abstract class AbstractDMNDataManagementStrategy extends AbstractDataManagementStrategy {

    protected final EventBus eventBus;
    protected Path currentPath;

    protected abstract void retrieveFactModelTuple(final TestToolsView.Presenter testToolsPresenter,
                                                   final ScenarioSimulationContext context,
                                                   String dmnFilePath);

    public AbstractDMNDataManagementStrategy(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void populateTestTools(final TestToolsView.Presenter testToolsPresenter,
                                  final ScenarioSimulationContext context) {
        String dmnFilePath = model.getSimulation().getSimulationDescriptor().getDmnFilePath();
        if (factModelTreeHolder.getFactModelTuple() != null) {
            getSuccessCallback(testToolsPresenter, context).callback(factModelTreeHolder.getFactModelTuple());
        } else {
            retrieveFactModelTuple(testToolsPresenter, context, dmnFilePath);
        }
    }

    @Override
    public void manageScenarioSimulationModelContent(ObservablePath currentPath, ScenarioSimulationModelContent toManage) {
        this.currentPath = currentPath.getOriginal();
        model = toManage.getModel();
    }

    @Override
    public boolean isADataType(String value) {
        return factModelTreeHolder.factModelTuple.getHiddenFacts().keySet().contains(value) || factModelTreeHolder.factModelTuple.getVisibleFacts().keySet().contains(value);
    }

    public RemoteCallback<FactModelTuple> getSuccessCallback(final TestToolsView.Presenter testToolsPresenter,
                                                             final ScenarioSimulationContext context) {
        return factMappingTuple -> getSuccessCallbackMethod(factMappingTuple, testToolsPresenter, context);
    }

    public void getSuccessCallbackMethod(final FactModelTuple factModelTuple,
                                         final TestToolsView.Presenter testToolsPresenter,
                                         final ScenarioSimulationContext context) {
        // Instantiate a map of already assigned properties
        factModelTreeHolder.setFactModelTuple(factModelTuple);
        storeData(factModelTuple, testToolsPresenter, context);
        showErrorsAndCleanupState(factModelTuple);
    }

    protected void showErrorsAndCleanupState(FactModelTuple factModelTuple) {
        StringBuilder builder = new StringBuilder();
        boolean showError = false;
        if (!factModelTuple.getMultipleNestedCollectionError().isEmpty()) {
            showError = true;
            builder.append("Nested collections are not supported! Violated by:<br/>");
            factModelTuple.getMultipleNestedCollectionError().forEach(error -> builder.append("<b>" + error + "</b><br/>"));
            builder.append("<br/>");
        }
        if (!factModelTuple.getMultipleNestedObjectError().isEmpty()) {
            showError = true;
            builder.append("Complex nested objects inside a collection are not supported! Violated by:<br/>");
            factModelTuple.getMultipleNestedObjectError().forEach(error -> builder.append("<b>" + error + "</b><br/>"));
        }
        if (showError) {
            factModelTuple.getMultipleNestedCollectionError().clear();
            factModelTuple.getMultipleNestedObjectError().clear();
            eventBus.fireEvent(new UnsupportedDMNEvent(builder.toString()));
        }
    }

    protected ErrorCallback<Message> getErrorCallback(TestToolsView.Presenter testToolsPresenter) {
        return (error, exception) -> {
            testToolsPresenter.setDataObjectFieldsMap(new TreeMap<>());
            ErrorPopup.showMessage(exception.getMessage());
            return false;
        };
    }
}
