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
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.components.alerts.LienzoPanelAlerts;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AlertsControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasLostFocusEvent;

@Dependent
@Default
public class LienzoAlertsControl<C extends AbstractCanvas>
        extends AbstractCanvasControl<C>
        implements AlertsControl<C> {

    private final LienzoPanelAlerts alerts;

    @Inject
    public LienzoAlertsControl(final LienzoPanelAlerts alerts) {
        this.alerts = alerts;
    }

    @Override
    protected void doInit() {
        alerts.init(this::getCanvas);
    }

    @Override
    public AlertsControl<C> addInfo(String info) {
        alerts.addInfo(info);
        return this;
    }

    @Override
    public AlertsControl<C> addWarning(String warning) {
        alerts.addWarning(warning);
        return this;
    }

    @Override
    public AlertsControl<C> addError(String error) {
        alerts.addError(error);
        return this;
    }

    @Override
    public AlertsControl<C> clear() {
        alerts.clear();
        return this;
    }

    @Override
    protected void doDestroy() {
        super.doDestroy();
        alerts.destroy();
    }

    void onCanvasFocusedEvent(final @Observes CanvasFocusedEvent focusedEvent) {
        if (null != canvas && canvas.equals(focusedEvent.getCanvas())) {
            alerts.enable();
        }
    }

    void onCanvasLostFocusEvent(final @Observes CanvasLostFocusEvent lostFocusEvent) {
        if (null != canvas && canvas.equals(lostFocusEvent.getCanvas())) {
            alerts.disable();
        }
    }

    private WiresCanvas getCanvas() {
        return (WiresCanvas) canvas;
    }
}
