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
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;
import com.ait.lienzo.tools.client.event.INodeEvent.Type;
import elemental2.dom.UIEvent;
import elemental2.dom.WheelEvent;

/**
 * MouseWheelZoomMediator zooms in or out when the mouse wheel is moved.
 *
 * @see Mediators
 * @since 1.1
 */
public class MouseWheelZoomMediator extends AbstractMediator {

    public interface Callback {

        void onActivate();

        void onZoomIn();

        void onZoomOut();

        void onDeactivate();
    }

    private double m_minScale = 0;

    private double m_maxScale = Double.MAX_VALUE;

    private boolean m_downZoomOut = true;

    private double m_zoomFactor = 0.1;

    private boolean m_scaleAboutPoint = true;

    private boolean m_xconstrained = false;

    private boolean m_yconstrained = false;

    private boolean m_active = false;

    private Callback m_callback = null;

    public MouseWheelZoomMediator() {
    }

    public MouseWheelZoomMediator(final IEventFilter... filters) {
        setEventFilter(EventFilter.and(filters));
    }

    public MouseWheelZoomMediator setXConstrained(final boolean m_constrained) {
        this.m_xconstrained = m_constrained;
        return this;
    }

    public boolean isXConstrained() {
        return m_xconstrained;
    }

    public MouseWheelZoomMediator setYConstrained(final boolean m_constrained) {
        this.m_yconstrained = m_constrained;
        return this;
    }

    public boolean isYConstrained() {
        return m_yconstrained;
    }

    public MouseWheelZoomMediator setCallback(final Callback m_callback) {
        this.m_callback = m_callback;
        return this;
    }

    @Override
    public <H extends EventHandler> boolean handleEvent(Type<H> type, final UIEvent event, int x, int y) {
        final IEventFilter filter = getEventFilter();

        if ((null == filter) || (!filter.isEnabled()) || (filter.test(event))) {
            attempt_activate();
        } else {
            attempt_deactivate();
        }

        if (type == NodeMouseWheelEvent.getType() && isActive()) {
            onMouseWheel((WheelEvent) event, x, y);

            return true;
        }
        return false;
    }

    @Override
    public void cancel() {
        attempt_deactivate();
    }

    public MouseWheelZoomMediator setScaleAboutPoint(final boolean s) {
        m_scaleAboutPoint = s;

        return this;
    }

    public boolean isScaleAboutPoint() {
        return m_scaleAboutPoint;
    }

    /**
     * Sets the minimum scaleWithXY of the viewport.
     * <p>
     * The default value is 0 (unlimited.)
     *
     * @return double
     */
    public double getMinScale() {
        return m_minScale;
    }

    /**
     * Sets the minimum scaleWithXY of the viewport.
     * <p>
     * The default value is 0 (unlimited.)
     *
     * @param minScale
     * @return MouseWheelZoomMediator
     */
    public MouseWheelZoomMediator setMinScale(final double minScale) {
        m_minScale = minScale;

        return this;
    }

    /**
     * Sets the maximum scaleWithXY of the viewport.
     * <p>
     * The default value is Double.MAX_VALUE (unlimited.)
     *
     * @return double
     */
    public double getMaxScale() {
        return m_maxScale;
    }

    /**
     * Sets the maximum scaleWithXY of the viewport.
     * <p>
     * The default value is Double.MAX_VALUE (unlimited.)
     *
     * @param maxScale double
     * @return MouseWheelZoomMediator
     */
    public MouseWheelZoomMediator setMaxScale(final double maxScale) {
        m_maxScale = maxScale;

        return this;
    }

    /**
     * Sets whether rolling the mouse wheel down will zoom out.
     * <p>
     * The default value is true.
     *
     * @param downZoomOut
     */
    public MouseWheelZoomMediator setDownZoomOut(final boolean downZoomOut) {
        m_downZoomOut = downZoomOut;

        return this;
    }

    /**
     * Returns whether rolling the mouse wheel down will zoom out.
     * <p>
     * The default value is true.
     *
     * @return boolean
     */
    public boolean isDownZoomOut() {
        return m_downZoomOut;
    }

    public boolean isActive() {
        return m_active;
    }

    /**
     * Returns the zoom factor by which we zoom in or out when the mouse wheel is moved.
     * <p>
     * The default value is 0.1 (10%)
     *
     * @return double
     */
    public double getZoomFactor() {
        return m_zoomFactor;
    }

    /**
     * Sets the zoom factor by which we zoom in or out when the mouse wheel is moved.
     * <p>
     * The default value is 0.1 (10%)
     *
     * @param zoomFactor double
     * @return MouseSwipeZoomMediator
     */
    public MouseWheelZoomMediator setZoomFactor(final double zoomFactor) {
        m_zoomFactor = zoomFactor;

        return this;
    }

    public void attempt_activate() {
        if (!m_active) {
            m_active = true;
            if (m_callback != null) {
                m_callback.onActivate();
            }
        }
    }

    public void attempt_deactivate() {
        if (m_active) {
            m_active = false;
            if (m_callback != null) {
                m_callback.onDeactivate();
            }
        }
    }

    protected void onMouseWheel(WheelEvent event, int x, int y) {
        Transform transform = getTransform();

        if (transform == null) {
            setTransform(transform = new Transform());
        }
        double scaleDelta;

        if (event.deltaY < 0 == m_downZoomOut) // down
        {
            // zoom out
            scaleDelta = 1 / (1 + m_zoomFactor);
            if (m_callback != null) {
                m_callback.onZoomOut();
            }
        } else {
            // zoom in
            scaleDelta = 1 + m_zoomFactor;
            if (m_callback != null) {
                m_callback.onZoomIn();
            }
        }
        // ASSUMPTION: scaleX == scaleY

        double currentScale = transform.getScaleX();

        double newScale = currentScale * scaleDelta;

        if (newScale < m_minScale) {
            scaleDelta = m_minScale / currentScale;
        }
        if ((m_maxScale > 0) && (newScale > m_maxScale)) {
            scaleDelta = m_maxScale / currentScale;
        }

        if (m_scaleAboutPoint) {

            Point2D p = new Point2D(x, y);

            transform.getInverse().transform(p, p);

            transform = transform.copy();

            transform.scaleAboutPoint(scaleDelta, p.getX(), p.getY());

            final double translatedX = m_xconstrained && transform.getTranslateX() > 0 ? transform.getTranslateX() * -1 / transform.getScaleX(): 0.0;

            final double translatedY = m_yconstrained && transform.getTranslateY() > 0 ? transform.getTranslateY() * -1 / transform.getScaleY(): 0.0;

            transform.translate(translatedX, translatedY);

            Viewport v = getViewport();

            BoundingBox boundingBox = getViewport().getScene().getBoundingBox();

        } else {
            transform.scale(scaleDelta);
        }

        setTransform(transform);

        if (isBatchDraw()) {
            getViewport().getScene().batch();
        } else {
            getViewport().getScene().draw();
        }
    }
}
