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
package org.drools.workbench.screens.scenariosimulation.client.producers;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.commands.CommandExecutor;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationView;
import org.drools.workbench.screens.scenariosimulation.client.popup.DeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.PreserveDeletePopupPresenter;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * <code>@Dependent</code> Class meant to be the only <i>Producer</i> for a given {@link org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter}
 */
@Dependent
public class ScenarioSimulationProducer {


    @Inject
    ScenarioSimulationViewProducer scenarioSimulationViewProducer;

    @Inject
    EventBusProducer eventBusProducer;

    @Inject
    DeletePopupPresenter deletePopupPresenter;
    @Inject
    PreserveDeletePopupPresenter preserveDeletePopupPresenter;

    @Inject
    CommandExecutor commandExecutor;

    @Inject
    Event<NotificationEvent> notificationEvent;

    public EventBus getEventBus() {
        return eventBusProducer.getEventBus();
    }

    public ScenarioSimulationView getScenarioSimulationView() {
        return scenarioSimulationViewProducer.getScenarioSimulationView(getEventBus());
    }

    public CommandExecutor getCommandExecutor() {
        commandExecutor.setEventBus(getEventBus());
        commandExecutor.setScenarioGridPanel(getScenarioSimulationView().getScenarioGridPanel());
        commandExecutor.setDeletePopupPresenter(deletePopupPresenter);
        commandExecutor.setPreserveDeletePopupPresenter(preserveDeletePopupPresenter);
        commandExecutor.setNotificationEvent(notificationEvent);
        return commandExecutor;
    }
}
