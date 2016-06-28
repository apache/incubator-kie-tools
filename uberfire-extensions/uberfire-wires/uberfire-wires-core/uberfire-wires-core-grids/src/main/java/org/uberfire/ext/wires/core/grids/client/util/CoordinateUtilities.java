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
package org.uberfire.ext.wires.core.grids.client.util;

import java.util.List;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

/**
 * Utilities class
 */
public class CoordinateUtilities {

    /**
     * Convert a DOM-relative coordinate to one within a GridWidget, taking
     * the current transformation (translation and scale) into consideration.
     * @param view The GridWidget to which we need to find the relative coordinate.
     * @param point The Canvas/DOM MouseEvent coordinate.
     * @return A coordinate relative to the GridWidget (in un-transformed coordinate space).
     */
    public static Point2D convertDOMToGridCoordinate( final GridWidget view,
                                                      final Point2D point ) {
        Transform transform = view.getViewport().getTransform();
        if ( transform == null ) {
            view.getViewport().setTransform( transform = new Transform() );
        }

        transform = transform.copy().getInverse();
        final Point2D p = new Point2D( point.getX(),
                                       point.getY() );
        transform.transform( p,
                             p );
        return p.add( view.getLocation().mul( -1.0 ) );
    }

    /**
     * Gets a cell corresponding to the provided Canvas coordinates relative to the grid. Grid-relative coordinates can be
     * obtained from {@link INodeXYEvent} using {@link CoordinateUtilities#convertDOMToGridCoordinate(GridWidget, Point2D)}
     * @param gridWidget GridWidget to check.
     * @param cp coordinates relative to the GridWidget.
     * @return A {@link GridData.SelectedCell} or null if the coordinates did not map to a cell.
     */
    public static GridData.SelectedCell getCell( final GridWidget gridWidget,
                                                 final Point2D cp ) {
        final double cx = cp.getX();
        final double cy = cp.getY();
        final Group header = gridWidget.getHeader();
        final GridData gridModel = gridWidget.getModel();
        final GridRenderer renderer = gridWidget.getRenderer();
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final double headerMaxY = ( header == null ? renderer.getHeaderHeight() : renderer.getHeaderHeight() + header.getY() );

        if ( cx < 0 || cx > gridWidget.getWidth() ) {
            return null;
        }
        if ( cy < headerMaxY || cy > gridWidget.getHeight() ) {
            return null;
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
            return null;
        }

        //Get column index
        final List<GridColumn<?>> columns = gridModel.getColumns();
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation( cx );
        final GridColumn<?> column = ci.getColumn();
        final int uiColumnIndex = ci.getUiColumnIndex();

        if ( column == null ) {
            return null;
        }
        if ( uiColumnIndex < 0 || uiColumnIndex > columns.size() - 1 ) {
            return null;
        }

        return new GridData.SelectedCell( uiRowIndex,
                                          uiColumnIndex );
    }

}
