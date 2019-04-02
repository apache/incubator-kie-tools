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

package com.ait.lienzo.client.core.mediator;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.event.shared.GwtEvent;

/**
 * MousePanMediator provides pan behavior similar to dragging the mouse in Google Maps.
 * Only the zoomable Layers are affected.
 * 
 * @see Mediators
 * 
 * @since 1.1
 */
public class MousePanMediator extends AbstractMediator
{
    private Point2D   m_last             = new Point2D();

    private boolean   m_dragging         = false;

    private boolean   m_xconstrained     = false;

    private boolean   m_yconstrained     = false;

    private Transform m_inverseTransform = null;

    public MousePanMediator()
    {
    }

    public MousePanMediator(final IEventFilter... filters)
    {
        setEventFilter(EventFilter.and(filters));
    }

    public MousePanMediator setXConstrained(final boolean m_constrained)
    {
        this.m_xconstrained = m_constrained;
        return this;
    }

    public boolean isXConstrained()
    {
        return m_xconstrained;
    }

    public MousePanMediator setYConstrained(final boolean m_constrained)
    {
        this.m_yconstrained = m_constrained;
        return this;
    }

    public boolean isYConstrained()
    {
        return m_yconstrained;
    }

    @Override
    public void cancel()
    {
        m_dragging = false;
    }

    @Override
    public boolean handleEvent(final GwtEvent<?> event)
    {
        if (event.getAssociatedType() == NodeMouseMoveEvent.getType())
        {
            if (m_dragging)
            {
                onMouseMove((NodeMouseMoveEvent) event);

                return true;
            }
            return false;
        }
        else if (event.getAssociatedType() == NodeMouseDownEvent.getType())
        {
            final IEventFilter filter = getEventFilter();

            if ((null == filter) || (false == filter.isEnabled()) || (filter.test(event)))
            {
                onMouseDown((NodeMouseDownEvent) event);

                return true;
            }
            return false;
        }
        else if (event.getAssociatedType() == NodeMouseUpEvent.getType())
        {
            if (m_dragging)
            {
                onMouseUp((NodeMouseUpEvent) event);

                return true;
            }
        }
        return false;
    }

    protected void onMouseDown(final NodeMouseDownEvent event)
    {
        m_last = new Point2D(event.getX(), event.getY());

        m_dragging = true;

        Transform transform = getTransform();

        if (transform == null)
        {
            setTransform(transform = new Transform());
        }
        m_inverseTransform = transform.getInverse();

        m_inverseTransform.transform(m_last, m_last);
    }

    protected void onMouseMove(final NodeMouseMoveEvent event)
    {
        final Transform transform = getTransform();
        final Point2D curr = new Point2D(event.getX(), event.getY());

        m_inverseTransform.transform(curr, curr);

        final double x = m_xconstrained && transform.getTranslateX() > 0 ? getTransform().getTranslateX() * -1 : curr.getX() - m_last.getX();
        final double y = m_yconstrained && transform.getTranslateY() > 0 ? getTransform().getTranslateY() * -1 : curr.getY() - m_last.getY();

        setTransform(getTransform().copy().translate(x, y));

        m_last = curr;

        if (isBatchDraw())
        {
            getViewport().getScene().batch();
        }
        else
        {
            getViewport().getScene().draw();
        }
    }

    protected void onMouseUp(final NodeMouseUpEvent event)
    {
        cancel();
    }
}
