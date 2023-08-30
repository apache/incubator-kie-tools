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


package org.kie.workbench.common.stunner.client.lienzo.shape.impl;

import java.util.function.BiConsumer;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ShapeStateDefaultHandlerTest {

    @Spy
    private ShapeStateAttributeHandler<ShapeView> handler;

    @Mock
    BiConsumer<ShapeView, ShapeStateAttributeHandler.ShapeStateAttributes> stateAttributesApplier;

    @Mock
    private ShapeView borderShape;

    @Mock
    private LienzoShapeView backgroundShape;

    private ShapeStateDefaultHandler tested;

    @Before
    public void setup() throws Exception {
        handler = spy(new ShapeStateAttributeHandler<>(stateAttributesApplier));
        tested = new ShapeStateDefaultHandler(handler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectedStateShadow() {
        when(handler.getShapeState()).thenReturn(ShapeState.SELECTED);
        tested.setBackgroundShape(backgroundShape);
        tested.applyState(ShapeState.SELECTED);
        verify(backgroundShape, times(1)).setShadow(anyString(),
                                                    anyInt(),
                                                    anyDouble(),
                                                    anyDouble());
        verify(backgroundShape, never()).removeShadow();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHighlightStateShadow() {
        when(handler.getShapeState()).thenReturn(ShapeState.HIGHLIGHT);
        tested.setBackgroundShape(backgroundShape);
        verify(backgroundShape, never()).setShadow(anyString(),
                                                   anyInt(),
                                                   anyDouble(),
                                                   anyDouble());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoneStateShadow() {
        when(handler.getShapeState()).thenReturn(ShapeState.NONE);
        tested.setBackgroundShape(backgroundShape);
        verify(backgroundShape, never()).setShadow(anyString(),
                                                   anyInt(),
                                                   anyDouble(),
                                                   anyDouble());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInvalidStateShadow() {
        when(handler.getShapeState()).thenReturn(ShapeState.INVALID);
        tested.setBackgroundShape(backgroundShape);
        verify(backgroundShape, never()).setShadow(anyString(),
                                                   anyInt(),
                                                   anyDouble(),
                                                   anyDouble());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReset() {
        tested.setBackgroundShape(backgroundShape);
        tested.reset();
        verify(backgroundShape, times(1)).removeShadow();
        verify(backgroundShape, never()).setShadow(anyString(),
                                                   anyInt(),
                                                   anyDouble(),
                                                   anyDouble());
    }

    @Test
    public void testSetBorderShape() {
        tested.setBorderShape(() -> borderShape);
        verifyShapeTypeAttributeWasSet(borderShape, "shapeType=BORDER");
    }

    @Test
    public void testSetBackgroundShape() {
        tested.setBackgroundShape(backgroundShape);
        verifyShapeTypeAttributeWasSet(backgroundShape, "shapeType=BACKGROUND");
    }

    private void verifyShapeTypeAttributeWasSet(ShapeView<?> shape, String value) {
        ArgumentCaptor<Object> viewUserDataCaptor = ArgumentCaptor.forClass(Object.class);
        verify(shape).setUserData(viewUserDataCaptor.capture());
        assertTrue(viewUserDataCaptor.getValue().toString().endsWith(value));
    }
}
