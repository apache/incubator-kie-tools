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

import org.drools.workbench.screens.scenariosimulation.client.editor.menu.ExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderGivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.OtherContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.UnmodifiableColumnGridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelClickHandler;

/**
 * <code>@Dependent</code> <i>Producer</i> for a given {@link ScenarioSimulationGridPanelClickHandler}
 */
@Dependent
public class ScenarioSimulationGridPanelClickHandlerProducer {

    @Inject
    private OtherContextMenu otherContextMenu;
    @Inject
    private HeaderGivenContextMenu headerGivenContextMenu;
    @Inject
    private HeaderExpectedContextMenu headerExpectedContextMenu;
    @Inject
    private GivenContextMenu givenContextMenu;
    @Inject
    private ExpectedContextMenu expectedContextMenu;
    @Inject
    private GridContextMenu gridContextMenu;
    @Inject
    private UnmodifiableColumnGridContextMenu unmodifiableColumnGridContextMenu;

    @Inject
    private ScenarioSimulationGridPanelClickHandler scenarioSimulationGridPanelClickHandler;

    public ScenarioSimulationGridPanelClickHandler getScenarioSimulationGridPanelClickHandler() {
        scenarioSimulationGridPanelClickHandler.setExpectedContextMenu(expectedContextMenu);
        scenarioSimulationGridPanelClickHandler.setGivenContextMenu(givenContextMenu);
        scenarioSimulationGridPanelClickHandler.setGridContextMenu(gridContextMenu);
        scenarioSimulationGridPanelClickHandler.setUnmodifiableColumnGridContextMenu(unmodifiableColumnGridContextMenu);
        scenarioSimulationGridPanelClickHandler.setHeaderExpectedContextMenu(headerExpectedContextMenu);
        scenarioSimulationGridPanelClickHandler.setHeaderGivenContextMenu(headerGivenContextMenu);
        scenarioSimulationGridPanelClickHandler.setOtherContextMenu(otherContextMenu);
        return scenarioSimulationGridPanelClickHandler;
    }
}
