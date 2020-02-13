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

package org.uberfire.ext.wires.core.grids.client.widget.grid;

import java.util.Optional;

import com.ait.lienzo.client.core.event.AbstractNodeMouseEvent;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * Defines a generic handler for any type of {@link AbstractNodeMouseEvent}.
 */
public interface NodeMouseEventHandler {

    /**
     * Executes when a {@link NodeMouseEventHandler} reacts to the {@link AbstractNodeMouseEvent}
     * to which the {@link NodeMouseEventHandler} has been registered. Note {@code uiHeaderRowIndex},
     * {@code uiHeaderColumnIndex}, {@code uiRowIndex} and {@code uiColumnIndex} may be {@link Optional#empty()} if
     * the event did not occur over the applicable element within the {@link GridWidget}.
     * @param gridWidget The {@link GridWidget} on which the event occurred.
     * @param relativeLocation {@link Point2D} relative to the top-left of the {@link GridWidget}
     * @param uiHeaderRowIndex Index of the Header row as seen in the UI. 0-based index. Top row is 0.
     * @param uiHeaderColumnIndex Index of the Header column as seen in the UI. 0-based index. Leftmost column is 0.
     * @param uiRowIndex Index of the Body row as seen in the UI
     * @param uiColumnIndex Index of the Body column as seen in the UI
     * @param event The original event.
     * @return true if the event was handled.
     */
    boolean onNodeMouseEvent(final GridWidget gridWidget,
                             final Point2D relativeLocation,
                             final Optional<Integer> uiHeaderRowIndex,
                             final Optional<Integer> uiHeaderColumnIndex,
                             final Optional<Integer> uiRowIndex,
                             final Optional<Integer> uiColumnIndex,
                             final AbstractNodeMouseEvent event);

    /**
     * Handles the event for a Header element.
     * @param gridWidget The {@link GridWidget} on which the event occurred.
     * @param relativeLocation {@link Point2D} relative to the top-left of the {@link GridWidget}
     * @param uiHeaderRowIndex Index of the Header row as seen in the UI. 0-based index. Top row is 0.
     * @param uiHeaderColumnIndex Index of the Header column as seen in the UI. 0-based index. Leftmost column is 0.
     * @param event The original event.
     * @return true if the event was handled.
     */
    default boolean handleHeaderCell(final GridWidget gridWidget,
                                     final Point2D relativeLocation,
                                     final int uiHeaderRowIndex,
                                     final int uiHeaderColumnIndex,
                                     final AbstractNodeMouseEvent event) {
        return false;
    }

    /**
     * Handles the event for a Body element.
     * @param gridWidget The {@link GridWidget} on which the event occurred.
     * @param relativeLocation {@link Point2D} relative to the top-left of the {@link GridWidget}
     * @param uiRowIndex Index of the Body row as seen in the UI
     * @param uiColumnIndex Index of the Body column as seen in the UI
     * @param event The original event.
     * @return true if the event was handled.
     */
    default boolean handleBodyCell(final GridWidget gridWidget,
                                   final Point2D relativeLocation,
                                   final int uiRowIndex,
                                   final int uiColumnIndex,
                                   final AbstractNodeMouseEvent event) {
        return false;
    }

    /**
     * Returns whether the {@link AbstractNodeMouseEvent} occurred during a Drag and Drop operation.
     * @param gridWidget The {@link GridWidget} on which the event occurred.
     * @return true if the event occurred during a Drag and Drop operation.
     */
    default boolean isDNDOperationInProgress(final GridWidget gridWidget) {
        if (!(gridWidget.getLayer() instanceof GridLayer)) {
            return false;
        }
        final GridLayer gridLayer = (GridLayer) gridWidget.getLayer();
        final GridWidgetDnDHandlersState.GridWidgetHandlersOperation operation = gridLayer.getGridWidgetHandlersState().getOperation();
        switch (operation) {
            case NONE:
            case COLUMN_RESIZE_PENDING:
            case COLUMN_MOVE_PENDING:
            case COLUMN_MOVE_INITIATED:
            case ROW_MOVE_PENDING:
            case ROW_MOVE_INITIATED:
            case GRID_MOVE_PENDING:
                return false;
        }
        return true;
    }
}
