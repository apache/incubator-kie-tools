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

package org.uberfire.ext.wires.core.grids.client.util;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.GreenTheme;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.DefaultPinnedModeManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CoordinateUtilitiesTest {

    private GridData gridData;

    private GridSelectionManager gridSelectionManager;

    private GridPinnedModeManager gridPinnedModeManager;

    private GridRenderer gridRenderer;

    private Point2D point;

    private Point2D convertedPoint;

    private GridWidget view;

    @Before
    public void setUp() throws Exception {
        gridData = new BaseGridData();
        gridSelectionManager = new DefaultGridLayer();
        gridPinnedModeManager = new DefaultPinnedModeManager((DefaultGridLayer) gridSelectionManager);
        gridRenderer = new BaseGridRenderer(new GreenTheme());
    }

    @Test
    public void testConvertDOMToGridCoordinateNoParent() throws Exception {
        point = new Point2D(15D, 20D);
        view = new BaseGridWidget(gridData, gridSelectionManager, gridPinnedModeManager, gridRenderer);
        convertedPoint = CoordinateUtilities.convertDOMToGridCoordinate(view, point);
        Assertions.assertThat(convertedPoint).isNotNull();
        Assertions.assertThat(convertedPoint.getX()).isEqualTo(15D);
        Assertions.assertThat(convertedPoint.getY()).isEqualTo(20D);
    }

    @Test
    public void testConvertDOMToGridCoordinateWithParentWithoutTransform() throws Exception {
        point = new Point2D(15D, 20D);
        view = spy(new BaseGridWidget(gridData, gridSelectionManager, gridPinnedModeManager, gridRenderer));
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
        view = spy(new BaseGridWidget(gridData, gridSelectionManager, gridPinnedModeManager, gridRenderer));
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
        view = spy(new BaseGridWidget(gridData, gridSelectionManager, gridPinnedModeManager, gridRenderer));
        when(view.getViewport()).thenReturn(viewport);
        when(viewport.getTransform()).thenReturn(transform);
        convertedPoint = CoordinateUtilities.convertDOMToGridCoordinate(view, point);
        Assertions.assertThat(convertedPoint).isNotNull();
        Assertions.assertThat(Double.valueOf(convertedPoint.getX()).intValue()).isEqualTo(-5);
        Assertions.assertThat(Double.valueOf(convertedPoint.getY()).intValue()).isEqualTo(-10);
    }
}
