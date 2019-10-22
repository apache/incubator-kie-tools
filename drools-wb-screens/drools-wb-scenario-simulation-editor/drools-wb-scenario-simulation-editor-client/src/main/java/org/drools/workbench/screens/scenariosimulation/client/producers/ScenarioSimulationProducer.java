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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioCommandManager;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioCommandRegistry;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationView;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.drools.workbench.screens.scenariosimulation.client.popup.ConfirmPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.DeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.FileUploadPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.PreserveDeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * <code>@Dependent</code> Class meant to be the only <i>Producer</i> for a given {@link org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter}
 */
@Dependent
public class ScenarioSimulationProducer {

    @Inject
    protected ScenarioSimulationViewProducer scenarioSimulationViewProducer;

    @Inject
    protected EventBusProducer eventBusProducer;

    @Inject
    protected DeletePopupPresenter deletePopupPresenter;

    @Inject
    protected PreserveDeletePopupPresenter preserveDeletePopupPresenter;

    @Inject
    protected ConfirmPopupPresenter confirmPopupPresenter;

    @Inject
    protected FileUploadPopupPresenter fileUploadPopupPresenter;

    @Inject
    protected ScenarioSimulationEventHandler scenarioSimulationEventHandler;

    @Inject
    protected ScenarioCommandRegistry scenarioCommandRegistry;

    @Inject
    protected ScenarioCommandManager scenarioCommandManager;

    @Inject
    protected Event<NotificationEvent> notificationEvent;

    @PostConstruct
    public void init() {
        final ScenarioContextMenuRegistry scenarioContextMenuRegistry =
                scenarioSimulationViewProducer.getScenarioContextMenuRegistry();
        scenarioContextMenuRegistry.setEventBus(getEventBus());

        scenarioSimulationEventHandler.setEventBus(getEventBus());
        scenarioSimulationEventHandler.setDeletePopupPresenter(deletePopupPresenter);
        scenarioSimulationEventHandler.setPreserveDeletePopupPresenter(preserveDeletePopupPresenter);
        scenarioSimulationEventHandler.setConfirmPopupPresenter(confirmPopupPresenter);
        scenarioSimulationEventHandler.setFileUploadPopupPresenter(fileUploadPopupPresenter);
        scenarioSimulationEventHandler.setNotificationEvent(notificationEvent);
        scenarioSimulationEventHandler.setScenarioCommandManager(scenarioCommandManager);
        scenarioSimulationEventHandler.setScenarioCommandRegistry(scenarioCommandRegistry);
    }

    public EventBus getEventBus() {
        return eventBusProducer.getEventBus();
    }

    public ScenarioSimulationView getScenarioSimulationView() {
        return scenarioSimulationViewProducer.getScenarioSimulationView(getEventBus());
    }

    public ScenarioGridWidget getScenarioBackgroundGridWidget() {
        return scenarioSimulationViewProducer.getScenarioBackgroundGridWidget(getEventBus());
    }

    public void setScenarioSimulationEditorPresenter(ScenarioSimulationEditorPresenter presenter) {
        scenarioSimulationEventHandler.setScenarioSimulationPresenter(presenter);
    }
}
