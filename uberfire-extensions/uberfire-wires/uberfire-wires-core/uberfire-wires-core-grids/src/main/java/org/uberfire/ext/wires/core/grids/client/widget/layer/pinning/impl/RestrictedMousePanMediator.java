/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseOutEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.mediator.AbstractMediator;
import com.ait.lienzo.client.core.mediator.IEventFilter;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.GwtEvent;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;

/**
 * This a fork of {@link com.ait.lienzo.client.core.mediator.MousePanMediator} however this implementation does not
 * stop the propagation of NodeMouseEvents to other listeners. Lienzo's implementation causes other NodeMouseMoveHandler,
 * NodeMouseDownHandler and NodeMouseUpHandler to miss receipt of Events. This implementation also restricts
 * transformations according to a {@link TransformMediator}.
 */
public class RestrictedMousePanMediator extends AbstractMediator {

    private GridLayer gridLayer;

    private TransformMediator transformMediator;

    private Point2D m_last = new Point2D();

    private boolean m_dragging = false;

    private Transform m_inverseTransform = null;

    public RestrictedMousePanMediator(final GridLayer gridLayer) {
        this.gridLayer = gridLayer;
    }

    protected RestrictedMousePanMediator() {
    }

    public boolean isDragging() {
        return m_dragging;
    }

    public TransformMediator getTransformMediator() {
        return this.transformMediator;
    }

    public void setTransformMediator(final TransformMediator transformMediator) {
        this.transformMediator = transformMediator;
    }

    @Override
    public void cancel() {
        m_dragging = false;
        setCursor(Style.Cursor.DEFAULT);
    }

    protected void setCursor(final Style.Cursor cursor) {
        getLayerViewport().getElement().getStyle().setCursor(cursor);
    }

    protected Viewport getLayerViewport() {
        return getGridLayer().getViewport();
    }

    GridLayer getGridLayer() {
        return gridLayer;
    }

    @Override
    public boolean handleEvent(final GwtEvent<?> event) {
        if (event.getAssociatedType() == NodeMouseMoveEvent.getType()) {
            if (isDragging()) {
                onMouseMove((NodeMouseMoveEvent) event);
            }
        } else if (event.getAssociatedType() == NodeMouseDownEvent.getType()) {
            final IEventFilter filter = getEventFilter();

            if ((null == filter) || (false == filter.isEnabled()) || (filter.test(event))) {
                onMouseDown((NodeMouseDownEvent) event);
            }
        } else if (event.getAssociatedType() == NodeMouseUpEvent.getType()) {
            if (isDragging()) {
                onMouseUp((NodeMouseUpEvent) event);
            }
        } else if (event.getAssociatedType() == NodeMouseOutEvent.getType()) {
            cancel();
        }

        return false;
    }

    protected void onMouseDown(final NodeMouseDownEvent event) {
        m_last = new Point2D(event.getX(),
                             event.getY());

        m_dragging = true;

        Transform transform = getTransform();

        if (transform == null) {
            setTransform(transform = new Transform());
        }
        m_inverseTransform = transform.getInverse();

        m_inverseTransform.transform(m_last,
                                     m_last);

        setCursor(Style.Cursor.MOVE);
    }

    protected void onMouseMove(final NodeMouseMoveEvent event) {
        final Point2D curr = new Point2D(event.getX(),
                                         event.getY());

        inverseTransform().transform(curr,
                                     curr);

        double deltaX = curr.getX() - m_last.getX();
        double deltaY = curr.getY() - m_last.getY();

        Transform newTransform = getTransform().copy().translate(deltaX,
                                                                 deltaY);
        if (transformMediator != null) {
            newTransform = transformMediator.adjust(newTransform,
                                                    gridLayer.getVisibleBounds());
        }

        setTransform(newTransform);

        m_last = curr;

        if (isBatchDraw()) {
            getViewport().getScene().batch();
        } else {
            getViewport().getScene().draw();
        }
    }

    protected void onMouseUp(final NodeMouseUpEvent event) {
        cancel();
    }

    protected Transform inverseTransform() {
        return m_inverseTransform;
    }
}
