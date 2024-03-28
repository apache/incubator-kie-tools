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


package org.kie.workbench.common.stunner.lienzo.flowpanel;

import java.util.Objects;

import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import org.gwtproject.timer.client.Timer;
import org.kie.j2cl.tools.di.core.IsElement;

/**
 * Floating view implementation for generic J2CL Widgets.
 */
public class FloatingWidgetView implements FloatingView<IsElement> {

    private double ox;
    private double oy;
    private double x;
    private double y;
    private boolean attached;
    private Timer timer;
    private int timeout = 800;
    private boolean visible;
    private Runnable hideCallback;
    private final FlowPanel panel = new FlowPanel();

    public FloatingWidgetView() {
        this.attached = false;
        this.ox = 0;
        this.oy = 0;
        this.visible = false;
        this.hideCallback = () -> {
        };
    }

    @Override
    public void add(final IsElement item) {
        panel.add(item);
    }

    @Override
    public FloatingView<IsElement> setOffsetX(final double ox) {
        this.ox = ox;
        reposition();
        return this;
    }

    @Override
    public FloatingView<IsElement> setOffsetY(final double oy) {
        this.oy = oy;
        reposition();
        return this;
    }

    @Override
    public FloatingWidgetView setX(final double x) {
        this.x = x;
        reposition();
        return this;
    }

    @Override
    public FloatingWidgetView setY(final double y) {
        this.y = y;
        reposition();
        return this;
    }

    @Override
    public FloatingWidgetView setTimeOut(final int timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public FloatingView<IsElement> clearTimeOut() {
        setTimeOut(-1);
        return this;
    }

    @Override
    public FloatingView<IsElement> setHideCallback(final Runnable hideCallback) {
        Objects.requireNonNull(hideCallback, "Parameter named 'hideCallback' should be not null!");
        this.hideCallback = hideCallback;
        return this;
    }

    @Override
    public void clear() {
        panel.clear();
    }

    @Override
    public FloatingWidgetView show() {
        if (!isVisible()) {
            visible = true;
            attach();
            startTimeout();
            reposition();
            doShow();
        }
        return this;
    }

    @Override
    public FloatingWidgetView hide() {
        if (isVisible()) {
            this.visible = false;
            stopTimeout();
            doHide();
        }
        return this;
    }

    protected void doShow() {
        panel.getElement().style.display = "inline";
    }

    protected void doHide() {
        panel.getElement().style.display = "none";
        hideCallback.run();
    }

    private void attach() {
        if (!attached) {
            DomGlobal.document.body.appendChild(panel.getElement());
            panel.getElement().style.position = "fixed";
            panel.getElement().style.zIndex = CSSProperties.ZIndexUnionType.of(Integer.MAX_VALUE);
            doHide();
            attached = true;
        }
    }

    @Override
    public void destroy() {
        stopTimeout();
        detach();
        timer = null;
        hideCallback = null;
    }

    private void detach() {
        if (attached) {
            DomGlobal.document.body.removeChild(panel.getElement());
            attached = false;
        }
    }

    private void reposition() {
        panel.getElement().style.left = (ox + x) + "px";
        panel.getElement().style.top = (oy + y) + "px";
    }

    private boolean isVisible() {
        return visible;
    }

    public void startTimeout() {
        if (timeout > 0 &&
                (null == timer || !timer.isRunning())) {
            timer = new Timer() {
                @Override
                public void run() {
                    FloatingWidgetView.this.hide();
                }
            };
            timer.schedule(timeout);
        }
    }

    public void stopTimeout() {
        if (null != timer && timer.isRunning()) {
            timer.cancel();
        }
    }

    public FlowPanel getPanel() {
        return panel;
    }
}
