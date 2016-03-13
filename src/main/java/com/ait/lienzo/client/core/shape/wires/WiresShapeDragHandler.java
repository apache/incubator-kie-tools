/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.client.core.shape.wires.AlignAndDistribute.AlignAndDistributeHandler;

/**
 * This is a composite drag handler to manage the interference of DockingAndContainmentHandler and AlignAndDistribtueHandler during snap.
 *
 * The DockingAndContainment snap is applied first and thus takes priority. If DockingAndContainment snap is applied, then AlignAndDistribute snap is only applied if the
 * result is still on a point of the path. If the snap would move the point off the path, then the adjust is undone.
 *
 * If DockingAndContainment snap is not applied, then AlignAndDistribute can be appplied regardless.
 */
public class WiresShapeDragHandler implements NodeMouseDownHandler, NodeMouseUpHandler, NodeDragEndHandler, DragConstraintEnforcer
{
    private WiresShape                   m_shape;

    private AlignAndDistributeHandler    m_alignAndDistributeHandler;

    private DockingAndContainmentHandler m_dockingAndContainmentHandler;

    private double                       m_shapeStartX;

    private double                       m_shapeStartY;

    public WiresShapeDragHandler(WiresShape shape, WiresManager wiresManager)
    {
        m_shape = shape;
    }

    public void setAlignAndDistributeHandler(AlignAndDistributeHandler alignAndDistributeHandler)
    {
        m_alignAndDistributeHandler = alignAndDistributeHandler;
    }

    public void setDockingAndContainmentHandler(DockingAndContainmentHandler dockingAndContainmentHandler)
    {
        m_dockingAndContainmentHandler = dockingAndContainmentHandler;
    }

    @Override
    public void startDrag(DragContext dragContext)
    {

        Point2D absShapeLoc =  m_shape.getPath().getAbsoluteLocation();
        m_shapeStartX = absShapeLoc.getX();
        m_shapeStartY = absShapeLoc.getY();

        if ( m_dockingAndContainmentHandler != null )
        {
            m_dockingAndContainmentHandler.startDrag(dragContext);
        }

        if ( m_alignAndDistributeHandler != null )
        {
            m_alignAndDistributeHandler.startDrag(dragContext);
        }
    }

    @Override
    public boolean adjust(final Point2D dxy)
    {

        boolean adjusted1 = false;
        if ( m_dockingAndContainmentHandler != null )
        {
            adjusted1 = m_dockingAndContainmentHandler.adjust(dxy);
        }

        double dx = dxy.getX();
        double dy = dxy.getY();
        boolean adjusted2 = false;
        if ( m_alignAndDistributeHandler != null && m_alignAndDistributeHandler.isDraggable())
        {
            adjusted2 = m_alignAndDistributeHandler.adjust(dxy);
        }

        if ( adjusted1 && adjusted2 && ( dxy.getX() != dx || dxy.getY() != dy ) )
        {
            BoundingBox box = m_shape.getPath().getBoundingBox();

            PickerPart part = m_dockingAndContainmentHandler.getPicker().findShapeAt((int) (m_shapeStartX + dxy.getX() + (box.getWidth()/2)),
                                                                                     (int) (m_shapeStartY + dxy.getY() + (box.getHeight()/2)));

            if ( part == null || part.getShapePart() != PickerPart.ShapePart.BORDER)
            {
                dxy.setX(dx);
                dxy.setY(dy);
                adjusted2 = false;
            }
        }

        return adjusted1 || adjusted2;
    }

    @Override
    public void onNodeDragEnd(NodeDragEndEvent event)
    {
        if ( m_dockingAndContainmentHandler != null )
        {
            m_dockingAndContainmentHandler.onNodeDragEnd(event);
        }

        if ( m_alignAndDistributeHandler != null )
        {
            m_alignAndDistributeHandler.onNodeDragEnd(event);
        }
    }

    @Override
    public void onNodeMouseDown(NodeMouseDownEvent event)
    {
        if ( m_dockingAndContainmentHandler != null )
        {
            m_dockingAndContainmentHandler.onNodeMouseDown(event);
        }
    }

    @Override
    public void onNodeMouseUp(NodeMouseUpEvent event)
    {
        if ( m_dockingAndContainmentHandler != null )
        {
            m_dockingAndContainmentHandler.onNodeMouseUp(event);
        }
    }


}
