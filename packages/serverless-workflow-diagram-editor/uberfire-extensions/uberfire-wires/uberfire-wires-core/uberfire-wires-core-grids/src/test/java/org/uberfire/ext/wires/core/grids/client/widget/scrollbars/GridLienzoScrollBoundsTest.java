/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.scrollbars;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class GridLienzoScrollBoundsTest {

    @Mock
    private DefaultGridLayer defaultGridLayer;

    @Mock
    private GridLienzoScrollHandler gridLienzoScrollHandler;

    private GridLienzoScrollBounds gridLienzoScrollBounds;

    @Before
    public void setUp() {
        gridLienzoScrollBounds = spy(new GridLienzoScrollBounds(gridLienzoScrollHandler));

        doReturn(defaultGridLayer).when(gridLienzoScrollHandler).getDefaultGridLayer();
    }

    @Test
    public void testMaxBoundXWhenAWidgetHasTheMaximumValue() {

        doReturn(getGridWidgets()).when(defaultGridLayer).getGridWidgets();

        assertEquals(biggestWidget().getX() + biggestWidget().getWidth(),
                     gridLienzoScrollBounds.maxBoundX(),
                     0);
    }

    @Test
    public void testMaxBoundXWhenVisibleBoundsHasTheMaximumValue() {

        final BaseBounds visibleBounds = makeMaxBounds();

        doReturn(true).when(gridLienzoScrollBounds).hasVisibleBounds();
        doReturn(getGridWidgets()).when(defaultGridLayer).getGridWidgets();
        doReturn(visibleBounds).when(defaultGridLayer).getVisibleBounds();

        assertEquals(visibleBounds.getWidth(),
                     gridLienzoScrollBounds.maxBoundX(),
                     0);
    }

    @Test
    public void testMaxBoundXWhenDefaultBoundsHasTheMaximumValue() {

        final BaseBounds defaultBounds = makeMaxBounds();

        doReturn(getGridWidgets()).when(defaultGridLayer).getGridWidgets();
        doReturn(defaultBounds).when(gridLienzoScrollBounds).getDefaultBounds();

        assertEquals(defaultBounds.getWidth(),
                     gridLienzoScrollBounds.maxBoundX(),
                     0);
    }

    @Test
    public void testMaxBoundYWhenAWidgetHasTheMaximumValue() {

        doReturn(getGridWidgets()).when(defaultGridLayer).getGridWidgets();

        assertEquals(biggestWidget().getY() + biggestWidget().getHeight(),
                     gridLienzoScrollBounds.maxBoundY(),
                     0);
    }

    @Test
    public void testMaxBoundYWhenVisibleBoundsHasTheMaximumValue() {

        final BaseBounds visibleBounds = makeMaxBounds();

        doReturn(true).when(gridLienzoScrollBounds).hasVisibleBounds();
        doReturn(getGridWidgets()).when(defaultGridLayer).getGridWidgets();
        doReturn(visibleBounds).when(defaultGridLayer).getVisibleBounds();

        assertEquals(visibleBounds.getHeight(),
                     gridLienzoScrollBounds.maxBoundY(),
                     0);
    }

    @Test
    public void testMaxBoundYWhenDefaultBoundsHasTheMaximumValue() {

        final BaseBounds defaultBounds = makeMaxBounds();

        doReturn(getGridWidgets()).when(defaultGridLayer).getGridWidgets();
        doReturn(defaultBounds).when(gridLienzoScrollBounds).getDefaultBounds();

        assertEquals(defaultBounds.getHeight(),
                     gridLienzoScrollBounds.maxBoundY(),
                     0);
    }

    @Test
    public void testMinBoundXWhenAWidgetHasTheMinimumValue() {

        doReturn(getGridWidgets()).when(defaultGridLayer).getGridWidgets();

        assertEquals(biggestWidget().getX(),
                     gridLienzoScrollBounds.minBoundX(),
                     0);
    }

    @Test
    public void testMinBoundXWhenVisibleBoundsHasTheMinimumValue() {

        final BaseBounds visibleBounds = makeMinBounds();

        doReturn(true).when(gridLienzoScrollBounds).hasVisibleBounds();
        doReturn(getGridWidgets()).when(defaultGridLayer).getGridWidgets();
        doReturn(visibleBounds).when(defaultGridLayer).getVisibleBounds();

        assertEquals(visibleBounds.getX(),
                     gridLienzoScrollBounds.minBoundX(),
                     0);
    }

    @Test
    public void testMinBoundXWhenDefaultBoundsHasTheMinimumValue() {

        final BaseBounds defaultBounds = makeMinBounds();

        doReturn(getGridWidgets()).when(defaultGridLayer).getGridWidgets();
        doReturn(defaultBounds).when(gridLienzoScrollBounds).getDefaultBounds();

        assertEquals(defaultBounds.getX(),
                     gridLienzoScrollBounds.minBoundX(),
                     0);
    }

    @Test
    public void testMinBoundYWhenAWidgetHasTheMinimumValue() {

        doReturn(getGridWidgets()).when(defaultGridLayer).getGridWidgets();

        assertEquals(biggestWidget().getY(),
                     gridLienzoScrollBounds.minBoundY(),
                     0);
    }

    @Test
    public void testMinBoundYWhenVisibleBoundsHasTheMinimumValue() {

        final BaseBounds visibleBounds = makeMinBounds();

        doReturn(true).when(gridLienzoScrollBounds).hasVisibleBounds();
        doReturn(getGridWidgets()).when(defaultGridLayer).getGridWidgets();
        doReturn(visibleBounds).when(defaultGridLayer).getVisibleBounds();

        assertEquals(visibleBounds.getY(),
                     gridLienzoScrollBounds.minBoundY(),
                     0);
    }

    @Test
    public void testMinBoundYWhenDefaultBoundsHasTheMinimumValue() {

        final BaseBounds defaultBounds = makeMinBounds();

        doReturn(getGridWidgets()).when(defaultGridLayer).getGridWidgets();
        doReturn(defaultBounds).when(gridLienzoScrollBounds).getDefaultBounds();

        assertEquals(defaultBounds.getY(),
                     gridLienzoScrollBounds.minBoundY(),
                     0);
    }

    @Test
    public void testSetDefaultBounds() {

        final Bounds bounds = mock(Bounds.class);

        gridLienzoScrollBounds.setDefaultBounds(bounds);

        assertEquals(bounds,
                     gridLienzoScrollBounds.getDefaultBounds());
    }

    @Test
    public void testGetVisibleBounds() {

        final DefaultGridLayer defaultGridLayer = mock(DefaultGridLayer.class);
        final Bounds expectedBounds = mock(Bounds.class);

        doReturn(expectedBounds).when(defaultGridLayer).getVisibleBounds();
        doReturn(defaultGridLayer).when(gridLienzoScrollBounds).getDefaultGridLayer();

        final Bounds actualBounds = gridLienzoScrollBounds.getVisibleBounds();

        assertEquals(expectedBounds,
                     actualBounds);
    }

    @Test
    public void testGetGridWidgets() {

        doReturn(getGridWidgets()).when(defaultGridLayer).getGridWidgets();

        final Set<GridWidget> gridWidgets = gridLienzoScrollBounds.getGridWidgets();

        assertEquals(4,
                     gridWidgets.size());
    }

    @Test
    public void testGetVisibleGridWidgets() {

        doReturn(getGridWidgets()).when(defaultGridLayer).getGridWidgets();

        final Stream<GridWidget> gridWidgets = gridLienzoScrollBounds.getVisibleGridWidgets();

        assertEquals(3,
                     gridWidgets.count());
    }

    @Test
    public void testHasDefaultBoundsWhenDefaultBoundsIsNull() {

        doReturn(null).when(gridLienzoScrollBounds).getDefaultBounds();

        assertFalse(gridLienzoScrollBounds.hasDefaultBounds());
    }

    @Test
    public void testHasDefaultBoundsWhenDefaultBoundsIsNotNull() {

        doReturn(mock(Bounds.class)).when(gridLienzoScrollBounds).getDefaultBounds();

        assertTrue(gridLienzoScrollBounds.hasDefaultBounds());
    }

    @Test
    public void testHasVisibleBoundsWhenViewportIsNull() {

        doReturn(null).when(gridLienzoScrollHandler).getViewport();

        assertFalse(gridLienzoScrollBounds.hasVisibleBounds());
    }

    @Test
    public void testHasVisibleBoundsWhenViewportIsNotNull() {

        doReturn(mock(Viewport.class)).when(defaultGridLayer).getViewport();
        doReturn(defaultGridLayer).when(gridLienzoScrollBounds).getDefaultGridLayer();

        assertTrue(gridLienzoScrollBounds.hasVisibleBounds());
    }

    private GridWidget makeGridWidget(final Double x,
                                      final Double y,
                                      final Double width,
                                      final Double height,
                                      final boolean visible) {

        final GridWidget mock = mock(GridWidget.class);

        when(mock.getX()).thenReturn(x);
        when(mock.getY()).thenReturn(y);
        when(mock.getWidth()).thenReturn(width);
        when(mock.getHeight()).thenReturn(height);
        when(mock.isVisible()).thenReturn(visible);

        return mock;
    }

    private HashSet<GridWidget> getGridWidgets() {
        return new HashSet<GridWidget>() {{
            add(smallestWidget());
            add(mediumWidget());
            add(biggestWidget());
            add(hiddenWidget());
        }};
    }

    private BaseBounds makeMaxBounds() {
        return new BaseBounds(0,
                              0,
                              8000d,
                              6000d);
    }

    private BaseBounds makeMinBounds() {
        return new BaseBounds(-8000d,
                              -6000d,
                              1d,
                              1d);
    }

    private GridWidget biggestWidget() {
        return makeGridWidget(-3840d,
                              -2160d,
                              3840d * 2,
                              2160d * 2,
                              true);
    }

    private GridWidget mediumWidget() {
        return makeGridWidget(-2560d,
                              -1440d,
                              2560d * 2,
                              1440d * 2,
                              true);
    }

    private GridWidget smallestWidget() {
        return makeGridWidget(-1920d,
                              -1080d,
                              1920d * 2,
                              1080d * 2,
                              true);
    }

    private GridWidget hiddenWidget() {
        return makeGridWidget(-3840d,
                              -2160d,
                              3840d * 2,
                              2160d * 2,
                              false);
    }
}
