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
package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.common.api.java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class PreviewLayerTest {

    private static final Bounds BG_BOUNDS = Bounds.build(0d, 0d, 800d, 1200d);

    private static final Bounds VISIBLE_BOUNDS = Bounds.build(0d, 0d, 234.3d, 654.5d);

    private PreviewLayer tested;

    @Before
    public void setUp() {
        this.tested = new PreviewLayer(new Supplier<Bounds>() {
            @Override
            public Bounds get() {
                return BG_BOUNDS;
            }
        },
                                       new Supplier<Bounds>() {
                                           @Override
                                           public Bounds get() {
                                               return VISIBLE_BOUNDS;
                                           }
                                       });
    }

    @Test
    public void testDraw() {
        Context2D context = mock(Context2D.class);
        BoundingBox bounds = mock(BoundingBox.class);
        tested.drawWithTransforms(context, 1, bounds);
        verify(context, times(1)).save();
        verify(context, times(1)).setGlobalAlpha(eq(PreviewLayer.ALPHA));
        verify(context, times(1)).setFillColor(eq(PreviewLayer.FILL_COLOR));
        verify(context, times(1)).fillRect(eq(0d), eq(0d), eq(800d), eq(1200d));
        verify(context, times(1)).clearRect(eq(0d), eq(0d), eq(234.3d), eq(654.5d));
        verify(context, times(1)).restore();
    }

    @Test
    public void testDrawButNothingToOverlap() {
        Context2D context = mock(Context2D.class);
        BoundingBox bounds = mock(BoundingBox.class);
        tested = new PreviewLayer(new Supplier<Bounds>() {
            @Override
            public Bounds get() {
                return BG_BOUNDS;
            }
        },
                                  new Supplier<Bounds>() {
                                      @Override
                                      public Bounds get() {
                                          return Bounds.empty();
                                      }
                                  });
        tested.drawWithTransforms(context, 1, bounds);
        verify(context, never()).save();
        verify(context, never()).setGlobalAlpha(anyDouble());
        verify(context, never()).setFillColor(anyString());
        verify(context, never()).fillRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        verify(context, never()).clearRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        verify(context, never()).restore();
    }
}
