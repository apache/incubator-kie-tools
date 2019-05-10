/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.wires.core.grids.client.widget.grid;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionManager;

/**
 * The base of all GridWidgets.
 */
public interface GridWidget extends IPrimitive<Group>,
                                    NodeMouseClickHandler,
                                    CellSelectionManager {

    /**
     * Returns the Model backing the Widget.
     * @return
     */
    GridData getModel();

    /**
     * Returns the Renderer used to render the Widget.
     * @return
     */
    GridRenderer getRenderer();

    /**
     * Returns the Rendered used to render the Widget.
     * @param renderer
     */
    void setRenderer(final GridRenderer renderer);

    /**
     * Returns helper for rendering the Widget.
     * @return
     */
    BaseGridRendererHelper getRendererHelper();

    /**
     * Returns the Group representing the GridWidget's Body
     * @return
     */
    Group getBody();

    /**
     * Returns the Group representing the GridWidget's Header
     * @return
     */
    Group getHeader();

    /**
     * Returns the width of the whole Widget.
     * @return
     */
    double getWidth();

    /**
     * Returns the height of the whole Widget, including Header and Body.
     * @return
     */
    double getHeight();

    /**
     * Selects the Widget; i.e. it has been clicked on, so show some visual indicator that it has been selected.
     */
    void select();

    /**
     * Deselects the Widget; i.e. another GridWidget has been clicked on, so hide
     * any visual indicator that this Widget was selected.
     */
    void deselect();

    /**
     * Returns the selected state of the Widget.
     * @return true if the Widget is selected.
     */
    boolean isSelected();

    /**
     * Show context menu of a header cell at coordinates 'uiHeaderRowIndex' and 'uiHeaderColumnIndex'.
     * If the provided coordinate does not resolve to a header cell in the Grid no operation is performed.
     * @param uiHeaderRowIndex Header row index of cell to invoke context menu
     * @param uiHeaderColumnIndex Header column index of cell to invoke context menu
     * @return true if menu was shown.
     */
    boolean showContextMenuForHeader(final int uiHeaderRowIndex,
                                     final int uiHeaderColumnIndex);

    /**
     * Show context menu of a cell at coordinates 'uiRowIndex' and 'uiColumnIndex'.
     * If the provided coordinate does not resolve to a cell in the Grid no operation is performed.
     * @param uiRowIndex Row index of cell to invoke context menu
     * @param uiColumnIndex Column index of cell to invoke context menu
     * @return true if menu was shown.
     */
    boolean showContextMenuForCell(final int uiRowIndex,
                                   final int uiColumnIndex);

    /**
     * Returns the {@link CellSelectionManager} associated with the {@link GridWidget}
     * @return
     */
    CellSelectionManager getCellSelectionManager();

    /**
     * Checks whether a cell-relative coordinate is "on" the hot-spot to toggle the collapsed/expanded state.
     * @param cellX The MouseEvent relative to the cell's x-coordinate.
     * @param cellY The MouseEvent relative to the cell's y-coordinate.
     * @param cellWidth Width of the containing cell.
     * @param cellHeight Height of the containing cell.
     * @return true if the cell coordinate is on the hot-spot.
     */
    boolean onGroupingToggle(final double cellX,
                             final double cellY,
                             final double cellWidth,
                             final double cellHeight);

    /**
     * Checks whether a canvas coordinate is within the "drag handle" for the GridWidget.
     * Canvas coordinates can be mapped to coordinates relative to the GridWidget with
     * {@link CoordinateUtilities#convertDOMToGridCoordinate(GridWidget, Point2D)}
     * @param event The INodeXYEvent relative to the canvas coordinate system.
     * @return true if the event is within the drag handle.
     */
    @SuppressWarnings("unused")
    default boolean onDragHandle(final INodeXYEvent event) {
        return false;
    }
}
