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

package org.kie.workbench.common.stunner.core.client.canvas;

import java.util.Optional;

import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
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
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractCanvasTest {

    private static final String PARENT_UUID = "parentUUID";
    private static final String CHILD_UUID = "childUUID";

    @Mock
    EventSourceMock<CanvasClearEvent> clearEvent;
    @Mock
    EventSourceMock<CanvasShapeAddedEvent> shapeAddedEvent;
    @Mock
    EventSourceMock<CanvasShapeRemovedEvent> shapeRemovedEvent;
    @Mock
    EventSourceMock<CanvasDrawnEvent> canvasDrawnEvent;
    @Mock
    EventSourceMock<CanvasFocusedEvent> canvasFocusEvent;
    @Mock
    Layer layer;
    @Mock
    AbstractCanvas.View canvasView;
    @Mock
    Shape parentShape;
    @Mock
    Shape childShape;
    @Mock
    ShapeView parentShapeView;
    @Mock
    ShapeView childShapeView;

    private AbstractCanvas<AbstractCanvas.View> tested;

    @Before
    public void setup() throws Exception {
        when(canvasView.getLayer()).thenReturn(layer);
        when(parentShape.getUUID()).thenReturn(PARENT_UUID);
        when(childShape.getUUID()).thenReturn(CHILD_UUID);
        when(parentShape.getShapeView()).thenReturn(parentShapeView);
        when(childShape.getShapeView()).thenReturn(childShapeView);
        this.tested = new AbstractCanvasStub(clearEvent,
                                             shapeAddedEvent,
                                             shapeRemovedEvent,
                                             canvasDrawnEvent,
                                             canvasFocusEvent,
                                             layer,
                                             canvasView);
        assertEquals(layer,
                     tested.getLayer());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShow() {
        final Object panel = mock(Object.class);
        tested.show(panel,
                    600,
                    400,
                    layer);
        verify(canvasView,
               times(1)).show(eq(panel),
                              eq(600),
                              eq(400),
                              eq(layer));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddChildShape() {
        tested.addChildShape(parentShape,
                             childShape);
        verify(canvasView,
               times(1)).addChildShape(eq(parentShapeView),
                                       eq(childShapeView));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteChildShape() {
        tested.deleteChildShape(parentShape,
                                childShape);
        verify(canvasView,
               times(1)).removeChildShape(eq(parentShapeView),
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
               times(1)).addShape(eq(parentShapeView));
        verify(parentShapeView,
               times(1)).setUUID(eq(PARENT_UUID));
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
               times(1)).removeShape(eq(parentShapeView));
        assertTrue(tested.getShapes().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClear() {
        tested.shapes.put(parentShape.getUUID(), parentShape);
        tested.shapes.put(childShape.getUUID(), childShape);
        tested.clear();
        verify(canvasView,
               times(1)).removeShape(eq(parentShapeView));
        verify(canvasView,
               times(1)).removeShape(eq(childShapeView));
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
               times(1)).removeShape(eq(parentShapeView));
        verify(canvasView,
               times(1)).removeShape(eq(childShapeView));
        assertTrue(tested.getShapes().isEmpty());
        verify(canvasView,
               times(1)).destroy();
        verify(layer,
               times(1)).destroy();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClearShapes() throws Exception {
        tested.shapes.put(parentShape.getUUID(), parentShape);
        tested.shapes.put(childShape.getUUID(), childShape);
        tested.clearShapes();
        verify(canvasView,
               times(1)).removeShape(eq(parentShapeView));
        verify(canvasView,
               times(1)).removeShape(eq(childShapeView));
        assertTrue(tested.getShapes().isEmpty());
    }

    private class AbstractCanvasStub extends AbstractCanvas<AbstractCanvas.View> {

        public AbstractCanvasStub(final Event<CanvasClearEvent> canvasClearEvent,
                                  final Event<CanvasShapeAddedEvent> canvasShapeAddedEvent,
                                  final Event<CanvasShapeRemovedEvent> canvasShapeRemovedEvent,
                                  final Event<CanvasDrawnEvent> canvasDrawnEvent,
                                  final Event<CanvasFocusedEvent> canvasFocusedEvent,
                                  final Layer layer,
                                  final View view) {
            super(canvasClearEvent,
                  canvasShapeAddedEvent,
                  canvasShapeRemovedEvent,
                  canvasDrawnEvent,
                  canvasFocusedEvent,
                  layer,
                  view);
        }

        @Override
        public void addControl(final IsWidget controlView) {

        }

        @Override
        public void deleteControl(final IsWidget controlView) {

        }

        @Override
        public Canvas initialize(CanvasSettings settings) {
            return this;
        }

        @Override
        public Optional<Shape> getShapeAt(final double x,
                                          final double y) {
            return Optional.empty();
        }

        @Override
        public void focus() {

        }
    }
}
