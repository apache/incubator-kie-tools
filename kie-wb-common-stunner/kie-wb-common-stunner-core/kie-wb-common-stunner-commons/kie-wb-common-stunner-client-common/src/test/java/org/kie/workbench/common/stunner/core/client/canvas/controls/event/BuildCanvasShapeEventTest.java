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

package org.kie.workbench.common.stunner.core.client.canvas.controls.event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Transform;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BuildCanvasShapeEventTest {

    private final double ALLOWED_ERROR = 0.00001;
    @Mock
    AbstractCanvasHandler canvasHandler;

    @Mock
    AbstractCanvas canvas;

    @Mock
    Transform transform;

    BuildCanvasShapeEvent tested;

    @Before
    public void setup() throws Exception {
        canvasHandler = mock(AbstractCanvasHandler.class);
        canvas = mock(AbstractCanvas.class);
        transform = mock(Transform.class);
        when(canvas.getTransform()).thenReturn(transform);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
    }

    @Test
    public void testBuildWithoutTransform() {
        when(transform.inverse(anyDouble(), anyDouble())).thenAnswer((invocation -> new Point2D(invocation.getArgumentAt(0, Double.class), invocation.getArgumentAt(1, Double.class))));
        tested = new BuildCanvasShapeEvent(canvasHandler, null, null, 0, 0);
        assertEquals(0, tested.getX(), ALLOWED_ERROR);
        assertEquals(0, tested.getY(), ALLOWED_ERROR);

        tested = new BuildCanvasShapeEvent(canvasHandler, null, null, 5, 15);
        assertEquals(5, tested.getX(), ALLOWED_ERROR);
        assertEquals(15, tested.getY(), ALLOWED_ERROR);
    }

    @Test
    public void testBuildWithPan() {
        when(transform.inverse(anyDouble(), anyDouble())).thenAnswer((invocation -> new Point2D(invocation.getArgumentAt(0, Double.class) + 5, invocation.getArgumentAt(1, Double.class) - 10)));

        tested = new BuildCanvasShapeEvent(canvasHandler, null, null, 0, 0);
        assertEquals(5, tested.getX(), ALLOWED_ERROR);
        assertEquals(-10, tested.getY(), ALLOWED_ERROR);

        tested = new BuildCanvasShapeEvent(canvasHandler, null, null, 5, 15);
        assertEquals(10, tested.getX(), ALLOWED_ERROR);
        assertEquals(5, tested.getY(), ALLOWED_ERROR);
    }

    @Test
    public void testBuildWithZoom() {
        when(transform.inverse(anyDouble(), anyDouble())).thenAnswer((invocation -> new Point2D(invocation.getArgumentAt(0, Double.class) * 0.5, invocation.getArgumentAt(1, Double.class) * 2)));

        tested = new BuildCanvasShapeEvent(canvasHandler, null, null, 0, 0);
        assertEquals(0, tested.getX(), ALLOWED_ERROR);
        assertEquals(0, tested.getY(), ALLOWED_ERROR);

        tested = new BuildCanvasShapeEvent(canvasHandler, null, null, 5, 15);
        assertEquals(2.5, tested.getX(), ALLOWED_ERROR);
        assertEquals(30, tested.getY(), ALLOWED_ERROR);
    }

    @Test
    public void testBuildWithPanAndZoom() {
        when(transform.inverse(anyDouble(), anyDouble())).thenAnswer((invocation -> new Point2D((invocation.getArgumentAt(0, Double.class) + 5) * 0.5, (invocation.getArgumentAt(1, Double.class) - 5) * 2)));

        tested = new BuildCanvasShapeEvent(canvasHandler, null, null, 0, 0);
        assertEquals(2.5, tested.getX(), ALLOWED_ERROR);
        assertEquals(-10, tested.getY(), ALLOWED_ERROR);

        tested = new BuildCanvasShapeEvent(canvasHandler, null, null, 5, 15);
        assertEquals(5, tested.getX(), ALLOWED_ERROR);
        assertEquals(20, tested.getY(), ALLOWED_ERROR);
    }
}
