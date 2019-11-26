/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class CellContextUtilitiesTest {

    private static final double HEADER_HEIGHT = 50.0;
    private static final double HEADER_ROW_HEIGHT = HEADER_HEIGHT / 2;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private GridRenderer gridRenderer;

    @Mock
    private BaseGridRendererHelper.RenderingInformation ri;

    @Mock
    private BaseGridRendererHelper.ColumnInformation ci;

    @Mock
    private BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation;

    @Mock
    private GridColumn.HeaderMetaData headerMetaDataC1;

    @Mock
    private GridColumn.HeaderMetaData headerMetaDataC2;

    @Captor
    private ArgumentCaptor<GridBodyCellEditContext> gridBodyCellEditContextCaptor;

    private Point2D rp = new Point2D(0, 0);
    private Point2D computedLocation = new Point2D(0, 0);
    private BaseGridRendererHelper gridRendererHelper;
    private BaseGridRow row1;
    private BaseGridRow row2;
    private BaseGridRow row3;

    @Before
    public void setup() {
        row1 = new BaseGridRow();
        row2 = new BaseGridRow();
        row3 = new BaseGridRow();

        gridRendererHelper = spy(new BaseGridRendererHelper(gridWidget));

        doReturn(computedLocation).when(gridWidget).getComputedLocation();
        doReturn(gridRenderer).when(gridWidget).getRenderer();
        doReturn(gridRendererHelper).when(gridWidget).getRendererHelper();
        doReturn(ri).when(gridRendererHelper).getRenderingInformation();
        doReturn(HEADER_HEIGHT).when(gridRenderer).getHeaderHeight();
        doReturn(HEADER_ROW_HEIGHT).when(gridRenderer).getHeaderRowHeight();
        doReturn(HEADER_HEIGHT).when(ri).getHeaderRowsHeight();
        doReturn(HEADER_ROW_HEIGHT).when(ri).getHeaderRowHeight();

        doReturn(floatingBlockInformation).when(ri).getFloatingBlockInformation();
        doReturn(0.0).when(floatingBlockInformation).getX();
        doReturn(0.0).when(floatingBlockInformation).getWidth();

        doReturn(mock(Viewport.class)).when(gridWidget).getViewport();

        doReturn(new BaseGridData()).when(gridWidget).getModel();
    }

    @Test
    public void testMakeHeaderRenderContextNoBlockMultipleColumns() {
        final List<GridColumn<?>> allColumns = new ArrayList<>();
        final GridColumn<?> uiColumn1 = mockGridColumn(25.0);
        final GridColumn<?> uiColumn2 = mockGridColumn(50.0);
        final GridColumn<?> uiColumn3 = mockGridColumn(100.0);
        allColumns.add(uiColumn1);
        allColumns.add(uiColumn2);
        allColumns.add(uiColumn3);

        doReturn(allColumns).when(ri).getAllColumns();
        doReturn(uiColumn2).when(ci).getColumn();
        doReturn(25.0).when(ci).getOffsetX();
        doReturn(1).when(ci).getUiColumnIndex();

        final GridBodyCellRenderContext context = CellContextUtilities.makeHeaderCellRenderContext(gridWidget,
                                                                                                   ri,
                                                                                                   ci,
                                                                                                   rp,
                                                                                                   0);

        assertNotNull(context);
        assertEquals(25.0,
                     context.getAbsoluteCellX(),
                     0.0);
        assertEquals(50.0,
                     context.getCellWidth(),
                     0.0);
    }

    @Test
    public void testMakeCellRenderContextOneRow() {
        final List<GridColumn<?>> allColumns = new ArrayList<>();
        final List<Double> allRowHeights = Collections.singletonList(row1.getHeight());
        final GridColumn<?> uiColumn1 = mockGridColumn(25.0);
        final GridColumn<?> uiColumn2 = mockGridColumn(60.0);
        final GridColumn<?> uiColumn3 = mockGridColumn(100.0);
        allColumns.add(uiColumn1);
        allColumns.add(uiColumn2);
        allColumns.add(uiColumn3);
        gridWidget.getModel().appendRow(row1);

        doReturn(allColumns).when(ri).getAllColumns();
        doReturn(allRowHeights).when(ri).getAllRowHeights();
        doReturn(uiColumn2).when(ci).getColumn();
        doReturn(25.0).when(ci).getOffsetX();
        doReturn(1).when(ci).getUiColumnIndex();

        final GridBodyCellEditContext context = CellContextUtilities.makeCellRenderContext(gridWidget,
                                                                                           ri,
                                                                                           ci,
                                                                                           0);

        assertNotNull(context);
        assertThat(context.getAbsoluteCellX())
                .as("Should be column offset")
                .isEqualTo(25.0);

        assertThat(context.getAbsoluteCellY())
                .as("Should be headers height")
                .isEqualTo(HEADER_HEIGHT);
    }

    @Test
    public void testMakeCellRenderContextThreeRows() {
        setupThreeRowGrid();

        final GridBodyCellEditContext context = CellContextUtilities.makeCellRenderContext(gridWidget,
                                                                                           ri,
                                                                                           ci,
                                                                                           2);

        assertNotNull(context);
        assertThat(context.getAbsoluteCellX())
                .as("Should be column offset")
                .isEqualTo(25.0);

        assertThat(context.getAbsoluteCellY())
                .as("Should be sum of header height plus preceding row heights")
                .isEqualTo(HEADER_HEIGHT + row1.getHeight() + row2.getHeight());
    }

    @Test
    public void testMakeCellRenderContextThreeRowsWhenScrolled() {
        setupThreeRowGrid();

        final Point2D gridComputedLocation = new Point2D(0, -row1.getHeight());

        doReturn(gridComputedLocation).when(gridWidget).getComputedLocation();

        final GridBodyCellEditContext context = CellContextUtilities.makeCellRenderContext(gridWidget,
                                                                                           ri,
                                                                                           ci,
                                                                                           2);

        assertNotNull(context);
        assertThat(context.getAbsoluteCellX())
                .as("Should be column offset")
                .isEqualTo(25.0);

        assertThat(context.getAbsoluteCellY())
                .as("Should be sum of header height plus preceding visible row heights")
                .isEqualTo(HEADER_HEIGHT + row2.getHeight());
    }

    private void setupThreeRowGrid() {
        final List<GridColumn<?>> allColumns = new ArrayList<>();
        final List<Double> allRowHeights = Collections.nCopies(3, row1.getHeight());
        final GridColumn<?> uiColumn1 = mockGridColumn(25.0);
        final GridColumn<?> uiColumn2 = mockGridColumn(60.0);
        final GridColumn<?> uiColumn3 = mockGridColumn(100.0);
        allColumns.add(uiColumn1);
        allColumns.add(uiColumn2);
        allColumns.add(uiColumn3);

        gridWidget.getModel().appendRow(row1);
        gridWidget.getModel().appendRow(row2);
        gridWidget.getModel().appendRow(row3);

        doReturn(allColumns).when(ri).getAllColumns();
        doReturn(allRowHeights).when(ri).getAllRowHeights();
        doReturn(uiColumn3).when(ci).getColumn();
        doReturn(25.0).when(ci).getOffsetX();
        doReturn(3).when(ci).getUiColumnIndex();
    }

    @Test
    public void testMakeHeaderRenderContextLeadBlock() {
        final List<GridColumn<?>> allColumns = new ArrayList<>();
        final GridColumn<?> uiColumn1 = mockGridColumn(25.0);
        final GridColumn<?> uiColumn2 = mockGridColumn(50.0,
                                                       uiColumn1.getHeaderMetaData());
        allColumns.add(uiColumn1);
        allColumns.add(uiColumn2);

        doReturn(allColumns).when(ri).getAllColumns();
        doReturn(uiColumn2).when(ci).getColumn();
        doReturn(25.0).when(ci).getOffsetX();
        doReturn(1).when(ci).getUiColumnIndex();

        final GridBodyCellRenderContext context = CellContextUtilities.makeHeaderCellRenderContext(gridWidget,
                                                                                                   ri,
                                                                                                   ci,
                                                                                                   rp,
                                                                                                   0);

        assertNotNull(context);
        assertEquals(25.0,
                     context.getAbsoluteCellX(),
                     0.0);
        assertEquals(75.0,
                     context.getCellWidth(),
                     0.0);
    }

    @Test
    public void testMakeHeaderRenderContextDifferentColumnHeaderMetaDataRows_Column0_Row0() {
        final List<GridColumn<?>> allColumns = setupHeadersWithDifferentColumnHeaderMetaDataRows();

        doReturn(allColumns).when(ri).getAllColumns();
        doReturn(allColumns.get(0)).when(ci).getColumn();
        doReturn(0.0).when(ci).getOffsetX();
        doReturn(0).when(ci).getUiColumnIndex();

        final GridBodyCellRenderContext context = CellContextUtilities.makeHeaderCellRenderContext(gridWidget,
                                                                                                   ri,
                                                                                                   ci,
                                                                                                   rp,
                                                                                                   0);

        assertNotNull(context);
        assertEquals(0.0,
                     context.getAbsoluteCellX(),
                     0.0);
        assertEquals(0.0,
                     context.getAbsoluteCellY(),
                     0.0);
        assertEquals(50.0,
                     context.getCellWidth(),
                     0.0);
        assertEquals(HEADER_ROW_HEIGHT,
                     context.getCellHeight(),
                     0.0);
    }

    @Test
    public void testMakeHeaderRenderContextDifferentColumnHeaderMetaDataRows_Column0_Row1() {
        final List<GridColumn<?>> allColumns = setupHeadersWithDifferentColumnHeaderMetaDataRows();

        doReturn(allColumns).when(ri).getAllColumns();
        doReturn(allColumns.get(0)).when(ci).getColumn();
        doReturn(0.0).when(ci).getOffsetX();
        doReturn(0).when(ci).getUiColumnIndex();

        final GridBodyCellRenderContext context = CellContextUtilities.makeHeaderCellRenderContext(gridWidget,
                                                                                                   ri,
                                                                                                   ci,
                                                                                                   rp,
                                                                                                   1);

        assertNotNull(context);
        assertEquals(0.0,
                     context.getAbsoluteCellX(),
                     0.0);
        assertEquals(25.0,
                     context.getAbsoluteCellY(),
                     0.0);
        assertEquals(50.0,
                     context.getCellWidth(),
                     0.0);
        assertEquals(HEADER_ROW_HEIGHT,
                     context.getCellHeight(),
                     0.0);
    }

    @Test
    public void testMakeHeaderRenderContextDifferentColumnHeaderMetaDataRows_Column1_Row0() {
        final List<GridColumn<?>> allColumns = setupHeadersWithDifferentColumnHeaderMetaDataRows();

        doReturn(allColumns).when(ri).getAllColumns();
        doReturn(allColumns.get(1)).when(ci).getColumn();
        doReturn(50.0).when(ci).getOffsetX();
        doReturn(1).when(ci).getUiColumnIndex();

        final GridBodyCellRenderContext context = CellContextUtilities.makeHeaderCellRenderContext(gridWidget,
                                                                                                   ri,
                                                                                                   ci,
                                                                                                   rp,
                                                                                                   0);

        assertNotNull(context);
        assertEquals(50.0,
                     context.getAbsoluteCellX(),
                     0.0);
        assertEquals(0.0,
                     context.getAbsoluteCellY(),
                     0.0);
        assertEquals(50.0,
                     context.getCellWidth(),
                     0.0);
        assertEquals(HEADER_HEIGHT,
                     context.getCellHeight(),
                     0.0);
    }

    private List<GridColumn<?>> setupHeadersWithDifferentColumnHeaderMetaDataRows() {
        final List<GridColumn<?>> allColumns = new ArrayList<>();
        //Two Header rows
        final GridColumn<?> uiColumn1 = mockGridColumn(50.0);
        //Single Header row
        final GridColumn<?> uiColumn2 = mockGridColumn(50.0,
                                                       Collections.singletonList(mock(GridColumn.HeaderMetaData.class)));
        allColumns.add(uiColumn1);
        allColumns.add(uiColumn2);

        return allColumns;
    }

    @Test
    public void testMakeHeaderRenderContextLeadBlockWithExtraLeadNonBlockColumn() {
        final List<GridColumn<?>> allColumns = new ArrayList<>();
        final GridColumn<?> uiColumn1 = mockGridColumn(25.0);
        final GridColumn<?> uiColumn2 = mockGridColumn(50.0);
        final GridColumn<?> uiColumn3 = mockGridColumn(75.0,
                                                       uiColumn2.getHeaderMetaData());
        allColumns.add(uiColumn1);
        allColumns.add(uiColumn2);
        allColumns.add(uiColumn3);

        doReturn(allColumns).when(ri).getAllColumns();
        doReturn(uiColumn3).when(ci).getColumn();
        doReturn(75.0).when(ci).getOffsetX();
        doReturn(2).when(ci).getUiColumnIndex();

        final GridBodyCellRenderContext context = CellContextUtilities.makeHeaderCellRenderContext(gridWidget,
                                                                                                   ri,
                                                                                                   ci,
                                                                                                   rp,
                                                                                                   0);

        assertNotNull(context);
        assertEquals(75.0,
                     context.getAbsoluteCellX(),
                     0.0);
        assertEquals(125.0,
                     context.getCellWidth(),
                     0.0);
    }

    @Test
    public void testMakeHeaderRenderContextTailBlock() {
        final List<GridColumn<?>> allColumns = new ArrayList<>();
        final GridColumn<?> uiColumn1 = mockGridColumn(25.0);
        final GridColumn<?> uiColumn2 = mockGridColumn(50.0,
                                                       uiColumn1.getHeaderMetaData());
        allColumns.add(uiColumn1);
        allColumns.add(uiColumn2);

        doReturn(allColumns).when(ri).getAllColumns();
        doReturn(uiColumn1).when(ci).getColumn();
        doReturn(0.0).when(ci).getOffsetX();
        doReturn(0).when(ci).getUiColumnIndex();

        final GridBodyCellRenderContext context = CellContextUtilities.makeHeaderCellRenderContext(gridWidget,
                                                                                                   ri,
                                                                                                   ci,
                                                                                                   rp,
                                                                                                   0);

        assertNotNull(context);
        assertEquals(0.0,
                     context.getAbsoluteCellX(),
                     0.0);
        assertEquals(75.0,
                     context.getCellWidth(),
                     0.0);
    }

    @Test
    public void testMakeHeaderRenderContextTailBlockWithExtraTailNonBlockColumn() {
        final List<GridColumn<?>> allColumns = new ArrayList<>();
        final GridColumn<?> uiColumn1 = mockGridColumn(25.0);
        final GridColumn<?> uiColumn2 = mockGridColumn(50.0,
                                                       uiColumn1.getHeaderMetaData());
        final GridColumn<?> uiColumn3 = mockGridColumn(100.0);
        allColumns.add(uiColumn1);
        allColumns.add(uiColumn2);
        allColumns.add(uiColumn3);

        doReturn(allColumns).when(ri).getAllColumns();
        doReturn(uiColumn1).when(ci).getColumn();
        doReturn(0.0).when(ci).getOffsetX();
        doReturn(0).when(ci).getUiColumnIndex();

        final GridBodyCellRenderContext context = CellContextUtilities.makeHeaderCellRenderContext(gridWidget,
                                                                                                   ri,
                                                                                                   ci,
                                                                                                   rp,
                                                                                                   0);

        assertNotNull(context);
        assertEquals(0.0,
                     context.getAbsoluteCellX(),
                     0.0);
        assertEquals(75.0,
                     context.getCellWidth(),
                     0.0);
    }

    @Test
    public void testMakeHeaderRenderContextNoBlock() {
        final List<GridColumn<?>> allColumns = new ArrayList<>();
        final GridColumn<?> uiColumn = mockGridColumn(100.0);
        allColumns.add(uiColumn);

        doReturn(allColumns).when(ri).getAllColumns();
        doReturn(uiColumn).when(ci).getColumn();
        doReturn(0.0).when(ci).getOffsetX();
        doReturn(0).when(ci).getUiColumnIndex();

        final GridBodyCellRenderContext context = CellContextUtilities.makeHeaderCellRenderContext(gridWidget,
                                                                                                   ri,
                                                                                                   ci,
                                                                                                   rp,
                                                                                                   0);

        assertNotNull(context);
        assertEquals(0.0,
                     context.getAbsoluteCellX(),
                     0.0);
        assertEquals(100.0,
                     context.getCellWidth(),
                     0.0);
    }

    @Test
    public void testEditWhenNoCellSelected() {
        final GridColumn.HeaderMetaData headerMetaData = mock(GridColumn.HeaderMetaData.class);
        final GridColumn<?> gridColumn = mockGridColumn(100.0, Arrays.asList(headerMetaData));
        gridWidget.getModel().appendColumn(gridColumn);

        CellContextUtilities.editSelectedCell(gridWidget);

        verify(headerMetaData, never()).edit(any(GridBodyCellEditContext.class));
        verify(gridWidget, never()).startEditingCell(anyInt(), anyInt());
        verify(gridWidget, never()).startEditingCell(any(Point2D.class));
    }

    @Test
    public void testEditWhenHeaderCellSelected() {
        setupHeaderMetadata();

        CellContextUtilities.editSelectedCell(gridWidget);

        verify(headerMetaDataC1, never()).edit(any(GridBodyCellEditContext.class));
        verify(headerMetaDataC2).edit(gridBodyCellEditContextCaptor.capture());
        final GridBodyCellEditContext gridBodyCellEditContext = gridBodyCellEditContextCaptor.getValue();
        assertThat(gridBodyCellEditContext)
                .hasFieldOrPropertyWithValue("columnIndex", 1)
                .hasFieldOrPropertyWithValue("rowIndex", 0);

        verify(gridWidget, never()).startEditingCell(anyInt(), anyInt());
        verify(gridWidget, never()).startEditingCell(any(Point2D.class));
    }

    @Test
    public void testEditWhenHeaderCellSelectedWithRelativeLocation() {
        setupHeaderMetadata();

        final Point2D relativeLocation = new Point2D(25.0, 35.0);

        CellContextUtilities.editSelectedCell(gridWidget, relativeLocation);

        verify(headerMetaDataC1, never()).edit(any(GridBodyCellEditContext.class));
        verify(headerMetaDataC2).edit(gridBodyCellEditContextCaptor.capture());
        final GridBodyCellEditContext gridBodyCellEditContext = gridBodyCellEditContextCaptor.getValue();
        assertThat(gridBodyCellEditContext)
                .hasFieldOrPropertyWithValue("columnIndex", 1)
                .hasFieldOrPropertyWithValue("rowIndex", 0)
                .hasFieldOrPropertyWithValue("relativeLocation", Optional.of(relativeLocation));

        verify(gridWidget, never()).startEditingCell(anyInt(), anyInt());
        verify(gridWidget, never()).startEditingCell(any(Point2D.class));
    }

    private void setupHeaderMetadata() {
        final GridColumn<?> gridColumnOne = mockGridColumn(100.0, Arrays.asList(headerMetaDataC1));
        final GridColumn<?> gridColumnTwo = mockGridColumn(100.0, Arrays.asList(headerMetaDataC2));

        doReturn(0).when(gridColumnOne).getIndex();
        doReturn(1).when(gridColumnTwo).getIndex();

        final double secondColumnXCoordinate = gridColumnOne.getWidth() + gridColumnTwo.getWidth() / 2;

        doReturn(ci).when(gridRendererHelper).getColumnInformation(secondColumnXCoordinate);
        doReturn(gridColumnOne.getWidth()).when(gridRendererHelper).getColumnOffset(gridColumnTwo);
        doReturn(gridColumnTwo).when(ci).getColumn();
        doReturn(0.0).when(ci).getOffsetX();
        doReturn(1).when(ci).getUiColumnIndex();

        doReturn(Arrays.asList(gridColumnOne, gridColumnTwo)).when(ri).getAllColumns();

        gridWidget.getModel().appendColumn(gridColumnOne);
        gridWidget.getModel().appendColumn(gridColumnTwo);
        gridWidget.getModel().selectHeaderCell(0, 1);
    }

    @Test
    public void testEditWhenDataCellSelectedSecondRow() {
        final GridColumn.HeaderMetaData headerMetaData = mock(GridColumn.HeaderMetaData.class);
        final GridColumn<?> gridColumn = mockGridColumn(100.0, Arrays.asList(headerMetaData));

        doReturn(0).when(gridColumn).getIndex();

        gridWidget.getModel().appendColumn(gridColumn);
        gridWidget.getModel().appendRow(row1);
        gridWidget.getModel().appendRow(row2);
        gridWidget.getModel().selectCell(1, 0);

        CellContextUtilities.editSelectedCell(gridWidget);

        verify(headerMetaData, never()).edit(any(GridBodyCellEditContext.class));
        verify(gridWidget).startEditingCell(1, 0);
    }

    @Test
    public void testEditWhenDataCellSelectedSecondColumn() {
        final GridColumn.HeaderMetaData headerMetaDataC1 = mock(GridColumn.HeaderMetaData.class);
        final GridColumn.HeaderMetaData headerMetaDataC2 = mock(GridColumn.HeaderMetaData.class);
        final GridColumn<?> gridColumnOne = mockGridColumn(100.0, Arrays.asList(headerMetaDataC1));
        final GridColumn<?> gridColumnTwo = mockGridColumn(100.0, Arrays.asList(headerMetaDataC2));

        doReturn(0).when(gridColumnOne).getIndex();
        doReturn(1).when(gridColumnTwo).getIndex();

        gridWidget.getModel().appendColumn(gridColumnOne);
        gridWidget.getModel().appendColumn(gridColumnTwo);
        gridWidget.getModel().appendRow(row1);
        gridWidget.getModel().selectCell(0, 1);

        CellContextUtilities.editSelectedCell(gridWidget);

        verify(headerMetaDataC1, never()).edit(any(GridBodyCellEditContext.class));
        verify(headerMetaDataC2, never()).edit(any(GridBodyCellEditContext.class));
        verify(gridWidget).startEditingCell(0, 1);
    }

    private GridColumn<?> mockGridColumn(final double width) {
        final List<GridColumn.HeaderMetaData> headerMetaData = new ArrayList<>();
        headerMetaData.add(mock(GridColumn.HeaderMetaData.class));
        headerMetaData.add(mock(GridColumn.HeaderMetaData.class));

        return mockGridColumn(width,
                              headerMetaData);
    }

    private GridColumn<?> mockGridColumn(final double width,
                                         final List<GridColumn.HeaderMetaData> headerMetaData) {
        final GridColumn<?> uiColumn = mock(GridColumn.class);

        doReturn(headerMetaData).when(uiColumn).getHeaderMetaData();
        doReturn(width).when(uiColumn).getWidth();

        return uiColumn;
    }
}
