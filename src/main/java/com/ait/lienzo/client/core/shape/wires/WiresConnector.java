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

import static com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleStandardType.POINT;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ArrowEnd;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;

public class WiresConnector
{
    interface WiresConnectorHandler extends NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler, NodeMouseClickHandler
    {

        WiresConnectorControl getControl();

    }

    private WiresConnection                       m_headConnection;

    private WiresConnection                       m_tailConnection;

    private IControlHandleList                    m_pointHandles;

    private HandlerRegistrationManager            m_HandlerRegistrationManager;

    private AbstractDirectionalMultiPointShape<?> m_line;

    private MultiPathDecorator                    m_headDecorator;

    private MultiPathDecorator                    m_tailDecorator;

    private Group                                 m_group;

    private IConnectionAcceptor                   m_connectionAcceptor = IConnectionAcceptor.ALL;

    public WiresConnector(AbstractDirectionalMultiPointShape<?> line, MultiPathDecorator headDecorator, MultiPathDecorator tailDecorator)
    {
        m_line = line;
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

    public WiresConnector(WiresMagnet headMagnet, WiresMagnet tailMagnet, AbstractDirectionalMultiPointShape<?> line, MultiPathDecorator headDecorator, MultiPathDecorator tailDecorator)
    {
        this(line, headDecorator, tailDecorator);
        setHeadMagnet(headMagnet);
        setTailMagnet(tailMagnet);
    }

    public WiresConnector setHeadMagnet(WiresMagnet headMagnet)
    {
        if (null != headMagnet)
        {
            m_headConnection.setMagnet(headMagnet);
        }
        return this;
    }

    public WiresConnector setTailMagnet(WiresMagnet tailMagnet)
    {
        if (null != tailMagnet)
        {
            m_tailConnection.setMagnet(tailMagnet);
        }
        return this;
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

    public void setTailConnection(WiresConnection tailConnection)
    {
        m_tailConnection = tailConnection;
    }

    public void setPointHandles(IControlHandleList pointHandles)
    {
        m_pointHandles = pointHandles;
    }

    public AbstractDirectionalMultiPointShape<?> getLine()
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
        m_pointHandles.destroy();
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

    public void updateForSpecialConnections()
    {
        updateForCenterConnection();
        updateForAutoConnections();
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
        if ( connection.getMagnet() != null && connection.getMagnet().getIndexer() == 0 )
        {

            MultiPath   path              = connection.getMagnet().getMagnets().getWiresShape().getPath();
            BoundingBox box               = path.getBoundingBox();
            Point2D c = Geometry.findCenter(box);
            Point2D     intersectPoint    = Geometry.getPathIntersect(connection, path, c, pointIndex);
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

    public boolean updateForAutoConnections()
    {
        // used when a connection is not being dragged
        WiresConnection headC = getHeadConnection();
        WiresConnection tailC = getTailConnection();

        WiresShape headS = (headC.getMagnet() != null ) ? headC.getMagnet().getMagnets().getWiresShape() : null;
        WiresShape tailS = (tailC.getMagnet() != null ) ? tailC.getMagnet().getMagnets().getWiresShape() : null;

        return updateForAutoConnections(headS, tailS);
    }

    public boolean updateForAutoConnections(WiresShape headS, WiresShape tailS)
    {
        boolean accept = true;

        // Allowed conections has already been checked, but for consistency and notifications will be rechecked via acceptor
        // Will not set a magnet if the connection is already set to that returned magnet
        WiresMagnet[] magnets = getMagnetsOnAutoConnection(headS, tailS);
        if ( magnets != null )
        {
            if (magnets[0] != null && getHeadConnection().getMagnet() != magnets[0])
            {
                accept = accept && getConnectionAcceptor().acceptHead(getHeadConnection(), magnets[0]);
                if (accept)
                {
                    getHeadConnection().setMagnet(magnets[0]);
                }
            }

            if (accept && magnets[1] != null && getTailConnection().getMagnet() != magnets[1])
            {
                accept = accept && getConnectionAcceptor().acceptTail(getTailConnection(), magnets[1]);
                if (accept)
                {
                   getTailConnection().setMagnet(magnets[1]);
                }
            }
        }

        return accept;
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

        BoundingBox headBox = (headS != null ) ? headS.getGroup().getBoundingPoints().getBoundingBox() : null;
        BoundingBox tailBox = ( tailS != null ) ? tailS.getGroup().getBoundingPoints().getBoundingBox() : null;

        WiresMagnet[] magets;

        if ( headBox != null && tailBox != null && !headBox.overlaps(tailBox) )
        {
            magets = getMagnetsNonOverlappedShapes(headC, tailC, headS, tailS, headBox, tailBox);
        }
        else
        {
            magets = getMagnetsOverlappedShapesOrNoShape(headC, tailC, headS, tailS, headBox, tailBox);
        }

        return magets;
    }

    private WiresMagnet[] getMagnetsNonOverlappedShapes(WiresConnection headC, WiresConnection tailC, WiresShape headS, WiresShape tailS, BoundingBox headBox, BoundingBox tailBox)
    {
        // There is no shape overlap.
        // If one box is in the corner of the other box, then use nearest corner connections
        // else use nearest mid connection.
        boolean headAbove =  headBox.getBottom() < tailBox.getTop();
        boolean headBelow = headBox.getTop() > tailBox.getBottom();
        boolean headLeft = headBox.getRight() < tailBox.getLeft();
        boolean headRight = headBox.getLeft() > tailBox.getRight();

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

    private WiresMagnet[] getMagnets(WiresShape headS, int headMagnetIndex, WiresShape tailS, int tailMagnetIndex)
    {
        // ony set the side if it's auto connect, else null

        WiresMagnet headM = null;
        if ( getHeadConnection().isAutoConnection())
        {
           headM = headS.getMagnets().getMagnet(headMagnetIndex);
        }


        WiresMagnet tailM = null;
        if ( getTailConnection().isAutoConnection())
        {
            tailM = tailS.getMagnets().getMagnet(tailMagnetIndex);
        }
        return new WiresMagnet[] {headM, tailM};
    }


    static class WiresConnectorHandlerImpl implements WiresConnectorHandler
    {
        private final WiresConnectorControl m_control;

        private final WiresConnector        m_connector;

        WiresConnectorHandlerImpl(WiresConnector connector, WiresManager wiresManager)
        {
            this.m_control = wiresManager.getControlFactory().newConnectorControl(connector, wiresManager);
            this.m_connector = connector;
            init();
        }

        private void init()
        {
            if (m_connector.m_HandlerRegistrationManager != null)
            {
                m_connector.m_HandlerRegistrationManager.removeHandler();
            }

            m_connector.m_HandlerRegistrationManager = new HandlerRegistrationManager();

            m_connector.m_HandlerRegistrationManager.register(m_connector.getLine().addNodeMouseClickHandler(this));
            m_connector.m_HandlerRegistrationManager.register(m_connector.getHead().addNodeMouseClickHandler(this));
            m_connector.m_HandlerRegistrationManager.register(m_connector.getTail().addNodeMouseClickHandler(this));
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event)
        {
            this.m_control.dragStart(event.getDragContext());
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event)
        {
            this.m_control.dragMove(event.getDragContext());
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event)
        {
            this.m_control.dragEnd(event.getDragContext());
        }


        @Override
        public void onNodeMouseClick(NodeMouseClickEvent event)
        {

            if (m_connector.getPointHandles().isVisible())
            {
                if (event.isShiftKeyDown())
                {
                    this.m_control.addControlPoint(event.getX(), event.getY());
                }
                else
                {
                    this.m_control.hideControlPoints();
                }
            }
            else if (((Node<?> ) event.getSource()).getParent() == m_connector.getGroup() )
            {
                this.m_control.showControlPoints();
            }
        }

        public WiresConnectorControl getControl()
        {
            return m_control;
        }

    }

}
