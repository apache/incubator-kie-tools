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
package org.kie.workbench.common.stunner.client.widgets.tooltip;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphTooltip;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionGlyphTooltipImplTest {

    private static final String DEFINITION_SET_ID = "defSetId";

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private GlyphTooltipImpl.View view;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private DefinitionAdapter definitionAdapter;

    @Mock
    private ShapeSet shapeSet;

    @Mock
    private ShapeFactory shapeFactory;

    private DefinitionGlyphTooltipImpl tooltip;

    @Before
    public void setup() {
        tooltip = new DefinitionGlyphTooltipImpl(definitionManager,
                                                 shapeManager,
                                                 factoryManager,
                                                 view);
    }

    @Test
    public void checkTooltipGlyphsAreDestroyed() {
        final Glyph glyph = makeGlyph("id1");

        tooltip.destroy();

        verify(glyph).destroy();
    }

    @SuppressWarnings("unchecked")
    private Glyph makeGlyph(final String id) {
        final Glyph glyph = mock(Glyph.class);
        final Group group = mock(Group.class);
        final Object definition = mock(Object.class);
        when(glyph.getGroup()).thenReturn(group);
        when(group.asNode()).thenReturn(mock(Node.class));
        when(factoryManager.newDefinition(eq(id))).thenReturn(definition);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(definitionAdapter.getTitle(eq(definition))).thenReturn(id);

        when(shapeManager.getDefaultShapeSet(eq(DEFINITION_SET_ID))).thenReturn(shapeSet);
        when(shapeSet.getShapeFactory()).thenReturn(shapeFactory);
        when(shapeFactory.glyph(eq(id),
                                anyDouble(),
                                anyDouble())).thenReturn(glyph);

        tooltip.showGlyph(DEFINITION_SET_ID,
                          id,
                          0,
                          0,
                          0,
                          0,
                          GlyphTooltip.Direction.NORTH);

        return glyph;
    }
}
