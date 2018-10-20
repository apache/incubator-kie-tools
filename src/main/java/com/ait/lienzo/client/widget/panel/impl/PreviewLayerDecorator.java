/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DefaultDragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.tooling.common.api.java.util.function.Supplier;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;

public class PreviewLayerDecorator
{
    static final String STROKE_COLOR = ColorName.RED.getColorString();

    static final double STROKE_WIDTH = 3d;

    private final HandlerRegistrationManager handlers;

    private final Supplier<Bounds>           backgroundBounds;

    private final Supplier<Bounds>           visibleBounds;

    private final EventHandler               eventHandler;

    private final Rectangle                  decorator;

    private       DragContext                dragContext;

    public interface EventHandler
    {
        void onMouseEnter();

        void onMouseExit();

        void onMove(Point2D point);
    }

    public PreviewLayerDecorator(final HandlerRegistrationManager handlers,
                                 final Supplier<Bounds> backgroundBounds,
                                 final Supplier<Bounds> visibleBounds,
                                 final EventHandler eventHandler)
    {
        this(handlers,
             backgroundBounds,
             visibleBounds,
             eventHandler,
             buildDecorator());
    }

    PreviewLayerDecorator(final HandlerRegistrationManager handlers,
                          final Supplier<Bounds> backgroundBounds,
                          final Supplier<Bounds> visibleBounds,
                          final EventHandler eventHandler,
                          final Rectangle decorator)
    {
        this.handlers = handlers;
        this.backgroundBounds = backgroundBounds;
        this.visibleBounds = visibleBounds;
        this.eventHandler = eventHandler;
        this.decorator = decorator;
        init();
    }

    public void update()
    {
        final Bounds viewportBounds = backgroundBounds.get();
        final Bounds bounds         = visibleBounds.get();
        final double x              = bounds.getX();
        final double y              = bounds.getY();
        final double width          = bounds.getWidth();
        final double height         = bounds.getHeight();
        if (width <= 0 && height <= 0)
        {
            decorator.setStrokeAlpha(0)
                     .setListening(false);
        }
        else if (!isDragging())
        {
            decorator.setListening(true)
                     .getDragBounds()
                     .setX1(viewportBounds.getX())
                     .setY1(viewportBounds.getY())
                     .setX2(viewportBounds.getWidth() - width)
                     .setY2(viewportBounds.getHeight() - height);

            decorator.setX(x)
                     .setY(y);
            if (width != viewportBounds.getWidth() ||
                height != viewportBounds.getHeight())
            {
                decorator.setWidth(width)
                         .setHeight(height)
                         .setStrokeAlpha(1);
            }
        }
    }

    public void destroy()
    {
        dragContext = null;
        decorator.removeFromParent();
    }

    public IPrimitive<?> asPrimitive()
    {
        return decorator;
    }

    private void init()
    {
        handlers.register(
                decorator.addNodeDragStartHandler(new NodeDragStartHandler()
                {
                    @Override
                    public void onNodeDragStart(NodeDragStartEvent event)
                    {
                        onDecoratorDragStart(event);
                    }
                })
                         );
        handlers.register(
                decorator.addNodeDragMoveHandler(new NodeDragMoveHandler()
                {
                    @Override
                    public void onNodeDragMove(NodeDragMoveEvent event)
                    {
                        onDecoratorDragMove();
                    }
                })
                         );

        handlers.register(
                decorator.addNodeDragEndHandler(new NodeDragEndHandler()
                {
                    @Override
                    public void onNodeDragEnd(NodeDragEndEvent event)
                    {
                        onDecoratorDragEnd();
                    }
                })
                         );

        handlers.register(
                decorator.addNodeMouseEnterHandler(new NodeMouseEnterHandler()
                {
                    @Override
                    public void onNodeMouseEnter(NodeMouseEnterEvent event)
                    {
                        onMouseEnter();
                    }
                }));

        handlers.register(
                decorator.addNodeMouseExitHandler(new NodeMouseExitHandler()
                {
                    @Override
                    public void onNodeMouseExit(NodeMouseExitEvent event)
                    {
                        onMouseExit();
                    }
                }));
    }

    static Rectangle buildDecorator()
    {
        return new Rectangle(5, 5)
                .setStrokeAlpha(1)
                .setStrokeColor(STROKE_COLOR)
                .setStrokeWidth(STROKE_WIDTH)
                .setListening(true)
                .setFillBoundsForSelection(true)
                .setFillShapeForSelection(true)
                .setDraggable(true)
                .setDragBounds(new DragBounds(0, 0, 5, 5))
                .setDragConstraints(new DefaultDragConstraintEnforcer());
    }

    public boolean isDragging()
    {
        return null != dragContext;
    }

    void onMouseEnter()
    {
        eventHandler.onMouseEnter();
    }

    void onMouseExit()
    {
        eventHandler.onMouseExit();
    }

    void onDecoratorDragStart(final AbstractNodeDragEvent event)
    {
        dragContext = event.getDragContext();
    }

    void onDecoratorDragMove()
    {
        eventHandler.onMove(decorator.getLocation().copy());
    }

    void onDecoratorDragEnd()
    {
        dragContext = null;
    }
}
