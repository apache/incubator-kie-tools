/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.grid.handlers;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.event.AbstractNodeHumanInputEvent;
import com.ait.lienzo.client.core.types.Point2D;
import org.kie.workbench.common.dmn.client.editors.expressions.util.DynamicReadOnlyUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.DefaultGridWidgetEditCellMouseEventHandler;

public class DelegatingGridWidgetEditCellMouseEventHandler extends DefaultGridWidgetEditCellMouseEventHandler {

    private final Supplier<GridCellTuple> parentSupplier;
    private final Supplier<Integer> nestingSupplier;

    public DelegatingGridWidgetEditCellMouseEventHandler(final Supplier<GridCellTuple> parentSupplier,
                                                         final Supplier<Integer> nestingSupplier) {
        this.parentSupplier = parentSupplier;
        this.nestingSupplier = nestingSupplier;
    }

    @Override
    public boolean onNodeMouseEvent(final GridWidget gridWidget,
                                    final Point2D relativeLocation,
                                    final Optional<Integer> uiHeaderRowIndex,
                                    final Optional<Integer> uiHeaderColumnIndex,
                                    final Optional<Integer> uiRowIndex,
                                    final Optional<Integer> uiColumnIndex,
                                    final AbstractNodeHumanInputEvent event) {
        if (DynamicReadOnlyUtils.isOnlyVisualChangeAllowed(gridWidget)) {
            return false;
        }

        if (nestingSupplier.get() == 0) {
            return doSuperOnNodeMouseEvent(gridWidget,
                                           relativeLocation,
                                           uiHeaderRowIndex,
                                           uiHeaderColumnIndex,
                                           uiRowIndex,
                                           uiColumnIndex,
                                           event);
        }

        boolean isHandled = false;
        if (uiRowIndex.isPresent() && uiColumnIndex.isPresent()) {
            isHandled = delegatedHandleBodyCell(gridWidget,
                                                relativeLocation,
                                                uiRowIndex.get(),
                                                uiColumnIndex.get(),
                                                event);
        }

        return isHandled;
    }

    boolean doSuperOnNodeMouseEvent(final GridWidget gridWidget,
                                    final Point2D relativeLocation,
                                    final Optional<Integer> uiHeaderRowIndex,
                                    final Optional<Integer> uiHeaderColumnIndex,
                                    final Optional<Integer> uiRowIndex,
                                    final Optional<Integer> uiColumnIndex,
                                    final AbstractNodeHumanInputEvent event) {
        return super.onNodeMouseEvent(gridWidget,
                                      relativeLocation,
                                      uiHeaderRowIndex,
                                      uiHeaderColumnIndex,
                                      uiRowIndex,
                                      uiColumnIndex,
                                      event);
    }

    private boolean delegatedHandleBodyCell(final GridWidget gridWidget,
                                            final Point2D relativeLocation,
                                            final int uiRowIndex,
                                            final int uiColumnIndex,
                                            final AbstractNodeHumanInputEvent event) {
        final GridCellTuple parent = parentSupplier.get();
        final GridWidget parentGridWidget = parent.getGridWidget();
        final GridData parentGridData = parentGridWidget.getModel();
        if (parentGridData.getSelectedCells().size() == 1) {
            final GridData gridData = gridWidget.getModel();
            final GridCell<?> cell = gridData.getCell(uiRowIndex, uiColumnIndex);
            final GridCellEditAction cellEditAction = cell == null ? GridCell.DEFAULT_EDIT_ACTION : cell.getSupportedEditAction();
            if (isEventHandled(cellEditAction, event)) {
                return gridWidget.startEditingCell(relativeLocation);
            }
        }
        return false;
    }

    boolean isEventHandled(final GridCellEditAction cellEditAction,
                           final AbstractNodeHumanInputEvent event) {
        return Objects.equals(cellEditAction, GridCellEditAction.getSupportedEditAction(event));
    }
}
