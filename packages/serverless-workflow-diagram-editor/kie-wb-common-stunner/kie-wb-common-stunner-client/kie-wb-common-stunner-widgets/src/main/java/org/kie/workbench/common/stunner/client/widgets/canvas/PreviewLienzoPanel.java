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

import com.ait.lienzo.client.widget.panel.impl.PreviewPanel;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;

@Dependent
@Typed(PreviewLienzoPanel.class)
public class PreviewLienzoPanel
        extends DelegateLienzoPanel<StunnerLienzoBoundsPanel> {

    public static final int SCALE_DIVISOR = 5;

    private final StunnerLienzoBoundsPanel panel;
    private final SessionManager sessionManager;

    @Inject
    public PreviewLienzoPanel(final StunnerLienzoBoundsPanel panel,
                              final SessionManager sessionManager) {
        this.panel = panel;
        this.sessionManager = sessionManager;
    }

    @PostConstruct
    public void init() {
        ClientSession session = sessionManager.getCurrentSession();
        WiresCanvas wiresCanvas = (WiresCanvas) session.getCanvas();
        WiresCanvasView wiresCanvasView = wiresCanvas.getView();
        ScrollableLienzoPanel scrollableLienzoPanel = (ScrollableLienzoPanel) wiresCanvasView.getPanel();
        ScrollablePanel scrollablePanel = scrollableLienzoPanel.getView();

        final double diagramWidth = scrollablePanel.getBounds().getWidth();
        final double diagramHeight = scrollablePanel.getBounds().getHeight();
        final double internalRatio = diagramWidth / diagramHeight;
        final double panelWidth = scrollablePanel.getLienzoPanel().getWidePx();
        final int width = (int)panelWidth / SCALE_DIVISOR;
        final int height = (int)(width / internalRatio);

        PreviewPanel previewPanel = new PreviewPanel(width, height);
        panel.setPanelBuilder(() -> previewPanel);
    }

    public PreviewLienzoPanel observe(final ScrollableLienzoPanel panel) {
        ((PreviewPanel) getDelegate().getView())
                .observe((ScrollablePanel) panel.getDelegate().getView());

        return this;
    }

    @Override
    protected StunnerLienzoBoundsPanel getDelegate() {
        return panel;
    }
}
