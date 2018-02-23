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

package org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl;

import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class GridTransformMediatorTest {

    private GridTransformMediator restriction;

    @Mock
    private GridWidget grid;

    @Before
    public void setup() {
        this.restriction = new GridTransformMediator(grid);
    }

    @Test
    public void testGridNarrowerThanVisibleBounds() {
        when(grid.getX()).thenReturn(10.0);
        when(grid.getY()).thenReturn(0.0);
        when(grid.getWidth()).thenReturn(100.0);
        when(grid.getHeight()).thenReturn(100.0);

        final Bounds visibleBounds = new BaseBounds(-500,
                                                    -500,
                                                    1000,
                                                    1000);
        final Transform test = new Transform().translate(1200.0,
                                                         0.0);
        final Transform result = restriction.adjust(test,
                                                    visibleBounds);

        assertNotNull(result);
        assertEquals(-10.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(0.0,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testGridShorterThanVisibleBounds() {
        when(grid.getX()).thenReturn(0.0);
        when(grid.getY()).thenReturn(10.0);
        when(grid.getWidth()).thenReturn(100.0);
        when(grid.getHeight()).thenReturn(100.0);

        final Bounds visibleBounds = new BaseBounds(-500,
                                                    -500,
                                                    1000,
                                                    1000);
        final Transform test = new Transform().translate(1200.0,
                                                         0.0);
        final Transform result = restriction.adjust(test,
                                                    visibleBounds);

        assertNotNull(result);
        assertEquals(0.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(-10.0,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testGridWiderThanVisibleBoundsLeftEdge() {
        when(grid.getX()).thenReturn(10.0);
        when(grid.getY()).thenReturn(0.0);
        when(grid.getWidth()).thenReturn(1100.0);
        when(grid.getHeight()).thenReturn(100.0);

        final Bounds visibleBounds = new BaseBounds(-500,
                                                    -500,
                                                    1000,
                                                    1000);
        final Transform test = new Transform().translate(1200.0,
                                                         0.0);
        final Transform result = restriction.adjust(test,
                                                    visibleBounds);

        assertNotNull(result);
        assertEquals(-10.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(0.0,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testGridWiderThanVisibleBoundsRightEdge() {
        when(grid.getX()).thenReturn(10.0);
        when(grid.getY()).thenReturn(0.0);
        when(grid.getWidth()).thenReturn(1100.0);
        when(grid.getHeight()).thenReturn(100.0);

        final Bounds visibleBounds = new BaseBounds(-500,
                                                    -500,
                                                    1000,
                                                    1000);
        final Transform test = new Transform().translate(-200.0,
                                                         0.0);
        final Transform result = restriction.adjust(test,
                                                    visibleBounds);

        assertNotNull(result);
        assertEquals(-110.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(0.0,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testGridTallerThanVisibleBoundsTopEdge() {
        when(grid.getX()).thenReturn(0.0);
        when(grid.getY()).thenReturn(10.0);
        when(grid.getWidth()).thenReturn(100.0);
        when(grid.getHeight()).thenReturn(1100.0);

        final Bounds visibleBounds = new BaseBounds(-500,
                                                    -500,
                                                    1000,
                                                    1000);
        final Transform test = new Transform().translate(0.0,
                                                         1200.0);
        final Transform result = restriction.adjust(test,
                                                    visibleBounds);

        assertNotNull(result);
        assertEquals(0.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(-10.0,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testGridTallerThanVisibleBoundsBottomEdge() {
        when(grid.getX()).thenReturn(0.0);
        when(grid.getY()).thenReturn(10.0);
        when(grid.getWidth()).thenReturn(100.0);
        when(grid.getHeight()).thenReturn(1100.0);

        final Bounds visibleBounds = new BaseBounds(-500,
                                                    -500,
                                                    1000,
                                                    1000);
        final Transform test = new Transform().translate(0.0,
                                                         -200.0);
        final Transform result = restriction.adjust(test,
                                                    visibleBounds);

        assertNotNull(result);
        assertEquals(0.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(-110.0,
                     result.getTranslateY(),
                     0.0);
    }
}
