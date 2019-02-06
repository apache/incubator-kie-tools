/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities.getColumnScenarioHeaderMetaData;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities.getEnableRightPanelEvent;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getUiHeaderRowIndex;

/**
 * This class is meant to provide common implementations for <b>editing</b> cell to be used by click handler and keyboard handler
 */
public class CommonEditHandler {

    /**
     * Start editing a cell
     * @param scenarioGrid
     * @param uiColumnIndex
     * @param scenarioGridColumn
     * @param uiRowIndex
     * @param isHeader
     * @param eventBus
     * @return
     */
    public static boolean startEdit(ScenarioGrid scenarioGrid, Integer uiColumnIndex, ScenarioGridColumn scenarioGridColumn, Integer uiRowIndex, boolean isHeader, EventBus eventBus) {
        if (isHeader) {
            return manageHeaderLeftClick(scenarioGrid, uiColumnIndex, scenarioGridColumn, uiRowIndex, eventBus);
        } else {
            return manageGridLeftClick(scenarioGrid, uiRowIndex, uiColumnIndex, scenarioGridColumn);
        }
    }

    /**
     * This method check if the click happened on an <b>second level header</b> (i.e. the header of a specific column) cell. If it is so, manage it and returns <code>true</code>,
     * otherwise returns <code>false</code>
     * @param scenarioGrid
     * @param uiColumnIndex
     * @param scenarioGridColumn
     * @param uiHeaderRowIndex
     * @param eventBus
     * @return
     */
    protected static boolean manageHeaderLeftClick(ScenarioGrid scenarioGrid, Integer uiColumnIndex, ScenarioGridColumn scenarioGridColumn, Integer uiHeaderRowIndex, EventBus eventBus) {
        if (!isEditableHeaderLocal(scenarioGridColumn, uiHeaderRowIndex)) {
            return false;
        }
        ScenarioHeaderMetaData clickedScenarioHeaderMetadata = (ScenarioHeaderMetaData) scenarioGridColumn.getHeaderMetaData().get(uiHeaderRowIndex);
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
                return manageGivenExpectHeaderLeftClick(scenarioGrid, clickedScenarioHeaderMetadata,
                                                        scenarioGridColumn,
                                                        group,
                                                        uiColumnIndex, eventBus);
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
    protected static boolean manageGivenExpectHeaderLeftClick(ScenarioGrid scenarioGrid, ScenarioHeaderMetaData clickedScenarioHeaderMetadata,
                                                              ScenarioGridColumn scenarioGridColumn,
                                                              String group,
                                                              Integer uiColumnIndex, EventBus eventBus) {
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
     * This method check if the click happened on an <i>writable</i> column of a <b>grid row</b>. If it is so, start editing the cell,
     * otherwise returns <code>false</code>
     * @param uiRowIndex
     * @param uiColumnIndex
     * @param scenarioGridColumn
     * @return
     */
    public static boolean manageGridLeftClick(ScenarioGrid scenarioGrid, Integer uiRowIndex, Integer uiColumnIndex, ScenarioGridColumn scenarioGridColumn) {
        final GridCell<?> cell = scenarioGrid.getModel().getCell(uiRowIndex, uiColumnIndex);
        if (cell == null) {
            return false;
        }
        if (((ScenarioGridCell) cell).isEditingMode()) {
            return true;
        }
        ((ScenarioGridCell) cell).setEditingMode((!scenarioGridColumn.isReadOnly()) && scenarioGrid.startEditingCell(uiRowIndex, uiColumnIndex));
        return ((ScenarioGridCell) cell).isEditingMode();
    }

    // Indirection add for test
    protected static ScenarioHeaderMetaData getColumnScenarioHeaderMetaDataLocal(ScenarioGrid scenarioGrid, Point2D point) {
        return getColumnScenarioHeaderMetaData(scenarioGrid, point);
    }

    // Indirection add for test
    protected static Integer getUiHeaderRowIndexLocal(ScenarioGrid scenarioGrid, Point2D point) {
        return getUiHeaderRowIndex(scenarioGrid, point);
    }

    // Indirection add for test
    protected static boolean isEditableHeaderLocal(GridColumn<?> scenarioGridColumn, Integer uiHeaderRowIndex) {
        return ScenarioSimulationGridHeaderUtilities.isEditableHeader(scenarioGridColumn, uiHeaderRowIndex);
    }
}
