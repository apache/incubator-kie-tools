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
package org.drools.workbench.screens.scenariosimulation.client.handlers;

import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.Dependent;

import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.events.DisableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities.getEnableTestToolsEvent;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.convertDOMToGridCoordinate;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getRelativeXOfEvent;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getRelativeYOfEvent;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getUiColumnIndex;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getUiRowIndex;

@Dependent
public class ScenarioSimulationGridPanelClickHandler implements ClickHandler,
                                                                ContextMenuHandler {

    protected ScenarioGrid scenarioGrid;
    protected ScenarioContextMenuRegistry scenarioContextMenuRegistry;
    protected EventBus eventBus;
    protected AtomicInteger clickReceived = new AtomicInteger(0);
    protected BaseGridRendererHelper rendererHelper;

    public ScenarioSimulationGridPanelClickHandler() {
    }

    public void setScenarioGrid(ScenarioGrid scenarioGrid) {
        this.scenarioGrid = scenarioGrid;
        this.rendererHelper = scenarioGrid.getRendererHelper();
    }

    public void setScenarioContextMenuRegistry(ScenarioContextMenuRegistry scenarioContextMenuRegistry) {
        this.scenarioContextMenuRegistry = scenarioContextMenuRegistry;
    }

    /**
     * This method must be called <b>after</b> all the <i>ContextMenu</i> setters
     * @param eventBus
     */
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        this.scenarioContextMenuRegistry.setEventBus(eventBus);
    }

    @Override
    public void onClick(ClickEvent event) {
        clickReceived.getAndIncrement();
        final int canvasX = getRelativeXOfEvent(event);
        final int canvasY = getRelativeYOfEvent(event);
        scenarioContextMenuRegistry.hideMenus();
        scenarioGrid.clearSelections();
        if (!manageLeftClick(canvasX, canvasY)) { // It was not a grid click
            eventBus.fireEvent(new DisableTestToolsEvent());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onContextMenu(final ContextMenuEvent event) {
        scenarioContextMenuRegistry.hideMenus();
        if (scenarioContextMenuRegistry.manageRightClick(scenarioGrid, event)) {
            event.preventDefault();
            event.stopPropagation();
        }
    }

    public void hideMenus() {
        scenarioContextMenuRegistry.hideMenus();
    }

    /**
     * @param canvasX
     * @param canvasY
     * @return
     */
    protected boolean manageLeftClick(final int canvasX, final int canvasY) {
        final Point2D gridClickPoint = convertDOMToGridCoordinateLocal(canvasX, canvasY);
        Integer uiRowIndex = getUiHeaderRowIndexLocal(gridClickPoint);
        boolean isHeader = true;
        if (uiRowIndex == null) {
            uiRowIndex = getUiRowIndexLocal(gridClickPoint.getY());
            isHeader = false;
        }
        if (uiRowIndex == null) {
            return false;
        }

        final Integer uiColumnIndex = getUiColumnIndexLocal(gridClickPoint.getX());
        if (uiColumnIndex == null) {
            return false;
        }
        ScenarioGridColumn scenarioGridColumn = (ScenarioGridColumn) scenarioGrid.getModel().getColumns().get(uiColumnIndex);
        if (scenarioGridColumn == null) {
            return false;
        }
        if (isHeader) {
            return manageHeaderLeftClick(uiColumnIndex, scenarioGridColumn, gridClickPoint);
        } else {
            return manageGridLeftClick(uiRowIndex, uiColumnIndex);
        }
    }

    /**
     * This method check if the click happened on an <b>second level header</b> (i.e. the header of a specific column) cell. If it is so, manage it and returns <code>true</code>,
     * otherwise returns <code>false</code>
     * @param uiColumnIndex
     * @param scenarioGridColumn
     * @param clickPoint - coordinates relative to the grid top left corner
     * @return
     */
    protected boolean manageHeaderLeftClick(Integer uiColumnIndex, ScenarioGridColumn scenarioGridColumn, Point2D
            clickPoint) {
        //Get row index
        final Integer uiHeaderRowIndex = getUiHeaderRowIndexLocal(clickPoint);
        if (uiHeaderRowIndex == null) {
            return false;
        }
        ScenarioHeaderMetaData clickedScenarioHeaderMetadata = getColumnScenarioHeaderMetaDataLocal(clickPoint);
        if (clickedScenarioHeaderMetadata == null) {
            return false;
        }
        String group = clickedScenarioHeaderMetadata.getColumnGroup();
        if (group.contains("-")) {
            group = group.substring(0, group.indexOf("-"));
        }
        switch (group) {
            case "GIVEN":
            case "EXPECT":
                return manageGivenExpectHeaderLeftClick(clickedScenarioHeaderMetadata,
                                                        scenarioGridColumn,
                                                        group,
                                                        uiColumnIndex);
            default:
                return false;
        }
    }

    /**
     * This method manage the click happened on an <i>GIVEN</i> or <i>EXPECT</i> header, starting editing it if not already did.
     * @param clickedScenarioHeaderMetadata
     * @param scenarioGridColumn
     * @param group
     * @param uiColumnIndex
     * @return
     */
    protected boolean manageGivenExpectHeaderLeftClick(ScenarioHeaderMetaData clickedScenarioHeaderMetadata,
                                                       ScenarioGridColumn scenarioGridColumn,
                                                       String group,
                                                       Integer uiColumnIndex) {
        scenarioGrid.setSelectedColumnAndHeader(scenarioGridColumn.getHeaderMetaData().indexOf(clickedScenarioHeaderMetadata), uiColumnIndex);

        if (scenarioGridColumn.isInstanceAssigned() && clickedScenarioHeaderMetadata.getMetadataType().equals(ScenarioHeaderMetaData.MetadataType.INSTANCE)) {
            eventBus.fireEvent(new ReloadTestToolsEvent(true, true));
            return true;
        }
        EnableTestToolsEvent toFire = getEnableTestToolsEvent(scenarioGrid,
                                                              scenarioGridColumn,
                                                              clickedScenarioHeaderMetadata,
                                                              uiColumnIndex,
                                                              group);
        eventBus.fireEvent(toFire);
        return true;
    }

    /**
     * This method check if the click happened on an column of a <b>grid row</b>. If it is so, select the cell,
     * otherwise returns <code>false</code>
     * @param uiRowIndex
     * @param uiColumnIndex
     * @return
     */
    protected boolean manageGridLeftClick(Integer uiRowIndex, Integer uiColumnIndex) {
        final GridCell<?> cell = scenarioGrid.getModel().getCell(uiRowIndex, uiColumnIndex);
        if (cell == null) {
            return false;
        } else {
            scenarioGrid.getModel().selectCell(uiRowIndex, uiColumnIndex);
            return true;
        }
    }

    // Indirection add for test
    protected Integer getUiHeaderRowIndexLocal(Point2D clickPoint) {
        return CommonEditHandler.getUiHeaderRowIndexLocal(scenarioGrid, clickPoint);
    }

    // Indirection add for test
    protected Integer getUiRowIndexLocal(double relativeY) {
        return getUiRowIndex(scenarioGrid, relativeY);
    }

    // Indirection add for test
    protected Integer getUiColumnIndexLocal(double relativeX) {
        return getUiColumnIndex(scenarioGrid, relativeX);
    }

    // Indirection add for test
    protected Point2D convertDOMToGridCoordinateLocal(double canvasX, double canvasY) {
        return convertDOMToGridCoordinate(scenarioGrid,
                                          new Point2D(canvasX,
                                                      canvasY));
    }

    // Indirection add for test
    protected ScenarioHeaderMetaData getColumnScenarioHeaderMetaDataLocal(Point2D clickPoint) {
        return CommonEditHandler.getColumnScenarioHeaderMetaDataLocal(scenarioGrid, clickPoint);
    }
}
