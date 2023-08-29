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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ImageBitmapProxyTest {

    @Mock
    private JsImageBitmap image;

    @Mock
    private Context2D context;

    private ImageBitmapProxy tested;

    @Before
    public void init() {
        tested = spy(new ImageBitmapProxy(image));
    }

    @Test
    public void testDraw() {
        tested.draw(context);
        verify(context, times(1)).drawImage(eq(image),
                                            eq(0d),
                                            eq(0d));
    }

    @Test
    public void testIsLoaded() {
        assertTrue(tested.isLoaded());
    }

    @Test
    public void testDrawWithClipArea() {
        final ImageClipBounds clipBounds = new ImageClipBounds(0, 0, 5, 5, 10, 10);
        tested.draw(context,
                    clipBounds);
        verify(context, times(1)).drawImage(eq(image),
                                            eq(0d),
                                            eq(0d),
                                            eq(5d),
                                            eq(5d),
                                            eq(0d),
                                            eq(0d),
                                            eq(10d),
                                            eq(10d));
    }
}
