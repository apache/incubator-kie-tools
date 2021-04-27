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
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;

/**
 * A {@link NodeMouseEventHandler} to handle selection of cells.
 */
public class DefaultGridWidgetCellSelectorMouseEventHandler implements NodeMouseEventHandler {

    protected GridSelectionManager selectionManager;

    public DefaultGridWidgetCellSelectorMouseEventHandler(final GridSelectionManager selectionManager) {
        this.selectionManager = selectionManager;
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
        if (!isHandled && uiRowIndex.isPresent() && uiColumnIndex.isPresent()) {
            isHandled = handleBodyCell(gridWidget,
                                       relativeLocation,
                                       uiRowIndex.get(),
                                       uiColumnIndex.get(),
                                       event);
        }
        if (isHandled) {
            if (!gridWidget.isSelected()) {
                selectionManager.select(gridWidget);
            }
        }

        return isHandled;
    }

    /**
     * Select header cells.
     */
    @Override
    public boolean handleHeaderCell(final GridWidget gridWidget,
                                    final Point2D relativeLocation,
                                    final int uiHeaderRowIndex,
                                    final int uiHeaderColumnIndex,
                                    final AbstractNodeMouseEvent event) {
        final boolean isHandled = gridWidget.selectHeaderCell(relativeLocation,
                                                              event.isShiftKeyDown(),
                                                              event.isControlKeyDown());
        if (isHandled) {
            gridWidget.getLayer().batch();
        }
        return isHandled;
    }

    /**
     * Select body cells.
     */
    @Override
    public boolean handleBodyCell(final GridWidget gridWidget,
                                  final Point2D relativeLocation,
                                  final int uiRowIndex,
                                  final int uiColumnIndex,
                                  final AbstractNodeMouseEvent event) {
        final boolean isHandled = gridWidget.selectCell(relativeLocation,
                                                        event.isShiftKeyDown(),
                                                        event.isControlKeyDown());

        if (isHandled) {
            gridWidget.getLayer().batch();
        }
        return isHandled;
    }
}
