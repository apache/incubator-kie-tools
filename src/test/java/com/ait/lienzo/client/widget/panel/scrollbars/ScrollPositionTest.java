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

package com.ait.lienzo.client.widget.panel.scrollbars;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(LienzoMockitoTestRunner.class)
public class ScrollPositionTest {

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
    private ScrollablePanelHandler scrollHandler;

    @Mock
    private ScrollBounds scrollBounds;

    @Mock
    private ScrollablePanel scrollablePanel;

    private ScrollPosition scrollPosition;

    @Before
    public void setUp() {

        scrollPosition = spy(new ScrollPosition(scrollHandler));

        doReturn(makeTransform()).when(scrollPosition).getTransform();
        doReturn(makeVisibleBounds()).when(scrollPosition).getVisibleBounds();
        doReturn(makeScrollBoundsHelper()).when(scrollPosition).bounds();
        doReturn(scrollablePanel).when(scrollHandler).getPanel();
        doReturn(scrollBounds).when(scrollHandler).scrollBounds();
    }

    @Test
    public void testGetCurrentXLevel() {

        final Double actualLevel = scrollPosition.currentRelativeX();
        final Double expectedLevel = 100 * CURRENT_X / DELTA_X;

        assertEquals(expectedLevel,
                     actualLevel,
                     0);
    }

    @Test
    public void testGetCurrentXLevelWhenDeltaXIsZero() {

        doReturn(0d).when(scrollPosition).deltaX();

        final Double actualLevel = scrollPosition.currentRelativeX();
        final Double expectedLevel = 0d;

        assertEquals(expectedLevel,
                     actualLevel,
                     0);
    }

    @Test
    public void testGetCurrentYLevel() {

        final Double actualLevel = scrollPosition.currentRelativeY();
        final Double expectedLevel = 100 * CURRENT_Y / DELTA_Y;

        assertEquals(actualLevel,
                     expectedLevel,
                     0);
    }

    @Test
    public void testGetCurrentYLevelWhenDeltaYIsZero() {

        doReturn(0d).when(scrollPosition).deltaY();

        final Double actualLevel = scrollPosition.currentRelativeY();
        final Double expectedLevel = 0d;

        assertEquals(expectedLevel,
                     actualLevel,
                     0);
    }

    @Test
    public void testCurrentXPosition() {

        final Double level = 46.66d;
        final Double expectedPosition = -(MIN_BOUND_X + (DELTA_X * level / 100));
        final Double actualPosition = scrollPosition.currentPositionX(level);

        assertEquals(expectedPosition,
                     actualPosition,
                     0);
    }

    @Test
    public void testCurrentYPosition() {

        final Double level = 37.14d;
        final Double expectedPosition = -(MIN_BOUND_Y + (DELTA_Y * level / 100));
        final Double actualPosition = scrollPosition.currentPositionY(level);

        assertEquals(expectedPosition,
                     actualPosition,
                     0);
    }

    @Test
    public void testGetVisibleBounds() {

        final Bounds expectedBounds = mock(Bounds.class);

        doReturn(expectedBounds).when(scrollablePanel).getVisibleBounds();
        doCallRealMethod().when(scrollPosition).getVisibleBounds();

        final Bounds actualBounds = scrollPosition.getVisibleBounds();

        assertEquals(expectedBounds,
                     actualBounds);
    }

    @Test
    public void testGetTransform() {

        final Layer layer = mock(Layer.class);
        final Viewport viewport = mock(Viewport.class);
        final Transform expectedTransform = mock(Transform.class);

        doReturn(layer).when(scrollPosition).getLayer();
        doReturn(viewport).when(layer).getViewport();
        doReturn(expectedTransform).when(viewport).getTransform();
        doCallRealMethod().when(scrollPosition).getTransform();

        final Transform actualTransform = scrollPosition.getTransform();

        assertEquals(expectedTransform,
                     actualTransform);
    }

    @Test
    public void testBounds() {

        doCallRealMethod().when(scrollPosition).bounds();

        assertTrue(scrollPosition.bounds() != null);
    }

    private ScrollBounds makeScrollBoundsHelper() {

        final ScrollBounds scrollBounds = mock(ScrollBounds.class);

        doReturn(MAX_BOUND_X).when(scrollBounds).maxBoundX();
        doReturn(MAX_BOUND_Y).when(scrollBounds).maxBoundY();
        doReturn(MIN_BOUND_X).when(scrollBounds).minBoundX();
        doReturn(MIN_BOUND_Y).when(scrollBounds).minBoundY();

        return scrollBounds;
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
