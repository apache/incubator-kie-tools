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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.Dependent;

import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.AbstractHeaderMenuPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.BaseMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.ExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderGivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.OtherContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.UnmodifiableColumnGridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.events.DisableRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities.getColumnScenarioHeaderMetaData;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities.getEnableRightPanelEvent;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.convertDOMToGridCoordinate;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getRelativeXOfEvent;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getRelativeYOfEvent;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getUiColumnIndex;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getUiHeaderRowIndex;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getUiRowIndex;

@Dependent
public class ScenarioSimulationGridPanelClickHandler implements ClickHandler,
                                                                ContextMenuHandler {

    protected ScenarioGrid scenarioGrid;
    protected OtherContextMenu otherContextMenu;
    protected HeaderGivenContextMenu headerGivenContextMenu;
    protected HeaderExpectedContextMenu headerExpectedContextMenu;
    protected GivenContextMenu givenContextMenu;
    protected ExpectedContextMenu expectedContextMenu;
    protected GridContextMenu gridContextMenu;
    protected UnmodifiableColumnGridContextMenu unmodifiableColumnGridContextMenu;
    protected Set<AbstractHeaderMenuPresenter> managedMenus = new HashSet<>();
    protected EventBus eventBus;
    protected AtomicInteger clickReceived = new AtomicInteger(0);
    protected BaseGridRendererHelper rendererHelper;

    public ScenarioSimulationGridPanelClickHandler() {
    }

    public void setScenarioGrid(ScenarioGrid scenarioGrid) {
        this.scenarioGrid = scenarioGrid;
        this.rendererHelper = scenarioGrid.getRendererHelper();
    }

    public void setOtherContextMenu(OtherContextMenu otherContextMenu) {
        this.otherContextMenu = otherContextMenu;
        managedMenus.add(otherContextMenu);
    }

    public void setHeaderGivenContextMenu(HeaderGivenContextMenu headerGivenContextMenu) {
        this.headerGivenContextMenu = headerGivenContextMenu;
        managedMenus.add(headerGivenContextMenu);
    }

    public void setHeaderExpectedContextMenu(HeaderExpectedContextMenu headerExpectedContextMenu) {
        this.headerExpectedContextMenu = headerExpectedContextMenu;
        managedMenus.add(headerExpectedContextMenu);
    }

    public void setGivenContextMenu(GivenContextMenu givenContextMenu) {
        this.givenContextMenu = givenContextMenu;
        managedMenus.add(givenContextMenu);
    }

    public void setExpectedContextMenu(ExpectedContextMenu expectedContextMenu) {
        this.expectedContextMenu = expectedContextMenu;
        managedMenus.add(expectedContextMenu);
    }

    public void setGridContextMenu(GridContextMenu gridContextMenu) {
        this.gridContextMenu = gridContextMenu;
        managedMenus.add(gridContextMenu);
    }

    public void setUnmodifiableColumnGridContextMenu(UnmodifiableColumnGridContextMenu unmodifiableColumnGridContextMenu) {
        this.unmodifiableColumnGridContextMenu = unmodifiableColumnGridContextMenu;
        managedMenus.add(unmodifiableColumnGridContextMenu);
    }

    /**
     * This method must be called <b>after</b> all the <i>ContextMenu</i> setters
     * @param eventBus
     */
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        managedMenus.forEach(menu -> menu.setEventBus(eventBus));
    }

    @Override
    public void onClick(ClickEvent event) {
        clickReceived.getAndIncrement();
        final int canvasX = getRelativeXOfEvent(event);
        final int canvasY = getRelativeYOfEvent(event);
        hideMenus();
        scenarioGrid.clearSelections();
        if (!manageLeftClick(canvasX, canvasY)) { // It was not a grid click
            eventBus.fireEvent(new DisableRightPanelEvent());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onContextMenu(final ContextMenuEvent event) {
        hideMenus();
        if (manageRightClick(event)) {
            event.preventDefault();
            event.stopPropagation();
        }
    }

    protected boolean manageRightClick(final ContextMenuEvent event) {
        final int canvasX = getRelativeXOfEvent(event);
        final int canvasY = getRelativeYOfEvent(event);
        final Point2D gridClickPoint = convertDOMToGridCoordinate(scenarioGrid,
                                                                  new Point2D(canvasX,
                                                                              canvasY));
        final Integer uiColumnIndex = getUiColumnIndex(scenarioGrid,
                                                       gridClickPoint.getX());
        if (uiColumnIndex == null) {
            return false;
        }
        if (!manageHeaderRightClick(scenarioGrid,
                                    event.getNativeEvent().getClientX(),
                                    event.getNativeEvent().getClientY(),
                                    gridClickPoint,
                                    uiColumnIndex)) {
            return manageBodyRightClick(scenarioGrid,
                                        event.getNativeEvent().getClientX(),
                                        event.getNativeEvent().getClientY(),
                                        gridClickPoint.getY(),
                                        uiColumnIndex);
        } else {
            return true;
        }
    }

    public void hideMenus() {
        managedMenus.forEach(BaseMenu::hide);
    }

    /**
     * This method check if the click happened on an <b>header</b> cell. If it is so, manage it and returns <code>true</code>,
     * otherwise returns <code>false</code>
     * @param scenarioGrid
     * @param left
     * @param top
     * @param clickPoint - coordinates relative to the grid top left corner
     * @param uiColumnIndex
     * @return
     */
    protected boolean manageHeaderRightClick(ScenarioGrid scenarioGrid, int left, int top, Point2D clickPoint, Integer uiColumnIndex) {
        ScenarioHeaderMetaData columnMetadata = getColumnScenarioHeaderMetaDataLocal(scenarioGrid, clickPoint);
        if (columnMetadata == null) {
            return false;
        }
        //Get row index
        final Integer uiHeaderRowIndex = getUiHeaderRowIndexLocal(scenarioGrid, clickPoint);
        if (uiHeaderRowIndex == null) {
            return false;
        }
        String group = columnMetadata.getColumnGroup();
        if (group.contains("-")) {
            group = group.substring(0, group.indexOf("-"));
        }
        switch (group) {
            case "":
                switch (columnMetadata.getTitle()) {
                    case "GIVEN":
                        headerGivenContextMenu.show(left, top);
                        break;
                    case "EXPECT":
                        headerExpectedContextMenu.show(left, top);
                        break;
                    default:
                        otherContextMenu.show(left, top);
                }
                break;
            case "GIVEN":
                givenContextMenu.show(left, top, uiColumnIndex, group, columnMetadata.isPropertyHeader());
                break;
            case "EXPECT":
                expectedContextMenu.show(left, top, uiColumnIndex, group, columnMetadata.isPropertyHeader());
                break;
            default:
                otherContextMenu.show(left, top);
        }
        scenarioGrid.setSelectedColumnAndHeader(uiHeaderRowIndex, uiColumnIndex);
        return true;
    }

    /**
     * This method check if the click happened on an <b>body</b> cell. If it is so, manage it and returns <code>true</code>,
     * otherwise returns <code>false</code>
     * @param scenarioGrid
     * @param left
     * @param top
     * @param gridY
     * @param uiColumnIndex
     * @return
     */
    protected boolean manageBodyRightClick(ScenarioGrid scenarioGrid, int left, int top, double gridY, Integer uiColumnIndex) {
        scenarioGrid.deselect();
        final Integer uiRowIndex = getUiRowIndex(scenarioGrid, gridY);
        if (uiRowIndex == null) {
            return false;
        }
        ScenarioGridColumn scenarioGridColumn = (ScenarioGridColumn) scenarioGrid.getModel().getColumns().get(uiColumnIndex);
        if (scenarioGridColumn == null) {
            return false;
        }
        String group = scenarioGridColumn.getInformationHeaderMetaData().getColumnGroup();
        switch (group) {
            case "GIVEN":
            case "EXPECT":
                gridContextMenu.show(left, top, uiColumnIndex, uiRowIndex, group, true);
                break;
            default:
                unmodifiableColumnGridContextMenu.show(left, top, uiRowIndex);
        }
        return true;
    }

    /**
     * @param canvasX
     * @param canvasY
     * @return
     */
    protected boolean manageLeftClick(final int canvasX, final int canvasY) {
        final Point2D gridClickPoint = convertDOMToGridCoordinate(scenarioGrid,
                                                                  new Point2D(canvasX,
                                                                              canvasY));
        final Integer uiColumnIndex = getUiColumnIndex(scenarioGrid,
                                                       gridClickPoint.getX());
        if (uiColumnIndex == null) {
            return false;
        }
        ScenarioGridColumn scenarioGridColumn = (ScenarioGridColumn) scenarioGrid.getModel().getColumns().get(uiColumnIndex);
        if (scenarioGridColumn == null) {
            return false;
        }
        if (!manageHeaderLeftClick(uiColumnIndex, scenarioGridColumn, gridClickPoint)) {
        final Integer uiRowIndex = getUiRowIndex(scenarioGrid, gridClickPoint.getY());

        if (uiRowIndex == null) {
            return false;
        } else {
            return manageGridLeftClick(uiRowIndex, uiColumnIndex);
        }
        } else {
            return true;
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
        final Integer uiHeaderRowIndex = getUiHeaderRowIndexLocal(scenarioGrid, clickPoint);
        if (uiHeaderRowIndex == null) {
            return false;
        }
        if (!isEditableHeaderLocal(scenarioGridColumn, uiHeaderRowIndex)) {
            return false;
        }
        ScenarioHeaderMetaData clickedScenarioHeaderMetadata = getColumnScenarioHeaderMetaDataLocal(scenarioGrid, clickPoint);
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

        if (scenarioGridColumn.isInstanceAssigned() && clickedScenarioHeaderMetadata.isInstanceHeader()) {
            eventBus.fireEvent(new ReloadRightPanelEvent(true, true));
            return true;
        }
        EnableRightPanelEvent toFire = getEnableRightPanelEvent(scenarioGrid,
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
    protected ScenarioHeaderMetaData getColumnScenarioHeaderMetaDataLocal(ScenarioGrid scenarioGrid, Point2D point) {
        return getColumnScenarioHeaderMetaData(scenarioGrid, point);
    }

    // Indirection add for test
    protected Integer getUiHeaderRowIndexLocal(ScenarioGrid scenarioGrid, Point2D point) {
        return getUiHeaderRowIndex(scenarioGrid, point);
    }

    // Indirection add for test
    protected boolean isEditableHeaderLocal(GridColumn<?> scenarioGridColumn, Integer uiHeaderRowIndex) {
        return ScenarioSimulationGridHeaderUtilities.isEditableHeader(scenarioGridColumn, uiHeaderRowIndex);
    }

}
