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
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.util.CellContextUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities.getColumnScenarioHeaderMetaData;
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
     * @return
     */
    public static boolean startEdit(ScenarioGrid scenarioGrid, Integer uiColumnIndex, ScenarioGridColumn scenarioGridColumn, Integer uiRowIndex, boolean isHeader) {
        if (isHeader) {
            return manageHeaderLeftClick(scenarioGrid, uiColumnIndex, scenarioGridColumn, uiRowIndex);
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
     * @return
     */
    protected static boolean manageHeaderLeftClick(ScenarioGrid scenarioGrid, Integer uiColumnIndex, ScenarioGridColumn scenarioGridColumn, Integer uiHeaderRowIndex/*, EventBus eventBus*/) {
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
                return manageGivenExpectHeaderLeftClick(scenarioGrid, clickedScenarioHeaderMetadata, uiColumnIndex, uiHeaderRowIndex);
            default:
                return false;
        }
    }

    /**
     * This method manage the click happened on an <i>GIVEN</i> or <i>EXPECT</i> header, starting editing it if not already did.
     * @param clickedScenarioHeaderMetadata
     * @return
     */
    protected static boolean manageGivenExpectHeaderLeftClick(ScenarioGrid scenarioGrid, ScenarioHeaderMetaData clickedScenarioHeaderMetadata, int uiColumnIndex, int uiHeaderRowIndex) {
        final ScenarioGridModel gridModel = scenarioGrid.getModel();
        final GridColumn<?> column = gridModel.getColumns().get(uiColumnIndex);
        final BaseGridRendererHelper rendererHelper = scenarioGrid.getRendererHelper();
        final BaseGridRendererHelper.RenderingInformation ri = rendererHelper.getRenderingInformation();
        final double columnXCoordinate = rendererHelper.getColumnOffset(column) + column.getWidth() / 2;
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(columnXCoordinate);
        final GridBodyCellEditContext context = CellContextUtilities.makeHeaderCellRenderContext(scenarioGrid,
                                                                                                 ri,
                                                                                                 ci,
                                                                                                 null,
                                                                                                 uiHeaderRowIndex);
        clickedScenarioHeaderMetadata.edit(context);
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
    protected static boolean isEditableHeaderLocal(ScenarioGridColumn scenarioGridColumn, Integer uiHeaderRowIndex) {
        return ScenarioSimulationGridHeaderUtilities.isEditableHeader(scenarioGridColumn, uiHeaderRowIndex);
    }
}
