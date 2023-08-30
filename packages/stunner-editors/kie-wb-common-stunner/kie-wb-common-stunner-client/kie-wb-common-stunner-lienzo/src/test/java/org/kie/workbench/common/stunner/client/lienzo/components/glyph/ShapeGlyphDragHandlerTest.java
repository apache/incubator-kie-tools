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
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler.Callback;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoPanelWidget;
import org.kie.workbench.common.stunner.core.client.shape.view.event.GWTHandlerRegistration;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
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
    private AbsolutePanel rootPanel;

    @Mock
    private HTMLDivElement proxyElement;

    @Mock
    private CSSStyleDeclaration proxyStyle;

    @Mock
    private HandlerRegistration moveHandlerReg;

    @Mock
    private HandlerRegistration upHandlerReg;

    @Mock
    private HandlerRegistration keyHandlerReg;

    private ShapeGlyphDragHandler tested;
    private GWTHandlerRegistration handlerRegistrations;
    private LienzoPanelWidget proxyPanel;
    private Group glyphGroup;

    @Before
    public void setUp() throws Exception {
        proxyPanel = spy(LienzoPanelWidget.create(DRAG_PROXY_WIDTH, DRAG_PROXY_HEIGHT));
        when(proxyPanel.getElement()).thenReturn(proxyElement);
        proxyElement.style = proxyStyle;
        handlerRegistrations = new GWTHandlerRegistration();
        glyphGroup = new Group();
        when(glyphLienzoGlyphRenderer.render(eq(glyph), anyDouble(), anyDouble())).thenReturn(glyphGroup);
        when(glyphDragItem.getHeight()).thenReturn(DRAG_PROXY_WIDTH);
        when(glyphDragItem.getWidth()).thenReturn(DRAG_PROXY_HEIGHT);
        when(glyphDragItem.getShape()).thenReturn(glyph);
        when(rootPanel.addDomHandler(any(MouseMoveHandler.class), eq(MouseMoveEvent.getType())))
                .thenReturn(moveHandlerReg);
        when(rootPanel.addDomHandler(any(MouseUpHandler.class), eq(MouseUpEvent.getType())))
                .thenReturn(upHandlerReg);
        when(rootPanel.addDomHandler(any(KeyDownHandler.class), eq(KeyDownEvent.getType())))
                .thenReturn(keyHandlerReg);
        tested = new ShapeGlyphDragHandler(glyphLienzoGlyphRenderer,
                                           handlerRegistrations,
                                           () -> rootPanel,
                                           item -> proxyPanel,
                                           (task, timeout) -> task.execute());
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
        verify(rootPanel, times(1)).add(eq(proxyPanel));
    }

    @Test
    public void testProxyhHandlers() {
        Callback callback = mock(Callback.class);
        tested.show(glyphDragItem, 11, 33, callback);

        // Check keyboard event handling.
        assertTrue(handlerRegistrations.isRegistered(keyHandlerReg));

        // Check mouse move event handling.
        assertTrue(handlerRegistrations.isRegistered(moveHandlerReg));
        MouseMoveEvent moveEvent = mock(MouseMoveEvent.class);
        when(moveEvent.getX()).thenReturn(7);
        when(moveEvent.getY()).thenReturn(9);
        when(moveEvent.getClientX()).thenReturn(3);
        when(moveEvent.getClientY()).thenReturn(5);
        tested.onMouseMove(moveEvent,
                           callback);
        assertEquals((7 + "px"), proxyStyle.left);
        assertEquals((9 + "px"), proxyStyle.top);
        verify(callback, times(1)).onMove(eq(3), eq(5));

        // Check mouse up event handling.
        assertTrue(handlerRegistrations.isRegistered(upHandlerReg));
        MouseUpEvent upEvent = mock(MouseUpEvent.class);
        when(upEvent.getX()).thenReturn(7);
        when(upEvent.getY()).thenReturn(9);
        when(upEvent.getClientX()).thenReturn(3);
        when(upEvent.getClientY()).thenReturn(5);
        tested.onMouseUp(upEvent,
                         callback);
        verify(moveHandlerReg, times(1)).removeHandler();
        verify(upHandlerReg, times(1)).removeHandler();
        verify(rootPanel, times(1)).remove(eq(proxyPanel));
        verify(callback, times(1)).onComplete(eq(3), eq(5));
        assertTrue(handlerRegistrations.isEmpty());
    }

    @Test
    public void testKeyboardHandling() {
        Callback callback = mock(Callback.class);
        tested.show(glyphDragItem, 11, 33, callback);
        assertTrue(handlerRegistrations.isRegistered(keyHandlerReg));
        assertTrue(handlerRegistrations.isRegistered(moveHandlerReg));
        assertTrue(handlerRegistrations.isRegistered(upHandlerReg));
        KeyDownEvent event = mock(KeyDownEvent.class);
        tested.onKeyDown(event);
        verify(moveHandlerReg, times(1)).removeHandler();
        verify(upHandlerReg, times(1)).removeHandler();
        verify(rootPanel, times(1)).remove(eq(proxyPanel));
        assertTrue(handlerRegistrations.isEmpty());
    }

    @Test
    public void testClear() throws Exception {
        Callback callback = mock(Callback.class);
        tested.show(glyphDragItem, 11, 33, callback);
        tested.clear();
        verify(proxyPanel, never()).destroy();
        verify(moveHandlerReg, times(1)).removeHandler();
        verify(upHandlerReg, times(1)).removeHandler();
        verify(rootPanel, times(1)).remove(eq(proxyPanel));
        assertTrue(handlerRegistrations.isEmpty());
    }

    @Test
    public void testDestroy() throws Exception {
        Callback callback = mock(Callback.class);
        tested.show(glyphDragItem, 11, 33, callback);
        tested.destroy();
        verify(proxyPanel, times(1)).destroy();
        verify(moveHandlerReg, times(1)).removeHandler();
        verify(upHandlerReg, times(1)).removeHandler();
        verify(rootPanel, times(1)).remove(eq(proxyPanel));
        assertTrue(handlerRegistrations.isEmpty());
    }
}
