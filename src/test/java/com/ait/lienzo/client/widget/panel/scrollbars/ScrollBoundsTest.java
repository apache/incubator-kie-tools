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
package com.ait.lienzo.client.widget.panel.scrollbars;

import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScrollBoundsTest
{
    @Mock
    private ScrollablePanelHandler panelHandler;

    @Mock
    private ScrollablePanel        panel;

    private ScrollBounds           tested;

    private Bounds                 bounds;

    @Before
    public void setUp()
    {
        bounds = Bounds.build(12.5d, 44d, 235.6d, 876.5d);
        this.tested = new ScrollBounds(panelHandler);
        when(panelHandler.getPanel()).thenReturn(panel);
        when(panel.getBounds()).thenReturn(bounds);
    }

    @Test
    public void testMaxBoundX()
    {
        assertEquals(248.1d, tested.maxBoundX(), 0);
    }

    @Test
    public void testMaxBoundY()
    {
        assertEquals(920.5d, tested.maxBoundY(), 0);
    }

    @Test
    public void testMinBoundX()
    {
        assertEquals(0d, tested.minBoundX(), 0);
    }

    @Test
    public void testMinBoundY()
    {
        assertEquals(0d, tested.minBoundY(), 0);
    }

    @Test
    public void testMinBoundXNegative()
    {
        bounds.setX(-34.3d);
        assertEquals(-34.3d, tested.minBoundX(), 0);
    }

    @Test
    public void testMinBoundYNegative()
    {
        bounds.setY(-56.3d);
        assertEquals(-56.3d, tested.minBoundY(), 0);
    }
}
