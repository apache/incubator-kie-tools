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


package org.kie.workbench.common.stunner.client.widgets.components.glyph;

import java.util.function.BiFunction;

import elemental2.dom.HTMLDivElement;
import io.crysknife.client.IsElement;
import io.crysknife.client.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.components.views.WidgetElementRendererView;
import org.kie.workbench.common.stunner.core.client.shape.ImageStrip;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripGlyph;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripRegistry;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.treblereel.j2cl.processors.common.resources.ImageResource;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ImageStripDOMGlyphRendererTest {

    private static final ImageStripGlyph GLYPH = ImageStripGlyph.create(ImageStripTestType.class,
            1);
    private static final int SIZE = 16;

    @Mock
    private ImageStripRegistry stripRegistry;

    @Mock
    private WidgetElementRendererView view;
    private ManagedInstance<WidgetElementRendererView> viewInstances;

    @Mock
    private BiFunction<String, Integer[], HTMLDivElement> panelBuilder;

    @Mock
    private ImageResource imageResource;

    private ImageStripDOMGlyphRenderer tested;
    private ImageStripTestType strip;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        strip = new ImageStripTestType(imageResource, "testCss");
        when(stripRegistry.get(any(Class.class))).thenReturn(strip);
        viewInstances = spy(new ManagedInstanceStub<>(view));
        tested = spy(new ImageStripDOMGlyphRenderer(stripRegistry,
                viewInstances,
                panelBuilder));
        doNothing().when(tested).inject(any(String.class));
    }

    @Test
    public void testGlyphType() {
        assertEquals(ImageStripGlyph.class, tested.getGlyphType());
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(viewInstances, times(1)).destroyAll();
    }

    @Test
    public void testRender() {
        IsElement rendered = tested.render(GLYPH,
                SIZE,
                SIZE);
        ArgumentCaptor<Integer[]> clipCaptor = ArgumentCaptor.forClass(Integer[].class);
        verify(panelBuilder, times(1)).apply(eq("testClass"),
                clipCaptor.capture());
        Integer[] clip = clipCaptor.getValue();
        assertEquals(SIZE, clip[0], 0);
        assertEquals(0, clip[1], 0);
        assertEquals(rendered, view);
    }

    @Test
    public void testBackGroundPosition() {
        final String backGroundPosition = ImageStripDOMGlyphRenderer.backGroundPosition(123, 321);
        assertEquals(backGroundPosition, "background-position: 123px 321px !important");
    }

    private static class ImageStripTestType implements ImageStrip {

        private final ImageResource imageResource;
        private final String cssResource;

        private ImageStripTestType(ImageResource imageResource,
                                   String cssResource) {
            this.imageResource = imageResource;
            this.cssResource = cssResource;
        }

        @Override
        public ImageResource getImage() {
            return imageResource;
        }

        @Override
        public StripCssResource getCss() {
            return new StripCssResource() {
                @Override
                public String getCssResource() {
                    return cssResource;
                }

                @Override
                public String getClassName() {
                    return "testClass";
                }
            };
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
            return Orientation.HORIZONTAL;
        }
    }
}
