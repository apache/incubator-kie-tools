/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.StringPopupColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.GridRendererContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.SelectionsTransformer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper.RenderingBlockInformation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.BlueTheme;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetRenderingTestUtils.makeRenderingInformation;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper.RenderingInformation;

public abstract class BaseGridRendererTest {

    protected static final double WIDTH = 100.0;

    protected static final double HEIGHT = 200.0;

    @Mock
    protected GridColumnRenderer<String> columnRenderer;

    @Mock
    protected GridBodyRenderContext context;

    @Mock
    protected BaseGridRendererHelper rendererHelper;

    @Mock
    protected RenderingInformation renderingInformation;

    @Mock
    protected Group parent;

    @Mock
    protected GridRendererContext rc;

    @Captor
    protected ArgumentCaptor<List<GridColumn<?>>> columnsCaptor;

    @Captor
    protected ArgumentCaptor<SelectedRange> selectedRangeCaptor;

    protected GridData model;

    protected GridColumn<String> column;

    protected GridRendererTheme theme = new BlueTheme();

    protected BaseGridRenderer renderer;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        final BaseGridRenderer wrapped = new BaseGridRenderer(theme);
        this.renderer = spy(wrapped);

        this.column = makeGridColumn(100.0);

        this.model = new BaseGridData();
        this.model.appendColumn(column);
        this.model.appendRow(new BaseGridRow());
        this.model.appendRow(new BaseGridRow());
        this.model.appendRow(new BaseGridRow());

        setupSelectionContext();

        doCallRealMethod().when(rendererHelper).getWidth(anyList());

        when(rc.getGroup()).thenReturn(parent);
        when(rc.isSelectionLayer()).thenReturn(isSelectionLayer());
    }

    @Test
    public void checkRenderHeaderWhenColumnsHaveNoMetaData() {
        column.getHeaderMetaData().clear();

        final RenderingInformation ri = makeRenderingInformation(model);
        final GridHeaderRenderContext context = mock(GridHeaderRenderContext.class);

        final List<GridRenderer.RendererCommand> commands = renderer.renderHeader(model,
                                                                                  context,
                                                                                  rendererHelper,
                                                                                  ri);
        assertThat(commands).isNotNull();
        assertThat(commands).asList().hasSize(0);
    }

    protected GridColumn<String> makeGridColumn(final double width) {
        return new StringPopupColumn(new BaseHeaderMetaData("title"),
                                     columnRenderer,
                                     width);
    }

    protected void setupSelectionContext() {
        final SelectionsTransformer selectionsTransformer = new DefaultSelectionsTransformer(model,
                                                                                             model.getColumns());

        when(context.getBlockColumns()).thenReturn(model.getColumns());
        when(context.getTransformer()).thenReturn(selectionsTransformer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMakeCellHighlight() {
        final int rowIndex = 1;
        final int visibleRowIndex = 2;
        final List<Double> allRowHeights = new ArrayList<>();
        final GridData model = mock(GridData.class);
        final GridRendererTheme theme = mock(GridRendererTheme.class);
        final Rectangle rectangle = mock(Rectangle.class);
        when(theme.getHighlightedCellBackground()).thenReturn(rectangle);
        when(rectangle.setListening(false)).thenReturn(rectangle);
        renderer.setTheme(theme);

        doNothing().when(renderer).setCellHighlightX(rectangle, context, rendererHelper);
        doNothing().when(renderer).setCellHighlightY(rectangle, rendererHelper, visibleRowIndex, model);
        doNothing().when(renderer).setCellHighlightSize(rectangle, model, column, allRowHeights, rowIndex);

        final Rectangle current = renderer.makeCellHighlight(rowIndex,
                                                             visibleRowIndex,
                                                             model,
                                                             rendererHelper,
                                                             renderingInformation,
                                                             column,
                                                             context);

        assertEquals(rectangle, current);
    }

    @Test
    public void testSetCellHighlightY() {
        final int visibleRowIndex = 2;
        final Rectangle rectangle = mock(Rectangle.class);
        final double y = 190;
        final GridCell cell = mock(GridCell.class);
        final int rowIndex = 5;
        final int columnIndex = 1;

        when(rendererHelper.getRowOffset(visibleRowIndex)).thenReturn(y);
        doReturn(rowIndex).when(renderer).getHighlightCellRowIndex();
        doReturn(columnIndex).when(renderer).getHighlightCellColumnIndex();
        final GridData dataModel = mock(GridData.class);

        when(dataModel.getCell(rowIndex, columnIndex)).thenReturn(cell);
        when(cell.isMerged()).thenReturn(false);

        renderer.setCellHighlightY(rectangle, rendererHelper, visibleRowIndex, dataModel);

        verify(rectangle).setY(y);
    }

    @Test
    public void testSetCellHighlightYWithMergedCell() {
        final int visibleRowIndex = 2;
        final Rectangle rectangle = mock(Rectangle.class);
        final double y = 190;
        final GridCell cell = mock(GridCell.class);
        final int rowIndex = 5;
        final int columnIndex = 1;
        final int firstMergedCellIndex = 3;
        when(rendererHelper.getRowOffset(firstMergedCellIndex)).thenReturn(y);
        doReturn(rowIndex).when(renderer).getHighlightCellRowIndex();
        doReturn(columnIndex).when(renderer).getHighlightCellColumnIndex();
        final GridData dataModel = mock(GridData.class);

        when(dataModel.getCell(rowIndex, columnIndex)).thenReturn(cell);
        when(cell.isMerged()).thenReturn(true);
        doReturn(firstMergedCellIndex).when(renderer).getFirstRowIndexOfMergedBlock(dataModel, visibleRowIndex);

        renderer.setCellHighlightY(rectangle, rendererHelper, visibleRowIndex, dataModel);

        verify(rectangle).setY(y);
    }

    @Test
    public void setCellHighlightX() {
        final RenderingInformation renderingInformation = mock(RenderingInformation.class);
        final RenderingBlockInformation floatingBlockInformation = mock(RenderingBlockInformation.class);
        final List<GridColumn<?>> fakeList = mock(List.class);
        when(floatingBlockInformation.getColumns()).thenReturn(fakeList);
        when(fakeList.size()).thenReturn(1);
        final Rectangle rectangle = mock(Rectangle.class);
        final double columnOffsetX = 77;
        final int columnIndex = 3;
        final double xOffset = 128;
        final double expectedOffset = xOffset - columnOffsetX;

        when(renderingInformation.getFloatingBlockInformation()).thenReturn(floatingBlockInformation);
        when(context.getAbsoluteColumnOffsetX()).thenReturn(columnOffsetX);
        final GridColumn<?> column = mock(GridColumn.class);
        when(column.getIndex()).thenReturn(columnIndex);

        when(rendererHelper.getColumnOffset(columnIndex)).thenReturn(xOffset);

        renderer.highlightCell(columnIndex, 0);
        renderer.setCellHighlightX(rectangle, context, rendererHelper);

        verify(rectangle).setX(expectedOffset);
    }

    @Test
    public void testSetCellHighlightSize() {
        final Rectangle rectangle = mock(Rectangle.class);
        final GridData model = mock(GridData.class);
        final GridColumn<?> column = mock(GridColumn.class);
        final double width = 100;
        final int rowIndex = 4;
        final GridRow row = mock(GridRow.class);
        final double height = 20;
        when(column.getWidth()).thenReturn(width);
        when(model.getRow(rowIndex)).thenReturn(row);
        doReturn(1).when(renderer).getMergedCellsCount(model, rowIndex);

        renderer.setCellHighlightSize(rectangle, model, column, Collections.nCopies(5, height), rowIndex);

        verify(rectangle).setWidth(width);
        verify(rectangle).setHeight(height);
    }

    @Test
    public void testGetMergedCellsCount() {
        final GridData model = mock(GridData.class);
        final GridCell cell = mock(GridCell.class);
        final int rowIndex = 0;
        final int columnIndex = 1;
        final int mergedCellsCount = 1;

        when(renderer.getHighlightCellColumnIndex()).thenReturn(columnIndex);
        when(model.getCell(rowIndex, columnIndex)).thenReturn(cell);
        when(cell.getMergedCellCount()).thenReturn(mergedCellsCount);

        final int actual = renderer.getMergedCellsCount(model, rowIndex);

        assertEquals(mergedCellsCount, actual);
    }

    @Test
    public void testRenderHighlightedCells() {

        final RenderingInformation renderingInformation = mock(RenderingInformation.class);
        final List<GridColumn<?>> columnsList = new ArrayList<>();
        columnsList.add(column);
        final GridData dataModel = mock(GridData.class);

        doReturn(1).when(renderer).getHighlightCellRowIndex();
        doReturn(0).when(renderer).getHighlightCellColumnIndex();
        when(renderingInformation.getMinVisibleRowIndex()).thenReturn(1);
        when(dataModel.getColumns()).thenReturn(columnsList);

        final GridRenderer.RendererCommand result = renderer.renderHighlightedCells(dataModel, context, rendererHelper, renderingInformation);

        assertNotNull(result);
    }

    @Test
    public void testGetRendererCommand() {

        final int highlightCellRowIndex = 0;
        final GridData dataModel = mock(GridData.class);
        final int visibleRowIndex = 0;
        final List<GridColumn<?>> columnsList = new ArrayList<>();
        columnsList.add(column);
        final GridRendererContext rc = mock(GridRendererContext.class);
        final Group group = mock(Group.class);
        final Rectangle rectangle = mock(Rectangle.class);

        doReturn(highlightCellRowIndex).when(renderer).getHighlightCellRowIndex();
        when(dataModel.getColumns()).thenReturn(columnsList);
        when(rc.getGroup()).thenReturn(group);
        when(rc.isSelectionLayer()).thenReturn(false);

        doReturn(rectangle).when(renderer).makeCellHighlight(highlightCellRowIndex,
                                                             visibleRowIndex,
                                                             dataModel,
                                                             rendererHelper,
                                                             renderingInformation,
                                                             column,
                                                             context);

        final GridRenderer.RendererCommand cmd = renderer.getRendererCommand(dataModel,
                                                                             context,
                                                                             rendererHelper,
                                                                             renderingInformation,
                                                                             column,
                                                                             visibleRowIndex);

        cmd.execute(rc);

        verify(group).add(rectangle);
    }

    @Test
    public void testAddRenderHighlightedCellsCommand() {

        final GridData dataModel = mock(GridData.class);
        final RenderingInformation renderingInformation = mock(RenderingInformation.class);
        final List<GridRenderer.RendererCommand> commands = mock(List.class);
        final int columnIndex = 1;
        final int rowIndex = 1;
        final GridRenderer.RendererCommand cmd = mock(GridRenderer.RendererCommand.class);

        doReturn(columnIndex).when(renderer).getHighlightCellColumnIndex();
        doReturn(rowIndex).when(renderer).getHighlightCellRowIndex();
        doReturn(cmd).when(renderer).renderHighlightedCells(dataModel,
                                                            context,
                                                            rendererHelper,
                                                            renderingInformation);

        renderer.addRenderHighlightedCellsCommand(dataModel,
                                                  context,
                                                  rendererHelper,
                                                  renderingInformation,
                                                  commands);

        verify(commands).add(cmd);
    }

    @Test
    public void testClearCellHighlight() {

        renderer.highlightCell(0, 0);
        renderer.clearCellHighlight();

        final Integer rowIndex = renderer.getHighlightCellRowIndex();
        final Integer columnIndex = renderer.getHighlightCellColumnIndex();

        assertTrue(Objects.isNull(rowIndex));
        assertTrue(Objects.isNull(columnIndex));
    }

    @Test
    public void testGetFirstRowIndexOfMergedBlock() {

        final GridData dataModel = mock(GridData.class);
        final int rowIndex = 2;

        final GridCell cell2 = mock(GridCell.class);
        when(cell2.getMergedCellCount()).thenReturn(0);
        when(dataModel.getCell(2, 0)).thenReturn(cell2);

        final GridCell cell1 = mock(GridCell.class);
        when(cell1.getMergedCellCount()).thenReturn(1);
        when(dataModel.getCell(1, 0)).thenReturn(cell1);

        renderer.highlightCell(0, 0);
        final int actual = renderer.getFirstRowIndexOfMergedBlock(dataModel, rowIndex);

        assertEquals(1, actual);
    }

    protected abstract boolean isSelectionLayer();
}
