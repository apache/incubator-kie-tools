/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import java.util.function.Function;
import java.util.function.Predicate;

import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.shape.AbstractMultiPointShape;
import com.ait.lienzo.client.core.shape.Arc;
import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.decorator.IShapeDecorator;
import com.ait.lienzo.client.core.shape.wires.decorator.PointHandleDecorator;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.Timer;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectorControlPointBuilderTest {

    private static final MultiPath headPath = spy(new MultiPath().circle(10));

    private static final MultiPath tailPath = new MultiPath().circle(10);

    private static Predicate<WiresConnector> TRUE_PREDICATE = connector -> true;

    @Mock
    private WiresConnectorControlImpl control;

    @Mock
    private PointHandleDecorator pointHandleDecorator;

    @Mock
    private HandlerRegistrationManager controlEvents;

    @Mock
    private Viewport viewport;

    private WiresConnectorControlPointBuilder tested;

    private Layer layer;

    private Layer overLayer;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        layer = spy(new Layer());
        Transform transform = new Transform();
        when(layer.getViewport()).thenReturn(viewport);
        when(viewport.getTransform()).thenReturn(transform);
        overLayer = spy(new Layer());
        when(layer.getOverLayer()).thenReturn(overLayer);
        IDirectionalMultiPointShape line = new PolyLine(new Point2D(0, 0), new Point2D(100, 100));
        WiresConnector connector = spy(new WiresConnector(line, new MultiPathDecorator(headPath), new MultiPathDecorator(tailPath)));
        when(connector.getControl()).thenReturn(control);
        layer.add(connector.getGroup());
        when(control.getPointHandleDecorator()).thenReturn(pointHandleDecorator);
        when(control.areControlPointsVisible()).thenReturn(false);
        when(control.getControlPointEventRegistrationManager()).thenReturn(controlEvents);
        tested = new WiresConnectorControlPointBuilder(TRUE_PREDICATE, TRUE_PREDICATE, connector);
    }

    @Test
    public void testEnable() {
        Timer exitTimer = mock(Timer.class);
        when(exitTimer.isRunning()).thenReturn(false);
        tested.exitTimer = exitTimer;

        tested.enable();

        assertTrue(tested.isEnabled());
        verify(control, times(1)).showControlPoints();
        verify(control, never()).hideControlPoints();
        verify(controlEvents, times(8)).register(any(HandlerRegistration.class));
        verify(exitTimer, times(1)).run();
        assertNull(tested.exitTimer);
    }

    @Test
    public void testDisable() {
        Timer exitTimer = mock(Timer.class);
        when(exitTimer.isRunning()).thenReturn(false);
        tested.enable();
        tested.exitTimer = exitTimer;
        Shape<?> cpShape = mock(Shape.class);
        tested.mousePointerCP = cpShape;

        tested.disable();
        verify(exitTimer, times(1)).cancel();
        assertNotNull(tested.exitTimer);

        tested.exitTimer.run();
        verify(cpShape, times(1)).removeFromParent();
        assertFalse(tested.isEnabled());
        assertNull(tested.mousePointerCP);
        verify(layer, atLeastOnce()).batch();
        verify(overLayer, atLeastOnce()).batch();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testScheduleControlPointBuildAnimation() {
        tested.enable();
        tested.mousePointerCP = spy(new Rectangle(50, 50)
                                            .setX(1.5)
                                            .setY(33.65)
                                            .setStrokeAlpha(1)
                                            .setStrokeColor(ColorName.RED));
        Function<Shape<?>, IAnimationHandle> animateTask = mock(Function.class);
        tested.scheduleControlPointBuildAnimation(animateTask);
        verify(overLayer, times(1)).add(eq(tested.cpBuilderAnimationShape));
        assertEquals(1.5d, tested.cpBuilderAnimationShape.getX(), 0d);
        assertEquals(33.65d, tested.cpBuilderAnimationShape.getY(), 0d);
        assertEquals(1d, tested.cpBuilderAnimationShape.getStrokeAlpha(), 0d);
        assertEquals(ColorName.RED.getColorString(), tested.cpBuilderAnimationShape.getStrokeColor());
        assertTrue(tested.cpBuilderAnimationShape.getStrokeWidth() > 0);
        verify(animateTask, times(1)).apply(eq(tested.cpBuilderAnimationShape));
    }

    @Test
    public void testCloseControlPointBuildAnimation() {
        tested.controlPointBuildAnimation = mock(IAnimationHandle.class);
        tested.cpBuilderAnimationShape = mock(Arc.class);
        tested.closeControlPointBuildAnimation();
        verify(tested.controlPointBuildAnimation, times(1)).stop();
        verify(tested.cpBuilderAnimationShape, times(1)).removeFromParent();
    }

    @Test
    public void testCreateControlPointAt() {
        tested.enable();
        Shape<?> cpShape = mock(Shape.class);
        tested.mousePointerCP = cpShape;

        tested.createControlPointAt(232, 621);

        verify(cpShape, times(1)).removeFromParent();
        assertNull(tested.mousePointerCP);
        verify(layer, atLeastOnce()).batch();
        verify(overLayer, atLeastOnce()).batch();
    }

    @Test
    public void testMoveControlPointTo() {
        tested.enable();
        tested.moveControlPointTo(55, 55);

        assertNull(tested.exitTimer);
        assertEquals(55.00000000000001d, tested.mousePointerCP.getX(), 0d);
        assertEquals(55.00000000000001d, tested.mousePointerCP.getY(), 0d);
        assertEquals(AbstractMultiPointShape.DefaultMultiPointShapeHandleFactory.SELECTION_OFFSET, tested.mousePointerCP.getSelectionBoundsOffset(), 0d);
        assertEquals(AbstractMultiPointShape.DefaultMultiPointShapeHandleFactory.SELECTION_OFFSET, tested.mousePointerCP.getSelectionBoundsOffset(), 0d);
        assertTrue(tested.mousePointerCP.isListening());
        assertTrue(tested.mousePointerCP.isFillShapeForSelection());
        assertTrue(tested.mousePointerCP.isFillBoundsForSelection());
        verify(pointHandleDecorator, times(1)).decorate(eq(tested.mousePointerCP), eq(IShapeDecorator.ShapeState.INVALID));
        verify(overLayer, times(1)).add(eq(tested.mousePointerCP));
        verify(layer, atLeastOnce()).batch();
        verify(overLayer, atLeastOnce()).batch();
    }

    @Test
    public void testMoveNearToCP() {
        tested.enable();
        tested.moveControlPointTo(0, 0);

        assertNotNull(tested.exitTimer);
        verify(layer, never()).batch();
        verify(overLayer, never()).batch();
    }
}
