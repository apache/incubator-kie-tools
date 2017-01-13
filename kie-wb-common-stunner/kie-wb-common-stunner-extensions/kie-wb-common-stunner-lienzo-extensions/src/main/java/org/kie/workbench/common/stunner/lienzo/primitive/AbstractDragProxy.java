/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.lienzo.primitive;

import com.ait.lienzo.client.core.shape.Layer;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

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
    private final HandlerRegistration[] handlerRegs = new HandlerRegistration[3];

    protected abstract void addToLayer(final Layer layer,
                                       final T shape);

    protected abstract void removeFromLayer(final Layer layer,
                                            final T shape);

    protected abstract void setX(final T shape,
                                 final int x);

    protected abstract void setY(final T shape,
                                 final int y);

    public AbstractDragProxy(final Layer layer,
                             final T shape,
                             final int x,
                             final int y,
                             final int timeout,
                             final Callback callback) {
        this.timer = new Timer() {
            @Override
            public void run() {
                if (null != timeoutRunnable) {
                    timeoutRunnable.run();
                    ;
                }
            }
        };
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

    private void create(final int initialX,
                        final int initialY,
                        final int timeout,
                        final Callback callback) {
        if (!attached) {
            addToLayer(layer,
                       shapeProxy);
            setX(shapeProxy,
                 initialX);
            setY(shapeProxy,
                 initialY);
            attached = true;
            callback.onStart(initialX,
                             initialY);
        }
        handlerRegs[0] = RootPanel.get().addDomHandler(new MouseMoveHandler() {

                                                           @Override
                                                           public void onMouseMove(final MouseMoveEvent mouseMoveEvent) {
                                                               if (attached) {
                                                                   if (xDiff == null) {
                                                                       xDiff = initialX - mouseMoveEvent.getX();
                                                                   }
                                                                   if (yDiff == null) {
                                                                       yDiff = initialY - mouseMoveEvent.getY();
                                                                   }
                                                                   final int x = getXDiff() + mouseMoveEvent.getX();
                                                                   final int y = getYDiff() + mouseMoveEvent.getY();
                                                                   setX(shapeProxy,
                                                                        x);
                                                                   setY(shapeProxy,
                                                                        y);
                                                                   layer.batch();
                                                                   if (!timer.isRunning()) {
                                                                       timer.schedule(timeout);
                                                                   }
                                                                   timeoutRunnable = () -> callback.onMove(x,
                                                                                                           y);
                                                                   timer.schedule(timeout);
                                                               }
                                                           }
                                                       },
                                                       MouseMoveEvent.getType());
        handlerRegs[1] = RootPanel.get().addDomHandler(new MouseDownHandler() {

                                                           @Override
                                                           public void onMouseDown(final MouseDownEvent mouseDownEvent) {
                                                               mouseDownEvent.stopPropagation();
                                                               mouseDownEvent.preventDefault();
                                                           }
                                                       },
                                                       MouseDownEvent.getType());
        handlerRegs[2] = RootPanel.get().addDomHandler(new MouseUpHandler() {

                                                           @Override
                                                           public void onMouseUp(final MouseUpEvent mouseUpEvent) {
                                                               if (attached) {
                                                                   timer.cancel();
                                                                   final int x = getXDiff() + mouseUpEvent.getX();
                                                                   final int y = getYDiff() + mouseUpEvent.getY();
                                                                   AbstractDragProxy.this.clear();
                                                                   callback.onComplete(x,
                                                                                       y);
                                                               }
                                                           }
                                                       },
                                                       MouseUpEvent.getType());
    }

    private void removeHandlers() {
        handlerRegs[0].removeHandler();
        handlerRegs[1].removeHandler();
        handlerRegs[2].removeHandler();
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

    private int getXDiff() {
        return null != xDiff ? xDiff : 0;
    }

    private int getYDiff() {
        return null != yDiff ? yDiff : 0;
    }
}
