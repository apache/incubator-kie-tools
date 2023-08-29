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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DeleteElementsCommandTest extends AbstractGraphCommandTest {

    private static final String SOURCE_UUID = "sourceUUID";
    private static final String TARGET_UUID = "targetUUID";
    private static final String EDGE_UUID = "edgeUUID";

    private Node source;
    private Node target;
    private Edge edge;

    @Mock
    private ViewConnector connContent;

    private Optional<Connection> sourceMagnet;
    private Optional<Connection> targetMagnet;

    private DeleteElementsCommand tested;

    @Before
    public void setup() throws Exception {
        super.init();
        source = mockNode(SOURCE_UUID);
        target = mockNode(TARGET_UUID);
        sourceMagnet = Optional.of(MagnetConnection.Builder.at(0d,
                                                               0d));
        targetMagnet = Optional.of(MagnetConnection.Builder.at(0d,
                                                               0d));
        edge = mockEdge(EDGE_UUID);
        when(graphIndex.get(eq(SOURCE_UUID))).thenReturn(source);
        when(graphIndex.getNode(eq(SOURCE_UUID))).thenReturn(source);
        when(graphIndex.get(eq(TARGET_UUID))).thenReturn(target);
        when(graphIndex.getNode(eq(TARGET_UUID))).thenReturn(target);
        when(graphIndex.get(eq(EDGE_UUID))).thenReturn(edge);
        when(graphIndex.getEdge(eq(EDGE_UUID))).thenReturn(edge);
        when(edge.getContent()).thenReturn(connContent);
        when(edge.getSourceNode()).thenReturn(source);
        when(edge.getTargetNode()).thenReturn(target);
        when(connContent.getSourceConnection()).thenReturn(sourceMagnet);
        when(connContent.getTargetConnection()).thenReturn(targetMagnet);
        final List sourceOutEdges = Collections.singletonList(edge);
        final List targetInEdges = asList(edge);
        when(source.getOutEdges()).thenReturn(sourceOutEdges);
        when(target.getInEdges()).thenReturn(targetInEdges);
        this.tested = new DeleteElementsCommand(asList(SOURCE_UUID, TARGET_UUID, EDGE_UUID));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompositeTheRightCommands() {
        tested.initialize(graphCommandExecutionContext);
        final List<Command<GraphCommandExecutionContext, RuleViolation>> commands = tested.getCommands();
        assertEquals(3, commands.size());
        DeleteConnectorCommand deleteConnectorCommand = (DeleteConnectorCommand) commands.get(0);
        assertEquals(edge, deleteConnectorCommand.getEdge());
        SafeDeleteNodeCommand deleteSourceNodeCommand = (SafeDeleteNodeCommand) commands.get(1);
        assertEquals(source, deleteSourceNodeCommand.getNode());
        SafeDeleteNodeCommand deleteTargetNodeCommand = (SafeDeleteNodeCommand) commands.get(2);
        assertEquals(target, deleteTargetNodeCommand.getNode());
        // Finally assert that the edge is not being removed several times, as it's being excluded on the rest of commands.
        assertTrue(deleteSourceNodeCommand.getOptions().getExclusions().contains(EDGE_UUID));
        assertTrue(deleteTargetNodeCommand.getOptions().getExclusions().contains(EDGE_UUID));
    }
}
