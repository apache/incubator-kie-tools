/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.canvas.wires;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.client.lienzo.Lienzo;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.widgets.canvas.view.LienzoPanel;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasDrawnEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;

@Dependent
public class WiresCanvasPresenter extends WiresCanvas implements IsWidget {

    private static final int PADDING = 15;

    private final LienzoPanel lienzoPanel;

    protected WiresCanvasPresenter() {
        this(null,
             null,
             null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public WiresCanvasPresenter(final Event<CanvasClearEvent> canvasClearEvent,
                                final Event<CanvasShapeAddedEvent> canvasShapeAddedEvent,
                                final Event<CanvasShapeRemovedEvent> canvasShapeRemovedEvent,
                                final Event<CanvasDrawnEvent> canvasDrawnEvent,
                                final Event<CanvasFocusedEvent> canvasFocusedEvent,
                                final @Lienzo Layer layer,
                                final @Default WiresCanvas.View view,
                                final LienzoPanel lienzoPanel) {
        super(canvasClearEvent,
              canvasShapeAddedEvent,
              canvasShapeRemovedEvent,
              canvasDrawnEvent,
              canvasFocusedEvent,
              layer,
              view);
        this.lienzoPanel = lienzoPanel;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public Canvas initialize(final int width,
                             final int height) {
        super.initialize(width,
                         height);
        lienzoPanel.show(width,
                         height,
                         PADDING);
        // Show a decorator to represent the diagram's bounds.
        view
                .setDecoratorStrokeWidth(1)
                .setDecoratorStrokeAlpha(0.8)
                .setDecoratorStrokeColor("#d3d3d3");
        show(lienzoPanel.asWidget(),
             width,
             height,
             layer);
        layer.onAfterDraw(WiresCanvasPresenter.this::afterDrawCanvas);
        return this;
    }

    @Override
    public void addControl(final IsWidget control) {
        view.add(control);
    }

    @Override
    public void deleteControl(final IsWidget control) {
        view.remove(control);
    }

    @Override
    public void destroy() {
        super.destroy();
        lienzoPanel.destroy();
    }

    public LienzoPanel getLienzoPanel() {
        return lienzoPanel;
    }
}
