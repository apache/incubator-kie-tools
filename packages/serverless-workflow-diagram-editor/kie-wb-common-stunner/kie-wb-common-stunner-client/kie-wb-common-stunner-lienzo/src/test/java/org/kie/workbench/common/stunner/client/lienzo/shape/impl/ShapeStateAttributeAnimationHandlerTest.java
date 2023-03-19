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

import java.util.Collections;

import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributesFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ShapeStateAttributeAnimationHandlerTest {

    @Mock
    private Command completeCallback;

    @Mock
    private Shape<?> shape;

    @Mock
    private IAnimationHandle animationHandle;

    @Mock
    private LienzoShapeView view;

    private ShapeStateAttributeAnimationHandler<LienzoShapeView> tested;

    @Before
    public void setup() throws Exception {
        when(view.getDecorators()).thenReturn(Collections.singletonList(shape));
        when(shape.animate(any(AnimationTweener.class),
                           any(AnimationProperties.class),
                           anyDouble(),
                           any(AnimationCallback.class)))
                .thenReturn(animationHandle);
        tested = new ShapeStateAttributeAnimationHandler<>();
        tested.getAttributesHandler().useAttributes(ShapeStateAttributesFactory::buildStateAttributes);
        tested.getAttributesHandler().setView(() -> view);
        tested.onComplete(completeCallback);
    }

    @Test
    public void testApplyStateAnimation() {
        tested.applyState(ShapeState.SELECTED);
        verify(shape, times(1)).animate(any(AnimationTweener.class),
                                        any(AnimationProperties.class),
                                        anyDouble(),
                                        any(AnimationCallback.class));
        tested.reset();
        verify(animationHandle, times(1)).stop();
        assertEquals(ShapeState.NONE, tested.getShapeState());
    }

    @Test
    public void testApplyStateAnimationWithResetBeforeAnimationCompletes() {
        final ArgumentCaptor<AnimationCallback> animationCallbackCaptor = ArgumentCaptor.forClass(AnimationCallback.class);
        tested.applyState(ShapeState.SELECTED);
        verify(shape, times(1)).animate(any(AnimationTweener.class),
                                        any(AnimationProperties.class),
                                        anyDouble(),
                                        animationCallbackCaptor.capture());
        tested.reset();

        final AnimationCallback animationCallback = animationCallbackCaptor.getValue();
        animationCallback.onClose(mock(IAnimation.class), mock(IAnimationHandle.class));
    }

    @Test
    public void testReset() {
        tested.setAnimationHandle(animationHandle);
        tested.reset();
        verify(shape, never()).animate(any(AnimationTweener.class),
                                       any(AnimationProperties.class),
                                       anyDouble(),
                                       any(AnimationCallback.class));
        verify(animationHandle, times(1)).stop();
        assertEquals(ShapeState.NONE, tested.getShapeState());
    }
}
