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

package org.kie.workbench.common.stunner.client.lienzo.components.views;

import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

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

    private LienzoPanelFocusHandler tested;

    @Before
    public void setup() {
        when(panel.getView()).thenReturn(panelView);
        when(panelView.getLienzoPanel()).thenReturn(lienzoPanel);
        tested = new LienzoPanelFocusHandler();
    }

    @Test
    public void testListen() {
        tested.listen(panel, onFocus, onLostFocus);
        ArgumentCaptor<MouseOverHandler> overHandlerArgumentCaptor = ArgumentCaptor.forClass(MouseOverHandler.class);
        verify(panelView, times(1)).addMouseOverHandler(overHandlerArgumentCaptor.capture());
        overHandlerArgumentCaptor.getValue().onMouseOver(mock(MouseOverEvent.class));
        verify(onFocus, times(1)).execute();
        ArgumentCaptor<MouseOutHandler> outHandlerArgumentCaptor = ArgumentCaptor.forClass(MouseOutHandler.class);
        verify(panelView, times(1)).addMouseOutHandler(outHandlerArgumentCaptor.capture());
        outHandlerArgumentCaptor.getValue().onMouseOut(mock(MouseOutEvent.class));
        verify(onLostFocus, times(1)).execute();
    }

    @Test
    public void testClear() {
        HandlerRegistration out = mock(HandlerRegistration.class);
        HandlerRegistration over = mock(HandlerRegistration.class);
        tested.outHandler = out;
        tested.overHandler = over;
        tested.clear();
        verify(out, times(1)).removeHandler();
        verify(over, times(1)).removeHandler();
    }
}
