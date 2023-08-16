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

import com.ait.lienzo.client.core.shape.AbstractImageShape;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import elemental2.core.JsArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
@WithClassesToStub(JsArray.class)
public class ImageProxyTest {

    @Mock
    private ScratchPad normalImage;

    @Mock
    private ScratchPad filterImage;

    @Mock
    private ScratchPad selectImage;

    @Mock
    private AbstractImageShape imageShape;

    @Mock
    private JsImageBitmap image;

    private ImageProxy tested;

    @Before
    public void setup() {
        tested = new ImageProxy<>(imageShape,
                                  normalImage,
                                  filterImage,
                                  selectImage,
                                  image);

    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(imageShape, times(1)).removeFromParent();
        verify(normalImage, times(1)).clear();
        verify(filterImage, times(1)).clear();
        verify(selectImage, times(1)).clear();
        verify(image, times(1)).close();
        assertTrue(tested.getFilters().isEmpty());
    }
}