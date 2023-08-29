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


package org.kie.workbench.common.stunner.client.lienzo.components.views;

import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class LienzoPanelFocusHandlerTest {

    @Mock
    private LienzoPanel panel;

    @Mock
    private LienzoBoundsPanel panelView;

    @Mock
    private com.ait.lienzo.client.widget.panel.LienzoPanel lienzoPanel;

    @Mock
    private Command onFocus;

    @Mock
    private Command onLostFocus;

    @Mock
    private HTMLDivElement panelElement;

    private LienzoPanelFocusHandler tested;

    static final String ON_MOUSE_OVER = "mouseover";
    static final String ON_MOUSE_LEAVE = "mouseleave";

    @Before
    public void setup() {
        when(panel.getView()).thenReturn(panelView);
        when(panelView.getElement()).thenReturn(panelElement);
        when(panelView.getLienzoPanel()).thenReturn(lienzoPanel);
        tested = new LienzoPanelFocusHandler();
    }

    @Test
    public void testListen() {
        tested.listen(panel, onFocus, onLostFocus);
        Event mouseOverEvent = mock(Event.class);
        ArgumentCaptor<EventListener> overHandlerArgumentCaptor = ArgumentCaptor.forClass(EventListener.class);
        verify(panelElement, times(1)).addEventListener(eq(ON_MOUSE_OVER), overHandlerArgumentCaptor.capture());
        overHandlerArgumentCaptor.getValue().handleEvent(mouseOverEvent);
        verify(onFocus, times(1)).execute();

        Event mouseOutEvent = mock(Event.class);
        ArgumentCaptor<EventListener> outHandlerArgumentCaptor = ArgumentCaptor.forClass(EventListener.class);
        verify(panelElement, times(1)).addEventListener(eq(ON_MOUSE_LEAVE), outHandlerArgumentCaptor.capture());
        outHandlerArgumentCaptor.getValue().handleEvent(mouseOutEvent);
        verify(onLostFocus, times(1)).execute();
    }

    @Test
    public void testClear() {
        EventListener out = mock(EventListener.class);
        EventListener over = mock(EventListener.class);
        tested.panel = panelElement;
        tested.mouseOverListener = out;
        tested.mouseLeaveListener = over;
        tested.clear();
        verify(panelElement, times(1)).removeEventListener(anyString(), eq(out));
        verify(panelElement, times(1)).removeEventListener(anyString(), eq(over));
    }
}
