/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.GreenTheme;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.DefaultPinnedModeManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CoordinateUtilitiesTest {

    private static final double DEFAULT_ROW_HEIGHT = 20D;
    private static final double COLUMN_WIDTH = 50D;

    protected final int NATIVE_EVENT_CLIENT_X = 100;
    protected final int NATIVE_EVENT_CLIENT_Y = 100;
    protected final int TARGET_ABSOLUTE_LEFT = 50;
    protected final int TARGET_SCROLL_LEFT = 20;
    protected final int TARGET_ABSOLUTE_TOP = 50;
    protected final int TARGET_SCROLL_TOP = 20;
    protected final int DOCUMENT_SCROLL_LEFT = 10;
    protected final int DOCUMENT_SCROLL_TOP = 10;

    private final int EXPECTED_RELATIVE_X = NATIVE_EVENT_CLIENT_X - TARGET_ABSOLUTE_LEFT + TARGET_SCROLL_LEFT + DOCUMENT_SCROLL_LEFT;
    private final int EXPECTED_RELATIVE_Y = NATIVE_EVENT_CLIENT_Y - TARGET_ABSOLUTE_TOP + TARGET_SCROLL_TOP + DOCUMENT_SCROLL_TOP;

    @Mock
    private BaseGridRendererHelper gridRendererHelper;

    @Mock
    private BaseGridRendererHelper.RenderingInformation ri;

    @Mock
    private BaseGridRendererHelper.ColumnInformation ci;

    @Mock
    private Element targetMock;

    @Mock
    private NativeEvent nativeEventMock;

    @Mock
    private ContextMenuEvent contextMenuEventMock;

    @Mock
    private Document documentMock;

    private GridData gridData;

    private GridSelectionManager gridSelectionManager;

    private GridPinnedModeManager gridPinnedModeManager;

    private GridRenderer gridRenderer;

    private GridColumnRenderer gridColumnRenderer;

    private Point2D point;

    private Point2D convertedPoint;

    private GridWidget view;

    @Before
    public void setUp() throws Exception {
        gridData = new BaseGridData();
        gridSelectionManager = new DefaultGridLayer();
        gridPinnedModeManager = new DefaultPinnedModeManager((DefaultGridLayer) gridSelectionManager);
        gridRenderer = new BaseGridRenderer(new GreenTheme());
        gridColumnRenderer = mock(GridColumnRenderer.class);

        when(nativeEventMock.getClientX()).thenReturn(NATIVE_EVENT_CLIENT_X);
        when(nativeEventMock.getClientY()).thenReturn(NATIVE_EVENT_CLIENT_Y);

        when(targetMock.getOwnerDocument()).thenReturn(documentMock);
        when(targetMock.getAbsoluteLeft()).thenReturn(TARGET_ABSOLUTE_LEFT);
        when(targetMock.getScrollLeft()).thenReturn(TARGET_SCROLL_LEFT);
        when(targetMock.getAbsoluteTop()).thenReturn(TARGET_ABSOLUTE_TOP);
        when(targetMock.getScrollTop()).thenReturn(TARGET_SCROLL_TOP);

        when(documentMock.getScrollLeft()).thenReturn(DOCUMENT_SCROLL_LEFT);
        when(documentMock.getScrollTop()).thenReturn(DOCUMENT_SCROLL_TOP);

        when(contextMenuEventMock.getNativeEvent()).thenReturn(nativeEventMock);
        when(contextMenuEventMock.getRelativeElement()).thenReturn(targetMock);
    }

    @Test
    public void testConvertDOMToGridCoordinateNoParent() throws Exception {
        point = new Point2D(15D, 20D);
        setupGridWidget();
        convertedPoint = CoordinateUtilities.convertDOMToGridCoordinate(view, point);
        Assertions.assertThat(convertedPoint).isNotNull();
        Assertions.assertThat(convertedPoint.getX()).isEqualTo(15D);
        Assertions.assertThat(convertedPoint.getY()).isEqualTo(20D);
    }

    @Test
    public void testConvertDOMToGridCoordinateWithParentWithoutTransform() throws Exception {
        point = new Point2D(15D, 20D);
        setupGridWidget();
        when(view.getViewport()).thenReturn(mock(Viewport.class));
        convertedPoint = CoordinateUtilities.convertDOMToGridCoordinate(view, point);
        Assertions.assertThat(convertedPoint).isNotNull();
        Assertions.assertThat(convertedPoint.getX()).isEqualTo(15D);
        Assertions.assertThat(convertedPoint.getY()).isEqualTo(20D);
    }

    @Test
    public void testConvertDOMToGridCoordinateWithParentWithTransformMove() throws Exception {
        final Viewport viewport = mock(Viewport.class);
        final Transform transform = new Transform();
        transform.translate(10D, 10D);
        point = new Point2D(15D, 20D);
        setupGridWidget();
        when(view.getViewport()).thenReturn(viewport);
        when(viewport.getTransform()).thenReturn(transform);
        convertedPoint = CoordinateUtilities.convertDOMToGridCoordinate(view, point);
        Assertions.assertThat(convertedPoint).isNotNull();
        Assertions.assertThat(convertedPoint.getX()).isEqualTo(5D);
        Assertions.assertThat(convertedPoint.getY()).isEqualTo(10D);
    }

    @Test
    public void testConvertDOMToGridCoordinateWithParentWithTransformMoveAndRotate() throws Exception {
        final Viewport viewport = mock(Viewport.class);
        final Transform transform = new Transform();
        transform.translate(10D, 10D);
        transform.rotate(Math.PI);
        point = new Point2D(15D, 20D);
        setupGridWidget();
        when(view.getViewport()).thenReturn(viewport);
        when(viewport.getTransform()).thenReturn(transform);
        convertedPoint = CoordinateUtilities.convertDOMToGridCoordinate(view, point);
        Assertions.assertThat(convertedPoint).isNotNull();
        Assertions.assertThat(Double.valueOf(convertedPoint.getX()).intValue()).isEqualTo(-5);
        Assertions.assertThat(Double.valueOf(convertedPoint.getY()).intValue()).isEqualTo(-10);
    }

    @Test
    public void testGetUiRowIndexOverHeader() throws Exception {
        setupGridWidget();
        final Integer rowIndex = CoordinateUtilities.getUiRowIndex(view, -1);
        Assertions.assertThat(rowIndex).isNull();
    }

    @Test
    public void testGetUiRowIndexInHeader() throws Exception {
        setupGridWidget();
        final Integer rowIndex = CoordinateUtilities.getUiRowIndex(view, gridRenderer.getHeaderHeight() - 1);
        Assertions.assertThat(rowIndex).isNull();
    }

    @Test
    public void testGetUiRowIndexInHeaderInFirstRow() throws Exception {
        // one row has height 20
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        setupGridWidget();
        final Integer rowIndex = CoordinateUtilities.getUiRowIndex(view, gridRenderer.getHeaderHeight() + 1);
        Assertions.assertThat(rowIndex).isEqualTo(0);
    }

    @Test
    public void testGetUiRowIndexInHeaderInSecondRow() throws Exception {
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        setupGridWidget();
        final Integer rowIndex = CoordinateUtilities.getUiRowIndex(view, gridRenderer.getHeaderHeight() + DEFAULT_ROW_HEIGHT + 1);
        Assertions.assertThat(rowIndex).isEqualTo(1);
    }

    @Test
    public void testGetUiRowIndexInHeaderInThirdRow() throws Exception {
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        setupGridWidget();
        final Integer rowIndex = CoordinateUtilities.getUiRowIndex(view, gridRenderer.getHeaderHeight() + (DEFAULT_ROW_HEIGHT * 2) + 1);
        Assertions.assertThat(rowIndex).isEqualTo(2);
    }

    @Test
    public void testGetUiRowIndexInHeaderBelowLastRow() throws Exception {
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        setupGridWidget();
        final Integer rowIndex = CoordinateUtilities.getUiRowIndex(view, gridRenderer.getHeaderHeight() + (DEFAULT_ROW_HEIGHT * 3) + 1);
        Assertions.assertThat(rowIndex).isNull();
    }

    @Test
    public void testGetUiColumnIndexBeforeWidget() throws Exception {
        setupGridWidget();
        final Integer columnIndex = CoordinateUtilities.getUiColumnIndex(view, -1);
        Assertions.assertThat(columnIndex).isNull();
    }

    @Test
    public void testGetUiColumnIndexAfterWidgetHeader() throws Exception {
        setupGridWidget();
        final Integer columnIndex = CoordinateUtilities.getUiColumnIndex(view, view.getWidth() + 1);
        Assertions.assertThat(columnIndex).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetUiColumnIndexInHeaderAfterLastColumn() throws Exception {
        gridData.appendColumn(new BaseGridColumn<>(new BaseHeaderMetaData("first"), gridColumnRenderer, COLUMN_WIDTH));
        gridData.appendColumn(new BaseGridColumn<>(new BaseHeaderMetaData("second"), gridColumnRenderer, COLUMN_WIDTH));
        gridData.appendColumn(new BaseGridColumn<>(new BaseHeaderMetaData("third"), gridColumnRenderer, COLUMN_WIDTH));
        setupGridWidget();
        doReturn(gridSelectionManager).when(view).getLayer();
        final Integer columnIndex = CoordinateUtilities.getUiColumnIndex(view, (COLUMN_WIDTH * 3) + 1);
        Assertions.assertThat(columnIndex).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetUiHeaderRowIndexHeaderMinY() {
        point = new Point2D(COLUMN_WIDTH / 2, -5.0);
        setupGridWidget();

        final GridColumn uiColumn = new BaseGridColumn<>(new BaseHeaderMetaData("first"), gridColumnRenderer, COLUMN_WIDTH);
        gridData.appendColumn(uiColumn);
        doReturn(uiColumn).when(ci).getColumn();

        final Integer uiHeaderRowIndex = CoordinateUtilities.getUiHeaderRowIndex(view,
                                                                                 point);
        assertNull(uiHeaderRowIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetUiHeaderRowIndexHeaderMaxY() {
        point = new Point2D(COLUMN_WIDTH / 2, gridRenderer.getHeaderHeight() + 5.0);
        setupGridWidget();

        final GridColumn uiColumn = new BaseGridColumn<>(new BaseHeaderMetaData("first"), gridColumnRenderer, COLUMN_WIDTH);
        gridData.appendColumn(uiColumn);
        doReturn(uiColumn).when(ci).getColumn();

        final Integer uiHeaderRowIndex = CoordinateUtilities.getUiHeaderRowIndex(view,
                                                                                 point);
        assertNull(uiHeaderRowIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetUiHeaderRowIndexRow0() {
        point = new Point2D(COLUMN_WIDTH / 2, gridRenderer.getHeaderRowHeight() - 5.0);
        setupGridWidget();

        final GridColumn uiColumn = new BaseGridColumn<>(new BaseHeaderMetaData("first"), gridColumnRenderer, COLUMN_WIDTH);
        gridData.appendColumn(uiColumn);
        doReturn(uiColumn).when(ci).getColumn();

        final Integer uiHeaderRowIndex = CoordinateUtilities.getUiHeaderRowIndex(view,
                                                                                 point);
        assertNotNull(uiHeaderRowIndex);
        assertEquals(0,
                     (int) uiHeaderRowIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetUiHeaderRowIndexRow1() {
        point = new Point2D(COLUMN_WIDTH / 2, gridRenderer.getHeaderRowHeight() + 5.0);
        setupGridWidget();

        final List<GridColumn.HeaderMetaData> headerMetaData = new ArrayList<>();
        headerMetaData.add(new BaseHeaderMetaData("first"));
        headerMetaData.add(new BaseHeaderMetaData("second"));
        final GridColumn uiColumn = new BaseGridColumn<>(headerMetaData,
                                                         gridColumnRenderer,
                                                         COLUMN_WIDTH);
        gridData.appendColumn(uiColumn);
        doReturn(uiColumn).when(ci).getColumn();

        final Integer uiHeaderRowIndex = CoordinateUtilities.getUiHeaderRowIndex(view,
                                                                                 point);
        assertNotNull(uiHeaderRowIndex);
        assertEquals(1,
                     (int) uiHeaderRowIndex);
    }

    @Test
    public void testGetRelativeXOfEvent() {
        int retrieved = CoordinateUtilities.getRelativeXOfEvent(contextMenuEventMock);
        assertEquals(EXPECTED_RELATIVE_X, retrieved);
    }

    @Test
    public void testGetRelativeYOfEvent() {
        int retrieved = CoordinateUtilities.getRelativeYOfEvent(contextMenuEventMock);
        assertEquals(EXPECTED_RELATIVE_Y, retrieved);
    }

    @Test
    public void testGetUiHeaderRowIndexOnNoRowsHeader() {
        point = new Point2D(COLUMN_WIDTH / 2, gridRenderer.getHeaderRowHeight() - 5.0);
        setupGridWidget();

        gridData.setHeaderRowCount(0);
        final Integer uiHeaderRowIndex = CoordinateUtilities.getUiHeaderRowIndex(view,
                                                                                 point);
        assertNull(uiHeaderRowIndex);
    }

    private void setupGridWidget() {
        view = spy(new BaseGridWidget(gridData, gridSelectionManager, gridPinnedModeManager, gridRenderer));
        doReturn(gridRenderer).when(view).getRenderer();
        doReturn(gridRendererHelper).when(view).getRendererHelper();
        doReturn(ri).when(gridRendererHelper).getRenderingInformation();
        doReturn(ci).when(gridRendererHelper).getColumnInformation(anyDouble());
        doReturn(mock(Viewport.class)).when(view).getViewport();
        doReturn(0.0).when(ci).getOffsetX();
        doReturn(0).when(ci).getUiColumnIndex();
    }
}
