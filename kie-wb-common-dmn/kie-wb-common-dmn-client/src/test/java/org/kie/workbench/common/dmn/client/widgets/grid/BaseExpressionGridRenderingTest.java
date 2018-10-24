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

package org.kie.workbench.common.dmn.client.widgets.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBoundaryRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.GridRendererContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderBodyGridBackgroundCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderBodyGridContentCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderBodyGridLinesCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderGridBoundaryCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderHeaderBackgroundCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderHeaderContentCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderHeaderGridLinesCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderSelectedCellsCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderSelectorCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RendererCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseExpressionGridRenderingTest extends BaseExpressionGridTest {

    @Mock
    private Context2D context;

    @Mock
    private RenderSelectorCommand renderSelectorCommand;

    @Mock
    private RenderSelectedCellsCommand renderSelectedCellsCommand;

    @Mock
    private RenderHeaderBackgroundCommand renderHeaderBackgroundCommand;

    @Mock
    private RenderHeaderGridLinesCommand renderHeaderGridLinesCommand;

    @Mock
    private RenderHeaderContentCommand renderHeaderContentCommand;

    @Mock
    private RenderBodyGridBackgroundCommand renderBodyGridBackgroundCommand;

    @Mock
    private RenderBodyGridLinesCommand renderBodyGridLinesCommand;

    @Mock
    private RenderBodyGridContentCommand renderBodyGridContentCommand;

    @Mock
    private RenderHeaderGridLinesCommand renderHeaderBodyDividerCommand;

    @Mock
    private RenderGridBoundaryCommand renderGridBoundaryCommand;

    private boolean isHeaderHidden;

    @Before
    @Override
    @SuppressWarnings("unchecked")
    public void setup() {
        super.setup();

        doReturn(false).when(context).isSelection();
        doReturn(new BaseBounds(0, 0, 1000, 1000)).when(gridLayer).getVisibleBounds();

        grid.getModel().appendColumn(new RowNumberColumn());
        grid.getModel().appendRow(new DMNGridRow());

        doReturn(renderSelectorCommand).when(renderer).renderSelector(anyDouble(),
                                                                      anyDouble(),
                                                                      any(BaseGridRendererHelper.RenderingInformation.class));

        doReturn(renderSelectedCellsCommand).when(renderer).renderSelectedCells(any(GridData.class),
                                                                                any(GridBodyRenderContext.class),
                                                                                any(BaseGridRendererHelper.class),
                                                                                any(List.class),
                                                                                any(BiFunction.class),
                                                                                any(Function.class));

        final List<RendererCommand> renderHeaderCommands = new ArrayList<>();
        renderHeaderCommands.add(renderHeaderBackgroundCommand);
        renderHeaderCommands.add(renderHeaderGridLinesCommand);
        renderHeaderCommands.add(renderHeaderContentCommand);
        doReturn(renderHeaderCommands).when(renderer).renderHeader(any(GridData.class),
                                                                   any(GridHeaderRenderContext.class),
                                                                   any(BaseGridRendererHelper.class),
                                                                   any(BaseGridRendererHelper.RenderingInformation.class));

        final List<RendererCommand> renderBodyCommands = new ArrayList<>();
        renderBodyCommands.add(renderBodyGridBackgroundCommand);
        renderBodyCommands.add(renderBodyGridLinesCommand);
        renderBodyCommands.add(renderBodyGridContentCommand);
        doReturn(renderBodyCommands).when(renderer).renderBody(any(GridData.class),
                                                               any(GridBodyRenderContext.class),
                                                               any(BaseGridRendererHelper.class),
                                                               any(BaseGridRendererHelper.RenderingInformation.class));

        doReturn(renderHeaderBodyDividerCommand).when(renderer).renderHeaderBodyDivider(anyDouble());

        doReturn(renderGridBoundaryCommand).when(renderer).renderGridBoundary(any(GridBoundaryRenderContext.class));

        doReturn(BaseExpressionGridRenderer.HEADER_HEIGHT).when(renderer).getHeaderHeight();
    }

    @Override
    @SuppressWarnings("unchecked")
    public BaseExpressionGrid getGrid() {
        final GridCellTuple parent = new GridCellTuple(0, 0, null);
        final HasExpression hasExpression = mock(HasExpression.class);
        final Optional<LiteralExpression> expression = Optional.of(mock(LiteralExpression.class));
        final Optional<HasName> hasName = Optional.of(mock(HasName.class));

        return new BaseExpressionGrid(parent,
                                      Optional.empty(),
                                      hasExpression,
                                      expression,
                                      hasName,
                                      gridPanel,
                                      gridLayer,
                                      new DMNGridData(),
                                      renderer,
                                      definitionUtils,
                                      sessionManager,
                                      sessionCommandManager,
                                      canvasCommandFactory,
                                      editorSelectedEvent,
                                      domainObjectSelectionEvent,
                                      cellEditorControls,
                                      listSelector,
                                      translationService,
                                      0) {
            @Override
            protected BaseUIModelMapper makeUiModelMapper() {
                return mapper;
            }

            @Override
            protected void initialiseUiColumns() {
                //Nothing for this test
            }

            @Override
            protected void initialiseUiModel() {
                //Nothing for this test
            }

            @Override
            protected boolean isHeaderHidden() {
                return isHeaderHidden;
            }
        };
    }

    @Test
    public void testRenderQueueCommandsWhenHeaderIsHidden() {
        this.isHeaderHidden = true;

        grid.drawWithTransforms(context, 1.0, new BoundingBox());

        verify(renderHeaderBackgroundCommand, never()).execute(any(GridRendererContext.class));
        verify(renderHeaderGridLinesCommand, never()).execute(any(GridRendererContext.class));
        verify(renderHeaderContentCommand, never()).execute(any(GridRendererContext.class));
        verify(renderHeaderBodyDividerCommand, never()).execute(any(GridRendererContext.class));

        final InOrder order = inOrder(renderBodyGridBackgroundCommand,
                                      renderBodyGridContentCommand,
                                      renderBodyGridLinesCommand,
                                      renderGridBoundaryCommand,
                                      renderSelectedCellsCommand);

        order.verify(renderBodyGridBackgroundCommand).execute(any(GridRendererContext.class));
        order.verify(renderBodyGridContentCommand).execute(any(GridRendererContext.class));
        order.verify(renderGridBoundaryCommand).execute(any(GridRendererContext.class));
        order.verify(renderBodyGridLinesCommand).execute(any(GridRendererContext.class));
        //Render header selections and body selections
        order.verify(renderSelectedCellsCommand, times(2)).execute(any(GridRendererContext.class));
    }

    @Test
    public void testRenderQueueCommandsWhenHeaderIsNotHidden() {
        this.isHeaderHidden = false;

        grid.drawWithTransforms(context, 1.0, new BoundingBox());

        final InOrder order = inOrder(renderHeaderBackgroundCommand,
                                      renderHeaderContentCommand,
                                      renderBodyGridBackgroundCommand,
                                      renderBodyGridContentCommand,
                                      renderGridBoundaryCommand,
                                      renderHeaderGridLinesCommand,
                                      renderBodyGridLinesCommand,
                                      renderSelectedCellsCommand);

        order.verify(renderHeaderBackgroundCommand).execute(any(GridRendererContext.class));
        order.verify(renderHeaderContentCommand).execute(any(GridRendererContext.class));
        order.verify(renderBodyGridBackgroundCommand).execute(any(GridRendererContext.class));
        order.verify(renderBodyGridContentCommand).execute(any(GridRendererContext.class));
        order.verify(renderGridBoundaryCommand).execute(any(GridRendererContext.class));
        order.verify(renderHeaderGridLinesCommand).execute(any(GridRendererContext.class));
        order.verify(renderBodyGridLinesCommand).execute(any(GridRendererContext.class));
        //Render header selections and body selections
        order.verify(renderSelectedCellsCommand, times(2)).execute(any(GridRendererContext.class));
    }

    @Test
    public void testHeaderDimensionsWhenHeaderNotHidden() {
        final GridRenderer renderer = new BaseExpressionGridRenderer(false);

        assertEquals(BaseExpressionGridRenderer.HEADER_HEIGHT,
                     renderer.getHeaderHeight(),
                     0.0);
        assertEquals(BaseExpressionGridRenderer.HEADER_ROW_HEIGHT,
                     renderer.getHeaderRowHeight(),
                     0.0);
    }

    @Test
    public void testHeaderDimensionsWhenHeaderIsNotHidden() {
        final GridRenderer renderer = new BaseExpressionGridRenderer(true);

        assertEquals(0.0,
                     renderer.getHeaderHeight(),
                     0.0);
        assertEquals(0.0,
                     renderer.getHeaderRowHeight(),
                     0.0);
    }
}
