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
package com.ait.lienzo.client.widget.panel;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoBoundsPanelTest
{
    private static final Bounds BOUNDS = Bounds.relativeBox(300d, 500d);

    @Mock
    private LienzoPanel       lienzoPanel;

    @Mock
    private BoundsProvider    boundsProvider;

    @Mock
    private Runnable          refreshCommand;

    @Mock
    private Runnable          destroyCommand;

    @Mock
    private Layer             layer;

    @Mock
    private ScratchPad        scratchPad;

    private LienzoBoundsPanel tested;

    @Before
    public void setUp()
    {
        when(boundsProvider.get(eq(layer))).thenReturn(BOUNDS);
        when(layer.getScratchPad()).thenReturn(scratchPad);
        this.tested = new LienzoBoundsPanel(lienzoPanel,
                                            boundsProvider)
        {
            @Override
            public LienzoBoundsPanel onRefresh()
            {
                refreshCommand.run();
                return this;
            }

            @Override
            protected void doDestroy()
            {
                super.doDestroy();
                destroyCommand.run();
            }
        };
    }

    @Test
    public void testAddLayer()
    {
        tested.add(layer);
        assertEquals(layer, tested.getLayer());
    }

    @Test
    public void testSetLayer()
    {
        tested.set(layer);
        assertEquals(layer, tested.getLayer());
    }

    @Test(expected = IllegalStateException.class)
    public void testAddLayerTwiceUnsupported()
    {
        tested.add(layer);
        tested.add(mock(Layer.class));
    }

    @Test
    public void testSetBackgroundLayer()
    {
        Layer bgLayer = mock(Layer.class);
        tested.setBackgroundLayer(bgLayer);
        verify(lienzoPanel, times(1)).setBackgroundLayer(eq(bgLayer));
    }

    @Test
    public void testDefaultBounds()
    {
        Bounds defaultBounds = Bounds.empty();
        tested.setDefaultBounds(defaultBounds);
        assertEquals(defaultBounds, tested.getDefaultBounds());
    }

    @Test
    public void testSizeGetters()
    {
        when(lienzoPanel.getWidth()).thenReturn(300);
        when(lienzoPanel.getHeight()).thenReturn(100);
        assertEquals(300, tested.getWidth());
        assertEquals(100, tested.getHeight());
    }

    @Test
    public void testRefresh()
    {
        tested.set(layer);
        tested.refresh();
        Bounds bounds = tested.getBounds();
        assertEquals(BOUNDS.getX(), bounds.getX(), 0);
        assertEquals(BOUNDS.getY(), bounds.getY(), 0);
        assertEquals(BOUNDS.getWidth(), bounds.getWidth(), 0);
        assertEquals(BOUNDS.getHeight(), bounds.getHeight(), 0);
        verify(refreshCommand, times(1)).run();
        verify(scratchPad, times(1)).setPixelSize(eq(300), eq(500));
    }

    @Test
    public void testChangeDefaultBoundsAndRefresh()
    {
        tested.set(layer);
        tested.setDefaultBounds(Bounds.relativeBox(800d, 1200d));
        tested.refresh();
        verify(scratchPad, times(1)).setPixelSize(eq(800), eq(1200));
        Bounds bounds = tested.getBounds();
        assertEquals(0d, bounds.getX(), 0);
        assertEquals(0d, bounds.getY(), 0);
        assertEquals(800d, bounds.getWidth(), 0);
        assertEquals(1200d, bounds.getHeight(), 0);
        verify(refreshCommand, times(1)).run();
    }

    @Test
    public void testDestroy()
    {
        tested.set(layer);
        tested.destroy();
        verify(lienzoPanel, times(1)).destroy();
        verify(destroyCommand, times(1)).run();
        assertNull(tested.getLayer());
    }
}
