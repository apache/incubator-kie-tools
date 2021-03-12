/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import com.ait.lienzo.client.core.event.AbstractNodeMouseEvent;
import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.core.client.Callback;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.DefaultGridWidgetEditCellMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

public class GuidedDecisionTableSortGridWidgetMouseEventHandler
        extends DefaultGridWidgetEditCellMouseEventHandler {

    private final Callback<GridColumn, Void> sortCallback;

    public GuidedDecisionTableSortGridWidgetMouseEventHandler(final Callback<GridColumn, Void> sortCallback) {
        this.sortCallback = sortCallback;
    }

    @Override
    public boolean handleHeaderCell(final GridWidget gridWidget,
                                    final Point2D relativeLocation,
                                    final int uiHeaderRowIndex,
                                    final int uiHeaderColumnIndex,
                                    final AbstractNodeMouseEvent event) {
        //Get column information
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(relativeLocation.getX());
        final GridColumn<?> column = ci.getColumn();
        if (column == null) {
            sortCallback.onFailure(null);
            return false;
        }
        sortCallback.onSuccess(column);

        return true;
    }
}
