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

import java.util.function.Supplier;

import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.impl.LienzoFixedPanel;
import com.ait.lienzo.client.widget.panel.impl.PreviewPanel;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class PreviewLienzoPanelTest {

    @Mock
    private StunnerLienzoBoundsPanel panel;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSession clientSession;

    @Mock
    private WiresCanvas wiresCanvas;

    @Mock
    private WiresCanvasView wiresCanvasView;

    @Mock
    private ScrollableLienzoPanel scrollableLienzoPanel;

    @Mock
    private ScrollablePanel scrollablePanel;

    @Mock
    private LienzoFixedPanel lienzoFixedPanel;

    private PreviewLienzoPanel tested;

    @Before
    public void init() {
        tested = new PreviewLienzoPanel(panel, sessionManager);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        when(sessionManager.getCurrentSession()).thenReturn(clientSession);
        when(clientSession.getCanvas()).thenReturn(wiresCanvas);
        when(wiresCanvas.getView()).thenReturn(wiresCanvasView);
        when(wiresCanvasView.getPanel()).thenReturn(scrollableLienzoPanel);
        when(scrollableLienzoPanel.getView()).thenReturn(scrollablePanel);
        when((scrollablePanel.getInternalWidth())).thenReturn(1000.0);
        when((scrollablePanel.getInternalHeight())).thenReturn(500.0);
        when((scrollablePanel.getLienzoPanel())).thenReturn(lienzoFixedPanel);
        when((scrollablePanel.getBounds())).thenReturn(Bounds.build(0.0, 0.0, 1000.0, 500.0));
        when(lienzoFixedPanel.getWidePx()).thenReturn(1000);

        tested.init();
        ArgumentCaptor<Supplier> builderCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(panel, times(1)).setPanelBuilder(builderCaptor.capture());
        Supplier<LienzoBoundsPanel> builder = builderCaptor.getValue();
        LienzoBoundsPanel result = builder.get();
        assertTrue(result instanceof PreviewPanel);

        final int expectedWidth = lienzoFixedPanel.getWidePx() / PreviewLienzoPanel.SCALE_DIVISOR;
        assertEquals(expectedWidth, result.getWidePx());

        final double diagramWidth = scrollablePanel.getBounds().getWidth();
        final double diagramHeight = scrollablePanel.getBounds().getHeight();
        final double internalRatio = diagramWidth / diagramHeight;
        final int expectedHeight = (int)(expectedWidth / internalRatio);
        assertEquals(expectedHeight, result.getHighPx());
    }

    @Test
    public void testRefresh() {
        PreviewPanel view = mock(PreviewPanel.class);
        when(panel.getView()).thenReturn(view);
        ScrollableLienzoPanel delegate = mock(ScrollableLienzoPanel.class);
        StunnerLienzoBoundsPanel delegate2 = mock(StunnerLienzoBoundsPanel.class);
        when(delegate.getDelegate()).thenReturn(delegate2);
        ScrollablePanel previewView = mock(ScrollablePanel.class);
        when(delegate2.getView()).thenReturn(previewView);
        tested.observe(delegate);
        verify(view, times(1)).observe(eq(previewView));
    }
}
