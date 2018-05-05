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

package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MorphActionsToolboxFactoryTest {

    private static final String E_UUID = "e1";
    private static final String DEF_ID = "def1";
    private static final String MORPH_TARGET_ID = "morphTarget1";

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private AdapterManager adapters;

    @Mock
    private AdapterRegistry adapterRegistry;

    @Mock
    private DefinitionAdapter definitionAdapter;

    @Mock
    private MorphAdapter morphAdapter;

    @Mock
    private MorphNodeAction morphNodeAction;

    @Mock
    private Command morphNodeActionDestroyer;

    @Mock
    private ActionsToolboxView view;

    @Mock
    private Command viewDestroyer;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Node<View, Edge> element;

    @Mock
    private View<Object> elementContent;

    @Mock
    private Object definition;

    @Mock
    private MorphDefinition morphDefinition;

    private MorphActionsToolboxFactory tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(morphNodeAction.setMorphDefinition(any(MorphDefinition.class))).thenReturn(morphNodeAction);
        when(morphNodeAction.setTargetDefinitionId(anyString())).thenReturn(morphNodeAction);
        when(definitionManager.adapters()).thenReturn(adapters);
        when(adapters.registry()).thenReturn(adapterRegistry);
        when(adapters.forDefinition()).thenReturn(definitionAdapter);
        when(adapterRegistry.getMorphAdapter(any(Class.class))).thenReturn(morphAdapter);
        when(morphAdapter.getMorphDefinitions(eq(definition))).thenReturn(Collections.singleton(morphDefinition));
        when(morphAdapter.getTargets(eq(definition),
                                     eq(morphDefinition)))
                .thenReturn(Collections.singleton(MORPH_TARGET_ID));
        when(element.getUUID()).thenReturn(E_UUID);
        when(element.getContent()).thenReturn(elementContent);
        when(element.asNode()).thenReturn(element);
        when(definitionAdapter.getId(eq(definition))).thenReturn(DEF_ID);
        when(elementContent.getDefinition()).thenReturn(definition);
        when(definitionUtils.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionUtils.hasMorphTargets(eq(definition))).thenReturn(true);
        this.tested = new MorphActionsToolboxFactory(definitionUtils,
                                                     () -> morphNodeAction,
                                                     morphNodeActionDestroyer,
                                                     () -> view,
                                                     viewDestroyer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildToolbox() {
        final Optional<Toolbox<?>> toolbox =
                tested.build(canvasHandler,
                             element);
        assertTrue(toolbox.isPresent());
        assertTrue(toolbox.get() instanceof ActionsToolbox);
        final ActionsToolbox actionsToolbox = (ActionsToolbox) toolbox.get();
        assertEquals(E_UUID,
                     actionsToolbox.getElementUUID());
        assertEquals(1,
                     actionsToolbox.size());
        assertEquals(morphNodeAction,
                     actionsToolbox.iterator().next());
        verify(morphNodeAction,
               times(1)).setMorphDefinition(eq(morphDefinition));
        verify(morphNodeAction,
               times(1)).setTargetDefinitionId(eq(MORPH_TARGET_ID));
        verify(view,
               times(1)).init(eq(actionsToolbox));
        verify(view,
               times(1)).addButton(any(Glyph.class),
                                   anyString(),
                                   any(Consumer.class));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(morphNodeActionDestroyer, times(1)).execute();
        verify(viewDestroyer, times(1)).execute();
    }
}
