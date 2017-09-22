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

package org.kie.workbench.common.stunner.client.lienzo.shape.animation;

import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.HasDecorators;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ShapeDecoratorAnimationTest {

    private static final String COLOR = "color1";
    private static final double STROKE_WIDTH = 14;
    private static final double STROKE_ALPHA = 0.44;

    @Mock
    private Shape<?> shape;

    @Mock
    private com.ait.lienzo.client.core.shape.Shape<?> decorator;

    private ShapeDecoratorAnimation tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        final ShapeView shapeView = new ShapeViewExtStub();
        ((HasDecorators) shapeView).getDecorators().add(decorator);
        when(shape.getShapeView()).thenReturn(shapeView);
        this.tested = new ShapeDecoratorAnimation(COLOR,
                                                  STROKE_WIDTH,
                                                  STROKE_ALPHA);
        this.tested.forShape(shape);
    }

    @Test
    public void testAnimate() {
        tested.run();
        final ArgumentCaptor<AnimationProperties> propertiesArgumentCaptor = ArgumentCaptor.forClass(AnimationProperties.class);
        verify(decorator,
               times(1)).animate(any(AnimationTweener.class),
                                 propertiesArgumentCaptor.capture(),
                                 anyDouble(),
                                 any(AnimationCallback.class));
        final AnimationProperties animationProperties = propertiesArgumentCaptor.getValue();
        assertEquals(3,
                     animationProperties.size());
    }
}
