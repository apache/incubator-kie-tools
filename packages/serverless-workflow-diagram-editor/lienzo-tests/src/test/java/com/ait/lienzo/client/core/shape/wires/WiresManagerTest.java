/*
 *
 *    Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.event.OnEventHandlers;
import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresHandlerFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresManagerTest {

    private static final String LAYER_ID = "theLayer";

    private WiresManager tested;

    private Layer layer;

    private Scene scene = new Scene();

    @Mock
    private HTMLDivElement element;

    private Viewport viewport;

    @Before
    public void setup() {
        OnEventHandlers onEventHandlers = new OnEventHandlers();

        layer = spy(new Layer());
        viewport = mock(Viewport.class);
        when(viewport.getScene()).thenReturn(scene);
        when(layer.getViewport()).thenReturn(viewport);
        when(viewport.getOnEventHandlers()).thenReturn(onEventHandlers);
        when(viewport.getElement()).thenReturn(element);
        doNothing().when(layer).setPixelSize(anyInt(), anyInt());

        when(viewport.setPixelSize(anyInt(), anyInt())).thenReturn(viewport);
        layer.setID(LAYER_ID);
        viewport.getScene().add(layer);
        tested = WiresManager.get(layer);
    }

    @Test
    public void testGetWiresManager() {
        final Layer layer2 = spy(new Layer());
        layer2.setID("layer2");
        when(layer2.getViewport()).thenReturn(viewport);
        final WiresManager tested2 = WiresManager.get(layer2);
        assertEquals(tested, WiresManager.get(layer));
        assertEquals(tested2, WiresManager.get(layer2));
    }

    @Test
    public void testCreateWiresManagerInstance() {
        final Layer layer2 = mock(Layer.class);
        when(layer2.uuid()).thenReturn("layer2");
        when(layer2.getViewport()).thenReturn(viewport);

        final WiresManager manager = WiresManager.get(layer2);
        verify(layer2, times(1)).setOnLayerBeforeDraw(any(WiresManager.LinePreparer.class));
        assertNotNull(manager.getAlignAndDistribute());
        assertNotNull(manager.getLayer());
        final WiresLayer wiresLayer = manager.getLayer();
        assertEquals(layer2, wiresLayer.getLayer());
    }

    @Test
    public void testRegisterShape() {
        final IContainmentAcceptor containmentAcceptor = mock(IContainmentAcceptor.class);
        final IDockingAcceptor dockingAcceptor = mock(IDockingAcceptor.class);
        final ILineSpliceAcceptor lineSpliceAcceptor = mock(ILineSpliceAcceptor.class);
        tested.setContainmentAcceptor(containmentAcceptor);
        tested.setDockingAcceptor(dockingAcceptor);
        tested.setLineSpliceAcceptor(lineSpliceAcceptor);
        final WiresManager spied = spy(tested);
        final HandlerRegistrationManager handlerRegistrationManager = mock(HandlerRegistrationManager.class);
        doReturn(handlerRegistrationManager).when(spied).createHandlerRegistrationManager();
        final WiresShape s = new WiresShape(new MultiPath().rect(0, 0, 10, 10));
        final WiresShape shape = spy(s);
        final WiresShapeControl shapeControl = spied.register(shape);
        assertNotNull(shapeControl);
        assertNotNull(tested.getShape(shape.uuid()));
        verify(shape, times(1)).setControl(any(WiresShapeControl.class));
        verify(layer, times(1)).add(eq(shape.getGroup()));
        verify(handlerRegistrationManager, atLeastOnce()).register(any(HandlerRegistration.class));
        verify(shape).addWiresResizeStartHandler(any(WiresResizeStartHandler.class));
        verify(shape).addWiresResizeEndHandler(any(WiresResizeEndHandler.class));
    }

    @Test
    public void testResizeShape() {
        final Viewport viewport = mock(Viewport.class);
        when(viewport.getOverLayer()).thenReturn(mock(Layer.class));
        when(layer.getViewport()).thenReturn(viewport);
        when(layer.getLayer()).thenReturn(layer);

        final ScratchPad pad = mock(ScratchPad.class);
        when(layer.getScratchPad()).thenReturn(pad);
        when(pad.getWidth()).thenReturn(10);
        when(pad.getHeight()).thenReturn(10);

        final Context2D context2D = mock(Context2D.class);
        when(pad.getContext()).thenReturn(context2D);

        final IContainmentAcceptor containmentAcceptor = mock(IContainmentAcceptor.class);
        tested.setContainmentAcceptor(containmentAcceptor);

        final IDockingAcceptor dockingAcceptor = mock(IDockingAcceptor.class);
        tested.setDockingAcceptor(dockingAcceptor);

        final ILineSpliceAcceptor lineSpliceAcceptor = mock(ILineSpliceAcceptor.class);
        tested.setLineSpliceAcceptor(lineSpliceAcceptor);

        final WiresManager spied = spy(tested);
        final HandlerRegistrationManager handlerRegistrationManager = mock(HandlerRegistrationManager.class);
        doReturn(handlerRegistrationManager).when(spied).createHandlerRegistrationManager();
        final WiresShape shape = spy(new WiresShape(new MultiPath().rect(0, 0, 10, 10)));

        final Group group = spy(shape.getGroup());
        when(shape.getGroup()).thenReturn(group);
        final WiresShapeControl shapeControl = spied.register(shape);

        verify(shape).addWiresResizeStartHandler(any(WiresResizeStartHandler.class));
        verify(shape).addWiresResizeEndHandler(any(WiresResizeEndHandler.class));
    }

    @Test
    public void testDeregisterShape() {
        final WiresManager spied = spy(tested);
        final HandlerRegistrationManager handlerRegistrationManager = mock(HandlerRegistrationManager.class);
        doReturn(handlerRegistrationManager).when(spied).createHandlerRegistrationManager();
        final Group group = new Group();
        final String gUUID = group.uuid();
        final WiresShape s = new WiresShape(new MultiPath().rect(0, 0, 10, 10));
        final WiresShape shape = spy(s);
        spied.enableSelectionManager();
        spied.getSelectionManager().getSelectedItems().add(shape);
        spied.register(shape);
        spied.deregister(shape);
        assertNull(tested.getShape(gUUID));
        assertTrue(spied.getSelectionManager().getSelectedItems().isEmpty());
        verify(handlerRegistrationManager, times(1)).removeHandler();
        verify(shape, times(1)).destroy();
        verify(layer, atLeastOnce()).remove(eq(s.getGroup()));
    }

    @Test
    public void testRegisterConnector() {
        final IConnectionAcceptor connectionAcceptor = mock(IConnectionAcceptor.class);
        tested.setConnectionAcceptor(connectionAcceptor);
        final WiresManager spied = spy(tested);
        final HandlerRegistrationManager handlerRegistrationManager = mock(HandlerRegistrationManager.class);
        doReturn(handlerRegistrationManager).when(spied).createHandlerRegistrationManager();
        final Group group = new Group();
        final Group shapeGroup = spy(group);
        final AbstractDirectionalMultiPointShape<?> line = mock(AbstractDirectionalMultiPointShape.class);
        final MultiPath head = mock(MultiPath.class);
        final MultiPath tail = mock(MultiPath.class);
        final WiresConnector connector = mock(WiresConnector.class);
        final WiresHandlerFactory wiresHandlerFactory = mock(WiresHandlerFactory.class);
        final WiresConnectorHandler wiresConnectorHandler = mock(WiresConnectorHandler.class);
        final WiresConnectorControl wiresConnectorControl = mock(WiresConnectorControl.class);
        doReturn(shapeGroup).when(connector).getGroup();
        doReturn(line).when(connector).getLine();
        doReturn(head).when(connector).getHead();
        doReturn(tail).when(connector).getTail();
        doReturn(group.uuid()).when(connector).uuid();
        doReturn(wiresConnectorHandler).when(wiresHandlerFactory).newConnectorHandler(connector, spied);
        doReturn(wiresConnectorControl).when(wiresConnectorHandler).getControl();

        spied.setWiresHandlerFactory(wiresHandlerFactory);
        assertEquals(spied.getWiresHandlerFactory(), wiresHandlerFactory);
        final WiresConnectorControl connectorControl = spied.register(connector);
        assertNotNull(connectorControl);
        assertFalse(spied.getConnectorList().isEmpty());
        verify(connector, times(1)).setControl(any(WiresConnectorControl.class));
        verify(connector, times(1)).addToLayer(eq(layer));
    }

    @Test
    public void testRegisterAddHandler() {
        final IConnectionAcceptor connectionAcceptor = mock(IConnectionAcceptor.class);
        tested.setConnectionAcceptor(connectionAcceptor);
        final WiresManager spied = spy(tested);
        final HandlerRegistrationManager handlerRegistrationManager = mock(HandlerRegistrationManager.class);
        doReturn(handlerRegistrationManager).when(spied).createHandlerRegistrationManager();
        final Group group = new Group();
        final Group shapeGroup = spy(group);
        final AbstractDirectionalMultiPointShape<?> line = mock(AbstractDirectionalMultiPointShape.class);
        final MultiPath head = mock(MultiPath.class);
        final MultiPath tail = mock(MultiPath.class);
        final WiresConnector connector = mock(WiresConnector.class);
        final WiresHandlerFactory wiresHandlerFactory = mock(WiresHandlerFactory.class);
        final WiresConnectorHandler wiresConnectorHandler = mock(WiresConnectorHandler.class);
        final WiresConnectorControl wiresConnectorControl = mock(WiresConnectorControl.class);
        doReturn(shapeGroup).when(connector).getGroup();
        doReturn(line).when(connector).getLine();
        doReturn(head).when(connector).getHead();
        doReturn(tail).when(connector).getTail();
        doReturn(group.uuid()).when(connector).uuid();
        doReturn(wiresConnectorHandler).when(wiresHandlerFactory).newConnectorHandler(connector, spied);
        doReturn(wiresConnectorControl).when(wiresConnectorHandler).getControl();

        spied.setWiresHandlerFactory(wiresHandlerFactory);
        assertEquals(spied.getWiresHandlerFactory(), wiresHandlerFactory);
        spied.addHandlers(connector);
        assertTrue(spied.getConnectorList().isEmpty());
        verify(wiresHandlerFactory, times(1)).newConnectorHandler(connector, spied);
    }

    @Test
    public void testDeregisterConnector() {
        final WiresManager spied = spy(tested);
        final HandlerRegistrationManager handlerRegistrationManager = mock(HandlerRegistrationManager.class);
        doReturn(handlerRegistrationManager).when(spied).createHandlerRegistrationManager();
        final Group group = new Group();
        final Group shapeGroup = spy(group);
        final AbstractDirectionalMultiPointShape<?> line = mock(AbstractDirectionalMultiPointShape.class);
        final MultiPath head = mock(MultiPath.class);
        final MultiPath tail = mock(MultiPath.class);
        final WiresConnector connector = mock(WiresConnector.class);
        doReturn(shapeGroup).when(connector).getGroup();
        doReturn(line).when(connector).getLine();
        doReturn(head).when(connector).getHead();
        doReturn(tail).when(connector).getTail();
        doReturn(group.uuid()).when(connector).uuid();
        spied.enableSelectionManager();
        spied.getSelectionManager().getSelectedItems().add(connector);
        spied.register(connector);
        spied.addHandlers(connector);
        spied.deregister(connector);
        assertTrue(spied.getConnectorList().isEmpty());
        assertTrue(spied.getSelectionManager().getSelectedItems().isEmpty());
        verify(handlerRegistrationManager, times(1)).removeHandler();
        verify(connector, times(1)).destroy();
    }
}