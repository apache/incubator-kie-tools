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

import com.ait.lienzo.client.core.event.NodeMouseWheelEvent;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.event.shared.GwtEvent;

/**
 * MouseWheelZoomMediator zooms in or out when the mouse wheel is moved.
 * 
 * @see Mediators
 * 
 * @since 1.1
 */
public class MouseWheelZoomMediator extends AbstractMediator
{
    private double  m_minScale          = 0;

    private double  m_maxScale          = Double.MAX_VALUE;

    private boolean m_downZoomOut       = true;

    private double  m_zoomFactor        = 0.1;

    private boolean m_scaleAboutPoint   = true;

    public MouseWheelZoomMediator()
    {
        LienzoPanel.enableWindowMouseWheelScroll(true);
    }

    public MouseWheelZoomMediator(final IEventFilter... filters)
    {
        setEventFilter(EventFilter.and(filters));
    }

    @Override
    public boolean handleEvent(final GwtEvent<?> event)
    {
        if (event.getAssociatedType() == NodeMouseWheelEvent.getType())
        {
            final IEventFilter filter = getEventFilter();

            if ((null == filter) || (false == filter.isEnabled()) || (filter.test(event)))
            {
                onMouseWheel((NodeMouseWheelEvent) event);

                return true;
            }
        }
        return false;
    }

    @Override
    public void cancel()
    {
        // nothing to do
    }

    public MouseWheelZoomMediator setScaleAboutPoint(final boolean s)
    {
        m_scaleAboutPoint = s;

        return this;
    }

    public boolean isScaleAboutPoint()
    {
        return m_scaleAboutPoint;
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
     * @return MouseWheelZoomMediator
     */
    public MouseWheelZoomMediator setMinScale(final double minScale)
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
     * @return MouseWheelZoomMediator
     */
    public MouseWheelZoomMediator setMaxScale(final double maxScale)
    {
        m_maxScale = maxScale;

        return this;
    }

    /**
     * Returns whether rolling the mouse wheel down will zoom out.
     * 
     * The default value is true.
     * 
     * @return boolean
     */
    public boolean isDownZoomOut()
    {
        return m_downZoomOut;
    }

    /**
     * Sets whether rolling the mouse wheel down will zoom out.
     * 
     * The default value is true.
     * 
     * @param downZoomOut
     */
    public MouseWheelZoomMediator setDownZoomOut(final boolean downZoomOut)
    {
        m_downZoomOut = downZoomOut;

        return this;
    }

    /**
     * Returns the zoom factor by which we zoom in or out when the mouse wheel is moved.
     * 
     * The default value is 0.1 (10%)
     *   
     * @return double
     */
    public double getZoomFactor()
    {
        return m_zoomFactor;
    }

    /**
     * Sets the zoom factor by which we zoom in or out when the mouse wheel is moved.
     * 
     * The default value is 0.1 (10%)
     * 
     * @param zoomFactor double
     * @return MouseSwipeZoomMediator
     */
    public MouseWheelZoomMediator setZoomFactor(final double zoomFactor)
    {
        m_zoomFactor = zoomFactor;

        return this;
    }

    protected void onMouseWheel(final NodeMouseWheelEvent event)
    {
        Transform transform = getTransform();

        if (transform == null)
        {
            setTransform(transform = new Transform());
        }
        double scaleDelta;

        if (event.isSouth() == m_downZoomOut) // down
        {
            // zoom out
            scaleDelta = 1 / (1 + m_zoomFactor);
        }
        else
        {
            // zoom in
            scaleDelta = 1 + m_zoomFactor;
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

        if (m_scaleAboutPoint)
        {
            Point2D p = new Point2D(event.getX(), event.getY());

            transform.getInverse().transform(p, p);

            transform = transform.copy();

            transform.scaleAboutPoint(scaleDelta, p.getX(), p.getY());
        }
        else
        {
            transform.scale(scaleDelta);
        }

        setTransform(transform);

        if (isBatchDraw())
        {
            getViewport().getScene().batch();
        }
        else
        {
            getViewport().getScene().draw();
        }
    }
}
