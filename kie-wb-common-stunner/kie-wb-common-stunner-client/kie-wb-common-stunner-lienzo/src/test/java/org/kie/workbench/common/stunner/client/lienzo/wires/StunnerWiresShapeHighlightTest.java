/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.wires;

import java.util.Collections;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeHighlightImpl;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.animation.ShapeViewDecoratorAnimation;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.core.client.animation.Animation;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasUnhighlightEvent;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class StunnerWiresShapeHighlightTest {

    private static final String color = "color1";
    private static final double width = 5d;
    private static final double alpha = 0.5d;

    @Mock
    private WiresManager wiresManager;

    @Mock
    private WiresShape lienzoShape;

    @Mock
    private LienzoShapeView stunnerShape;

    @Mock
    private Shape decorator;

    @Mock
    private WiresShapeHighlightImpl delegate;

    @Mock
    private EventSourceMock<CanvasUnhighlightEvent> unhighlightEvent;

    private StunnerWiresShapeHighlight tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(wiresManager.getDockingAcceptor()).thenReturn(IDockingAcceptor.ALL);
        when(wiresManager.getContainmentAcceptor()).thenReturn(IContainmentAcceptor.ALL);
        when(wiresManager.getLocationAcceptor()).thenReturn(ILocationAcceptor.ALL);
        when(wiresManager.getConnectionAcceptor()).thenReturn(IConnectionAcceptor.ALL);
        when(stunnerShape.getStrokeColor()).thenReturn(color);
        when(stunnerShape.getStrokeWidth()).thenReturn(width);
        when(stunnerShape.getStrokeAlpha()).thenReturn(alpha);
        when(stunnerShape.getDecorators()).thenReturn(Collections.singletonList(decorator));
        tested = new StunnerWiresShapeHighlight(unhighlightEvent, delegate);
    }

    @Test
    public void testHighlightBodyForWiresShape() {
        tested.highlight(lienzoShape, PickerPart.ShapePart.BODY);
        verify(delegate, times(1)).highlight(eq(lienzoShape),
                                             eq(PickerPart.ShapePart.BODY));
    }

    @Test
    public void testHighlightBorder() {
        tested.highlight(lienzoShape, PickerPart.ShapePart.BORDER);
        verify(delegate, times(1)).highlight(eq(lienzoShape),
                                             eq(PickerPart.ShapePart.BORDER));
    }

    @Test
    public void testHighlightBodyForStunnerShape() {
        tested.highlightBody(stunnerShape,
                             "color");
        verify(decorator, times(1)).animate(any(AnimationTweener.class),
                                            any(AnimationProperties.class),
                                            anyDouble(),
                                            any(IAnimationCallback.class));
        final Animation restoreAnimation = tested.getRestoreAnimation();
        assertNotNull(restoreAnimation);
        assertTrue(restoreAnimation instanceof ShapeViewDecoratorAnimation);
        final ShapeViewDecoratorAnimation viewRestoreAnimation = (ShapeViewDecoratorAnimation) restoreAnimation;
        assertEquals(color, viewRestoreAnimation.getColor());
        assertEquals(alpha, viewRestoreAnimation.getAlpha(), 0d);
        assertEquals(width, viewRestoreAnimation.getStrokeWidth(), 0d);
    }

    @Test
    public void testRestore() {
        tested.highlightBody(stunnerShape,
                             "color");
        tested.restore();
        verify(unhighlightEvent, times(1)).fire(any(CanvasUnhighlightEvent.class));
        verify(delegate, times(1)).restore();
        verify(decorator, times(2)).animate(any(AnimationTweener.class),
                                            any(AnimationProperties.class),
                                            anyDouble(),
                                            any(IAnimationCallback.class));
    }
}
