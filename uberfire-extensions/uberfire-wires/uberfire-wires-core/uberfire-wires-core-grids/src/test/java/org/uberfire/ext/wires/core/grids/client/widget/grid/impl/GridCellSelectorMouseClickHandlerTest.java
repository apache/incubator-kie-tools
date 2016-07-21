/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;

import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class GridCellSelectorMouseClickHandlerTest {

    @Mock
    private GridWidget gridWidget;

    @Mock
    private Group header;

    @Mock
    private Viewport viewport;

    @Mock
    private DefaultGridLayer layer;

    @Mock
    private GridSelectionManager selectionManager;

    @Mock
    private GridRenderer renderer;

    @Mock
    private NodeMouseClickEvent event;

    @Mock
    private GridData uiModel;

    @Mock
    private BaseGridRendererHelper helper;

    @Mock
    private GridColumn<String> uiColumn;

    @Mock
    private GridRow uiRow;

    @Mock
    private GridCell uiCell;

    @Mock
    private CellSelectionStrategy cellSelectionStrategy;

    private GridCellSelectorMouseClickHandler handler;

    @Before
    public void setup() {
        when( gridWidget.getViewport() ).thenReturn( viewport );
        when( gridWidget.getModel() ).thenReturn( uiModel );
        when( gridWidget.getRenderer() ).thenReturn( renderer );
        when( gridWidget.getRendererHelper() ).thenReturn( helper );
        when( gridWidget.getLayer() ).thenReturn( layer );
        when( gridWidget.getHeader() ).thenReturn( header );
        when( gridWidget.getHeight() ).thenReturn( 128.0 );
        when( gridWidget.getLocation() ).thenReturn( new Point2D( 100,
                                                                  100 ) );
        when( renderer.getHeaderHeight() ).thenReturn( 64.0 );
        when( renderer.getHeaderRowHeight() ).thenReturn( 32.0 );
        when( uiModel.getHeaderRowCount() ).thenReturn( 2 );
        when( uiModel.getColumnCount() ).thenReturn( 1 );
        when( uiModel.getColumns() ).thenReturn( new ArrayList<GridColumn<?>>() {{
            add( uiColumn );
        }} );
        when( uiModel.getRowCount() ).thenReturn( 1 );
        when( uiModel.getRow( eq( 0 ) ) ).thenReturn( uiRow );
        when( uiRow.getHeight() ).thenReturn( 64.0 );
        when( uiCell.getSelectionManager() ).thenReturn( cellSelectionStrategy );

        final GridCellSelectorMouseClickHandler wrapped = new GridCellSelectorMouseClickHandler( gridWidget,
                                                                                                 selectionManager,
                                                                                                 renderer );
        handler = spy( wrapped );
    }

    @Test
    public void skipInvisibleGrid() {
        when( gridWidget.isVisible() ).thenReturn( false );

        handler.onNodeMouseClick( event );

        verify( handler,
                never() ).handleBodyCellClick( any( NodeMouseClickEvent.class ) );
    }

    @Test
    public void basicCheckForBodyHandlerWithinBodyBounds() {
        when( gridWidget.isVisible() ).thenReturn( true );

        when( event.getX() ).thenReturn( 100 );
        when( event.getY() ).thenReturn( 200 );

        final BaseGridRendererHelper.ColumnInformation ci = new BaseGridRendererHelper.ColumnInformation( uiColumn,
                                                                                                          0,
                                                                                                          0 );
        when( helper.getColumnInformation( any( Double.class ) ) ).thenReturn( ci );

        handler.onNodeMouseClick( event );

        verify( handler,
                times( 1 ) ).handleBodyCellClick( any( NodeMouseClickEvent.class ) );
        verify( gridWidget,
                times( 1 ) ).selectCell( any( Point2D.class ),
                                         eq( false ),
                                         eq( false ) );
    }

    @Test
    public void basicCheckForBodyHandlerOutsideBodyBounds() {
        when( gridWidget.isVisible() ).thenReturn( true );

        when( event.getX() ).thenReturn( 100 );
        when( event.getY() ).thenReturn( 120 );

        final BaseGridRendererHelper.ColumnInformation ci = new BaseGridRendererHelper.ColumnInformation( uiColumn,
                                                                                                          0,
                                                                                                          0 );
        when( helper.getColumnInformation( any( Double.class ) ) ).thenReturn( ci );

        handler.onNodeMouseClick( event );

        verify( handler,
                times( 1 ) ).handleBodyCellClick( any( NodeMouseClickEvent.class ) );
        verify( uiModel,
                never() ).getCell( any( Integer.class ),
                                   any( Integer.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void selectSingleCell() {
        when( gridWidget.isVisible() ).thenReturn( true );

        when( event.getX() ).thenReturn( 100 );
        when( event.getY() ).thenReturn( 200 );

        when( uiModel.getCell( any( Integer.class ),
                               any( Integer.class ) ) ).thenReturn( uiCell );
        final BaseGridRendererHelper.ColumnInformation ci = new BaseGridRendererHelper.ColumnInformation( uiColumn,
                                                                                                          0,
                                                                                                          0 );
        when( helper.getColumnInformation( any( Double.class ) ) ).thenReturn( ci );

        final BaseGridRendererHelper.RenderingInformation ri = new BaseGridRendererHelper.RenderingInformation( mock( Bounds.class ),
                                                                                                                new ArrayList<GridColumn<?>>() {{
                                                                                                                    add( uiColumn );
                                                                                                                }},
                                                                                                                mock( BaseGridRendererHelper.RenderingBlockInformation.class ),
                                                                                                                mock( BaseGridRendererHelper.RenderingBlockInformation.class ),
                                                                                                                0,
                                                                                                                1,
                                                                                                                new ArrayList<Double>() {{
                                                                                                                    add( 64.0 );
                                                                                                                }},
                                                                                                                false,
                                                                                                                false,
                                                                                                                0,
                                                                                                                2,
                                                                                                                0 );
        when( helper.getRenderingInformation() ).thenReturn( ri );

        handler.onNodeMouseClick( event );

        verify( handler,
                times( 1 ) ).handleBodyCellClick( any( NodeMouseClickEvent.class ) );
        verify( gridWidget,
                times( 1 ) ).selectCell( any( Point2D.class ),
                                         eq( false ),
                                         eq( false ) );
    }

}
