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

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeHighlightControl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.tools.client.collection.NFastArrayList;

public class WiresShapeProxy
        extends AbstractWiresProxy
        implements WiresProxy {

    private final Supplier<WiresShape> shapeBuilder;
    private final Consumer<WiresShape> shapeAcceptor;
    private final Consumer<WiresShape> shapeDestroyer;

    private WiresShape shape;
    private WiresShapeHighlightControl control;
    private Point2D startLocation;

    public WiresShapeProxy(final WiresManager wiresManager,
                           final Supplier<WiresShape> shapeBuilder,
                           final Consumer<WiresShape> shapeAcceptor,
                           final Consumer<WiresShape> shapeDestroyer) {
        super(wiresManager);
        this.shapeBuilder = shapeBuilder;
        this.shapeAcceptor = shapeAcceptor;
        this.shapeDestroyer = shapeDestroyer;
    }

    @Override
    public void start(final double x,
                      final double y) {
        final Point2D location = new Point2D(x, y);
        shape = shapeBuilder.get();
        startLocation = location.copy();
        if (null != shape.getParent()) {
            final Point2D parentLocation = shape.getParent().getComputedLocation();
            startLocation = startLocation.add(parentLocation);
            shape.removeFromParent();
            getWiresLayer().add(shape);
        }
        hideAllConnectorControlPoints(shape);
        control = WiresShapeHighlightControl.create(getWiresManager(),
                                                    new Supplier<WiresShapeControl>() {
                                                        @Override
                                                        public WiresShapeControl get() {
                                                            return shape.getControl();
                                                        }
                                                    });
        shape.setLocation(startLocation);
        refreshAlignAndDistro();
        control.onMoveStart(startLocation.getX(), startLocation.getY());
        batch();
    }

    @Override
    public void move(final double dx,
                     final double dy) {
        final boolean adjusted = control.onMove(dx, dy);
        final Point2D adjust = control.getAdjust();
        final Point2D location = adjusted ?
                startLocation.copy().offset(adjust.getX(), adjust.getY()) :
                startLocation.copy().offset(dx, dy);
        shape.setLocation(location);
        batch();
    }

    @Override
    public void end() {
        control.onMoveComplete();
        if (control.isAccepted()) {
            shapeAcceptor.accept(shape);
            resetState();
        } else {
            destroy();
        }
        batch();
    }

    @Override
    public void destroy() {
        if (null != shape) {
            shapeDestroyer.accept(shape);
            resetState();
        }
    }

    public WiresShape getShape() {
        return shape;
    }

    private void resetState() {
        shape = null;
        control = null;
        startLocation = null;
    }

    private void refreshAlignAndDistro() {
        if (null != control.getAlignAndDistributeControl()) {
            control.getAlignAndDistributeControl().refresh(false, true);
        }
    }

    private static void hideAllConnectorControlPoints(final WiresShape shape) {
        final MagnetManager.Magnets magnets = shape.getMagnets();
        if (null != magnets) {
            for (int i = 0; i < magnets.size(); i++) {
                final WiresMagnet magnet = magnets.getMagnet(i);
                if (null != magnet) {
                    final NFastArrayList<WiresConnection> connections = magnet.getConnections();
                    if (null != connections) {
                        for (int j = 0; j < connections.size(); j++) {
                            final WiresConnection connection = connections.get(j);
                            if (null != connection) {
                                final WiresConnector connector = connection.getConnector();
                                if (null != connector) {
                                    if (null != connector.getControl()) {
                                        connector.getControl().hideControlPoints();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
