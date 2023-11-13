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


package com.ait.lienzo.client.core.shape.wires.proxy;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.tools.client.event.HandlerRegistration;

public class WiresDragProxy {

    private static final byte MOVE = 0;
    private static final byte UP = 1;
    private static final byte OUT = 2;
    private static final byte EXIT = 3;

    private final Supplier<AbstractWiresProxy> delegate;
    private final Supplier<Layer> proxyLayerBuilder;
    private final HandlerRegistration[] registrations;
    private final Point2D startPoint;
    private Layer proxyDragLayer;

    public WiresDragProxy(final Supplier<AbstractWiresProxy> delegate) {
        this(delegate,
             () -> new Layer().setListening(true));
    }

    public WiresDragProxy(final Supplier<AbstractWiresProxy> delegate,
                          final Supplier<Layer> proxyLayerBuilder) {
        this.delegate = delegate;
        this.proxyLayerBuilder = proxyLayerBuilder;
        this.registrations = new HandlerRegistration[4];
        this.startPoint = new Point2D(0d, 0d);
        this.proxyDragLayer = null;
    }

    public void enable(final double x,
                       final double y) {
        initEventHandling(x, y);
        start(x, y);
    }

    public void destroy() {
        endEventHandling();
        getDelegate().destroy();
    }

    private void start(final double x,
                       final double y) {
        startPoint.setX(x);
        startPoint.setY(y);

        final Point2D start = getStartAdjusted(x, y);
        getDelegate().start(start.getX(), start.getY());
    }

    private void move(final double x,
                      final double y) {

        final Point2D adjusted = getAdjustedForZoomPoint(startPoint.getX(), startPoint.getY());
        final double dx = x - adjusted.getX();
        final double dy = y - adjusted.getY();

        getDelegate().move(dx, dy);
        proxyDragLayer.moveToTop();
    }

    private void end() {
        getDelegate().end();
        endEventHandling();
    }

    private void initEventHandling(final double x,
                                   final double y) {

        proxyDragLayer = proxyLayerBuilder.get();
        getLayer().getScene().add(proxyDragLayer.moveToTop());
        getLayer().setListening(false);
        registrations[UP] = proxyDragLayer.addNodeMouseUpHandler(upEvent -> end());
        registrations[EXIT] = proxyDragLayer.addNodeMouseExitHandler(exitEvent -> end());
        registrations[OUT] = proxyDragLayer.addNodeMouseOutHandler(outEvent -> end());
        registrations[MOVE] = proxyDragLayer.addNodeMouseMoveHandler(moveEvent -> {
                                                                         final Point2D adjusted = getAdjustedForZoomPoint(moveEvent.getX(), moveEvent.getY());
                                                                         move(adjusted.getX(), adjusted.getY());
                                                                     }
        );
    }

    protected Point2D getAdjustedForZoomPoint(final double x, final double y) {
        final Transform transform = getLayer().getViewport().getTransform();
        final Point2D inversed = new Point2D(0, 0);
        final Point2D point = new Point2D(x, y);
        transform.getInverse().transform(point, inversed);
        return inversed;
    }

    private void endEventHandling() {
        for (int i = 0; i < registrations.length; i++) {
            HandlerRegistration registration = registrations[i];
            if (null != registration) {
                registration.removeHandler();
                registrations[i] = null;
            }
        }

        if (isProxyEnabled()) {
            proxyDragLayer.removeFromParent();
            proxyDragLayer = null;
            getLayer().setListening(true);
        }
    }

    private Point2D getStartAdjusted(final double x,
                                     final double y) {
        Point2D viewportLoc = new Point2D(x, y);
        Transform inverse = getLayer().getViewport().getTransform().getInverse();
        inverse.transform(viewportLoc, viewportLoc);
        return viewportLoc;
    }

    private boolean isProxyEnabled() {
        return null != proxyDragLayer;
    }

    private AbstractWiresProxy getDelegate() {
        return delegate.get();
    }

    private Layer getLayer() {
        return getDelegate().getLayer();
    }
}
