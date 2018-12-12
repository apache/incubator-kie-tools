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

package org.kie.workbench.common.stunner.client.lienzo.canvas;

import java.util.Optional;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasSettings;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasDrawnEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoCanvasTest {

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
    private LienzoCanvasView view;
    @Mock
    private LienzoLayer lienzoLayer;
    @Mock
    private Layer layer;

    private LienzoCanvasStub tested;

    @Before
    public void setUp() throws Exception {
        when(lienzoLayer.getLienzoLayer()).thenReturn(layer);
        when(layer.getLayer()).thenReturn(layer);
        when(view.getLayer()).thenReturn(lienzoLayer);
        this.tested = spy(new LienzoCanvasStub(clearEvent,
                                               shapeAddedEvent,
                                               shapeRemovedEvent,
                                               canvasDrawnEvent,
                                               canvasFocusEvent,
                                               view));
    }

    @Test
    public void testInitialize() {
        CanvasPanel panel = mock(CanvasPanel.class);
        CanvasSettings settings = mock(CanvasSettings.class);
        when(settings.isHiDPIEnabled()).thenReturn(true);
        assertEquals(tested, tested.initialize(panel,
                                               settings));
        assertTrue(LienzoCore.get().isHidpiEnabled());
        assertNotNull(tested.getEventHandlerManager());
        verify(view, times(1)).initialize(eq(panel), eq(settings));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetShapeAt() {
        double x = 10;
        double y = 33;
        com.ait.lienzo.client.core.shape.Shape lienzoShape = mock(com.ait.lienzo.client.core.shape.Shape.class);
        WiresUtils.UserData userData = new WiresUtils.UserData("uuid1", "group1");
        when(lienzoShape.getUserData()).thenReturn(userData);
        when(layer.findShapeAtPoint(eq(10), eq(33))).thenReturn(lienzoShape);
        Shape stunnerShape = mock(Shape.class);
        when(tested.getShape(eq("uuid1"))).thenReturn(stunnerShape);
        Optional<Shape> shape = tested.getShapeAt(x, y);
        assertTrue(shape.isPresent());
        assertEquals(stunnerShape, shape.get());
    }

    @Test
    public void testOnAfterDraw() {
        Command callback = mock(Command.class);
        tested.onAfterDraw(callback);
        verify(lienzoLayer, times(1)).onAfterDraw(eq(callback));
    }

    @Test
    public void testFocus() {
        LienzoPanel panel = mock(LienzoPanel.class);
        when(view.getLienzoPanel()).thenReturn(panel);
        tested.focus();
        verify(panel, times(1)).focus();
    }

    @Test
    public void testEventHandling() {
        CanvasPanel panel = mock(CanvasPanel.class);
        CanvasSettings settings = mock(CanvasSettings.class);
        ViewEventHandlerManager eventHandler = mock(ViewEventHandlerManager.class);
        tested.initialize(panel, settings, eventHandler);
        tested.supports(ViewEventType.DRAG);
        verify(eventHandler, times(1)).supports(eq(ViewEventType.DRAG));
        tested.enableHandlers();
        verify(eventHandler, times(1)).enable();
        tested.disableHandlers();
        verify(eventHandler, times(1)).disable();
        DragHandler dragHandler = mock(DragHandler.class);
        tested.addHandler(ViewEventType.DRAG, dragHandler);
        verify(eventHandler, times(1)).addHandler(eq(ViewEventType.DRAG),
                                                  eq(dragHandler));
        tested.removeHandler(dragHandler);
        verify(eventHandler, times(1)).removeHandler(eq(dragHandler));
    }

    @Test
    public void testDestroy() {
        CanvasPanel panel = mock(CanvasPanel.class);
        CanvasSettings settings = mock(CanvasSettings.class);
        ViewEventHandlerManager eventHandler = mock(ViewEventHandlerManager.class);
        tested.initialize(panel, settings, eventHandler);
        tested.destroy();
        verify(eventHandler, times(1)).destroy();
        verify(view, times(1)).destroy();
    }

    public static class LienzoCanvasStub extends LienzoCanvas<LienzoCanvasView> {

        private LienzoCanvasView view;

        protected LienzoCanvasStub(final Event<CanvasClearEvent> canvasClearEvent,
                                   final Event<CanvasShapeAddedEvent> canvasShapeAddedEvent,
                                   final Event<CanvasShapeRemovedEvent> canvasShapeRemovedEvent,
                                   final Event<CanvasDrawnEvent> canvasDrawnEvent,
                                   final Event<CanvasFocusedEvent> canvasFocusedEvent,
                                   final LienzoCanvasView view) {
            super(canvasClearEvent, canvasShapeAddedEvent, canvasShapeRemovedEvent, canvasDrawnEvent, canvasFocusedEvent);
            this.view = view;
        }

        @Override
        public LienzoCanvasView getView() {
            return view;
        }

        @Override
        protected void addChild(Shape shape) {

        }

        @Override
        protected void deleteChild(Shape shape) {

        }
    }
}
