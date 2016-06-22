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

import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateTransformationUtils;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
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

    public BaseGridWidgetMouseDoubleClickHandler( final GridWidget gridWidget,
                                                  final GridSelectionManager selectionManager,
                                                  final GridPinnedModeManager pinnedModeManager,
                                                  final GridRenderer renderer ) {
        this.gridWidget = gridWidget;
        this.gridModel = gridWidget.getModel();
        this.rendererHelper = gridWidget.getRendererHelper();
        this.selectionManager = selectionManager;
        this.pinnedModeManager = pinnedModeManager;
        this.renderer = renderer;
    }

    @Override
    public void onNodeMouseDoubleClick( final NodeMouseDoubleClickEvent event ) {
        if ( !gridWidget.isVisible() ) {
            return;
        }
        if ( !handleHeaderCellDoubleClick( event ) ) {
            handleBodyCellDoubleClick( event );
        }
    }

    /**
     * Enters or exits "pinned" mode; where one GridWidget is displayed and is scrollable.
     * @param event
     */
    boolean handleHeaderCellDoubleClick( final NodeMouseDoubleClickEvent event ) {
        //Convert Canvas co-ordinate to Grid co-ordinate
        final Point2D ap = CoordinateTransformationUtils.convertDOMToGridCoordinate( gridWidget,
                                                                                     new Point2D( event.getX(),
                                                                                                  event.getY() ) );
        final double cx = ap.getX();
        final double cy = ap.getY();

        final Group header = gridWidget.getHeader();
        final double headerRowsYOffset = getHeaderRowsYOffset();
        final double headerMinY = ( header == null ? headerRowsYOffset : header.getY() + headerRowsYOffset );
        final double headerMaxY = ( header == null ? renderer.getHeaderHeight() : renderer.getHeaderHeight() + header.getY() );

        if ( cx < 0 || cx > gridWidget.getWidth() ) {
            return false;
        }
        if ( cy < headerMinY || cy > headerMaxY ) {
            return false;
        }

        if ( !pinnedModeManager.isGridPinned() ) {
            pinnedModeManager.enterPinnedMode( gridWidget,
                                               () -> {/*Nothing*/} );

        } else {
            pinnedModeManager.exitPinnedMode( () -> {/*Nothing*/} );
        }

        return true;
    }

    private double getHeaderRowsYOffset() {
        final GridData model = gridWidget.getModel();
        final int headerRowCount = model.getHeaderRowCount();
        final double headerHeight = renderer.getHeaderHeight();
        final double headerRowHeight = renderer.getHeaderRowHeight();
        final double headerRowsHeight = headerRowHeight * headerRowCount;
        final double headerRowsYOffset = headerHeight - headerRowsHeight;

        return headerRowsYOffset;
    }

    /**
     * Check if a MouseDoubleClickEvent happened within a cell and delegate a response
     * to sub-classes {code}doeEdit(){code} method, passing a context object that can
     * be used to determine the cell that was double-clicked.
     * @param event
     */

    boolean handleBodyCellDoubleClick( final NodeMouseDoubleClickEvent event ) {
        //Convert Canvas co-ordinate to Grid co-ordinate
        final Point2D ap = CoordinateTransformationUtils.convertDOMToGridCoordinate( gridWidget,
                                                                                     new Point2D( event.getX(),
                                                                                                  event.getY() ) );
        final double cx = ap.getX();
        final double cy = ap.getY();

        final Group header = gridWidget.getHeader();
        final double headerMaxY = ( header == null ? renderer.getHeaderHeight() : renderer.getHeaderHeight() + header.getY() );

        if ( cx < 0 || cx > gridWidget.getWidth() ) {
            return false;
        }
        if ( cy < headerMaxY || cy > gridWidget.getHeight() ) {
            return false;
        }
        if ( gridModel.getRowCount() == 0 ) {
            return false;
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
            return false;
        }

        //Get column information
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation( cx );
        final GridColumn<?> column = ci.getColumn();
        if ( column == null ) {
            return false;
        }
        final int uiColumnIndex = ci.getUiColumnIndex();
        final List<GridColumn<?>> columns = gridModel.getColumns();
        if ( uiColumnIndex < 0 || uiColumnIndex > columns.size() - 1 ) {
            return false;
        }
        final double offsetX = ci.getOffsetX();

        //Get rendering information
        final BaseGridRendererHelper.RenderingInformation renderingInformation = rendererHelper.getRenderingInformation();
        if ( renderingInformation == null ) {
            return false;
        }

        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final double floatingX = floatingBlockInformation.getX();
        final double floatingWidth = floatingBlockInformation.getWidth();

        //Construct context of MouseEvent
        final double cellX = gridWidget.getX() + offsetX;
        final double cellY = gridWidget.getY() + renderer.getHeaderHeight() + getRowOffset( uiRowIndex,
                                                                                            uiColumnIndex );
        final double cellHeight = getCellHeight( uiRowIndex,
                                                 uiColumnIndex );

        final double clipMinY = gridWidget.getY() + header.getY() + renderer.getHeaderHeight();
        final double clipMinX = gridWidget.getX() + floatingX + floatingWidth;

        final GridBodyCellRenderContext context = new GridBodyCellRenderContext( cellX,
                                                                                 cellY,
                                                                                 column.getWidth(),
                                                                                 cellHeight,
                                                                                 clipMinY,
                                                                                 clipMinX,
                                                                                 uiRowIndex,
                                                                                 uiColumnIndex,
                                                                                 floatingBlockInformation.getColumns().contains( column ),
                                                                                 gridWidget.getViewport().getTransform(),
                                                                                 renderer );

        onDoubleClick( context );

        return true;
    }

    /**
     * Get the y-coordinate of the row relative to the grid. i.e. 0 <= offset <= gridHeight.
     * This may be different to the underlying model's {code}getRowOffset(){code} for merged cells.
     * @param uiRowIndex The index of the row on which the MouseDoubleClickEvent occurred.
     * @param uiColumnIndex The index of the column in which the MouseDoubleClickEvent occurred.
     * @return
     */
    protected double getRowOffset( final int uiRowIndex,
                                   final int uiColumnIndex ) {
        final GridCell<?> cell = gridModel.getCell( uiRowIndex,
                                                    uiColumnIndex );
        if ( cell == null ) {
            return rendererHelper.getRowOffset( uiRowIndex );
        }
        if ( cell.getMergedCellCount() == 1 ) {
            return rendererHelper.getRowOffset( uiRowIndex );
        } else if ( cell.getMergedCellCount() > 1 ) {
            return rendererHelper.getRowOffset( uiRowIndex );
        } else {
            int _uiRowIndex = uiRowIndex;
            GridCell<?> _cell = cell;
            while ( _cell.getMergedCellCount() == 0 ) {
                _uiRowIndex--;
                _cell = gridModel.getCell( _uiRowIndex,
                                           uiColumnIndex );
            }
            return rendererHelper.getRowOffset( _uiRowIndex );
        }
    }

    /**
     * Get the height of a cell. This may be different to the row's height for merged cells.
     * @param uiRowIndex The index of the row on which the MouseDoubleClickEvent occurred.
     * @param uiColumnIndex The index of the column in which the MouseDoubleClickEvent occurred.
     * @return
     */
    protected double getCellHeight( final int uiRowIndex,
                                    final int uiColumnIndex ) {
        final GridCell<?> cell = gridModel.getCell( uiRowIndex,
                                                    uiColumnIndex );
        if ( cell == null ) {
            return gridModel.getRow( uiRowIndex ).getHeight();
        }
        if ( cell.getMergedCellCount() == 1 ) {
            return gridModel.getRow( uiRowIndex ).getHeight();
        } else if ( cell.getMergedCellCount() > 1 ) {
            double height = 0;
            for ( int i = uiRowIndex; i < uiRowIndex + cell.getMergedCellCount(); i++ ) {
                height = height + gridModel.getRow( i ).getHeight();
            }
            return height;
        } else {
            int _uiRowIndex = uiRowIndex;
            GridCell<?> _cell = cell;
            while ( _cell.getMergedCellCount() == 0 ) {
                _uiRowIndex--;
                _cell = gridModel.getCell( _uiRowIndex,
                                           uiColumnIndex );
            }
            double height = 0;
            for ( int i = _uiRowIndex; i < _uiRowIndex + _cell.getMergedCellCount(); i++ ) {
                height = height + gridModel.getRow( i ).getHeight();
            }
            return height;
        }
    }

    /**
     * Signal a MouseDoubleClickEvent has occurred on a cell in the Body.
     * Information regarding the cell, cell's dimensions etc are provided
     * in the render context.
     * @param context
     */
    @SuppressWarnings("unchecked")
    protected void onDoubleClick( final GridBodyCellRenderContext context ) {
        final int uiRowIndex = context.getRowIndex();
        final int uiColumnIndex = context.getColumnIndex();
        final GridCell<?> cell = gridModel.getCell( uiRowIndex,
                                                    uiColumnIndex );
        final GridColumn column = gridModel.getColumns().get( uiColumnIndex );
        column.edit( (GridCell) cell,
                     context,
                     new Callback<GridCellValue<?>>() {

                         @Override
                         public void callback( final GridCellValue<?> value ) {
                             gridModel.setCell( uiRowIndex,
                                                uiColumnIndex,
                                                value );
                             gridWidget.getLayer().batch();
                         }
                     } );
    }

}
