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

package org.kie.workbench.common.stunner.client.lienzo.shape.impl;

import java.util.function.Function;
import java.util.function.Supplier;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ShapeStateDefaultHandlerTest {

    @Mock
    private ShapeStateAttributeAnimationHandler<LienzoShapeView<?>> handler;

    @Mock
    private ShapeStateAttributeHandler<LienzoShapeView<?>> delegateHandler;

    @Mock
    private LienzoShapeView<?> borderShape;

    @Mock
    private LienzoShapeView<?> backgroundShape;

    @Captor
    private ArgumentCaptor<Function<ShapeState, ShapeStateAttributeHandler.ShapeStateAttributes>> stateAttributesProviderCaptor;

    private ShapeStateDefaultHandler tested;
    private Command onComplete;

    @Before
    public void setup() throws Exception {
        doAnswer(invocationOnMock -> {
            ShapeStateDefaultHandlerTest.this.onComplete = (Command) invocationOnMock.getArguments()[0];
            return handler;
        }).when(handler).onComplete(any(Command.class));
        when(handler.getAttributesHandler()).thenReturn(delegateHandler);
        tested = new ShapeStateDefaultHandler(handler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectedStateStroke() {
        reset(delegateHandler);

        when(handler.getShapeState()).thenReturn(ShapeState.SELECTED);

        tested.setRenderType(ShapeStateDefaultHandler.RenderType.STROKE);

        verify(delegateHandler).useAttributes(stateAttributesProviderCaptor.capture());

        final Function<ShapeState, ShapeStateAttributeHandler.ShapeStateAttributes> stateAttributesProvider = stateAttributesProviderCaptor.getValue();
        final ShapeStateAttributeHandler.ShapeStateAttributes attributes = stateAttributesProvider.apply(ShapeState.SELECTED);

        assertNotNull(attributes.getValues().get(ShapeStateAttributeHandler.ShapeStateAttribute.STROKE_COLOR));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectedStateShadow() {
        when(handler.getShapeState()).thenReturn(ShapeState.SELECTED);
        tested.setBackgroundShape(() -> backgroundShape);
        assertNotNull(onComplete);
        this.onComplete.execute();
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
        tested.setBackgroundShape(() -> backgroundShape);
        assertNotNull(onComplete);
        this.onComplete.execute();
        verify(backgroundShape, never()).setShadow(anyString(),
                                                   anyInt(),
                                                   anyDouble(),
                                                   anyDouble());
        verify(backgroundShape, times(1)).removeShadow();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoneStateShadow() {
        when(handler.getShapeState()).thenReturn(ShapeState.NONE);
        tested.setBackgroundShape(() -> backgroundShape);
        assertNotNull(onComplete);
        this.onComplete.execute();
        verify(backgroundShape, times(1)).removeShadow();
        verify(backgroundShape, never()).setShadow(anyString(),
                                                   anyInt(),
                                                   anyDouble(),
                                                   anyDouble());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInvalidStateShadow() {
        when(handler.getShapeState()).thenReturn(ShapeState.INVALID);
        tested.setBackgroundShape(() -> backgroundShape);
        assertNotNull(onComplete);
        this.onComplete.execute();
        verify(backgroundShape, times(1)).removeShadow();
        verify(backgroundShape, never()).setShadow(anyString(),
                                                   anyInt(),
                                                   anyDouble(),
                                                   anyDouble());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetShapes() {
        tested.setBorderShape(() -> borderShape)
                .setBackgroundShape(() -> backgroundShape);
        ArgumentCaptor<Supplier> viewCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(delegateHandler, times(1)).setView(viewCaptor.capture());
        Supplier<LienzoShapeView<?>> viewSupplier = viewCaptor.getValue();
        assertEquals(borderShape, viewSupplier.get());
        assertEquals(backgroundShape, tested.getBackgroundShape());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReset() {
        tested.setBackgroundShape(() -> backgroundShape);
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
        ArgumentCaptor<Supplier> viewCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(delegateHandler, times(1)).setView(viewCaptor.capture());
        Supplier<LienzoShapeView<?>> viewSupplier = viewCaptor.getValue();
        assertEquals(borderShape, viewSupplier.get());
        verifyShapeTypeAttributeWasSet(borderShape, "shapeType=BORDER");
    }

    @Test
    public void testSetBackgroundShape() {
        tested.setBackgroundShape(() -> backgroundShape);
        verifyShapeTypeAttributeWasSet(backgroundShape, "shapeType=BACKGROUND");
    }

    private void verifyShapeTypeAttributeWasSet(LienzoShapeView<?> shape, String value) {
        ArgumentCaptor<Object> viewUserDataCaptor = ArgumentCaptor.forClass(Object.class);
        verify(shape).setUserData(viewUserDataCaptor.capture());
        assertTrue(viewUserDataCaptor.getValue().toString().endsWith(value));
    }
}
