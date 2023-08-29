/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
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
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CanvasHighlightTest {

    public static final String ID1 = "n1";
    public static final String ID2 = "n2";

    @Mock
    private AbstractCanvasHandler handler;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private AbstractCanvas.CanvasView canvasView;

    @Mock
    private Node node1;

    @Mock
    private Node node2;

    @Mock
    private Shape shape1;

    @Mock
    private Shape shape2;

    private CanvasHighlight tested;

    @Before
    public void setup() {
        when(handler.getCanvas()).thenReturn(canvas);
        when(handler.getAbstractCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(canvasView);
        when(node1.getUUID()).thenReturn(ID1);
        when(node2.getUUID()).thenReturn(ID2);
        when(canvas.getShape(eq(ID1))).thenReturn(shape1);
        when(canvas.getShape(eq(ID2))).thenReturn(shape2);
        tested = new CanvasHighlight()
                .setCanvasHandler(handler);
    }

    @Test
    public void testSingle() {
        tested.none(node1);
        verify(shape1,
               times(1)).applyState(eq(ShapeState.NONE));
        verify(shape2,
               times(0)).applyState(any(ShapeState.class));
        verify(canvasView,
               times(1)).setCursor(eq(AbstractCanvas.Cursors.DEFAULT));
    }

    @Test
    public void testMultiple() {
        tested.none(node1);
        tested.invalid(node2);
        verify(shape1,
               times(1)).applyState(eq(ShapeState.NONE));
        verify(shape2,
               times(1)).applyState(eq(ShapeState.INVALID));
        verify(canvasView,
               times(1)).setCursor(eq(AbstractCanvas.Cursors.DEFAULT));
        verify(canvasView,
               times(1)).setCursor(eq(AbstractCanvas.Cursors.NOT_ALLOWED));
    }

    @Test
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
               atLeastOnce()).setCursor(eq(AbstractCanvas.Cursors.MOVE));
    }

    @Test
    public void testUnhighLightEvent() {
        tested.highLight(node1);
        tested.invalid(node2);
        tested.onCanvasUnhighlightEvent(mock(CanvasUnhighlightEvent.class));
        verify(shape1,
               times(1)).applyState(eq(ShapeState.HIGHLIGHT));
        verify(shape1,
               times(1)).applyState(eq(ShapeState.NONE));
        verify(shape2,
               times(1)).applyState(eq(ShapeState.INVALID));
        verify(shape2,
               times(1)).applyState(eq(ShapeState.NONE));
        verify(canvasView,
               atLeastOnce()).setCursor(eq(AbstractCanvas.Cursors.DEFAULT));
    }

    @Test
    public void testDestroy() {
        tested.highLight(node1);
        tested.invalid(node2);
        tested.destroy();
        verify(shape1,
               times(1)).applyState(eq(ShapeState.HIGHLIGHT));
        verify(shape1,
               times(0)).applyState(eq(ShapeState.NONE));
        verify(shape2,
               times(1)).applyState(eq(ShapeState.INVALID));
        verify(shape2,
               times(0)).applyState(eq(ShapeState.NONE));
        verify(canvasView,
               atLeastOnce()).setCursor(eq(AbstractCanvas.Cursors.DEFAULT));
    }
}
