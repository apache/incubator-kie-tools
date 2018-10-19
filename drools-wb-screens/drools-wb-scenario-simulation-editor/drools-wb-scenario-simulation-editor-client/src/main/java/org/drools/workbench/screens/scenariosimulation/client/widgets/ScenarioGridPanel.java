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
package org.drools.workbench.screens.scenariosimulation.client.widgets;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.Dependent;

import com.ait.lienzo.client.core.event.NodeMouseOutEvent;
import com.ait.lienzo.client.core.event.NodeMouseOutHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import org.drools.workbench.screens.scenariosimulation.client.events.DisableRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelClickHandler;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

/**
 * ScenarioGridPanel implementation of <code>GridLienzoPanel</code>.
 * <p>
 * This panel contains a <code>ScenarioGridLayer</code> and it is instantiated only once.
 * The Clicks are managed by the injected <code>ScenarioSimulationGridPanelClickHandler</code>
 */
@Dependent
public class ScenarioGridPanel extends GridLienzoPanel implements NodeMouseOutHandler {

    private EventBus eventBus;
    private ScenarioSimulationGridPanelClickHandler clickHandler;

    Set<HandlerRegistration> handlerRegistrations = new HashSet<>();

    public ScenarioGridPanel() {
    }

    public void addClickHandler(final ScenarioSimulationGridPanelClickHandler clickHandler) {
        this.clickHandler = clickHandler;
        unregister();
        handlerRegistrations.add(getDomElementContainer().addDomHandler(clickHandler,
                                                                        ContextMenuEvent.getType()));
        handlerRegistrations.add(getDomElementContainer().addDomHandler(clickHandler,
                                                                        ClickEvent.getType()));
        handlerRegistrations.add(getScenarioGridLayer().addNodeMouseOutHandler(this));
    }

    public ScenarioGridLayer getScenarioGridLayer() {
        return (ScenarioGridLayer) getDefaultGridLayer();
    }

    public ScenarioGrid getScenarioGrid() {
        return ((ScenarioGridLayer) getDefaultGridLayer()).getScenarioGrid();
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        getScenarioGrid().setEventBus(eventBus);
    }

    public void select() {
        getDefaultGridLayer().select(getScenarioGrid()); // This is to have "floatable" header, ie. not moving
    }

    @Override
    public void onNodeMouseOut(NodeMouseOutEvent event) {
        final int height = getScenarioGridLayer().getHeight();
        final int width = getScenarioGridLayer().getWidth();
        final int x = event.getX();
        final int y = event.getY();
        if (x < 0 || x > width || y < 0 || y > height) {
            clickHandler.hideMenus();
        }
    }

    public void unregister() {
        handlerRegistrations.forEach(HandlerRegistration::removeHandler);
        handlerRegistrations.clear();
    }

    public void signalRightPanelAboutSelectedHeaderCells() {
        signalRightPanelNoHeaderCellSelected();

        final ScenarioGridModel model = getScenarioGrid().getModel();

        if (model.getSelectedHeaderCells().size() > 0) {
            final GridData.SelectedCell cell = model.getSelectedHeaderCells().get(0);

            final int uiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(model.getColumns(),
                                                                             cell.getColumnIndex());
            final int uiRowIndex = cell.getRowIndex();

            getScenarioGrid().setSelectedColumnAndHeader(uiRowIndex, uiColumnIndex);

            final GridColumn column = model.getColumns().get(uiColumnIndex);
            if (uiRowIndex > 0 && column instanceof ScenarioGridColumn) {
                signalRightPanelHeaderCellSelected((ScenarioGridColumn) column,
                                                   cell,
                                                   uiColumnIndex);
            }
        }
    }

    private void signalRightPanelNoHeaderCellSelected() {
        eventBus.fireEvent(new DisableRightPanelEvent());
    }

    private void signalRightPanelHeaderCellSelected(final ScenarioGridColumn scenarioGridColumn,
                                                    final GridData.SelectedCell selectedHeaderCell,
                                                    final int uiColumnIndex) {
        final EnableRightPanelEvent enableRightPanelEvent = getEnableRightPanelEvent(scenarioGridColumn,
                                                                                     selectedHeaderCell,
                                                                                     uiColumnIndex);
        eventBus.fireEvent(enableRightPanelEvent);
    }

    private EnableRightPanelEvent getEnableRightPanelEvent(final ScenarioGridColumn scenarioGridColumn,
                                                           final GridData.SelectedCell selectedHeaderCell,
                                                           final int uiColumnIndex) {
        final int uiRowIndex = selectedHeaderCell.getRowIndex();
        ScenarioHeaderMetaData scenarioHeaderMetaData =
                ScenarioSimulationGridHeaderUtilities.getColumnScenarioHeaderMetaData(scenarioGridColumn, uiRowIndex);

        return ScenarioSimulationGridHeaderUtilities.getEnableRightPanelEvent(getScenarioGrid(),
                                                                              scenarioGridColumn,
                                                                              scenarioHeaderMetaData,
                                                                              uiColumnIndex,
                                                                              scenarioHeaderMetaData.getColumnGroup());
    }
}