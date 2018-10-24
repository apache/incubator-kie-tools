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
package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;

/**
 * MouseClickHandler to handle selection of cells.
 */
public class GridCellSelectorMouseClickHandler implements NodeMouseClickHandler {

    protected GridData gridModel;
    protected GridWidget gridWidget;
    protected BaseGridRendererHelper rendererHelper;
    protected GridSelectionManager selectionManager;
    protected GridRenderer renderer;

    public GridCellSelectorMouseClickHandler(final GridWidget gridWidget,
                                             final GridSelectionManager selectionManager,
                                             final GridRenderer renderer) {
        this.gridWidget = gridWidget;
        this.gridModel = gridWidget.getModel();
        this.rendererHelper = gridWidget.getRendererHelper();
        this.selectionManager = selectionManager;
        this.renderer = renderer;
    }

    @Override
    public void onNodeMouseClick(final NodeMouseClickEvent event) {
        if (!gridWidget.isVisible()) {
            return;
        }
        handleHeaderCellClick(event);
        handleBodyCellClick(event);
    }

    /**
     * Select header cells.
     * @param event
     */
    protected void handleHeaderCellClick(final NodeMouseClickEvent event) {
        //Convert Canvas co-ordinate to Grid co-ordinate
        final Point2D rp = CoordinateUtilities.convertDOMToGridCoordinate(gridWidget,
                                                                          new Point2D(event.getX(),
                                                                                      event.getY()));

        if (gridWidget.selectHeaderCell(rp,
                                        event.isShiftKeyDown(),
                                        event.isControlKeyDown())) {
            gridWidget.getLayer().batch();
        }
    }

    /**
     * Select body cells.
     * @param event
     */
    protected void handleBodyCellClick(final NodeMouseClickEvent event) {
        //Convert Canvas co-ordinate to Grid co-ordinate
        final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate(gridWidget,
                                                                          new Point2D(event.getX(),
                                                                                      event.getY()));

        if (gridWidget.selectCell(ap,
                                  event.isShiftKeyDown(),
                                  event.isControlKeyDown())) {
            gridWidget.getLayer().batch();
        }
    }
}
