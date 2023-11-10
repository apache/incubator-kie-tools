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


package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.profile.DomainProfileManager;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MorphActionsToolboxFactoryTest {

    private static final String E_UUID = "e1";
    private static final String DEF_ID = "def1";
    private static final String MORPH_TARGET_ID = "morphTarget1";

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private DomainProfileManager profileManager;

    @Mock
    private Predicate<String> profileFilter;

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
    private MorphNodeToolboxAction morphNodeAction;

    @Mock
    private Command morphNodeActionDestroyer;

    @Mock
    private ActionsToolboxView view;

    @Mock
    private Command viewDestroyer;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

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
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(profileManager.isDefinitionIdAllowed(eq(metadata))).thenReturn(profileFilter);
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
        when(definitionAdapter.getId(eq(definition))).thenReturn(DefinitionId.build(DEF_ID));
        when(elementContent.getDefinition()).thenReturn(definition);
        when(definitionUtils.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionUtils.hasMorphTargets(eq(definition))).thenReturn(true);
        this.tested = new MorphActionsToolboxFactory(definitionUtils,
                                                     profileManager,
                                                     () -> morphNodeAction,
                                                     morphNodeActionDestroyer,
                                                     () -> view,
                                                     viewDestroyer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildToolbox() {
        when(profileFilter.test(eq(MORPH_TARGET_ID))).thenReturn(true);
        final Optional<Toolbox<?>> toolbox =
                tested.build(canvasHandler,
                             element);
        verify(profileManager, times(1)).isDefinitionIdAllowed(eq(metadata));
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
    }

    @Test
    public void testBuildToolboxWithProfileRestrictions() {
        when(profileFilter.test(eq(MORPH_TARGET_ID))).thenReturn(false);
        final Optional<Toolbox<?>> toolbox =
                tested.build(canvasHandler,
                             element);
        verify(profileManager, times(1)).isDefinitionIdAllowed(eq(metadata));
        assertFalse(toolbox.isPresent());
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(morphNodeActionDestroyer, times(1)).execute();
        verify(viewDestroyer, times(1)).execute();
    }
}
