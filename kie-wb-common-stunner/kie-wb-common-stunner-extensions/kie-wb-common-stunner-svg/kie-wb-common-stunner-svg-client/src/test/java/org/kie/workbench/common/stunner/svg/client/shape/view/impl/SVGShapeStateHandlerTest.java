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
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class SVGShapeStateHandlerTest {

    private final SVGShapeStateHolderImpl STATE_HOLDER1 =
            new SVGShapeStateHolderImpl(ShapeState.SELECTED,
                                        1d,
                                        "#FF55AA",
                                        0.8d,
                                        "#0099FF",
                                        0.5d,
                                        5d);

    private final SVGShapeStateHolderImpl STATE_HOLDER2 =
            new SVGShapeStateHolderImpl(ShapeState.INVALID,
                                        1d,
                                        null,
                                        0.4d,
                                        "#0099FF",
                                        null,
                                        null);

    @Mock
    SVGShapeView<?> view;

    private SVGShapeStateHandler tested;

    @Before
    public void setup() throws Exception {
        this.tested = new SVGShapeStateHandler(view);
    }

    @Test
    public void testNoState() {
        assertFalse(tested.applyState(ShapeState.SELECTED));
        assertFalse(tested.applyState(ShapeState.INVALID));
        assertFalse(tested.applyState(ShapeState.HIGHLIGHT));
    }

    @Test
    public void testState1() {
        tested.registerStateHolder(ShapeState.SELECTED,
                                   STATE_HOLDER1);
        final boolean result = tested.applyState(ShapeState.SELECTED);
        assertTrue(result);
        verify(view,
               times(1)).setAlpha(eq(1d));
        verify(view,
               times(1)).setFillColor(eq("#FF55AA"));
        verify(view,
               times(1)).setFillAlpha(eq(0.8d));
        verify(view,
               times(1)).setStrokeColor(eq("#0099FF"));
        verify(view,
               times(1)).setStrokeAlpha(eq(0.5d));
        verify(view,
               times(1)).setStrokeWidth(eq(5d));
    }

    @Test
    public void testState2() {
        tested.registerStateHolder(ShapeState.INVALID,
                                   STATE_HOLDER2);
        final boolean result = tested.applyState(ShapeState.INVALID);
        assertTrue(result);
        verify(view,
               times(1)).setAlpha(eq(1d));
        verify(view,
               times(0)).setFillColor(anyString());
        verify(view,
               times(1)).setFillAlpha(eq(0.4d));
        verify(view,
               times(1)).setStrokeColor(eq("#0099FF"));
        verify(view,
               times(0)).setStrokeAlpha(anyDouble());
        verify(view,
               times(0)).setStrokeWidth(anyDouble());
    }
}
