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

package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPathClipper;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.GwtMockito;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetRenderingTestUtils;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ColumnRenderingStrategyMergedTest {

    private static final double ROW_HEIGHT = 50;

    private static final double COLUMN_WIDTH = 100;

    private static final double CONTEXT_X_POSITION = 0;

    private static final int CONTEXT_MIN_VISIBLE_ROW_INDEX = 0;

    private static final int CONTEXT_MAX_VISIBLE_ROW_INDEX = 2;

    @Spy
    private MultiPath multiPath = new MultiPath();

    @Mock
    private BaseGridRendererHelper rendererHelper;

    @Mock
    private BaseGridRendererHelper.RenderingInformation renderingInformation;

    @Mock
    private BiFunction<Boolean, GridColumn<?>, Boolean> columnRenderingConstraint;

    @Mock
    private BoundingBoxPathClipperFactory boundingBoxPathClipperFactory;

    @Mock
    private IPathClipper pathClipper;

    @Mock
    private Group columnGroup;

    @Mock
    private Group cellGroup;

    @Mock
    private GridColumn<?> gridColumn;

    @Mock
    private GridBodyColumnRenderContext context;

    @Mock
    private GridRenderer gridRenderer;

    @Mock
    private GridRendererTheme gridRendererTheme;

    @Mock
    private GridColumnRenderer gridColumnRenderer;

    @Mock
    private GridData gridData;

    @Mock
    private GridCell<String> gridCell;

    @Mock
    private GridRow gridRow;

    @Captor
    private ArgumentCaptor<GridBodyCellRenderContext> gridBodyCellRenderContextArgumentCaptor;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        GwtMockito.useProviderForType(BoundingBoxPathClipperFactory.class, aClass -> boundingBoxPathClipperFactory);
        GwtMockito.useProviderForType(Group.class, aClass -> columnGroup);

        when(context.getX()).thenReturn(CONTEXT_X_POSITION);
        when(context.getMinVisibleRowIndex()).thenReturn(CONTEXT_MIN_VISIBLE_ROW_INDEX);
        when(context.getMaxVisibleRowIndex()).thenReturn(CONTEXT_MAX_VISIBLE_ROW_INDEX);
        when(context.getModel()).thenReturn(gridData);
        when(context.getRenderer()).thenReturn(gridRenderer);

        when(gridRenderer.getTheme()).thenReturn(gridRendererTheme);
        when(gridRendererTheme.getBodyGridLine()).thenReturn(multiPath);
        when(columnRenderingConstraint.apply(false, gridColumn)).thenReturn(true);
        when(renderingInformation.getVisibleRowOffsets()).thenReturn(Arrays.asList(0d, ROW_HEIGHT, ROW_HEIGHT * 2d));
        when(boundingBoxPathClipperFactory.newClipper(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(pathClipper);

        when(gridColumn.getWidth()).thenReturn(COLUMN_WIDTH);
        when(gridColumn.getColumnRenderer()).thenReturn(gridColumnRenderer);
        //Grid-lines are only rendered for all but the last column so ensure we have at least 2 columns
        when(gridData.getColumnCount()).thenReturn(2);
        when(gridData.getColumns()).thenReturn(Arrays.asList(gridColumn, mock(GridColumn.class)));
    }

    @Test
    public void testGetCellHeightCells3() {
        doReturn(3).when(gridCell).getMergedCellCount();
        final List<Double> allRowHeights = new ArrayList<>(Collections.nCopies(3, BaseGridWidgetRenderingTestUtils.HEADER_ROW_HEIGHT));
        assertThat(ColumnRenderingStrategyMerged.getCellHeight(0, allRowHeights, gridCell)).isEqualTo(BaseGridWidgetRenderingTestUtils.HEADER_ROW_HEIGHT * 3);
    }

    @Test
    public void testGetCellHeightCells4() {
        doReturn(4).when(gridCell).getMergedCellCount();
        final List<Double> allRowHeights = new ArrayList<>(Collections.nCopies(4, BaseGridWidgetRenderingTestUtils.HEADER_ROW_HEIGHT));
        assertThat(ColumnRenderingStrategyMerged.getCellHeight(0, allRowHeights, gridCell)).isEqualTo(BaseGridWidgetRenderingTestUtils.HEADER_ROW_HEIGHT * 4);
    }

    @Test
    public void testIsCollapsedCellMixedValueThreeDifferentValues() {
        when(gridData.getRow(anyInt())).thenReturn(gridRow);
        when(gridRow.getHeight()).thenReturn(BaseGridWidgetRenderingTestUtils.HEADER_ROW_HEIGHT);

        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("two", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("three", 0);
        doReturn(3).when(gridData).getRowCount();
        doReturn(cellOne).when(gridData).getCell(0, 0);
        doReturn(cellTwo).when(gridData).getCell(1, 0);
        doReturn(cellThree).when(gridData).getCell(2, 0);
        doReturn(true).when(gridRow).isCollapsed();

        assertThat(ColumnRenderingStrategyMerged.isCollapsedCellMixedValue(gridData, 2, 0)).isTrue();
    }

    @Test
    public void testIsCollapsedCellMixedValueOneDifferentValue_1() {
        when(gridData.getRow(anyInt())).thenReturn(gridRow);
        when(gridRow.getHeight()).thenReturn(BaseGridWidgetRenderingTestUtils.HEADER_ROW_HEIGHT);

        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("two", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("one", 0);
        doReturn(3).when(gridData).getRowCount();
        doReturn(cellOne).when(gridData).getCell(0, 0);
        doReturn(cellTwo).when(gridData).getCell(1, 0);
        doReturn(cellThree).when(gridData).getCell(2, 0);
        doReturn(true).when(gridRow).isCollapsed();

        assertThat(ColumnRenderingStrategyMerged.isCollapsedCellMixedValue(gridData, 2, 0)).isTrue();
    }

    @Test
    public void testIsCollapsedCellMixedValueOneDifferentValue_2() {
        when(gridData.getRow(anyInt())).thenReturn(gridRow);
        when(gridRow.getHeight()).thenReturn(BaseGridWidgetRenderingTestUtils.HEADER_ROW_HEIGHT);

        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("two", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("one", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("one", 0);
        doReturn(3).when(gridData).getRowCount();
        doReturn(cellOne).when(gridData).getCell(0, 0);
        doReturn(cellTwo).when(gridData).getCell(1, 0);
        doReturn(cellThree).when(gridData).getCell(2, 0);
        doReturn(true).when(gridRow).isCollapsed();

        assertThat(ColumnRenderingStrategyMerged.isCollapsedCellMixedValue(gridData, 2, 0)).isTrue();
    }

    @Test
    public void testIsCollapsedCellMixedValueOneDifferentValue_3() {
        when(gridData.getRow(anyInt())).thenReturn(gridRow);
        when(gridRow.getHeight()).thenReturn(BaseGridWidgetRenderingTestUtils.HEADER_ROW_HEIGHT);

        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("one", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("two", 0);
        doReturn(3).when(gridData).getRowCount();
        doReturn(cellOne).when(gridData).getCell(0, 0);
        doReturn(cellTwo).when(gridData).getCell(1, 0);
        doReturn(cellThree).when(gridData).getCell(2, 0);
        doReturn(true).when(gridRow).isCollapsed();

        assertThat(ColumnRenderingStrategyMerged.isCollapsedCellMixedValue(gridData, 2, 0)).isTrue();
    }

    @Test
    public void testIsCollapsedCellMixedValue() {
        when(gridData.getRow(anyInt())).thenReturn(gridRow);
        when(gridRow.getHeight()).thenReturn(BaseGridWidgetRenderingTestUtils.HEADER_ROW_HEIGHT);

        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("one", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("one", 0);
        doReturn(3).when(gridData).getRowCount();
        doReturn(cellOne).when(gridData).getCell(0, 0);
        doReturn(cellTwo).when(gridData).getCell(1, 0);
        doReturn(cellThree).when(gridData).getCell(2, 0);
        doReturn(true).when(gridRow).isCollapsed();

        assertThat(ColumnRenderingStrategyMerged.isCollapsedCellMixedValue(gridData, 2, 0)).isFalse();
    }

    @Test
    public void testIsCollapsedRowMixedValueThreeDifferentValues() {
        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("two", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("three", 0);
        final GridRow gridRowOne = mock(GridRow.class);
        final GridRow gridRowTwo = mock(GridRow.class);
        final GridRow gridRowThree = mock(GridRow.class);
        doReturn(gridRowOne).when(gridData).getRow(0);
        doReturn(gridRowTwo).when(gridData).getRow(1);
        doReturn(gridRowThree).when(gridData).getRow(2);
        doReturn(false).when(gridRowOne).isCollapsed();
        doReturn(true).when(gridRowTwo).isCollapsed();
        doReturn(true).when(gridRowThree).isCollapsed();
        doReturn(Collections.singletonMap(0, cellOne)).when(gridRowOne).getCells();
        doReturn(Collections.singletonMap(0, cellTwo)).when(gridRowTwo).getCells();
        doReturn(Collections.singletonMap(0, cellThree)).when(gridRowThree).getCells();

        assertThat(ColumnRenderingStrategyMerged.isCollapsedRowMultiValue(gridData, gridColumn, cellThree, 2)).isTrue();
    }

    @Test
    public void testIsCollapsedRowMixedValueOneDifferentValue_1() {
        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("one", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("two", 0);
        final GridRow gridRowOne = mock(GridRow.class);
        final GridRow gridRowTwo = mock(GridRow.class);
        final GridRow gridRowThree = mock(GridRow.class);
        doReturn(gridRowOne).when(gridData).getRow(0);
        doReturn(gridRowTwo).when(gridData).getRow(1);
        doReturn(gridRowThree).when(gridData).getRow(2);
        doReturn(false).when(gridRowOne).isCollapsed();
        doReturn(true).when(gridRowTwo).isCollapsed();
        doReturn(true).when(gridRowThree).isCollapsed();
        doReturn(Collections.singletonMap(0, cellOne)).when(gridRowOne).getCells();
        doReturn(Collections.singletonMap(0, cellTwo)).when(gridRowTwo).getCells();
        doReturn(Collections.singletonMap(0, cellThree)).when(gridRowThree).getCells();

        assertThat(ColumnRenderingStrategyMerged.isCollapsedRowMultiValue(gridData, gridColumn, cellThree, 2)).isTrue();
    }

    @Test
    public void testIsCollapsedRowMixedValueOneDifferentValue_2() {
        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("two", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("one", 0);
        final GridRow gridRowOne = mock(GridRow.class);
        final GridRow gridRowTwo = mock(GridRow.class);
        final GridRow gridRowThree = mock(GridRow.class);
        doReturn(gridRowOne).when(gridData).getRow(0);
        doReturn(gridRowTwo).when(gridData).getRow(1);
        doReturn(gridRowThree).when(gridData).getRow(2);
        doReturn(false).when(gridRowOne).isCollapsed();
        doReturn(true).when(gridRowTwo).isCollapsed();
        doReturn(true).when(gridRowThree).isCollapsed();
        doReturn(Collections.singletonMap(0, cellOne)).when(gridRowOne).getCells();
        doReturn(Collections.singletonMap(0, cellTwo)).when(gridRowTwo).getCells();
        doReturn(Collections.singletonMap(0, cellThree)).when(gridRowThree).getCells();

        assertThat(ColumnRenderingStrategyMerged.isCollapsedRowMultiValue(gridData, gridColumn, cellThree, 2)).isTrue();
    }

    @Test
    public void testIsCollapsedRowMixedValueOneDifferentValue_3() {
        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("one", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("two", 0);
        final GridRow gridRowOne = mock(GridRow.class);
        final GridRow gridRowTwo = mock(GridRow.class);
        final GridRow gridRowThree = mock(GridRow.class);
        doReturn(gridRowOne).when(gridData).getRow(0);
        doReturn(gridRowTwo).when(gridData).getRow(1);
        doReturn(gridRowThree).when(gridData).getRow(2);
        doReturn(false).when(gridRowOne).isCollapsed();
        doReturn(true).when(gridRowTwo).isCollapsed();
        doReturn(true).when(gridRowThree).isCollapsed();
        doReturn(Collections.singletonMap(0, cellOne)).when(gridRowOne).getCells();
        doReturn(Collections.singletonMap(0, cellTwo)).when(gridRowTwo).getCells();
        doReturn(Collections.singletonMap(0, cellThree)).when(gridRowThree).getCells();

        assertThat(ColumnRenderingStrategyMerged.isCollapsedRowMultiValue(gridData, gridColumn, cellThree, 2)).isTrue();
    }

    @Test
    public void testIsCollapsedRowMixedValue() {
        final GridCell<String> cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell<String> cellTwo = gridCellWithMockedMergedCellCount("one", 0);
        final GridCell<String> cellThree = gridCellWithMockedMergedCellCount("one", 0);
        final GridRow gridRowOne = mock(GridRow.class);
        final GridRow gridRowTwo = mock(GridRow.class);
        final GridRow gridRowThree = mock(GridRow.class);
        doReturn(gridRowOne).when(gridData).getRow(0);
        doReturn(gridRowTwo).when(gridData).getRow(1);
        doReturn(gridRowThree).when(gridData).getRow(2);
        doReturn(false).when(gridRowOne).isCollapsed();
        doReturn(true).when(gridRowTwo).isCollapsed();
        doReturn(true).when(gridRowThree).isCollapsed();
        doReturn(Collections.singletonMap(0, cellOne)).when(gridRowOne).getCells();
        doReturn(Collections.singletonMap(0, cellTwo)).when(gridRowTwo).getCells();
        doReturn(Collections.singletonMap(0, cellThree)).when(gridRowThree).getCells();

        assertThat(ColumnRenderingStrategyMerged.isCollapsedRowMultiValue(gridData, gridColumn, cellThree, 2)).isFalse();
    }

    private GridCell<String> gridCellWithMockedMergedCellCount(final String value,
                                                               final int mergedCellCount) {
        return new BaseGridCell<String>(new BaseGridCellValue<>(value)) {
            @Override
            public int getMergedCellCount() {
                return mergedCellCount;
            }
        };
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRenderNotSelectionLayer_Unmerged() {
        final GridCell cellOne = gridCellWithMockedMergedCellCount("one", 1);
        final GridCell cellTwo = gridCellWithMockedMergedCellCount("two", 1);
        final GridCell cellThree = gridCellWithMockedMergedCellCount("three", 1);
        final GridRow gridRowOne = mock(GridRow.class);
        final GridRow gridRowTwo = mock(GridRow.class);
        final GridRow gridRowThree = mock(GridRow.class);
        when(gridData.getRow(0)).thenReturn(gridRowOne);
        when(gridData.getRow(1)).thenReturn(gridRowTwo);
        when(gridData.getRow(2)).thenReturn(gridRowThree);
        when(gridRowOne.isCollapsed()).thenReturn(false);
        when(gridRowTwo.isCollapsed()).thenReturn(false);
        when(gridRowThree.isCollapsed()).thenReturn(false);
        when(gridRowOne.getCells()).thenReturn(Collections.singletonMap(0, cellOne));
        when(gridRowTwo.getCells()).thenReturn(Collections.singletonMap(0, cellTwo));
        when(gridRowThree.getCells()).thenReturn(Collections.singletonMap(0, cellThree));
        when(gridData.getCell(0, 0)).thenReturn(cellOne);
        when(gridData.getCell(1, 0)).thenReturn(cellTwo);
        when(gridData.getCell(2, 0)).thenReturn(cellThree);
        when(gridColumnRenderer.renderCell(any(GridCell.class), any(GridBodyCellRenderContext.class))).thenReturn(cellGroup);
        when(cellGroup.setX(anyDouble())).thenReturn(cellGroup);
        when(cellGroup.setY(anyDouble())).thenReturn(cellGroup);

        final List<Double> allRowHeights = new ArrayList<>(Collections.nCopies(3, ROW_HEIGHT));
        final GridRenderer.GridRendererContext rendererContext = mock(GridRenderer.GridRendererContext.class);
        final Group group = mock(Group.class);
        doReturn(false).when(rendererContext).isSelectionLayer();
        doReturn(group).when(rendererContext).getGroup();
        doReturn(allRowHeights).when(renderingInformation).getAllRowHeights();

        final List<GridRenderer.RendererCommand> commands = ColumnRenderingStrategyMerged.render(gridColumn,
                                                                                                 context,
                                                                                                 rendererHelper,
                                                                                                 renderingInformation,
                                                                                                 columnRenderingConstraint);

        // Grid lines and column content
        assertThat(commands).hasSize(2);

        // -- Grid lines --
        commands.get(0).execute(rendererContext);

        verify(group).add(multiPath);
        // Verify horizontal lines
        // First row
        verify(multiPath).M(CONTEXT_X_POSITION, 0 + 0.5);
        verify(multiPath).L(CONTEXT_X_POSITION + COLUMN_WIDTH, 0 + 0.5);

        // Second row
        verify(multiPath).M(CONTEXT_X_POSITION, ROW_HEIGHT + 0.5);
        verify(multiPath).L(CONTEXT_X_POSITION + COLUMN_WIDTH, ROW_HEIGHT + 0.5);

        // Third row
        verify(multiPath).M(CONTEXT_X_POSITION, ROW_HEIGHT * 2 + 0.5);
        verify(multiPath).L(CONTEXT_X_POSITION + COLUMN_WIDTH, ROW_HEIGHT * 2 + 0.5);

        // Vertical
        verify(multiPath).M(COLUMN_WIDTH + 0.5, 0);
        verify(multiPath).L(COLUMN_WIDTH + 0.5, ROW_HEIGHT * 3);

        reset(group);

        // -- Column content --
        commands.get(1).execute(rendererContext);

        verify(gridColumnRenderer).renderCell(eq(cellOne),
                                              gridBodyCellRenderContextArgumentCaptor.capture());
        assertGridBodyCellRenderContext(gridBodyCellRenderContextArgumentCaptor.getAllValues().get(0),
                                        0.0,
                                        0.0,
                                        gridColumn.getWidth(),
                                        ROW_HEIGHT,
                                        0,
                                        0);

        verify(gridColumnRenderer).renderCell(eq(cellTwo),
                                              gridBodyCellRenderContextArgumentCaptor.capture());
        assertGridBodyCellRenderContext(gridBodyCellRenderContextArgumentCaptor.getAllValues().get(1),
                                        0.0,
                                        50.0,
                                        gridColumn.getWidth(),
                                        ROW_HEIGHT,
                                        1,
                                        0);

        verify(gridColumnRenderer).renderCell(eq(cellThree),
                                              gridBodyCellRenderContextArgumentCaptor.capture());
        assertGridBodyCellRenderContext(gridBodyCellRenderContextArgumentCaptor.getAllValues().get(2),
                                        0.0,
                                        100.0,
                                        gridColumn.getWidth(),
                                        ROW_HEIGHT,
                                        2,
                                        0);

        verify(boundingBoxPathClipperFactory).newClipper(0,
                                                         0,
                                                         COLUMN_WIDTH,
                                                         ROW_HEIGHT * 3);
        verify(pathClipper).setActive(true);

        verify(columnGroup).setX(CONTEXT_X_POSITION);

        verify(group).add(columnGroup);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRenderNotSelectionLayer_Merged() {
        final GridCell cellOne = gridCellWithMockedMergedCellCount("one", 3);
        final GridCell cellTwo = gridCellWithMockedMergedCellCount("one", 0);
        final GridCell cellThree = gridCellWithMockedMergedCellCount("one", 0);
        final GridRow gridRowOne = mock(GridRow.class);
        final GridRow gridRowTwo = mock(GridRow.class);
        final GridRow gridRowThree = mock(GridRow.class);
        when(gridData.getRow(0)).thenReturn(gridRowOne);
        when(gridData.getRow(1)).thenReturn(gridRowTwo);
        when(gridData.getRow(2)).thenReturn(gridRowThree);
        when(gridRowOne.isMerged()).thenReturn(true);
        when(gridRowTwo.isMerged()).thenReturn(true);
        when(gridRowThree.isMerged()).thenReturn(true);
        when(gridRowOne.isCollapsed()).thenReturn(false);
        when(gridRowTwo.isCollapsed()).thenReturn(false);
        when(gridRowThree.isCollapsed()).thenReturn(false);
        when(gridRowOne.getCells()).thenReturn(Collections.singletonMap(0, cellOne));
        when(gridRowTwo.getCells()).thenReturn(Collections.singletonMap(0, cellTwo));
        when(gridRowThree.getCells()).thenReturn(Collections.singletonMap(0, cellThree));
        when(gridData.getCell(0, 0)).thenReturn(cellOne);
        when(gridData.getCell(1, 0)).thenReturn(cellTwo);
        when(gridData.getCell(2, 0)).thenReturn(cellThree);
        when(gridColumnRenderer.renderCell(any(GridCell.class), any(GridBodyCellRenderContext.class))).thenReturn(cellGroup);
        when(cellGroup.setX(anyDouble())).thenReturn(cellGroup);
        when(cellGroup.setY(anyDouble())).thenReturn(cellGroup);

        final List<Double> allRowHeights = new ArrayList<>(Collections.nCopies(3, ROW_HEIGHT));
        final GridRenderer.GridRendererContext rendererContext = mock(GridRenderer.GridRendererContext.class);
        final Group group = mock(Group.class);
        doReturn(false).when(rendererContext).isSelectionLayer();
        doReturn(group).when(rendererContext).getGroup();
        doReturn(allRowHeights).when(renderingInformation).getAllRowHeights();

        final List<GridRenderer.RendererCommand> commands = ColumnRenderingStrategyMerged.render(gridColumn,
                                                                                                 context,
                                                                                                 rendererHelper,
                                                                                                 renderingInformation,
                                                                                                 columnRenderingConstraint);

        // Grid lines and column content
        assertThat(commands).hasSize(2);

        // -- Grid lines --
        commands.get(0).execute(rendererContext);

        verify(group).add(multiPath);
        // Verify horizontal lines
        // First (merged) row
        verify(multiPath).M(CONTEXT_X_POSITION, 0 + 0.5);
        verify(multiPath).L(CONTEXT_X_POSITION + COLUMN_WIDTH, 0 + 0.5);

        // Vertical
        verify(multiPath).M(COLUMN_WIDTH + 0.5, 0);
        verify(multiPath).L(COLUMN_WIDTH + 0.5, ROW_HEIGHT * 3);

        reset(group);

        // -- Column content --
        commands.get(1).execute(rendererContext);

        verify(gridColumnRenderer).renderCell(eq(cellOne),
                                              gridBodyCellRenderContextArgumentCaptor.capture());
        final GridBodyCellRenderContext context = gridBodyCellRenderContextArgumentCaptor.getValue();
        assertGridBodyCellRenderContext(context,
                                        0.0,
                                        0.0,
                                        gridColumn.getWidth(),
                                        ROW_HEIGHT * 3,
                                        0,
                                        0);

        verify(boundingBoxPathClipperFactory).newClipper(0,
                                                         0,
                                                         COLUMN_WIDTH,
                                                         ROW_HEIGHT * 3);
        verify(pathClipper).setActive(true);

        verify(columnGroup).setX(CONTEXT_X_POSITION);

        verify(group).add(columnGroup);
    }

    private void assertGridBodyCellRenderContext(final GridBodyCellRenderContext context,
                                                 final double expectedAbsoluteCellX,
                                                 final double expectedAbsoluteCellY,
                                                 final double expectedCellWidth,
                                                 final double expectedCellHeight,
                                                 final int expectedRowIndex,
                                                 final int expectedColumnIndex) {
        assertThat(context).isNotNull();
        assertThat(context.getAbsoluteCellX()).isEqualTo(expectedAbsoluteCellX);
        assertThat(context.getAbsoluteCellY()).isEqualTo(expectedAbsoluteCellY);
        assertThat(context.getCellWidth()).isEqualTo(expectedCellWidth);
        assertThat(context.getCellHeight()).isEqualTo(expectedCellHeight);
        assertThat(context.getRowIndex()).isEqualTo(expectedRowIndex);
        assertThat(context.getColumnIndex()).isEqualTo(expectedColumnIndex);
    }
}
