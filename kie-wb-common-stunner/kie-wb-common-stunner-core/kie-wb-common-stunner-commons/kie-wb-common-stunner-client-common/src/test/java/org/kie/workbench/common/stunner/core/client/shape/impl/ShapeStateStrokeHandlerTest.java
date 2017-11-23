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

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShapeStateStrokeHandlerTest {

    private static final String COLOR = "color1";
    private static final double WIDTH = 10d;
    private static final double ALPHA = 0.6d;
    private static final Map<ShapeState, String> STATE_COLORS = new HashMap<ShapeState, String>() {{
        put(ShapeState.SELECTED, "selected");
        put(ShapeState.HIGHLIGHT, "highlight");
        put(ShapeState.INVALID, "invalid");
    }};

    @Mock
    private BiConsumer<Shape<ShapeView<?>>, ShapeStateStrokeHandler.ShapeStrokeState> strokeShapeStateHandler;

    @Mock
    private Shape<ShapeView<?>> shape;

    @Mock
    private ShapeView shapeView;

    private ShapeStateStrokeHandler<ShapeView<?>, Shape<ShapeView<?>>> tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(shape.getShapeView()).thenReturn(shapeView);
        when(shapeView.getStrokeColor()).thenReturn(COLOR);
        when(shapeView.getStrokeAlpha()).thenReturn(ALPHA);
        when(shapeView.getStrokeWidth()).thenReturn(WIDTH);
        this.tested = new ShapeStateStrokeHandler<>(strokeShapeStateHandler,
                                                    STATE_COLORS::get);
    }

    @Test
    public void testBindShape() {
        assertEquals(tested, tested.forShape(shape));
        assertEquals(shape, tested.getShape());
        assertEquals(ShapeState.NONE, tested.getShapeState());
        assertEquals(COLOR, tested.getStrokeState().getStrokeColor());
        assertEquals(ALPHA, tested.getStrokeState().getStrokeAlpha(), 0d);
        assertEquals(WIDTH, tested.getStrokeState().getStrokeWidth(), 9d);
    }

    @Test
    public void testApplySelectedState() {
        tested
                .forShape(shape)
                .applyState(ShapeState.SELECTED);
        final ArgumentCaptor<ShapeStateStrokeHandler.ShapeStrokeState> stateArgumentCaptor =
                ArgumentCaptor.forClass(ShapeStateStrokeHandler.ShapeStrokeState.class);
        verify(strokeShapeStateHandler, times(1)).accept(eq(shape),
                                                         stateArgumentCaptor.capture());
        final ShapeStateStrokeHandler.ShapeStrokeState state = stateArgumentCaptor.getValue();
        assertEquals("selected", state.getStrokeColor());
    }

    @Test
    public void testApplyHighlightState() {
        tested
                .forShape(shape)
                .applyState(ShapeState.HIGHLIGHT);
        final ArgumentCaptor<ShapeStateStrokeHandler.ShapeStrokeState> stateArgumentCaptor =
                ArgumentCaptor.forClass(ShapeStateStrokeHandler.ShapeStrokeState.class);
        verify(strokeShapeStateHandler, times(1)).accept(eq(shape),
                                                         stateArgumentCaptor.capture());
        final ShapeStateStrokeHandler.ShapeStrokeState state = stateArgumentCaptor.getValue();
        assertEquals("highlight", state.getStrokeColor());
    }

    @Test
    public void testApplyInvalidState() {
        tested
                .forShape(shape)
                .applyState(ShapeState.INVALID);
        final ArgumentCaptor<ShapeStateStrokeHandler.ShapeStrokeState> stateArgumentCaptor =
                ArgumentCaptor.forClass(ShapeStateStrokeHandler.ShapeStrokeState.class);
        verify(strokeShapeStateHandler, times(1)).accept(eq(shape),
                                                         stateArgumentCaptor.capture());
        final ShapeStateStrokeHandler.ShapeStrokeState state = stateArgumentCaptor.getValue();
        assertEquals("invalid", state.getStrokeColor());
    }
}
