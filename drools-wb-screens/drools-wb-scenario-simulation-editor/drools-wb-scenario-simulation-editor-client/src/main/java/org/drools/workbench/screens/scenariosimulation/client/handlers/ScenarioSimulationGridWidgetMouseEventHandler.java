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
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.DefaultGridWidgetEditCellMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

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
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(cx);
        final GridColumn<?> column = ci.getColumn();
        if (column == null) {
            return false;
        }
        final ScenarioHeaderMetaData headerMetaData = (ScenarioHeaderMetaData) column.getHeaderMetaData().get(uiHeaderRowIndex);
        final GridData gridData = gridWidget.getModel();
        if (gridData.getSelectedHeaderCells().size() == 1 && editSupportedLocal(headerMetaData.getSupportedEditAction(), event)) {
            return startEditLocal((ScenarioGrid) gridWidget, uiHeaderColumnIndex, (ScenarioGridColumn) column, uiHeaderRowIndex, true);
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
            if (editSupportedLocal(cellEditAction, event)) {
                ScenarioGridColumn scenarioGridColumn = (ScenarioGridColumn) gridWidget.getModel().getColumns().get(uiColumnIndex);
                return startEditLocal((ScenarioGrid) gridWidget, uiColumnIndex, scenarioGridColumn, uiRowIndex, false);
            }
        }
        return false;
    }

    // Indirection add for test
    protected boolean editSupportedLocal(GridCellEditAction gridCellEditAction, final AbstractNodeMouseEvent event) {
        return Objects.equals(gridCellEditAction, GridCellEditAction.getSupportedEditAction(event));
    }

    protected boolean startEditLocal(ScenarioGrid scenarioGrid, int uiHeaderColumnIndex, ScenarioGridColumn scenarioGridColumn, int uiHeaderRowIndex, boolean isHeader) {
        return CommonEditHandler.startEdit(scenarioGrid, uiHeaderColumnIndex, scenarioGridColumn, uiHeaderRowIndex, isHeader);
    }
}
