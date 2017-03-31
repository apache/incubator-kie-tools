/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.client.widgets.palette.factory;

import java.util.Collections;
import java.util.Map;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconRenderer;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconResource;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class BindableBS3PaletteGlyphViewFactoryTest {

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private ShapeSet shapeSet;

    @Mock
    private ShapeFactory shapeFactory;

    @Mock
    private BS3PaletteGlyphViewFactory glyphViewFactory;

    private BindableBS3PaletteGlyphViewFactory factory;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.factory = new BindableBS3PaletteGlyphViewFactory(shapeManager) {

            @Override
            BS3PaletteGlyphViewFactory getBS3PaletteGlyphViewFactory(final ShapeManager shapeManager) {
                return glyphViewFactory;
            }

            @Override
            protected Class<?> getDefinitionSetType() {
                //Dummy value
                return DefinitionSet.class;
            }

            @Override
            protected Class<? extends IconRenderer> getPaletteIconRendererType() {
                //Dummy value
                return IconRenderer.class;
            }

            @Override
            protected Map<String, IconResource> getCategoryIconResources() {
                //Dummy value
                return Collections.emptyMap();
            }

            @Override
            protected Map<String, IconResource> getDefinitionIconResources() {
                //Dummy value
                return Collections.emptyMap();
            }
        };
    }

    @Test
    public void checkGlyphViewFactoryIsDestroyed() {
        factory.destroy();

        verify(glyphViewFactory).destroy();
    }
}
