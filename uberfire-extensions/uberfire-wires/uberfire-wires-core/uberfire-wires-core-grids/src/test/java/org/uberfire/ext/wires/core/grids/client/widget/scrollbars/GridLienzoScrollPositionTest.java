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

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(GwtMockitoTestRunner.class)
public class GridLienzoScrollPositionTest {

    private static final Double MAX_BOUND_X = 2000d;

    private static final Double MAX_BOUND_Y = 2000d;

    private static final Double MIN_BOUND_X = -2000d;

    private static final Double MIN_BOUND_Y = -2000d;

    private static final Double VISIBLE_BOUND_WIDTH = 2500d;

    private static final Double VISIBLE_BOUND_HEIGHT = 500d;

    private static final Double TRANSLATE_X = 1300d;

    private static final Double TRANSLATE_Y = 700d;

    private static final Double SCALE_X = 1d;

    private static final Double SCALE_Y = 1d;

    private static final Double CURRENT_Y = -(TRANSLATE_Y / SCALE_Y + MIN_BOUND_Y);

    private static final Double CURRENT_X = -(TRANSLATE_X / SCALE_X + MIN_BOUND_X);

    private static final Double DELTA_Y = MAX_BOUND_Y - MIN_BOUND_Y - VISIBLE_BOUND_HEIGHT;

    private static final Double DELTA_X = MAX_BOUND_X - MIN_BOUND_X - VISIBLE_BOUND_WIDTH;

    @Mock
    private GridLienzoScrollHandler gridLienzoScrollHandler;

    @Mock
    private DefaultGridLayer defaultGridLayer;

    @Mock
    private GridLienzoScrollBounds scrollBounds;

    private GridLienzoScrollPosition gridLienzoScrollPosition;

    @Before
    public void setUp() {

        gridLienzoScrollPosition = spy(new GridLienzoScrollPosition(gridLienzoScrollHandler));

        doReturn(makeTransform()).when(gridLienzoScrollPosition).getTransform();
        doReturn(makeVisibleBounds()).when(gridLienzoScrollPosition).getVisibleBounds();
        doReturn(makeScrollBoundsHelper()).when(gridLienzoScrollPosition).bounds();
        doReturn(defaultGridLayer).when(gridLienzoScrollHandler).getDefaultGridLayer();
        doReturn(scrollBounds).when(gridLienzoScrollHandler).scrollBounds();
    }

    @Test
    public void testGetCurrentXLevel() {

        final Double actualLevel = gridLienzoScrollPosition.currentRelativeX();
        final Double expectedLevel = 100 * CURRENT_X / DELTA_X;

        assertEquals(expectedLevel,
                     actualLevel,
                     0);
    }

    @Test
    public void testGetCurrentXLevelWhenDeltaXIsZero() {

        doReturn(0d).when(gridLienzoScrollPosition).deltaX();

        final Double actualLevel = gridLienzoScrollPosition.currentRelativeX();
        final Double expectedLevel = 0d;

        assertEquals(expectedLevel,
                     actualLevel,
                     0);
    }

    @Test
    public void testGetCurrentYLevel() {

        final Double actualLevel = gridLienzoScrollPosition.currentRelativeY();
        final Double expectedLevel = 100 * CURRENT_Y / DELTA_Y;

        assertEquals(actualLevel,
                     expectedLevel,
                     0);
    }

    @Test
    public void testGetCurrentYLevelWhenDeltaYIsZero() {

        doReturn(0d).when(gridLienzoScrollPosition).deltaY();

        final Double actualLevel = gridLienzoScrollPosition.currentRelativeY();
        final Double expectedLevel = 0d;

        assertEquals(expectedLevel,
                     actualLevel,
                     0);
    }

    @Test
    public void testCurrentXPosition() {

        final Double level = 46.66d;
        final Double expectedPosition = -(MIN_BOUND_X + (DELTA_X * level / 100));
        final Double actualPosition = gridLienzoScrollPosition.currentPositionX(level);

        assertEquals(expectedPosition,
                     actualPosition,
                     0);
    }

    @Test
    public void testCurrentYPosition() {

        final Double level = 37.14d;
        final Double expectedPosition = -(MIN_BOUND_Y + (DELTA_Y * level / 100));
        final Double actualPosition = gridLienzoScrollPosition.currentPositionY(level);

        assertEquals(expectedPosition,
                     actualPosition,
                     0);
    }

    @Test
    public void testGetVisibleBounds() {

        final Bounds expectedBounds = mock(Bounds.class);

        doReturn(expectedBounds).when(defaultGridLayer).getVisibleBounds();
        doCallRealMethod().when(gridLienzoScrollPosition).getVisibleBounds();

        final Bounds actualBounds = gridLienzoScrollPosition.getVisibleBounds();

        assertEquals(expectedBounds,
                     actualBounds);
    }

    @Test
    public void testGetTransform() {

        final Viewport viewport = mock(Viewport.class);
        final Transform expectedTransform = mock(Transform.class);

        doReturn(viewport).when(defaultGridLayer).getViewport();
        doReturn(expectedTransform).when(viewport).getTransform();
        doCallRealMethod().when(gridLienzoScrollPosition).getTransform();

        final Transform actualTransform = gridLienzoScrollPosition.getTransform();

        assertEquals(expectedTransform,
                     actualTransform);
    }

    @Test
    public void testBounds() {

        doCallRealMethod().when(gridLienzoScrollPosition).bounds();

        assertTrue(gridLienzoScrollPosition.bounds() != null);
    }

    private GridLienzoScrollBounds makeScrollBoundsHelper() {

        final GridLienzoScrollBounds gridLienzoScrollBounds = mock(GridLienzoScrollBounds.class);

        doReturn(MAX_BOUND_X).when(gridLienzoScrollBounds).maxBoundX();
        doReturn(MAX_BOUND_Y).when(gridLienzoScrollBounds).maxBoundY();
        doReturn(MIN_BOUND_X).when(gridLienzoScrollBounds).minBoundX();
        doReturn(MIN_BOUND_Y).when(gridLienzoScrollBounds).minBoundY();

        return gridLienzoScrollBounds;
    }

    private Bounds makeVisibleBounds() {

        final Bounds bounds = mock(Bounds.class);

        doReturn(VISIBLE_BOUND_WIDTH).when(bounds).getWidth();
        doReturn(VISIBLE_BOUND_HEIGHT).when(bounds).getHeight();

        return bounds;
    }

    private Transform makeTransform() {

        final Transform transform = mock(Transform.class);

        doReturn(TRANSLATE_X).when(transform).getTranslateX();
        doReturn(TRANSLATE_Y).when(transform).getTranslateY();
        doReturn(SCALE_X).when(transform).getScaleX();
        doReturn(SCALE_Y).when(transform).getScaleY();

        return transform;
    }
}
