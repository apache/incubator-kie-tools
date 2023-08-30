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


package org.kie.workbench.common.stunner.core.client.shape.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler.ShapeStateAttribute;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler.ShapeStateAttributes;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShapeStateAttributeHandlerTest {

    private static final String FILL = "fill";
    private static final double FILL_ALPHA = 0.1d;
    private static final String STROKE = "stroke";
    private static final double STROKE_ALPHA = 0.2d;
    private static final double STROKE_WIDTH = 0.3d;

    private static final Map<ShapeState, ShapeStateAttributes> STATE_ATTRIBUTES =
            new HashMap<ShapeState, ShapeStateAttributes>() {{
                put(ShapeState.SELECTED,
                    new ShapeStateAttributes()
                            .set(ShapeStateAttribute.FILL_COLOR, "selectedFill"));
                put(ShapeState.INVALID,
                    new ShapeStateAttributes()
                            .set(ShapeStateAttribute.FILL_COLOR, "invalidFill"));
                put(ShapeState.HIGHLIGHT,
                    new ShapeStateAttributes()
                            .set(ShapeStateAttribute.FILL_COLOR, "highlightFill"));
            }};
    @Mock
    private BiConsumer<ShapeView<?>, ShapeStateAttributes> stateAttributesApplier;

    @Mock
    private ShapeView<?> view;

    private ShapeStateAttributeHandler<ShapeView<?>> tested;
    private Supplier<ShapeView<?>> viewSupplier;

    @Before
    public void setup() throws Exception {
        Function<ShapeState, ShapeStateAttributes> stateAttributesProvider = STATE_ATTRIBUTES::get;
        viewSupplier = () -> view;
        when(view.getFillColor()).thenReturn(FILL);
        when(view.getFillAlpha()).thenReturn(FILL_ALPHA);
        when(view.getStrokeColor()).thenReturn(STROKE);
        when(view.getStrokeAlpha()).thenReturn(STROKE_ALPHA);
        when(view.getStrokeWidth()).thenReturn(STROKE_WIDTH);
        tested = new ShapeStateAttributeHandler<>(stateAttributesApplier)
                .useAttributes(stateAttributesProvider);
    }

    @Test
    public void testSetView() {
        // Cascade pattern & method call.
        assertEquals(tested,
                     tested.setView(viewSupplier));
        assertEquals(view, tested.getShapeView());
        // Ensure current state is being saved.
        assertEquals(ShapeState.NONE, tested.getShapeState());
        assertEquals(FILL, tested.getStateHolder().getValues().get(ShapeStateAttribute.FILL_COLOR));
        assertEquals(FILL_ALPHA, tested.getStateHolder().getValues().get(ShapeStateAttribute.FILL_ALPHA));
        assertEquals(STROKE, tested.getStateHolder().getValues().get(ShapeStateAttribute.STROKE_COLOR));
        assertEquals(STROKE_ALPHA, tested.getStateHolder().getValues().get(ShapeStateAttribute.STROKE_ALPHA));
        assertEquals(STROKE_WIDTH, tested.getStateHolder().getValues().get(ShapeStateAttribute.STROKE_WIDTH));
    }

    @Test
    public void testShapeChanged() {
        // Cascade pattern & method call.
        tested.setViewSupplier(viewSupplier);
        assertEquals(tested,
                     tested.shapeAttributesChanged());
        // Ensure current state is being saved.
        assertEquals(ShapeState.NONE, tested.getShapeState());
        assertEquals(FILL, tested.getStateHolder().getValues().get(ShapeStateAttribute.FILL_COLOR));
        assertEquals(FILL_ALPHA, tested.getStateHolder().getValues().get(ShapeStateAttribute.FILL_ALPHA));
        assertEquals(STROKE, tested.getStateHolder().getValues().get(ShapeStateAttribute.STROKE_COLOR));
        assertEquals(STROKE_ALPHA, tested.getStateHolder().getValues().get(ShapeStateAttribute.STROKE_ALPHA));
        assertEquals(STROKE_WIDTH, tested.getStateHolder().getValues().get(ShapeStateAttribute.STROKE_WIDTH));
    }

    @Test
    public void testApplySelectedState() {
        tested.setViewSupplier(viewSupplier);
        tested.applyState(ShapeState.SELECTED);
        assertEquals(ShapeState.SELECTED, tested.getShapeState());
        ArgumentCaptor<ShapeStateAttributes> argumentCaptor = ArgumentCaptor.forClass(ShapeStateAttributes.class);
        verify(stateAttributesApplier, times(1)).accept(eq(view),
                                                        argumentCaptor.capture());
        ShapeStateAttributes attributes = argumentCaptor.getValue();
        assertEquals("selectedFill", attributes.getValues().get(ShapeStateAttribute.FILL_COLOR));
    }

    @Test
    public void testApplyInvalidState() {
        tested.setViewSupplier(viewSupplier);
        tested.applyState(ShapeState.INVALID);
        assertEquals(ShapeState.INVALID, tested.getShapeState());
        ArgumentCaptor<ShapeStateAttributes> argumentCaptor = ArgumentCaptor.forClass(ShapeStateAttributes.class);
        verify(stateAttributesApplier, times(1)).accept(eq(view),
                                                        argumentCaptor.capture());
        ShapeStateAttributes attributes = argumentCaptor.getValue();
        assertEquals("invalidFill", attributes.getValues().get(ShapeStateAttribute.FILL_COLOR));
    }

    @Test
    public void testApplyHighlightState() {
        tested.setViewSupplier(viewSupplier);
        tested.applyState(ShapeState.HIGHLIGHT);
        assertEquals(ShapeState.HIGHLIGHT, tested.getShapeState());
        ArgumentCaptor<ShapeStateAttributes> argumentCaptor = ArgumentCaptor.forClass(ShapeStateAttributes.class);
        verify(stateAttributesApplier, times(1)).accept(eq(view),
                                                        argumentCaptor.capture());
        ShapeStateAttributes attributes = argumentCaptor.getValue();
        assertEquals("highlightFill", attributes.getValues().get(ShapeStateAttribute.FILL_COLOR));
    }

    @Test
    public void testReset() {
        tested.setView(viewSupplier);
        tested.reset();
        assertEquals(ShapeState.NONE, tested.getShapeState());
        verify(view, times(1)).setFillColor(eq(FILL));
        verify(view, times(1)).setFillAlpha(eq(FILL_ALPHA));
        verify(view, times(1)).setStrokeColor(eq(STROKE));
        verify(view, times(1)).setStrokeAlpha(eq(STROKE_ALPHA));
        verify(view, times(1)).setStrokeWidth(eq(STROKE_WIDTH));
    }
}
