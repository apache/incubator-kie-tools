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
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl;

import java.util.List;

import com.ait.lienzo.client.core.shape.BoundingBoxPathClipper;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPathClipper;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

public abstract class BaseGridColumnRenderer<T> implements GridColumnRenderer<T> {

    private ColumnRenderingStrategyMerged renderColumnMerged = new ColumnRenderingStrategyMerged();
    private ColumnRenderingStrategyFlattened renderColumnFlattened = new ColumnRenderingStrategyFlattened();

    @Override
    @SuppressWarnings("unchecked")
    public Group renderHeader( final List<GridColumn.HeaderMetaData> headerMetaData,
                               final GridHeaderColumnRenderContext context,
                               final BaseGridRendererHelper.RenderingInformation renderingInformation ) {
        final Group g = new Group();
        final GridData model = context.getModel();
        final GridRenderer renderer = context.getRenderer();
        final GridRendererTheme theme = renderer.getTheme();

        final double headerRowsHeight = renderingInformation.getHeaderRowsHeight();
        final double headerRowsYOffset = renderingInformation.getHeaderRowsYOffset();
        final double rowHeight = headerRowsHeight / headerMetaData.size();

        final List<GridColumn<?>> allBlockColumns = context.getAllColumns();
        final List<GridColumn<?>> visibleBlockColumns = context.getBlockColumns();

        final int headerStartColumnIndex = allBlockColumns.indexOf( visibleBlockColumns.get( 0 ) );
        final int headerColumnIndex = allBlockColumns.indexOf( visibleBlockColumns.get( context.getColumnIndex() ) );
        final GridColumn<?> column = visibleBlockColumns.get( context.getColumnIndex() );

        //Grid lines and content
        final MultiPath headerGrid = theme.getHeaderGridLine().setY( headerRowsYOffset );

        for ( int headerRowIndex = 0; headerRowIndex < headerMetaData.size(); headerRowIndex++ ) {
            //Get extents of block for Header cell
            final int blockStartColumnIndex = getBlockStartColumnIndex( allBlockColumns,
                                                                        headerMetaData.get( headerRowIndex ),
                                                                        headerRowIndex,
                                                                        headerColumnIndex );
            final int blockEndColumnIndex = getBlockEndColumnIndex( allBlockColumns,
                                                                    headerMetaData.get( headerRowIndex ),
                                                                    headerRowIndex,
                                                                    headerColumnIndex );

            //Vertical grid lines
            if ( headerColumnIndex < model.getColumnCount() - 1 ) {
                if ( blockEndColumnIndex == headerColumnIndex ) {
                    final double hx = column.getWidth();
                    headerGrid.M( hx + 0.5,
                                  headerRowIndex * rowHeight ).L( hx + 0.5,
                                                                  ( headerRowIndex + 1 ) * rowHeight );
                }
            }

            //Check whether we need to render clipped cell (we only render once for blocks spanning multiple columns)
            boolean skip;
            if ( blockStartColumnIndex >= headerStartColumnIndex ) {
                skip = headerColumnIndex > blockStartColumnIndex;
            } else {
                skip = headerColumnIndex > headerStartColumnIndex;
            }
            if ( skip ) {
                continue;
            }

            //Get adjustments for the blocked Header cell
            final double offsetX = getBlockOffset( allBlockColumns,
                                                   blockStartColumnIndex,
                                                   headerColumnIndex );
            final double blockWidth = getBlockWidth( allBlockColumns,
                                                     blockStartColumnIndex,
                                                     blockEndColumnIndex );

            final String title = headerMetaData.get( headerRowIndex ).getTitle();
            final Group headerGroup = new Group();
            final Text t = theme.getHeaderText()
                    .setText( title )
                    .setListening( false )
                    .setX( blockWidth / 2 )
                    .setY( rowHeight / 2 );
            headerGroup.add( t );

            //Clip Header Group
            final BoundingBox bb = new BoundingBox( 0,
                                                    0,
                                                    blockWidth,
                                                    rowHeight );
            final IPathClipper clipper = new BoundingBoxPathClipper( bb );
            headerGroup.setX( offsetX ).setY( headerRowsYOffset + headerRowIndex * rowHeight ).setPathClipper( clipper );
            clipper.setActive( true );

            g.add( headerGroup );

            //Horizontal grid lines
            if ( headerRowIndex > 0 ) {
                headerGrid.M( offsetX,
                              ( headerRowIndex * rowHeight ) + 0.5 ).L( offsetX + blockWidth,
                                                                        ( headerRowIndex * rowHeight ) + 0.5 );
            }
        }

        g.add( headerGrid );

        return g;
    }

    @SuppressWarnings("unchecked")
    private int getBlockStartColumnIndex( final List<GridColumn<?>> allColumns,
                                          final GridColumn.HeaderMetaData headerMetaData,
                                          final int headerRowIndex,
                                          final int headerColumnIndex ) {
        //Back-track adding width of proceeding columns sharing header MetaData
        int candidateHeaderColumnIndex = headerColumnIndex;
        if ( candidateHeaderColumnIndex == 0 ) {
            return candidateHeaderColumnIndex;
        }
        while ( candidateHeaderColumnIndex > 0 ) {
            final GridColumn candidateColumn = allColumns.get( candidateHeaderColumnIndex - 1 );
            final List<GridColumn.HeaderMetaData> candidateHeaderMetaData = candidateColumn.getHeaderMetaData();
            if ( candidateHeaderMetaData.size() - 1 < headerRowIndex ) {
                break;
            }
            if ( !candidateHeaderMetaData.get( headerRowIndex ).equals( headerMetaData ) ) {
                break;
            }
            candidateHeaderColumnIndex--;
        }

        return candidateHeaderColumnIndex;
    }

    @SuppressWarnings("unchecked")
    private int getBlockEndColumnIndex( final List<GridColumn<?>> allColumns,
                                        final GridColumn.HeaderMetaData headerMetaData,
                                        final int headerRowIndex,
                                        final int headerColumnIndex ) {
        //Forward-track adding width of following columns sharing header MetaData
        int candidateHeaderColumnIndex = headerColumnIndex;
        if ( candidateHeaderColumnIndex == allColumns.size() - 1 ) {
            return candidateHeaderColumnIndex;
        }
        while ( candidateHeaderColumnIndex < allColumns.size() - 1 ) {
            final GridColumn candidateColumn = allColumns.get( candidateHeaderColumnIndex + 1 );
            final List<GridColumn.HeaderMetaData> candidateHeaderMetaData = candidateColumn.getHeaderMetaData();
            if ( candidateHeaderMetaData.size() - 1 < headerRowIndex ) {
                break;
            }
            if ( !candidateHeaderMetaData.get( headerRowIndex ).equals( headerMetaData ) ) {
                break;
            }
            candidateHeaderColumnIndex++;
        }

        return candidateHeaderColumnIndex;
    }

    private double getBlockOffset( final List<GridColumn<?>> allColumns,
                                   final int blockStartColumnIndex,
                                   final int headerColumnIndex ) {
        double blockOffset = 0;
        for ( int blockColumnIndex = blockStartColumnIndex; blockColumnIndex < headerColumnIndex; blockColumnIndex++ ) {
            final GridColumn column = allColumns.get( blockColumnIndex );
            if ( column.isVisible() ) {
                blockOffset = blockOffset - column.getWidth();
            }

        }
        return blockOffset;
    }

    private double getBlockWidth( final List<GridColumn<?>> allColumns,
                                  final int blockStartColumnIndex,
                                  final int blockEndColumnIndex ) {
        double blockWidth = 0;
        for ( int blockColumnIndex = blockStartColumnIndex; blockColumnIndex <= blockEndColumnIndex; blockColumnIndex++ ) {
            final GridColumn column = allColumns.get( blockColumnIndex );
            if ( column.isVisible() ) {
                blockWidth = blockWidth + column.getWidth();
            }
        }
        return blockWidth;
    }

    @Override
    public Group renderColumn( final GridColumn<?> column,
                               final GridBodyColumnRenderContext context,
                               final BaseGridRendererHelper.RenderingInformation renderingInformation ) {
        if ( context.getModel().isMerged() ) {
            return renderColumnMerged.render( column,
                                              context,
                                              renderingInformation );

        } else {
            return renderColumnFlattened.render( column,
                                                 context,
                                                 renderingInformation );
        }
    }

}
