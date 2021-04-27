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
package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.Objects;
import java.util.Optional;

import com.ait.lienzo.client.core.event.AbstractNodeMouseEvent;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;

/**
 * A {@link NodeMouseEventHandler} to handle editing of cells.
 */
public class DefaultGridWidgetEditCellMouseEventHandler implements NodeMouseEventHandler {

    @Override
    public boolean onNodeMouseEvent(final GridWidget gridWidget,
                                    final Point2D relativeLocation,
                                    final Optional<Integer> uiHeaderRowIndex,
                                    final Optional<Integer> uiHeaderColumnIndex,
                                    final Optional<Integer> uiRowIndex,
                                    final Optional<Integer> uiColumnIndex,
                                    final AbstractNodeMouseEvent event) {
        if (isDNDOperationInProgress(gridWidget)) {
            return false;
        }

        boolean isHandled = false;
        if (uiHeaderRowIndex.isPresent() && uiHeaderColumnIndex.isPresent()) {
            isHandled = handleHeaderCell(gridWidget,
                                         relativeLocation,
                                         uiHeaderRowIndex.get(),
                                         uiHeaderColumnIndex.get(),
                                         event);
        }
        if (!isHandled && uiRowIndex.isPresent() && uiColumnIndex.isPresent()) {
            isHandled = handleBodyCell(gridWidget,
                                       relativeLocation,
                                       uiRowIndex.get(),
                                       uiColumnIndex.get(),
                                       event);
        }

        return isHandled;
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
            if (Objects.equals(cellEditAction, GridCellEditAction.getSupportedEditAction(event))) {
                return gridWidget.startEditingCell(relativeLocation);
            }
        }
        return false;
    }
}
