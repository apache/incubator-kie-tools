/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.components.glyph;

import java.util.function.Supplier;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.util.Base64Util;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.components.views.ImageElementRendererView;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.util.SvgDataUriGenerator;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SvgElementGlyphRendererTest {

    private static final String SVG_CONTENT = "svg-content";
    private static final String DATA_URI = "data:image/svg+xml;base64," +
            Base64Util.encode(SVG_CONTENT.getBytes(),
                              0,
                              SVG_CONTENT.length());
    private static final SvgDataUriGenerator DATA_URI_UTIL = new SvgDataUriGenerator();

    @Mock
    private SafeUri uri;

    @Mock
    private Supplier<ImageElementRendererView> viewSupplier;

    @Mock
    private Command viewDestroyer;

    @Mock
    private ImageElementRendererView view;

    private SvgElementGlyphRenderer tested;

    @Before
    public void setup() throws Exception {
        when(uri.asString()).thenReturn(DATA_URI);
        when(viewSupplier.get()).thenReturn(view);
        this.tested = new SvgElementGlyphRenderer(DATA_URI_UTIL,
                                                  viewSupplier,
                                                  viewDestroyer);
    }

    @Test
    public void testType() {
        assertEquals(SvgDataUriGlyph.class,
                     tested.getGlyphType());
    }

    @Test
    public void testRender() {
        final SvgDataUriGlyph glyph = SvgDataUriGlyph.Builder.build(uri);
        tested.render(glyph,
                      100,
                      200);
        verify(viewSupplier,
               times(1)).get();
        verify(view,
               times(1)).setDOMContent(eq(SVG_CONTENT),
                                       eq(100),
                                       eq(200));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSvgDataUriGlyphWrong() {
        when(uri.asString()).thenReturn("someBadUri::d");
        tested.render(SvgDataUriGlyph.Builder.build(uri),
                      100,
                      200);
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(viewDestroyer, times(1)).execute();
    }
}
