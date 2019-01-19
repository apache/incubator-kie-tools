/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.canvas;

import java.util.OptionalInt;
import java.util.function.BiFunction;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseDownEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseUpEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyPressEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyUpEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.HandlerRegistrationImpl;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

@Dependent
@Typed(StunnerLienzoBoundsPanel.class)
public class StunnerLienzoBoundsPanel
        implements LienzoPanel {

    private final HandlerRegistrationImpl handlerRegistrationManager;
    private final Event<KeyPressEvent> keyPressEvent;
    private final Event<KeyDownEvent> keyDownEvent;
    private final Event<KeyUpEvent> keyUpEvent;
    private final Event<CanvasMouseDownEvent> mouseDownEvent;
    private final Event<CanvasMouseUpEvent> mouseUpEvent;
    private BiFunction<OptionalInt, OptionalInt, LienzoBoundsPanel> panelBuilder;
    private LienzoBoundsPanel view;

    @Inject
    public StunnerLienzoBoundsPanel(final Event<KeyPressEvent> keyPressEvent,
                                    final Event<KeyDownEvent> keyDownEvent,
                                    final Event<KeyUpEvent> keyUpEvent,
                                    final Event<CanvasMouseDownEvent> mouseDownEvent,
                                    final Event<CanvasMouseUpEvent> mouseUpEvent) {
        this(keyPressEvent,
             keyDownEvent,
             keyUpEvent,
             mouseDownEvent,
             mouseUpEvent,
             new HandlerRegistrationImpl());
    }

    StunnerLienzoBoundsPanel(final Event<KeyPressEvent> keyPressEvent,
                             final Event<KeyDownEvent> keyDownEvent,
                             final Event<KeyUpEvent> keyUpEvent,
                             final Event<CanvasMouseDownEvent> mouseDownEvent,
                             final Event<CanvasMouseUpEvent> mouseUpEvent,
                             final HandlerRegistrationImpl registration) {
        this.keyPressEvent = keyPressEvent;
        this.keyDownEvent = keyDownEvent;
        this.keyUpEvent = keyUpEvent;
        this.mouseDownEvent = mouseDownEvent;
        this.mouseUpEvent = mouseUpEvent;
        this.handlerRegistrationManager = registration;
    }

    public StunnerLienzoBoundsPanel setPanelBuilder(final BiFunction<OptionalInt, OptionalInt, LienzoBoundsPanel> panelBuilder) {
        this.panelBuilder = panelBuilder;
        return this;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public LienzoPanel show(final LienzoLayer layer) {
        return doShow(layer,
                      OptionalInt.empty(),
                      OptionalInt.empty());
    }

    @Override
    public LienzoPanel show(final LienzoLayer layer,
                            final int width,
                            final int height) {
        return doShow(layer,
                      OptionalInt.of(width),
                      OptionalInt.of(height));
    }

    private LienzoPanel doShow(final LienzoLayer layer,
                               final OptionalInt width,
                               final OptionalInt height) {
        setView(panelBuilder.apply(width,
                                   height));
        view.add(layer.getLienzoLayer());
        initHandlers();
        if (view instanceof StunnerLienzoBoundsPanelView) {
            ((StunnerLienzoBoundsPanelView) view).setPresenter(this);
        }
        return this;
    }

    private void broadcastBlurEvent() {
        final NativeEvent blur = Document.get().createBlurEvent();
        for (int i = 0; i < RootPanel.get().getWidgetCount(); i++) {
            final Widget w = RootPanel.get().getWidget(i);
            DomEvent.fireNativeEvent(blur,
                                     w);
        }
    }

    public HandlerRegistration register(final HandlerRegistration handler) {
        return handlerRegistrationManager.register(handler);
    }

    private void initHandlers() {
        register(
                getLienzoPanel().addMouseDownHandler(mouseDownEvent -> onMouseDown())
        );
        register(
                getLienzoPanel().addMouseUpHandler(mouseUpEvent -> onMouseUp())
        );
    }

    private com.ait.lienzo.client.widget.panel.LienzoPanel getLienzoPanel() {
        return view.getLienzoPanel();
    }

    @Override
    public LienzoPanel focus() {
        view.setFocus(true);
        return this;
    }

    @Override
    public int getWidthPx() {
        return getLienzoPanel().getWidthPx();
    }

    @Override
    public int getHeightPx() {
        return getLienzoPanel().getHeightPx();
    }

    @Override
    public Bounds getLocationConstraints() {
        return Bounds.createMinBounds(0d, 0d);
    }

    @Override
    public LienzoPanel setPixelSize(final int wide,
                                    final int high) {
        getLienzoPanel().setPixelSize(wide,
                                      high);
        return this;
    }

    @Override
    public void setBackgroundLayer(final Layer layer) {
        getLienzoPanel().setBackgroundLayer(layer);
    }

    public void destroy() {
        handlerRegistrationManager.destroy();
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

    void onKeyPress(final int unicodeChar) {
        final KeyboardEvent.Key key = getKey(unicodeChar);
        if (null != key) {
            keyPressEvent.fire(new KeyPressEvent(key));
        }
    }

    void onKeyDown(final int unicodeChar) {
        final KeyboardEvent.Key key = getKey(unicodeChar);
        if (null != key) {
            keyDownEvent.fire(new KeyDownEvent(key));
        }
    }

    void onKeyUp(final int unicodeChar) {
        final KeyboardEvent.Key key = getKey(unicodeChar);
        if (null != key) {
            keyUpEvent.fire(new KeyUpEvent(key));
        }
    }

    private KeyboardEvent.Key getKey(final int unicodeChar) {
        final KeyboardEvent.Key[] keys = KeyboardEvent.Key.values();
        for (final KeyboardEvent.Key key : keys) {
            final int c = key.getUnicharCode();
            if (c == unicodeChar) {
                return key;
            }
        }
        return null;
    }
}
