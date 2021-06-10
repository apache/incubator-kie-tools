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

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.BackingColorMapUtils;
import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectionControl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.tools.client.collection.NFastStringMap;

import elemental2.dom.ImageData;

public class WiresConnectionControlImpl implements WiresConnectionControl {

    private       WiresConnector        m_connector;

    private       WiresManager          m_manager;

    private final boolean               m_head;

    private       ImageData             m_shapesBacking;

    private       ImageData             m_magnetsBacking;

    private       MagnetManager.Magnets m_magnets;

    private       double                m_startX;

    private       double                m_startY;

    private       Point2D               m_adjust;

    private       String                m_colorKey;

    private boolean m_allowed;

    private WiresMagnet m_initial_magnet;

    private       WiresMagnet           m_current_magnet;

    private       boolean               m_initialAutoConnect;

    private final NFastStringMap<WiresShape> m_shape_color_map = new NFastStringMap<WiresShape>();

    private final NFastStringMap<WiresMagnet> m_magnet_color_map = new NFastStringMap<WiresMagnet>();

    public WiresConnectionControlImpl(final WiresConnector connector,
                                      final boolean isHeadNotTail,
                                      final WiresManager wiresManager) {
        m_connector = connector;
        m_manager = wiresManager;
        m_head = isHeadNotTail;
        m_adjust = new Point2D(0,
                               0);
        m_initial_magnet = null;
        m_current_magnet = null;
    }

    @Override
    public void onMoveStart(double x,
                            double y) {
        Point2D points = getControlNode().getComputedLocation();
        m_startX = points.getX();
        m_startY = points.getY();
        m_allowed = false;

        ScratchPad scratch = m_manager.getLayer().getLayer().getScratchPad();
        m_shapesBacking = BackingColorMapUtils.drawShapesToBacking(m_manager.getLayer().getChildShapes(),
                                                                   scratch,
                                                                   null,
                                                                   m_shape_color_map);
        m_connector.getLine().getOverLayer().getContext().createImageData(m_shapesBacking);

        WiresConnection connection = getConnection();
        m_initialAutoConnect = connection.isAutoConnection();
        connection.setAutoConnection(false); // set it to false while dragging
        m_initial_magnet = connection.getMagnet();
        if (null != m_initial_magnet) {
            m_magnets = connection.getMagnet().getMagnets();
            m_magnetsBacking = m_manager.getMagnetManager().drawMagnetsToBack(m_magnets,
                                                                              m_shape_color_map,
                                                                              m_magnet_color_map,
                                                                              scratch);
        }

        // always null when drag start and reset the offsets (they may already be 0)
        connection.setMagnet(null);
        connection.setXOffset(0);
        connection.setYOffset(0);
        String colorKey = BackingColorMapUtils.findColorAtPoint(m_shapesBacking,
                                                                (int) m_startX,
                                                                (int) m_startY);
        checkAllowAndShowMagnets(colorKey);
    }

    @Override
    public void onMoveComplete() {
        makeAndUpdateSpecialConnections();

        if (!isAllowed()) {
            WiresConnection connection = getConnection();
            connection.setAutoConnection(m_initialAutoConnect);
            connection.setMagnet(m_initial_magnet);
            WiresConnector connector = connection.getConnector();
            connector.updateForSpecialConnections(false);
        }

        if (m_magnets != null) {
            m_magnets.hide();
        }

        m_shapesBacking = null;// uses lots of memory, so let it GC
        m_magnetsBacking = null;// uses lots of memory, so let it GC
        m_magnets = null;// if this is not nulled, the Mangets reference could stop Magnets being GC, when not used anywhere else
        m_colorKey = null;
        m_current_magnet = null;
        m_initial_magnet = null;
        m_shape_color_map.clear();
        m_magnet_color_map.clear();
    }

    public boolean isAllowed() {
        return m_allowed;
    }

    private void makeAndUpdateSpecialConnections() {
        WiresConnection connection = getConnection();
        WiresShape shape = null;

        // shape remains null, if the connection is either not connecting to a magnet or not over the body of a shape.
        if (m_current_magnet != null) {
            shape = m_current_magnet.getMagnets().getWiresShape();
            connection.setAutoConnection(false);
        } else {
            if (m_colorKey != null) {
                shape = m_shape_color_map.get(m_colorKey);
                if (shape != null && shape.getMagnets() != null) {
                    // no magnet is selected, but if we are over a shape, then auto connect
                    connection.setAutoConnection(true);
                } else {
                    connection.setAutoConnection(false);
                }
            } else {
                connection.setAutoConnection(false);
            }
        }
        m_allowed = allowedMagnetAndUpdateAutoConnections(m_manager,
                                                               connection,
                                                               m_head,
                                                               shape,
                                                               m_current_magnet,
                                                               true);
    }

    public static boolean allowedMagnetAndUpdateAutoConnections(WiresManager wiresManager,
                                                                WiresConnection connection,
                                                                boolean isHead,
                                                                WiresShape shape,
                                                                WiresMagnet currentMagnet,
                                                                boolean applyAccept) {
        final IConnectionAcceptor connectionAcceptor = wiresManager.getConnectionAcceptor();
        WiresConnector connector = connection.getConnector();
        // shape can be null, to see if a connection can be unconnected to a magnet
        boolean accept;
        WiresShape headS;
        WiresShape tailS;
        if (isHead) {
            accept = connectionAcceptor.headConnectionAllowed(connection,
                                                              shape);
            headS = shape;
            tailS = (connector.getTailConnection().getMagnet() != null) ? connector.getTailConnection().getMagnet().getMagnets().getWiresShape() : null;
        } else {
            accept = connectionAcceptor.tailConnectionAllowed(connection,
                                                              shape);
            headS = (connector.getHeadConnection().getMagnet() != null) ? connector.getHeadConnection().getMagnet().getMagnets().getWiresShape() : null;
            tailS = shape;
        }

        if (applyAccept && accept) {
            accept = acceptMagnetAndUpdateAutoConnection(connectionAcceptor,
                                                                   connection,
                                                                   isHead,
                                                                   headS,
                                                                   tailS,
                                                                   currentMagnet);
        }
        return accept;
    }

    public static boolean acceptMagnetAndUpdateAutoConnection(IConnectionAcceptor connectionAcceptor,
                                                              WiresConnection connection,
                                                              boolean isHead,
                                                              WiresShape headS,
                                                              WiresShape tailS,
                                                              WiresMagnet currentMagnet) {
        WiresConnector connector = connection.getConnector();

        boolean accept = true;

        // Only set the current magnet, if auto connection is false
        final boolean isAuto = connection.isAutoConnection();
        if (!isAuto) {
            // m_current_magnet could also be null, and it's seeing if that's accepted
            // technically all connections have been checked and allowed, but for consistency and notifications will be rechecked via acceptor
            if (isHead) {
                accept = accept && connectionAcceptor.acceptHead(connection,
                                                                 currentMagnet);
            } else {
                accept = accept && connectionAcceptor.acceptTail(connection,
                                                                 currentMagnet);
            }

            if (accept) {
                // Set the magnet on the current connection
                // magnet could also be null
                connection.setMagnet(currentMagnet);
            }
        }

        if (accept) {
            // can be used during drag, as we know the current connection will have a null shape
            // this will cause the other side to be updated
            accept = accept && connector.updateForAutoConnections(headS,
                                                                  tailS,
                                                                  isAuto);
            connector.updateForCenterConnection();
        }

        return accept;
    }

    @Override
    public Point2D getAdjust() {
        return m_adjust;
    }

    @Override
    public boolean onMove(double dx,
                          double dy) {

        final Point2D dxy = new Point2D(dx,
                                        dy);
        m_adjust = new Point2D(0,
                               0);

        // this is redetermined on each drag adjust
        m_current_magnet = null;

        int x = (int) (m_startX + dxy.getX());
        int y = (int) (m_startY + dxy.getY());

        String colorKey = BackingColorMapUtils.findColorAtPoint(m_shapesBacking,
                                                                x,
                                                                y);
        if (m_colorKey != null && colorKey != null && !colorKey.equals(m_colorKey)) {
            // this can happen when the mouse moves from an outer shape to an inner shape, or vice-versa
            // hide and null, and it'll show for the new.
            if (null != m_magnets) {
                m_magnets.hide();
            }
            m_magnets = null;
            m_colorKey = null;
        }

        boolean isAllowed = true;
        if (m_magnets == null) {
            isAllowed = checkAllowAndShowMagnets(colorKey);
        }

        // can be used during drag, as we know the current connection will have a null shape
        // this will cause the other side to be updated
        m_connector.updateForSpecialConnections(false);

        if (isAllowed) {
            if (null != m_magnets) {
                String magnetColorKey = BackingColorMapUtils.findColorAtPoint(m_magnetsBacking,
                                                                              x,
                                                                              y);
                if (magnetColorKey == null) {
                    if (null != m_magnets) {
                        m_magnets.hide();
                    }
                    m_magnets = null;
                    m_colorKey = null;
                } else {
                    // Take into account that it can be null, when over the main shape, instead of a magnet
                    WiresMagnet potentialMagnet = m_magnet_color_map.get(magnetColorKey);
                    if (m_connector.getHeadConnection().getMagnet() != potentialMagnet && m_connector.getTailConnection().getMagnet() != potentialMagnet) {
                        // makeXY sure we don't addBoundingBox a connection's head and tail to the same magnet
                        m_current_magnet = potentialMagnet;
                    } else if (potentialMagnet == null) {
                        m_current_magnet = null;
                    }
                }
            }

            if (null != m_current_magnet) {
                Shape<?> control = m_current_magnet.getControl().asShape();
                if (control != null) {
                    // If there is a control, snap to it
                    Point2D absControl = control.getComputedLocation();
                    double targetX = absControl.getX();
                    double targetY = absControl.getY();

                    double tx = targetX - m_startX - dxy.getX();
                    double ty = targetY - m_startY - dxy.getY();
                    if (tx != 0 || ty != 0) {
                        m_adjust = new Point2D(dxy.getX() + tx,
                                               dxy.getY() + ty);
                    }
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public void destroy() {
        if (m_magnets != null) {
            m_magnets.hide();
            m_magnets = null;
        }
        m_shapesBacking = null;
        m_shapesBacking = m_magnetsBacking;
        m_colorKey = null;
        m_initial_magnet = null;
        m_current_magnet = null;
        m_allowed = false;
        m_shape_color_map.clear();
        m_magnet_color_map.clear();
    }

    private boolean checkAllowAndShowMagnets(String colorKey) {
        final WiresShape prim = null != colorKey ? m_shape_color_map.get(colorKey) : null;
        m_colorKey = colorKey;

        m_allowed = isConnectionAllowed(prim);

        if (m_allowed) {
            showMagnets(prim);
            return true;
        }

        return false;
    }

    private boolean isConnectionAllowed(WiresShape prim) {
        final IConnectionAcceptor connectionAcceptor = m_manager.getConnectionAcceptor();
        if (m_head) {
            return connectionAcceptor.headConnectionAllowed(m_connector.getHeadConnection(),
                                                            prim);
        } else {
            return connectionAcceptor.tailConnectionAllowed(m_connector.getTailConnection(),
                                                            prim);
        }
    }

    private void showMagnets(WiresShape prim) {
        m_magnets = null != prim ? prim.getMagnets() : null;
        if (m_magnets != null) {
            m_magnets.show();
            final ScratchPad scratch = m_manager.getLayer().getLayer().getScratchPad();
            m_magnetsBacking = m_manager.getMagnetManager().drawMagnetsToBack(m_magnets,
                                                                              m_shape_color_map,
                                                                              m_magnet_color_map,
                                                                              scratch);
        }
    }

    private WiresConnection getConnection() {
        if (m_head) {
            return m_connector.getHeadConnection();
        } else {
            return m_connector.getTailConnection();
        }
    }

    private Node<?> getControlNode() {
        return (Node<?>) (m_head ?
                m_connector.getHeadConnection().getControl() :
                m_connector.getTailConnection().getControl());
    }
}
