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

package org.kie.workbench.common.dmn.client.widgets.panel;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNGridPanelContextMenuHandlerTest {

    private static final double COLUMN0_WIDTH = 50.0;

    private static final double COLUMN1_WIDTH = 100.0;

    private static final double HEADER_HEIGHT = 20.0;

    private static final double ROW_HEIGHT = 20.0;

    @Mock
    private ContextMenuEvent event;

    @Mock
    private NativeEvent nativeEvent;

    @Mock
    private Element element;

    @Mock
    private Document document;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private GridRenderer renderer;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private DMNGridPanelCellSelectionHandler cellSelectionHandler;

    @Mock
    private HasCellEditorControls.Editor editor;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private TranslationService translationService;

    @Mock
    private GridColumn gridColumn;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

    private DMNGridPanelContextMenuHandler handler;

    private interface MockCell extends GridCell,
                                       HasCellEditorControls {

    }

    private interface MockContextMenuHeaderMetaData extends GridColumn.HeaderMetaData,
                                                            HasCellEditorControls,
                                                            HasListSelectorControl {

    }

    @Before
    public void setup() {
        this.handler = new DMNGridPanelContextMenuHandler(gridLayer,
                                                          cellEditorControls,
                                                          cellSelectionHandler);

        when(event.getNativeEvent()).thenReturn(nativeEvent);
        when(event.getRelativeElement()).thenReturn(element);
        when(element.getAbsoluteLeft()).thenReturn(0);
        when(element.getScrollLeft()).thenReturn(0);
        when(element.getAbsoluteTop()).thenReturn(0);
        when(element.getScrollTop()).thenReturn(0);
        when(element.getOwnerDocument()).thenReturn(document);
        when(document.getScrollLeft()).thenReturn(0);
        when(document.getScrollTop()).thenReturn(0);

        when(nativeEvent.getShiftKey()).thenReturn(false);
        when(nativeEvent.getCtrlKey()).thenReturn(false);

        when(gridColumn.getWidth()).thenReturn(100.0);
        when(gridColumn.isVisible()).thenReturn(true);

        when(renderer.getHeaderHeight()).thenReturn(HEADER_HEIGHT);
        when(renderer.getHeaderRowHeight()).thenReturn(HEADER_HEIGHT);

        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, 1000, 1000));
    }

    @Test
    public void onContextMenu_NoGridWidgets() {
        handler.onContextMenu(event);

        verify(cellEditorControls, never()).show(any(HasCellEditorControls.Editor.class),
                                                 anyInt(),
                                                 anyInt());
    }

    @Test
    public void onContextMenu_WithGridWidget_EventOutsideGridBounds() {
        when(nativeEvent.getClientX()).thenReturn((int) (COLUMN0_WIDTH + COLUMN1_WIDTH + 50));
        when(nativeEvent.getClientY()).thenReturn((int) ROW_HEIGHT + 50);

        final BaseGrid gridWidget = mockGridWidget();
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));

        handler.onContextMenu(event);

        verify(cellEditorControls, never()).show(any(HasCellEditorControls.Editor.class),
                                                 anyInt(),
                                                 anyInt());
    }

    @Test
    public void onContextMenu_WithGridWidget_WithNullCell() {
        when(nativeEvent.getClientX()).thenReturn((int) (COLUMN0_WIDTH / 2));
        when(nativeEvent.getClientY()).thenReturn((int) (ROW_HEIGHT + ROW_HEIGHT / 2));

        final BaseGrid gridWidget = mockGridWidget();
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));

        handler.onContextMenu(event);

        verify(cellEditorControls, never()).show(any(HasCellEditorControls.Editor.class),
                                                 anyInt(),
                                                 anyInt());
    }

    @Test
    public void onContextMenu_WithGridWidget_WithCellValueOfWrongType() {
        when(nativeEvent.getClientX()).thenReturn((int) (COLUMN0_WIDTH / 2));
        when(nativeEvent.getClientY()).thenReturn((int) (ROW_HEIGHT + ROW_HEIGHT / 2));

        final BaseGrid gridWidget = mockGridWidget();
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));

        gridWidget.getModel().setCellValue(1, 0, new ExpressionCellValue(Optional.empty()));

        handler.onContextMenu(event);

        verify(cellEditorControls, never()).show(any(HasCellEditorControls.Editor.class),
                                                 anyInt(),
                                                 anyInt());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onContextMenu_WithGridWidget_WithCellValue() {
        final int EVENT_X = (int) (COLUMN0_WIDTH / 2);
        final int EVENT_Y = (int) (HEADER_HEIGHT + ROW_HEIGHT + ROW_HEIGHT / 2);
        when(nativeEvent.getClientX()).thenReturn(EVENT_X);
        when(nativeEvent.getClientY()).thenReturn(EVENT_Y);

        final BaseGrid gridWidget = mockGridWidget();
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));

        final MockCell cell = mock(MockCell.class);
        gridWidget.getModel().setCell(1, 0, () -> cell);
        when(cell.getEditor()).thenReturn(Optional.of(editor));

        handler.onContextMenu(event);

        verify(editor).bind(eq(gridWidget),
                            eq(1),
                            eq(0));

        verify(cellEditorControls).show(eq(editor),
                                        eq(EVENT_X),
                                        eq(EVENT_Y));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onContextMenu_WithGridWidget_WithHeader() {
        final int EVENT_X = (int) (COLUMN0_WIDTH + COLUMN1_WIDTH / 2);
        final int EVENT_Y = (int) (HEADER_HEIGHT / 2);
        when(nativeEvent.getClientX()).thenReturn(EVENT_X);
        when(nativeEvent.getClientY()).thenReturn(EVENT_Y);

        final BaseGrid gridWidget = mockGridWidget();
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));

        final MockContextMenuHeaderMetaData headerMetaData = mock(MockContextMenuHeaderMetaData.class);
        when(gridColumn.getHeaderMetaData()).thenReturn(Collections.singletonList(headerMetaData));
        when(headerMetaData.getEditor()).thenReturn(Optional.of(editor));

        handler.onContextMenu(event);

        verify(editor).bind(eq(headerMetaData),
                            eq(0),
                            eq(1));

        verify(cellEditorControls).show(eq(editor),
                                        eq(EVENT_X),
                                        eq(EVENT_Y));
    }

    @Test
    public void onContextMenu_WithGridWidget_WithCellValue_WithOnlyVisualChangeAllowed() {
        final int EVENT_X = (int) (COLUMN0_WIDTH / 2);
        final int EVENT_Y = (int) (ROW_HEIGHT + ROW_HEIGHT / 2);
        when(nativeEvent.getClientX()).thenReturn(EVENT_X);
        when(nativeEvent.getClientY()).thenReturn(EVENT_Y);

        final BaseGrid gridWidget = mockGridWidget();
        doReturn(true).when(gridWidget).isOnlyVisualChangeAllowed();
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));

        handler.onContextMenu(event);

        verify(cellEditorControls, never()).show(any(HasCellEditorControls.Editor.class),
                                                 anyInt(),
                                                 anyInt());
    }

    @Test
    public void onContextMenu_WithGridWidget_WithCellSelectionStrategy_CellNotSelected() {
        when(nativeEvent.getClientX()).thenReturn((int) (COLUMN0_WIDTH / 2));
        when(nativeEvent.getClientY()).thenReturn((int) (HEADER_HEIGHT + ROW_HEIGHT + ROW_HEIGHT / 2));

        final BaseGrid gridWidget = mockGridWidget();
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));

        final MockCell cell = mock(MockCell.class);
        final CellSelectionStrategy selectionStrategy = mock(CellSelectionStrategy.class);
        gridWidget.getModel().setCell(1, 0, () -> cell);
        when(cell.getEditor()).thenReturn(Optional.of(editor));
        when(cell.getSelectionStrategy()).thenReturn(selectionStrategy);

        handler.onContextMenu(event);

        verify(cellSelectionHandler).selectCellIfRequired(eq(1),
                                                          eq(0),
                                                          eq(gridWidget),
                                                          eq(false),
                                                          eq(false));
    }

    @Test
    public void onContextMenu_WithGridWidget_WithCellSelectionStrategy_CellAlreadySelected() {
        when(nativeEvent.getClientX()).thenReturn((int) (COLUMN0_WIDTH / 2));
        when(nativeEvent.getClientY()).thenReturn((int) (ROW_HEIGHT + ROW_HEIGHT / 2));

        final BaseGrid gridWidget = mockGridWidget();
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(gridWidget));
        gridWidget.selectCell(1, 0, false, false);

        final MockCell cell = mock(MockCell.class);
        final CellSelectionStrategy selectionStrategy = mock(CellSelectionStrategy.class);
        gridWidget.getModel().setCell(1, 0, () -> cell);
        when(cell.getEditor()).thenReturn(Optional.of(editor));
        when(cell.getSelectionStrategy()).thenReturn(selectionStrategy);

        handler.onContextMenu(event);

        verify(selectionStrategy, never()).handleSelection(any(GridData.class),
                                                           anyInt(),
                                                           anyInt(),
                                                           anyBoolean(),
                                                           anyBoolean());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onContextMenu_WithMultipleOverlappingGridWidgets() {
        final int EVENT_X = (int) (COLUMN0_WIDTH / 2);
        final int EVENT_Y = (int) (HEADER_HEIGHT + ROW_HEIGHT + ROW_HEIGHT / 2);
        when(nativeEvent.getClientX()).thenReturn(EVENT_X);
        when(nativeEvent.getClientY()).thenReturn(EVENT_Y);

        final BaseGrid gridWidget1 = mockGridWidget();
        final BaseGrid gridWidget2 = mockGridWidget();
        // Without stubbing mocks to death this requires some knowledge of the internals of
        // DefaultGridLayer that maintains a LinkedHashSet of GridWidgets added to the Layer.
        // LinkedHashSet returns items in the order in which they were added.
        final Set<GridWidget> gridWidgets = new LinkedHashSet<>();
        gridWidgets.add(gridWidget1);
        gridWidgets.add(gridWidget2);
        when(gridLayer.getGridWidgets()).thenReturn(gridWidgets);

        final MockCell cell1 = mock(MockCell.class);
        gridWidget1.getModel().setCell(1, 0, () -> cell1);
        when(cell1.getEditor()).thenReturn(Optional.of(editor));

        final MockCell cell2 = mock(MockCell.class);
        gridWidget2.getModel().setCell(1, 0, () -> cell2);
        when(cell2.getEditor()).thenReturn(Optional.of(editor));

        handler.onContextMenu(event);

        // gridWidget2 was added second and is therefore considered "on top of" gridWidget1
        verify(editor).bind(eq(gridWidget2),
                            eq(1),
                            eq(0));

        verify(cellEditorControls).show(eq(editor),
                                        eq(EVENT_X),
                                        eq(EVENT_Y));
    }

    private BaseGrid mockGridWidget() {
        final BaseGrid gridWidget = spy(new BaseGrid<Expression>(gridLayer,
                                                                 new BaseGridData(false),
                                                                 renderer,
                                                                 sessionManager,
                                                                 sessionCommandManager,
                                                                 canvasCommandFactory,
                                                                 refreshFormPropertiesEvent,
                                                                 domainObjectSelectionEvent,
                                                                 cellEditorControls,
                                                                 translationService) {
            @Override
            public Layer getLayer() {
                return gridLayer;
            }
        });
        gridWidget.getModel().appendColumn(new RowNumberColumn());
        gridWidget.getModel().appendColumn(gridColumn);
        gridWidget.getModel().appendRow(new BaseGridRow());
        gridWidget.getModel().appendRow(new BaseGridRow());

        return gridWidget;
    }
}
