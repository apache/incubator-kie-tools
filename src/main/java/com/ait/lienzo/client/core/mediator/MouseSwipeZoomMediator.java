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
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.event.shared.GwtEvent;

/**
 * SwipeMouseZoomMediator zooms in or out when the user drags a
 * point (in the viewport) to the left or right respectively.
 * The directions can be reversed via the property <code>rightZoomOut</code>.
 * <p>
 * The viewport is scaled about the initial selection point.
 * 
 * @see Mediators
 * 
 * @since 1.1
 */
public class MouseSwipeZoomMediator extends AbstractMediator
{
    private double  m_minScale     = 0;

    private double  m_maxScale     = Double.MAX_VALUE;

    private boolean m_rightZoomOut = true;

    private double  m_zoomFactor   = 0.001;

    private Point2D m_start        = null;

    private boolean m_dragging     = false;

    private Point2D m_zoomCenter   = new Point2D();

    public MouseSwipeZoomMediator()
    {
    }

    public MouseSwipeZoomMediator(IEventFilter... filters)
    {
        setEventFilter(EventFilter.and(filters));
    }

    /**
     * Sets the minimum scale of the viewport.
     * 
     * The default value is 0 (unlimited.)
     * 
     * @return double
     */
    public double getMinScale()
    {
        return m_minScale;
    }

    /**
     * Sets the minimum scale of the viewport.
     * 
     * The default value is 0 (unlimited.)
     * 
     * @param minScale
     * @return MouseSwipeZoomMediator
     */
    public MouseSwipeZoomMediator setMinScale(double minScale)
    {
        m_minScale = minScale;

        return this;
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
     * @return MouseSwipeZoomMediator
     */
    public MouseSwipeZoomMediator setMaxScale(double maxScale)
    {
        m_maxScale = maxScale;

        return this;
    }

    /**
     * Returns whether dragging the mouse to the right will zoom out (true) or zoom in (false.)
     * Dragging the opposite direction will do the opposite.
     * 
     * The default value is true.
     * 
     * @return boolean
     */
    public boolean isRightZoomOut()
    {
        return m_rightZoomOut;
    }

    /**
     * Sets whether dragging the mouse to the right will zoom out (true) or zoom in (in.)
     * Dragging the opposite direction will do the opposite.
     * 
     * The default value is true.
     * 
     * @param rightZoomOut
     * @return MouseSwipeZoomMediator
     */
    public MouseSwipeZoomMediator setRightZoomOut(boolean rightZoomOut)
    {
        m_rightZoomOut = rightZoomOut;

        return this;
    }

    /**
     * Returns the zoom factor being used for zoom in and out operations.
     * 
     * If the zoom factor is not set, it defaults to 0.001
     * 
     * @return double
     */
    public double getZoomFactor()
    {
        return m_zoomFactor;
    }

    /**
     * Sets the zoom factor that will be used during the zoom in/out operation.
     * The zoom factor defaults to 0.001
     *  
     * @param zoomFactor
     * @return this MouseSwipeZoomMediator
     */
    public MouseSwipeZoomMediator setZoomFactor(double zoomFactor)
    {
        m_zoomFactor = zoomFactor;

        return this;
    }

    @Override
    public void cancel()
    {
        m_dragging = false;
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

        Transform transform = getTransform();

        transform.getInverse().transform(m_start, m_zoomCenter);
    }

    protected void onMouseUp(NodeMouseUpEvent event)
    {
        m_dragging = false;
    }

    protected void onMouseMove(NodeMouseMoveEvent event)
    {
        double dx = event.getX() - m_start.getX();

        double scaleDelta = (1.0 + (m_zoomFactor * dx * (m_rightZoomOut ? 1 : -1)));

        Transform transform = getTransform();

        if (transform == null)
        {
            setTransform(transform = new Transform());
        }
        // ASSUMPTION: scaleX == scaleY
        double currentScale = transform.getScaleX();

        double newScale = currentScale * scaleDelta;

        if (newScale < m_minScale)
        {
            scaleDelta = m_minScale / currentScale;
        }
        if ((m_maxScale > 0) && (newScale > m_maxScale))
        {
            scaleDelta = m_maxScale / currentScale;
        }
        transform = transform.copy();

        transform.scaleAboutPoint(scaleDelta, m_zoomCenter.getX(), m_zoomCenter.getY());

        setTransform(transform);

        redraw();
    }

    protected Transform getTransform()
    {
        return m_viewport.getTransform();
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
