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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.gwtproject.safehtml.shared.SafeUri;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.util.SvgDataUriGenerator;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoSvgDataUriGlyphRendererTest {

    private static final String URI_B64 = "data:image/svg+xml;base64,c3ZnLWNvbnRlbnQ=";
    private static final SvgDataUriGenerator DATA_URI_UTIL = new SvgDataUriGenerator();

    @Mock
    private SafeUri uri;

    @Mock
    private BiConsumer<String, Consumer<Picture>> pictureBuilder;

    @Mock
    private Group group;

    private LienzoSvgDataUriGlyphRenderer tested;
    private SvgDataUriGlyph glyph;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(uri.asString()).thenReturn(URI_B64);
        this.glyph = SvgDataUriGlyph.Builder.build(uri);
        this.tested = new LienzoSvgDataUriGlyphRenderer(DATA_URI_UTIL,
                                                        pictureBuilder);
    }

    @Test
    public void testType() {
        assertEquals(SvgDataUriGlyph.class,
                     tested.getGlyphType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRender() {
        tested.render(glyph,
                      100,
                      200);
        final ArgumentCaptor<String> imageGlyphCaptor = ArgumentCaptor.forClass(String.class);
        verify(pictureBuilder,
               times(1)).accept(imageGlyphCaptor.capture(),
                                any(Consumer.class));
        assertEquals(URI_B64,
                     imageGlyphCaptor.getValue());
    }
}
