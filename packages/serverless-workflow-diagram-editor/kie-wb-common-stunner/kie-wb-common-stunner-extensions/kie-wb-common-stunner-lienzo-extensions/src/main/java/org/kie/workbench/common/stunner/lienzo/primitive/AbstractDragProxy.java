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


package org.kie.workbench.common.stunner.lienzo.primitive;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.tools.client.event.EventType;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import org.gwtproject.timer.client.Timer;

public abstract class AbstractDragProxy<T> {

    public interface Callback {

        void onStart(final int x,
                     final int y);

        void onMove(final int x,
                    final int y);

        void onComplete(final int x,
                        final int y);
    }

    private boolean attached = false;
    private Timer timer;
    private Runnable timeoutRunnable;
    private Integer xDiff = null;
    private Integer yDiff = null;
    private Layer layer = null;
    private T shapeProxy = null;

    private final EventListener[] eventListeners = new EventListener[3];

    protected abstract void addToLayer(final Layer layer,
                                       final T shape);

    protected abstract void removeFromLayer(final Layer layer,
                                            final T shape);

    protected abstract void setLocation(T shape,
                                        int x,
                                        int y);

    public AbstractDragProxy(final Layer layer,
                             final T shape,
                             final int x,
                             final int y,
                             final int timeout,
                             final Callback callback) {
        this.timer = makeTimer();
        this.timer.schedule(timeout);
        this.xDiff = null;
        this.yDiff = null;
        this.layer = layer;
        this.shapeProxy = shape;
        create(x,
               y,
               timeout,
               callback);
    }

    void create(final int initialX,
                final int initialY,
                final int timeout,
                final Callback callback) {
        if (!attached) {
            addToLayer(layer,
                       shapeProxy);
            setLocation(shapeProxy,
                        relativeX(initialX),
                        relativeY(initialY));
            attached = true;
            callback.onStart(initialX,
                             initialY);
        }

        final HTMLElement rootPanel = DomGlobal.document.body;

        eventListeners[0] = mouseMoveEvent -> onMouseMove(mouseMoveEvent, initialX, initialY, timeout, callback);
        eventListeners[1] = mouseDownEvent -> {
            if (mouseDownEvent.type.equals(EventType.MOUSE_DOWN.getType())) {
                mouseDownEvent.stopPropagation();
                mouseDownEvent.preventDefault();
            }
        };
        eventListeners[2] = event -> onMouseUp(event, callback);

        rootPanel.addEventListener(EventType.MOUSE_MOVE.getType(), eventListeners[0]);
        rootPanel.addEventListener(EventType.MOUSE_DOWN.getType(), eventListeners[1]);
        rootPanel.addEventListener(EventType.MOUSE_UP.getType(), eventListeners[2]);
    }

    void onMouseUp(final Event event,
                   final Callback callback) {

        if (event.type.equals(EventType.MOUSE_UP.getType())) {
            MouseEvent mouseEvent = (MouseEvent) event;

            if (isAttached()) {
                timer.cancel();

                final int x = (int) mouseEvent.x;
                final int y = (int) mouseEvent.y;

                final int relativeX = relativeX(getXDiff() + x);
                final int relativeY = relativeY(getYDiff() + y);

                clear();

                callback.onComplete(relativeX, relativeY);
            }
        }
    }

    void onMouseMove(final Event event,
                     final int initialX,
                     final int initialY,
                     final int timeout,
                     final Callback callback) {
        if (event.type.equals(EventType.MOUSE_MOVE.getType())) {
            MouseEvent mouseEvent = (MouseEvent) event;

            if (isAttached()) {
                final int x = (int) mouseEvent.x;
                final int y = (int) mouseEvent.y;

                if (xDiff() == null) {
                    xDiff = initialX - x;
                }
                if (yDiff() == null) {
                    yDiff = initialY - y;
                }

                final int relativeX = relativeX(getXDiff() + x);
                final int relativeY = relativeY(getYDiff() + y);

                setLocation(shapeProxy, relativeX, relativeY);
                scheduleMove(callback, relativeX, relativeY, timeout);
            }
        }
    }

    boolean isAttached() {
        return attached;
    }

    Integer xDiff() {
        return xDiff;
    }

    Integer yDiff() {
        return yDiff;
    }

    Transform getViewportTransform() {
        return layer.getViewport().getTransform();
    }

    int relativeX(final int x) {
        final Double relativeX = ((x) - getViewportTransform().getTranslateX()) / getViewportTransform().getScaleX();
        return relativeX.intValue();
    }

    int relativeY(final int y) {
        final Double relativeY = ((y) - getViewportTransform().getTranslateY()) / getViewportTransform().getScaleY();
        return relativeY.intValue();
    }

    void scheduleMove(final Callback callback,
                      final int x,
                      final int y,
                      final int millis) {
        timeoutRunnable = () -> callback.onMove(x,
                                                y);
        if (!timer.isRunning()) {
            timer.schedule(millis);
        }
    }

    private void removeHandlers() {
        final HTMLElement rootPanel = DomGlobal.document.body;

        rootPanel.removeEventListener(EventType.MOUSE_MOVE.getType(), eventListeners[0]);
        rootPanel.removeEventListener(EventType.MOUSE_DOWN.getType(), eventListeners[1]);
        rootPanel.removeEventListener(EventType.MOUSE_UP.getType(), eventListeners[2]);
    }

    public void clear() {
        removeHandlers();
        removeFromLayer(layer,
                        shapeProxy);
        if (null != this.timer && this.timer.isRunning()) {
            this.timer.cancel();
        }
        this.attached = false;
        this.xDiff = null;
        this.yDiff = null;
    }

    public void destroy() {
        clear();
        this.timer = null;
        this.layer = null;
        this.shapeProxy = null;
    }

    int getXDiff() {
        return null != xDiff ? xDiff : 0;
    }

    int getYDiff() {
        return null != yDiff ? yDiff : 0;
    }

    Timer makeTimer() {
        return new Timer() {
            @Override
            public void run() {
                if (null != timeoutRunnable) {
                    timeoutRunnable.run();
                }
            }
        };
    }
}
