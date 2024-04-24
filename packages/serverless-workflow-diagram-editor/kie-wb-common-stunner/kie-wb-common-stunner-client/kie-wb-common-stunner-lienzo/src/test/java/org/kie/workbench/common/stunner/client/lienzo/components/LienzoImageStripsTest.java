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


package org.kie.workbench.common.stunner.client.lienzo.components;

import com.ait.lienzo.client.core.image.ImageStrips;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeUri;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.j2cl.tools.processors.common.resources.ImageResource;
import org.kie.workbench.common.stunner.core.client.shape.ImageStrip;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoImageStripsTest {

    private static final String STRIP_NAME = Object.class.getName();

    @Mock
    private ImageResource imageResource;

    @Mock
    private SafeUri safeUri;

    @Mock
    private CssResource cssResource;

    @Mock
    private ImageStrips imageStrips;

    private LienzoImageStrips tested;

    @Before
    public void setUp() {
        when(safeUri.asString()).thenReturn("someUri");
        when(imageResource.getSrc()).thenReturn("someUri");
        doAnswer(invocationOnMock -> {
            ((Runnable) invocationOnMock.getArguments()[1]).run();
            return null;
        }).when(imageStrips)
                .register(any(com.ait.lienzo.client.core.image.ImageStrip[].class), any(Runnable.class));
        final com.ait.lienzo.client.core.image.ImageStrip imageStrip = mock(com.ait.lienzo.client.core.image.ImageStrip.class);
        when(imageStrip.getName()).thenReturn(STRIP_NAME);
        when(imageStrips.get(STRIP_NAME)).thenReturn(imageStrip);
        tested = new LienzoImageStrips(imageStrips);
    }

    @Test
    public void testRegister() {
        Command callback = mock(Command.class);
        tested.register(STRIPS, callback);
        ArgumentCaptor<com.ait.lienzo.client.core.image.ImageStrip[]> stripsCaptor =
                ArgumentCaptor.forClass(com.ait.lienzo.client.core.image.ImageStrip[].class);
        verify(imageStrips, times(1)).register(stripsCaptor.capture(), any(Runnable.class));
        com.ait.lienzo.client.core.image.ImageStrip[] strips = stripsCaptor.getValue();
        assertStripsRegistered(strips);
        assertEquals(1, tested.getRegistered().size());
        assertEquals(1, tested.getRegistered().values().iterator().next().intValue());
    }

    @Test
    public void testRemoveFromLienzo() {
        tested.removeFromLienzo(STRIP_NAME);
        verify(imageStrips, times(1)).remove(STRIP_NAME);
        //testing null imageStrip
        reset(imageStrips);
        tested.removeFromLienzo(STRIP_NAME);
        verify(imageStrips, never()).remove(STRIP_NAME);
    }

    @Test
    public void testRegisterAndRemove() {
        Command callback = mock(Command.class);
        tested.register(STRIPS, callback);
        ArgumentCaptor<com.ait.lienzo.client.core.image.ImageStrip[]> stripsCaptor =
                ArgumentCaptor.forClass(com.ait.lienzo.client.core.image.ImageStrip[].class);
        verify(imageStrips, times(1)).register(stripsCaptor.capture(), any(Runnable.class));
        com.ait.lienzo.client.core.image.ImageStrip[] strips = stripsCaptor.getValue();
        assertStripsRegistered(strips);
        assertEquals(1, tested.getRegistered().size());
        assertEquals(1, tested.getRegistered().values().iterator().next().intValue());
        tested.remove(STRIPS);
        verify(imageStrips, times(1)).remove(STRIP_NAME);
        assertTrue(tested.getRegistered().isEmpty());
    }

    @Test
    public void testRegisterTwice() {
        Command callback = mock(Command.class);
        tested.register(STRIPS, callback);
        tested.register(STRIPS, callback);
        ArgumentCaptor<com.ait.lienzo.client.core.image.ImageStrip[]> stripsCaptor =
                ArgumentCaptor.forClass(com.ait.lienzo.client.core.image.ImageStrip[].class);
        verify(imageStrips, times(1)).register(stripsCaptor.capture(), any(Runnable.class));
        com.ait.lienzo.client.core.image.ImageStrip[] strips = stripsCaptor.getValue();
        assertStripsRegistered(strips);
        assertEquals(1, tested.getRegistered().size());
        assertEquals(2, tested.getRegistered().values().iterator().next().intValue());
    }

    @Test
    public void testRegisterTwiceAndRemoveOnce() {
        Command callback = mock(Command.class);
        tested.register(STRIPS, callback);
        tested.register(STRIPS, callback);
        ArgumentCaptor<com.ait.lienzo.client.core.image.ImageStrip[]> stripsCaptor =
                ArgumentCaptor.forClass(com.ait.lienzo.client.core.image.ImageStrip[].class);
        verify(imageStrips, times(1)).register(stripsCaptor.capture(), any(Runnable.class));
        com.ait.lienzo.client.core.image.ImageStrip[] strips = stripsCaptor.getValue();
        assertStripsRegistered(strips);
        assertEquals(1, tested.getRegistered().size());
        assertEquals(2, tested.getRegistered().values().iterator().next().intValue());
        tested.remove(STRIPS);
        verify(imageStrips, never()).remove(STRIP_NAME);
        assertEquals(1, tested.getRegistered().size());
        assertEquals(1, tested.getRegistered().values().iterator().next().intValue());
    }

    @Test
    public void testDestroy() {
        tested.register(STRIPS, mock(Command.class));
        tested.register(STRIPS, mock(Command.class));
        assertEquals(1, tested.getRegistered().size());
        assertEquals(2, tested.getRegistered().values().iterator().next().intValue());
        tested.destroy();
        verify(imageStrips, times(1)).remove(STRIP_NAME);
        assertTrue(tested.getRegistered().isEmpty());
    }

    private void assertStripsRegistered(final com.ait.lienzo.client.core.image.ImageStrip[] strips) {
        assertNotNull(strips);
        assertEquals(1, strips.length);
        assertEquals("someUri", strips[0].getUrl());
        assertEquals(16, strips[0].getWide());
        assertEquals(16, strips[0].getHigh());
        assertEquals(5, strips[0].getPadding());
        assertEquals(com.ait.lienzo.client.core.image.ImageStrip.Orientation.HORIZONTAL, strips[0].getOrientation());
    }

    private ImageStrip.StripCssResource STRIP_CSS = new ImageStrip.StripCssResource() {
        @Override
        public String getCssResource() {
            return "testClass";
        }

        @Override
        public String getClassName() {
            return "testClass";
        }
    };

    private ImageStrip STRIP = new ImageStrip() {


        @Override
        public ImageResource getImage() {
            return imageResource;
        }

        @Override
        public StripCssResource getCss() {
            return STRIP_CSS;
        }

        @Override
        public int getWide() {
            return 16;
        }

        @Override
        public int getHigh() {
            return 16;
        }

        @Override
        public int getPadding() {
            return 5;
        }

        @Override
        public Orientation getOrientation() {
            return Orientation.HORIZONTAL;
        }
    };

    private ImageStrip[] STRIPS = new ImageStrip[]{STRIP};
}
