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

package org.kie.workbench.common.stunner.client.lienzo.wires.decorator;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.decorator.IShapeDecorator;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class StunnerPointHandleDecoratorTest {

    protected static final Shadow SHADOW_SELECTED = new Shadow(StunnerPointHandleDecorator.MAIN_COLOR, 10, 0, 0);
    private StunnerPointHandleDecorator tested;
    private Shape shape;

    @Before
    public void setUp() throws Exception {
        tested = new StunnerPointHandleDecorator();
        shape = spy(new MultiPath());
    }

    @Test
    public void decorateValidNone() {
        tested.decorate(shape, IShapeDecorator.ShapeState.VALID);
        tested.decorate(shape, IShapeDecorator.ShapeState.NONE);
        verify(shape, times(2)).setFillColor(StunnerPointHandleDecorator.MAIN_COLOR);
        verify(shape, times(2)).setFillAlpha(0.8);
        verify(shape, times(2)).setStrokeAlpha(1);
        verify(shape, times(2)).setShadow(SHADOW_SELECTED);
        verify(shape, times(2)).setStrokeWidth(2);
        verify(shape, times(2)).setStrokeColor(StunnerPointHandleDecorator.STROKE_COLOR);
    }

    @Test
    public void decorateInvalid() {
        tested.decorate(shape, IShapeDecorator.ShapeState.INVALID);
        verify(shape).setFillColor(ColorName.WHITE);
        verify(shape).setShadow(SHADOW_SELECTED);
        verify(shape).setFillAlpha(1);
        verify(shape).setStrokeAlpha(1);
        verify(shape).setStrokeWidth(2);
        verify(shape).setStrokeColor(StunnerPointHandleDecorator.MAIN_COLOR);
    }
}