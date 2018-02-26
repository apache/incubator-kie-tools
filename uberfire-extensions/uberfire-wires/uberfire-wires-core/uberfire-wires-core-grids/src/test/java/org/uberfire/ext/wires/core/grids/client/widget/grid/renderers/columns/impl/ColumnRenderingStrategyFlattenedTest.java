/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import com.ait.lienzo.client.core.shape.BoundingBoxPathClipper;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPathClipper;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.WithClassesToStub;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@WithClassesToStub(BoundingBoxPathClipper.class)
@RunWith(LienzoMockitoTestRunner.class)
public class ColumnRenderingStrategyFlattenedTest {

    private final double ROW_HEIGHT = 50;
    private final double CONTEXT_X_POSITION = 0;
    private final double COLUMN_WIDTH = 220;
    private final int MIN_VISIBLE_ROW_INDEX = 0;
    private final int MAX_VISIBLE_ROW_INDEX = 2;

    @Mock
    private GridColumn<?> column;

    @Mock
    private GridBodyColumnRenderContext context;

    @Mock
    private GridData gridData;

    @Mock
    private GridRenderer gridRenderer;

    @Mock
    private GridRendererTheme gridRendererTheme;

    @Mock
    private GridColumnRenderer gridColumnRenderer;

    @Mock
    private GridRow rowOne;

    @Mock
    private GridRow rowTwo;

    @Mock
    private GridRow rowThree;

    /*
     *   [cellOne,   mock]
     *   [cellTwo,   mock]
     *   [cellThree, mock]
     */

    @Mock
    private GridCell cellOne;

    @Mock
    private GridCell cellTwo;

    @Mock
    private GridCell cellThree;

    @Spy
    private MultiPath multiPath = new MultiPath();

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

    @Before
    public void setUp() throws Exception {
        GwtMockito.useProviderForType(BoundingBoxPathClipperFactory.class, aClass -> boundingBoxPathClipperFactory);
        GwtMockito.useProviderForType(Group.class, aClass -> columnGroup);

        doReturn(CONTEXT_X_POSITION).when(context).getX();
        doReturn(MIN_VISIBLE_ROW_INDEX).when(context).getMinVisibleRowIndex();
        doReturn(MAX_VISIBLE_ROW_INDEX).when(context).getMaxVisibleRowIndex();
        doReturn(gridData).when(context).getModel();
        doReturn(gridRenderer).when(context).getRenderer();

        doReturn(gridRendererTheme).when(gridRenderer).getTheme();

        doReturn(multiPath).when(gridRendererTheme).getBodyGridLine();

        doReturn(Arrays.asList(0d,
                               ROW_HEIGHT,
                               ROW_HEIGHT * 2d)).when(renderingInformation).getVisibleRowOffsets();

        doReturn(COLUMN_WIDTH).when(column).getWidth();
        doReturn(gridColumnRenderer).when(column).getColumnRenderer();

        // grid not rendering for last column, so providing more
        doReturn(2).when(gridData).getColumnCount();
        doReturn(Arrays.asList(column, mock(GridColumn.class))).when(gridData).getColumns();
        doReturn(rowOne).when(gridData).getRow(0);
        doReturn(rowTwo).when(gridData).getRow(1);
        doReturn(rowThree).when(gridData).getRow(2);
        doReturn(cellOne).when(gridData).getCell(0, 0);
        doReturn(cellTwo).when(gridData).getCell(1, 0);
        doReturn(cellThree).when(gridData).getCell(2, 0);

        doReturn(ROW_HEIGHT).when(rowOne).getHeight();
        doReturn(ROW_HEIGHT).when(rowTwo).getHeight();
        doReturn(ROW_HEIGHT).when(rowThree).getHeight();

        doReturn(true).when(columnRenderingConstraint).apply(false, column);

        doReturn(pathClipper).when(boundingBoxPathClipperFactory).newClipper(0,
                                                                             0,
                                                                             COLUMN_WIDTH,
                                                                             ROW_HEIGHT * 3);
    }

    @Test
    public void testRenderNotSelectionLayer() throws Exception {
        final GridRenderer.GridRendererContext rendererContext = mock(GridRenderer.GridRendererContext.class);
        final Group group = mock(Group.class);
        doReturn(false).when(rendererContext).isSelectionLayer();
        doReturn(group).when(rendererContext).getGroup();

        final List<GridRenderer.RendererCommand> commands = ColumnRenderingStrategyFlattened.render(column,
                                                                                                    context,
                                                                                                    rendererHelper,
                                                                                                    renderingInformation,
                                                                                                    columnRenderingConstraint);

        // grid lines and column content
        Assertions.assertThat(commands).hasSize(2);

        // grid lines
        commands.get(0).execute(rendererContext);
        verify(group).add(multiPath);
        // verify horizontal lines
        // first row ignored
        verify(multiPath, never()).M(CONTEXT_X_POSITION, 0 + 0.5);
        verify(multiPath, never()).L(CONTEXT_X_POSITION + COLUMN_WIDTH, 0 + 0.5);

        // second row
        verify(multiPath).M(CONTEXT_X_POSITION, ROW_HEIGHT + 0.5);
        verify(multiPath).L(CONTEXT_X_POSITION + COLUMN_WIDTH, ROW_HEIGHT + 0.5);

        // third row
        verify(multiPath).M(CONTEXT_X_POSITION, ROW_HEIGHT * 2 + 0.5);
        verify(multiPath).L(CONTEXT_X_POSITION + COLUMN_WIDTH, ROW_HEIGHT * 2 + 0.5);

        // vertical
        verify(multiPath).M(COLUMN_WIDTH + 0.5, 0);
        verify(multiPath).L(COLUMN_WIDTH + 0.5, ROW_HEIGHT * 3);

        reset(group);

        // column content
        commands.get(1).execute(rendererContext);
        verify(gridColumnRenderer).renderCell(eq(cellOne),
                                              any(GridBodyCellRenderContext.class));
        verify(gridColumnRenderer).renderCell(eq(cellTwo),
                                              any(GridBodyCellRenderContext.class));
        verify(gridColumnRenderer).renderCell(eq(cellThree),
                                              any(GridBodyCellRenderContext.class));

        verify(boundingBoxPathClipperFactory).newClipper(0,
                                                         0,
                                                         COLUMN_WIDTH,
                                                         ROW_HEIGHT * 3);
        verify(pathClipper).setActive(true);

        verify(columnGroup).setX(CONTEXT_X_POSITION);

        verify(group).add(columnGroup);
    }
}
