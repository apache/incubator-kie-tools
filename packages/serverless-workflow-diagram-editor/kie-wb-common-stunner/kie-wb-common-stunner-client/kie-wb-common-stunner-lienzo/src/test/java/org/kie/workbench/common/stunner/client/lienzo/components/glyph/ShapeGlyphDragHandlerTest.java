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


package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.Event;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler.Callback;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoPanelWidget;
import org.kie.workbench.common.stunner.core.client.shape.view.event.NativeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.NativeHandlerRegistration;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ShapeGlyphDragHandlerTest {

    private static final int DRAG_PROXY_WIDTH = 150;
    private static final int DRAG_PROXY_HEIGHT = 300;

    @Mock
    private LienzoGlyphRenderers glyphLienzoGlyphRenderer;

    @Mock
    private Glyph glyph;

    @Mock
    private ShapeGlyphDragHandler.Item glyphDragItem;

    @Mock
    private HTMLDivElement proxyElement;

    @Mock
    private CSSStyleDeclaration proxyStyle;

    @Mock
    private NativeHandler moveHandlerReg;

    @Mock
    private NativeHandler upHandlerReg;

    @Mock
    private NativeHandler keyHandlerReg;

    @Mock
    private HTMLElement body;

    private ShapeGlyphDragHandler tested;
    private NativeHandlerRegistration handlerRegistrations;
    private LienzoPanelWidget proxyPanel;
    private Group glyphGroup;

    @Before
    public void setUp() throws Exception {
        proxyPanel = spy(LienzoPanelWidget.create(DRAG_PROXY_WIDTH, DRAG_PROXY_HEIGHT));
        when(proxyPanel.getElement()).thenReturn(proxyElement);
        proxyElement.style = proxyStyle;
        handlerRegistrations = new NativeHandlerRegistration();
        glyphGroup = new Group();
        when(glyphLienzoGlyphRenderer.render(eq(glyph), anyDouble(), anyDouble())).thenReturn(glyphGroup);
        when(glyphDragItem.getHeight()).thenReturn(DRAG_PROXY_WIDTH);
        when(glyphDragItem.getWidth()).thenReturn(DRAG_PROXY_HEIGHT);
        when(glyphDragItem.getShape()).thenReturn(glyph);
        tested = spy(new ShapeGlyphDragHandler(glyphLienzoGlyphRenderer,
                                               handlerRegistrations,
                                               () -> body,
                                               item -> proxyPanel,
                                               (task, timeout) -> task.execute()));
        doNothing().when(tested).attachHandlers(any(Callback.class));
    }

    @Test
    public void testShowProxy() {
        tested.show(glyphDragItem, 11, 33, mock(Callback.class));
        ArgumentCaptor<Layer> layerArgumentCaptor = ArgumentCaptor.forClass(Layer.class);
        verify(proxyPanel, times(1)).add(layerArgumentCaptor.capture());
        Layer layer = layerArgumentCaptor.getValue();

        assertEquals(glyphGroup, layer.getChildNodes().get(0));
        assertEquals("auto", proxyStyle.cursor);
        assertEquals("absolute", proxyStyle.position);
        assertEquals((11d + "px"), proxyStyle.left);
        assertEquals((33d + "px"), proxyStyle.top);
        verify(body, times(1)).appendChild(proxyPanel.getElement());
    }

    @Test
    public void testProxyHandlers() {
        Callback callback = mock(Callback.class);

        attachHandlers();
        tested.show(glyphDragItem, 11, 33, callback);

        // Check keyboard event handling.
        assertTrue(handlerRegistrations.isRegistered(keyHandlerReg));

        // Check mouse move event handling.
        assertTrue(handlerRegistrations.isRegistered(moveHandlerReg));

        MouseEvent moveEvent = mock(MouseEvent.class);
        moveEvent.type = ShapeGlyphDragHandler.MOUSE_MOVE;
        moveEvent.x = 7;
        moveEvent.y = 9;
        moveEvent.clientX = 3;
        moveEvent.clientY = 5;

        tested.onMouseMove(moveEvent,
                           callback);
        assertEquals((7 + "px"), proxyStyle.left);
        assertEquals((9 + "px"), proxyStyle.top);
        verify(callback, times(1)).onMove(eq(3), eq(5));

        // Check mouse up event handling.
        assertTrue(handlerRegistrations.isRegistered(upHandlerReg));
        MouseEvent upEvent = mock(MouseEvent.class);
        upEvent.type = ShapeGlyphDragHandler.MOUSE_UP;
        upEvent.x = 7;
        upEvent.y = 9;
        upEvent.clientX = 3;
        upEvent.clientY = 5;
        tested.onMouseUp(upEvent,
                         callback);
        verify(moveHandlerReg, times(1)).removeHandler();
        verify(upHandlerReg, times(1)).removeHandler();
        verify(body, times(1)).removeChild(proxyPanel.getElement());
        verify(callback, times(1)).onComplete(eq(3), eq(5));
        assertTrue(handlerRegistrations.isEmpty());
    }

    @Test
    public void testKeyboardHandling() {
        Callback callback = mock(Callback.class);
        attachHandlers();
        tested.show(glyphDragItem, 11, 33, callback);
        assertTrue(handlerRegistrations.isRegistered(keyHandlerReg));
        assertTrue(handlerRegistrations.isRegistered(moveHandlerReg));
        assertTrue(handlerRegistrations.isRegistered(upHandlerReg));
        Event event = mock(Event.class);
        event.type = ShapeGlyphDragHandler.KEY_DOWN;
        tested.onKeyDown(event);
        verify(moveHandlerReg, times(1)).removeHandler();
        verify(upHandlerReg, times(1)).removeHandler();
        verify(body, times(1)).removeChild(proxyPanel.getElement());
        assertTrue(handlerRegistrations.isEmpty());
    }

    @Test
    public void testClear() throws Exception {
        Callback callback = mock(Callback.class);
        attachHandlers();
        tested.show(glyphDragItem, 11, 33, callback);
        tested.clear();
        verify(proxyPanel, never()).destroy();
        verify(moveHandlerReg, times(1)).removeHandler();
        verify(upHandlerReg, times(1)).removeHandler();
        verify(body, times(1)).removeChild(proxyPanel.getElement());
        assertTrue(handlerRegistrations.isEmpty());
    }

    @Test
    public void testDestroy() throws Exception {
        Callback callback = mock(Callback.class);
        attachHandlers();
        tested.show(glyphDragItem, 11, 33, callback);
        tested.destroy();
        verify(proxyPanel, times(1)).destroy();
        verify(moveHandlerReg, times(1)).removeHandler();
        verify(upHandlerReg, times(1)).removeHandler();
        verify(body, times(1)).removeChild(proxyPanel.getElement());
        assertTrue(handlerRegistrations.isEmpty());
    }

    private void attachHandlers() {
        tested.mouseMoveHandler = moveHandlerReg;
        tested.mouseUpHandler = upHandlerReg;
        tested.keyDownHandler = keyHandlerReg;
        handlerRegistrations.register(tested.mouseMoveHandler);
        handlerRegistrations.register(tested.mouseUpHandler);
        handlerRegistrations.register(tested.keyDownHandler);
    }
}
