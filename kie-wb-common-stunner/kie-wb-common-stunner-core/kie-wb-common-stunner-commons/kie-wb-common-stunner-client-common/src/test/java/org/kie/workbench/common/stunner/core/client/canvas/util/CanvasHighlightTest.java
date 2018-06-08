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

package org.kie.workbench.common.stunner.core.client.canvas.util;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.RuleViolationImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CanvasHighlightTest {

    public static final String ID1 = "n1";
    public static final String ID2 = "n2";

    @Mock
    AbstractCanvasHandler handler;

    @Mock
    AbstractCanvas canvas;

    @Mock
    AbstractCanvas.View canvasView;

    @Mock
    Node node1;

    @Mock
    Node node2;

    @Mock
    Shape shape1;

    @Mock
    Shape shape2;

    private CanvasHighlight tested;

    @Before
    public void setup() throws Exception {
        when(handler.getCanvas()).thenReturn(canvas);
        when(handler.getAbstractCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(canvasView);
        when(node1.getUUID()).thenReturn(ID1);
        when(node2.getUUID()).thenReturn(ID2);
        when(canvas.getShape(eq(ID1))).thenReturn(shape1);
        when(canvas.getShape(eq(ID2))).thenReturn(shape2);
        tested = new CanvasHighlight(handler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSingle() {
        tested.none(node1);
        verify(shape1,
               times(1)).applyState(eq(ShapeState.NONE));
        verify(shape2,
               times(0)).applyState(any(ShapeState.class));
        verify(canvasView,
               times(1)).setCursor(eq(AbstractCanvas.Cursors.AUTO));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiple() {
        tested.none(node1);
        tested.invalid(node2);
        verify(shape1,
               times(1)).applyState(eq(ShapeState.NONE));
        verify(shape2,
               times(1)).applyState(eq(ShapeState.INVALID));
        verify(canvasView,
               times(1)).setCursor(eq(AbstractCanvas.Cursors.AUTO));
        verify(canvasView,
               times(1)).setCursor(eq(AbstractCanvas.Cursors.NOT_ALLOWED));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testViolations() {
        final List<RuleViolation> violations = new LinkedList<>();
        final RuleViolationImpl v1 = new RuleViolationImpl("m1");
        v1.setUUID(ID1);
        final RuleViolationImpl v2 = new RuleViolationImpl("m2");
        v2.setUUID(ID2);
        violations.add(v1);
        violations.add(v2);
        tested.invalid(violations);
        verify(shape1,
               times(1)).applyState(eq(ShapeState.INVALID));
        verify(shape2,
               times(1)).applyState(eq(ShapeState.INVALID));
        verify(canvasView,
               times(2)).setCursor(eq(AbstractCanvas.Cursors.NOT_ALLOWED));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnhighLight() {
        tested.highLight(node1);
        tested.invalid(node2);
        tested.unhighLight();
        verify(shape1,
               times(1)).applyState(eq(ShapeState.HIGHLIGHT));
        verify(shape1,
               times(1)).applyState(eq(ShapeState.NONE));
        verify(shape2,
               times(1)).applyState(eq(ShapeState.INVALID));
        verify(shape2,
               times(1)).applyState(eq(ShapeState.NONE));
        verify(canvasView,
               atLeastOnce()).setCursor(eq(AbstractCanvas.Cursors.AUTO));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        tested.highLight(node1);
        tested.invalid(node2);
        tested.destroy();
        tested.unhighLight();
        verify(shape1,
               times(1)).applyState(eq(ShapeState.HIGHLIGHT));
        verify(shape1,
               times(0)).applyState(eq(ShapeState.NONE));
        verify(shape2,
               times(1)).applyState(eq(ShapeState.INVALID));
        verify(shape2,
               times(0)).applyState(eq(ShapeState.NONE));
        verify(canvasView,
               atLeastOnce()).setCursor(eq(AbstractCanvas.Cursors.AUTO));
    }
}
