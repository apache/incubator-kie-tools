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

package org.kie.workbench.common.stunner.client.widgets.canvas;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.panel.BoundsProvider;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseDownEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseUpEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyPressEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyUpEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.HandlerRegistrationImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class StunnerLienzoBoundsPanelTest {

    @Mock
    private HandlerRegistrationImpl handlerRegistrationManager;

    @Mock
    private EventSourceMock<KeyPressEvent> keyPressEvent;

    @Mock
    private EventSourceMock<KeyDownEvent> keyDownEvent;

    @Mock
    private EventSourceMock<KeyUpEvent> keyUpEvent;

    @Mock
    private EventSourceMock<CanvasMouseDownEvent> mouseDownEvent;

    @Mock
    private EventSourceMock<CanvasMouseUpEvent> mouseUpEvent;

    @Mock
    private TestBoundsLienzoPanelView view;

    @Mock
    private com.ait.lienzo.client.widget.panel.LienzoPanel lienzoPanel;

    @Mock
    private LienzoLayer lienzoLayer;

    @Mock
    private Layer layer;

    private StunnerLienzoBoundsPanel tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        this.tested = new StunnerLienzoBoundsPanel(keyPressEvent,
                                                   keyDownEvent,
                                                   keyUpEvent,
                                                   mouseDownEvent,
                                                   mouseUpEvent,
                                                   handlerRegistrationManager)
                .setPanelBuilder((optionalInt, optionalInt2) -> view);
        when(view.getLienzoPanel()).thenReturn(lienzoPanel);
        when(lienzoLayer.getLienzoLayer()).thenReturn(layer);
    }

    @Test
    public void testShow() {
        tested.show(lienzoLayer,
                    300,
                    600);
        verify(view, times(1)).add(eq(layer));
        verify(view, times(1)).setPresenter(eq(tested));
        verify(lienzoPanel, times(1)).addMouseDownHandler(any(MouseDownHandler.class));
        verify(lienzoPanel, times(1)).addMouseUpHandler(any(MouseUpHandler.class));
        verify(handlerRegistrationManager, times(2)).register(any(HandlerRegistration.class));
    }

    @Test
    public void testFocus() {
        tested.setView(view)
                .focus();
        verify(view, times(1)).setFocus(eq(true));
    }

    @Test
    public void testSizeGetters() {
        tested.setView(view);
        when(lienzoPanel.getWidth()).thenReturn(100);
        when(lienzoPanel.getHeight()).thenReturn(450);
        assertEquals(100, tested.getWidth());
        assertEquals(450, tested.getHeight());
    }

    @Test
    public void testSetPixelSize() {
        tested.setView(view);
        tested.setPixelSize(100, 300);
        verify(lienzoPanel, times(1)).setPixelSize(eq(100), eq(300));
    }

    @Test
    public void testSetBackgroundLayer() {
        tested.setView(view);
        Layer bgLayer = mock(Layer.class);
        tested.setBackgroundLayer(bgLayer);
        verify(lienzoPanel, times(1)).setBackgroundLayer(eq(bgLayer));
    }

    @Test
    public void testDestroy() {
        tested.setView(view);
        tested.destroy();
        verify(handlerRegistrationManager, times(1)).destroy();
        verify(view, times(1)).destroy();
        assertNull(tested.getView());
    }

    @Test
    public void testOnMouseDown() {
        tested.onMouseDown();
        verify(mouseDownEvent, times(1)).fire(any(CanvasMouseDownEvent.class));
    }

    @Test
    public void testOnMouseUp() {
        tested.onMouseUp();
        verify(mouseUpEvent, times(1)).fire(any(CanvasMouseUpEvent.class));
    }

    @Test
    public void testOnKeyPress() {
        int unicharCode = KeyboardEvent.Key.CONTROL.getUnicharCode();
        tested.onKeyPress(unicharCode);
        ArgumentCaptor<KeyPressEvent> eventArgumentCaptor = ArgumentCaptor.forClass(KeyPressEvent.class);
        verify(keyPressEvent, times(1)).fire(eventArgumentCaptor.capture());
        KeyPressEvent keyEvent = eventArgumentCaptor.getValue();
        assertEquals(unicharCode, keyEvent.getKey().getUnicharCode());
    }

    @Test
    public void testOnKeyDown() {
        int unicharCode = KeyboardEvent.Key.CONTROL.getUnicharCode();
        tested.onKeyDown(unicharCode);
        ArgumentCaptor<KeyDownEvent> eventArgumentCaptor = ArgumentCaptor.forClass(KeyDownEvent.class);
        verify(keyDownEvent, times(1)).fire(eventArgumentCaptor.capture());
        KeyDownEvent keyEvent = eventArgumentCaptor.getValue();
        assertEquals(unicharCode, keyEvent.getKey().getUnicharCode());
    }

    @Test
    public void testOnKeyUp() {
        int unicharCode = KeyboardEvent.Key.CONTROL.getUnicharCode();
        tested.onKeyUp(unicharCode);
        ArgumentCaptor<KeyUpEvent> eventArgumentCaptor = ArgumentCaptor.forClass(KeyUpEvent.class);
        verify(keyUpEvent, times(1)).fire(eventArgumentCaptor.capture());
        KeyUpEvent keyEvent = eventArgumentCaptor.getValue();
        assertEquals(unicharCode, keyEvent.getKey().getUnicharCode());
    }

    private static class TestBoundsLienzoPanelView extends LienzoBoundsPanel implements StunnerLienzoBoundsPanelView {

        public TestBoundsLienzoPanelView(LienzoPanel lienzoPanel, BoundsProvider boundsProvider) {
            super(lienzoPanel, boundsProvider);
        }

        @Override
        public LienzoBoundsPanel onRefresh() {
            return null;
        }

        @Override
        public void setPresenter(StunnerLienzoBoundsPanel panel) {

        }
    }

    ;
}
