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
package org.kie.workbench.common.dmn.client.widgets.dnd;

import java.util.List;
import java.util.Objects;

import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDMouseUpHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public class DMNGridWidgetDnDMouseUpHandler extends GridWidgetDnDMouseUpHandler {

    public DMNGridWidgetDnDMouseUpHandler(final GridLayer layer,
                                          final GridWidgetDnDHandlersState state) {
        super(layer, state);
    }

    @Override
    public void onNodeMouseUp(final NodeMouseUpEvent event) {
        if (state.getOperation() == GridWidgetDnDHandlersState.GridWidgetHandlersOperation.COLUMN_RESIZE) {
            final GridWidget gridWidget = state.getActiveGridWidget();
            final List<GridColumn<?>> gridColumns = state.getActiveGridColumns();
            if (isBaseGrid(gridWidget) && isSingleDMNColumn(gridColumns)) {
                final BaseGrid uiGridWidget = (BaseGrid) gridWidget;
                final DMNGridColumn uiColumn = (DMNGridColumn) gridColumns.get(0);
                uiGridWidget.registerColumnResizeCompleted(uiColumn,
                                                           state.getEventInitialColumnWidth());
            }
        }
        super.onNodeMouseUp(event);
    }

    private boolean isBaseGrid(final GridWidget gridWidget) {
        return gridWidget instanceof BaseGrid;
    }

    private boolean isSingleDMNColumn(final List<GridColumn<?>> gridColumns) {
        if (Objects.isNull(gridColumns) || gridColumns.size() != 1) {
            return false;
        }
        return gridColumns.get(0) instanceof DMNGridColumn;
    }
}
