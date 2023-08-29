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


package org.kie.workbench.common.stunner.client.lienzo.shape.animation;

import java.util.Collections;

import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.core.client.animation.AnimationHandle;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ShapeViewDecoratorAnimationTest {

    private static final String COLOR = "color1";
    private static final double STROKE_WIDTH = 14;
    private static final double STROKE_ALPHA = 0.44;

    @Mock
    private LienzoShapeView<?> shapeView;

    @Mock
    private com.ait.lienzo.client.core.shape.Shape<?> decorator;

    @Mock
    private IAnimationHandle decoratorAnimationHandle;

    private ShapeViewDecoratorAnimation tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(decorator.animate(any(AnimationTweener.class), any(AnimationProperties.class), anyDouble(), any(IAnimationCallback.class)))
                .thenReturn(decoratorAnimationHandle);
        when(shapeView.getDecorators()).thenReturn(Collections.singletonList(decorator));
        this.tested = new ShapeViewDecoratorAnimation(() -> shapeView,
                                                      COLOR,
                                                      STROKE_WIDTH,
                                                      STROKE_ALPHA);
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
        assertEquals(3, propertiesArgumentCaptor.getValue().size());
    }

    @Test
    public void testAnimationHandler() {
        final AnimationHandle handle = tested.run();
        handle.run();
        verify(decoratorAnimationHandle, times(1)).run();
        handle.stop();
        verify(decoratorAnimationHandle, times(1)).stop();
        handle.isRunning();
        verify(decoratorAnimationHandle, times(1)).isRunning();
    }
}
