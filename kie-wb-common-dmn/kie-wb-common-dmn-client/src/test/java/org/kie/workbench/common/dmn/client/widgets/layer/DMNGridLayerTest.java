/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.layer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.GwtMockito;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionContainerGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.dnd.DelegatingGridWidgetDndMouseMoveHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNGridLayerTest {

    private static final double CONTAINER_WIDTH = 1000.0;

    private static final double CONTAINER_HEIGHT = 500.0;

    private static final double CONTAINER_X = 25.0;

    private static final double CONTAINER_Y = 50.0;

    private static final double VIEWPORT_TRANSLATE_X = 15.0;

    private static final double VIEWPORT_TRANSLATE_Y = 35.0;

    @Mock
    private Context2D context2D;

    @Mock
    private ExpressionContainerGrid container;

    @Mock
    private BaseExpressionGrid expressionGrid;

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    @Mock
    private Group ghostGroup;

    @Mock
    private Rectangle ghostRectangle;

    @Captor
    private ArgumentCaptor<GridLayerRedrawManager.PrioritizedCommand> drawCommand;

    private DMNGridLayer gridLayer;

    @Before
    public void setup() {
        this.gridLayer = spy(new TestDMNGridLayer(context2D));

        doReturn(viewport).when(gridLayer).getViewport();
        when(viewport.getTransform()).thenReturn(transform);
        when(container.getWidth()).thenReturn(CONTAINER_WIDTH);
        when(container.getHeight()).thenReturn(CONTAINER_HEIGHT);
        when(container.getX()).thenReturn(CONTAINER_X);
        when(container.getY()).thenReturn(CONTAINER_Y);
        when(transform.getTranslateX()).thenReturn(VIEWPORT_TRANSLATE_X);
        when(transform.getTranslateY()).thenReturn(VIEWPORT_TRANSLATE_Y);
    }

    @Test
    public void checkGridWidgetDnDMouseMoveHandler() {
        assertTrue(gridLayer.getGridWidgetDnDMouseMoveHandler() instanceof DelegatingGridWidgetDndMouseMoveHandler);
    }

    @Test
    public void testDrawDelegatesToBatch() {
        gridLayer.draw();

        verify(gridLayer).batch();
    }

    @Test
    public void testBatchDelegatesToDoBatch() {
        gridLayer.batch();

        verify(gridLayer).batch(drawCommand.capture());

        drawCommand.getValue().execute();

        verify(gridLayer).doBatch();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoGhostAddedWhenNoContainerFound() {
        gridLayer.doBatch();

        verify(gridLayer, never()).addGhost(any(ExpressionContainerGrid.class), any(GridWidget.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoGhostAddedWhenContainerFoundButNoExpressionGridFound() {
        doReturn(Collections.singleton(container)).when(gridLayer).getGridWidgets();

        gridLayer.doBatch();

        verify(gridLayer, never()).addGhost(any(ExpressionContainerGrid.class), any(GridWidget.class));
    }

    @Test
    public void testGhostAddedWhenContainerFoundAndExpressionGridFound() {
        doReturn(new HashSet<>(Arrays.asList(container, expressionGrid))).when(gridLayer).getGridWidgets();

        gridLayer.select(expressionGrid);

        gridLayer.doBatch();

        verify(gridLayer).addGhost(eq(container), eq(expressionGrid));
    }

    @Test
    public void testGhostRendering() {
        GwtMockito.useProviderForType(Group.class, clazz -> ghostGroup);
        doReturn(new HashSet<>(Arrays.asList(container, expressionGrid))).when(gridLayer).getGridWidgets();
        doReturn(ghostRectangle).when(gridLayer).getGhostRectangle();

        gridLayer.select(expressionGrid);

        gridLayer.doBatch();

        assertGhostRendering();
    }

    @Test
    public void testGhostRenderingLiteralExpression() {
        final LiteralExpressionGrid literalExpressionGrid = mock(LiteralExpressionGrid.class);
        final GridCellTuple parentGridCellTuple = new GridCellTuple(0, 0, container);
        GwtMockito.useProviderForType(Group.class, clazz -> ghostGroup);
        doReturn(new HashSet<>(Arrays.asList(container, literalExpressionGrid))).when(gridLayer).getGridWidgets();
        doReturn(ghostRectangle).when(gridLayer).getGhostRectangle();
        doReturn(parentGridCellTuple).when(literalExpressionGrid).getParentInformation();

        gridLayer.select(literalExpressionGrid);

        gridLayer.doBatch();

        assertGhostRendering();
    }

    @Test
    public void testGhostRenderingUndefinedExpression() {
        final UndefinedExpressionGrid undefinedExpressionGrid = mock(UndefinedExpressionGrid.class);
        final GridCellTuple parentGridCellTuple = new GridCellTuple(0, 0, container);
        GwtMockito.useProviderForType(Group.class, clazz -> ghostGroup);
        doReturn(new HashSet<>(Arrays.asList(container, undefinedExpressionGrid))).when(gridLayer).getGridWidgets();
        doReturn(ghostRectangle).when(gridLayer).getGhostRectangle();
        doReturn(parentGridCellTuple).when(undefinedExpressionGrid).getParentInformation();

        gridLayer.select(undefinedExpressionGrid);

        gridLayer.doBatch();

        assertGhostRendering();
    }

    private void assertGhostRendering() {
        verify(ghostRectangle).setWidth(eq(CONTAINER_WIDTH + BaseExpressionGridTheme.STROKE_WIDTH));
        verify(ghostRectangle).setHeight(eq(CONTAINER_HEIGHT + BaseExpressionGridTheme.STROKE_WIDTH));
        verify(ghostRectangle).setFillColor(ColorName.WHITE);
        verify(ghostRectangle).setAlpha(0.50);
        verify(ghostRectangle).setListening(false);

        verify(ghostGroup).setX(CONTAINER_X + VIEWPORT_TRANSLATE_X);
        verify(ghostGroup).setY(CONTAINER_Y + VIEWPORT_TRANSLATE_Y);
        verify(ghostGroup).setPathClipper(any(InverseGridWidgetClipper.class));
        verify(ghostGroup).add(ghostRectangle);
        verify(ghostGroup).drawWithTransforms(eq(context2D), anyDouble(), any(BoundingBox.class));
    }

    @Test
    public void testSelectGridWidget() {
        when(expressionGrid.getModel()).thenReturn(new BaseGridData(false));
        when(expressionGrid.asNode()).thenReturn(mock(Node.class));

        assertThat(gridLayer.getSelectedGridWidget().isPresent()).isFalse();

        gridLayer.add(expressionGrid);

        gridLayer.select(expressionGrid);

        assertThat(gridLayer.getSelectedGridWidget().isPresent()).isTrue();
        assertThat(gridLayer.getSelectedGridWidget().get()).isEqualTo(expressionGrid);
        verify(expressionGrid).select();
    }

    @Test
    public void testSelectNestedGridWidget() {
        final GridWidget gridWidget = mock(GridWidget.class);
        final GridData gridData = new BaseGridData(false);
        gridData.appendRow(new BaseGridRow());
        gridData.appendColumn(mock(GridColumn.class));
        gridData.setCellValue(0, 0, new ExpressionCellValue(Optional.of(expressionGrid)));

        gridLayer.register(gridWidget);
        gridLayer.register(expressionGrid);

        assertThat(gridLayer.getSelectedGridWidget().isPresent()).isFalse();

        //Select nested grid
        when(gridWidget.getModel()).thenReturn(gridData);
        when(expressionGrid.getModel()).thenReturn(new BaseGridData(false));
        gridLayer.select(expressionGrid);

        assertThat(gridLayer.getSelectedGridWidget().isPresent()).isTrue();
        assertThat(gridLayer.getSelectedGridWidget().get()).isEqualTo(expressionGrid);

        verify(expressionGrid).select();

        //Select outer grid, deselecting nested grid
        reset(gridWidget, expressionGrid);
        when(gridWidget.getModel()).thenReturn(gridData);
        when(expressionGrid.getModel()).thenReturn(new BaseGridData(false));
        when(expressionGrid.isSelected()).thenReturn(true);
        gridLayer.select(gridWidget);

        assertThat(gridLayer.getSelectedGridWidget().isPresent()).isTrue();
        assertThat(gridLayer.getSelectedGridWidget().get()).isEqualTo(gridWidget);

        verify(gridWidget).select();
        verify(expressionGrid).deselect();
    }

    @Test
    public void testDeregister() {
        final GridWidget gridWidget = mock(GridWidget.class);

        gridLayer.select(gridWidget);

        gridLayer.deregister(expressionGrid);

        assertThat(gridLayer.getSelectedGridWidget().isPresent()).isTrue();
        assertThat(gridLayer.getSelectedGridWidget().get()).isEqualTo(gridWidget);

        gridLayer.deregister(gridWidget);

        assertThat(gridLayer.getSelectedGridWidget().isPresent()).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemove() {
        final GridWidget gridWidget = mock(GridWidget.class);
        when(gridWidget.getModel()).thenReturn(new BaseGridData(false));
        when(expressionGrid.getModel()).thenReturn(new BaseGridData(false));
        when(gridWidget.asNode()).thenReturn(mock(Node.class));
        when(expressionGrid.asNode()).thenReturn(mock(Node.class));

        gridLayer.add(gridWidget);
        gridLayer.add(expressionGrid);
        gridLayer.select(expressionGrid);

        gridLayer.remove(gridWidget);

        assertThat(gridLayer.getSelectedGridWidget().isPresent()).isTrue();
        assertThat(gridLayer.getSelectedGridWidget().get()).isEqualTo(expressionGrid);

        gridLayer.remove(expressionGrid);

        assertThat(gridLayer.getSelectedGridWidget().isPresent()).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveAll() {
        final GridWidget gridWidget = mock(GridWidget.class);
        when(gridWidget.getModel()).thenReturn(new BaseGridData(false));
        when(expressionGrid.getModel()).thenReturn(new BaseGridData(false));
        when(gridWidget.asNode()).thenReturn(mock(Node.class));
        when(expressionGrid.asNode()).thenReturn(mock(Node.class));

        gridLayer.add(gridWidget);
        gridLayer.add(expressionGrid);
        gridLayer.select(expressionGrid);

        gridLayer.removeAll();

        assertThat(gridLayer.getSelectedGridWidget().isPresent()).isFalse();
    }

    private static class TestDMNGridLayer extends DMNGridLayer {

        private Context2D context2D;

        private TestDMNGridLayer(final Context2D context2D) {
            this.context2D = context2D;
        }

        @Override
        public Context2D getContext() {
            return context2D;
        }
    }
}
