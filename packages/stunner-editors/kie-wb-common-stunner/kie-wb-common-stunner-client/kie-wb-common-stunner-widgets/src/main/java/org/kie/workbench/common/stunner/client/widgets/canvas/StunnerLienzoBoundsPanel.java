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

import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.EventListener;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseDownEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseUpEvent;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

@Dependent
@Typed(StunnerLienzoBoundsPanel.class)
public class StunnerLienzoBoundsPanel
        implements LienzoPanel {

    private final Event<CanvasMouseDownEvent> mouseDownEvent;
    private final Event<CanvasMouseUpEvent> mouseUpEvent;
    private EventListener mouseDownEventListener;
    private EventListener mouseUpEventListener;
    private Supplier<LienzoBoundsPanel> panelBuilder;
    private LienzoBoundsPanel view;

    private EventListener keyUpEventListener;
    private EventListener keyDownEventListener;
    private EventListener keyPressEventListener;

    static final String ON_KEY_DOWN = "keydown";
    static final String ON_KEY_UP = "keyup";
    static final String ON_KEY_PRESS = "keypress";
    static final String ON_MOUSE_DOWN = "mousedown";
    static final String ON_MOUSE_UP = "mouseup";

    @Inject
    public StunnerLienzoBoundsPanel(final Event<CanvasMouseDownEvent> mouseDownEvent,
                                    final Event<CanvasMouseUpEvent> mouseUpEvent) {
        this.mouseDownEvent = mouseDownEvent;
        this.mouseUpEvent = mouseUpEvent;
    }

    public StunnerLienzoBoundsPanel setPanelBuilder(final Supplier<LienzoBoundsPanel> panelBuilder) {
        this.panelBuilder = panelBuilder;
        return this;
    }

    @Override
    public Widget asWidget() {
        return ElementWrapperWidget.getWidget(view.getElement());
    }

    @Override
    public LienzoPanel show(final LienzoLayer layer) {
        setView(panelBuilder.get());
        view.add(layer.getLienzoLayer());
        initHandlers();
        return this;
    }

    private void broadcastBlurEvent() {
        final NativeEvent blur = Document.get().createBlurEvent();
        for (int i = 0; i < RootPanel.get().getWidgetCount(); i++) {
            final Widget w = RootPanel.get().getWidget(i);
            DomEvent.fireNativeEvent(blur, w);
        }
    }

    private void initHandlers() {
        mouseDownEventListener = e -> onMouseDown();
        mouseUpEventListener = e -> onMouseUp();

        getLienzoPanel().getElement().addEventListener(ON_MOUSE_DOWN, mouseDownEventListener);
        getLienzoPanel().getElement().addEventListener(ON_MOUSE_UP, mouseUpEventListener);
    }

    private com.ait.lienzo.client.widget.panel.LienzoPanel getLienzoPanel() {
        return view.getLienzoPanel();
    }

    @Override
    public LienzoPanel focus() {
        // TODO: lienzo-to-native  check if it works
        view.getElement().focus();
        return this;
    }

    @Override
    public int getWidthPx() {
        return getLienzoPanel().getWidePx();
    }

    @Override
    public int getHeightPx() {
        return getLienzoPanel().getHighPx();
    }

    @Override
    public Bounds getLocationConstraints() {
        return Bounds.createMinBounds(0d, 0d);
    }

    @Override
    public void setBackgroundLayer(final Layer layer) {
        getLienzoPanel().setBackgroundLayer(layer);
    }

    public void destroy() {
        if (null != mouseDownEventListener) {
            getLienzoPanel().getElement().removeEventListener(ON_MOUSE_DOWN, mouseDownEventListener);
            mouseDownEventListener = null;
        }
        if (null != mouseUpEventListener) {
            getLienzoPanel().getElement().removeEventListener(ON_MOUSE_UP, mouseUpEventListener);
            mouseUpEventListener = null;
        }
        if (null != keyUpEventListener) {
            getLienzoPanel().getElement().removeEventListener(ON_KEY_UP, keyUpEventListener);
            keyUpEventListener = null;
        }
        if (null != keyDownEventListener) {
            getLienzoPanel().getElement().removeEventListener(ON_KEY_DOWN, keyDownEventListener);
            keyDownEventListener = null;
        }
        if (null != keyPressEventListener) {
            getLienzoPanel().getElement().removeEventListener(ON_KEY_PRESS, keyPressEventListener);
            keyPressEventListener = null;
        }

        view.destroy();
        panelBuilder = null;
        view = null;
    }

    @Override
    public LienzoBoundsPanel getView() {
        return view;
    }

    StunnerLienzoBoundsPanel setView(final LienzoBoundsPanel view) {
        this.view = view;
        return this;
    }

    void onMouseDown() {
        broadcastBlurEvent();
        mouseDownEvent.fire(new CanvasMouseDownEvent());
    }

    void onMouseUp() {
        broadcastBlurEvent();
        mouseUpEvent.fire(new CanvasMouseUpEvent());
    }

    void addKeyDownHandler(final EventListener keyDownEventListener) {
        if (null != keyDownEventListener) {
            this.keyDownEventListener = keyDownEventListener;
            getLienzoPanel().getElement().addEventListener(ON_KEY_DOWN, this.keyDownEventListener);
        }
    }

    void addKeyPressHandler(final EventListener keyPressEventListener) {
        if (null != keyPressEventListener) {
            this.keyPressEventListener = keyPressEventListener;
            getLienzoPanel().getElement().addEventListener(ON_KEY_PRESS, this.keyPressEventListener);
        }
    }

    void addKeyUpHandler(final EventListener keyUpEventListener) {
        if (null != keyUpEventListener) {
            this.keyUpEventListener = keyUpEventListener;
            getLienzoPanel().getElement().addEventListener(ON_KEY_UP, this.keyUpEventListener);
        }
    }
}
