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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.components.views.ImageElementRendererView;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ImageElementGlyphRendererTest {

    private static final String DATA_URI = "data:image/jpeg;base64,9j/4AAQSkZJRgABAQEASABIAAD";

    @Mock
    private SafeUri uri;

    @Mock
    private Supplier<ImageElementRendererView> viewSupplier;

    @Mock
    private ImageElementRendererView view;

    @Mock
    private Command viewDestroyer;

    private ImageElementGlyphRenderer tested;

    @Before
    public void setup() throws Exception {
        when(uri.asString()).thenReturn(DATA_URI);
        when(viewSupplier.get()).thenReturn(view);
        this.tested = new ImageElementGlyphRenderer(viewSupplier,
                                                    viewDestroyer);
    }

    @Test
    public void testType() {
        assertEquals(ImageDataUriGlyph.class,
                     tested.getGlyphType());
    }

    @Test
    public void testRender() {
        final ImageDataUriGlyph glyph = ImageDataUriGlyph.create(uri);
        tested.render(glyph,
                      100,
                      200);
        verify(viewSupplier,
               times(1)).get();
        verify(view,
               times(1)).setImage(eq(uri),
                                  eq(100),
                                  eq(200));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(viewDestroyer, times(1)).execute();
    }
}
