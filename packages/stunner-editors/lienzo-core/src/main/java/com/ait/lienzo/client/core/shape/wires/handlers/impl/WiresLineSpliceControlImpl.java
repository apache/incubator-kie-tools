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
import java.util.List;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.wires.ILineSpliceAcceptor;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLineSpliceControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.tools.client.collection.NFastArrayList;

import static com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresContainmentControlImpl.addIntoParent;
import static com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresContainmentControlImpl.calculateCandidateLocation;

public class WiresLineSpliceControlImpl extends AbstractWiresControl<WiresLineSpliceControlImpl> implements WiresLineSpliceControl {

    static final int XY_OFFSET = 25;

    WiresManager m_wiresManager;
    ILineSpliceAcceptor m_lineSpliceAcceptor;
    WiresConnector m_candidateConnector;
    Point2DArray m_firstHalfPoints;
    Point2DArray m_secondHalfPoints;
    Point2DArray m_intersectPoints;
    WiresContainer m_parent;

    public WiresLineSpliceControlImpl(final Supplier<WiresParentPickerControl> parentPickerControl) {
        super(parentPickerControl);

        m_wiresManager = getShape().getWiresManager();
        m_lineSpliceAcceptor = m_wiresManager.getLineSpliceAcceptor();
    }

    @Override
    public void destroy() {
        clear();

        m_wiresManager = null;
        m_lineSpliceAcceptor = null;
    }

    @Override
    public boolean onMove(double x, double y) {
        if (m_wiresManager.isSpliceEnabled()) {
            return isAllow();
        }

        return false;
    }

    @Override
    public void execute() {
        if (m_wiresManager.isSpliceEnabled()) {
            splice(true);

            // Containment
            if (null != m_parent) {
                addIntoParent(getShape(),
                              m_parent,
                              getWiresLayer(),
                              calculateCandidateLocation(getParentPickerControl(),
                                                         m_parent));
            }
        }
    }

    @Override
    public Point2D getAdjust() {
        return new Point2D(0, 0);
    }

    @Override
    public boolean isAllow() {
        return m_wiresManager.isSpliceEnabled() &&
                (splice(false) && runAcceptor(false));
    }

    @Override
    public boolean accept() {
        return m_wiresManager.isSpliceEnabled() && runAcceptor(true);
    }

    private boolean runAcceptor(final boolean acceptApply) {
        if (null == m_candidateConnector) {
            return false;
        }

        boolean allowAccept;
        final Point2D candidateLocation = calculateCandidateLocation(getParentPickerControl(), m_parent);

        allowAccept = m_lineSpliceAcceptor.allowSplice(getShape(),
                                                       new double[]{candidateLocation.getX(),
                                                               candidateLocation.getY()},
                                                       m_candidateConnector,
                                                       m_parent);
        if (acceptApply && allowAccept) {
            allowAccept = m_lineSpliceAcceptor.acceptSplice(getShape(),
                                                            new double[]{candidateLocation.getX(),
                                                                    candidateLocation.getY()},
                                                            m_candidateConnector,
                                                            convertPoints(m_firstHalfPoints),
                                                            convertPoints(m_secondHalfPoints),
                                                            m_parent);
        }

        return allowAccept;
    }

    @Override
    public void clear() {
        m_lineSpliceAcceptor.ensureUnHighLight();
        m_candidateConnector = null;
        m_firstHalfPoints = null;
        m_secondHalfPoints = null;
        m_intersectPoints = null;
        m_parent = null;
    }

    @Override
    public void reset() {
        clear();
    }

    boolean splice(final boolean acceptApply) {
        clear();

        if (getShape().getMagnets() == null) {
            // cannot connect to a shape with no magnets.
            return false;
        }

        for (WiresConnector candidateConnector : m_wiresManager.getConnectorList().asList()) {
            if ((candidateConnector.getHeadConnection().getMagnet() != null &&
                    candidateConnector.getHeadConnection().getMagnet().getMagnets().getWiresShape() == getShape()) ||
                    (candidateConnector.getTailConnection().getMagnet() != null &&
                            candidateConnector.getTailConnection().getMagnet().getMagnets().getWiresShape() == getShape())) {
                // don't split yourself
                return false;
            }

            m_intersectPoints = getIntersections(candidateConnector, getShape());

            if (m_intersectPoints != null) {
                if (m_intersectPoints.size() == 1) {
                    // One arrow end is enclosed in the shape, we can only splice/connect
                    // if that connection is not already connected.
                    return false;
                }

                final int[] intersectionSegmentIndexes = solveConnectorSegmentIntersectionIndexes(candidateConnector,
                                                                                                  getShape(),
                                                                                                  m_intersectPoints);

                if (intersectionSegmentIndexes[0] > 0) {
                    m_firstHalfPoints = new Point2DArray();
                    m_secondHalfPoints = new Point2DArray();

                    calculateNewPointsForLineSplicing(candidateConnector,
                                                      intersectionSegmentIndexes[0],
                                                      intersectionSegmentIndexes[1],
                                                      m_intersectPoints,
                                                      m_firstHalfPoints,
                                                      m_secondHalfPoints);

                    if (acceptApply) {
                        candidateConnector.getControl().hideControlPoints();

                        if (m_intersectPoints.size() > 1) {
                            cloneAndConnect(getShape(),
                                            candidateConnector,
                                            m_secondHalfPoints,
                                            m_wiresManager);
                        }

                        reconnectOriginalConnector(candidateConnector,
                                                   getShape(),
                                                   m_firstHalfPoints,
                                                   m_wiresManager);
                    }

                    m_candidateConnector = candidateConnector;
                    m_parent = getParentWhileSplicing(getParentPickerControl());

                    return true;
                }
            }
        }

        return false;
    }

    static void reconnectOriginalConnector(final WiresConnector originalConnector,
                                           final WiresShape spliceShape,
                                           final Point2DArray newPoints,
                                           final WiresManager wiresManager) {
        final WiresConnection tailCon = originalConnector.getTailConnection();
        final WiresMagnet headMagnet = originalConnector.getHeadConnection().getMagnet();
        final WiresMagnet tailMagnet = spliceShape.getMagnets().getMagnet(1);
        final Point2D endPoint = new Point2D(tailMagnet.getControl().getX(),
                                             tailMagnet.getControl().getY());
        newPoints.push(endPoint);

        // Deregister listeners before changing it
        wiresManager.deregister(originalConnector);

        originalConnector.getHeadConnection().setMagnet(headMagnet);
        originalConnector.getLine().setPoint2DArray(newPoints);

        tailCon.setAutoConnection(true);
        tailCon.setXOffset(0); // reset, if not already 0
        tailCon.setYOffset(0);
        tailCon.setPoint(endPoint);
        tailCon.setMagnet(tailMagnet);

        // Reattach listeners after changes
        wiresManager.register(originalConnector);
    }

    static void cloneAndConnect(final WiresShape spliceShape,
                                final WiresConnector originalConnector,
                                Point2DArray newPoints,
                                final WiresManager wiresManager) {
        WiresMagnet tailMagnet = null;
        WiresShape targetShape = null;
        final WiresConnection tailCon = originalConnector.getTailConnection();

        final WiresMagnet cMagnet = spliceShape.getMagnets().getMagnet(1);
        final Point2D startPoint = new Point2D(cMagnet.getControl().getX(),
                                               cMagnet.getControl().getY());
        // Add start point at the beginning
        newPoints.unshift(startPoint);

        if (null != tailCon) {
            tailMagnet = tailCon.getMagnet();
        }

        if (null != tailMagnet) {
            targetShape = tailMagnet.getMagnets().getWiresShape();
        }

        final IDirectionalMultiPointShape<?> line = originalConnector.getLine().cloneLine();
        line.setPoint2DArray(newPoints);

        // fix arrows orientation
        line.setHeadOffset(originalConnector.getLine().getHeadOffset());
        line.setTailOffset(originalConnector.getLine().getTailOffset());

        final WiresConnector newConnector = new WiresConnector(spliceShape.getMagnets().getMagnet(0),
                                                               tailMagnet,
                                                               line,
                                                               originalConnector.getHeadDecorator().copy(),
                                                               originalConnector.getTailDecorator().copy());

        newConnector.getHeadConnection().setAutoConnection(true);

        final WiresMagnet[] magnets = newConnector.getMagnetsOnAutoConnection(spliceShape, targetShape);
        if (magnets != null) {
            if (magnets[0] != null && newConnector.getHeadConnection().getMagnet() != magnets[0]) {
                newConnector.getHeadConnection().setMagnet(magnets[0]);
            }

            if (magnets[1] != null && newConnector.getTailConnection().getMagnet() != magnets[1]) {
                newConnector.getTailConnection().setMagnet(magnets[1]);
            }
        }

        wiresManager.register(newConnector);
    }

    static void calculateNewPointsForLineSplicing(final WiresConnector candidateConnector,
                                                  final int firstSegmentIndex,
                                                  final int lastSegmentIndex,
                                                  final Point2DArray intersectPoints,
                                                  final Point2DArray firstHalfPoints,
                                                  final Point2DArray secondHalfPoints) {
        Point2DArray oldPoints = candidateConnector.getLine().getPoint2DArray();

        if (firstSegmentIndex > 0) {
            firstHalfPoints.push(oldPoints.get(0));

            for (int i = 1; i < firstSegmentIndex; i++) {
                firstHalfPoints.push(oldPoints.get(i));
            }

            if (intersectPoints.size() > 1) {
                // will skip any segments between first and last. this happens if a shape straddles multiple segments.
                for (int i = lastSegmentIndex; i < oldPoints.size(); i++) {
                    secondHalfPoints.push(oldPoints.get(i));
                }
            }
        }
    }

    static int[] solveConnectorSegmentIntersectionIndexes(final WiresConnector connector,
                                                          final WiresShape spliceShape,
                                                          final Point2DArray intersectPoints) {
        final Point2DArray oldPoints = connector.getLine().getPoint2DArray();
        final Point2D absLoc = spliceShape.getPath().getComputedLocation();
        int firstSegmentIndex = Integer.MAX_VALUE;
        int lastSegmentIndex = 0;

        for (Point2D p : intersectPoints.asArray()) {
            final double x = p.getX() + absLoc.getX();
            final double y = p.getY() + absLoc.getY();

            // get first and last segment, this can happen if shape straddles multiple segments of the line
            final int pointIndex = WiresConnector.getIndexForSelectedSegment(connector,
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

        return new int[]{firstSegmentIndex, lastSegmentIndex};
    }

    static Point2DArray getIntersections(final WiresConnector candidateConnector,
                                         final WiresShape spliceShape) {
        Point2DArray intersectPoints = null;
        NFastArrayList<PathPartList> array = spliceShape.getPath().getActualPathPartListArray();

        for (int i = 0, size = array.size(); i < size; i++) {
            PathPartList pathPartList = array.get(i);
            intersectPoints = getPoint2Ds(candidateConnector.getLine().getPoint2DArray(),
                                          intersectPoints,
                                          spliceShape.getPath().getComputedLocation(),
                                          pathPartList);
        }

        return intersectPoints;
    }

    static Point2DArray getPoint2Ds(Point2DArray linePoints,
                                    Point2DArray intersectPoints,
                                    Point2D absLoc,
                                    PathPartList pathPartList) {
        final Point2DArray offsetLinePoints = new Point2DArray();

        for (int i = 0, size = linePoints.size(); i < size; i++) {
            final Point2D p = linePoints.get(i);
            offsetLinePoints.push(p.copy().offset(-absLoc.getX(),
                                                  -absLoc.getY()));
        }

        final Point2DArray pathPartIntersectPoints = Geometry.getIntersectPolyLinePath(offsetLinePoints,
                                                                                       pathPartList,
                                                                                       false);
        if (pathPartIntersectPoints != null) {
            if (intersectPoints == null) {
                intersectPoints = new Point2DArray();
            }

            for (int i = 0, size = pathPartIntersectPoints.size(); i < size; i++) {
                final Point2D p = pathPartIntersectPoints.get(i);
                intersectPoints.push(p);
            }
        }

        return intersectPoints;
    }

    static WiresContainer getParentWhileSplicing(final WiresParentPickerControl parentPickerControl) {
        final Point2D currentLocation = parentPickerControl.getCurrentLocation();
        WiresContainer parent = parentPickerControl.getParent();

        if (parent == parentPickerControl.getShape().getWiresManager().getLayer()) {
            PickerPart parentPart = parentPickerControl
                    .getIndex()
                    .findShapeAt((int) currentLocation.getX() + XY_OFFSET,
                                 (int) currentLocation.getY() - XY_OFFSET);

            if (null != parentPart) {
                parent = parentPart.getShape();
            }
        }

        return parent;
    }

    static List<double[]> convertPoints(final Point2DArray point2DArray) {
        List<double[]> points = new ArrayList<>();
        for (Point2D point : point2DArray.getPoints()) {
            points.add(new double[]{point.getX(), point.getY()});
        }
        return points;
    }
}