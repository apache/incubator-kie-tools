/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.widget.grid.selections;

import com.ait.lienzo.client.core.types.Point2D;

/**
 * Manager for Cell selection operations.
 */
public interface CellSelectionManager {

    /**
     * Handles selection of a cell by delegating selection to a @{link CellSelectionStrategy} associated
     * with the cell being selected. Different strategies may select an entire row, a range depending
     * upon shift/control key states etc.
     * @param rp Canvas coordinate relative to the GridWidget.
     * @param isShiftKeyDown True if the shift key is pressed.
     * @param isControlKeyDown True if the control key is pressed.
     * @return true if the selections have changed.
     */
    boolean selectCell(final Point2D rp,
                       final boolean isShiftKeyDown,
                       final boolean isControlKeyDown);

    /**
     * Handles selection of a cell by delegating selection to a @{link CellSelectionStrategy} associated
     * with the cell being selected. Different strategies may select an entire row, a range depending
     * upon shift/control key states etc.
     * @param uiRowIndex Index of row as seen in the UI
     * @param uiColumnIndex Index of the column as seen in the UI
     * @param isShiftKeyDown True if the shift key is pressed.
     * @param isControlKeyDown True if the control key is pressed.
     * @return true if the selections have changed.
     */
    boolean selectCell(final int uiRowIndex,
                       final int uiColumnIndex,
                       final boolean isShiftKeyDown,
                       final boolean isControlKeyDown);

    /**
     * Handles selection of a cell in the Header by delegating selection to a @{link HeaderCellSelectionStrategy}
     * associated with the header cell being selected. Different strategies may select an entire column, or a range
     * depending upon shift/control key states etc.
     * @param rp Canvas coordinate relative to the GridWidget.
     * @param isShiftKeyDown True if the shift key is pressed.
     * @param isControlKeyDown True if the control key is pressed.
     * @return true if the selections have changed.
     */
    boolean selectHeaderCell(final Point2D rp,
                             final boolean isShiftKeyDown,
                             final boolean isControlKeyDown);

    /**
     * Handles selection of a cell in the Header by delegating selection to a @{link HeaderCellSelectionStrategy}
     * associated with the header cell being selected. Different strategies may select an entire column, or a range
     * depending upon shift/control key states etc.
     * @param uiHeaderRowIndex Index of row as seen in the UI. 0-based index. Top row is 0.
     * @param uiHeaderColumnIndex Index of the column as seen in the UI. 0-based index. Leftmost column is 0.
     * @param isShiftKeyDown True if the shift key is pressed.
     * @param isControlKeyDown True if the control key is pressed.
     * @return true if the selections have changed.
     */
    boolean selectHeaderCell(final int uiHeaderRowIndex,
                             final int uiHeaderColumnIndex,
                             final boolean isShiftKeyDown,
                             final boolean isControlKeyDown);

    /**
     * Adjusts an existing selection, based on the selection origin, depending on the
     * provided parameters. If the shift key is down the current selected range is extended
     * in the required direction; otherwise the current origin is moved in the required direction.
     * @param direction The proposed direction in which to extend the selection.
     * @param isShiftKeyDown true if the shift key is pressed.
     * @return true if the selection changed, otherwise false.
     */
    boolean adjustSelection(final SelectionExtension direction,
                            final boolean isShiftKeyDown);

    /**
     * Handles initiation of editing a cell. If the provided Canvas coordinate
     * does not resolve to a cell in the Grid no operation if performed.
     * @param rp Canvas coordinate relative to the GridWidget.
     * @return true if an edit operation was successfully initiated.
     */
    boolean startEditingCell(final Point2D rp);

    /**
     * Handles initiation of editing a cell. If the provided Canvas coordinate
     * does not resolve to a cell in the Grid no operation if performed.
     * @param uiRowIndex Row index of cell being edited.
     * @param uiColumnIndex Column index of cell being edited.
     * @return true if an edit operation was successfully initiated.
     */
    boolean startEditingCell(final int uiRowIndex,
                             final int uiColumnIndex);
}
