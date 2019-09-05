/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class GridHighlightHelperTest {

    @Mock
    private GridLienzoPanel gridPanel;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private BaseGridRendererHelper rendererHelper;

    @Mock
    private DefaultGridLayer defaultGridLayer;

    @Mock
    private Bounds visibleBounds;

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    private int column = 2;

    private int row = 4;

    private int x = 64;

    private int y = 128;

    private double columnOffset = 64;

    private double rowOffset = 128;

    private double defaultPaddingX = 1;

    private double defaultPaddingY = 2;

    private double defaultScale = 1;

    private double defaultTranslate = 1;

    private double visibleBoundsX = -800;

    private double visibleBoundsY = -600;

    private double visibleBoundsWidth = 810;

    private double visibleBoundsHeight = 620;

    private GridHighlightHelper highlightHelper;

    @Before
    public void init() {

        highlightHelper = spy(new GridHighlightHelper(gridPanel, gridWidget));
        highlightHelper.withPaddingX(defaultPaddingX);
        highlightHelper.withPaddingY(defaultPaddingY);

        when(gridPanel.getDefaultGridLayer()).thenReturn(defaultGridLayer);
        when(gridWidget.getRendererHelper()).thenReturn(rendererHelper);

        // BaseGridRendererHelper
        when(rendererHelper.getColumnOffset(column)).thenReturn(columnOffset);
        when(rendererHelper.getRowOffset(row)).thenReturn(rowOffset);

        // DefaultGridLayer
        when(defaultGridLayer.getVisibleBounds()).thenReturn(visibleBounds);
        when(defaultGridLayer.getViewport()).thenReturn(viewport);

        // VisibleBounds
        when(visibleBounds.getX()).thenReturn(visibleBoundsX);
        when(visibleBounds.getY()).thenReturn(visibleBoundsY);
        when(visibleBounds.getWidth()).thenReturn(visibleBoundsWidth);
        when(visibleBounds.getHeight()).thenReturn(visibleBoundsHeight);

        // Viewport
        when(viewport.getTransform()).thenReturn(transform);
        when(transform.getScaleY()).thenReturn(defaultScale);
        when(transform.getScaleX()).thenReturn(defaultScale);
        when(transform.getTranslateY()).thenReturn(defaultTranslate);
        when(transform.getTranslateX()).thenReturn(defaultTranslate);
    }

    @Test
    public void testHighlight() {

        doNothing().when(highlightHelper).moveCanvasTo(anyInt(), anyInt());
        doNothing().when(highlightHelper).highlightCell(row, column);

        highlightHelper.highlight(row, column);

        verify(gridWidget).selectCell(row, column, false, false);
        verify(gridWidget).draw();
        verify(highlightHelper).moveCanvasTo(-63, -126);
    }

    @Test
    public void testHighlightWithPinnedGrid() {

        doNothing().when(highlightHelper).moveCanvasTo(anyInt(), anyInt());

        highlightHelper.withPinnedGrid().highlight(row, column);

        verify(gridWidget).selectCell(row, column, false, false);
        verify(gridWidget).draw();
        verify(highlightHelper).moveCanvasTo(-63, -126);
    }

    @Test
    public void testHighlightWithPadding() {

        doNothing().when(highlightHelper).moveCanvasTo(anyInt(), anyInt());

        highlightHelper
                .withPaddingX(128)
                .withPaddingY(64)
                .highlight(row, column);

        verify(gridWidget).selectCell(row, column, false, false);
        verify(gridWidget).draw();
        verify(highlightHelper).moveCanvasTo(64, -64);
    }

    @Test
    public void testMoveCanvasToWhenTheElementIsVisible() {

        final int expectedDeltaX = 0;
        final int expectedDeltaY = 0;
        final Transform copy = mock(Transform.class);
        final Transform newTransform = mock(Transform.class);

        when(transform.copy()).thenReturn(copy);
        when(copy.translate(anyInt(), anyInt())).thenReturn(newTransform);

        highlightHelper.moveCanvasTo(x, y);

        verify(copy).translate(expectedDeltaX, expectedDeltaY);
        verify(viewport).setTransform(newTransform);
        verify(defaultGridLayer).batch();
        verify(gridPanel).refreshScrollPosition();
    }

    @Test
    public void testMoveCanvasToWhenElementIsNotHorizontallyVisible() {

        final int expectedDeltaX = -747;
        final int expectedDeltaY = 0;
        final Transform copy = mock(Transform.class);
        final Transform newTransform = mock(Transform.class);

        when(transform.copy()).thenReturn(copy);
        when(copy.translate(anyInt(), anyInt())).thenReturn(newTransform);

        highlightHelper.moveCanvasTo(x - visibleBoundsWidth, y);

        verify(copy).translate(expectedDeltaX, expectedDeltaY);
        verify(viewport).setTransform(newTransform);
        verify(defaultGridLayer).batch();
        verify(gridPanel).refreshScrollPosition();
    }

    @Test
    public void testMoveCanvasToWhenElementIsNotVerticallyVisible() {

        final int expectedDeltaX = 0;
        final int expectedDeltaY = -493;
        final Transform copy = mock(Transform.class);
        final Transform newTransform = mock(Transform.class);

        when(transform.copy()).thenReturn(copy);
        when(copy.translate(anyInt(), anyInt())).thenReturn(newTransform);

        highlightHelper.moveCanvasTo(x, y - visibleBoundsHeight);

        verify(copy).translate(expectedDeltaX, expectedDeltaY);
        verify(viewport).setTransform(newTransform);
        verify(defaultGridLayer).batch();
        verify(gridPanel).refreshScrollPosition();
    }

    @Test
    public void testClearSelections() {

        final GridData model = mock(GridData.class);

        when(gridWidget.getModel()).thenReturn(model);

        highlightHelper.clearSelections();

        verify(model).clearSelections();
        verify(gridWidget).draw();
    }

    @Test
    public void testCalculateRowOffset() {

        final double widgetY = -32;
        final double paddingY = 20;
        final double expected = -(widgetY + rowOffset - paddingY);

        when(highlightHelper.getPaddingY()).thenReturn(paddingY);
        when(gridWidget.getY()).thenReturn(widgetY);

        final double actual = highlightHelper.calculateRowOffset(row);

        assertEquals(expected, actual, 0.01d);
    }

    @Test
    public void testCalculateColumnOffset() {

        final double widgetX = -67;
        final double paddingX = 12;
        final double expected = -(widgetX + columnOffset - paddingX);

        when(highlightHelper.getPaddingX()).thenReturn(paddingX);
        when(gridWidget.getX()).thenReturn(widgetX);

        final double actual = highlightHelper.calculateColumnOffset(column);

        assertEquals(expected, actual, 0.01d);
    }

    @Test
    public void testClearHighlight() {

        final BaseGridRenderer renderer = mock(BaseGridRenderer.class);
        when(gridWidget.getRenderer()).thenReturn(renderer);

        highlightHelper.clearHighlight();

        verify(renderer).clearCellHighlight();
        verify(gridWidget).draw();
    }

    @Test
    public void testHighlightCell() {

        final BaseGridRenderer renderer = mock(BaseGridRenderer.class);
        when(gridWidget.getRenderer()).thenReturn(renderer);

        highlightHelper.highlightCell(row, column);

        verify(renderer).highlightCell(column, row);
    }
}
