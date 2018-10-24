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

import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

/**
 * Base MouseDoubleClickHandler to handle double-clicks to either the GridWidgets Header or Body. This
 * implementation supports double-clicking on a cell in the Body and delegating a response to the
 * sub-classes {code}onDoubleClick(){code} method.
 */
public class BaseGridWidgetMouseDoubleClickHandler implements NodeMouseDoubleClickHandler {

    protected GridData gridModel;
    protected GridWidget gridWidget;
    protected BaseGridRendererHelper rendererHelper;
    protected GridSelectionManager selectionManager;
    protected GridPinnedModeManager pinnedModeManager;
    protected GridRenderer renderer;

    public BaseGridWidgetMouseDoubleClickHandler(final GridWidget gridWidget,
                                                 final GridSelectionManager selectionManager,
                                                 final GridPinnedModeManager pinnedModeManager,
                                                 final GridRenderer renderer) {
        this.gridWidget = gridWidget;
        this.gridModel = gridWidget.getModel();
        this.rendererHelper = gridWidget.getRendererHelper();
        this.selectionManager = selectionManager;
        this.pinnedModeManager = pinnedModeManager;
        this.renderer = renderer;
    }

    @Override
    public void onNodeMouseDoubleClick(final NodeMouseDoubleClickEvent event) {
        if (!gridWidget.isVisible()) {
            return;
        }
        if (!handleHeaderCellDoubleClick(event)) {
            handleBodyCellDoubleClick(event);
        }
    }

    /**
     * Enters or exits "pinned" mode; where one GridWidget is displayed and is scrollable.
     * @param event
     */
    protected boolean handleHeaderCellDoubleClick(final NodeMouseDoubleClickEvent event) {
        //Convert Canvas co-ordinate to Grid co-ordinate
        final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate(gridWidget,
                                                                          new Point2D(event.getX(),
                                                                                      event.getY()));
        final double cx = ap.getX();
        final double cy = ap.getY();

        final Group header = gridWidget.getHeader();
        final BaseGridRendererHelper.RenderingInformation ri = rendererHelper.getRenderingInformation();
        final double headerRowsYOffset = ri.getHeaderRowsYOffset();
        final double headerMinY = (header == null ? headerRowsYOffset : header.getY() + headerRowsYOffset);
        final double headerMaxY = (header == null ? renderer.getHeaderHeight() : renderer.getHeaderHeight() + header.getY());

        if (cx < 0 || cx > gridWidget.getWidth()) {
            return false;
        }
        if (cy < headerMinY || cy > headerMaxY) {
            return false;
        }

        if (!pinnedModeManager.isGridPinned()) {
            pinnedModeManager.enterPinnedMode(gridWidget,
                                              () -> {/*Nothing*/});
        } else {
            pinnedModeManager.exitPinnedMode(() -> {/*Nothing*/});
        }

        return true;
    }

    /**
     * Check if a MouseDoubleClickEvent happened within a cell and delegate a response
     * to sub-classes {code}doeEdit(){code} method, passing a context object that can
     * be used to determine the cell that was double-clicked.
     * @param event
     */

    protected boolean handleBodyCellDoubleClick(final NodeMouseDoubleClickEvent event) {
        //Convert Canvas co-ordinate to Grid co-ordinate
        final Point2D rp = CoordinateUtilities.convertDOMToGridCoordinate(gridWidget,
                                                                          new Point2D(event.getX(),
                                                                                      event.getY()));

        return gridWidget.startEditingCell(rp);
    }
}
