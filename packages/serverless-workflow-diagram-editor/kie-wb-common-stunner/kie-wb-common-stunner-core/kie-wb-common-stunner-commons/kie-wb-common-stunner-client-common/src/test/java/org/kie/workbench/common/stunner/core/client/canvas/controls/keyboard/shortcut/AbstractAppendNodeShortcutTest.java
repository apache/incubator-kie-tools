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


package org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.shortcut;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.lookup.domain.CommonDomainLookups;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractAppendNodeShortcutTest {

    @Mock
    protected ToolboxDomainLookups toolboxDomainLookups;

    @Mock
    protected DefinitionsCacheRegistry definitionsCacheRegistry;

    @Mock
    protected GeneralCreateNodeAction generalCreateNodeAction;

    @Mock
    private CommonDomainLookups commonDomainLookups;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    private String selectedNodeId;

    private String targetDefinitionNodeId;

    private String connectorDefinitionId;

    private String definitionSetId;

    @Mock
    private Index graphIndex;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private Metadata metadata;

    private AbstractAppendNodeShortcut testedShortcut;

    @Test
    public void testAppend() {

        final Object targetNodeDefinition = mock(Object.class);
        testedShortcut = new AbstractAppendNodeShortcutMock(targetNodeDefinition);

        selectedNodeId = "selected";
        targetDefinitionNodeId = "target";
        connectorDefinitionId = "connector";
        definitionSetId = "definition";

        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(canvasHandler.getDiagram()).thenReturn(diagram);

        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);

        when(metadata.getDefinitionSetId()).thenReturn(definitionSetId);

        final Element selectedElement = mock(Element.class);
        when(graphIndex.get(selectedNodeId)).thenReturn(selectedElement);

        final Node selectedNode = mock(Node.class);
        when(selectedElement.asNode()).thenReturn(selectedNode);

        when(toolboxDomainLookups.get(definitionSetId)).thenReturn(commonDomainLookups);

        final Set<String> connectorDefinitionIds = Collections.singleton(connectorDefinitionId);
        when(commonDomainLookups.lookupTargetConnectors(selectedNode)).thenReturn(connectorDefinitionIds);
        final Set<String> targetNodesDefinitionIds = Collections.singleton(targetDefinitionNodeId);
        when(commonDomainLookups.lookupTargetNodes(graph,
                                                   selectedNode,
                                                   connectorDefinitionId)).thenReturn(targetNodesDefinitionIds);

        // positive test
        // found demanded target definition
        when(definitionsCacheRegistry.getDefinitionById(targetDefinitionNodeId)).thenReturn(targetNodeDefinition);

        testedShortcut.executeAction(canvasHandler, selectedNodeId);

        verify(generalCreateNodeAction).executeAction(canvasHandler,
                                                      selectedNodeId,
                                                      targetDefinitionNodeId,
                                                      connectorDefinitionId);

        reset(definitionsCacheRegistry);
        reset(generalCreateNodeAction);

        // negative test
        // demanden target definition not found
        when(definitionsCacheRegistry.getDefinitionById(targetDefinitionNodeId)).thenReturn(new Object());

        testedShortcut.executeAction(canvasHandler, selectedNodeId);

        verify(generalCreateNodeAction, never()).executeAction(any(),
                                                               any(),
                                                               any(),
                                                               any());
    }

    private class AbstractAppendNodeShortcutMock extends AbstractAppendNodeShortcut {

        private Object targetNodeDefinition;

        public AbstractAppendNodeShortcutMock(final Object targetNodeDefinition) {
            super(toolboxDomainLookups, definitionsCacheRegistry, generalCreateNodeAction);

            this.targetNodeDefinition = targetNodeDefinition;
        }

        @Override
        public boolean canAppendNodeOfDefinition(final Object definition) {
            return definition == targetNodeDefinition;
        }

        @Override
        public boolean matchesPressedKeys(final KeyboardEvent.Key... pressedKeys) {
            // tested per AbstractAppendNodeShortcut implementations
            return false;
        }

        @Override
        public boolean matchesSelectedElement(final Element selectedElement) {
            // tested per AbstractAppendNodeShortcut implementations
            return false;
        }

        @Override
        public KeyboardEvent.Key[] getKeyCombination() {
            return new KeyboardEvent.Key[]{};
        }

        @Override
        public String getLabel() {
            return null;
        }
    }
}
