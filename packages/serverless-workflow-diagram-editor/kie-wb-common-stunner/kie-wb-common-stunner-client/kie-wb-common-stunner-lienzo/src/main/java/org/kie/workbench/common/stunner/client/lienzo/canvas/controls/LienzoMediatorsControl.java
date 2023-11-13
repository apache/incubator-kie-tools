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


package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.LienzoPanelMediators;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasLostFocusEvent;

@Dependent
@Default
public class LienzoMediatorsControl<C extends AbstractCanvas>
        extends AbstractCanvasControl<C>
        implements MediatorsControl<C> {

    private final LienzoPanelMediators mediators;

    @Inject
    public LienzoMediatorsControl(final LienzoPanelMediators mediators) {
        this.mediators = mediators;
    }

    @Override
    protected void doInit() {
        mediators.init(this::getCanvas);
    }

    @Override
    public MediatorsControl<C> setMinScale(final double minScale) {
        mediators.setMinScale(minScale);
        return this;
    }

    @Override
    public MediatorsControl<C> setMaxScale(final double maxScale) {
        mediators.setMaxScale(maxScale);
        return this;
    }

    @Override
    public MediatorsControl<C> setZoomFactor(final double factor) {
        mediators.setZoomFactor(factor);
        return this;
    }

    @Override
    public MediatorsControl<C> scale(final double factor) {
        getLayer().scale(factor);
        return this;
    }

    @Override
    public MediatorsControl<C> scale(final double sx,
                                     final double sy) {
        getLayer().scale(sx, sy);
        return this;
    }

    @Override
    public MediatorsControl<C> translate(final double tx,
                                         final double ty) {
        getLayer().translate(tx, ty);
        return this;
    }

    @Override
    protected void doDestroy() {
        super.doDestroy();
        mediators.destroy();
    }

    void onCanvasFocusedEvent(final @Observes CanvasFocusedEvent focusedEvent) {
        if (null != canvas && canvas.equals(focusedEvent.getCanvas())) {
            mediators.enable();
        }
    }

    void onCanvasLostFocusEvent(final @Observes CanvasLostFocusEvent lostFocusEvent) {
        if (null != canvas && canvas.equals(lostFocusEvent.getCanvas())) {
            mediators.disable();
        }
    }

    private WiresCanvas getCanvas() {
        return (WiresCanvas) canvas;
    }

    private LienzoLayer getLayer() {
        final WiresCanvas wiresCanvas = getCanvas();
        final WiresCanvasView view = wiresCanvas.getView();
        return view.getLayer();
    }
}
