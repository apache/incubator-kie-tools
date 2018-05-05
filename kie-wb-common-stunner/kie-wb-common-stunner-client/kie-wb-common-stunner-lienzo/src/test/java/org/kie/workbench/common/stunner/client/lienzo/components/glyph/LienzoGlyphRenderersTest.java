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

package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LienzoGlyphRenderersTest {

    @Mock
    private LienzoGlyphRenderer shapeGlyphRendererInstance;
    private ManagedInstanceStub<LienzoGlyphRenderer> rendererInstances;

    private LienzoGlyphRenderers tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        rendererInstances = spy(new ManagedInstanceStub<>(shapeGlyphRendererInstance));
        when(shapeGlyphRendererInstance.getGlyphType()).thenReturn(ShapeGlyph.class);
        this.tested = new LienzoGlyphRenderers(rendererInstances);
        tested.init();
    }

    @Test
    public void testType() {
        assertEquals(Glyph.class,
                     tested.getGlyphType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRender() {
        final ShapeGlyph glyph = ShapeGlyph.create();
        tested.render(glyph,
                      100,
                      200);
        verify(shapeGlyphRendererInstance,
               times(1)).render(eq(glyph),
                                eq(100d),
                                eq(200d));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(rendererInstances, times(1)).destroyAll();
    }
}
