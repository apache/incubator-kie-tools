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

import elemental2.dom.UIEvent;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.tools.client.event.INodeEvent.Type;
import java.util.function.Consumer;
import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;
/**
 * MouseBoxZoomMediator zooms in when the user drags a rectangular area.
 * <p>
 * The visual style of the drag box can be modified by changing the 
 * attributes of the Rectangle primitive.
 * The default drag box uses a black outline of 1 pixel wide and 
 * an opacity (alpha value) of 0.5.
 * <p>
 * The dimensions of the zoom box will be adjusted to match the aspect ratio
 * of the viewport, thus maintaining the same scaleWithXY in the X and Y axes.
 * 
 * @see Mediators
 * 
 * @since 1.1
 */
public class MouseBoxZoomMediator extends AbstractMediator
{
    private double    m_maxScale         = Double.MAX_VALUE;

    private Point2D   m_start            = null;

    private Point2D   m_end              = new Point2D(0, 0);

    private Transform m_inverseTransform = null;

    private boolean   m_dragging         = false;

    private Layer     m_dragLayer        = null;

    private Rectangle m_rectangle        = null;

    private boolean   m_addedRectangle   = false;

    private Consumer<Transform> m_onTransform     = transform -> {};

    public MouseBoxZoomMediator()
    {
        setDefaultRectangle();
    }

    public MouseBoxZoomMediator(final IEventFilter... filters)
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
    public MouseBoxZoomMediator setMaxScale(final double maxScale)
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
    public MouseBoxZoomMediator setRectangle(final Rectangle r)
    {
        m_rectangle = r;

        return this;
    }

    public MouseBoxZoomMediator setOnTransform(final Consumer<Transform> m_onTransform)
    {
        this.m_onTransform = m_onTransform;

        return this;
    }

    public Consumer<Transform> getOnTransform()
    {
        return m_onTransform;
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
    public <H extends EventHandler> boolean handleEvent(Type<H> type, final UIEvent event, int x, int y)
    {
        if (type == NodeMouseMoveEvent.getType())
        {
            if (m_dragging)
            {
                onMouseMove(x, y);
                return true;
            }
            return false;
        }
        else if (type == NodeMouseDownEvent.getType())
        {
            final IEventFilter filter = getEventFilter();

            if ((null == filter) || (!filter.isEnabled()) || (filter.test(event)))
            {
                onMouseDown(x, y);
                return true;
            }
            return false;
        }
        else if (type == NodeMouseUpEvent.getType())
        {
            if (m_dragging)
            {
                onMouseUp(x, y);
                return true;
            }
        }
        return false;
    }

    protected void onMouseDown(int x, int y)
    {
        m_start = new Point2D(x, y);

        m_dragging = true;

        m_dragLayer = getViewport().getDragLayer();

        Transform transform = m_dragLayer.isTransformable() ? getViewport().getTransform() : null;

        if (transform == null)
        {
            transform = new Transform();
        }
        m_rectangle.setStrokeWidth(1 / transform.getScaleX());

        m_inverseTransform = transform.getInverse();

        m_inverseTransform.transform(m_start, m_start);

        m_addedRectangle = false;
    }

    protected void onMouseMove(int eventX, int eventY)
    {
        m_end.setX(eventX).setY(eventY);

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

    protected void onMouseUp(int eventX, int eventY)
    {
        cancel();

        m_end.setX(eventX).setY(eventY);

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
        double scaleX = getViewport().getWidth() / dx;
        double scaleY = getViewport().getHeight() / dy;
        double scale = Math.min(scaleX, scaleY);

        Transform transform = Transform.createViewportTransform(x, y, dx, dy, getViewport().getWidth(), getViewport().getHeight());

        // prevent zooming in too far
        if (null != transform && scale > m_maxScale)
        {
            double ds = m_maxScale / scale;
            transform.scaleAboutPoint(ds, m_start.getX(), m_start.getY());
        }

        transform = transform != null ? transform : new Transform();

        setTransform(transform);

        m_onTransform.accept(transform);

        if (isBatchDraw())
        {
            getViewport().getScene().batch();
        }
        else
        {
            getViewport().getScene().draw();
        }
    }

    protected void setDefaultRectangle()
    {
        setRectangle(new Rectangle(1, 1).setStrokeColor(new Color(0, 0, 0, 0.5)));
    }

}
