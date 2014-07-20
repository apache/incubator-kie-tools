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

package com.ait.lienzo.client.core.mediator;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.Color;
import com.google.gwt.event.shared.GwtEvent;

/**
 * MouseBoxZoomMediator zooms in when the user drags a rectangular area.
 * <p>
 * The visual style of the drag box can be modified by changing the 
 * attributes of the Rectangle primitive.
 * The default drag box uses a black outline of 1 pixel wide and 
 * an opacity (alpha value) of 0.5.
 * <p>
 * The dimensions of the zoom box will be adjusted to match the aspect ratio
 * of the viewport, thus maintaining the same scale in the X and Y axes.
 * 
 * @see Mediators
 * 
 * @since 1.1
 */
public class MouseBoxZoomMediator extends AbstractMediator
{
    private double    m_maxScale         = Double.MAX_VALUE;

    private Point2D   m_start            = null;

    private Point2D   m_end              = new Point2D();

    private Transform m_inverseTransform = null;

    private boolean   m_dragging         = false;

    private Layer     m_dragLayer        = null;

    private Rectangle m_rectangle        = null;

    private boolean   m_addedRectangle   = false;

    public MouseBoxZoomMediator()
    {
        setDefaultRectangle();
    }

    public MouseBoxZoomMediator(IEventFilter... filters)
    {
        this();

        setEventFilter(EventFilter.and(filters));
    }

    /**
     * Sets the maximum scale of the viewport.
     * 
     * The default value is Double.MAX_VALUE (unlimited.)
     * 
     * @return double
     */
    public double getMaxScale()
    {
        return m_maxScale;
    }

    /**
     * Sets the maximum scale of the viewport.
     * 
     * The default value is Double.MAX_VALUE (unlimited.)
     * 
     * @param maxScale double
     * @return MouseBoxZoomMediator
     */
    public MouseBoxZoomMediator setMaxScale(double maxScale)
    {
        m_maxScale = maxScale;

        return this;
    }

    /**
     * Returns the {@link Rectangle} that is used to draw the zoom box.
     * 
     * @return {@link Rectangle}
     */
    public Rectangle getRectangle()
    {
        return m_rectangle;
    }

    /**
     * Sets the {@link Rectangle} that is used to draw the zoom box.
     * @param r {@link Rectangle}
     * @return MouseBoxZoomMediator
     */
    public MouseBoxZoomMediator setRectangle(Rectangle r)
    {
        m_rectangle = r;

        return this;
    }

    @Override
    public void cancel()
    {
        m_dragging = false;

        if (m_addedRectangle)
        {
            m_dragLayer.remove(m_rectangle);

            m_addedRectangle = false;

            m_dragLayer.draw();
        }
    }

    @Override
    public boolean handleEvent(GwtEvent<?> event)
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
            if (m_eventFilter.matches(event))
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

    protected void onMouseDown(NodeMouseDownEvent event)
    {
        m_start = new Point2D(event.getX(), event.getY());

        m_dragging = true;

        m_dragLayer = m_viewport.getDraglayer();

        Transform transform = m_dragLayer.isTransformable() ? m_viewport.getTransform() : m_dragLayer.getTransform();

        if (transform == null)
        {
            transform = new Transform();
        }
        m_rectangle.setStrokeWidth(1 / transform.getScaleX());

        m_inverseTransform = transform.getInverse();

        m_inverseTransform.transform(m_start, m_start);

        m_addedRectangle = false;
    }

    protected void onMouseMove(NodeMouseMoveEvent event)
    {
        m_end.setX(event.getX()).setY(event.getY());

        m_inverseTransform.transform(m_end, m_end);

        double x = m_start.getX();

        double y = m_start.getY();

        double dx = m_end.getX() - x;

        double dy = m_end.getY() - y;

        if (dx < 0)
        {
            x += dx;

            dx = -dx;
        }
        if (dy < 0)
        {
            y += dy;

            dy = -dy;
        }
        m_rectangle.setX(x).setY(y).setWidth(dx).setHeight(dy);

        if (!m_addedRectangle)
        {
            m_addedRectangle = true;

            m_dragLayer.add(m_rectangle);
        }
        m_dragLayer.draw();
    }

    protected void onMouseUp(NodeMouseUpEvent event)
    {
        cancel();

        m_end.setX(event.getX()).setY(event.getY());

        m_inverseTransform.transform(m_end, m_end);

        double x = m_start.getX();

        double y = m_start.getY();

        double dx = m_end.getX() - x;

        double dy = m_end.getY() - y;

        if (dx < 0)
        {
            x += dx;

            dx = -dx;
        }
        if (dy < 0)
        {
            y += dy;

            dy = -dy;
        }
        // prevent zooming in too far
        double scaleX = m_viewport.getWidth() / dx;

        double scaleY = m_viewport.getHeight() / dy;

        double scale = (scaleX > scaleY) ? scaleY : scaleX;

        if (scale > m_maxScale)
        {
            return;// zoomed in too far
        }
        Transform transform = createTransform(x, y, dx, dy);

        setTransform(transform);

        redraw();
    }

    protected void setDefaultRectangle()
    {
        setRectangle(new Rectangle(1, 1).setStrokeColor(new Color(0, 0, 0, 0.5)));
    }

    protected Transform createTransform(double x, double y, double dx, double dy)
    {
        return Transform.createViewportTransform(x, y, dx, dy, m_viewport.getWidth(), m_viewport.getHeight());
    }

    protected void setTransform(Transform transform)
    {
        m_viewport.setTransform(transform);
    }

    protected void redraw()
    {
        m_viewport.getScene().draw();
    }
}
