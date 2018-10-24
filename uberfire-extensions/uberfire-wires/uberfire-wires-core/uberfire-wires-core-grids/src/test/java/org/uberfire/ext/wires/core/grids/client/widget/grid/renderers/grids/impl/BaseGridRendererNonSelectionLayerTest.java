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
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBoundaryRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetRenderingTestUtils;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderBodyGridBackgroundCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderGridBoundaryCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderHeaderBackgroundCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderHeaderGridLinesCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderSelectorCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RendererCommand;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetRenderingTestUtils.HEADER_ROW_HEIGHT;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetRenderingTestUtils.makeRenderingInformation;

@WithClassesToStub({Text.class})
@RunWith(LienzoMockitoTestRunner.class)
public class BaseGridRendererNonSelectionLayerTest extends BaseGridRendererTest {

    @Override
    protected boolean isSelectionLayer() {
        return false;
    }

    @Test
    public void checkRenderSelector() {
        final BaseGridRendererHelper.RenderingInformation ri = makeRenderingInformation(model,
                                                                                        Arrays.asList(0d, HEADER_ROW_HEIGHT, HEADER_ROW_HEIGHT * 2));

        final RendererCommand command = renderer.renderSelector(WIDTH,
                                                                HEIGHT,
                                                                ri);

        assertNotNull(command);
        assertRenderingCommands(Collections.singletonList(command),
                                RenderSelectorCommand.class);

        command.execute(rc);

        final ArgumentCaptor<MultiPath> selectorCaptor = ArgumentCaptor.forClass(MultiPath.class);
        verify(parent).add(selectorCaptor.capture());

        final MultiPath selector = selectorCaptor.getValue();
        assertEquals(WIDTH,
                     selector.getBoundingBox().getWidth(),
                     0.5);
        assertEquals(HEIGHT,
                     selector.getBoundingBox().getHeight(),
                     0.5);
    }

    @Test
    public void checkSelectedCellsClippedByHeader() {
        checkRenderedSelectedCells(0,
                                   0,
                                   1,
                                   3,
                                   1,
                                   2,
                                   column.getWidth());
    }

    @Test
    public void checkSelectedCellsNotClippedByHeader() {
        checkRenderedSelectedCells(0,
                                   0,
                                   1,
                                   3,
                                   0,
                                   2,
                                   column.getWidth());
    }

    @Test
    public void checkSelectedCellsWithHiddenColumnSingleColumn() {
        column.setVisible(false);
        checkRenderedSelectedCells(0,
                                   0,
                                   1,
                                   3,
                                   0,
                                   2,
                                   0.0);
    }

    @Test
    public void checkSelectedCellsWithHiddenColumnMultipleColumns() {
        final GridColumn<String> column2 = makeGridColumn(200.0);
        this.model.appendColumn(column2);
        setupSelectionContext();

        checkRenderedSelectedCells(0,
                                   0,
                                   2,
                                   1,
                                   0,
                                   0,
                                   column.getWidth() + column2.getWidth());
    }

    @Test
    public void checkSelectedCellsWithHiddenColumnMultipleColumnsFirstHidden() {
        final GridColumn<String> column2 = makeGridColumn(200.0);
        this.model.appendColumn(column2);
        this.column.setVisible(false);
        setupSelectionContext();

        checkRenderedSelectedCells(0,
                                   0,
                                   2,
                                   1,
                                   0,
                                   0,
                                   column2.getWidth());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkRenderHeader() {
        final BaseGridRendererHelper.RenderingInformation ri = makeRenderingInformation(model,
                                                                                        Arrays.asList(0d, HEADER_ROW_HEIGHT, HEADER_ROW_HEIGHT * 2));
        final GridHeaderRenderContext context = mock(GridHeaderRenderContext.class);
        doReturn(model.getColumns()).when(context).getAllColumns();
        doReturn(model.getColumns()).when(context).getBlockColumns();

        final List<RendererCommand> commands = renderer.renderHeader(model,
                                                                     context,
                                                                     rendererHelper,
                                                                     ri);
        assertThat(commands).isNotNull();
        assertThat(commands).asList().hasSize(2);
        assertRenderingCommands(commands,
                                RenderHeaderBackgroundCommand.class, RenderHeaderGridLinesCommand.class);

        //Check the ColumnRenderer was asked to contribute towards the rendering
        //It is mocked in this test and hence we cannot verify it actually did anything.
        verify(columnRenderer).renderHeader(anyList(),
                                            any(GridHeaderColumnRenderContext.class),
                                            eq(ri),
                                            any(BiFunction.class));

        //Notional check for background rendering
        final ArgumentCaptor<Rectangle> rectangleCaptor = ArgumentCaptor.forClass(Rectangle.class);

        commands.stream().filter(c -> c instanceof RenderHeaderBackgroundCommand).findFirst().ifPresent(c -> c.execute(rc));

        verify(parent).add(rectangleCaptor.capture());

        assertRenderedRectangle(rectangleCaptor.getValue(),
                                column.getWidth(),
                                BaseGridWidgetRenderingTestUtils.HEADER_HEIGHT);

        //Notional check for header/body divider
        reset(parent);
        final ArgumentCaptor<Line> lineCaptor = ArgumentCaptor.forClass(Line.class);

        commands.stream().filter(c -> c instanceof RenderHeaderGridLinesCommand).findFirst().ifPresent(c -> c.execute(rc));

        verify(parent).add(lineCaptor.capture());

        final Line line = lineCaptor.getValue();
        assertEquals(column.getWidth(),
                     line.getBoundingBox().getWidth(),
                     0.5);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkRenderBody() {
        final BaseGridRendererHelper.RenderingInformation ri = makeRenderingInformation(model,
                                                                                        Arrays.asList(0d, HEADER_ROW_HEIGHT, HEADER_ROW_HEIGHT * 2));
        final GridBodyRenderContext context = mock(GridBodyRenderContext.class);
        doReturn(0).when(context).getMinVisibleRowIndex();
        doReturn(model.getRowCount() - 1).when(context).getMaxVisibleRowIndex();
        doReturn(model.getColumns()).when(context).getBlockColumns();

        final List<RendererCommand> commands = renderer.renderBody(model,
                                                                   context,
                                                                   rendererHelper,
                                                                   ri);
        assertThat(commands).isNotNull();
        assertThat(commands).asList().hasSize(1);
        assertThat(commands).asList().hasOnlyOneElementSatisfying(o -> assertTrue(o instanceof RenderBodyGridBackgroundCommand));

        //Check the ColumnRenderer was asked to contribute towards the rendering
        //It is mocked in this test and hence we cannot verify it actually did anything.
        verify(columnRenderer).renderColumn(eq(column),
                                            any(GridBodyColumnRenderContext.class),
                                            eq(rendererHelper),
                                            eq(ri),
                                            any(BiFunction.class));

        //Notional check for background rendering
        final ArgumentCaptor<Rectangle> rectangleCaptor = ArgumentCaptor.forClass(Rectangle.class);

        commands.get(0).execute(rc);

        verify(parent).add(rectangleCaptor.capture());

        assertRenderedRectangle(rectangleCaptor.getValue(),
                                column.getWidth(),
                                ri.getVisibleRowOffsets().get(2) + HEADER_ROW_HEIGHT);
    }

    @Test
    public void checkRenderBoundary() {
        final double WIDTH = 100.0;
        final double HEIGHT = 200.0;
        final GridBoundaryRenderContext context = new GridBoundaryRenderContext(0, 0, WIDTH, HEIGHT);

        final RendererCommand command = renderer.renderGridBoundary(context);

        assertNotNull(command);
        assertRenderingCommands(Collections.singletonList(command),
                                RenderGridBoundaryCommand.class);

        command.execute(rc);

        final ArgumentCaptor<Rectangle> boundaryCaptor = ArgumentCaptor.forClass(Rectangle.class);
        verify(parent).add(boundaryCaptor.capture());

        assertRenderedRectangle(boundaryCaptor.getValue(),
                                WIDTH,
                                HEIGHT);
    }

    @SuppressWarnings("unchecked")
    private void checkRenderedSelectedCells(final int selectionRowIndex,
                                            final int selectionColumnIndex,
                                            final int selectionColumnCount,
                                            final int selectionRowCount,
                                            final int minVisibleRowIndex,
                                            final int maxVisibleRowIndex,
                                            final double expectedSelectionWidth) {
        this.model.selectCells(selectionRowIndex,
                               selectionColumnIndex,
                               selectionColumnCount,
                               selectionRowCount);
        when(context.getMinVisibleRowIndex()).thenReturn(minVisibleRowIndex);
        when(context.getMaxVisibleRowIndex()).thenReturn(maxVisibleRowIndex);

        final double[] actualSelectionWidth = new double[]{0.0};
        doAnswer((i) -> {
            actualSelectionWidth[0] = (double) i.callRealMethod();
            return actualSelectionWidth[0];
        }).when(renderer).getSelectedRangeWidth(anyList(), anyInt(), any(SelectedRange.class));

        renderer.renderSelectedCells(model,
                                     context,
                                     rendererHelper,
                                     model.getSelectedCells(),
                                     (uiRowIndex, minVisibleUiRowIndex) -> 0.0,
                                     selectedRange -> 0.0).execute(rc);

        verify(renderer,
               times(1)).renderSelectedRange(columnsCaptor.capture(),
                                             eq(selectionColumnIndex),
                                             selectedRangeCaptor.capture(),
                                             any(Function.class));

        final List<GridColumn<?>> columns = columnsCaptor.getValue();
        assertNotNull(columns);
        assertEquals(selectionColumnCount,
                     columns.size());
        assertEquals(column,
                     columns.get(0));

        final SelectedRange selectedRange = selectedRangeCaptor.getValue();
        assertNotNull(selectedRange);
        assertEquals(selectionColumnIndex,
                     selectedRange.getUiColumnIndex());
        assertEquals(minVisibleRowIndex,
                     selectedRange.getUiRowIndex());
        assertEquals(selectionColumnCount,
                     selectedRange.getWidth());
        assertEquals(maxVisibleRowIndex - minVisibleRowIndex + 1,
                     selectedRange.getHeight());

        assertEquals(expectedSelectionWidth,
                     actualSelectionWidth[0],
                     0.0);
    }

    @SafeVarargs
    private final void assertRenderingCommands(final List<RendererCommand> actualCommands,
                                               final Class<? extends RendererCommand>... expectedTypes) {
        assertThat(actualCommands).asList().hasOnlyElementsOfTypes(expectedTypes);
        Arrays.asList(expectedTypes).forEach(type -> assertThat(actualCommands).asList().filteredOn(type::isInstance).hasSize(1));
    }

    private void assertRenderedRectangle(final Rectangle rectangle,
                                         final double expectedWidth,
                                         final double expectedHeight) {
        assertEquals(expectedWidth,
                     rectangle.getWidth(),
                     0.5);
        assertEquals(expectedHeight,
                     rectangle.getHeight(),
                     0.5);
    }
}
