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

package com.ait.lienzo.client.core.shape.wires.proxy;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectionControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectionControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectorControlImpl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;

public class WiresConnectorProxy
        extends AbstractWiresProxy
        implements WiresProxy {

    private final Supplier<WiresConnector> connectorBuilder;
    private final Consumer<WiresConnector> connectorAcceptor;
    private final Consumer<WiresConnector> connectorDestroyer;

    private WiresConnector connector;
    private Point2D startLocation;

    public WiresConnectorProxy(final WiresManager wiresManager,
                               final Supplier<WiresConnector> connectorBuilder,
                               final Consumer<WiresConnector> connectorAcceptor,
                               final Consumer<WiresConnector> connectorDestroyer) {
        super(wiresManager);
        this.connectorBuilder = connectorBuilder;
        this.connectorAcceptor = connectorAcceptor;
        this.connectorDestroyer = connectorDestroyer;
    }

    @Override
    public void start(final double x,
                      final double y) {
        startLocation = new Point2D(x, y);
        enable();
        displayControlPoints();
        setLocation(startLocation);
        getTailConnectionControl().onMoveStart(startLocation.getX(), startLocation.getY());
        batch();
    }

    @Override
    public void move(final double dx,
                     final double dy) {
        final boolean adjust = getTailConnectionControl().onMove(dx, dy);
        final Point2D adjustPoint = getTailConnectionControl().getAdjust();
        final Point2D location = adjust ?
                startLocation.copy().offset(adjustPoint.getX(), adjustPoint.getY()) :
                startLocation.copy().offset(dx, dy);
        setLocation(location);
        batch();
    }

    @Override
    public void end() {
        final WiresConnectionControlImpl control = (WiresConnectionControlImpl) getTailConnectionControl();
        control.onMoveComplete();
        if (control.isAllowed()) {
            hideControlPoints();
            connectorAcceptor.accept(connector);
            connector = null;
            startLocation = null;
        } else {
            destroy();
        }
        batch();
    }

    @Override
    public void destroy() {
        if (null != connector) {
            connectorDestroyer.accept(connector);
            connector = null;
        }
        startLocation = null;
    }

    private void enable() {
        connector = connectorBuilder.get();
    }

    private void setLocation(Point2D location) {
        getTailShape().setLocation(location);
        Point2DArray points = connector.getLine().getPoint2DArray();
        Point2D point = points.get(points.size() - 1);
        point.setX(location.getX());
        point.setY(location.getY());
        connector.getLine().refresh();
    }

    private void displayControlPoints() {
        connector.getPointHandles().show();
        getConnectorControl().initHeadConnection();
        getConnectorControl().initTailConnection();
    }

    private void hideControlPoints() {
        connector.getPointHandles().hide();
    }

    private WiresConnectorControlImpl getConnectorControl() {
        return (WiresConnectorControlImpl) connector.getControl();
    }

    private WiresConnectionControl getTailConnectionControl() {
        return getConnectorControl().getTailConnectionControl();
    }

    private IPrimitive<?> getTailShape() {
        return connector.getTailConnection().getControl();
    }
}
