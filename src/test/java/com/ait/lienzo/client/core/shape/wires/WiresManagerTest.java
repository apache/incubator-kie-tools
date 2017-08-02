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
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndEvent;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresManagerTest
{
    private static final String LAYER_ID = "theLayer";

    private WiresManager        tested;

    private Layer               layer;

    @Before
    public void setup()
    {
        layer = spy(new Layer());
        layer.setID(LAYER_ID);
        tested = WiresManager.get(layer);
    }

    @Test
    public void testGetWiresManager()
    {
        Layer layer2 = new Layer();
        layer2.setID("layer2");
        WiresManager tested2 = WiresManager.get(layer2);
        assertEquals(tested, WiresManager.get(layer));
        assertEquals(tested2, WiresManager.get(layer2));
    }

    @Test
    public void testCreateWiresManagerInstance()
    {
        Layer layer2 = mock(Layer.class);
        when(layer2.uuid()).thenReturn("layer2");
        WiresManager manager = WiresManager.get(layer2);
        verify(layer2, times(1)).setOnLayerBeforeDraw(any(WiresManager.LinePreparer.class));
        assertNotNull(manager.getAlignAndDistribute());
        assertNotNull(manager.getLayer());
        WiresLayer wiresLayer = manager.getLayer();
        assertEquals(layer2, wiresLayer.getLayer());
    }

    @Test
    public void testRegisterShape()
    {
        IContainmentAcceptor containmentAcceptor = mock(IContainmentAcceptor.class);
        IDockingAcceptor dockingAcceptor = mock(IDockingAcceptor.class);
        tested.setContainmentAcceptor(containmentAcceptor);
        tested.setDockingAcceptor(dockingAcceptor);
        WiresManager spied = spy(tested);
        HandlerRegistrationManager handlerRegistrationManager = mock(HandlerRegistrationManager.class);
        doReturn(handlerRegistrationManager).when(spied).createHandlerRegistrationManager();
        WiresShape s = new WiresShape( new MultiPath().rect( 0, 0, 10, 10 ) );
        WiresShape shape = spy( s );
        WiresShapeControl shapeControl = spied.register(shape);
        assertNotNull(shapeControl);
        assertNotNull(tested.getShape(shape.uuid()));
        verify(shape, times(1)).setContainmentAcceptor(eq(containmentAcceptor));
        verify(shape, times(1)).setDockingAcceptor(eq(dockingAcceptor));
        verify(shape, times(1)).addWiresShapeHandler(eq( handlerRegistrationManager ), any(WiresShape.WiresShapeHandler.class));
        verify(layer, times(1)).add(eq(shape.getGroup()));
        verify(handlerRegistrationManager, times(4)).register(any(HandlerRegistration.class));
    }

    @Test
    public void testResizeShape()
    {
        Viewport viewport = mock(Viewport.class);
        when(viewport.getOverLayer()).thenReturn(mock(Layer.class));
        when(layer.getViewport()).thenReturn(viewport);
        when(layer.getLayer()).thenReturn(layer);

        ScratchPad pad = mock(ScratchPad.class);
        when(layer.getScratchPad()).thenReturn(pad);
        when(pad.getWidth()).thenReturn(10);
        when(pad.getHeight()).thenReturn(10);

        Context2D context2D = mock(Context2D.class);
        when(pad.getContext()).thenReturn(context2D);


        IContainmentAcceptor containmentAcceptor = mock(IContainmentAcceptor.class);
        tested.setContainmentAcceptor(containmentAcceptor);

        IDockingAcceptor dockingAcceptor = mock(IDockingAcceptor.class);
        tested.setDockingAcceptor(dockingAcceptor);

        WiresManager spied = spy(tested);
        HandlerRegistrationManager handlerRegistrationManager = mock(HandlerRegistrationManager.class);
        doReturn(handlerRegistrationManager).when(spied).createHandlerRegistrationManager();
        WiresShape shape = spy(new WiresShape(new MultiPath().rect(0, 0, 10, 10)));

        Group group = spy(shape.getGroup());
        when(shape.getGroup()).thenReturn(group);
        WiresShapeControl shapeControl = spied.register(shape);

        // group.getBoundingBoxAttributes are used for box calculation during shape registration and store calculated values in double primitives
        verify(group).getBoundingBoxAttributes();
        shape.getHandlerManager().fireEvent(new WiresResizeEndEvent(shape, new NodeDragMoveEvent(mock(DragContext.class)), 1, 1, 11, 11));
        // group.getBoundingBoxAttributes are used for box calculation during shape registration AND trigger re-calculation during shape resize
        verify(group, times(2)).getBoundingBoxAttributes();

        // Not possible to check used values during drag and drop for now. AlignAndDistributeControls stores calculated primitives (numbers with type double) after registration/re-sizing.
        // But calculations based on AlignAndDistribute#getBoundingBox(group); method  and use plain JSO for calculations, so we will have 0 for any calculations in our's tests.
        shapeControl.dragStart(mock(DragContext.class));
    }

    @Test
    public void testDeregisterShape()
    {
        WiresManager spied = spy(tested);
        HandlerRegistrationManager handlerRegistrationManager = mock(HandlerRegistrationManager.class);
        doReturn(handlerRegistrationManager).when(spied).createHandlerRegistrationManager();
        Group group = new Group();
        String gUUID = group.uuid();
        WiresShape s = new WiresShape( new MultiPath().rect( 0, 0, 10, 10 ) );
        WiresShape shape = spy( s );
        spied.register(shape);
        spied.deregister(shape);
        assertNull(tested.getShape(gUUID));
        verify(handlerRegistrationManager, times(1)).removeHandler();
        verify(shape, times(1)).destroy();
        // TODO: Review unnecessary calls.
        verify(layer, times(3)).remove(eq(s.getGroup()));
    }

    @Test
    public void testRegisterConnector()
    {
        IConnectionAcceptor connectionAcceptor = mock(IConnectionAcceptor.class);
        tested.setConnectionAcceptor(connectionAcceptor);
        WiresManager spied = spy(tested);
        HandlerRegistrationManager handlerRegistrationManager = mock(HandlerRegistrationManager.class);
        doReturn(handlerRegistrationManager).when(spied).createHandlerRegistrationManager();
        Group group = new Group();
        Group shapeGroup = spy(group);
        AbstractDirectionalMultiPointShape<?> line = mock(AbstractDirectionalMultiPointShape.class);
        MultiPath head = mock(MultiPath.class);
        MultiPath tail = mock(MultiPath.class);
        WiresConnector connector = mock(WiresConnector.class);
        doReturn(shapeGroup).when(connector).getGroup();
        doReturn(line).when(connector).getLine();
        doReturn(head).when(connector).getHead();
        doReturn(tail).when(connector).getTail();
        doReturn(group.uuid()).when(connector).uuid();
        WiresConnectorControl connectorControl = spied.register(connector);
        assertNotNull(connectorControl);
        assertFalse(spied.getConnectorList().isEmpty());
        verify(connector, times(1)).setConnectionAcceptor(eq(connectionAcceptor));
        verify(connector, times(1)).setWiresConnectorHandler(eq(handlerRegistrationManager), any(WiresConnector.WiresConnectorHandler.class));
        verify(connector, times(1)).addToLayer(eq(layer));
    }

    @Test
    public void testDeregisterConnector()
    {
        WiresManager spied = spy(tested);
        HandlerRegistrationManager handlerRegistrationManager = mock(HandlerRegistrationManager.class);
        doReturn(handlerRegistrationManager).when(spied).createHandlerRegistrationManager();
        Group group = new Group();
        Group shapeGroup = spy(group);
        AbstractDirectionalMultiPointShape<?> line = mock(AbstractDirectionalMultiPointShape.class);
        MultiPath head = mock(MultiPath.class);
        MultiPath tail = mock(MultiPath.class);
        WiresConnector connector = mock(WiresConnector.class);
        doReturn(shapeGroup).when(connector).getGroup();
        doReturn(line).when(connector).getLine();
        doReturn(head).when(connector).getHead();
        doReturn(tail).when(connector).getTail();
        doReturn(group.uuid()).when(connector).uuid();
        spied.register(connector);
        spied.deregister(connector);
        assertTrue(spied.getConnectorList().isEmpty());
        verify(handlerRegistrationManager, times(1)).removeHandler();
        verify(connector, times(1)).destroy();
    }
}
