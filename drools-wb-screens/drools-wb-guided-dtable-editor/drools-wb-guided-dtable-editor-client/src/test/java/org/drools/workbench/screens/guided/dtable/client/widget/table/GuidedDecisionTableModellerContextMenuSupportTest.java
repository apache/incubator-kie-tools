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

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.HashSet;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.CellContextMenu;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.RowContextMenu;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class GuidedDecisionTableModellerContextMenuSupportTest {

    @Mock
    private CellContextMenu cellContextMenu;

    @Mock
    private RowContextMenu rowContextMenu;

    @Mock
    private GuidedDecisionTableModellerPresenter modellerPresenter;

    @Mock
    private BaseGridRendererHelper.ColumnInformation columnInformation;

    @Mock
    private ContextMenuEvent event;

    @Mock
    private NativeEvent nativeEvent;

    @Mock
    private Element element;

    @Mock
    private Document document;

    @Mock
    private Viewport viewport;

    @Mock
    private Layer layer;

    private GridData uiModel;

    private GuidedDecisionTableModellerContextMenuSupport contextMenuSupport;

    @Before
    public void setup() {
        this.uiModel = spy( new BaseGridData() );
        this.uiModel.appendRow( new BaseGridRow() );

        this.contextMenuSupport = new GuidedDecisionTableModellerContextMenuSupport( cellContextMenu,
                                                                                     rowContextMenu );

        when( event.getNativeEvent() ).thenReturn( nativeEvent );
        when( event.getRelativeElement() ).thenReturn( element );
        when( element.getAbsoluteLeft() ).thenReturn( 0 );
        when( element.getScrollLeft() ).thenReturn( 0 );
        when( element.getAbsoluteTop() ).thenReturn( 0 );
        when( element.getScrollTop() ).thenReturn( 0 );
        when( element.getOwnerDocument() ).thenReturn( document );
        when( document.getScrollLeft() ).thenReturn( 0 );
        when( document.getScrollTop() ).thenReturn( 0 );

        when( nativeEvent.getShiftKey() ).thenReturn( false );
        when( nativeEvent.getCtrlKey() ).thenReturn( false );
        when( nativeEvent.getClientX() ).thenReturn( 50 );
        when( nativeEvent.getClientY() ).thenReturn( 50 );

        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable();
        when( modellerPresenter.getAvailableDecisionTables() ).thenReturn( new HashSet<GuidedDecisionTableView.Presenter>() {{
            add( dtPresenter );
        }} );
    }

    @Test
    public void getContextMenuHandler() {
        final ContextMenuHandler handler = contextMenuSupport.getContextMenuHandler( modellerPresenter );
        assertNotNull( handler );
    }

    @Test
    public void getContextMenuMouseDownHandler() {
        final MouseDownHandler handler = contextMenuSupport.getContextMenuMouseDownHandler();
        assertNotNull( handler );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onContextMenu_RowContextMenu() {
        final GridColumn uiColumn = new RowNumberColumn();

        uiModel.appendColumn( uiColumn );

        when( columnInformation.getColumn() ).thenReturn( uiColumn );

        final ContextMenuHandler handler = contextMenuSupport.getContextMenuHandler( modellerPresenter );

        handler.onContextMenu( event );

        verify( rowContextMenu,
                times( 1 ) ).show( any( Integer.class ),
                                   any( Integer.class ) );
        verify( cellContextMenu,
                never() ).show( any( Integer.class ),
                                any( Integer.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onContextMenu_CellContextMenu() {
        final GridColumn uiColumn = new BaseGridColumn( mock( GridColumn.HeaderMetaData.class ),
                                                        mock( GridColumnRenderer.class ),
                                                        100.0 );

        uiModel.appendColumn( uiColumn );

        when( columnInformation.getColumn() ).thenReturn( uiColumn );

        final ContextMenuHandler handler = contextMenuSupport.getContextMenuHandler( modellerPresenter );

        handler.onContextMenu( event );

        verify( rowContextMenu,
                never() ).show( any( Integer.class ),
                                any( Integer.class ) );
        verify( cellContextMenu,
                times( 1 ) ).show( any( Integer.class ),
                                   any( Integer.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onContextMenuWithCellSelectionManagerWithChangeInSelection() {
        final GridCell uiCell = mock( GridCell.class );
        final GridColumn uiColumn = new RowNumberColumn();
        final CellSelectionManager cellSelectionManager = mock( CellSelectionManager.class );

        uiModel.appendColumn( uiColumn );

        when( columnInformation.getColumn() ).thenReturn( uiColumn );
        when( uiModel.getCell( any( Integer.class ),
                               any( Integer.class ) ) ).thenReturn( uiCell );
        when( uiCell.getSelectionManager() ).thenReturn( cellSelectionManager );
        when( cellSelectionManager.handleSelection( any( GridData.class ),
                                                    any( Integer.class ),
                                                    any( Integer.class ),
                                                    any( Boolean.class ),
                                                    any( Boolean.class ) ) ).thenReturn( true );

        final ContextMenuHandler handler = contextMenuSupport.getContextMenuHandler( modellerPresenter );

        handler.onContextMenu( event );

        verify( cellSelectionManager,
                times( 1 ) ).handleSelection( eq( uiModel ),
                                              eq( 0 ),
                                              eq( 0 ),
                                              eq( false ),
                                              eq( false ) );
        verify( layer,
                times( 1 ) ).batch();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onContextMenuWithCellSelectionManagerWithoutChangeInSelection() {
        final GridCell uiCell = mock( GridCell.class );
        final GridColumn uiColumn = new RowNumberColumn();
        final CellSelectionManager cellSelectionManager = mock( CellSelectionManager.class );

        uiModel.appendColumn( uiColumn );

        when( columnInformation.getColumn() ).thenReturn( uiColumn );
        when( uiModel.getCell( any( Integer.class ),
                               any( Integer.class ) ) ).thenReturn( uiCell );
        when( uiCell.getSelectionManager() ).thenReturn( cellSelectionManager );

        final ContextMenuHandler handler = contextMenuSupport.getContextMenuHandler( modellerPresenter );

        handler.onContextMenu( event );

        verify( cellSelectionManager,
                times( 1 ) ).handleSelection( eq( uiModel ),
                                              eq( 0 ),
                                              eq( 0 ),
                                              eq( false ),
                                              eq( false ) );
        verify( layer,
                never() ).batch();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onContextMenuWithoutCellSelectionManager() {
        final GridColumn uiColumn = new RowNumberColumn();
        uiModel.appendColumn( uiColumn );

        when( columnInformation.getColumn() ).thenReturn( uiColumn );

        final GridCell uiCell = mock( GridCell.class );
        when( uiModel.getCell( any( Integer.class ),
                               any( Integer.class ) ) ).thenReturn( uiCell );
        when( uiCell.getSelectionManager() ).thenReturn( null );

        final ContextMenuHandler handler = contextMenuSupport.getContextMenuHandler( modellerPresenter );

        handler.onContextMenu( event );

        verify( layer,
                never() ).batch();
    }

    private GuidedDecisionTableView.Presenter makeDecisionTable() {
        final GuidedDecisionTableView.Presenter dtPresenter = mock( GuidedDecisionTableView.Presenter.class );
        final GuidedDecisionTableView dtView = mock( GuidedDecisionTableView.class );
        final GridRenderer renderer = mock( GridRenderer.class );
        final BaseGridRendererHelper helper = mock( BaseGridRendererHelper.class );

        when( dtPresenter.getView() ).thenReturn( dtView );
        when( dtPresenter.getAccess() ).thenReturn( mock( GuidedDecisionTablePresenter.Access.class ) );
        when( dtPresenter.getModel() ).thenReturn( mock( GuidedDecisionTable52.class ) );

        when( dtView.getViewport() ).thenReturn( viewport );
        when( dtView.getLayer() ).thenReturn( layer );
        when( dtView.getLocation() ).thenReturn( new Point2D( 0, 0 ) );
        when( dtView.getWidth() ).thenReturn( 50.0 );
        when( dtView.getHeight() ).thenReturn( 52.0 );
        when( dtView.getModel() ).thenReturn( uiModel );

        when( dtView.getRenderer() ).thenReturn( renderer );
        when( renderer.getHeaderHeight() ).thenReturn( 32.0 );

        when( dtView.getRendererHelper() ).thenReturn( helper );
        when( helper.getColumnInformation( any( Double.class ) ) ).thenReturn( columnInformation );

        return dtPresenter;
    }

}
