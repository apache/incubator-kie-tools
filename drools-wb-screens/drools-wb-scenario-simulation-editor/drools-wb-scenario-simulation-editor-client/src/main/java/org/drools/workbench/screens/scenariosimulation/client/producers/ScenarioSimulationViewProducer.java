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
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelMouseMoveHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationMainGridPanelClickHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationMainGridPanelMouseMoveHandler;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.drools.workbench.screens.scenariosimulation.client.popover.ErrorReportPopoverPresenter;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;

/**
 * <code>@Dependent</code> <i>Producer</i> for a given {@link org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationView}
 */
@Dependent
public class ScenarioSimulationViewProducer {

    @Inject
    protected ScenarioSimulationView scenarioSimulationView;

    @Inject
    protected ScenarioGridWidget scenarioMainGridWidget;

    @Inject
    protected ScenarioGridWidget scenarioBackgroundGridWidget;

    @Inject
    protected ScenarioGridPanelProducer scenarioGridPanelProducer;

    @Inject
    protected ScenarioSimulationMainGridPanelClickHandler scenarioMainGridPanelClickHandler;

    @Inject
    protected ScenarioSimulationMainGridPanelMouseMoveHandler scenarioMainGridPanelMouseMoveHandler;

    @Inject
    protected ScenarioSimulationMainGridPanelClickHandler scenarioBackgroundGridPanelClickHandler;

    @Inject
    protected ScenarioSimulationMainGridPanelMouseMoveHandler scenarioBackgroundGridPanelMouseMoveHandler;

    @Inject
    protected ErrorReportPopoverPresenter errorReportPopupPresenter;

    public ScenarioSimulationView getScenarioSimulationView(final EventBus eventBus) {
        scenarioSimulationView.setScenarioGridWidget(getScenarioMainGridWidget(eventBus));
        return scenarioSimulationView;
    }

    protected ScenarioGridWidget getScenarioMainGridWidget(final EventBus eventBus) {
        initGridWidget(scenarioMainGridWidget,
                       scenarioGridPanelProducer.getScenarioMainGridPanel(),
                       scenarioMainGridPanelClickHandler,
                       scenarioMainGridPanelMouseMoveHandler,
                       eventBus);
        return scenarioMainGridWidget;
    }

    public ScenarioGridWidget getScenarioBackgroundGridWidget(final EventBus eventBus) {
        initGridWidget(scenarioBackgroundGridWidget,
                       scenarioGridPanelProducer.getScenarioBackgroundGridPanel(),
                       scenarioBackgroundGridPanelClickHandler,
                       scenarioBackgroundGridPanelMouseMoveHandler,
                       eventBus);
        return scenarioBackgroundGridWidget;
    }

    protected void initGridWidget(final ScenarioGridWidget scenarioGridWidget,
                                final ScenarioGridPanel scenarioGridPanel,
                                final ScenarioSimulationGridPanelClickHandler clickHandler,
                                final ScenarioSimulationGridPanelMouseMoveHandler mouseMoveHandler,
                                final EventBus eventBus) {
        scenarioGridPanel.setEventBus(eventBus);
        final ScenarioContextMenuRegistry scenarioContextMenuRegistry =
                scenarioGridPanelProducer.getScenarioContextMenuRegistry();
        scenarioContextMenuRegistry.setEventBus(eventBus);
        clickHandler.setScenarioContextMenuRegistry(scenarioContextMenuRegistry);
        clickHandler.setScenarioGridPanel(scenarioGridPanel);
        clickHandler.setEventBus(eventBus);
        scenarioContextMenuRegistry.setErrorReportPopoverPresenter(errorReportPopupPresenter);
        mouseMoveHandler.setScenarioGridPanel(scenarioGridPanel);
        mouseMoveHandler.setErrorReportPopupPresenter(errorReportPopupPresenter);
        scenarioGridPanel.addHandlers(clickHandler, mouseMoveHandler);
        scenarioGridWidget.setScenarioGridPanel(scenarioGridPanel);
    }

    public ScenarioContextMenuRegistry getScenarioContextMenuRegistry() {
        return scenarioGridPanelProducer.getScenarioContextMenuRegistry();
    }
}
