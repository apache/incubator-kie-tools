/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.components.alerts;

import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoPanelFocusHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;

@Dependent
public class LienzoPanelAlerts {

    final AlertsPresenter alerts;
    LienzoPanelFocusHandler focusHandler;

    @Inject
    public LienzoPanelAlerts(final AlertsPresenter alerts) {
        this.alerts = alerts;
    }

    public LienzoPanelAlerts init(final Supplier<LienzoCanvas> canvas) {
        CanvasPanel panel = canvas.get().getView().getPanel();
        if (panel instanceof LienzoPanel) {
            focusHandler = new LienzoPanelFocusHandler()
                    .listen((LienzoPanel) canvas.get().getView().getPanel(),
                            this::enable,
                            this::disable);
            alerts.init(canvas);
        }
        return this;
    }

    public LienzoPanelAlerts addInfo(final String info) {
        alerts.addInfo(info);
        return this;
    }

    public LienzoPanelAlerts addWarning(final String warning) {
        alerts.addWarning(warning);
        return this;
    }

    public LienzoPanelAlerts addError(final String error) {
        alerts.addError(error);
        return this;
    }

    public LienzoPanelAlerts clear() {
        alerts.clear();
        return this;
    }

    public void enable() {
        alerts.show();
    }

    public void disable() {
        alerts.scheduleHide();
    }

    public void destroy() {
        if (null != focusHandler) {
            focusHandler.clear();
            focusHandler = null;
        }
        alerts.destroy();
    }
}
