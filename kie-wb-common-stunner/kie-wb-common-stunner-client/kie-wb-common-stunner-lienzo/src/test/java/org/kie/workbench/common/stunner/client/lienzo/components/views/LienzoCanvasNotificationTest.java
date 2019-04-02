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
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.mockito.Mock;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
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

    private LienzoCanvasNotification tested;

    @Before
    public void setup() {
        when(panel.getView()).thenReturn(panelView);
        tested = new LienzoCanvasNotification(view);
    }

    @Test
    public void testInit() {
        tested.init(() -> panel);
        verify(panelView, times(1)).addMouseOutHandler(any(MouseOutHandler.class));
    }

    @Test
    public void testShow() {
        when(panel.getWidthPx()).thenReturn(1200);
        when(panel.getHeightPx()).thenReturn(600);
        when(panelView.getAbsoluteLeft()).thenReturn(5);
        when(panelView.getAbsoluteTop()).thenReturn(10);
        tested.init(() -> panel);
        tested.show("some text");
        verify(view, times(1)).setText(eq("some text"));
        verify(view, times(1)).at(560d, 60d);
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
        HandlerRegistration r = mock(HandlerRegistration.class);
        tested.outHandler = r;
        tested.destroy();
        verify(r, times(1)).removeHandler();
        assertNull(tested.outHandler);
        assertNull(tested.panel);
    }
}
