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

import java.util.function.Function;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.shape.wires.*;
import com.ait.lienzo.client.core.shape.wires.decorator.IShapeDecorator;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.tools.client.Timer;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;
import java.util.function.Predicate;

import static com.ait.lienzo.client.core.shape.AbstractMultiPointShape.DefaultMultiPointShapeHandleFactory.*;

public class WiresConnectorControlPointBuilder
{
    private static final int                                 EXIT_DELAY                     = 150;

    private static final String                              DEFAULT_CP_BUILDER_SHAPE_COLOR = "#FF0000";

    private static final double                              NEW_CP_SCALE_FACTOR            = 1.5;

    private final        Predicate<WiresConnector>           canHideControlPoints;

    private final        Predicate<WiresConnector>           canShowControlPoints;

    private final        WiresConnector                      connector;

    private final        NFastArrayList<HandlerRegistration> registrations;

    Arc              cpBuilderAnimationShape;

    IAnimationHandle controlPointBuildAnimation;

    Shape<?>         mousePointerCP;

    Timer            exitTimer;

    public WiresConnectorControlPointBuilder(final Predicate<WiresConnector> canShowControlPoints,
                                             final Predicate<WiresConnector> canHideControlPoints,
                                             final WiresConnector connector)
    {
        this.canShowControlPoints = canShowControlPoints;
        this.canHideControlPoints = canHideControlPoints;
        this.connector = connector;
        this.registrations = new NFastArrayList<>();
    }

    public void enable()
    {
        forceRunExitTimer();
        if (canShowControlPoints.test(connector))
        {
            connector.getControl().showControlPoints();
        }
        listenForControlPoints();
    }

    public boolean isEnabled()
    {
        return !registrations.isEmpty();
    }

    public void disable()
    {
        if (!isEnabled())
        {
            return;
        }

        cancelExitTimer();

        exitTimer = new Timer()
        {
            @Override
            public void run()
            {
                exit();
            }
        };

        exitTimer.schedule(EXIT_DELAY);
    }

    public void destroy()
    {
        cancelExitTimer();
        exit();
    }

    private void exit()
    {
        destroyMousePointerCP();
        if (canHideControlPoints.test(connector))
        {
            getControl().hideControlPoints();
        }
        else
        {
            for (int i = 0; i < registrations.size(); i++)
            {
                registrations.get(i).removeHandler();
            }
        }
        registrations.clear();
    }

    public void scheduleControlPointBuildAnimation(final int delay)
    {
        scheduleControlPointBuildAnimation(shape -> shape.animate(AnimationTweener.LINEAR,
                                                          AnimationProperties.toPropertyList(AnimationProperty.Properties.END_ANGLE(2 * Math.PI)),
                                                          delay));
    }

    void scheduleControlPointBuildAnimation(final Function<Shape<?>, IAnimationHandle> animateTask)
    {
        closeControlPointBuildAnimation();
        if (null != mousePointerCP)
        {
            final BoundingBox bb   = mousePointerCP.getBoundingBox();
            final double      size = bb.getWidth() > bb.getHeight() ? bb.getWidth() : bb.getHeight();
            cpBuilderAnimationShape =
                    new Arc(size, 0, 0)
                            .setX(mousePointerCP.getX())
                            .setY(mousePointerCP.getY())
                            .setStrokeAlpha(1)
                            .setStrokeColor(figureOutControlPointBuilderColor(mousePointerCP))
                            .setStrokeWidth(1.5);
            getTransientPointLayer().add(cpBuilderAnimationShape);
            controlPointBuildAnimation = animateTask.apply(cpBuilderAnimationShape);
            batch();
        }
    }

    public void closeControlPointBuildAnimation()
    {
        if (null != controlPointBuildAnimation)
        {
            controlPointBuildAnimation.stop();
        }
        if (null != cpBuilderAnimationShape)
        {
            cpBuilderAnimationShape.removeFromParent();
        }
        batch();
    }

    public void createControlPointAt(final int x,
                                     final int y)
    {
        if (!isEnabled())
        {
            return;
        }

        destroyMousePointerCP();

        final Point2D location = WiresShapeControlUtils.getViewportRelativeLocation(getLayer().getViewport(), x, y);
        int           index    = getPointNearTo(location);
        if (index > -1)
        {
            final IControlHandle current = connector.getPointHandles().getHandle(index);
            if (null != current)
            {
                current.getControl().setScale(NEW_CP_SCALE_FACTOR);
                batch();
            }
        }
    }

    public void moveControlPointTo(final int x,
                                   final int y)
    {
        if (!isEnabled())
        {
            return;
        }

        final Point2D relLocation = WiresShapeControlUtils.getViewportRelativeLocation(getLayer().getViewport(), x, y);

        final Point2DArray linePoints   = connector.getLine().getPoint2DArray();
        final Point2D      closestPoint = Geometry.findClosestPointOnLine(relLocation.getX(), relLocation.getY(), linePoints);
        if (closestPoint == null)
        {
            disable();
            return;
        }

        //check it the closest point is overlapping or it is very close to any line point
        if (getPointNearTo(closestPoint) > -1)
        {
            disable();
            return;
        }

        cancelExitTimer();

        if (mousePointerCP == null)
        {
            mousePointerCP = createTransientControlHandle();
            getTransientPointLayer().setListening(true);
            getTransientPointLayer().add(mousePointerCP);
        }

        //setting current position
        mousePointerCP.setX(closestPoint.getX()).setY(closestPoint.getY());

        batch();
    }

    private void listenForControlPoints()
    {
        final HandlerRegistrationManager events = getControl().getControlPointEventRegistrationManager();
        if (null != events)
        {
            // Listen for CP events.
            final IControlHandleList pointHandles = connector.getPointHandles();
            for (int i = 0; i < pointHandles.size(); i++)
            {
                final IControlHandle handle = pointHandles.getHandle(i);
                listenForControlPoint(events,
                                      handle.getControl());
            }
            // Listen for head connection events.
            final WiresConnection headConnection = connector.getHeadConnection();
            listenForConnection(events, headConnection);
            // Listen for tail connection events.
            final WiresConnection tailConnection = connector.getTailConnection();
            listenForConnection(events, tailConnection);
        }
    }

    private void listenForConnection(final HandlerRegistrationManager events,
                                     final WiresConnection connection)
    {
        if (null != connection)
        {
            final IPrimitive<?> control = connection.getControl();
            if (null != control)
            {
                listenForControlPoint(events,
                                      control);
            }
            final WiresMagnet magnet = connection.getMagnet();
            if (null != magnet && null != magnet.getControl())
            {
                listenForControlPoint(events,
                                      magnet.getControl());
            }
        }
    }

    private void listenForControlPoint(final HandlerRegistrationManager manager,
                                       final IDrawable<?> cp)
    {

        register(manager,
                 cp.addNodeMouseEnterHandler(event -> {
                     destroyMousePointerCP();
                     cancelExitTimer();
                 }));

        register(manager,
                 cp.addNodeMouseExitHandler(event -> disable()));
    }

    private Shape<?> createTransientControlHandle()
    {
        final Shape<?> pointHandleShape = new Circle(R0);
        getControl().getPointHandleDecorator().decorate(pointHandleShape, IShapeDecorator.ShapeState.INVALID);
        pointHandleShape.setSelectionBoundsOffset(SELECTION_OFFSET);
        pointHandleShape.setSelectionStrokeOffset(SELECTION_OFFSET);
        pointHandleShape.setFillBoundsForSelection(true);
        pointHandleShape.setFillShapeForSelection(true);
        pointHandleShape.setListening(true);
        return pointHandleShape;
    }

    private int getPointNearTo(final Point2D location)
    {
        final Point2DArray linePoints = connector.getLine().getPoint2DArray();
        for (int i = 0; i < linePoints.size(); i++)
        {
            final Point2D point = linePoints.get(i);
            if (Geometry.distance(location.getX(), location.getY(), point.getX(), point.getY()) < R1)
            {
                return i;
            }
        }
        return -1;
    }

    private void destroyMousePointerCP()
    {
        if (null != mousePointerCP)
        {
            mousePointerCP.removeFromParent();
            mousePointerCP = null;
        }
        closeControlPointBuildAnimation();
        batch();
    }

    private void register(final HandlerRegistrationManager manager,
                          final HandlerRegistration registration)
    {
        registrations.add(registration);
        manager.register(registration);
    }

    private void cancelExitTimer()
    {
        if (null != exitTimer)
        {
            exitTimer.cancel();
            exitTimer = null;
        }
    }

    private void forceRunExitTimer()
    {
        if (null != exitTimer && !exitTimer.isRunning())
        {
            exitTimer.run();
            exitTimer = null;
        }
    }

    private void batch()
    {
        getLayer().batch();
        getTransientPointLayer().batch();
    }

    private WiresConnectorControlImpl getControl()
    {
        return (WiresConnectorControlImpl) connector.getControl();
    }

    private Layer getTransientPointLayer()
    {
        return getOverLayer();
    }

    private Layer getLayer()
    {
        return connector.getGroup().getLayer().getLayer();
    }

    private Layer getOverLayer()
    {
        return getLayer().getOverLayer();
    }

    private static String figureOutControlPointBuilderColor(final Shape<?> mousePointerDecorator)
    {
        String color = null;
        if (mousePointerDecorator.getStrokeAlpha() > 0
            && isNotWhite(mousePointerDecorator.getStrokeColor()))
        {
            color = mousePointerDecorator.getStrokeColor();
        }
        else if (mousePointerDecorator.getFillAlpha() > 0
                 && isNotWhite(mousePointerDecorator.getFillColor()))
        {
            color = mousePointerDecorator.getFillColor();
        }
        return null != color ? color : DEFAULT_CP_BUILDER_SHAPE_COLOR;
    }

    private static boolean isNotWhite(final String color)
    {
        return !"#FFFFFF".equalsIgnoreCase(color);
    }
}
