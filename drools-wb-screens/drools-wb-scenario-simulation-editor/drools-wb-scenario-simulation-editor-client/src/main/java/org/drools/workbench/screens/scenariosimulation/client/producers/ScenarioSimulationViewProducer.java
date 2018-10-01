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
import javax.inject.Inject;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationView;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelClickHandler;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;

/**
 * <code>@Dependent</code> <i>Producer</i> for a given {@link org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationView}
 */
@Dependent
public class ScenarioSimulationViewProducer {

    @Inject
    private ScenarioSimulationView scenarioSimulationView;

    @Inject
    private ScenarioGridPanelProducer scenarioGridPanelProducer;

    @Inject
    private ScenarioSimulationGridPanelClickHandlerProducer scenarioSimulationGridPanelClickHandlerProducer;

    public ScenarioSimulationView getScenarioSimulationView(EventBus eventBus) {

        final ScenarioGridPanel scenarioGridPanel = scenarioGridPanelProducer.getScenarioGridPanel();
        scenarioGridPanel.setEventBus(eventBus);

        final ScenarioSimulationGridPanelClickHandler scenarioSimulationGridPanelClickHandler = scenarioSimulationGridPanelClickHandlerProducer.getScenarioSimulationGridPanelClickHandler();

        scenarioSimulationGridPanelClickHandler.setScenarioGrid(scenarioGridPanel.getScenarioGrid());
        scenarioSimulationGridPanelClickHandler.setEventBus(eventBus);

        scenarioGridPanel.addClickHandler(scenarioSimulationGridPanelClickHandler);
        scenarioSimulationView.setScenarioGridPanel(scenarioGridPanel);
        return scenarioSimulationView;
    }

}
