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

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.views.WidgetElementRendererView;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ElementShapeGlyphRendererTest {

    @Mock
    private LienzoGlyphRenderer<ShapeGlyph> lienzoShapeGlyphRenderer;

    @Mock
    private Supplier<WidgetElementRendererView> viewSupplier;

    @Mock
    private WidgetElementRendererView view;

    @Mock
    private Command viewDestroyer;

    private ElementShapeGlyphRenderer tested;

    @Before
    public void setup() throws Exception {
        when(viewSupplier.get()).thenReturn(view);
        when(lienzoShapeGlyphRenderer.render(any(ShapeGlyph.class),
                                             anyDouble(),
                                             anyDouble())).thenReturn(new Group());
        this.tested = new ElementShapeGlyphRenderer(lienzoShapeGlyphRenderer,
                                                    viewSupplier,
                                                    viewDestroyer);
    }

    @Test
    public void testType() {
        assertEquals(ShapeGlyph.class,
                     tested.getGlyphType());
    }

    @Test
    public void testRender() {
        final ShapeGlyph glyph = ShapeGlyph.create();
        tested.render(glyph,
                      100,
                      200);
        verify(viewSupplier,
               times(1)).get();
        verify(lienzoShapeGlyphRenderer,
               times(1)).render(eq(glyph),
                                eq(100d),
                                eq(200d));
        verify(view,
               times(1)).setWidget(any(LienzoPanel.class));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(viewDestroyer, times(1)).execute();
    }
}
