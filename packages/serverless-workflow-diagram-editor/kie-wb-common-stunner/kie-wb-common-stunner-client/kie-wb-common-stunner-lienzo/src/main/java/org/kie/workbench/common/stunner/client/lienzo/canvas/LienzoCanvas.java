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


package org.kie.workbench.common.stunner.client.lienzo.canvas;

import java.util.Optional;

import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.shape.Layer;
import jakarta.enterprise.event.Event;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoLayerUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasSettings;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasDrawnEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.uberfire.mvp.Command;

public abstract class LienzoCanvas<V extends LienzoCanvasView>
        extends AbstractCanvas<V> {

    private static final ViewEventType[] SUPPORTED_EVENT_TYPES = new ViewEventType[]{
            ViewEventType.MOUSE_CLICK, ViewEventType.MOUSE_DBL_CLICK, ViewEventType.MOUSE_MOVE
    };

    private ViewEventHandlerManager eventHandlerManager;

    protected LienzoCanvas(final Event<CanvasClearEvent> canvasClearEvent,
                           final Event<CanvasShapeAddedEvent> canvasShapeAddedEvent,
                           final Event<CanvasShapeRemovedEvent> canvasShapeRemovedEvent,
                           final Event<CanvasDrawnEvent> canvasDrawnEvent,
                           final Event<CanvasFocusedEvent> canvasFocusedEvent) {
        super(canvasClearEvent, canvasShapeAddedEvent, canvasShapeRemovedEvent, canvasDrawnEvent, canvasFocusedEvent);
    }

    public AbstractCanvas<V> initialize(final CanvasPanel panel,
                                        final CanvasSettings settings) {
        eventHandlerManager = new ViewEventHandlerManager(getView().getLayer().getLienzoLayer(),
                                                          SUPPORTED_EVENT_TYPES);
        return initialize(panel,
                          settings,
                          eventHandlerManager);
    }

    AbstractCanvas<V> initialize(final CanvasPanel panel,
                                 final CanvasSettings settings,
                                 final ViewEventHandlerManager viewEventHandlerManager) {
        LienzoCore.get().setHidpiEnabled(settings.isHiDPIEnabled());
        this.eventHandlerManager = viewEventHandlerManager;
        super.initialize(panel, settings);
        Layer toplayer = new Layer().setListening(true);
        getView().getLayer().getLienzoLayer().getScene().add(toplayer);
        return this;
    }

    public void setBackgroundColor(final String color) {
        getView().getLienzoPanel().getView().getElement().style.backgroundColor = color;
    }

    @Override
    public Optional<Shape> getShapeAt(final double x,
                                      final double y) {
        final LienzoLayer lienzoLayer = getView().getLayer();
        final String uuid = LienzoLayerUtils.getUUID_At(lienzoLayer,
                                                        x,
                                                        y);
        return Optional.ofNullable(getShape(uuid));
    }

    @Override
    public void onAfterDraw(final Command callback) {
        getView().getLayer().onAfterDraw(callback);
    }

    @Override
    public void focus() {
        getView().getLienzoPanel().focus();
    }

    @Override
    public boolean supports(final ViewEventType type) {
        return eventHandlerManager.supports(type);
    }

    @Override
    public AbstractCanvas<V> addHandler(final ViewEventType type,
                                        final ViewHandler<? extends ViewEvent> eventHandler) {
        eventHandlerManager.addHandler(type,
                                       eventHandler);
        return this;
    }

    @Override
    public AbstractCanvas<V> removeHandler(final ViewHandler<? extends ViewEvent> eventHandler) {
        eventHandlerManager.removeHandler(eventHandler);
        return this;
    }

    @Override
    public AbstractCanvas<V> enableHandlers() {
        eventHandlerManager.enable();
        return this;
    }

    @Override
    public AbstractCanvas<V> disableHandlers() {
        eventHandlerManager.disable();
        return this;
    }

    @Override
    public Shape<?> getAttachableShape() {
        return null;
    }

    @Override
    public void destroy() {
        if (null != eventHandlerManager) {
            eventHandlerManager.destroy();
            eventHandlerManager = null;
        }
        super.destroy();
    }

    ViewEventHandlerManager getEventHandlerManager() {
        return eventHandlerManager;
    }

    @Override
    public boolean isEventHandlesEnabled() {
        return getEventHandlerManager().isEnabled();
    }
}
