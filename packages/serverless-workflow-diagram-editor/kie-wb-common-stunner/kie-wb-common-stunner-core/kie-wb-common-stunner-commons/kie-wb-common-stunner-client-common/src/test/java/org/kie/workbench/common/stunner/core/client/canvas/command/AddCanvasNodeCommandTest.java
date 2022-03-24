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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AddCanvasNodeCommandTest extends AbstractCanvasCommandTest {

    @Mock
    private Node<View<?>, Edge> candidate;

    @Mock
    private View content;

    @Mock
    private Shape shape;

    @Mock
    private ShapeView view;

    private AddCanvasNodeCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();
        BoundingBox boundingBox = new BoundingBox(0d, 0d, 50d, 50d);
        when(shape.getShapeView()).thenReturn(view);
        when(view.getBoundingBox()).thenReturn(boundingBox);
        when(canvas.getShape(eq("someUUID"))).thenReturn(shape);
        when(candidate.getUUID()).thenReturn("someUUID");
        when(candidate.getContent()).thenReturn(content);
        this.tested = new AddCanvasNodeCommand(candidate,
                                               SHAPE_SET_ID);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        when(content.getBounds()).thenReturn(Bounds.create(0d, 0d, 10d, 10d));
        final CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertNotEquals(CommandResult.Type.ERROR,
                        result.getType());
        verify(canvasHandler,
               times(1)).register(eq(SHAPE_SET_ID),
                                  eq(candidate));
        verify(canvasHandler,
               times(1)).applyElementMutation(eq(candidate),
                                              any(MutationContext.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteAndSetViewBounds() {
        when(content.getBounds()).thenReturn(Bounds.create(0d, 0d, 0d, 0d));
        final CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertNotEquals(CommandResult.Type.ERROR,
                        result.getType());
        verify(canvasHandler,
               times(1)).register(eq(SHAPE_SET_ID),
                                  eq(candidate));
        verify(canvasHandler,
               times(1)).applyElementMutation(eq(candidate),
                                              any(MutationContext.class));
        final ArgumentCaptor<Bounds> boundsArgumentCaptor = ArgumentCaptor.forClass(Bounds.class);
        verify(content,
               times(1)).setBounds(boundsArgumentCaptor.capture());
        final Bounds bounds = boundsArgumentCaptor.getValue();
        assertEquals(0d, bounds.getX(), 0d);
        assertEquals(0d, bounds.getY(), 0d);
        assertEquals(50d, bounds.getWidth(), 0d);
        assertEquals(50d, bounds.getHeight(), 0d);
    }
}
