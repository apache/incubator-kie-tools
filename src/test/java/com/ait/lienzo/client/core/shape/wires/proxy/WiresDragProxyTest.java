/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.shape.wires.proxy;

import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveHandler;
import com.ait.lienzo.client.core.event.NodeMouseOutEvent;
import com.ait.lienzo.client.core.event.NodeMouseOutHandler;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.common.api.java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresDragProxyTest {

    @Mock
    private AbstractWiresProxy proxy;

    private WiresDragProxy tested;
    private Layer layer;
    private Layer proxyLayer;
    private Scene scene;
    private Viewport viewport;

    private NodeMouseUpHandler upHandler;
    private NodeMouseOutHandler outHandler;
    private NodeMouseExitHandler exitHandler;
    private NodeMouseMoveHandler moveHandler;

    @Before
    public void setup() {
        layer = spy(new Layer());
        proxyLayer = spy(new Layer());
        scene = spy(new Scene());
        viewport = new Viewport(scene, 600, 600);
        Transform transform = new Transform().translate(2d, 3d);
        when(proxy.getLayer()).thenReturn(layer);
        when(layer.getScene()).thenReturn(scene);
        when(layer.getViewport()).thenReturn(viewport);
        viewport.setTransform(transform);

        tested = new WiresDragProxy(new Supplier<AbstractWiresProxy>() {
            @Override
            public AbstractWiresProxy get() {
                return proxy;
            }
        },
                                    new Supplier<Layer>() {
                                        @Override
                                        public Layer get() {
                                            return proxyLayer;
                                        }
                                    });
    }

    @Test
    public void testEnable() {
        double x = 1d;
        double y = 3d;
        tested.enable(x, y);
        initAndVerifyHandlers();
        verify(scene, times(1)).add(eq(proxyLayer));
        assertTrue(proxyLayer.isListening());
        assertFalse(layer.isListening());
        verify(proxy, times(1)).start(eq(-1.0d), eq(0d));
    }

    @Test
    public void testMove() {
        double x = 1d;
        double y = 3d;
        tested.enable(x, y);
        initAndVerifyHandlers();
        NodeMouseMoveEvent event = mock(NodeMouseMoveEvent.class);
        when(event.getX()).thenReturn(2);
        when(event.getY()).thenReturn(1);
        moveHandler.onNodeMouseMove(event);
        verify(proxy, times(1)).move(1d, -2d);
        verify(proxyLayer, atLeastOnce()).moveToTop();
    }

    @Test
    public void testEndByMouseUp() {
        double x = 1d;
        double y = 3d;
        tested.enable(x, y);
        initAndVerifyHandlers();
        NodeMouseUpEvent event = mock(NodeMouseUpEvent.class);
        when(event.getX()).thenReturn(2);
        when(event.getY()).thenReturn(1);
        upHandler.onNodeMouseUp(event);
        verify(proxy, times(1)).end();
        verify(proxyLayer, atLeastOnce()).removeFromParent();
        assertTrue(layer.isListening());
    }

    @Test
    public void testEndByMouseExit() {
        double x = 1d;
        double y = 3d;
        tested.enable(x, y);
        initAndVerifyHandlers();
        NodeMouseExitEvent event = mock(NodeMouseExitEvent.class);
        when(event.getX()).thenReturn(2);
        when(event.getY()).thenReturn(1);
        exitHandler.onNodeMouseExit(event);
        verify(proxy, times(1)).end();
        verify(proxyLayer, atLeastOnce()).removeFromParent();
        assertTrue(layer.isListening());
    }

    @Test
    public void testEndByMouseOut() {
        double x = 1d;
        double y = 3d;
        tested.enable(x, y);
        initAndVerifyHandlers();
        NodeMouseOutEvent event = mock(NodeMouseOutEvent.class);
        when(event.getX()).thenReturn(2);
        when(event.getY()).thenReturn(1);
        outHandler.onNodeMouseOut(event);
        verify(proxy, times(1)).end();
        verify(proxyLayer, atLeastOnce()).removeFromParent();
        assertTrue(layer.isListening());
    }

    private void initAndVerifyHandlers() {
        ArgumentCaptor<NodeMouseUpHandler> upHandlerCaptor = ArgumentCaptor.forClass(NodeMouseUpHandler.class);
        verify(proxyLayer, times(1)).addNodeMouseUpHandler(upHandlerCaptor.capture());
        upHandler = upHandlerCaptor.getValue();
        ArgumentCaptor<NodeMouseOutHandler> outHandlerCaptor = ArgumentCaptor.forClass(NodeMouseOutHandler.class);
        verify(proxyLayer, times(1)).addNodeMouseOutHandler(outHandlerCaptor.capture());
        outHandler = outHandlerCaptor.getValue();
        ArgumentCaptor<NodeMouseExitHandler> exitHandlerCaptor = ArgumentCaptor.forClass(NodeMouseExitHandler.class);
        verify(proxyLayer, times(1)).addNodeMouseExitHandler(exitHandlerCaptor.capture());
        exitHandler = exitHandlerCaptor.getValue();
        ArgumentCaptor<NodeMouseMoveHandler> moveHandlerCaptor = ArgumentCaptor.forClass(NodeMouseMoveHandler.class);
        verify(proxyLayer, times(1)).addNodeMouseMoveHandler(moveHandlerCaptor.capture());
        moveHandler = moveHandlerCaptor.getValue();
        assertNotNull(upHandler);
        assertNotNull(outHandler);
        assertNotNull(exitHandler);
        assertNotNull(moveHandler);
    }
}
