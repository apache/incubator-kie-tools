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

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;
import org.mockito.Mock;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class BS3PaletteGlyphViewFactoryTest {

    private static final String DEFINITION_SET_ID = "defSetId";

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private ShapeSet shapeSet;

    @Mock
    private ShapeFactory shapeFactory;

    private BS3PaletteGlyphViewFactory factory;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.factory = new BS3PaletteGlyphViewFactory(shapeManager);

        when(shapeManager.getDefaultShapeSet(eq(DEFINITION_SET_ID))).thenReturn(shapeSet);
        when(shapeSet.getShapeFactory()).thenReturn(shapeFactory);
    }

    @Test
    public void checkGlyphsAreDestroyed() {
        final Glyph glyph1 = makeGlyph("id1");
        final Glyph glyph2 = makeGlyph("id2");

        factory.getDefinitionIconSettings(DEFINITION_SET_ID,
                                          "id1");
        factory.getDefinitionIconSettings(DEFINITION_SET_ID,
                                          "id2");

        factory.destroy();

        verify(glyph1).destroy();
        verify(glyph2).destroy();
    }

    @SuppressWarnings("unchecked")
    private Glyph makeGlyph(final String id) {
        final Glyph glyph = mock(Glyph.class);
        final Group group = mock(Group.class);
        when(glyph.getGroup()).thenReturn(group);
        when(group.asNode()).thenReturn(mock(Node.class));
        when(shapeFactory.glyph(eq(id),
                                anyDouble(),
                                anyDouble())).thenReturn(glyph);
        return glyph;
    }
}
