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

package org.kie.workbench.common.stunner.core.client.shape.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShapeStateHelperTest {

    private final static String STROKE_COLOR_0 = "sc0";
    private final static double STROKE_WIDTH_0 = 12;
    private final static double STROKE_ALPHA_0 = 0.1;

    @Mock
    Shape<ShapeView> shape;

    @Mock
    ShapeView shapeView;

    private ShapeStateHelper<ShapeView, Shape<ShapeView>> tested;

    @Before
    public void setup() throws Exception {
        when(shapeView.getStrokeColor()).thenReturn(STROKE_COLOR_0);
        when(shapeView.getStrokeAlpha()).thenReturn(STROKE_ALPHA_0);
        when(shapeView.getStrokeWidth()).thenReturn(STROKE_WIDTH_0);
        when(shape.getShapeView()).thenReturn(shapeView);
        this.tested = new ShapeStateHelper<ShapeView, Shape<ShapeView>>(shape);
        tested.save(ShapeState.NONE::equals);
    }

    @Test
    public void testApplySelectedState() {
        testApplyState(ShapeState.SELECTED);
    }

    @Test
    public void testApplyInvalidState() {
        testApplyState(ShapeState.INVALID);
    }

    @Test
    public void testApplyHighlightdState() {
        testApplyState(ShapeState.HIGHLIGHT);
    }

    @Test
    public void testApplyNoneState() {
        testApplyState(ShapeState.SELECTED);
        tested.applyState(ShapeState.NONE);
        verify(shapeView,
               times(1)).setStrokeColor(eq(STROKE_COLOR_0));
        verify(shapeView,
               times(1)).setStrokeWidth(eq(STROKE_WIDTH_0));
        verify(shapeView,
               times(1)).setStrokeAlpha(eq(STROKE_ALPHA_0));
    }

    private void testApplyState(final ShapeState state) {
        tested.applyState(state);
        verify(shapeView,
               times(1)).setStrokeColor(eq(state.getColor()));
        verify(shapeView,
               times(1)).setStrokeWidth(eq(STROKE_WIDTH_0 + (ShapeStateHelper.ACTIVE_STROKE_WIDTH_PCT * STROKE_WIDTH_0)));
        verify(shapeView,
               times(1)).setStrokeAlpha(eq(ShapeStateHelper.ACTIVE_STROKE_ALPHA));
    }
}
