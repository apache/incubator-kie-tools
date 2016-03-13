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

import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;

/**
 * This class handles parent and child docking snap. For each potential parent it generates a picker image. This image consts of three layers
 * That must be added in the correct order 1) The body. 2) The hotspot border (wider than normal border) 3) The border.
 * The 2) hotspot is used to detect if a snap should take place and the 3) is used to check if any later (chained composite parent) adjustments
 * would move the adjustment outside of valid snap to the border. a PickerPart is used to allow index lookup for all three different colours, to
 * allow the x/y to asociated with the correct parts 1), 2) or 3)
 *
 * Drag is a simulated event, that starts on a mouse down. If it detects a drag it cancels the mouseup event. However small mouse movements can
 * result in no drag event and still a mouse up. For this reason the code uses both drag end and mouse up events, to catch both situations.
 *
 * When docked the shape.dockedTo is set, it is null when not docked.
 *
 * There is different mouse behaviour, depending on whether the child shape starts docked or not. When it starts undocked the hotspot detection
 * is based on the mouse x/y, when it starts docked it's based on the shape center. Once undocked it switches back to the mouse x/y.
 */
public class DockingAndContainmentHandler implements NodeMouseDownHandler, NodeMouseUpHandler, NodeDragEndHandler, DragConstraintEnforcer {

    public static final int DOCKING_BORDER_WIDTH = 20;

    private WiresShape           m_shape;

    private WiresContainer       m_parent;

    private WiresLayer           m_layer;

    private WiresManager         m_wiresManager;

    private String               m_priorFill;

    private double               m_priorAlpha;

    private PickerPart           m_parentPart;

    private MultiPath            m_path;

    private double               m_mouseStartX;

    private double               m_mouseStartY;

    private double               m_shapeStartCenterX;

    private double               m_shapeStartCenterY;

    private double               m_shapeStartX;

    private double               m_shapeStartY;

    private boolean              m_startDocked;

    private ColorMapBackedPicker m_picker;

    public DockingAndContainmentHandler(WiresShape shape, WiresManager wiresManager)
    {
        m_shape = shape;
        m_wiresManager = wiresManager;
        m_layer = m_wiresManager.getLayer();
    }

    public ColorMapBackedPicker getPicker() {
        return m_picker;
    }

    @Override
    public void startDrag(DragContext dragContext)
    {
        m_picker = new ColorMapBackedPicker(m_layer.getChildShapes(), m_layer.getLayer().getScratchPad(), m_shape, true, DOCKING_BORDER_WIDTH);

        Point2D absShapeLoc =  m_shape.getPath().getAbsoluteLocation();
        BoundingBox box = m_shape.getPath().getBoundingBox();
        m_shapeStartX = absShapeLoc.getX();
        m_shapeStartY = absShapeLoc.getY();

        m_shapeStartCenterX = m_shapeStartX + (box.getWidth()/2);
        m_shapeStartCenterY = m_shapeStartY + (box.getHeight()/2);

        m_mouseStartX = dragContext.getDragStartX();
        m_mouseStartY = dragContext.getDragStartY();

        m_parent = m_shape.getParent();
        if (m_parent != null && m_parent instanceof WiresShape)
        {

            if ( m_shape.getDockedTo() == null )
            {
                highlightBody((WiresShape) m_parent);
                m_parentPart = new PickerPart((WiresShape) m_parent, PickerPart.ShapePart.BODY );
            }
            else
            {
                highlightBorder((WiresShape) m_parent);
                m_parentPart = m_picker.findShapeAt((int)m_shapeStartCenterX, (int)m_shapeStartCenterY);
                m_startDocked = true;
            }
            m_layer.getLayer().batch();
            m_layer.getLayer().getOverLayer().batch();

        }
    }

    @Override
    public boolean adjust(final Point2D dxy)
    {
        int x = 0;
        int y = 0;
        if ( m_startDocked )
        {
            x = (int) m_shapeStartCenterX;
            y = (int) m_shapeStartCenterY;
        }
        else
        {
            x = (int) m_mouseStartX;
            y = (int) m_mouseStartY;
        }

        WiresContainer parent = null;
        x = (int) (x + dxy.getX());
        y = (int) (y + dxy.getY());
        PickerPart parentPart = m_picker.findShapeAt(x,y);

        if (parentPart != null)
        {
            parent = parentPart.getShape();
        }

        if (parent != m_parent || parentPart != m_parentPart)
        {
            boolean batch = false;

            if (m_parent != null && m_parent instanceof WiresShape)
            {
                if ( m_parentPart != null && m_parentPart.getShapePart() == PickerPart.ShapePart.BODY ) {
                    restoreBody();
                } else if  (m_path != null){
                    m_path.removeFromParent();
                    m_path = null;
                    m_shape.setDockedTo(null);
                    m_startDocked = false;
                }
                batch = true;
            }

            if (parent != null && parent instanceof WiresShape)
            {
                if ( parentPart.getShapePart() == PickerPart.ShapePart.BODY )
                {
                    if ( parent.getContainmentAcceptor().containmentAllowed(parent, m_shape))
                    {
                        highlightBody((WiresShape) parent);
                    }
                }
                else if ( parent.getDockingAcceptor().dockingAllowed(parent, m_shape) )
                {
                    highlightBorder((WiresShape) parent);
                }
                else
                {
                    // There is no valid parentPart, so null it.
                    parentPart = null;
                }
                batch = true;
            }

            if (batch)
            {
                m_layer.getLayer().batch();
                m_layer.getLayer().getOverLayer().batch();
            }
        }
        m_parent = parent;
        m_parentPart = parentPart;

        if (m_path != null)
        {
            Point2D absLoc = ((WiresShape) m_parent).getGroup().getAbsoluteLocation(); // convert to local xy of the path
            Point2D intersection = Geometry.findIntersection((int) (x - absLoc.getX()), (int) (y - absLoc.getY()), ((WiresShape) m_parent).getPath());
            if ( intersection != null )
            {
                BoundingBox box = m_shape.getPath().getBoundingBox();


                double newX = absLoc.getX() + intersection.getX() - (box.getWidth()/2);
                double newY = absLoc.getY() + intersection.getY() - (box.getHeight()/2);

                dxy.setX(newX - m_shapeStartX).setY(newY - m_shapeStartY);
                return true;
            }
        }

        return false;
    }

    private void restoreBody() {
        ((WiresShape) m_parent).getPath().setFillColor(m_priorFill);
        ((WiresShape) m_parent).getPath().setFillAlpha(m_priorAlpha);
    }

    private void highlightBorder(WiresShape parent) {
        MultiPath path = parent.getPath();
        m_path = path.copy();
        m_path.setStrokeWidth(DOCKING_BORDER_WIDTH);
        Point2D absLoc = path.getAbsoluteLocation();
        m_path.setX(absLoc.getX());
        m_path.setY(absLoc.getY());
        m_path.setStrokeColor("#CC1100");
        m_path.setStrokeAlpha(0.8);
        m_layer.getLayer().getOverLayer().add(m_path);
    }

    private void highlightBody(WiresShape parent)
    {
        m_priorFill = parent.getPath().getFillColor();
        m_priorAlpha = parent.getPath().getFillAlpha();
        parent.getPath().setFillColor("#CCCCCC");
        parent.getPath().setFillAlpha(0.8);
    }

    @Override
    public void onNodeDragEnd(NodeDragEndEvent event)
    {
        addShapeToParent();
    }

    @Override
    public void onNodeMouseDown(NodeMouseDownEvent event)
    {
        m_parent = m_shape.getParent();
    }

    @Override
    public void onNodeMouseUp(NodeMouseUpEvent event)
    {
        if (m_parent != m_shape.getParent())
        {
            addShapeToParent();
        }
    }

    private void addShapeToParent()
    {
        Point2D absLoc = m_shape.getGroup().getAbsoluteLocation();

        if (m_parent == null)
        {
            m_parent = m_layer;
        }

        if ( m_path != null ) {
            m_path.removeFromParent();
            m_layer.getLayer().getOverLayer().batch();
        }

        if ( m_parentPart == null || m_parentPart.getShapePart() == PickerPart.ShapePart.BODY ) {
            if (m_parent.getContainmentAcceptor().acceptContainment(m_parent, m_shape)) {
                if (m_parent instanceof WiresShape) {
                    restoreBody();
                }

                m_shape.removeFromParent();

                if (m_parent == m_layer) {
                    m_shape.getGroup().setLocation(absLoc);
                } else {
                    Point2D trgAbsOffset = m_parent.getContainer().getAbsoluteLocation();

                    m_shape.getGroup().setX(absLoc.getX() - trgAbsOffset.getX()).setY(absLoc.getY() - trgAbsOffset.getY());
                }
                m_parent.add(m_shape);

                m_shape.setDockedTo(null);

                m_layer.getLayer().batch();
            }
        }
        else if ( m_parentPart != null &&  m_parentPart.getShapePart() != PickerPart.ShapePart.BODY &&
                  m_parent.getDockingAcceptor().acceptDocking(m_parent, m_shape) )
        {
            m_shape.removeFromParent();

            Point2D trgAbsOffset = m_parent.getContainer().getAbsoluteLocation();
            m_shape.getGroup().setX(absLoc.getX() - trgAbsOffset.getX()).setY(absLoc.getY() - trgAbsOffset.getY());
            m_parent.add(m_shape);

            m_shape.setDockedTo(m_parent);

            m_layer.getLayer().batch();
        }
        else
        {
            throw new IllegalStateException("Defensive Programming: Should not happen");
        }

        m_parent = null;
        m_parentPart = null;
        m_priorFill = null;
        m_picker = null;
    }
}
