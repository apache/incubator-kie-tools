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
import javax.inject.Inject;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationView;
import org.drools.workbench.screens.scenariosimulation.client.factories.CollectionEditorSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioExpressionCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioInvokeContextMenuForSelectedCell;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelClickHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelMouseMoveHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationKeyboardEditHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationMainGridPanelClickHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationMainGridPanelMouseMoveHandler;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.drools.workbench.screens.scenariosimulation.client.models.AbstractScesimGridModel;
import org.drools.workbench.screens.scenariosimulation.client.models.BackgroundGridModel;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.popover.ErrorReportPopoverPresenter;
import org.drools.workbench.screens.scenariosimulation.client.renderers.ScenarioGridRenderer;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetKeyboardHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveDown;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveLeft;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveRight;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveUp;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationSelectBottomRightCell;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationSelectTopLeftCell;

/**
 * <code>@Dependent</code> <i>Producer</i> for a given {@link ScenarioGridPanel}
 */
@Dependent
public class ScenarioGridPanelProducer {

    @Inject
    protected ScenarioContextMenuRegistry scenarioContextMenuRegistry;

    @Inject
    protected ScenarioGridLayer simulationGridLayer;

    @Inject
    protected ScenarioGridPanel simulationGridPanel;

    @Inject
    protected ScenarioGridLayer backgroundGridLayer;

    @Inject
    protected ScenarioGridPanel backgroundGridPanel;

    @Inject
    protected ViewsProvider viewsProvider;

    @Inject
    protected ScenarioSimulationView scenarioSimulationView;

    @Inject
    protected ScenarioGridWidget simulationGridWidget;

    @Inject
    protected ScenarioGridWidget backgroundGridWidget;

    @Inject
    protected ScenarioSimulationMainGridPanelClickHandler simulationGridPanelClickHandler;

    @Inject
    protected ScenarioSimulationMainGridPanelMouseMoveHandler simulationGridPanelMouseMoveHandler;

    @Inject
    protected ScenarioSimulationMainGridPanelClickHandler backgroundGridPanelClickHandler;

    @Inject
    protected ScenarioSimulationMainGridPanelMouseMoveHandler backgroundGridPanelMouseMoveHandler;

    @Inject
    protected ErrorReportPopoverPresenter errorReportPopupPresenter;

    protected ScenarioSimulationContext scenarioSimulationContext;


    @PostConstruct
    public void init() {
        scenarioSimulationContext = new ScenarioSimulationContext(simulationGridWidget, backgroundGridWidget);
        initializeGrid(simulationGridLayer, simulationGridPanel, new ScenarioGridModel(false), scenarioSimulationContext);
        initializeGrid(backgroundGridLayer, backgroundGridPanel, new BackgroundGridModel(false), scenarioSimulationContext);
    }

    public ScenarioSimulationContext getScenarioSimulationContext() {
        return scenarioSimulationContext;
    }

    public ScenarioGridPanel getSimulationGridPanel() {
        return simulationGridPanel;
    }

    public ScenarioGridPanel getBackgroundGridPanel() {
        return backgroundGridPanel;
    }

    public ScenarioContextMenuRegistry getScenarioContextMenuRegistry() {
        return scenarioContextMenuRegistry;
    }

    public ScenarioSimulationView getScenarioSimulationView(final EventBus eventBus) {
        scenarioSimulationView.setScenarioGridWidget(getScenarioMainGridWidget(eventBus));
        return scenarioSimulationView;
    }

    public ScenarioGridWidget getBackgroundGridWidget(final EventBus eventBus) {
        initGridWidget(backgroundGridWidget, getBackgroundGridPanel(),
                       backgroundGridPanelClickHandler,
                       backgroundGridPanelMouseMoveHandler,
                       eventBus);
        return backgroundGridWidget;
    }

    protected void initializeGrid(ScenarioGridLayer scenarioGridLayer, ScenarioGridPanel scenarioGridPanel, AbstractScesimGridModel abstractScesimGridModel, ScenarioSimulationContext scenarioSimulationContext) {
        final ScenarioGrid scenarioGrid = new ScenarioGrid(abstractScesimGridModel,
                                                           scenarioGridLayer,
                                                           new ScenarioGridRenderer(false),
                                                           scenarioContextMenuRegistry);
        scenarioGridLayer.addScenarioGrid(scenarioGrid);
        scenarioGridPanel.add(scenarioGridLayer);
        scenarioGrid.setScenarioSimulationContext(scenarioSimulationContext);
        abstractScesimGridModel.setCollectionEditorSingletonDOMElementFactory(
                new CollectionEditorSingletonDOMElementFactory(scenarioGridPanel,
                                                               scenarioGridLayer,
                                                               scenarioGridLayer.getScenarioGrid(),
                                                               scenarioSimulationContext,
                                                               viewsProvider));
        abstractScesimGridModel.setScenarioCellTextAreaSingletonDOMElementFactory(
                new ScenarioCellTextAreaSingletonDOMElementFactory(scenarioGridPanel,
                                                                   scenarioGridLayer,
                                                                   scenarioGridLayer.getScenarioGrid()));
        abstractScesimGridModel.setScenarioHeaderTextBoxSingletonDOMElementFactory(
                new ScenarioHeaderTextBoxSingletonDOMElementFactory(scenarioGridPanel,
                                                                    scenarioGridLayer,
                                                                    scenarioGridLayer.getScenarioGrid()));
        abstractScesimGridModel.setScenarioExpressionCellTextAreaSingletonDOMElementFactory(
                new ScenarioExpressionCellTextAreaSingletonDOMElementFactory(scenarioGridPanel,
                                                                             scenarioGridLayer,
                                                                             scenarioGridLayer.getScenarioGrid()));

        final ScenarioSimulationKeyboardEditHandler scenarioSimulationKeyboardEditHandler =
                new ScenarioSimulationKeyboardEditHandler(scenarioGridLayer);
        final ScenarioInvokeContextMenuForSelectedCell invokeContextMenuKeyboardOperation =
                new ScenarioInvokeContextMenuForSelectedCell(scenarioGridLayer, scenarioContextMenuRegistry);

        final BaseGridWidgetKeyboardHandler handler = new BaseGridWidgetKeyboardHandler(scenarioGridLayer);
        handler.addOperation(scenarioSimulationKeyboardEditHandler,
                             new KeyboardOperationSelectTopLeftCell(scenarioGridLayer),
                             new KeyboardOperationMoveLeft(scenarioGridLayer),
                             new KeyboardOperationMoveRight(scenarioGridLayer),
                             new KeyboardOperationMoveUp(scenarioGridLayer),
                             new KeyboardOperationMoveDown(scenarioGridLayer),
                             new KeyboardOperationSelectBottomRightCell(scenarioGridLayer),
                             invokeContextMenuKeyboardOperation);
        scenarioGridPanel.addKeyDownHandler(handler);

        // Hack to enable PINNED MODE i.e. not draggable
        scenarioGridLayer.enterPinnedMode(scenarioGrid, () -> {});
    }

    protected ScenarioGridWidget getScenarioMainGridWidget(final EventBus eventBus) {
        initGridWidget(simulationGridWidget, getSimulationGridPanel(),
                       simulationGridPanelClickHandler,
                       simulationGridPanelMouseMoveHandler,
                       eventBus);
        return simulationGridWidget;
    }

    protected void initGridWidget(final ScenarioGridWidget scenarioGridWidget,
                                  final ScenarioGridPanel scenarioGridPanel,
                                  final ScenarioSimulationGridPanelClickHandler clickHandler,
                                  final ScenarioSimulationGridPanelMouseMoveHandler mouseMoveHandler,
                                  final EventBus eventBus) {
        scenarioGridPanel.setEventBus(eventBus);
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

}