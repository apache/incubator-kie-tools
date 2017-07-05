/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.shape;

import java.util.function.Supplier;

import com.google.gwt.safehtml.shared.SafeUri;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GlyphTests {

    private static final String IMAGE_JPG_URI = "data:image/jpeg;base64,9j/4AAQSkZJRgABAQEASABIAAD";
    private static final String IMAGE_SVG_URI = "data:image/svg+xml;base64,c3ZnLWNvbnRlbnQ=";

    @Mock
    private SafeUri jpgUri;

    @Mock
    private SafeUri svgUri;

    @Mock
    private Supplier<ShapeFactory> factorySupplier;

    @Before
    public void setup() throws Exception {
        when(jpgUri.asString()).thenReturn(IMAGE_JPG_URI);
        when(svgUri.asString()).thenReturn(IMAGE_SVG_URI);
    }

    @Test
    public void testShapeGlyph() {
        final ShapeGlyph glyph = ShapeGlyph.create();
        glyph.setDefinitionId("def1");
        glyph.setFactorySupplier(factorySupplier);
        assertEquals("def1",
                     glyph.getDefinitionId());
        assertEquals(factorySupplier,
                     glyph.getFactorySupplier());
    }

    @Test
    public void testImageDataUriGlyph() {
        final ImageDataUriGlyph imageDataUriGlyph = ImageDataUriGlyph.create(jpgUri);
        assertEquals(jpgUri,
                     imageDataUriGlyph.getUri());
    }

    @Test
    public void testSvgDataUriGlyph() {
        final SvgDataUriGlyph svgDataUriGlyph = SvgDataUriGlyph.create(svgUri);
        assertEquals(svgUri,
                     svgDataUriGlyph.getUri());
    }
}
