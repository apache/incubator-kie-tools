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
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.lookup.domain.CommonDomainLookups;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FlowActionsToolboxFactoryTest {

    private static final String DS_ID = "defSetId1";
    private static final String EDGE_ID = "edgeId1";
    private static final String NODE_ID = "nodeId1";
    private static final String E_UUID = "e1";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private ToolboxDomainLookups toolboxLookups;

    @Mock
    private CommonDomainLookups domainLookups;

    @Mock
    private CreateConnectorAction createConnectorAction;

    @Mock
    private Command createConnectorActionDestroyer;

    @Mock
    private CreateNodeAction createNodeAction;

    @Mock
    private Command createNodeActionDestroyer;

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
    private Graph graph;

    @Mock
    private Node<View<Object>, Edge> element;

    private FlowActionsToolboxFactory tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(createConnectorAction.setEdgeId(anyString())).thenReturn(createConnectorAction);
        when(createNodeAction.setEdgeId(anyString())).thenReturn(createNodeAction);
        when(createNodeAction.setNodeId(anyString())).thenReturn(createNodeAction);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(DS_ID);
        when(element.getUUID()).thenReturn(E_UUID);
        when(element.asNode()).thenReturn(element);
        when(toolboxLookups.get(anyString())).thenReturn(domainLookups);
        when(domainLookups.lookupTargetConnectors(eq(element)))
                .thenReturn(Collections.singleton(EDGE_ID));
        when(domainLookups.lookupTargetNodes(eq(graph),
                                             eq(element),
                                             eq(EDGE_ID)))
                .thenReturn(Collections.singleton(NODE_ID));
        when(domainLookups.lookupMorphBaseDefinitions(anySet()))
                .thenReturn(Collections.singleton(NODE_ID));
        this.tested = new FlowActionsToolboxFactory(toolboxLookups,
                                                    () -> createConnectorAction,
                                                    createConnectorActionDestroyer,
                                                    () -> createNodeAction,
                                                    createNodeActionDestroyer,
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
        assertEquals(2,
                     actionsToolbox.size());
        final Iterator actionIt = actionsToolbox.iterator();
        assertEquals(createConnectorAction,
                     actionIt.next());
        assertEquals(createNodeAction,
                     actionIt.next());
        verify(createConnectorAction,
               times(1)).setEdgeId(eq(EDGE_ID));
        verify(createNodeAction,
               times(1)).setEdgeId(eq(EDGE_ID));
        verify(createNodeAction,
               times(1)).setNodeId(eq(NODE_ID));
        verify(view,
               times(1)).init(eq(actionsToolbox));
        verify(view,
               times(2)).addButton(any(Glyph.class),
                                   anyString(),
                                   any(Consumer.class));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(createConnectorActionDestroyer, times(1)).execute();
        verify(createNodeActionDestroyer, times(1)).execute();
        verify(viewDestroyer, times(1)).execute();
    }
}
