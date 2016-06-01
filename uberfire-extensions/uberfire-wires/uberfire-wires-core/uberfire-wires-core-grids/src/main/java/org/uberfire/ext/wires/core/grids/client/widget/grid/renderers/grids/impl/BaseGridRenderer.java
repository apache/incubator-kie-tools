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
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl;

import java.util.List;

import com.ait.lienzo.client.core.shape.BoundingBoxPathClipper;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPathClipper;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Transform;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.SelectionsTransformer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

/**
 * A renderer that only renders the visible columns and rows of merged data. This implementation
 * can render the data either in a merged state or non-merged state.
 */
public class BaseGridRenderer implements GridRenderer {

    private static final int HEADER_HEIGHT = 64;

    protected GridRendererTheme theme;

    public BaseGridRenderer( final GridRendererTheme theme ) {
        setTheme( theme );
    }

    @Override
    public double getHeaderHeight() {
        return HEADER_HEIGHT;
    }

    @Override
    public void setTheme( final GridRendererTheme theme ) {
        this.theme = PortablePreconditions.checkNotNull( "theme",
                                                         theme );
    }

    @Override
    public GridRendererTheme getTheme() {
        return theme;
    }

    @Override
    public Group renderSelector( final double width,
                                 final double height ) {
        final Group g = new Group();
        final Rectangle selector = theme.getSelector()
                .setWidth( width )
                .setHeight( height )
                .setListening( false );
        g.add( selector );
        return g;
    }

    @Override
    public Group renderSelectedCells( final GridData model,
                                      final GridBodyRenderContext context,
                                      final BaseGridRendererHelper rendererHelper ) {
        final List<GridColumn<?>> blockColumns = context.getBlockColumns();
        final SelectionsTransformer transformer = context.getTransformer();
        final int minVisibleUiColumnIndex = model.getColumns().indexOf( blockColumns.get( 0 ) );
        final int maxVisibleUiColumnIndex = model.getColumns().indexOf( blockColumns.get( blockColumns.size() - 1 ) );
        final int minVisibleUiRowIndex = context.getMinVisibleRowIndex();
        final int maxVisibleUiRowIndex = context.getMaxVisibleRowIndex();

        //Convert SelectedCells into SelectedRanges, i.e. group them into rectangular ranges
        final List<SelectedRange> selectedRanges = transformer.transformToSelectedRanges();

        final Group g = new Group();
        for ( SelectedRange selectedRange : selectedRanges ) {
            final int rangeOriginUiColumnIndex = selectedRange.getUiColumnIndex();
            final int rangeOriginUiRowIndex = selectedRange.getUiRowIndex();
            final int rangeUiWidth = selectedRange.getWidth();
            final int rangeUiHeight = selectedRange.getHeight();

            //Only render range highlights if they're at least partially visible
            if ( rangeOriginUiColumnIndex + rangeUiWidth - 1 < minVisibleUiColumnIndex ) {
                continue;
            }
            if ( rangeOriginUiColumnIndex > maxVisibleUiColumnIndex ) {
                continue;
            }
            if ( rangeOriginUiRowIndex + rangeUiHeight - 1 < minVisibleUiRowIndex ) {
                continue;
            }
            if ( rangeOriginUiRowIndex > maxVisibleUiRowIndex ) {
                continue;
            }

            final Group cs = renderSelectedRange( model,
                                                  blockColumns,
                                                  minVisibleUiColumnIndex,
                                                  selectedRange );
            if ( cs != null ) {
                final double csx = rendererHelper.getColumnOffset( blockColumns,
                                                                   rangeOriginUiColumnIndex - minVisibleUiColumnIndex );
                final double csy = rendererHelper.getRowOffset( rangeOriginUiRowIndex ) - rendererHelper.getRowOffset( minVisibleUiRowIndex );
                cs.setX( csx )
                        .setY( csy )
                        .setListening( false );
                g.add( cs );
            }
        }
        return g;
    }

    protected Group renderSelectedRange( final GridData model,
                                         final List<GridColumn<?>> blockColumns,
                                         final int minVisibleUiColumnIndex,
                                         final SelectedRange selectedRange ) {
        final Group cellSelector = new Group();
        final double width = getSelectedRangeWidth( blockColumns,
                                                    minVisibleUiColumnIndex,
                                                    selectedRange );
        final double height = getSelectedRangeHeight( model,
                                                      selectedRange );
        final Rectangle selector = theme.getCellSelector()
                .setWidth( width )
                .setHeight( height )
                .setStrokeWidth( 1.0 )
                .setListening( false );

        final Rectangle highlight = theme.getCellSelector()
                .setWidth( width )
                .setHeight( height )
                .setFillColor( selector.getStrokeColor() )
                .setListening( false )
                .setAlpha( 0.25 );

        cellSelector.add( highlight );
        cellSelector.add( selector );

        return cellSelector;
    }

    private double getSelectedRangeWidth( final List<GridColumn<?>> blockColumns,
                                          final int minVisibleUiColumnIndex,
                                          final SelectedRange selectedRange ) {
        double width = 0;
        for ( int columnIndex = 0; columnIndex < selectedRange.getWidth(); columnIndex++ ) {
            final int relativeColumnIndex = columnIndex + selectedRange.getUiColumnIndex() - minVisibleUiColumnIndex;
            width = width + blockColumns.get( relativeColumnIndex ).getWidth();
        }
        return width;
    }

    private double getSelectedRangeHeight( final GridData model,
                                           final SelectedRange selectedRange ) {
        double height = 0;
        for ( int rowIndex = 0; rowIndex < selectedRange.getHeight(); rowIndex++ ) {
            height = height + model.getRow( selectedRange.getUiRowIndex() + rowIndex ).getHeight();
        }
        return height;
    }

    @Override
    public Group renderHeader( final GridData model,
                               final GridHeaderRenderContext context,
                               final BaseGridRendererHelper rendererHelper,
                               final BaseGridRendererHelper.RenderingInformation renderingInformation ) {
        final List<GridColumn<?>> allBlockColumns = context.getAllColumns();
        final List<GridColumn<?>> visibleBlockColumns = context.getBlockColumns();
        final boolean isSelectionLayer = context.isSelectionLayer();
        final double width = rendererHelper.getWidth( visibleBlockColumns );

        final Group g = new Group();
        final Rectangle header = theme.getHeaderBackground()
                .setHeight( getHeaderHeight() )
                .setWidth( width )
                .setListening( true );
        g.add( header );

        //Don't render the Header's detail if we're rendering the SelectionLayer
        if ( isSelectionLayer ) {
            return g;
        }

        //Linked columns
        double x = 0;
        for ( final GridColumn<?> column : visibleBlockColumns ) {
            if ( column.isVisible() ) {
                final double w = column.getWidth();
                if ( column.isLinked() ) {
                    final Rectangle lr = theme.getHeaderLinkBackground()
                            .setWidth( w )
                            .setHeight( getHeaderHeight() )
                            .setX( x );
                    g.add( lr );
                }
                x = x + w;
            }
        }

        //Column title and grid lines
        x = 0;
        for ( final GridColumn<?> column : visibleBlockColumns ) {
            if ( column.isVisible() ) {
                final int columnIndex = visibleBlockColumns.indexOf( column );
                final GridHeaderCellRenderContext headerCellRenderContext = new GridHeaderCellRenderContext( allBlockColumns,
                                                                                                             visibleBlockColumns,
                                                                                                             columnIndex,
                                                                                                             this );
                final Group hc = column.getColumnRenderer().renderHeader( column.getHeaderMetaData(),
                                                                          headerCellRenderContext );
                final double w = column.getWidth();
                hc.setX( x );//.setListening( false );
                g.add( hc );
                x = x + w;
            }
        }

        return g;
    }

    @Override
    public Group renderBody( final GridData model,
                             final GridBodyRenderContext context,
                             final BaseGridRendererHelper rendererHelper,
                             final BaseGridRendererHelper.RenderingInformation renderingInformation ) {
        final double absoluteGridX = context.getAbsoluteGridX();
        final double absoluteGridY = context.getAbsoluteGridY();
        final double absoluteColumnOffsetX = context.getAbsoluteColumnOffsetX();
        final double clipMinY = context.getClipMinY();
        final double clipMinX = context.getClipMinX();
        final int minVisibleRowIndex = context.getMinVisibleRowIndex();
        final int maxVisibleRowIndex = context.getMaxVisibleRowIndex();
        final List<GridColumn<?>> blockColumns = context.getBlockColumns();
        final boolean isSelectionLayer = context.isSelectionLayer();
        final Transform transform = context.getTransform();
        final GridRenderer renderer = context.getRenderer();

        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final List<Double> rowOffsets = renderingInformation.getRowOffsets();

        final double columnHeight = rowOffsets.get( maxVisibleRowIndex - minVisibleRowIndex ) - rowOffsets.get( 0 ) + model.getRow( maxVisibleRowIndex ).getHeight();

        final Group g = new Group();

        double columnX = 0;
        for ( GridColumn<?> column : blockColumns ) {
            if ( column.isVisible() ) {
                final double columnWidth = column.getWidth();
                final double columnRelativeX = rendererHelper.getColumnOffset( blockColumns,
                                                                               blockColumns.indexOf( column ) ) + absoluteColumnOffsetX;
                final boolean isFloating = floatingBlockInformation.getColumns().contains( column );
                final GridBodyColumnRenderContext columnContext = new GridBodyColumnRenderContext( absoluteGridX,
                                                                                                   absoluteGridY,
                                                                                                   absoluteGridX + columnRelativeX,
                                                                                                   clipMinY,
                                                                                                   clipMinX,
                                                                                                   minVisibleRowIndex,
                                                                                                   maxVisibleRowIndex,
                                                                                                   rowOffsets,
                                                                                                   isSelectionLayer,
                                                                                                   isFloating,
                                                                                                   model,
                                                                                                   transform,
                                                                                                   renderer );
                final Group columnGroup = column.getColumnRenderer().renderColumn( column,
                                                                                   columnContext,
                                                                                   rendererHelper );

                //Clip Column Group
                final BoundingBox bb = new BoundingBox( 0,
                                                        0,
                                                        columnWidth,
                                                        columnHeight );
                final IPathClipper clipper = new BoundingBoxPathClipper( bb );
                columnGroup.setX( columnX ).setPathClipper( clipper );
                clipper.setActive( true );

                g.add( columnGroup );

                columnX = columnX + columnWidth;
            }
        }
        return g;
    }

    @Override
    public boolean onGroupingToggle( double cellX,
                                     double cellY,
                                     double cellWidth,
                                     double cellHeight ) {
        return GroupingToggle.onHotSpot( cellX,
                                         cellY,
                                         cellWidth,
                                         cellHeight );
    }

}
