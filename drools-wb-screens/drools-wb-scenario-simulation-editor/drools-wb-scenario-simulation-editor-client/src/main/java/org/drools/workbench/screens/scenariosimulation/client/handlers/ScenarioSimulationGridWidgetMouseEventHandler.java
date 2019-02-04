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

import java.util.Objects;

import com.ait.lienzo.client.core.event.AbstractNodeMouseEvent;
import com.ait.lienzo.client.core.types.Point2D;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.CellContextUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.DefaultGridWidgetEditCellMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities.isHeaderEditable;

public class ScenarioSimulationGridWidgetMouseEventHandler extends DefaultGridWidgetEditCellMouseEventHandler {

    @Override
    public boolean handleHeaderCell(final GridWidget gridWidget,
                                    final Point2D relativeLocation,
                                    final int uiHeaderRowIndex,
                                    final int uiHeaderColumnIndex,
                                    final AbstractNodeMouseEvent event) {
        //Get column information
        final double cx = relativeLocation.getX();
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final BaseGridRendererHelper.RenderingInformation ri = rendererHelper.getRenderingInformation();
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(cx);
        final GridColumn<?> column = ci.getColumn();
        if (column == null) {
            return false;
        }
        if (!isEditableHeaderLocal(column, uiHeaderRowIndex)) {
            return true;
        }
        final ScenarioHeaderMetaData headerMetaData = (ScenarioHeaderMetaData) column.getHeaderMetaData().get(uiHeaderRowIndex);
        final GridData gridData = gridWidget.getModel();
        if (gridData.getSelectedHeaderCells().size() == 1 &&
                Objects.equals(headerMetaData.getSupportedEditAction(), GridCellEditAction.getSupportedEditAction(event)) &&
                isHeaderEditable(rendererHelper, headerMetaData, (ScenarioGridColumn) column)) {
            final Point2D gridWidgetComputedLocation = gridWidget.getComputedLocation();
            final GridBodyCellEditContext context = CellContextUtilities.makeRenderContext(gridWidget,
                                                                                           ri,
                                                                                           ci,
                                                                                           relativeLocation.add(gridWidgetComputedLocation),
                                                                                           uiHeaderRowIndex);
            headerMetaData.edit(context);
        }
        return true;
    }

    /**
     * Checks if a {@link AbstractNodeMouseEvent} happened within a {@link GridCell}. If the
     * {@link AbstractNodeMouseEvent} is found to have happened within a cell, the {@link GridCell#getSupportedEditAction()}
     * is checked to {@link Object#equals(Object)} that for the {@link AbstractNodeMouseEvent}. If they equal then the
     * {@link GridCell} is put into "edit" mode via {@link GridWidget#startEditingCell(Point2D)}.
     */
    @Override
    public boolean handleBodyCell(final GridWidget gridWidget,
                                  final Point2D relativeLocation,
                                  final int uiRowIndex,
                                  final int uiColumnIndex,
                                  final AbstractNodeMouseEvent event) {
        final GridData gridData = gridWidget.getModel();
        if (gridData.getSelectedCells().size() == 1) {
            final GridCell<?> cell = gridData.getCell(uiRowIndex, uiColumnIndex);
            final GridCellEditAction cellEditAction = cell == null ? GridCell.DEFAULT_EDIT_ACTION : cell.getSupportedEditAction();
            if (Objects.equals(cellEditAction, getSupportedEditActionLocal(event))) {
                ScenarioGridColumn scenarioGridColumn = (ScenarioGridColumn) gridWidget.getModel().getColumns().get(uiColumnIndex);
                return manageStartEditingGridCell((ScenarioGrid) gridWidget, uiRowIndex, uiColumnIndex, scenarioGridColumn);
            }
        }
        return false;
    }

    /**
     * This method check if the click happened on an <i>writable</i> column of a <b>grid row</b>. If it is so, start editing the cell,
     * otherwise returns <code>false</code>
     *
     * @param scenarioGrid
     * @param uiRowIndex
     * @param uiColumnIndex
     * @param scenarioGridColumn
     * @return
     */
    protected boolean manageStartEditingGridCell(ScenarioGrid scenarioGrid, Integer uiRowIndex, Integer uiColumnIndex, ScenarioGridColumn scenarioGridColumn) {
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
    protected boolean isEditableHeaderLocal(GridColumn<?> scenarioGridColumn, Integer uiHeaderRowIndex) {
        return ScenarioSimulationGridHeaderUtilities.isEditableHeader(scenarioGridColumn, uiHeaderRowIndex);
    }

    // Indirection add for test
    protected GridCellEditAction getSupportedEditActionLocal(final AbstractNodeMouseEvent event) {
        return GridCellEditAction.getSupportedEditAction(event);
    }
}
