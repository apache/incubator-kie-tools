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


package org.kie.workbench.common.stunner.core.client.canvas;

import java.util.Optional;

import jakarta.enterprise.event.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasDrawnEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCanvasTest {

    private static final String PARENT_UUID = "parentUUID";
    private static final String CHILD_UUID = "childUUID";

    @Mock
    private EventSourceMock<CanvasClearEvent> clearEvent;
    @Mock
    private EventSourceMock<CanvasShapeAddedEvent> shapeAddedEvent;
    @Mock
    private EventSourceMock<CanvasShapeRemovedEvent> shapeRemovedEvent;
    @Mock
    private EventSourceMock<CanvasDrawnEvent> canvasDrawnEvent;
    @Mock
    private EventSourceMock<CanvasFocusedEvent> canvasFocusEvent;
    @Mock
    private AbstractCanvas.CanvasView canvasView;
    @Mock
    private Shape parentShape;
    @Mock
    private Shape childShape;
    @Mock
    private ShapeView parentShapeView;
    @Mock
    private ShapeView childShapeView;

    private AbstractCanvas<AbstractCanvas.CanvasView> tested;

    @Before
    public void setup() throws Exception {
        when(parentShape.getUUID()).thenReturn(PARENT_UUID);
        when(childShape.getUUID()).thenReturn(CHILD_UUID);
        when(parentShape.getShapeView()).thenReturn(parentShapeView);
        when(childShape.getShapeView()).thenReturn(childShapeView);
        this.tested = new AbstractCanvasStub(clearEvent,
                                             shapeAddedEvent,
                                             shapeRemovedEvent,
                                             canvasDrawnEvent,
                                             canvasFocusEvent);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddChildShape() {
        tested.addChild(parentShape,
                        childShape);
        verify(canvasView,
               times(1)).addChild(eq(parentShapeView),
                                  eq(childShapeView));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteChildShape() {
        tested.deleteChild(parentShape,
                           childShape);
        verify(canvasView,
               times(1)).deleteChild(eq(parentShapeView),
                                     eq(childShapeView));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDock() {
        tested.dock(parentShape,
                    childShape);
        verify(canvasView,
               times(1)).dock(eq(parentShapeView),
                              eq(childShapeView));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndock() {
        tested.undock(parentShape,
                      childShape);
        verify(canvasView,
               times(1)).undock(eq(parentShapeView),
                                eq(childShapeView));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndockToLayer() {
        tested.undock(parentShape,
                      childShape);
        verify(canvasView,
               times(1)).undock(eq(parentShapeView),
                                eq(childShapeView));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddShape() {
        tested.addShape(parentShape);
        verify(canvasView,
               times(1)).add(eq(parentShapeView));
        verify(parentShapeView,
               never()).setUUID(anyString());
        assertEquals(1,
                     tested.getShapes().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGrid() {
        final CanvasGrid grid = mock(CanvasGrid.class);
        tested.setGrid(grid);
        verify(canvasView,
               times(1)).setGrid(eq(grid));
        assertEquals(grid,
                     tested.getGrid());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteShape() {
        tested.shapes.put(parentShape.getUUID(), parentShape);
        tested.deleteShape(parentShape);
        verify(canvasView,
               times(1)).delete(eq(parentShapeView));
        assertTrue(tested.getShapes().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClear() {
        tested.shapes.put(parentShape.getUUID(), parentShape);
        tested.shapes.put(childShape.getUUID(), childShape);
        tested.clear();
        verify(canvasView,
               times(1)).delete(eq(parentShapeView));
        verify(canvasView,
               times(1)).delete(eq(childShapeView));
        assertTrue(tested.getShapes().isEmpty());
        verify(canvasView,
               times(1)).clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        tested.shapes.put(parentShape.getUUID(), parentShape);
        tested.shapes.put(childShape.getUUID(), childShape);
        tested.destroy();
        verify(canvasView,
               times(1)).delete(eq(parentShapeView));
        verify(canvasView,
               times(1)).delete(eq(childShapeView));
        assertTrue(tested.getShapes().isEmpty());
        verify(canvasView,
               times(1)).destroy();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClearShapes() throws Exception {
        tested.shapes.put(parentShape.getUUID(), parentShape);
        tested.shapes.put(childShape.getUUID(), childShape);
        tested.clearShapes();
        verify(canvasView,
               times(1)).delete(eq(parentShapeView));
        verify(canvasView,
               times(1)).delete(eq(childShapeView));
        assertTrue(tested.getShapes().isEmpty());
    }

    private class AbstractCanvasStub extends AbstractCanvas<AbstractCanvas.CanvasView> {

        public AbstractCanvasStub(final Event<CanvasClearEvent> canvasClearEvent,
                                  final Event<CanvasShapeAddedEvent> canvasShapeAddedEvent,
                                  final Event<CanvasShapeRemovedEvent> canvasShapeRemovedEvent,
                                  final Event<CanvasDrawnEvent> canvasDrawnEvent,
                                  final Event<CanvasFocusedEvent> canvasFocusedEvent) {
            super(canvasClearEvent,
                  canvasShapeAddedEvent,
                  canvasShapeRemovedEvent,
                  canvasDrawnEvent,
                  canvasFocusedEvent);
        }

        @Override
        public CanvasView getView() {
            return canvasView;
        }

        @Override
        protected void addChild(Shape shape) {

        }

        @Override
        protected void deleteChild(Shape shape) {

        }

        @Override
        public Optional<Shape> getShapeAt(final double x,
                                          final double y) {
            return Optional.empty();
        }

        @Override
        public void onAfterDraw(Command callback) {

        }

        @Override
        public void focus() {

        }

        @Override
        public boolean isEventHandlesEnabled() {
            return true;
        }

        @Override
        public boolean supports(ViewEventType type) {
            return false;
        }

        @Override
        public AbstractCanvas<CanvasView> addHandler(ViewEventType type, ViewHandler<? extends ViewEvent> eventHandler) {
            return null;
        }

        @Override
        public AbstractCanvas<CanvasView> removeHandler(ViewHandler<? extends ViewEvent> eventHandler) {
            return null;
        }

        @Override
        public AbstractCanvas<CanvasView> enableHandlers() {
            return null;
        }

        @Override
        public AbstractCanvas<CanvasView> disableHandlers() {
            return null;
        }

        @Override
        public Shape<?> getAttachableShape() {
            return null;
        }
    }
}
