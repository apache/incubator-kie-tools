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


package org.kie.workbench.common.stunner.core.client.components.views;

import java.util.Objects;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import jakarta.enterprise.context.Dependent;
import org.gwtproject.timer.client.Timer;
import org.kie.j2cl.tools.di.core.IsElement;
import org.kie.workbench.common.stunner.core.client.shape.view.event.NativeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.NativeHandlerRegistration;
import org.uberfire.mvp.Command;

import static elemental2.dom.CSSProperties.ZIndexUnionType;
import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;

/**
 * Floating view implementation for generic GWT Widgets.
 */
@Dependent
public class FloatingWidgetView implements FloatingView<IsElement> {

    private double ox;
    private double oy;
    private double x;
    private double y;
    private boolean attached;
    private Timer timer;
    private int timeout = 800;
    private boolean visible;
    private Command hideCallback;
    private final HTMLDivElement panel = (HTMLDivElement) DomGlobal.document.createElement("div");
    private final NativeHandlerRegistration handlerRegistrationManager = new NativeHandlerRegistration();

    private static final String MOUSE_OVER = "mouseover";
    private static final String MOUSE_OUT = "mouseout";

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
        panel.appendChild(item.getElement());
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
    public FloatingView<IsElement> setHideCallback(final Command hideCallback) {
        Objects.requireNonNull(hideCallback, "Parameter named 'hideCallback' should be not null!");
        this.hideCallback = hideCallback;
        return this;
    }

    @Override
    public void clear() {
        removeAllChildren(panel);
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
        panel.style.display = "inline";
    }

    protected void doHide() {
        panel.style.display = "none";
        hideCallback.execute();
    }

    private void attach() {
        if (!attached) {
            getRootPanel().appendChild(panel);
            //RootPanel.get().add(panel);
            registerHoverEventHandlers();
            panel.style.position = "fixed";
            panel.style.zIndex = ZIndexUnionType.of(Integer.MAX_VALUE);
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
            DomGlobal.document.body.removeChild(panel);
            attached = false;
        }
    }

    private void reposition() {
        panel.style.left = ox + x + "px";
        panel.style.top = oy + y + "px";
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

    protected HTMLElement getPanel() {
        return panel;
    }

    public HTMLElement getRootPanel() {
        return DomGlobal.document.body;
    }

    private void registerHoverEventHandlers() {
        final NativeHandler mouseOverHandler = new NativeHandler(MOUSE_OVER,
                                                                 mouseOverEvent -> stopTimeout(),
                                                                 panel).add();

        final NativeHandler mouseOutHandler = new NativeHandler(MOUSE_OUT,
                                                                mouseOutEvent -> startTimeout(),
                                                                panel).add();

        handlerRegistrationManager.register(mouseOverHandler);
        handlerRegistrationManager.register(mouseOutHandler);
    }
}
