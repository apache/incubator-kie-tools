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

package org.kie.workbench.common.stunner.svg.client.shape.view.impl;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.impl.AnimatedShapeStateStrokeHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class SVGShapeStateHandlerTest {

    @Mock
    private AnimatedShapeStateStrokeHandler strokeHandler;

    private SVGShapeStateHandler tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        tested = new SVGShapeStateHandler(strokeHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testForShape() {
        final Shape shape = mock(Shape.class);
        assertEquals(tested, tested.forShape(shape));
        verify(strokeHandler, times(1)).forShape(eq(shape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testApplyState() {
        tested.applyState(ShapeState.SELECTED);
        verify(strokeHandler, times(1)).applyState(eq(ShapeState.SELECTED));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShapeUpdated() {
        tested.shapeUpdated();
        verify(strokeHandler, times(1)).shapeUpdated();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReset() {
        tested.reset();
        verify(strokeHandler, times(1)).reset();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetShapeState() {
        tested.getShapeState();
        verify(strokeHandler, times(1)).getShapeState();
    }
}
