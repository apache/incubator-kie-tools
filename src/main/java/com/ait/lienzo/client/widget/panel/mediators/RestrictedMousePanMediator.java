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
package com.ait.lienzo.client.widget.panel.mediators;

import com.ait.lienzo.client.core.mediator.AbstractMediator;
import com.ait.lienzo.client.core.mediator.IEventFilter;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;
import com.ait.lienzo.tools.client.event.INodeEvent;
import elemental2.dom.UIEvent;

/**
 * This a fork of {@link com.ait.lienzo.client.core.mediator.MousePanMediator} however this implementation does not stop
 * the propagation of NodeMouseEvents to other listeners. Lienzo's implementation causes other NodeMouseMoveHandler,
 * NodeMouseDownHandler and NodeMouseUpHandler to miss receipt of Events. This implementation also restricts
 * transformations according to a {@link TransformMediator}.
 */
public class RestrictedMousePanMediator extends AbstractMediator
{
    private final LienzoBoundsPanel panel;

    private       TransformMediator transformMediator;

    private Point2D   m_last             = new Point2D(0, 0);

    private boolean   m_dragging         = false;

    private Transform m_inverseTransform = null;

    public RestrictedMousePanMediator(final LienzoBoundsPanel panel)
    {
        this.panel = panel;
    }

    public boolean isDragging()
    {
        return m_dragging;
    }

    public TransformMediator getTransformMediator()
    {
        return this.transformMediator;
    }

    public void setTransformMediator(final TransformMediator transformMediator)
    {
        this.transformMediator = transformMediator;
    }

    @Override
    public void cancel()
    {
        m_dragging = false;
    }

    Layer getLayer()
    {
        return getViewport().getLayer();
    }

    @Override
    public <H extends EventHandler> boolean handleEvent(INodeEvent.Type<H> type,
                                                        UIEvent event,
                                                        int x,
                                                        int y) {
        if ("mouseMove".equals(event.type)) {
            if (isDragging())
            {
                onMouseMove(x, y);
            }
        } else if ("mouseDown".equals(event.type)) {
            final IEventFilter filter = getEventFilter();

            if ((null == filter) || (!filter.isEnabled()) || (filter.test(event)))
            {
                onMouseDown(x, y);
            }
        } else if ("mouseUp".equals(event.type)) {
            if (isDragging())
            {
                onMouseUp();
            }
        } else if ("mouseOut".equals(event.type)) {
            cancel();
        }

        return false;
    }

    /*@Override
    public boolean handleEvent(final GwtEvent<?> event)
    {
        if (event.getAssociatedType() == NodeMouseMoveEvent.getType())
        {
            if (isDragging())
            {
                onMouseMove((NodeMouseMoveEvent) event);
            }
        }
        else if (event.getAssociatedType() == NodeMouseDownEvent.getType())
        {
            final IEventFilter filter = getEventFilter();

            if ((null == filter) || (false == filter.isEnabled()) || (filter.test(event)))
            {
                onMouseDown((NodeMouseDownEvent) event);
            }
        }
        else if (event.getAssociatedType() == NodeMouseUpEvent.getType())
        {
            if (isDragging())
            {
                onMouseUp((NodeMouseUpEvent) event);
            }
        }
        else if (event.getAssociatedType() == NodeMouseOutEvent.getType())
        {
            cancel();
        }

        return false;
    }*/

    protected void onMouseDown(final int x,
                               final int y)
    {
        m_last = new Point2D(x, y);

        m_dragging = true;

        Transform transform = getTransform();

        if (transform == null)
        {
            setTransform(transform = new Transform());
        }
        m_inverseTransform = transform.getInverse();

        m_inverseTransform.transform(m_last,
                                     m_last);
    }

    protected void onMouseMove(final int x,
                               final int y)
    {
        final Point2D curr = new Point2D(x, y);

        inverseTransform().transform(curr,
                                     curr);

        double deltaX = curr.getX() - m_last.getX();
        double deltaY = curr.getY() - m_last.getY();

        Transform newTransform = getTransform().copy().translate(deltaX,
                                                                 deltaY);
        if (transformMediator != null)
        {
            newTransform = transformMediator.adjust(newTransform,
                                                    panel.getBounds());
        }

        setTransform(newTransform);

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

    protected void onMouseUp()
    {
        cancel();
    }

    protected Transform inverseTransform()
    {
        return m_inverseTransform;
    }
}
