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


package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.style.Style;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.gwtproject.timer.client.Timer;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoPanelWidget;
import org.kie.workbench.common.stunner.core.client.shape.view.event.NativeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.NativeHandlerRegistration;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.uberfire.mvp.Command;

@Dependent
public class ShapeGlyphDragHandler {

    public interface Item {

        Glyph getShape();

        int getWidth();

        int getHeight();
    }

    public interface Callback {

        void onStart(int x,
                     int y);

        void onMove(int x,
                    int y);

        void onComplete(int x,
                        int y);
    }

    private final LienzoGlyphRenderer<Glyph> glyphLienzoGlyphRenderer;
    final NativeHandlerRegistration handlerRegistrations;
    private final Supplier<HTMLElement> rootPanelSupplier;
    private final Function<ShapeGlyphDragHandler.Item, LienzoPanelWidget> lienzoPanelBuilder;
    private final BiConsumer<Command, Integer> timer;
    private LienzoPanelWidget dragProxyPanel;
    static final String MOUSE_MOVE = "mousemove";
    static final String MOUSE_UP = "mouseup";
    static final String KEY_DOWN = "keydown";
    NativeHandler mouseMoveHandler;
    NativeHandler mouseUpHandler;
    NativeHandler keyDownHandler;

    @Inject
    public ShapeGlyphDragHandler(final LienzoGlyphRenderers glyphLienzoGlyphRenderer) {
        this(glyphLienzoGlyphRenderer,
             new NativeHandlerRegistration(),
             () -> DomGlobal.document.body,
             item -> LienzoPanelWidget.create(item.getWidth() * 2,
                                              item.getHeight() * 2),
             (task, millis) -> new Timer() {
                 @Override
                 public void run() {
                     task.execute();
                 }
             }.schedule(millis));
    }

    ShapeGlyphDragHandler(final LienzoGlyphRenderer<Glyph> glyphLienzoGlyphRenderer,
                          final NativeHandlerRegistration handlerRegistrations,
                          final Supplier<HTMLElement> rootPanelSupplier,
                          final Function<ShapeGlyphDragHandler.Item, LienzoPanelWidget> lienzoPanelBuilder,
                          final BiConsumer<Command, Integer> timer) {
        this.glyphLienzoGlyphRenderer = glyphLienzoGlyphRenderer;
        this.handlerRegistrations = handlerRegistrations;
        this.rootPanelSupplier = rootPanelSupplier;
        this.lienzoPanelBuilder = lienzoPanelBuilder;
        this.timer = timer;
    }

    public ShapeGlyphDragHandler show(final ShapeGlyphDragHandler.Item item,
                                      final int x,
                                      final int y,
                                      final ShapeGlyphDragHandler.Callback Callback) {
        // Create the lienzo "proxy" panel instance.
        final Layer dragProxyLayer = new Layer();
        this.dragProxyPanel = lienzoPanelBuilder.apply(item);
        dragProxyPanel.add(dragProxyLayer);
        attachHandlers(Callback);

        // Add the glyph instance into the layer.
        dragProxyLayer.add(glyphLienzoGlyphRenderer
                                   .render(item.getShape(),
                                           item.getWidth(),
                                           item.getHeight())
                                   .setX(0)
                                   .setY(0));

        // Handle the proxy panel instance.
        moveProxyTo(x, y);
        rootPanelSupplier.get().appendChild(dragProxyPanel.getElement());

        return this;
    }

    public void clear() {
        clearState(null);
    }

    public void destroy() {
        clearState(() -> dragProxyPanel.destroy());
    }

    private void moveProxyTo(final double x,
                             final double y) {
        HTMLDivElement element = dragProxyPanel.getElement();
        element.style.cursor = Style.Cursor.AUTO.getCssName();
        element.style.position = Style.Position.ABSOLUTE.getCssName();
        element.style.left = x + Style.Unit.PX.getType();
        element.style.top = y + Style.Unit.PX.getType();
        element.style.zIndex = CSSProperties.ZIndexUnionType.of(Integer.MAX_VALUE);
    }

    void attachHandlers(final ShapeGlyphDragHandler.Callback callback) {
        HTMLElement panelElement = rootPanelSupplier.get();

        mouseMoveHandler = new NativeHandler(MOUSE_MOVE,
                                             mouseMoveEvent -> onMouseMove(mouseMoveEvent, callback),
                                             panelElement).add();
        register(mouseMoveHandler);

        //delay to attach the MouseUpEvent handler, to avoid "clicking" to drop item.
        timer.accept(() -> {
            mouseUpHandler = new NativeHandler(MOUSE_UP,
                                               mouseUpEvent -> onMouseUp(mouseUpEvent, callback),
                                               panelElement).add();
            register(mouseUpHandler);
        }, 200);

        keyDownHandler = new NativeHandler(KEY_DOWN,
                                           keyDownEvent -> onKeyDown(keyDownEvent),
                                           panelElement).add();
        register(keyDownHandler);
    }

    void onMouseMove(final Event event,
                     final ShapeGlyphDragHandler.Callback callback) {
        if (event.type.equals(MOUSE_MOVE)) {
            MouseEvent mouseEvent = (MouseEvent) event;

            HTMLDivElement element = dragProxyPanel.getElement();
            element.style.left = (int) mouseEvent.x + "px";
            element.style.top = (int) mouseEvent.y + "px";
            callback.onMove((int) mouseEvent.clientX, (int) mouseEvent.clientY);
        }
    }

    void onMouseUp(final Event event,
                   final ShapeGlyphDragHandler.Callback callback) {
        if (event.type.equals(MOUSE_UP)) {
            MouseEvent mouseEvent = (MouseEvent) event;
            clearHandlers();
            rootPanelSupplier.get().removeChild(dragProxyPanel.getElement());
            callback.onComplete((int) mouseEvent.clientX,
                                (int) mouseEvent.clientY);
        }
    }

    void onKeyDown(final Event event) {
        if (event.type.equals(KEY_DOWN)) {
            clear();
        }
    }

    private void register(final NativeHandler registration) {
        handlerRegistrations.register(registration);
    }

    private void clearHandlers() {
        handlerRegistrations.removeHandler();
        mouseMoveHandler = null;
        mouseUpHandler = null;
        keyDownHandler = null;
    }

    private void clearState(final Command proxyDestroyCommand) {
        clearHandlers();
        if (Objects.nonNull(dragProxyPanel)) {
            rootPanelSupplier.get().removeChild(dragProxyPanel.getElement());
            if (null != proxyDestroyCommand) {
                proxyDestroyCommand.execute();
            }
            dragProxyPanel = null;
        }
    }
}
