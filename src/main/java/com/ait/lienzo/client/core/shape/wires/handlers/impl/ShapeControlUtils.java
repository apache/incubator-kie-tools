package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;

public class ShapeControlUtils {

    public static WiresConnector[] collectionSpecialConnectors(WiresShape shape) {
        if (shape.getMagnets() == null) {
            return null;
        }
        Map<String, WiresConnector> connectors = new HashMap<String, WiresConnector>();
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

        for (WiresShape child : shape.getChildShapes()) {
            collectionSpecialConnectors(child,
                                        connectors);
        }
    }

    public static boolean isConnected(final WiresConnection connection) {
        return  null != connection && null != connection.getMagnet();
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
            for (WiresShape child : shape.getChildShapes()) {
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

    public static boolean checkForAndApplyLineSplice(WiresManager wiresManager,
                                                     WiresShape shape) {
        if (!wiresManager.isSpliceEnabled() || shape.getMagnets() == null) {
            // cannot connect to a shape with no magnets.
            return true;
        }

        boolean accept = true;
        for (WiresConnector c : wiresManager.getConnectorList()) {
            Point2DArray linePoints = ((OrthogonalPolyLine) c.getLine()).getComputedPoint2DArray();
            MultiPath path = shape.getPath();
            Point2DArray intersectPoints = null;
            Point2D absLoc = path.getComputedLocation();
            intersectPoints = getIntersections(linePoints,
                                               path,
                                               intersectPoints,
                                               absLoc);

            if ((c.getHeadConnection().getMagnet() != null && c.getHeadConnection().getMagnet().getMagnets().getWiresShape() == shape) ||
                    (c.getTailConnection().getMagnet() != null && c.getTailConnection().getMagnet().getMagnets().getWiresShape() == shape)) {
                // don't split yourself
                return accept;
            }

            if (intersectPoints != null) {
                WiresConnection headCon = c.getHeadConnection();
                WiresConnection tailCon = c.getTailConnection();

                if (intersectPoints.size() == 1) {
                    // one arrow end is enclosed in the shape, we can only splice/connect if that connection is not already connected.
                    BoundingBox bbox = shape.getContainer().getComputedBoundingPoints().getBoundingBox();
                    if (bbox.contains(headCon.getPoint()) && headCon.getMagnet() != null) {
                        return accept;
                    } else if (bbox.contains(tailCon.getPoint()) && headCon.getMagnet() != null) {
                        return accept;
                    } else {
                        throw new RuntimeException("Defensive programming: should not be possible if there is a single intersection.");
                    }
                }

                c.getControl().hideControlPoints();

                Point2DArray oldPoints = c.getLine().getPoint2DArray();
                int firstSegmentIndex = Integer.MAX_VALUE;
                int lastSegmentIndex = 0;
                for (Point2D p : intersectPoints) {
                    double x = p.getX() + absLoc.getX();
                    double y = p.getY() + absLoc.getY();

                    // get first and last segment, this can happen if shape straddles multiple segments of the line
                    int pointIndex = WiresConnector.getIndexForSelectedSegment(c,
                                                                                          (int) x,
                                                                                          (int) y,
                                                                                          oldPoints);
                    if (pointIndex < firstSegmentIndex) {
                        firstSegmentIndex = pointIndex;
                    }
                    if (pointIndex > lastSegmentIndex) {
                        lastSegmentIndex = pointIndex;
                    }
                }

                WiresConnector c2 = null;

                // record these, as they may need restoring later.
                double tailXOffset = 0;
                double tailYOffset = 0;
                boolean tailAutoConnection = false;
                Point2D tailPoint = null;
                WiresMagnet tailMagnet = null;
                if (tailCon != null) {
                    tailXOffset = tailCon.getXOffset();
                    tailYOffset = tailCon.getYOffset();
                    tailAutoConnection = tailCon.isAutoConnection();
                    tailMagnet = tailCon.getMagnet();
                    tailPoint = tailCon.getPoint();
                }

                if (firstSegmentIndex > 0) {
                    Point2DArray newPoints1 = new Point2DArray();
                    Point2DArray newPoints2 = new Point2DArray();

                    newPoints1.push(oldPoints.get(0));

                    for (int i = 1; i < firstSegmentIndex; i++) {
                        newPoints1.push(oldPoints.get(i));
                    }

                    WiresMagnet cmagnet = shape.getMagnets().getMagnet(1);

                    // check if isAllowed
                    WiresConnectionControlImpl.allowedMagnetAndUpdateAutoConnections(wiresManager,
                                                                                     headCon,
                                                                                     true,
                                                                                     shape,
                                                                                     cmagnet,
                                                                                     false);
                    accept = accept && WiresConnectionControlImpl.allowedMagnetAndUpdateAutoConnections(wiresManager,
                                                                                                        tailCon,
                                                                                                        false,
                                                                                                        shape,
                                                                                                        cmagnet,
                                                                                                        false);
                    if (!accept) {
                        return accept;
                    }

                    if (intersectPoints.size() > 1) {
                        Point2D startPoint = new Point2D(cmagnet.getControl().getX(),
                                                         cmagnet.getControl().getY());
                        newPoints2.push(startPoint);

                        // will skip any segments between first and last. this happens if a shape straddles multiple segments.
                        for (int i = lastSegmentIndex; i < oldPoints.size(); i++) {
                            newPoints2.push(oldPoints.get(i));
                        }

                        IDirectionalMultiPointShape<?> line = c.getLine().copy();
                        line.setPoint2DArray(newPoints2);
                        c2 = new WiresConnector(line,
                                                c.getHeadDecorator().copy(),
                                                c.getTailDecorator().copy());
                        wiresManager.register(c2);
                        WiresConnection headCon2 = c2.getHeadConnection();
                        headCon2.setAutoConnection(true);
                        headCon2.setXOffset(0); // reset, if not already 0
                        headCon2.setYOffset(0);

                        WiresConnection tailCon2 = c2.getTailConnection();
                        tailCon2.setAutoConnection(tailCon.isAutoConnection()); // preserve tail auto connection
                        tailCon2.setMagnet(tailCon.getMagnet());
                        tailCon2.setXOffset(tailCon.getXOffset()); //reset, if not already 0
                        tailCon2.setYOffset(tailCon.getYOffset());
                        tailCon2.setPoint(tailCon.getPoint());
                        accept = accept && WiresConnectionControlImpl.allowedMagnetAndUpdateAutoConnections(wiresManager,
                                                                                                            headCon2,
                                                                                                            true,
                                                                                                            shape,
                                                                                                            cmagnet,
                                                                                                            true);
                        if (!accept) {
                            // we already checked isAllowed before mutation, so this in theory should not be needed. Adding for future proofing and completeness - in
                            // case a future version doesn't require identical behavioural logic for allowed and accept.
                            tailCon2.setMagnet(null);
                            wiresManager.deregister(c2);
                            return accept;
                        }
                    }

                    // this is done after the potential newPoitns2, as it reads values from the original connector.
                    Point2D endPoint = new Point2D(cmagnet.getControl().getX(),
                                                   cmagnet.getControl().getY());
                    newPoints1.push(endPoint);
                    tailCon.setAutoConnection(true);
                    tailCon.setXOffset(0); // reset, if not already 0
                    tailCon.setYOffset(0);
                    tailCon.setPoint(endPoint);
                    c.getLine().setPoint2DArray(newPoints1);
                    accept = accept && WiresConnectionControlImpl.allowedMagnetAndUpdateAutoConnections(wiresManager,
                                                                                                        tailCon,
                                                                                                        false,
                                                                                                        shape,
                                                                                                        cmagnet,
                                                                                                        true);
                    if (!accept) {
                        // we already checked isAllowed before mutation, so this in theory should not be needed. Adding for future proofing and completeness - in
                        // case a future version doesn't require identical behavioural logic for allowed and accept.
                        if (c2 != null) {
                            c2.getTailConnection().setMagnet(null);
                            c2.getHeadConnection().setMagnet(null);
                            wiresManager.deregister(c2);
                        }

                        if (tailCon != null) {
                            tailCon.setAutoConnection(tailAutoConnection);
                            tailCon.setXOffset(tailXOffset); // reset, if not already 0
                            tailCon.setYOffset(tailYOffset);
                            tailCon.setMagnet(tailMagnet);
                            tailCon.setPoint(tailPoint);
                        }
                        return accept;
                    }
                }
            }
        }

        return accept;
    }

    private static Point2DArray getIntersections(Point2DArray linePoints,
                                                 MultiPath path,
                                                 Point2DArray intersectPoints,
                                                 Point2D absLoc) {
        for (PathPartList pathPartList : path.getActualPathPartListArray()) {
            intersectPoints = getPoint2Ds(linePoints,
                                          intersectPoints,
                                          absLoc,
                                          pathPartList);
        }
        return intersectPoints;
    }

    private static Point2DArray getPoint2Ds(Point2DArray linePoints,
                                            Point2DArray intersectPoints,
                                            Point2D absLoc,
                                            PathPartList pathPartList) {
        Point2DArray offsetLinePoints = new Point2DArray();

        for (Point2D p : linePoints) {
            offsetLinePoints.push(p.copy().offset(-absLoc.getX(),
                                                  -absLoc.getY()));
        }

        Point2DArray pathPartIntersectPoints = Geometry.getIntersectPolyLinePath(offsetLinePoints,
                                                                                 pathPartList,
                                                                                 false);
        if (pathPartIntersectPoints != null) {
            if (intersectPoints == null) {
                intersectPoints = new Point2DArray();
            }
            for (Point2D p : pathPartIntersectPoints) {
                intersectPoints.push(p);
            }
        }
        return intersectPoints;
    }
}
