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
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;

/**
 * A {@link NodeMouseEventHandler} to handle interaction with a "linked"
 * {@link GridColumn} in the Header and delegating a response to the {@link GridSelectionManager}.
 */
public class DefaultGridWidgetLinkedColumnMouseEventHandler implements NodeMouseEventHandler {

    protected GridSelectionManager selectionManager;
    protected GridRenderer renderer;

    public DefaultGridWidgetLinkedColumnMouseEventHandler(final GridSelectionManager selectionManager,
                                                          final GridRenderer renderer) {
        this.selectionManager = selectionManager;
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
     * Checks if the {@link AbstractNodeMouseEvent} happened on a "linked" {@link GridColumn}. If
     * the {@link AbstractNodeMouseEvent} was found to have happened on a {@link GridWidget} "linked" column then
     * selection of the "linked" {@link GridColumn} is delegated to {@link GridSelectionManager#selectLinkedColumn(GridColumn)}.
     */
    @Override
    public boolean handleHeaderCell(final GridWidget gridWidget,
                                    final Point2D relativeLocation,
                                    final int uiHeaderRowIndex,
                                    final int uiHeaderColumnIndex,
                                    final AbstractNodeMouseEvent event) {
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();

        final double cx = relativeLocation.getX();
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(cx);
        final GridColumn<?> column = ci.getColumn();
        if (column == null) {
            return false;
        }

        //If linked scroll it into view
        if (column.isLinked()) {
            final GridColumn<?> link = column.getLink();
            selectionManager.selectLinkedColumn(link);
            return true;
        }

        return false;
    }
}
