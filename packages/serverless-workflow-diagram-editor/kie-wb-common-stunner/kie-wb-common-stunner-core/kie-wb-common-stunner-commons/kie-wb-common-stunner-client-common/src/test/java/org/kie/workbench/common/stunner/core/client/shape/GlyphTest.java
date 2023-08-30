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


package org.kie.workbench.common.stunner.core.client.shape;

import java.util.Collection;
import java.util.function.Supplier;

import com.google.gwt.safehtml.shared.SafeUri;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class GlyphTest {

    private static final String IMAGE_JPG_URI = "data:image/jpeg;base64,9j/4AAQSkZJRgABAQEASABIAAD";
    private static final String IMAGE_SVG_URI = "data:image/svg+xml;base64,c3ZnLWNvbnRlbnQ=";

    @Mock
    private SafeUri jpgUri;

    @Mock
    private SafeUri svgUri;

    @Mock
    private SafeUri svgUri2;

    @Mock
    private SafeUri svgUri3;

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
        final SvgDataUriGlyph svgDataUriGlyph = SvgDataUriGlyph.Builder.build(svgUri);
        assertEquals(svgUri,
                     svgDataUriGlyph.getSvg());
        assertTrue(svgDataUriGlyph.getDefs().isEmpty());
        assertTrue(svgDataUriGlyph.getValidUseRefIds().isEmpty());
    }

    @Test
    public void testSvgDataUriGlyphComposite() {
        final SvgDataUriGlyph.Builder builder = SvgDataUriGlyph.Builder.create()
                .setUri(svgUri)
                .addUri("uri2",
                        svgUri2)
                .addUri("uri3",
                        svgUri3);
        final SvgDataUriGlyph svgDataUriGlyph = builder.build();
        final Collection<SafeUri> defs = svgDataUriGlyph.getDefs();
        final Collection<String> ids = svgDataUriGlyph.getValidUseRefIds();
        assertEquals(svgUri,
                     svgDataUriGlyph.getSvg());
        assertEquals(2,
                     defs.size());
        assertEquals(2,
                     ids.size());
        assertTrue(defs.contains(svgUri2));
        assertTrue(defs.contains(svgUri3));
        assertTrue(ids.contains("uri2"));
        assertTrue(ids.contains("uri3"));
    }

    @Test
    public void testSvgDataUriGlyphFilterComposite() {
        final SvgDataUriGlyph.Builder builder = SvgDataUriGlyph.Builder.create()
                .setUri(svgUri)
                .addUri("uri2",
                        svgUri2)
                .addUri("uri3",
                        svgUri3);
        final SvgDataUriGlyph svgDataUriGlyph1 = builder.build("uri3");
        final Collection<SafeUri> defs1 = svgDataUriGlyph1.getDefs();
        final Collection<String> ids1 = svgDataUriGlyph1.getValidUseRefIds();
        assertEquals(svgUri,
                     svgDataUriGlyph1.getSvg());
        assertEquals(1,
                     defs1.size());
        assertEquals(1,
                     defs1.size());
        assertTrue(defs1.contains(svgUri3));
        assertTrue(ids1.contains("uri3"));
        final SvgDataUriGlyph svgDataUriGlyph2 = builder.build("uri2");
        final Collection<SafeUri> defs2 = svgDataUriGlyph2.getDefs();
        final Collection<String> ids2 = svgDataUriGlyph2.getValidUseRefIds();
        assertEquals(svgUri,
                     svgDataUriGlyph2.getSvg());
        assertEquals(1,
                     defs2.size());
        assertEquals(1,
                     defs2.size());
        assertTrue(defs2.contains(svgUri2));
        assertTrue(ids2.contains("uri2"));
    }
}
