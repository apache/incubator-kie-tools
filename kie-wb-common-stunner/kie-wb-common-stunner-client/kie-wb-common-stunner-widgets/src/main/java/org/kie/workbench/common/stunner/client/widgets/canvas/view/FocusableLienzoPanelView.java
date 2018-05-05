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
package org.kie.workbench.common.stunner.client.widgets.canvas.view;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * LienzoPanel that can take the Focus (and more importantly cause other Widgets to loose the Focus)
 */
public class FocusableLienzoPanelView extends com.ait.lienzo.client.widget.LienzoPanel {

    private final HandlerRegistration mouseDownHandlerReg;
    private final HandlerRegistration mouseWheelHandlerReg;

    public FocusableLienzoPanelView(final int width,
                                    final int height) {
        super(width,
              height);
        //Basic support to loose focus on other Widgets when a WiresCanvas is clicked
        mouseDownHandlerReg = addMouseDownHandler(event -> broadcastBlurEvent());
        mouseWheelHandlerReg = addMouseWheelHandler(event -> broadcastBlurEvent());
    }

    protected void broadcastBlurEvent() {
        final NativeEvent blur = Document.get().createBlurEvent();
        for (int i = 0; i < RootPanel.get().getWidgetCount(); i++) {
            final Widget w = RootPanel.get().getWidget(i);
            DomEvent.fireNativeEvent(blur,
                                     w);
        }
    }

    public void destroy() {
        mouseDownHandlerReg.removeHandler();
        mouseWheelHandlerReg.removeHandler();
        super.destroy();
    }
}
