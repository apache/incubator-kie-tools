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

package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@Dependent
public class ShapeGlyphDragHandlerImpl implements ShapeGlyphDragHandler<AbstractCanvas> {

    private static final int ZINDEX = Integer.MAX_VALUE;

    private final LienzoGlyphRenderer<Glyph> glyphLienzoGlyphRenderer;
    final List<HandlerRegistration> handlerRegistrations = new ArrayList<>();
    LienzoPanel dragProxyPanel;

    @Inject
    public ShapeGlyphDragHandlerImpl(final LienzoGlyphRenderers glyphLienzoGlyphRenderer) {
        this.glyphLienzoGlyphRenderer = glyphLienzoGlyphRenderer;
    }

    @Override
    public DragProxy<AbstractCanvas, Item, DragProxyCallback> proxyFor(AbstractCanvas context) {
        return this;
    }

    @Override
    public DragProxy<AbstractCanvas, Item, DragProxyCallback> show(Item item, int x, int y, DragProxyCallback dragProxyCallback) {

        int width = item.getWidth();
        int height = item.getHeight();

        final Group dragShape = glyphLienzoGlyphRenderer.render(item.getShape(), width, height);
        dragShape.setX(0);
        dragShape.setY(0);
        this.dragProxyPanel = new LienzoPanel((width * 2), (height * 2));
        dragProxyPanel.getElement().getStyle().setCursor(Style.Cursor.AUTO);
        final Layer dragProxyLayer = new Layer();
        dragProxyLayer.add(dragShape);
        dragProxyPanel.add(dragProxyLayer);
        dragProxyLayer.batch();
        setDragProxyPosition(dragProxyPanel, width, height, x, y);
        attachDragProxyHandlers(dragProxyPanel, dragProxyCallback);

        addKeyboardEscHandler();
        RootPanel.get().add(dragProxyPanel);
        return this;
    }

    @Override
    public void clear() {
        if (Objects.nonNull(dragProxyPanel)) {
            clearHandlers();
            dragProxyPanel.clear();
            RootPanel.get().remove(dragProxyPanel);
            dragProxyPanel = null;
        }
    }

    @Override
    public void destroy() {
        clearHandlers();
        if (Objects.nonNull(dragProxyPanel)) {
            RootPanel.get().remove(dragProxyPanel);
            dragProxyPanel.destroy();
            dragProxyPanel = null;
        }
    }

    private void setDragProxyPosition(final LienzoPanel dragProxyPanel, final double proxyWidth, final double proxyHeight, final double x, final double y) {
        Style style = dragProxyPanel.getElement().getStyle();
        style.setPosition(Style.Position.ABSOLUTE);
        style.setLeft(x, Style.Unit.PX);
        style.setTop(y, Style.Unit.PX);
        style.setZIndex(ZINDEX);
    }

    private void attachDragProxyHandlers(final LienzoPanel floatingPanel, final DragProxyCallback callback) {
        final Style style = floatingPanel.getElement().getStyle();

        //MouseMoveEvents
        addMouseMoveEvents(floatingPanel, callback, style);

        //MouseUpEvent
        //delay to attach the MouseUpEvent handler, to avoid "clicking" to drop item.
        new Timer() {
            @Override
            public void run() {
                addMouseUpEvent(floatingPanel, callback);
                this.cancel();
            }
        }.schedule(200);
    }

    private void addMouseMoveEvents(LienzoPanel floatingPanel, DragProxyCallback callback, Style style) {
        handlerRegistrations.add(RootPanel.get().addDomHandler(mouseMoveEvent -> {
            style.setLeft(mouseMoveEvent.getX(), Style.Unit.PX);
            style.setTop(mouseMoveEvent.getY(), Style.Unit.PX);
            final int x = mouseMoveEvent.getX();
            final int y = mouseMoveEvent.getY();
            callback.onMove(x, y);
        }, MouseMoveEvent.getType()));
    }

    private void addMouseUpEvent(LienzoPanel floatingPanel, DragProxyCallback callback) {
        handlerRegistrations.add(RootPanel.get().addDomHandler(mouseUpEvent -> {
            clearHandlers();
            RootPanel.get().remove(floatingPanel);
            final int x = mouseUpEvent.getX();
            final int y = mouseUpEvent.getY();
            callback.onComplete(x, y);
        }, MouseUpEvent.getType()));
    }

    private void addKeyboardEscHandler() {
        handlerRegistrations.add(RootPanel.get().addDomHandler(k -> clear(), KeyDownEvent.getType()));
    }

    private void clearHandlers() {
        handlerRegistrations.stream().forEach(HandlerRegistration::removeHandler);
        handlerRegistrations.clear();
    }
}