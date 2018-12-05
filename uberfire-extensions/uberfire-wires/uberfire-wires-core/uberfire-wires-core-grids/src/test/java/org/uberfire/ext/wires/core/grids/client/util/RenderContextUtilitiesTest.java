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
import java.util.List;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(LienzoMockitoTestRunner.class)
public class RenderContextUtilitiesTest {

    private static final double HEADER_HEIGHT = 50.0;
    private static final double HEADER_ROW_HEIGHT = HEADER_HEIGHT / 2;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private GridRenderer gridRenderer;

    @Mock
    private BaseGridRendererHelper gridRendererHelper;

    @Mock
    private BaseGridRendererHelper.RenderingInformation ri;

    @Mock
    private BaseGridRendererHelper.ColumnInformation ci;

    @Mock
    private BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation;

    private Point2D rp = new Point2D(0, 0);

    @Before
    public void setup() {
        doReturn(gridRenderer).when(gridWidget).getRenderer();
        doReturn(gridRendererHelper).when(gridWidget).getRendererHelper();
        doReturn(ri).when(gridRendererHelper).getRenderingInformation();
        doReturn(HEADER_HEIGHT).when(gridRenderer).getHeaderHeight();
        doReturn(HEADER_ROW_HEIGHT).when(gridRenderer).getHeaderRowHeight();

        doReturn(floatingBlockInformation).when(ri).getFloatingBlockInformation();
        doReturn(0.0).when(floatingBlockInformation).getX();
        doReturn(0.0).when(floatingBlockInformation).getWidth();

        doReturn(mock(Viewport.class)).when(gridWidget).getViewport();

        doReturn(new BaseGridData()).when(gridWidget).getModel();
    }

    @Test
    public void testMakeRenderContextNoBlockMultipleColumns() {
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

        final GridBodyCellRenderContext context = RenderContextUtilities.makeRenderContext(gridWidget,
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
    public void testMakeRenderContextLeadBlock() {
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

        final GridBodyCellRenderContext context = RenderContextUtilities.makeRenderContext(gridWidget,
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
    public void testMakeRenderContextLeadBlockWithExtraLeadNonBlockColumn() {
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

        final GridBodyCellRenderContext context = RenderContextUtilities.makeRenderContext(gridWidget,
                                                                                           ri,
                                                                                           ci,
                                                                                           rp,
                                                                                           0);

        assertNotNull(context);
        assertEquals(25.0,
                     context.getAbsoluteCellX(),
                     0.0);
        assertEquals(125.0,
                     context.getCellWidth(),
                     0.0);
    }

    @Test
    public void testMakeRenderContextTailBlock() {
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

        final GridBodyCellRenderContext context = RenderContextUtilities.makeRenderContext(gridWidget,
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
    public void testMakeRenderContextTailBlockWithExtraTailNonBlockColumn() {
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

        final GridBodyCellRenderContext context = RenderContextUtilities.makeRenderContext(gridWidget,
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
    public void testMakeRenderContextNoBlock() {
        final List<GridColumn<?>> allColumns = new ArrayList<>();
        final GridColumn<?> uiColumn = mockGridColumn(100.0);
        allColumns.add(uiColumn);

        doReturn(allColumns).when(ri).getAllColumns();
        doReturn(uiColumn).when(ci).getColumn();
        doReturn(0.0).when(ci).getOffsetX();
        doReturn(0).when(ci).getUiColumnIndex();

        final GridBodyCellRenderContext context = RenderContextUtilities.makeRenderContext(gridWidget,
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
