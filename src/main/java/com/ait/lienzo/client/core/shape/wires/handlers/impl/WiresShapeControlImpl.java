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

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.AlignAndDistributeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingAndContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.client.widget.DragContext;

/**
 * The DockingAndContainment snap is applied first and thus takes priority. If DockingAndContainment snap is applied, then AlignAndDistribute snap is only applied if the
 * result is still on a point of the path. If the snap would move the point off the path, then the adjust is undone.
 *
 * If DockingAndContainment snap is not applied, then AlignAndDistribute can be applied regardless.
 */
public class WiresShapeControlImpl implements WiresShapeControl
{
    @SuppressWarnings("unused")
    private WiresManager                      m_manager;

    private WiresShape                        m_shape;

    private AlignAndDistributeControl         m_alignAndDistributeControl;

    private WiresDockingAndContainmentControl m_dockingAndContainmentControl;

    private double                            m_shapeStartX;

    private double                            m_shapeStartY;

    private WiresConnector[]                  m_connectorsWithSpecialConnections;

    public WiresShapeControlImpl(WiresShape shape, WiresManager wiresManager)
    {
        m_manager = wiresManager;
        m_shape = shape;
    }

    @Override
    public void setAlignAndDistributeControl(AlignAndDistributeControl alignAndDistributeHandler)
    {
        m_alignAndDistributeControl = alignAndDistributeHandler;
    }

    @Override
    public void setDockingAndContainmentControl(WiresDockingAndContainmentControl m_dockingAndContainmentControl)
    {
        this.m_dockingAndContainmentControl = m_dockingAndContainmentControl;
    }


    @Override
    public void dragStart(final DragContext context)
    {

        final Point2D absShapeLoc = m_shape.getPath().getComputedLocation();
        m_shapeStartX = absShapeLoc.getX();
        m_shapeStartY = absShapeLoc.getY();

        if (m_dockingAndContainmentControl != null)
        {
            m_dockingAndContainmentControl.dragStart(context);
        }

        if (m_alignAndDistributeControl != null)
        {
            m_alignAndDistributeControl.dragStart();
        }

        // index nested shapes that have special connectors, to avoid searching during drag.
        m_connectorsWithSpecialConnections = collectionSpecialConnectors(m_shape);

    }

    public static  WiresConnector[] collectionSpecialConnectors(WiresShape shape)
    {
        if (shape.getMagnets() == null)
        {
            return null;
        }
        Map<String, WiresConnector> connectors = new HashMap<String, WiresConnector>();
        collectionSpecialConnectors(shape, connectors);
        WiresConnector[] connectorsWithSpecialConnections = connectors.values().toArray(new WiresConnector[connectors.size()]);
        return connectorsWithSpecialConnections;
    }

    public static void collectionSpecialConnectors(WiresShape shape, Map<String, WiresConnector> connectors)
    {
        // start with 0, as we can have center connections too
        for ( int i = 0, size0 = shape.getMagnets().size(); i < size0; i++ )
        {
            WiresMagnet m = shape.getMagnets().getMagnet(i);
            for ( int j = 0, size1 = m.getConnectionsSize(); j < size1; j++ )
            {
                WiresConnection connection = m.getConnections().get(j);
                if (connection.isSpecialConnection())
                {
                    connectors.put(connection.getConnector().getGroup().uuid(), connection.getConnector());
                }
            }
        }

        for (WiresShape child : shape.getChildShapes())
        {

            collectionSpecialConnectors(child, connectors);
        }
    }


    @Override
    public boolean dragEnd(final DragContext context)
    {
        boolean allowed = true;

        if (m_dockingAndContainmentControl != null)
        {
            allowed = m_dockingAndContainmentControl.dragEnd(context);
        }

        allowed = allowed & checkForAndApplyLineSplice();

        // Cancel the drag operation if docking or containment not allowed.
        if (!allowed)
        {
            context.reset();
        }
        else
        {
            updateSpecialConnections(m_connectorsWithSpecialConnections);
        }

        if (m_alignAndDistributeControl != null)
        {
            m_alignAndDistributeControl.dragEnd();
        }

        return allowed;
    }

    private boolean checkForAndApplyLineSplice()
    {
        if (!m_manager.isSpliceEnabled() || m_shape.getMagnets() == null)
        {
            // cannot connect to a shape with no magnets.
            return true;
        }



        boolean accept = true;
        for (WiresConnector c : m_manager.getConnectorList())
        {
            Point2DArray linePoints      = ((OrthogonalPolyLine)c.getLine()).getComputedPoint2DArray();
            MultiPath    path            = m_shape.getPath();
            Point2DArray intersectPoints = null;
            Point2D      absLoc          = path.getComputedLocation();
            intersectPoints = getIntersections(linePoints, path, intersectPoints, absLoc);

            if ( (c.getHeadConnection().getMagnet() != null && c.getHeadConnection().getMagnet().getMagnets().getWiresShape() == m_shape) ||
                 (c.getTailConnection().getMagnet() != null && c.getTailConnection().getMagnet().getMagnets().getWiresShape() == m_shape) )
            {
                // don't split yourself
                return accept;
            }

            if (intersectPoints != null)
            {
                WiresConnection headCon = c.getHeadConnection();
                WiresConnection tailCon = c.getTailConnection();

                if ( intersectPoints.size() == 1)
                {
                    // one arrow end is enclosed in the shape, we can only splice/connect if that connection is not already connected.
                    BoundingBox bbox = m_shape.getContainer().getComputedBoundingPoints().getBoundingBox();
                    if ( bbox.contains(headCon.getPoint()) && headCon.getMagnet()!=null )
                    {
                        return accept;
                    }
                    else if ( bbox.contains(tailCon.getPoint()) && headCon.getMagnet()!=null)
                    {
                        return accept;
                    }
                    else
                    {
                        throw new RuntimeException("Defensive programming: should not be possible if there is a single intersection.");
                    }

                }

                c.getWiresConnectorHandler().getControl().hideControlPoints();

                Point2DArray oldPoints = c.getLine().getPoint2DArray();
                int firstSegmentIndex = Integer.MAX_VALUE;
                int lastSegmentIndex = 0;
                for (Point2D p : intersectPoints)
                {
                    double x = p.getX() + absLoc.getX();
                    double y = p.getY() + absLoc.getY();

                    // get first and last segment, this can happen if shape straddles multiple segments of the line
                    int pointIndex = WiresConnectorControlImpl.getIndexForSelectedSegment(c, (int) x, (int)  y, oldPoints);
                    if (pointIndex < firstSegmentIndex)
                    {
                        firstSegmentIndex = pointIndex;
                    }
                    if ( pointIndex > lastSegmentIndex )
                    {
                        lastSegmentIndex = pointIndex;
                    }
                }



                WiresConnector c2 = null;


                // record these, as they may need restoring later.
                double      tailXOffset = 0;
                double      tailYOffset = 0;
                boolean     tailAutoConnection = false;
                Point2D     tailPoint = null;
                WiresMagnet tailMagnet  = null;
                if ( tailCon != null )
                {
                    tailXOffset        = tailCon.getXOffset();
                    tailYOffset        = tailCon.getYOffset();
                    tailAutoConnection = tailCon.isAutoConnection();
                    tailMagnet         = tailCon.getMagnet();
                    tailPoint          = tailCon.getPoint();
                }

                if (firstSegmentIndex > 0)
                {
                    Point2DArray newPoints1 = new Point2DArray();
                    Point2DArray newPoints2 = new Point2DArray();

                    newPoints1.push(oldPoints.get(0));

                    for (int i = 1; i < firstSegmentIndex; i++)
                    {
                        newPoints1.push(oldPoints.get(i));
                    }

                    WiresMagnet cmagnet = m_shape.getMagnets().getMagnet(1);



                    // check if isAllowed
                    WiresConnectionControlImpl.allowedMagnetAndUpdateAutoConnections(headCon, true, m_shape, cmagnet, false);
                    accept = accept && WiresConnectionControlImpl.allowedMagnetAndUpdateAutoConnections(tailCon, false, m_shape, cmagnet, false);
                    if ( !accept )
                    {
                        return accept;
                    }

                    if ( intersectPoints.size() > 1 )
                    {
                        Point2D startPoint = new Point2D(cmagnet.getControl().getX(), cmagnet.getControl().getY());
                        newPoints2.push(startPoint);

                        // will skip any segments between first and last. this happens if a shape straddles multiple segments.
                        for (int i = lastSegmentIndex; i < oldPoints.size(); i++)
                        {
                            newPoints2.push(oldPoints.get(i));
                        }

                        AbstractDirectionalMultiPointShape<?> line = c.getLine().copy();
                        line.setPoint2DArray(newPoints2);
                        c2 = new WiresConnector(line, c.getHeadDecorator().copy(), c.getTailDecorator().copy());
                        m_manager.register(c2);
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
                        accept = accept && WiresConnectionControlImpl.allowedMagnetAndUpdateAutoConnections(headCon2, true, m_shape, cmagnet, true);
                        if ( !accept)
                        {
                            // we already checked isAllowed before mutation, so this in theory should not be needed. Adding for future proofing and completeness - in
                            // case a future version doesn't require identical behavioural logic for allowed and accept.
                            tailCon2.setMagnet(null);
                            m_manager.deregister(c2);
                            return accept;
                        }
                    }

                    // this is done after the potential newPoitns2, as it reads values from the original connector.
                    Point2D endPoint = new Point2D(cmagnet.getControl().getX(), cmagnet.getControl().getY());
                    newPoints1.push( endPoint );
                    tailCon.setAutoConnection(true);
                    tailCon.setXOffset(0); // reset, if not already 0
                    tailCon.setYOffset(0);
                    tailCon.setPoint(endPoint);
                    c.getLine().setPoint2DArray(newPoints1);
                    accept = accept && WiresConnectionControlImpl.allowedMagnetAndUpdateAutoConnections(tailCon, false, m_shape, cmagnet, true);
                    if ( !accept)
                    {
                        // we already checked isAllowed before mutation, so this in theory should not be needed. Adding for future proofing and completeness - in
                        // case a future version doesn't require identical behavioural logic for allowed and accept.
                        if (c2 != null)
                        {
                            c2.getTailConnection().setMagnet(null);
                            c2.getHeadConnection().setMagnet(null);
                            m_manager.deregister(c2);
                        }

                        if (tailCon != null)
                        {
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

    private Point2DArray getIntersections(Point2DArray linePoints, MultiPath path, Point2DArray intersectPoints, Point2D absLoc)
    {
        for (PathPartList pathPartList : path.getPathPartListArray())
        {
            intersectPoints = getPoint2Ds(linePoints, intersectPoints, absLoc, pathPartList);
        }
        return intersectPoints;
    }

    private Point2DArray getPoint2Ds(Point2DArray linePoints, Point2DArray intersectPoints, Point2D absLoc, PathPartList pathPartList)
    {
        Point2DArray offsetLinePoints = new Point2DArray();

        for (Point2D p : linePoints)
        {
            offsetLinePoints.push( p.copy().offset(-absLoc.getX(), -absLoc.getY()) );
        }

        Point2DArray pathPartIntersectPoints = Geometry.getIntersectPolyLinePath(offsetLinePoints, pathPartList, false);
        if (pathPartIntersectPoints != null)
        {
            if (intersectPoints == null)
            {
                intersectPoints = new Point2DArray();
            }
            for (Point2D p : pathPartIntersectPoints)
            {
                intersectPoints.push(p);
            }
        }
        return intersectPoints;
    }

    @Override
    public void dragMove(final DragContext context)
    {
        // Nothing to do.
    }

    @Override
    public boolean dragAdjust(final Point2D dxy)
    {

        boolean adjusted1 = false;
        if (m_dockingAndContainmentControl != null)
        {
            adjusted1 = m_dockingAndContainmentControl.dragAdjust(dxy);
        }

        double dx = dxy.getX();
        double dy = dxy.getY();
        boolean adjusted2 = false;
        if (m_alignAndDistributeControl != null && m_alignAndDistributeControl.isDraggable())
        {
            adjusted2 = m_alignAndDistributeControl.dragAdjust(dxy);
        }

        if (adjusted1 && adjusted2 && (dxy.getX() != dx || dxy.getY() != dy))
        {
            BoundingBox box = m_shape.getPath().getBoundingBox();

            PickerPart part = m_dockingAndContainmentControl.getPicker().findShapeAt((int) (m_shapeStartX + dxy.getX() + (box.getWidth() / 2)), (int) (m_shapeStartY + dxy.getY() + (box.getHeight() / 2)));

            if (part == null || part.getShapePart() != PickerPart.ShapePart.BORDER)
            {
                dxy.setX(dx);
                dxy.setY(dy);
                adjusted2 = false;
            }
        }

        updateSpecialConnections(m_connectorsWithSpecialConnections);


        return adjusted1 || adjusted2;

    }

    public static void updateSpecialConnections(WiresConnector[] connectors)
    {
        for( WiresConnector connector : connectors)
        {
            connector.updateForSpecialConnections();
        }
    }

    @Override
    public void onNodeMouseDown(NodeMouseDownEvent e)
    {
        if (m_dockingAndContainmentControl != null)
        {
            m_dockingAndContainmentControl.onNodeMouseDown(e);
        }
    }

    @Override
    public void onNodeMouseUp(NodeMouseUpEvent e)
    {
        if (m_dockingAndContainmentControl != null)
        {
            m_dockingAndContainmentControl.onNodeMouseUp(e);
        }
    }

    @Override
    public void onNodeClick(NodeMouseClickEvent e)
    {
        if ( m_manager.getSelectionManager() != null )
        {
            m_manager.getSelectionManager().selected(m_shape, e);
        }
        m_shape.getGroup().getLayer().draw();
    }

}
