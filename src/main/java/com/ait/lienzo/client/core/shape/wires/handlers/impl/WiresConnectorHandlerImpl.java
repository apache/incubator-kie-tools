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

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.tools.client.Timer;
import java.util.function.Consumer;

public class WiresConnectorHandlerImpl implements WiresConnectorHandler
{
    static final int MOUSE_DOWN_TIMER_DELAY = 350;

    private final WiresConnector        m_connector;

    private final  WiresManager         m_wiresManager;
    private final  Consumer<Event>      mouseDownEventConsumer;
    private        Timer                clickTimer;
    private        Event                event;
    private        boolean              ownToken;

    private final Consumer<Event>      mouseClickEventConsumer;

    Timer                             mouseDownTimer;

    public static class Event
    {
        final double  x;

        final double  y;

        final boolean isShiftKeyDown;

        public Event(final double x,
                     final double y,
                     final boolean isShiftKeyDown)
        {
            this.x = x;
            this.y = y;
            this.isShiftKeyDown = isShiftKeyDown;
        }

        public double getX()
        {
            return x;
        }

        public double getY()
        {
            return y;
        }

        public boolean isShiftKeyDown()
        {
            return isShiftKeyDown;
        }
    }

    public WiresConnectorHandlerImpl(final WiresConnector connector,
                                     final WiresManager wiresManager)
    {
        this(connector,
             wiresManager,
             WiresConnectorEventFunctions.select(wiresManager, connector),
             WiresConnectorEventFunctions.addControlPoint(connector));
    }

    public WiresConnectorHandlerImpl(final WiresConnector connector,
                                     final WiresManager wiresManager,
                                     final Consumer<Event> mouseClickEventConsumer,
                                     final Consumer<Event> mouseDownEventConsumer)
    {
        this.m_connector = connector;
        this.m_wiresManager = wiresManager;
        this.mouseClickEventConsumer = mouseClickEventConsumer;
        this.mouseDownEventConsumer = mouseDownEventConsumer;
    }

    @Override
    public void onNodeDragStart(final NodeDragStartEvent event)
    {
        getControl().hideControlPoints();
        getControl().onMoveStart(event.getDragContext().getDragStartX(),
                                      event.getDragContext().getDragStartY());
    }

    @Override
    public void onNodeDragMove(final NodeDragMoveEvent event)
    {
        getControl().onMove(event.getDragContext().getDragStartX(),
                                 event.getDragContext().getDragStartY());
    }

    @Override
    public void onNodeDragEnd(final NodeDragEndEvent event)
    {
        getControl().onMove(event.getDragContext().getDragStartX(),
                            event.getDragContext().getDragStartY());
        if (getControl().accept()) {
            getControl().onMoveComplete();
            getControl().execute();
        } else {
            getControl().reset();
        }
        
    }

    public WiresConnectorControlImpl getControl()
    {
        return (WiresConnectorControlImpl) m_connector.getControl();
    }

    WiresConnector getConnector()
    {
        return m_connector;
    }

    WiresManager getWiresManager()
    {
        return m_wiresManager;
    }

    @Override
    public void onNodeMouseEnter(NodeMouseEnterEvent event)
    {
        ifControlPointsBuilder(builder -> builder.enable());
    }

    @Override
    public void onNodeMouseExit(NodeMouseExitEvent event)
    {
        ifControlPointsBuilder(builder -> builder.disable());
    }

    @Override
    public void onNodeMouseDown(final NodeMouseDownEvent event)
    {
        ifControlPointsBuilder(builder -> {
            mouseDownTimer = new Timer()
            {
                @Override
                public void run()
                {
                    final Point2D point = WiresShapeControlUtils.getViewportRelativeLocation(getLayer().getViewport(), event);
                    mouseDownEventConsumer.accept(new Event(point.getX(), point.getY(), false));
                    builder.createControlPointAt(event.getX(), event.getY());
                }
            };
            mouseDownTimer.schedule(MOUSE_DOWN_TIMER_DELAY);
            builder.scheduleControlPointBuildAnimation(MOUSE_DOWN_TIMER_DELAY);
        });
    }

    @Override
    public void onNodeMouseMove(final NodeMouseMoveEvent event)
    {
        if (null != mouseDownTimer)
        {
            mouseDownTimer.run();
            mouseDownTimer = null;
        }
        ifControlPointsBuilder(builder -> builder.moveControlPointTo(event.getX(), event.getY()));
    }

    @Override
    public void onNodeMouseClick(final NodeMouseClickEvent mouseEvent)
    {
        if (null != mouseDownTimer)
        {
            mouseDownTimer.cancel();
            mouseDownTimer = null;
        }
        getControl().getControlPointBuilder().closeControlPointBuildAnimation();
        final Point2D location = WiresShapeControlUtils.getViewportRelativeLocation(getLayer().getViewport(), mouseEvent);
        final Event   event    = new Event(location.getX(), location.getY(), mouseEvent.isShiftKeyDown());
        mouseClickEventConsumer.accept(event);
    }

    private Layer getLayer()
    {
        return m_wiresManager.getLayer().getLayer();
    }

    private void ifControlPointsBuilder(final Consumer<WiresConnectorControlPointBuilder> consumer)
    {
        if (isControlPointsBuilderAllowed(getConnector()))
        {
            consumer.accept(getControl().getControlPointBuilder());
        }
    }

    private static boolean isControlPointsBuilderAllowed(final WiresConnector connector)
    {
        if (!connector.getLine().isControlPointShape())
        {
            // skipping in case the connector is not a control point shape
            return false;
        }
        return !connector.isDraggable();
    }

}