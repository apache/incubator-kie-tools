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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.ait.lienzo.client.core.event.AbstractNodeHumanInputEvent;
import com.ait.lienzo.client.core.types.Point2D;
import org.kie.workbench.common.dmn.client.editors.expressions.util.DynamicReadOnlyUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderUtilities;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.util.CellContextUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.DefaultGridWidgetEditCellMouseEventHandler;

public class EditableHeaderGridWidgetEditCellMouseEventHandler extends DefaultGridWidgetEditCellMouseEventHandler {

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

        return super.onNodeMouseEvent(gridWidget,
                                      relativeLocation,
                                      uiHeaderRowIndex,
                                      uiHeaderColumnIndex,
                                      uiRowIndex,
                                      uiColumnIndex,
                                      event);
    }

    @Override
    public boolean handleHeaderCell(final GridWidget gridWidget,
                                    final Point2D relativeLocation,
                                    final int uiHeaderRowIndex,
                                    final int uiHeaderColumnIndex,
                                    final AbstractNodeHumanInputEvent event) {
        final List<GridColumn<?>> gridColumns = gridWidget.getModel().getColumns();
        final GridColumn<?> gridColumn = gridColumns.get(uiHeaderColumnIndex);
        final List<GridColumn.HeaderMetaData> gridColumnHeaderMetaData = gridColumn.getHeaderMetaData();

        if (Objects.isNull(gridColumnHeaderMetaData) || gridColumnHeaderMetaData.isEmpty()) {
            return false;
        }

        if (!EditableHeaderUtilities.hasEditableHeader(gridColumn)) {
            return false;
        }

        if (!EditableHeaderUtilities.isEditableHeader(gridColumn,
                                                      uiHeaderRowIndex)) {
            return false;
        }

        final EditableHeaderMetaData editableHeaderMetaData = (EditableHeaderMetaData) gridColumn.getHeaderMetaData().get(uiHeaderRowIndex);

        if (Objects.equals(editableHeaderMetaData.getSupportedEditAction(),
                           GridCellEditAction.getSupportedEditAction(event))) {
            final Point2D gridWidgetComputedLocation = gridWidget.getComputedLocation();
            CellContextUtilities.editSelectedCell(gridWidget, relativeLocation.add(gridWidgetComputedLocation));
            return true;
        }

        return false;
    }
}
