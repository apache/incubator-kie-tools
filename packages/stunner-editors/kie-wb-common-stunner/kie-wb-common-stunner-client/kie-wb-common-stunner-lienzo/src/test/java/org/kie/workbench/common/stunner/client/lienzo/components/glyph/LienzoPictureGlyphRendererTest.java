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
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.safehtml.shared.SafeUri;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoPictureGlyphRendererTest {

    private static final String URI = "http://url";

    @Mock
    private SafeUri uri;

    @Mock
    private Picture picture;

    @Mock
    private BoundingBox boundingBox;

    @Mock
    private BiConsumer<String, Consumer<Picture>> pictureBuilder;

    private LienzoPictureGlyphRenderer tested;
    private ImageDataUriGlyph glyph;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(boundingBox.getX()).thenReturn(0d);
        when(boundingBox.getY()).thenReturn(0d);
        when(boundingBox.getHeight()).thenReturn(100d);
        when(boundingBox.getHeight()).thenReturn(100d);
        when(picture.getBoundingBox()).thenReturn(boundingBox);
        when(picture.asNode()).thenReturn((Node) picture);
        when(uri.asString()).thenReturn(URI);
        doAnswer(invocationOnMock -> {
            final Consumer<Picture> consumer = (Consumer<Picture>) invocationOnMock.getArguments()[1];
            consumer.accept(picture);
            return null;
        }).when(pictureBuilder).accept(anyString(),
                                       any(Consumer.class));
        this.glyph = ImageDataUriGlyph.create(uri);
        this.tested = new LienzoPictureGlyphRenderer(pictureBuilder);
    }

    @Test
    public void testType() {
        assertEquals(ImageDataUriGlyph.class,
                     tested.getGlyphType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRender() {
        final Group glyphView =
                tested.render(glyph,
                              100,
                              100);
        assertNotNull(glyphView);
        verify(pictureBuilder,
               times(1)).accept(eq("http://url"),
                                any(Consumer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        final Group glyphView =
                tested.render(glyph,
                              100,
                              100);
        assertNotNull(glyphView);
        glyphView.removeFromParent();
        verify(picture,
               times(1)).removeFromParent();
    }
}
