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

import java.util.function.Supplier;

import com.ait.lienzo.client.core.event.AbstractNodeHumanInputEvent;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DefaultDragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.tools.client.event.HandlerRegistration;

public class PreviewLayerDecorator
{
    static final  String                     STROKE_COLOR = ColorName.RED.getColorString();

    static final  double                     STROKE_WIDTH = 3d;

    private final Supplier<Bounds>           backgroundBounds;

    private final Supplier<Bounds>           visibleBounds;

    private final EventHandler               eventHandler;

    private final Rectangle                  decorator;

    private       DragContext                dragContext;

    private HandlerRegistration dragStartHandlerReg;
    private HandlerRegistration dragMoveHandlerReg;
    private HandlerRegistration dragEndHandlerReg;
    private HandlerRegistration mouseEnterHandlerReg;
    private HandlerRegistration mouseExitHandlerReg;

    public interface EventHandler
    {
        void onMouseEnter();

        void onMouseExit();

        void onMove(Point2D point);
    }

    public PreviewLayerDecorator(final Supplier<Bounds> backgroundBounds,
                                 final Supplier<Bounds> visibleBounds,
                                 final EventHandler eventHandler)
    {
        this(backgroundBounds,
             visibleBounds,
             eventHandler,
             buildDecorator());
    }

    PreviewLayerDecorator(final Supplier<Bounds> backgroundBounds,
                          final Supplier<Bounds> visibleBounds,
                          final EventHandler eventHandler,
                          final Rectangle decorator)
    {
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
        dragStartHandlerReg.removeHandler();
        dragMoveHandlerReg.removeHandler();
        dragEndHandlerReg.removeHandler();
        mouseEnterHandlerReg.removeHandler();
        mouseExitHandlerReg.removeHandler();
        dragContext = null;
        decorator.removeFromParent();
    }

    public IPrimitive<?> asPrimitive()
    {
        return decorator;
    }

    private void init()
    {
        dragStartHandlerReg = decorator.addNodeDragStartHandler(this::onDecoratorDragStart);
        dragMoveHandlerReg = decorator.addNodeDragMoveHandler(event -> onDecoratorDragMove());
        dragEndHandlerReg = decorator.addNodeDragEndHandler(event -> onDecoratorDragEnd());
        mouseEnterHandlerReg = decorator.addNodeMouseEnterHandler(event -> onMouseEnter());
        mouseExitHandlerReg = decorator.addNodeMouseExitHandler(event -> onMouseExit());
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

    void onDecoratorDragStart(final AbstractNodeHumanInputEvent event)
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
