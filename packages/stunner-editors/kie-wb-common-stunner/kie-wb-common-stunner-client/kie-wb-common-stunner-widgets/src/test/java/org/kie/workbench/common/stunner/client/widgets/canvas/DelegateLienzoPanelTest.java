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


package org.kie.workbench.common.stunner.client.widgets.canvas;

import com.ait.lienzo.client.core.shape.Layer;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DelegateLienzoPanelTest {

    @Mock
    private LienzoPanel delegate;

    private DelegateLienzoPanel<LienzoPanel> tested;

    @Before
    public void init() {
        tested = new DelegateLienzoPanel<LienzoPanel>() {
            @Override
            protected LienzoPanel getDelegate() {
                return delegate;
            }
        };
    }

    @Test
    public void testShow() {
        LienzoLayer layer = mock(LienzoLayer.class);
        tested.show(layer);
        verify(delegate, times(1)).show(eq(layer));
    }

    @Test
    public void testSetBackgroundLayer() {
        Layer layer = mock(Layer.class);
        tested.setBackgroundLayer(layer);
        verify(delegate, times(1)).setBackgroundLayer(eq(layer));
    }

    @Test
    public void testFocus() {
        tested.focus();
        verify(delegate, times(1)).focus();
    }

    @Test
    public void testSizeGetters() {
        when(delegate.getWidthPx()).thenReturn(100);
        when(delegate.getHeightPx()).thenReturn(200);
        assertEquals(100, tested.getWidthPx());
        assertEquals(200, tested.getHeightPx());
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(delegate, times(1)).destroy();
    }

    @Test
    public void testAsWidget() {
        Widget widget = mock(Widget.class);
        when(delegate.asWidget()).thenReturn(widget);
        assertEquals(widget, tested.asWidget());
    }
}
