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

package org.kie.workbench.common.stunner.client.lienzo.canvas.wires;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.wires.WiresManagerFactory;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasSettings;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresCanvasTest {

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
    private WiresCanvasView view;
    @Mock
    private WiresLayer wiresLayer;
    @Mock
    private Layer layer;
    @Mock
    private WiresManagerFactory wiresManagerFactory;
    @Mock
    private WiresManager wiresManager;

    private WiresCanvas tested;

    @Before
    public void setUp() throws Exception {
        when(wiresLayer.getLienzoLayer()).thenReturn(layer);
        when(layer.getLayer()).thenReturn(layer);
        when(view.getLayer()).thenReturn(wiresLayer);
        when(wiresManagerFactory.newWiresManager(eq(layer))).thenReturn(wiresManager);
        tested = new WiresCanvas(clearEvent,
                                 shapeAddedEvent,
                                 shapeRemovedEvent,
                                 canvasDrawnEvent,
                                 canvasFocusEvent,
                                 wiresManagerFactory,
                                 view);
    }

    @Test
    public void testGetView() {
        assertEquals(view, tested.getView());
    }

    @Test
    public void testInitialize() {
        CanvasPanel panel = mock(CanvasPanel.class);
        CanvasSettings settings = mock(CanvasSettings.class);
        assertEquals(tested, tested.initialize(panel,
                                               settings));
        verify(wiresManager, times(1)).setSpliceEnabled(eq(false));
        verify(wiresManager, times(1)).setLocationAcceptor(eq(ILocationAcceptor.NONE));
        verify(wiresManager, times(1)).setContainmentAcceptor(eq(IContainmentAcceptor.NONE));
        verify(wiresManager, times(1)).setDockingAcceptor(eq(IDockingAcceptor.NONE));
        verify(wiresManager, times(1)).setConnectionAcceptor(eq(IConnectionAcceptor.NONE));
        verify(wiresManager, times(1)).setControlPointsAcceptor(eq(IControlPointsAcceptor.NONE));
        verify(view, times(1)).use(eq(wiresManager));
        verify(view, times(1)).initialize(eq(panel), eq(settings));
        assertEquals(wiresManager, tested.getWiresManager());
    }

    @Test
    public void testAddChild() {
        Shape shape = mock(Shape.class);
        ShapeView shapeView = mock(ShapeView.class);
        when(shape.getShapeView()).thenReturn(shapeView);
        tested.addChild(shape);
        verify(view, times(1)).addRoot(eq(shapeView));
    }

    @Test
    public void testDeleteChild() {
        Shape shape = mock(Shape.class);
        ShapeView shapeView = mock(ShapeView.class);
        when(shape.getShapeView()).thenReturn(shapeView);
        tested.deleteChild(shape);
        verify(view, times(1)).deleteRoot(eq(shapeView));
    }
}
