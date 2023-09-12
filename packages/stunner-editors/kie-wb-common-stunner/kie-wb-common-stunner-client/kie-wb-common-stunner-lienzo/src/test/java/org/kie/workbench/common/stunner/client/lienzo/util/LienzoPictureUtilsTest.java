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


package org.kie.workbench.common.stunner.client.lienzo.util;

import com.ait.lienzo.client.core.image.ImageProxy;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwtmockito.WithClassesToStub;
import elemental2.dom.HTMLImageElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WithClassesToStub({ImageElement.class})
@RunWith(LienzoMockitoTestRunner.class)
public class LienzoPictureUtilsTest {

    @Mock
    private Picture picture;

    @Mock
    private ImageProxy imageProxy;

    @Mock
    private HTMLImageElement imageElement;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        //Fake image loading...
        when(picture.getImageProxy()).thenReturn(imageProxy);
        when(imageProxy.getImage()).thenReturn(imageElement);
    }

    @Test
    public void checkDestructionRemovesResourcesFromDOMWhenPictureIsLoaded() {
        when(picture.isLoaded()).thenReturn(true);

        LienzoPictureUtils.tryDestroy(picture,
                                      LienzoPictureUtils::retryDestroy);

        verify(picture).removeFromParent();

        verify(imageElement).remove();
    }

    @Test
    public void checkDestructionRemovesResourcesFromDOMWhenPictureLoadedIsDelayed() {
        when(picture.isLoaded()).thenReturn(false);

        LienzoPictureUtils.tryDestroy(picture,
                                      (p) -> {
                                          when(picture.isLoaded()).thenReturn(true);
                                          LienzoPictureUtils.retryDestroy(p);
                                      });

        verify(picture).removeFromParent();
        verify(imageElement).remove();
    }
}
