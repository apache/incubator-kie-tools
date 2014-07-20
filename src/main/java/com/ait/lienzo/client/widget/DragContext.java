/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.widget;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;

/**
 * DragContext maintains information during a Drag operation of a Node.
 * <p>
 * Some values are defined in "global coordinates" (a.k.a. "canvas coordinates"
 * or "viewport coordinates"), 
 * e.g. the raw canvas event (x,y) values are defined in pixels relative to the top-left corner of the canvas.
 * <p> 
 * On the other hand, "local coordinates" are specific to a node's parent.
 * E.g. the X,Y position of a node is defined relative to the top-left corner of the parent node.
 * <p>
 * To convert between local and global coordinates, you'd need to concatenate the Transform
 * of the nodes ancestors, all the way down to the parent node (see {@link #getLocalToGlobal()}).
 * Converting from global to local coordinates requires the inverse of that Transform 
 * (see {@link #getGlobalToLocal()}.)
 */
public class DragContext
{
    // (x0,y0) in global coordinates - i.e. event(x,y) at start of drag operation
    private int                    m_dragStartX;

    private int                    m_dragStartY;

    // (x1,y1) in global coordinates - i.e. event(x,y) of last drag move
    private int                    m_eventX;

    private int                    m_eventY;

    // (dx,dy) in global coordinates
    private int                    m_dx            = 0;

    private int                    m_dy            = 0;

    // (dx,dy) in local coordinates (adjusted by DragConstraintsEnforcer)
    private Point2D                m_localAdjusted = new Point2D(0, 0);

    private Transform              m_globalToLocal;

    private Transform              m_localToGlobal;

    private IPrimitive<?>          m_node;

    private Point2D                m_p1            = new Point2D(0, 0); // (0,0) converted from global to local coordinates

    private DragConstraintEnforcer m_dragConstraints;

    private double                 m_nodeX;

    private double                 m_nodeY;

    /**
     * Starts a drag operation for the specified node.
     * 
     * @param event the first drag event
     * @param node the node that is being dragged
     */
    public DragContext(INodeXYEvent event, IPrimitive<?> node)
    {
        m_node = node;

        m_nodeX = node.getX();

        m_nodeY = node.getY();

        m_eventX = m_dragStartX = event.getX();

        m_eventY = m_dragStartY = event.getY();

        m_localToGlobal = m_node.getParent().getAbsoluteTransform();

        m_globalToLocal = m_localToGlobal.getInverse();

        // Convert one point from global to local coordinates
        // We need it when calculating (dx,dy) in local coordinates

        m_globalToLocal.transform(new Point2D(0, 0), m_p1);

        // Initialize the DragConstraintsEnforcer

        m_dragConstraints = node.getDragConstraints();

        if (m_dragConstraints != null) m_dragConstraints.startDrag(this);
    }

    /**
     * Draws the node during a drag operation.
     * Used internally.
     * 
     * @param context
     */
    public void drawNodeWithTransforms(Context2D context)
    {
        context.save();

        context.transform(m_localToGlobal);

        m_node.drawWithTransforms(context);

        context.restore();
    }

    /**
     * Updates the context for the specified Drag Move event.
     * Used internally.
     * 
     * @param event Drag Move event
     */
    public void dragUpdate(INodeXYEvent event)
    {
        m_eventX = event.getX();

        m_eventY = event.getY();

        m_dx = m_eventX - m_dragStartX;

        m_dy = m_eventY - m_dragStartY;

        Point2D p2 = new Point2D(0, 0);

        m_globalToLocal.transform(new Point2D(m_dx, m_dy), p2);

        m_localAdjusted.setX(p2.getX() - m_p1.getX());

        m_localAdjusted.setY(p2.getY() - m_p1.getY());

        // Let the constraints adjust the location if necessary

        if (m_dragConstraints != null)
        {
            m_dragConstraints.adjust(m_localAdjusted);
        }
        m_node.setX(m_nodeX + m_localAdjusted.getX());

        m_node.setY(m_nodeY + m_localAdjusted.getY());
    }

    /**
     * Called when the Drag operation is done.
     * It basically updates the node's new (X,Y) attributes.
     * 
     * Used internally.
     */
    public void dragDone()
    {
        // update X,Y attributes

        m_node.setX(m_nodeX + m_localAdjusted.getX());

        m_node.setY(m_nodeY + m_localAdjusted.getY());
    }

    /**
     * Moves the Node back to where it was before the drag operation.
     * Use this to undo the drag.
     */
    public void reset()
    {
        m_node.setX(m_nodeX);

        m_node.setY(m_nodeY);
    }

    /**
     * Returns x0 in global coordinates - i.e. event(x,y) at start of drag operation
     * 
     * @return int
     */
    public int getDragStartX()
    {
        return m_dragStartX;
    }

    /**
     * Returns y0 in global coordinates - i.e. event(x,y) at start of drag operation
     * 
     * @return int
     */
    public int getDragStartY()
    {
        return m_dragStartY;
    }

    /**
     * Returns x1 in global coordinates - i.e. event(x,y) of last drag move
     * @return
     */
    public int getEventX()
    {
        return m_eventX;
    }

    /**
     * Returns y1 in global coordinates - i.e. event(x,y) of last drag move
     * @return
     */
    public int getEventY()
    {
        return m_eventY;
    }

    /**
     * Returns dx (i.e. eventX - dragstartX) in global coordinates
     * 
     * @return int
     */
    public int getDx()
    {
        return m_dx;
    }

    /**
     * Returns dy (i.e. eventY - dragStartY) in global coordinates
     * 
     * @return int
     */
    public int getDy()
    {
        return m_dy;
    }

    /**
     * Returns the Transform that can be used to convert global
     * coordinates (i.e. canvas coordinates) to local coordinates
     * (i.e. within the context of the node's parent.)
     * This is the inverse of the localToGlobal transform.
     * 
     * @return
     */
    public Transform getGlobalToLocal()
    {
        return m_globalToLocal;
    }

    /**
     * Returns the Transform that can be used to convert local coordinates
     * (i.e. within the context of the node's parent) to global
     * coordinates (i.e. canvas coordinates).
     * This is the inverse of the globalToLocal transform.
     * 
     * @return
     */
    public Transform getLocalToGlobal()
    {
        return m_localToGlobal;
    }

    /**
     * Returns (dx,dy) in local coordinates, adjusted by the 
     * {@link DragConstraintEnforcer}
     * 
     * @return Point2D
     */
    public Point2D getLocalAdjusted()
    {
        return m_localAdjusted;
    }

    /**
     * Returns the node being dragged as an IPrimitive.
     * 
     * @return IPrimitive
     */
    public IPrimitive<?> getNode()
    {
        return m_node;
    }

    /**
     * Returns the {@link DragConstraintEnforcer} that adjusts the node
     * location during a drag operation.
     * 
     * @return {@link DragConstraintEnforcer}
     */
    public DragConstraintEnforcer getDragConstraints()
    {
        return m_dragConstraints;
    }
}