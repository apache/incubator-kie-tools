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

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.BoundsProvider;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class ScalablePanelTest
{
    private static final Bounds BOUNDS = Bounds.build(0d, 0d, 800d, 1200d);

    @Mock
    private LienzoPanel   lienzoPanel;

    @Mock
    private Layer         layer;

    @Mock
    private Viewport      viewport;

    private ScalablePanel tested;

    @Before
    public void setUp()
    {
        when(layer.getViewport()).thenReturn(viewport);
        when(lienzoPanel.getWidth()).thenReturn(300);
        when(lienzoPanel.getHeight()).thenReturn(150);
        this.tested = spy(new ScalablePanel(lienzoPanel,
                                            new BoundsProvider()
                                            {
                                                @Override
                                                public Bounds get(Layer layer)
                                                {
                                                    return BOUNDS;
                                                }
                                            }));
        tested.set(layer);
    }

    @Test
    public void testBatch()
    {
        tested.batch();
        verify(layer, times(1)).batch();
    }

    @Test
    public void testRefresh()
    {
        tested.refresh();
        ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);
        verify(viewport, times(1)).setTransform(transformArgumentCaptor.capture());
        Transform transform = transformArgumentCaptor.getValue();
        assertEquals(0.125d, transform.getScaleX(), 0);
        assertEquals(0.125d, transform.getScaleY(), 0);
        assertEquals(0d, transform.getTranslateX(), 0);
        assertEquals(0d, transform.getTranslateY(), 0);
        verify(layer, times(1)).batch();
    }
}
