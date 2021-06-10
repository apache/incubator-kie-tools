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

package com.ait.lienzo.client.widget;

import com.ait.lienzo.client.core.shape.Node;
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
public final class DefaultDragConstraintEnforcer implements DragConstraintEnforcer
{
    private DragBounds     m_bounds;

    private DragConstraint m_constraint;

    private double         m_dx1;

    private double         m_dx2;

    private double         m_dy1;

    private double         m_dy2;

    @Override
    public final void startDrag(final DragContext dragContext)
    {
        final Node       node = dragContext.getNode().asNode();

        m_constraint = node.getDragConstraint();

        m_bounds = node.getDragBounds();

        if (null != m_bounds)
        {
            final double x = node.getX();

            final double y = node.getY();

            if (m_bounds.isX1())
            {
                m_dx1 = m_bounds.getX1() - x;
            }
            if (m_bounds.isX2())
            {
                m_dx2 = m_bounds.getX2() - x;
            }
            if (m_bounds.isY1())
            {
                m_dy1 = m_bounds.getY1() - y;
            }
            if (m_bounds.isY2())
            {
                m_dy2 = m_bounds.getY2() - y;
            }
        }
    }

    @Override
    public final boolean adjust(final Point2D d)
    {
        if ((DragConstraint.NONE == m_constraint) && (null == m_bounds))
        {
            return false;
        }
        boolean move = false;

        double x = d.getX();

        double y = d.getY();

        if (DragConstraint.HORIZONTAL == m_constraint)
        {
            y = 0;

            move = true;
        }
        else if (DragConstraint.VERTICAL == m_constraint)
        {
            x = 0;

            move = true;
        }
        if (null != m_bounds)
        {
            if (m_bounds.isX1() && (x < m_dx1))
            {
                x = m_dx1;

                move = true;
            }
            else if (m_bounds.isX2() && (x > m_dx2))
            {
                x = m_dx2;

                move = true;
            }
            if (m_bounds.isY1() && (y < m_dy1))
            {
                y = m_dy1;

                move = true;
            }
            else if (m_bounds.isY2() && (y > m_dy2))
            {
                y = m_dy2;

                move = true;
            }
        }
        d.setX(x);

        d.setY(y);

        return move;
    }
}