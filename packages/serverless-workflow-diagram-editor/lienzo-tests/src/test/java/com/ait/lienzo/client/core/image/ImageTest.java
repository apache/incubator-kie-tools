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


package com.ait.lienzo.client.core.image;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ImageTest {

    private final static ImageStrip STRIP = new ImageStrip("stripTest",
                                                           "urlTest",
                                                           10,
                                                           20,
                                                           5,
                                                           ImageStrip.Orientation.HORIZONTAL);

    @Mock
    private ImageBitmapProxy proxy;

    private Image tested;

    @Before
    public void init() {
        tested = new Image();
        tested.imageProxy = proxy;
    }

    @Test
    public void testClippingAttributes() {
        tested.setClippedImageStartX(7);
        tested.setClippedImageStartY(8);
        tested.setClippedImageWidth(11);
        tested.setClippedImageHeight(21);
        tested.setClippedImageDestinationWidth(44);
        tested.setClippedImageDestinationHeight(67);
        assertEquals(7, tested.getClippedImageStartX());
        assertEquals(8, tested.getClippedImageStartY());
        assertEquals(11, tested.getClippedImageWidth());
        assertEquals(21, tested.getClippedImageHeight());
        assertEquals(44, tested.getClippedImageDestinationWidth());
        assertEquals(67, tested.getClippedImageDestinationHeight());
    }

    @Test
    public void testConfigureForImageStrip() {
        ImageStrips.get().registerStrip(STRIP,
                                        proxy);
        tested.setClippedImageStartX(1);
        tested.setClippedImageStartY(2);
        tested.setClippedImageWidth(3);
        tested.setClippedImageHeight(4);
        tested.setClippedImageDestinationWidth(5);
        tested.setClippedImageDestinationHeight(6);
        // Should destroy current proxy instance, create a new delegate proxy
        // and configure the clipping area as for the index.
        tested.configure(ImageStrips.encodeURL(STRIP.getName(), 0));
        assertNotNull(tested.imageProxy);
        assertNotEquals(proxy, tested.imageProxy);
        assertTrue(tested.imageProxy instanceof ImageStrips.ImageElementProxyDelegate);
        assertEquals(proxy, ((ImageStrips.ImageElementProxyDelegate) tested.imageProxy).getDelegate());
        verify(proxy, times(1)).destroy();
        assertEquals(0, tested.getClippedImageStartX(), 0);
        assertEquals(0, tested.getClippedImageStartY(), 0);
        assertEquals(0, tested.getClippedImageWidth(), 10);
        assertEquals(0, tested.getClippedImageHeight(), 20);
        assertEquals(0, tested.getClippedImageDestinationWidth(), 10);
        assertEquals(0, tested.getClippedImageDestinationHeight(), 20);
    }

    @Test
    public void testConfigureSomeUrl() {
        tested.setClippedImageStartX(1);
        tested.setClippedImageStartY(2);
        tested.setClippedImageWidth(3);
        tested.setClippedImageHeight(4);
        tested.setClippedImageDestinationWidth(5);
        tested.setClippedImageDestinationHeight(6);
        // Should destroy current proxy instance,
        // create a new one and restore the clipping area.
        tested.configure("someUrl");
        assertNotNull(tested.imageProxy);
        assertNotEquals(proxy, tested.imageProxy);
        verify(proxy, times(1)).destroy();
        assertEquals(0, tested.getClippedImageStartX(), 0);
        assertEquals(0, tested.getClippedImageStartY(), 0);
        assertEquals(0, tested.getClippedImageWidth(), 0);
        assertEquals(0, tested.getClippedImageHeight(), 0);
        assertEquals(0, tested.getClippedImageDestinationWidth(), 0);
        assertEquals(0, tested.getClippedImageDestinationHeight(), 0);
    }

    @Test
    public void testLoad() {
        doReturn(false).when(proxy).isLoaded();
        doAnswer(invocationOnMock -> {
            ((Runnable) invocationOnMock.getArguments()[1]).run();
            return null;
        }).when(proxy).load(anyString(),
                            any(Runnable.class));
        final String url = "anotherUrl";
        tested.setURL(url);
        final ImageLoadCallback callback = mock(ImageLoadCallback.class);
        tested.load(callback);
        verify(proxy, times(1)).load(eq(url),
                                     any(Runnable.class));
        verify(callback, times(1)).onImageLoaded(eq(tested));
    }

    @Test
    public void testAlreadyLoaded() {
        doReturn(true).when(proxy).isLoaded();
        final String url = "anotherUrl";
        tested.setURL(url);
        final ImageLoadCallback callback = mock(ImageLoadCallback.class);
        tested.load(callback);
        verify(proxy, never()).load(anyString(),
                                    any(Runnable.class));
        verify(callback, times(1)).onImageLoaded(eq(tested));
    }

    @Test
    public void testDrawImage() {
        final Context2D context = mock(Context2D.class);
        doReturn(true).when(proxy).isLoaded();
        doReturn(false).when(context).isSelection();
        tested.drawImage(context);
        verify(proxy, times(1)).draw(eq(context),
                                     any(ImageClipBounds.class));
    }

    @Test
    public void testDrawSelectionContext() {
        final Context2D context = mock(Context2D.class);
        doReturn(true).when(context).isSelection();
        doReturn(true).when(proxy).isLoaded();
        doReturn(10).when(proxy).getWidth();
        doReturn(20).when(proxy).getHeight();
        final Image spied = spy(tested);
        doReturn("#000000").when(spied).getColorKey();
        spied.drawImage(context);
        verify(proxy, never()).draw(eq(context),
                                    any(ImageClipBounds.class));
        verify(context, times(1)).save();
        verify(context, times(1)).setFillColor(anyString());
        verify(context, times(1)).fillRect(eq(0d),
                                           eq(0d),
                                           eq(10d),
                                           eq(20d));
        verify(context, times(1)).restore();
    }
}
