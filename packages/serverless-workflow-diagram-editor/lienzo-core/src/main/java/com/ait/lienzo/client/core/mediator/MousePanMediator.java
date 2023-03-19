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
import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;
import com.ait.lienzo.tools.client.event.INodeEvent.Type;
import elemental2.dom.UIEvent;

/**
 * MousePanMediator provides pan behavior similar to dragging the mouse in Google Maps.
 * Only the zoomable Layers are affected.
 *
 * @see Mediators
 * @since 1.1
 */
public class MousePanMediator extends AbstractMediator {

    public interface Callback {

        void onActivate();

        void onDragStart();

        void onDragEnd();

        void onDeactivate();
    }

    private Point2D m_last = new Point2D(0, 0);

    private boolean m_dragging = false;

    private boolean m_xconstrained = false;

    private boolean m_yconstrained = false;

    private Transform m_inverseTransform = null;

    private boolean m_active = false;

    private MousePanMediator.Callback m_callback = null;

    public MousePanMediator() {
    }

    public MousePanMediator(final IEventFilter... filters) {
        setEventFilter(EventFilter.and(filters));
    }

    public MousePanMediator setXConstrained(final boolean m_constrained) {
        this.m_xconstrained = m_constrained;
        return this;
    }

    public boolean isXConstrained() {
        return m_xconstrained;
    }

    public MousePanMediator setYConstrained(final boolean m_constrained) {
        this.m_yconstrained = m_constrained;
        return this;
    }

    public boolean isYConstrained() {
        return m_yconstrained;
    }

    public MousePanMediator setCallback(final MousePanMediator.Callback m_callback) {
        this.m_callback = m_callback;
        return this;
    }

    public boolean isActive() {
        return m_active;
    }

    public boolean isDragging() {
        return m_dragging;
    }

    @Override
    public void cancel() {
        stop_dragging();
    }

    @Override
    public <H extends EventHandler> boolean handleEvent(Type<H> type, final UIEvent event, int x, int y) {
        final IEventFilter filter = getEventFilter();
        if ((null == filter) || (!filter.isEnabled()) || (filter.test(event))) {
            attempt_activate();
        } else {
            attempt_deactivate();
        }

        if (type == NodeMouseMoveEvent.getType()) {
            if (m_dragging) {
                onMouseMove(x, y);

                return true;
            }
            return false;
        } else if (type == NodeMouseDownEvent.getType()) {
            if (isActive()) {
                onMouseDown(x, y);

                return true;
            }
            return false;
        } else if (type == NodeMouseUpEvent.getType()) {
            if (m_dragging) {
                onMouseUp();

                return true;
            }
        }
        return false;
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

    private void start_dragging() {
        m_dragging = true;
        if (m_callback != null) {
            m_callback.onDragStart();
        }

    }

    private void stop_dragging() {
        m_dragging = false;
        if (m_callback != null) {
            m_callback.onDragEnd();
        }
    }

    protected void onMouseDown(int x, int y) {
        m_last = new Point2D(x, y);

        start_dragging();

        Transform transform = getTransform();

        if (transform == null) {
            setTransform(transform = new Transform());
        }
        m_inverseTransform = transform.getInverse();

        m_inverseTransform.transform(m_last, m_last);
    }

    protected void onMouseMove(int x, int y) {
        final Transform transform = getTransform();
        final Point2D curr = new Point2D(x, y);

        m_inverseTransform.transform(curr, curr);

        final double translatedX = m_xconstrained && transform.getTranslateX() > 0 ? getTransform().getTranslateX() * -1 : curr.getX() - m_last.getX();
        final double translatedY = m_yconstrained && transform.getTranslateY() > 0 ? getTransform().getTranslateY() * -1 : curr.getY() - m_last.getY();

        setTransform(getTransform().copy().translate(translatedX, translatedY));

        m_last = curr;

        if (isBatchDraw()) {
            getViewport().getScene().batch();
        } else {
            getViewport().getScene().draw();
        }
    }

    protected void onMouseUp() {
        cancel();
    }
}
