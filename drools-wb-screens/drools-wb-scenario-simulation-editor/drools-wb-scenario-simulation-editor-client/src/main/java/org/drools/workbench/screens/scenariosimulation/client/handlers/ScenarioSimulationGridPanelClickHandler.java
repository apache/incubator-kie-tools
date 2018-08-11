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

import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities.getColumnScenarioHeaderMetaData;

public class ScenarioSimulationGridPanelClickHandler implements ClickHandler,
                                                                ContextMenuHandler {

    private ScenarioGrid scenarioGrid;
    private GridContextMenu gridContextMenu;
    private HeaderContextMenu headerContextMenu;

    public ScenarioSimulationGridPanelClickHandler(final ScenarioGrid scenarioGrid) {  // This could be make more generic, but apparently DefaultGridLayer could have more then one grid, so we have to find a way to
        // detect and identify the click on each of them
        this.scenarioGrid = scenarioGrid;
    }

    public void setGridContextMenu(GridContextMenu gridContextMenu) {
        this.gridContextMenu = gridContextMenu;
    }

    public void setHeaderContextMenu(HeaderContextMenu headerContextMenu) {
        this.headerContextMenu = headerContextMenu;
    }

    @Override
    public void onClick(ClickEvent event) {
        commonClickManagement();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onContextMenu(final ContextMenuEvent event) {
        event.preventDefault();
        event.stopPropagation();
        commonClickManagement();
        manageRightClick(event);
    }

    protected void manageRightClick(final ContextMenuEvent event) {
        final int canvasX = getRelativeX(event);
        final int canvasY = getRelativeY(event);
        final boolean isShiftKeyDown = event.getNativeEvent().getShiftKey();
        final boolean isControlKeyDown = event.getNativeEvent().getCtrlKey();
        final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate(scenarioGrid,
                                                                          new Point2D(canvasX,
                                                                                      canvasY));
        final Integer uiColumnIndex = CoordinateUtilities.getUiColumnIndex(scenarioGrid,
                                                                           ap.getX());
        if (uiColumnIndex == null) {
            return;
        }
        if (!manageHeaderRightClick(scenarioGrid, event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY(), ap.getY(), uiColumnIndex)) {
            manageBodyRightClick(scenarioGrid, event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY(), ap.getY(), uiColumnIndex, isShiftKeyDown, isControlKeyDown);
        }
    }

    protected int getRelativeX(final ContextMenuEvent event) {
        final NativeEvent e = event.getNativeEvent();
        final Element target = event.getRelativeElement();
        return e.getClientX() - target.getAbsoluteLeft() + target.getScrollLeft() + target.getOwnerDocument().getScrollLeft();
    }

    protected int getRelativeY(final ContextMenuEvent event) {
        final NativeEvent e = event.getNativeEvent();
        final Element target = event.getRelativeElement();
        return e.getClientY() - target.getAbsoluteTop() + target.getScrollTop() + target.getOwnerDocument().getScrollTop();
    }

    private void commonClickManagement() {
        gridContextMenu.hide();
        headerContextMenu.hide();
    }

    /**
     * This method check if the click happened on an <b>header</b> cell. If it is so, manage it and returns <code>true</code>,
     * otherwise returns <code>false</code>
     * @param scenarioGrid
     * @param left
     * @param top
     * @param gridY
     * @param uiColumnIndex
     * @return
     */
    private boolean manageHeaderRightClick(ScenarioGrid scenarioGrid, int left, int top, double gridY, Integer uiColumnIndex) {
        ScenarioHeaderMetaData columnMetadata = getColumnScenarioHeaderMetaData(scenarioGrid, scenarioGrid.getModel().getColumns().get(uiColumnIndex), gridY);
        if (columnMetadata == null) {
            return false;
        }
        headerContextMenu.show(left, top);
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
     * @param isShiftKeyDown
     * @param isControlKeyDown
     * @return
     */
    private boolean manageBodyRightClick(ScenarioGrid scenarioGrid, int left, int top, double gridY, Integer uiColumnIndex, boolean isShiftKeyDown, boolean isControlKeyDown) {
        scenarioGrid.deselect();
        final Integer uiRowIndex = CoordinateUtilities.getUiRowIndex(scenarioGrid, gridY);
        if (uiRowIndex == null) {
            return false;
        }
        gridContextMenu.show(left, top);
        return true;
    }
}
