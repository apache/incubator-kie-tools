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

package org.kie.workbench.common.stunner.cm.factory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactoryImpl;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementGraphFactoryImplTest {

    @Mock
    private BPMNGraphFactoryImpl bpmnGraphFactory;
    @Mock
    private DefinitionManager definitionManager;
    @Mock
    private FactoryManager factoryManager;
    @Mock
    private RuleManager ruleManager;
    @Mock
    private GraphCommandManager graphCommandManager;
    @Mock
    private GraphCommandFactory graphCommandFactory;
    @Mock
    private GraphIndexBuilder<?> indexBuilder;

    private CaseManagementGraphFactoryImpl factory;

    @Before
    public void setup() {
        Index index = mock(Index.class);
        when(indexBuilder.build(any(Graph.class))).thenReturn(index);
        factory = new CaseManagementGraphFactoryImpl(definitionManager,
                                                     factoryManager,
                                                     ruleManager,
                                                     graphCommandManager,
                                                     graphCommandFactory,
                                                     indexBuilder,
                                                     bpmnGraphFactory);
    }

    @Test
    public void assertFactoryType() {
        // It is important that CaseManagementGraphFactoryImpl declares it relates to the CaseManagementGraphFactory
        // otherwise all sorts of things break. This test attempts to drawer the importance of this to future changes
        // should someone decide to change the apparent innocuous method in CaseManagementGraphFactoryImpl.
        assertEquals(CaseManagementGraphFactory.class,
                     factory.getFactoryType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuild() {
        final Node diagramNode = mock(Node.class);
        when(factoryManager.newElement(anyString(),
                                       eq(CaseManagementDiagram.class))).thenReturn(diagramNode);

        final Node stageNode = mock(Node.class);
        when(factoryManager.newElement(anyString(),
                                       eq(AdHocSubprocess.class))).thenReturn(stageNode);
        when(stageNode.getContent()).thenReturn(mock(View.class));

        final Node startEventNode = mock(Node.class);
        when(factoryManager.newElement(anyString(),
                                       eq(StartNoneEvent.class))).thenReturn(startEventNode);
        when(startEventNode.getContent()).thenReturn(mock(View.class));

        final Node endEventNode = mock(Node.class);
        when(factoryManager.newElement(anyString(),
                                       eq(EndNoneEvent.class))).thenReturn(endEventNode);
        when(endEventNode.getContent()).thenReturn(mock(View.class));

        final Edge startEventEdge = mock(Edge.class);
        final Edge endEventEdge = mock(Edge.class);
        when(factoryManager.newElement(anyString(),
                                       eq(SequenceFlow.class))).thenReturn(startEventEdge, endEventEdge);

        final Graph<DefinitionSet, Node> graph = factory.build("uuid1", "defSetId");

        assertNotNull(graph);
        assertEquals("uuid1", graph.getUUID());
        assertEquals(1, graph.getLabels().size());
        assertTrue(graph.getLabels().contains("defSetId"));

        final ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);

        verify(graphCommandFactory,
               times(1)).addNode(eq(diagramNode));

        verify(graphCommandFactory,
               times(1)).addChildNode(eq(diagramNode), eq(stageNode));

        verify(graphCommandFactory,
               times(1)).addChildNode(eq(diagramNode), eq(startEventNode));

        verify(graphCommandFactory,
               times(1)).addChildNode(eq(diagramNode), eq(endEventNode));

        verify(graphCommandFactory,
               times(1)).setSourceNode(eq(startEventNode), eq(startEventEdge), anyObject());

        verify(graphCommandFactory,
               times(1)).setTargetNode(eq(stageNode), eq(startEventEdge), anyObject());

        verify(graphCommandFactory,
               times(1)).setSourceNode(eq(stageNode), eq(endEventEdge), anyObject());

        verify(graphCommandFactory,
               times(1)).setTargetNode(eq(endEventNode), eq(endEventEdge), anyObject());

        verify(graphCommandManager,
               times(1)).execute(any(GraphCommandExecutionContext.class), commandCaptor.capture());

        final Command command = commandCaptor.getValue();
        assertTrue(command instanceof CompositeCommand);
        final CompositeCommand compositeCommand = (CompositeCommand) command;
        assertEquals(8, compositeCommand.size());
    }
}
