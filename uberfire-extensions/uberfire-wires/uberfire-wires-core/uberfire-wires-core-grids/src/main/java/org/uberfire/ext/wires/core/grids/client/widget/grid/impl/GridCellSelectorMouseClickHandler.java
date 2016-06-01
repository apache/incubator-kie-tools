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

import java.util.List;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateTransformationUtils;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.CellRangeSelectionManager;
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

    public GridCellSelectorMouseClickHandler( final GridWidget gridWidget,
                                              final GridSelectionManager selectionManager,
                                              final GridRenderer renderer ) {
        this.gridWidget = gridWidget;
        this.gridModel = gridWidget.getModel();
        this.rendererHelper = gridWidget.getRendererHelper();
        this.selectionManager = selectionManager;
        this.renderer = renderer;
    }

    @Override
    public void onNodeMouseClick( final NodeMouseClickEvent event ) {
        if ( !gridWidget.isVisible() ) {
            return;
        }
        handleBodyCellClick( event );
    }

    /**
     * Select cells.
     * @param event
     */
    protected void handleBodyCellClick( final NodeMouseClickEvent event ) {
        //Convert Canvas co-ordinate to Grid co-ordinate
        final Point2D ap = CoordinateTransformationUtils.convertDOMToGridCoordinate( gridWidget,
                                                                                     new Point2D( event.getX(),
                                                                                                  event.getY() ) );
        final double cx = ap.getX();
        final double cy = ap.getY();

        final Group header = gridWidget.getHeader();
        final double headerMaxY = ( header == null ? renderer.getHeaderHeight() : renderer.getHeaderHeight() + header.getY() );

        if ( cx < 0 || cx > gridWidget.getWidth() ) {
            return;
        }
        if ( cy < headerMaxY || cy > gridWidget.getHeight() ) {
            return;
        }

        //Get row index
        GridRow row;
        int uiRowIndex = 0;
        double offsetY = cy - renderer.getHeaderHeight();
        while ( ( row = gridModel.getRow( uiRowIndex ) ).getHeight() < offsetY ) {
            offsetY = offsetY - row.getHeight();
            uiRowIndex++;
        }
        if ( uiRowIndex < 0 || uiRowIndex > gridModel.getRowCount() - 1 ) {
            return;
        }

        //Get column index
        final List<GridColumn<?>> columns = gridModel.getColumns();
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation( cx );
        final GridColumn<?> column = ci.getColumn();
        final int uiColumnIndex = ci.getUiColumnIndex();

        if ( column == null ) {
            return;
        }
        if ( uiColumnIndex < 0 || uiColumnIndex > columns.size() - 1 ) {
            return;
        }

        //Lookup CellSelectionManager for cell
        CellSelectionManager selectionManager;
        final GridCell<?> cell = gridModel.getCell( uiRowIndex,
                                                    uiColumnIndex );
        if ( cell == null ) {
            selectionManager = CellRangeSelectionManager.INSTANCE;
        } else {
            selectionManager = cell.getSelectionManager();
        }
        if ( selectionManager == null ) {
            return;
        }

        //Handle selection
        if ( selectionManager.handleSelection( event,
                                               uiRowIndex,
                                               uiColumnIndex,
                                               gridModel ) ) {
            gridWidget.getLayer().batch();
        }
    }

}
