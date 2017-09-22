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

package org.kie.workbench.common.stunner.client.lienzo.shape.impl;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class AnimationShapeStateHelperTest {

    @Mock
    private Shape shape;

    @Mock
    private com.ait.lienzo.client.core.shape.Shape<?> decorator1;

    @Mock
    private com.ait.lienzo.client.core.shape.Shape<?> decorator2;

    private final ShapeViewExtStub shapeView = new ShapeViewExtStub();

    private AnimationShapeStateHelper tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(shape.getShapeView()).thenReturn(shapeView);
        shapeView.getDecorators().add(decorator1);
        shapeView.getDecorators().add(decorator2);
        this.tested = new AnimationShapeStateHelper(shape);
    }

    @Test
    public void testAnimate() {
        tested.applyActiveState("color1");
        verify(decorator1,
               times(1)).animate(any(AnimationTweener.class),
                                 any(AnimationProperties.class),
                                 anyDouble(),
                                 any(IAnimationCallback.class));
        verify(decorator2,
               times(1)).animate(any(AnimationTweener.class),
                                 any(AnimationProperties.class),
                                 anyDouble(),
                                 any(IAnimationCallback.class));
    }
}
