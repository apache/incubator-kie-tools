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


package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.ait.lienzo.client.core.event.AbstractNodeHumanInputEvent;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.tools.client.collection.NFastArrayList;

public class WiresShapeControlUtils {

    public static void moveShapeUpToParent(final WiresShape shape,
                                           final WiresContainer parent) {
        if (null != parent && null != parent.getContainer()) {
            parent.getContainer().moveToTop(shape.getGroup());
        }
        moveConnectorsToTop(shape, new ArrayList<WiresShape>());
    }

    private static void moveConnectorsToTop(final WiresShape shape,
                                            final Collection<WiresShape> processed) {
        processed.add(shape);
        final NFastArrayList<WiresConnector> connectors = getConnectors(shape);
        for (WiresConnector connector : connectors.asList()) {
            connector.getGroup().moveToTop();
        }
        final NFastArrayList<WiresShape> childShapes = shape.getChildShapes();
        if (null != childShapes) {
            for (WiresShape childShape : childShapes.asList()) {
                if (!processed.contains(childShape)) {
                    moveConnectorsToTop(childShape, processed);
                }
            }
        }
    }

    public static NFastArrayList<WiresConnector> getConnectors(final WiresShape shape) {
        final NFastArrayList<WiresConnector> connectors = new NFastArrayList<>();
        if (null != shape && null != shape.getMagnets()) {
            final MagnetManager.Magnets magnets = shape.getMagnets();
            for (int i = 0; i < magnets.size(); i++) {
                final WiresMagnet magnet = magnets.getMagnet(i);
                if (null != magnet && null != magnet.getConnections()) {
                    final NFastArrayList<WiresConnection> connections = magnet.getConnections();
                    for (WiresConnection connection : connections.asList()) {
                        final WiresConnector connector = connection.getConnector();
                        if (null != connector) {
                            connectors.add(connector);
                        }
                    }
                }
            }
        }
        return connectors;
    }

    public static void excludeFromIndex(final WiresLayerIndex index,
                                        final WiresShape shape) {
        index.exclude(shape);
        final NFastArrayList<WiresShape> children = shape.getChildShapes();
        for (int i = 0; i < children.size(); i++) {
            excludeFromIndex(index,
                             children.get(i));
        }
    }

    public static Point2D getViewportRelativeLocation(final Viewport viewport,
                                                      final AbstractNodeHumanInputEvent mouseEvent) {
        return getViewportRelativeLocation(viewport,
                                           mouseEvent.getX(),
                                           mouseEvent.getY());
    }

    public static Point2D getViewportRelativeLocation(final Viewport viewport,
                                                      final double x,
                                                      final double y) {
        final Double relativeX = ((x) - viewport.getTransform().getTranslateX()) / viewport.getTransform().getScaleX();
        final Double relativeY = ((y) - viewport.getTransform().getTranslateY()) / viewport.getTransform().getScaleY();
        return new Point2D(relativeX, relativeY);
    }

    public static WiresConnector[] collectionSpecialConnectors(WiresShape shape) {
        if (shape.getMagnets() == null) {
            return null;
        }
        Map<String, WiresConnector> connectors = new HashMap<>();
        collectionSpecialConnectors(shape,
                                    connectors);
        return connectors.values().toArray(new WiresConnector[connectors.size()]);
    }

    public static void collectionSpecialConnectors(WiresShape shape,
                                                   Map<String, WiresConnector> connectors) {
        if (shape.getMagnets() != null) {
            // start with 0, as we can have center connections too
            for (int i = 0, size0 = shape.getMagnets().size(); i < size0; i++) {
                WiresMagnet m = shape.getMagnets().getMagnet(i);
                for (int j = 0, size1 = m.getConnectionsSize(); j < size1; j++) {
                    WiresConnection connection = m.getConnections().get(j);
                    if (connection.isSpecialConnection()) {
                        connectors.put(connection.getConnector().getGroup().uuid(),
                                       connection.getConnector());
                    }
                }
            }
        }

        for (WiresShape child : shape.getChildShapes().asList()) {
            collectionSpecialConnectors(child,
                                        connectors);
        }
    }

    public static boolean isConnected(final WiresConnection connection) {
        return null != connection && null != connection.getMagnet();
    }

    public static boolean isConnected(final WiresConnector connector) {
        return isConnected(connector.getHeadConnection()) && isConnected(connector.getTailConnection());
    }

    /**
     * Looks for all child {@link WiresConnector} inside for a given shape and children,
     * and returns the ones that can be updated as within the shape is also updated, by checking
     * if both source/target shapes are in same parent.
     */
    public static Map<String, WiresConnector> lookupChildrenConnectorsToUpdate(WiresShape shape) {
        final Map<String, WiresConnector> connectors = new HashMap<>();
        if (shape.getMagnets() != null) {
            // start with 0, as we can have center connections too
            for (int i = 0, size0 = shape.getMagnets().size(); i < size0; i++) {
                WiresMagnet m = shape.getMagnets().getMagnet(i);
                for (int j = 0, size1 = m.getConnectionsSize(); j < size1; j++) {
                    final WiresConnection connection = m.getConnections().get(j);
                    final WiresConnector connector = connection.getConnector();
                    if (isConnected(connector)) {
                        final WiresShape oppositeShape =
                                connection.getOppositeConnection().getMagnet().getMagnets().getWiresShape();
                        if (isSameParent(shape, oppositeShape)) {
                            connectors.put(connector.uuid(), connector);
                        }
                    }
                }
            }
        }

        if (shape.getChildShapes() != null) {
            for (WiresShape child : shape.getChildShapes().asList()) {
                //recursive call to children
                connectors.putAll(lookupChildrenConnectorsToUpdate(child));
            }
        }

        return connectors;
    }

    private static boolean isSameParent(final WiresShape s1,
                                        final WiresShape s2) {
        final WiresContainer parent1 = s1.getParent();
        final WiresContainer parent2 = s2.getParent();
        return Objects.equals(parent1, parent2);
    }

    public static void updateNestedShapes(WiresShape shape) {
        shape.shapeMoved();
    }

    public static void updateSpecialConnections(WiresConnector[] connectors,
                                                boolean isAcceptOp) {
        if (connectors == null) {
            return;
        }
        for (WiresConnector connector : connectors) {
            connector.updateForSpecialConnections(isAcceptOp);
        }
    }
}
