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

package com.ait.lienzo.client.widget.panel.mediators;

import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(LienzoMockitoTestRunner.class)
public class BoundaryTransformMediatorTest {

    private final Bounds bounds = Bounds.build(-1000,
                                               -1000,
                                               2000,
                                               2000);

    private BoundaryTransformMediator restriction;

    @Before
    public void setup() {
        this.restriction = new BoundaryTransformMediator(bounds);
    }

    @Test
    public void testLeftEdge() {
        final Bounds visibleBounds = Bounds.build(-500,
                                                  -500,
                                                  1000,
                                                  1000);
        final Transform test = new Transform().translate(1200.0,
                                                         0.0);
        final Transform result = restriction.adjust(test,
                                                    visibleBounds);

        assertNotNull(result);
        assertEquals(1000.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(0.0,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testRightEdge() {
        final Bounds visibleBounds = Bounds.build(-500,
                                                  -500,
                                                  1000,
                                                  1000);
        final Transform test = new Transform().translate(-200.0,
                                                         0.0);
        final Transform result = restriction.adjust(test,
                                                    visibleBounds);

        assertNotNull(result);
        assertEquals(0.0,
                     result.getTranslateX(),
                     0.0);
        assertEquals(0.0,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testTopEdge() {
        final Bounds visibleBounds = Bounds.build(-500,
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
        assertEquals(1000.0,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testBottomEdge() {
        final Bounds visibleBounds = Bounds.build(-500,
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
        assertEquals(0.0,
                     result.getTranslateY(),
                     0.0);
    }
}
