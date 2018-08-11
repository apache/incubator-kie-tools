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

package org.drools.workbench.screens.scenariosimulation.client.editor;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootPanel;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioSimulationViewProvider;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelClickHandler;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

/**
 * Implementation of the main view for the ScenarioSimulation editor.
 * <p>
 * This view contains a <code>ScenarioGridPanel</code>.
 */
public class ScenarioSimulationViewImpl
        extends KieEditorViewImpl
        implements ScenarioSimulationView {

    ScenarioGridPanel scenarioGridPanel;
    private ScenarioSimulationEditorPresenter presenter;

    @Inject
    ScenarioGridLayer scenarioGridLayer;

    @Inject
    private GridContextMenu gridContextMenu;

    @Inject
    private HeaderContextMenu headerContextMenu;

    protected HandlerRegistration clickHandlerRegistration;

    @Override
    public void init(final ScenarioSimulationEditorPresenter presenter) {
        this.presenter = presenter;
        this.scenarioGridPanel = ScenarioSimulationViewProvider.newScenarioGridPanel(scenarioGridLayer);
        scenarioGridLayer.enterPinnedMode(scenarioGridLayer.getScenarioGrid(), () -> {
        });  // Hack to overcome default implementation

        ScenarioSimulationGridPanelClickHandler scenarioSimulationGridPanelClickHandler = ScenarioSimulationViewProvider.newScenarioSimulationGridPanelClickHandler(scenarioGridPanel.getScenarioGrid());
        clickHandlerRegistration = RootPanel.get().addDomHandler(scenarioSimulationGridPanelClickHandler, ClickEvent.getType());
        scenarioSimulationGridPanelClickHandler.setGridContextMenu(gridContextMenu);
        scenarioSimulationGridPanelClickHandler.setHeaderContextMenu(headerContextMenu);
        scenarioGridPanel.setClickHandler(scenarioSimulationGridPanelClickHandler);
        initWidget(scenarioGridPanel);
    }

    @Override
    public void setContent(Simulation simulation) {
        scenarioGridPanel.getScenarioGrid().setContent(simulation);
    }

    @Override
    public void clear() {
        if (clickHandlerRegistration != null) {
            clickHandlerRegistration.removeHandler();
        }
    }

    @Override
    public void addGridMenuItem(String id, String label, String i18n, Command command) {
        gridContextMenu.addMenuItem(id, label, i18n, command);
    }

    @Override
    public void addHeaderMenuItem(String id, String label, String i18n, Command command) {
        headerContextMenu.addMenuItem(id, label, i18n, command);
    }

}