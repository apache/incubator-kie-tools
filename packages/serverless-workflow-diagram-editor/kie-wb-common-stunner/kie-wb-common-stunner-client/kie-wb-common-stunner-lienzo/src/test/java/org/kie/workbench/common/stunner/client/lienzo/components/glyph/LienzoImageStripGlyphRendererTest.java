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


package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import java.util.function.BiFunction;

import com.ait.lienzo.client.core.image.Image;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.j2cl.tools.processors.common.resources.ImageResource;
import org.kie.workbench.common.stunner.core.client.shape.ImageStrip;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripGlyph;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripRegistry;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoImageStripGlyphRendererTest {

    private static final ImageStripGlyph GLYPH = ImageStripGlyph.create(ImageStripTestType.class,
                                                                        0);
    private static final int SIZE = 16;

    @Mock
    private BiFunction<String, Integer, Image> imageBuilder;

    @Mock
    private Image image;

    private LienzoImageStripGlyphRenderer tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        doReturn(image).when(imageBuilder).apply(anyString(),
                                                 anyInt());
        when(image.getWidth()).thenReturn(16d);
        when(image.getHeight()).thenReturn(16d);
        when(image.asNode()).thenReturn(mock(Node.class));
        tested = new LienzoImageStripGlyphRenderer(imageBuilder);
    }

    @Test
    public void testGlyphType() {
        assertEquals(ImageStripGlyph.class, tested.getGlyphType());
    }

    @Test
    public void testRender() {
        tested.render(GLYPH,
                      SIZE,
                      SIZE);
        verify(imageBuilder, times(1))
                .apply(eq(ImageStripRegistry.getName(ImageStripTestType.class)),
                       eq(0));
    }

    private static class ImageStripTestType implements ImageStrip {

        @Override
        public ImageResource getImage() {
            return null;
        }

        @Override
        public StripCssResource getCss() {
            return null;
        }

        @Override
        public int getWide() {
            return SIZE;
        }

        @Override
        public int getHigh() {
            return SIZE;
        }

        @Override
        public int getPadding() {
            return 0;
        }

        @Override
        public Orientation getOrientation() {
            return null;
        }
    }
}
