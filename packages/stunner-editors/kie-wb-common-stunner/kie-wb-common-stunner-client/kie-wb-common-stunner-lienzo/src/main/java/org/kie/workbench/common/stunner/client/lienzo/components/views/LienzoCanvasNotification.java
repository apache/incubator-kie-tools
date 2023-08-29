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


package org.kie.workbench.common.stunner.client.lienzo.components.views;

import java.util.function.Supplier;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.tools.client.event.MouseEventUtil;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;

@Dependent
public class LienzoCanvasNotification {

    public interface View {

        void setText(String text);

        void at(double x, double y);

        void show();

        void hide();
    }

    private final View view;
    Supplier<LienzoPanel> panel;
    EventListener mouseLeaveEventListener;
    static final String ON_MOUSE_LEAVE = "mouseleave";

    @Inject
    public LienzoCanvasNotification(final View view) {
        this.view = view;
    }

    public void init(final Supplier<LienzoPanel> panel) {
        this.panel = panel;
        this.mouseLeaveEventListener = e -> hide();
        getPanelElement().addEventListener(ON_MOUSE_LEAVE, mouseLeaveEventListener);
    }

    public void show(final String text) {
        final LienzoPanel p = panel.get();

        final int absoluteLeft = MouseEventUtil.getAbsoluteLeft(p.getView().getElement());
        final int absoluteTop = MouseEventUtil.getAbsoluteTop(p.getView().getElement());
        final int width = p.getWidthPx();
        final double x = absoluteLeft + (width / 2) - (5 * text.length());
        final double y = absoluteTop + 50;
        view.at(x, y);
        view.setText(text);
        view.show();
    }

    public void hide() {
        view.setText("");
        view.hide();
    }

    @PreDestroy
    public void destroy() {
        if (null != mouseLeaveEventListener) {
            getPanelElement().removeEventListener(ON_MOUSE_LEAVE, mouseLeaveEventListener);
        }
        mouseLeaveEventListener = null;
        panel = null;
    }

    private HTMLDivElement getPanelElement() {
        return panel.get().getView().getElement();
    }
}
