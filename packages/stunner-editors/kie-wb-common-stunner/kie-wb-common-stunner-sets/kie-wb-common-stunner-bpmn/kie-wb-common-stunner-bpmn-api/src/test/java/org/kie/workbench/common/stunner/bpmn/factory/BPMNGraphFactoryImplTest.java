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


package org.kie.workbench.common.stunner.bpmn.factory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNGraphFactoryImplTest {

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

    private BPMNGraphFactoryImpl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        Index index = mock(Index.class);
        when(indexBuilder.build(any(Graph.class))).thenReturn(index);
        tested = new BPMNGraphFactoryImpl(definitionManager,
                                          factoryManager,
                                          ruleManager,
                                          graphCommandManager,
                                          graphCommandFactory,
                                          indexBuilder);
    }

    @Test
    public void testFactoryType() {
        assertEquals(BPMNGraphFactory.class,
                     tested.getFactoryType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuild() {
        final Node diagramNode = mock(Node.class);
        final Node startEventNode = mock(Node.class);
        when(factoryManager.newElement(anyString(),
                                       eq(getDefinitionId(BPMNDiagramImpl.class)))).thenReturn(diagramNode);
        final Graph<DefinitionSet, Node> graph = tested.build("uuid1",
                                                              "defSetId");
        assertNotNull(graph);
        assertEquals("uuid1",
                     graph.getUUID());
        assertEquals(1,
                     graph.getLabels().size());
        assertTrue(graph.getLabels().contains("defSetId"));
        final ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(graphCommandFactory,
               times(1)).addNode(eq(diagramNode));
        verify(graphCommandManager,
               times(1)).execute(any(GraphCommandExecutionContext.class),
                                 commandCaptor.capture());
        final Command command = commandCaptor.getValue();
        assertTrue(command instanceof CompositeCommand);
        final CompositeCommand compositeCommand = (CompositeCommand) command;
        assertEquals(1,
                     compositeCommand.size());
    }

    @Test
    public void testIsDelegateFactory() {
        assertFalse(tested.isDelegateFactory());
    }
}