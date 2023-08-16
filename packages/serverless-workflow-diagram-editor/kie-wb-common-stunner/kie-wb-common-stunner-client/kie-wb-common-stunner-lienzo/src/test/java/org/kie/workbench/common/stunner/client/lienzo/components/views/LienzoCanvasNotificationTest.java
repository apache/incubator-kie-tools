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

import java.util.function.Supplier;

import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.mockito.Mock;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class LienzoCanvasNotificationTest {

    @Mock
    private LienzoCanvasNotification.View view;

    @Mock
    private LienzoPanel panel;

    @Mock
    private LienzoBoundsPanel panelView;

    @Mock
    private HTMLDivElement htmlDivElement;

    private LienzoCanvasNotification tested;

    @Before
    public void setup() {
        when(panel.getView()).thenReturn(panelView);
        tested = new LienzoCanvasNotification(view);
        when(panelView.getElement()).thenReturn(htmlDivElement);
    }

    @Test
    public void testInit() {
        tested.init(() -> panel);
        verify(htmlDivElement, times(1)).addEventListener(any(), any(EventListener.class));
    }

    @Test
    public void testShow() {
        when(panel.getWidthPx()).thenReturn(1200);
        when(panel.getHeightPx()).thenReturn(600);
        tested.init(() -> panel);
        tested.show("some text");
        verify(view, times(1)).setText(eq("some text"));
        verify(view, times(1)).at(555d, 50d);
        verify(view, times(1)).show();
    }

    @Test
    public void testHide() {
        tested.hide();
        verify(view, times(1)).setText(eq(""));
        verify(view, times(1)).hide();
    }

    @Test
    public void testDestroy() {
        Supplier<LienzoPanel> panelSupplier = mock(Supplier.class);
        when(panelSupplier.get()).thenReturn(panel);
        tested.panel = panelSupplier;
        EventListener e = mock(EventListener.class);
        tested.mouseLeaveEventListener = e;
        tested.destroy();
        verify(htmlDivElement, times(1)).removeEventListener(any(), eq(e));
        assertNull(tested.mouseLeaveEventListener);
        assertNull(tested.panel);
    }

    @Test
    public void testDestroyEventListenerNull() {
        Supplier<LienzoPanel> panelSupplier = mock(Supplier.class);
        when(panelSupplier.get()).thenReturn(panel);
        tested.panel = panelSupplier;
        tested.mouseLeaveEventListener = null;
        tested.destroy();
        verify(htmlDivElement, times(0)).removeEventListener(any(), any());
        assertNull(tested.mouseLeaveEventListener);
        assertNull(tested.panel);
    }
}
