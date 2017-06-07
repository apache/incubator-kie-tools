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
import com.ait.lienzo.client.core.types.Point2D;
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

    }

    @Override
    public boolean dragEnd(final DragContext context)
    {
        boolean allowed = true;

        if (m_dockingAndContainmentControl != null)
        {
            allowed = m_dockingAndContainmentControl.dragEnd(context);
        }

        if (m_alignAndDistributeControl != null)
        {
            m_alignAndDistributeControl.dragEnd();
        }

        // Cancel the drag operation if docking or containment not allowed.
        if (!allowed)
        {
            context.reset();
            return false;
        }

        updateSpecialConnections();

        return true;
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

        updateSpecialConnections();


        return adjusted1 || adjusted2;

    }

    public void updateSpecialConnections()
    {
        // start with 0, as we can have center connections too
        for ( int i = 0, size0 = m_shape.getMagnets().size(); i < size0; i++ )
        {
            WiresMagnet m = m_shape.getMagnets().getMagnet(i);
            for ( int j = 0, size1 = m.getConnectionsSize(); j < size1; j++ )
            {
                WiresConnection connection = m.getConnections().get(j);

                WiresConnector connector = connection.getConnector();
                connector.updateForSpecialConnections();
            }
        }
    }

    @Override
    public void onNodeMouseDown()
    {
        if (m_dockingAndContainmentControl != null)
        {
            m_dockingAndContainmentControl.onNodeMouseDown();
        }
    }

    @Override
    public void onNodeMouseUp()
    {
        if (m_dockingAndContainmentControl != null)
        {
            m_dockingAndContainmentControl.onNodeMouseUp();
        }
    }

}
