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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static com.ait.lienzo.client.core.image.ImageStrips.URL_PATTERN;
import static com.ait.lienzo.client.core.image.ImageStrips.URL_SEPARATOR;
import static com.ait.lienzo.client.core.image.ImageStrips.decodeURL;
import static com.ait.lienzo.client.core.image.ImageStrips.encodeURL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ImageStripsTest {

    private static final String STRIP_NAME = "stripName";
    private static final String STRIP_URL = "stripUrl";

    private static final ImageStrip STRIP = new ImageStrip(STRIP_NAME,
                                                           STRIP_URL,
                                                           16,
                                                           16,
                                                           5,
                                                           ImageStrip.Orientation.HORIZONTAL);

    @Mock
    private ImageElementProxy proxy;

    private ImageStrips instance;

    @Before
    public void init() {
        doAnswer(invocationOnMock -> {
            ((Runnable) invocationOnMock.getArguments()[1]).run();
            return null;
        }).when(proxy).load(anyString(),
                            any(Runnable.class));
        instance = new ImageStrips(() -> proxy);
    }

    @Test
    public void testRegister() {
        Runnable callback = mock(Runnable.class);
        instance.register(new ImageStrip[]{STRIP},
                          callback);
        verify(proxy, times(1)).load(eq(STRIP_URL),
                                     any(Runnable.class));
        assertNotNull(instance.get(STRIP_NAME));
    }

    @Test
    public void testNewProxy() {
        Runnable callback = mock(Runnable.class);
        instance.register(new ImageStrip[]{STRIP},
                          callback);
        ImageElementProxy aProxy = instance.newProxy(STRIP);
        assertNotNull(aProxy);
        assertTrue(aProxy instanceof ImageStrips.ImageElementProxyDelegate);
        ImageStrips.ImageElementProxyDelegate delegateProxy = (ImageStrips.ImageElementProxyDelegate) aProxy;
        assertEquals(proxy, delegateProxy.getDelegate());
    }

    @Test
    public void testRemove() {
        instance.register(new ImageStrip[]{STRIP},
                          mock(Runnable.class));
        instance.remove(STRIP);
        verify(proxy, times(1)).destroy();
        assertNull(instance.get(STRIP_NAME));
    }

    @Test
    public void testUrlEncodings() {
        Assert.assertTrue(ImageStrips.isURLValid(URL_PATTERN + STRIP_NAME));
        Assert.assertFalse(ImageStrips.isURLValid("data:xml"));
        Assert.assertEquals(URL_PATTERN + STRIP_NAME + URL_SEPARATOR + 1,
                            encodeURL(STRIP_NAME,
                                      1));
        String[] decoded = decodeURL(URL_PATTERN + STRIP_NAME + URL_SEPARATOR + 1);
        assertEquals(STRIP_NAME, decoded[0]);
        assertEquals("1", decoded[1]);
    }
}
