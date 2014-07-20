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

import com.ait.lienzo.client.core.shape.Attributes;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.DragConstraint;

/**
 * DefaultDragConstraintEnforcer enforces the default drag constraints
 * as specified with a Node's <code>dragConstraint</code> and 
 * <code>dragBounds</code> attributes.
 * Either one or both attributes can be specified for a Node.
 * 
 * If you need custom drag constraint behavior, create your own {@link DragConstraintEnforcer}
 * 
 * @see DragBounds
 * @see DragConstraintEnforcer
 */
public class DefaultDragConstraintEnforcer implements DragConstraintEnforcer
{
    private DragBounds     m_bounds;

    private DragConstraint m_constraint;

    private double         m_dx1, m_dx2, m_dy1, m_dy2;

    @Override
    public void startDrag(DragContext dragContext)
    {
        Attributes attr = dragContext.getNode().asNode().getAttributes();

        m_constraint = attr.getDragConstraint();

        m_bounds = attr.getDragBounds();

        if (m_bounds != null)
        {
            double x = attr.getX();

            double y = attr.getY();

            if (m_bounds.isX1()) m_dx1 = m_bounds.getX1() - x;

            if (m_bounds.isX2()) m_dx2 = m_bounds.getX2() - x;

            if (m_bounds.isY1()) m_dy1 = m_bounds.getY1() - y;

            if (m_bounds.isY2()) m_dy2 = m_bounds.getY2() - y;
        }
    }

    @Override
    public void adjust(Point2D dxy)
    {
        double dx = dxy.getX();

        double dy = dxy.getY();

        switch (m_constraint)
        {
            default:
            case NONE:
            {
                break;
            }
            case HORIZONTAL:
            {
                dy = 0;
                break;
            }
            case VERTICAL:
            {
                dx = 0;
                break;
            }
        }
        if (m_bounds != null)
        {
            if (m_bounds.isX1() && dx < m_dx1) dx = m_dx1;
            else if (m_bounds.isX2() && dx > m_dx2) dx = m_dx2;

            if (m_bounds.isY1() && dy < m_dy1) dy = m_dy1;
            else if (m_bounds.isY2() && dy > m_dy2) dy = m_dy2;
        }
        dxy.setX(dx);

        dxy.setY(dy);
    }
}