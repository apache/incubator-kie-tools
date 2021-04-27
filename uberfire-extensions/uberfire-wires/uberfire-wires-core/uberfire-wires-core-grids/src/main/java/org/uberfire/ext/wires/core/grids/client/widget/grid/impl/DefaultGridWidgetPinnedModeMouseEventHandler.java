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

import java.util.Optional;

import com.ait.lienzo.client.core.event.AbstractNodeMouseEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

/**
 * A {@link NodeMouseEventHandler} to handle entering or exiting of a {@link GridWidget}
 * "pinned" state. If a {@link AbstractNodeMouseEvent} is found to have happened within the {@link GridWidget}
 * Header the "pinned" mode is toggled. See {@link GridPinnedModeManager} for more information.
 */
public class DefaultGridWidgetPinnedModeMouseEventHandler implements NodeMouseEventHandler {

    protected GridPinnedModeManager pinnedModeManager;
    protected GridRenderer renderer;

    public DefaultGridWidgetPinnedModeMouseEventHandler(final GridPinnedModeManager pinnedModeManager,
                                                        final GridRenderer renderer) {
        this.pinnedModeManager = pinnedModeManager;
        this.renderer = renderer;
    }

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

        return isHandled;
    }

    /**
     * Checks if a {@link AbstractNodeMouseEvent} is found to have happened within the {@link GridWidget}
     * Header. If the {@link AbstractNodeMouseEvent} was found to have happened in the {@link GridWidget} Header
     * the "pinned" mode is toggled. See {@link GridPinnedModeManager} for more information.
     */
    @Override
    public boolean handleHeaderCell(final GridWidget gridWidget,
                                    final Point2D relativeLocation,
                                    final int uiHeaderRowIndex,
                                    final int uiHeaderColumnIndex,
                                    final AbstractNodeMouseEvent event) {
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();

        final double cx = relativeLocation.getX();
        final double cy = relativeLocation.getY();

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
}
