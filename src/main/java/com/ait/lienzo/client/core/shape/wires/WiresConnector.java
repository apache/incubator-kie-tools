/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
// TODO - review DSJ

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ArrowEnd;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;

import static com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleStandardType.POINT;

public class WiresConnector
{
    public static boolean updateHeadTailForRefreshedConnector(WiresConnector c)
    {
        // Iterate each refreshed line and get the new points for the decorators
        if (c.getLine().asShape().getPathPartList().size() < 1)
        {
            // only do this for lines that have had refresh called
            IDirectionalMultiPointShape<?> line = c.getLine();

            if ( c.isSpecialConnection() && line.asShape().getPathPartList().size() == 0)
            {
                // if getPathPartList is empty, it was refreshed due to a point change
                c.updateForSpecialConnections(false);
            }

            final boolean prepared = line.isPathPartListPrepared(c.getLine().getAttributes());

            if (!prepared)
            {
                return true;
            }

            Point2DArray points     = line.getPoint2DArray();
            Point2D      p0         = points.get(0);
            Point2D      p1         = line.getHeadOffsetPoint();
            Point2DArray headPoints = new Point2DArray(p1, p0);
            c.getHeadDecorator().draw(headPoints);

            p0 = points.get(points.size() - 1);
            p1 = line.getTailOffsetPoint();
            Point2DArray tailPoints = new Point2DArray(p1, p0);
            c.getTailDecorator().draw(tailPoints);
        }
        return false;
    }

    private WiresConnection                       m_headConnection;

    private WiresConnection                       m_tailConnection;

    private IControlHandleList                    m_pointHandles;

    private HandlerRegistrationManager            m_HandlerRegistrationManager;

    private IDirectionalMultiPointShape<?> m_line;

    private MultiPathDecorator                    m_headDecorator;

    private MultiPathDecorator                    m_tailDecorator;

    private Group                                 m_group;

    private IConnectionAcceptor                   m_connectionAcceptor = IConnectionAcceptor.ALL;

    private WiresConnectorHandler                 m_wiresConnectorHandler;

    public WiresConnector(IDirectionalMultiPointShape<?> line, MultiPathDecorator headDecorator, MultiPathDecorator tailDecorator)
    {
        m_line = line;

        if (m_line instanceof OrthogonalPolyLine) {

            OrthogonalPolyLine polyline = (OrthogonalPolyLine)m_line;
            BoundingBox headBB = headDecorator.getPath().getBoundingBox();
            double breakDistance = Math.min(headBB.getWidth(), headBB.getHeight());
            polyline.setBreakDistance(breakDistance);
        }

        m_headDecorator = headDecorator;
        m_tailDecorator = tailDecorator;

        setHeadConnection(new WiresConnection(this, m_headDecorator.getPath(), ArrowEnd.HEAD));
        setTailConnection(new WiresConnection(this, m_tailDecorator.getPath(), ArrowEnd.TAIL));

        m_line.setEventPropagationMode(EventPropagationMode.FIRST_ANCESTOR);

        m_group = new Group();
        m_group.add(m_line);
        m_group.add(m_headDecorator.getPath());
        m_group.add(m_tailDecorator.getPath());

        // these are not draggable, only the group may or may not be draggable, depending if the line is connected or not
        m_line.setDraggable(false);
        m_headDecorator.getPath().setDraggable(false);
        m_tailDecorator.getPath().setDraggable(false);

        // The Line is only draggable if both Connections are unconnected
        setDraggable();
    }

    public WiresConnector(WiresMagnet headMagnet, WiresMagnet tailMagnet, IDirectionalMultiPointShape<?> line, MultiPathDecorator headDecorator, MultiPathDecorator tailDecorator)
    {
        this(line, headDecorator, tailDecorator);
        setHeadMagnet(headMagnet);
        setTailMagnet(tailMagnet);
    }

    public WiresConnector setHeadMagnet(WiresMagnet headMagnet)
    {
        m_headConnection.setMagnet(headMagnet);
        return this;
    }

    public WiresConnector setTailMagnet(WiresMagnet tailMagnet)
    {
        m_tailConnection.setMagnet(tailMagnet);
        return this;
    }


    public void select()
    {
        m_wiresConnectorHandler.getControl().showControlPoints();
        m_group.getLayer().batch();
    }

    public void unselect()
    {
        m_wiresConnectorHandler.getControl().hideControlPoints();
        m_group.getLayer().batch();
    }

    public IConnectionAcceptor getConnectionAcceptor()
    {
        return m_connectionAcceptor;
    }

    public void setConnectionAcceptor(IConnectionAcceptor connectionAcceptor)
    {
        m_connectionAcceptor = connectionAcceptor;
    }

    public void setWiresConnectorHandler( final HandlerRegistrationManager m_registrationManager,
                                          final WiresConnectorHandler handler )
    {

        final Group group = getGroup();

        m_registrationManager.register(group.addNodeDragStartHandler(handler));
        m_registrationManager.register(group.addNodeDragMoveHandler(handler));
        m_registrationManager.register(group.addNodeDragEndHandler(handler));

        if (m_HandlerRegistrationManager != null)
        {
            m_HandlerRegistrationManager.removeHandler();
        }
        m_HandlerRegistrationManager = new HandlerRegistrationManager();
        m_HandlerRegistrationManager.register(getLine().addNodeMouseClickHandler(handler));
        m_HandlerRegistrationManager.register(getLine().addNodeMouseDoubleClickHandler(handler));
        m_HandlerRegistrationManager.register(getHead().addNodeMouseClickHandler(handler));
        m_HandlerRegistrationManager.register(getTail().addNodeMouseClickHandler(handler));

        m_wiresConnectorHandler = handler;
    }

    public WiresConnectorHandler getWiresConnectorHandler()
    {
        return m_wiresConnectorHandler;
    }

    public void destroy()
    {
        destroyPointHandles();
        removeHandlers();
        removeFromLayer();
    }

    private void removeHandlers()
    {

        if (null != m_HandlerRegistrationManager)
        {
            m_HandlerRegistrationManager.removeHandler();
        }

    }

    public void addToLayer(Layer layer)
    {
        layer.add(m_group);
    }

    public void removeFromLayer()
    {
        m_group.removeFromParent();
    }

    public WiresConnection getHeadConnection()
    {
        return m_headConnection;
    }

    public void setHeadConnection(WiresConnection headConnection)
    {
        m_headConnection = headConnection;

    }

    public void setDraggable()
    {
        // The line can only be dragged if both Magnets are null
        m_group.setDraggable(isDraggable());
    }

    private boolean isDraggable()
    {
        return getHeadConnection().getMagnet() == null && getTailConnection().getMagnet() == null;
    }

    public WiresConnection getTailConnection()
    {
        return m_tailConnection;
    }

    public boolean isSpecialConnection()
    {
        return (m_headConnection != null && m_headConnection.isSpecialConnection()) || (m_tailConnection != null && m_tailConnection.isSpecialConnection());
    }

    public void setTailConnection(WiresConnection tailConnection)
    {
        m_tailConnection = tailConnection;
    }

    public void setPointHandles(IControlHandleList pointHandles)
    {
        m_pointHandles = pointHandles;
    }

    public IDirectionalMultiPointShape<?> getLine()
    {
        return m_line;
    }

    public MultiPathDecorator getHeadDecorator()
    {
        return m_headDecorator;
    }

    public MultiPathDecorator getTailDecorator()
    {
        return m_tailDecorator;
    }

    public MultiPath getHead()
    {
        return m_headDecorator.getPath();
    }

    public MultiPath getTail()
    {
        return m_tailDecorator.getPath();
    }

    public Group getGroup()
    {
        return m_group;
    }

    public String uuid()
    {
        return getGroup().uuid();
    }

    public void destroyPointHandles()
    {
        if (m_pointHandles != null)
        {
            m_pointHandles.destroy();
        }
        m_pointHandles = null;
    }

    public IControlHandleList getPointHandles()
    {
        if (m_pointHandles == null)
        {
            m_pointHandles = m_line.getControlHandles(POINT).get(POINT);
        }
        return m_pointHandles;
    }

    public void updateForSpecialConnections(boolean isAcceptOp)
    {
        updateForCenterConnection();
        updateForAutoConnections(isAcceptOp);
    }

    public void updateForCenterConnection()
    {
        WiresConnection headC = getHeadConnection();
        WiresConnection tailC = getTailConnection();

        updateForCenterConnection(this, headC, 1);
        updateForCenterConnection(this, tailC, getLine().getPoint2DArray().size()-2);
    }

    public static void updateForCenterConnection(WiresConnector connector, WiresConnection connection, int pointIndex)
    {
        if ( connection.getMagnet() != null && connection.getMagnet().getIndex() == 0 )
        {

            MultiPath   path              = connection.getMagnet().getMagnets().getWiresShape().getPath();
            BoundingBox box               = path.getBoundingBox();
            Point2D c = Geometry.findCenter(box);
            Point2D     intersectPoint    = Geometry.getPathIntersect(connection, path, c, pointIndex);
            if (null == intersectPoint) {
                intersectPoint = new Point2D();
            }

            Direction   d                 = MagnetManager.getDirection(intersectPoint, box);

            Point2D loc = path.getComputedLocation().copy();

            connection.setXOffset(intersectPoint.getX()-c.getX());
            connection.setYOffset(intersectPoint.getY()-c.getY());

            if ( connector.getHeadConnection() == connection )
            {
                connector.getLine().setHeadDirection(d);
            }
            else
            {
                connector.getLine().setTailDirection(d);
            }
            connection.move(loc.getX() + c.getX(), loc.getY() + c.getY());
        }
    }

    public boolean updateForAutoConnections(boolean isAcceptOp)
    {
        // used when a connection is not being dragged
        WiresConnection headC = getHeadConnection();
        WiresConnection tailC = getTailConnection();

        WiresShape headS = (headC.getMagnet() != null ) ? headC.getMagnet().getMagnets().getWiresShape() : null;
        WiresShape tailS = (tailC.getMagnet() != null ) ? tailC.getMagnet().getMagnets().getWiresShape() : null;

        return updateForAutoConnections(headS, tailS, isAcceptOp);
    }

    public boolean updateForAutoConnections(WiresShape headS, WiresShape tailS, boolean isAcceptOp)
    {
        boolean accept = true;

        // Allowed conections has already been checked, but for consistency and notifications will be rechecked via acceptor
        // Will not set a magnet if the connection is already set to that returned magnet
        WiresMagnet[] magnets = getMagnetsOnAutoConnection(headS, tailS);
        if ( magnets != null )
        {
            if (magnets[0] != null && getHeadConnection().getMagnet() != magnets[0])
            {
                accept = accept && executeHeadConnectionOperation(headS, magnets[0], isAcceptOp);
                if (accept)
                {
                    getHeadConnection().setMagnet(magnets[0]);
                }
            }

            if (accept && magnets[1] != null && getTailConnection().getMagnet() != magnets[1])
            {
                accept = accept && executeTailConnectionOperation(tailS, magnets[1], isAcceptOp);
                if (accept)
                {
                   getTailConnection().setMagnet(magnets[1]);
                }
            }
        }

        return accept;
    }

    private boolean executeHeadConnectionOperation(final WiresShape shape,
                                                   final WiresMagnet magnet,
                                                   final boolean isAcceptOp) {
        return isAcceptOp ?
                getConnectionAcceptor().acceptHead(getHeadConnection(), magnet) :
                getConnectionAcceptor().headConnectionAllowed(getHeadConnection(), shape);
    }

    private boolean executeTailConnectionOperation(final WiresShape shape,
                                                   final WiresMagnet magnet,
                                                   final boolean isAcceptOp) {
        return isAcceptOp ?
                getConnectionAcceptor().acceptTail(getTailConnection(), magnet) :
                getConnectionAcceptor().tailConnectionAllowed(getTailConnection(), shape);
    }

    /**
     * This is making some assumptions that will have to be fixed for anythign other than 8 Magnets at compass ordinal points.
     * If there is no shape overlap, and one box is in the corner of the other box, then use nearest corner connections, else use nearest mid connection.
     * Else there is overlap. This is now much more difficult, so just pick which every has the the shortest distanceto connections not contained by the other shape.
     */
    public WiresMagnet[] getMagnetsOnAutoConnection(WiresShape headS, WiresShape tailS)
    {
        WiresConnection headC = getHeadConnection();
        WiresConnection tailC = getTailConnection();

        if (!(headC.isAutoConnection() || tailC.isAutoConnection()))
        {
            // at least one side must be connected with auto connection on
            return null;
        }

        WiresMagnet[] magnets;
        BoundingBox headBox = (headS != null) ? headS.getGroup().getComputedBoundingPoints().getBoundingBox() : null;
        BoundingBox tailBox = (tailS != null) ? tailS.getGroup().getComputedBoundingPoints().getBoundingBox() : null;

        if ( getLine().getPoint2DArray().size() > 2 )
        {
            magnets = getMagnetsWithMidPoint(headC, tailC, headS, tailS, headBox, tailBox);
        }
        else
        {
            if (headBox != null && tailBox != null && !headBox.intersects(tailBox))
            {
                magnets = getMagnetsNonOverlappedShapes(headS, tailS, headBox, tailBox);
            }
            else
            {
                magnets = getMagnetsOverlappedShapesOrNoShape(headC, tailC, headS, tailS, headBox, tailBox);
            }
        }

        return magnets;
    }

    private WiresMagnet[] getMagnetsWithMidPoint(WiresConnection headC, WiresConnection tailC, WiresShape headS, WiresShape tailS, BoundingBox headBox, BoundingBox tailBox)
    {
        // make BB's of 1 Point2D, then we can reuse existing code.
        Point2D pAfterHead = getLine().getPoint2DArray().get(1);
        Point2D pBeforeTail = getLine().getPoint2DArray().get(getLine().getPoint2DArray().size()-2);

        BoundingBox firstBB = new BoundingBox(pAfterHead, pAfterHead);
        BoundingBox lastBB = new BoundingBox(pBeforeTail, pBeforeTail);

        WiresMagnet headM;
        WiresMagnet tailM;
        if (headBox != null && !headBox.intersects(firstBB))
        {
            WiresMagnet[] magnets = getMagnetsNonOverlappedShapes(headS, null, headBox, firstBB);
            headM = magnets[0];

        }
        else
        {
            WiresMagnet[] headMagnets = getMagnets(headC, headS);
            headM = getShortestMagnetToPoint(pAfterHead, headMagnets);
        }

        if (tailBox != null && !tailBox.intersects(lastBB))
        {
            WiresMagnet[] magnets = getMagnetsNonOverlappedShapes(null, tailS, lastBB, tailBox);
            tailM = magnets[1];
        }
        else
        {
            WiresMagnet[] tailMagnets = getMagnets(tailC, tailS);
            tailM = getShortestMagnetToPoint(pAfterHead, tailMagnets);
        }

        return new WiresMagnet[] {headM, tailM};
    }

    private WiresMagnet getShortestMagnetToPoint(Point2D point, WiresMagnet[] magnets)
    {
        double shortest = Double.MAX_VALUE;
        WiresMagnet shortestM = null;

        // Start at 1, as we don't include the center
        for (int i = 1, size0 = magnets.length; i < size0; i++)
        {
            WiresMagnet m = magnets[i];
            if (m != null)
            {
                double distance = point.distance(m.getControl().getComputedLocation());
                if (distance < shortest)
                {
                    shortest = distance;
                    shortestM = m;
                }
            }
        }

        return shortestM;
    }

    private WiresMagnet[] getMagnetsNonOverlappedShapes(WiresShape headS, WiresShape tailS, BoundingBox headBox, BoundingBox tailBox)
    {
        // There is no shape overlap.
        // If one box is in the corner of the other box, then use nearest corner connections
        // else use nearest mid connection.
        boolean headAbove = headBox.getMaxY() < tailBox.getMinY();
        boolean headBelow = headBox.getMinY() > tailBox.getMaxY();
        boolean headLeft = headBox.getMaxX() < tailBox.getMinX();
        boolean headRight = headBox.getMinX() > tailBox.getMaxX();

        WiresMagnet[] magets = null;
        if ( headAbove )
        {
            if ( headLeft )
            {
                magets = getMagnets(headS, 4, tailS, 8);
            }
            else if ( headRight )
            {
                magets = getMagnets(headS, 6, tailS, 2);
            }
            else
            {
                magets = getMagnets(headS, 5, tailS, 1);
            }
        }
        else if ( headBelow )
        {
            if ( headLeft )
            {
                magets = getMagnets(headS, 2, tailS, 6);
            }
            else if ( headRight )
            {
                magets = getMagnets(headS, 8, tailS, 4);
            }
            else
            {
                magets = getMagnets(headS, 1, tailS, 5);
            }
        }
        else
        {
            if ( headLeft )
            {
                magets = getMagnets(headS, 3, tailS, 7);
            }
            else if ( headRight )
            {
                magets = getMagnets(headS, 7, tailS, 3);
            }
        }

        return magets;
    }

    private WiresMagnet[] getMagnetsOverlappedShapesOrNoShape(WiresConnection headC, WiresConnection tailC, WiresShape headS, WiresShape tailS, BoundingBox headBox, BoundingBox tailBox)
    {
        // There is shape overlap, this is now much more difficult, so just pick which every has the the shortest distance
        // while giving preference to connections not contained by the other shape.
        double      shortest         = Double.MAX_VALUE;
        WiresMagnet shortestHeadM    = null;
        WiresMagnet shortestTailM    = null;
        double      headOffset       = headC.getLine().getHeadOffset();
        double      tailOffset       = tailC.getLine().getTailOffset();
        double      correction       = headC.getLine().getCorrectionOffset();

        int minContained = Integer.MAX_VALUE;

        // to be able to handle auto connection and fixed, in the same logic. simply copy the available magnets to an array. Fixed is an array of 1
        WiresMagnet[] headMagnets = getMagnets(headC, headS);
        WiresMagnet[] tailMagnets = getMagnets(tailC, tailS);

        // Start at 1, as we don't include the center
        for (int i = 1, size0 = headMagnets.length; i < size0; i++)
        {
            WiresMagnet headM              = headMagnets[i];
            Point2D     headOffSettedPoint;
            Point2D     headOriginalPoint;

            if ( headS != null && headM != null)
            {


                headOriginalPoint = headM.getControl().getComputedLocation();
                headOffSettedPoint = headOriginalPoint.copy();
                OrthogonalPolyLine.correctEndWithOffset(headOffset, headM.getDirection(), headOffSettedPoint);
                OrthogonalPolyLine.correctEndWithOffset(correction, headM.getDirection(), headOffSettedPoint);
            }
            else
            {
                // Ideally this would have also applied corrections, but at this point it's not easy to determine the Direction,
                // but should probably be done at some point in the future (TODO).
                headOriginalPoint =  headC.getControl().getComputedLocation();
                headOffSettedPoint = headOriginalPoint.copy();
            }

            boolean headContained = (tailBox != null) ? tailBox.contains(headOriginalPoint) : false;

            for (int j = 1, size1 = tailMagnets.length; j < size1; j++)
            {
                WiresMagnet tailM = tailMagnets[j];

                Point2D tailOffSettedPoint;
                Point2D tailOriginalPoint;

                if ( tailS != null && tailM != null)
                {
                    tailOriginalPoint = tailM.getControl().getComputedLocation();
                    tailOffSettedPoint = tailOriginalPoint.copy();
                    OrthogonalPolyLine.correctEndWithOffset(tailOffset, tailM.getDirection(), tailOffSettedPoint);
                    OrthogonalPolyLine.correctEndWithOffset(correction, tailM.getDirection(), tailOffSettedPoint);
                }
                else
                {
                    // Ideally this would have also applied corrections, but at this point it's not easy to determine the Direction,
                    // but should probably be done at some point in the future (TODO).
                    tailOriginalPoint =  tailC.getControl().getComputedLocation();
                    tailOffSettedPoint = tailOriginalPoint.copy();
                }

                boolean tailContained = (headBox != null) ? headBox.contains(tailOriginalPoint) : false;

                double distance  = headOffSettedPoint.distance(tailOffSettedPoint);
                int    contained = 0;
                if (headContained)
                {
                    contained++;
                }
                if (tailContained)
                {
                    contained++;
                }
                if (contained < minContained || (contained == minContained && distance <= shortest))
                {
                    minContained = contained;
                    shortest = distance;
                    shortestHeadM = headM;
                    shortestTailM = tailM;
                }
            }
        }

        return new WiresMagnet[] {shortestHeadM, shortestTailM};
    }

    private WiresMagnet[] getMagnets(WiresConnection connection, WiresShape shape)
    {
        WiresMagnet[] magnets;
        if ( connection.isAutoConnection() )
        {
            magnets = new WiresMagnet[shape.getMagnets().size()];
            for ( int i = 0, size = shape.getMagnets().size(); i < size; i++)
            {
                magnets[i] = shape.getMagnets().getMagnet(i);
            }
        }
        else if (shape == null)
        {
            // set it to 2, as centre is first, which is skipped.
            magnets = new WiresMagnet[2];
        }
        else
        {
            // set it to 2, as centre is first, which is skipped. And only populate the second
            magnets = new WiresMagnet[] {null, connection.getMagnet()};
        }
        return magnets;
    }

    WiresMagnet[] getMagnets(WiresShape headS, int headMagnetIndex, WiresShape tailS, int tailMagnetIndex)
    {
        // ony set the side if it's auto connect, else null

        // it uses 9, as center is 0
        int[] headMappng = null;
        int[] tailMappng = null;
        final boolean hasHeadMagnets = null != headS && null != headS.getMagnets();
        final boolean hasTailMagnets = null != tailS && null != tailS.getMagnets();
        if (hasHeadMagnets)
        {
            headMappng = headS.getMagnets().size() == 9 ? MagnetManager.EIGHT_CARDINALS_MAPPING : MagnetManager.FOUR_CARDINALS_MAPPING;
        }
        if (hasTailMagnets)
        {
            tailMappng = tailS.getMagnets().size() == 9 ? MagnetManager.EIGHT_CARDINALS_MAPPING : MagnetManager.FOUR_CARDINALS_MAPPING;
        }

        WiresMagnet headM = null;
        if ( hasHeadMagnets && getHeadConnection().isAutoConnection())
        {
           headM = headS.getMagnets().getMagnet(headMappng[headMagnetIndex]);
        }


        WiresMagnet tailM = null;
        if ( hasTailMagnets && getTailConnection().isAutoConnection())
        {
            tailM = tailS.getMagnets().getMagnet(tailMappng[tailMagnetIndex]);
        }
        return new WiresMagnet[] {headM, tailM};
    }

    public Point2DArray getControlPoints(){
        return m_line.getPoint2DArray();
    }

    @Override public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        WiresConnector that = (WiresConnector) o;

        return getGroup().uuid() == that.getGroup().uuid();
    }

    @Override public int hashCode()
    {
        return getGroup().uuid().hashCode();
    }
}
