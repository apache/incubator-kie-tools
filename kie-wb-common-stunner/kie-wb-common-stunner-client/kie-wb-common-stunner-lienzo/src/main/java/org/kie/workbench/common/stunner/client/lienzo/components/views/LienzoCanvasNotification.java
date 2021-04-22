/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.components.views;

import java.util.function.Supplier;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.shared.HandlerRegistration;
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
    HandlerRegistration outHandler;

    @Inject
    public LienzoCanvasNotification(final View view) {
        this.view = view;
    }

    public void init(final Supplier<LienzoPanel> panel) {
        this.panel = panel;
        this.outHandler = panel.get().getView().addMouseOutHandler(mouseOutEvent -> hide());
    }

    public void show(final String text) {
        final LienzoPanel p = panel.get();
        final int absoluteLeft = p.getView().getAbsoluteLeft();
        final int absoluteTop = p.getView().getAbsoluteTop();
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
        if (null != outHandler) {
            outHandler.removeHandler();
            outHandler = null;
        }
        panel = null;
    }
}
