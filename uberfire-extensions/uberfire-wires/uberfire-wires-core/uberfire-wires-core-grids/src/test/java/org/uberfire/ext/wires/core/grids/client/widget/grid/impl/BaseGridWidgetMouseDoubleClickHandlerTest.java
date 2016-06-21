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

import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.Command;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseGridWidgetMouseDoubleClickHandlerTest {

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
    private GridPinnedModeManager pinnedModeManager;

    @Mock
    private GridRenderer renderer;

    @Mock
    private NodeMouseDoubleClickEvent event;

    @Mock
    private GridData uiModel;

    @Mock
    private BaseGridRendererHelper helper;

    @Mock
    private GridColumn<String> uiColumn;

    @Mock
    private GridRow uiRow;

    @Captor
    private ArgumentCaptor<GridBodyCellRenderContext> cellContextArgumentCaptor;

    private BaseGridWidgetMouseDoubleClickHandler handler;

    @Before
    public void setup() {
        when( gridWidget.getViewport() ).thenReturn( viewport );
        when( gridWidget.getModel() ).thenReturn( uiModel );
        when( gridWidget.getRendererHelper() ).thenReturn( helper );
        when( gridWidget.getLayer() ).thenReturn( layer );
        when( gridWidget.getHeader() ).thenReturn( header );
        when( renderer.getHeaderHeight() ).thenReturn( 64.0 );
        when( renderer.getHeaderRowHeight() ).thenReturn( 32.0 );
        when( uiModel.getHeaderRowCount() ).thenReturn( 2 );
        when( uiModel.getColumnCount() ).thenReturn( 1 );
        when( uiModel.getColumns() ).thenReturn( new ArrayList<GridColumn<?>>() {{
            add( uiColumn );
        }} );

        final BaseGridWidgetMouseDoubleClickHandler wrapped = new BaseGridWidgetMouseDoubleClickHandler( gridWidget,
                                                                                                         selectionManager,
                                                                                                         pinnedModeManager,
                                                                                                         renderer );
        handler = spy( wrapped );
    }

    @Test
    public void skipInvisibleGrid() {
        when( gridWidget.isVisible() ).thenReturn( false );

        handler.onNodeMouseDoubleClick( event );

        verify( handler,
                never() ).handleHeaderCellDoubleClick( any( NodeMouseDoubleClickEvent.class ) );
        verify( handler,
                never() ).handleBodyCellDoubleClick( any( NodeMouseDoubleClickEvent.class ) );
        verify( selectionManager,
                never() ).select( eq( gridWidget ) );
    }

    @Test
    public void enterPinnedMode() {
        when( gridWidget.isVisible() ).thenReturn( true );

        when( event.getX() ).thenReturn( 100 );
        when( event.getY() ).thenReturn( 100 );

        when( gridWidget.getLocation() ).thenReturn( new Point2D( 100,
                                                                  100 ) );

        handler.onNodeMouseDoubleClick( event );

        verify( handler,
                times( 1 ) ).handleHeaderCellDoubleClick( any( NodeMouseDoubleClickEvent.class ) );
        verify( handler,
                times( 1 ) ).handleBodyCellDoubleClick( any( NodeMouseDoubleClickEvent.class ) );
        verify( pinnedModeManager,
                times( 1 ) ).enterPinnedMode( eq( gridWidget ),
                                              any( Command.class ) );
        verify( pinnedModeManager,
                never() ).exitPinnedMode( any( Command.class ) );
    }

    @Test
    public void exitPinnedMode() {
        when( gridWidget.isVisible() ).thenReturn( true );
        when( pinnedModeManager.isGridPinned() ).thenReturn( true );

        when( event.getX() ).thenReturn( 100 );
        when( event.getY() ).thenReturn( 100 );

        when( gridWidget.getLocation() ).thenReturn( new Point2D( 100,
                                                                  100 ) );

        handler.onNodeMouseDoubleClick( event );

        verify( handler,
                times( 1 ) ).handleHeaderCellDoubleClick( any( NodeMouseDoubleClickEvent.class ) );
        verify( handler,
                times( 1 ) ).handleBodyCellDoubleClick( any( NodeMouseDoubleClickEvent.class ) );
        verify( pinnedModeManager,
                never() ).enterPinnedMode( any( GridWidget.class ),
                                           any( Command.class ) );
        verify( pinnedModeManager,
                times( 1 ) ).exitPinnedMode( any( Command.class ) );
    }

    @Test
    public void basicCheckForBodyHandler() {
        when( gridWidget.isVisible() ).thenReturn( true );

        when( event.getX() ).thenReturn( 100 );
        when( event.getY() ).thenReturn( 200 );

        when( gridWidget.getLocation() ).thenReturn( new Point2D( 100,
                                                                  100 ) );
        when( gridWidget.getHeight() ).thenReturn( 200.0 );

        final BaseGridRendererHelper.ColumnInformation ci = new BaseGridRendererHelper.ColumnInformation( uiColumn,
                                                                                                          0,
                                                                                                          0 );
        when( helper.getColumnInformation( any( Double.class ) ) ).thenReturn( ci );

        handler.onNodeMouseDoubleClick( event );

        verify( handler,
                times( 1 ) ).handleHeaderCellDoubleClick( any( NodeMouseDoubleClickEvent.class ) );
        verify( handler,
                times( 1 ) ).handleBodyCellDoubleClick( any( NodeMouseDoubleClickEvent.class ) );
    }

    @Test
    public void editColumnBodyHandler() {
        when( gridWidget.isVisible() ).thenReturn( true );

        when( event.getX() ).thenReturn( 100 );
        when( event.getY() ).thenReturn( 200 );

        when( gridWidget.getLocation() ).thenReturn( new Point2D( 100,
                                                                  100 ) );
        when( gridWidget.getHeight() ).thenReturn( 200.0 );
        when( uiModel.getRowCount() ).thenReturn( 1 );
        when( uiModel.getRow( eq( 0 ) ) ).thenReturn( uiRow );
        when( uiRow.getHeight() ).thenReturn( 64.0 );

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

        handler.onNodeMouseDoubleClick( event );

        verify( handler,
                times( 1 ) ).handleHeaderCellDoubleClick( any( NodeMouseDoubleClickEvent.class ) );
        verify( handler,
                times( 1 ) ).handleBodyCellDoubleClick( any( NodeMouseDoubleClickEvent.class ) );
        verify( handler,
                times( 1 ) ).onDoubleClick( cellContextArgumentCaptor.capture() );

        final GridBodyCellRenderContext cellContext = cellContextArgumentCaptor.getValue();
        assertNotNull( cellContext );
    }

}
