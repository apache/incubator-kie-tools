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

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.exception.BadCommandArgumentsException;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class RemoveChildrenCommandTest extends AbstractGraphCommandTest {

    private static final String PARENT_UUID = "parentUUID";
    private static final String CANDIDATE_UUID = "candidateUUID";
    private static final String Edge_UUID = "edgeUUID";

    private Node parent;
    private Node candidate;
    private Edge edge;
    private
    @Mock
    Child edgeContent;
    private final List<Edge> parentOutEdges = new LinkedList<>();
    private final List<Edge> candidateInEdges = new LinkedList<>();

    private RemoveChildrenCommand tested;

    @Before
    public void setup() throws Exception {
        super.init();
        this.parent = mockNode(PARENT_UUID);
        this.candidate = mockNode(CANDIDATE_UUID);
        this.edge = mockEdge(CANDIDATE_UUID);
        when(edge.getContent()).thenReturn(edgeContent);
        when(edge.getTargetNode()).thenReturn(candidate);
        when(edge.getSourceNode()).thenReturn(parent);
        when(graphIndex.getNode(eq(PARENT_UUID))).thenReturn(parent);
        when(graphIndex.getNode(eq(CANDIDATE_UUID))).thenReturn(candidate);
        when(graphIndex.getEdge(eq(Edge_UUID))).thenReturn(edge);
        when(parent.getOutEdges()).thenReturn(parentOutEdges);
        when(candidate.getInEdges()).thenReturn(candidateInEdges);
        parentOutEdges.add(edge);
        this.tested = new RemoveChildrenCommand(PARENT_UUID,
                                                new String[]{CANDIDATE_UUID});
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(ruleManager,
               times(0)).evaluate(eq(ruleSet),
                                  any(RuleEvaluationContext.class));
    }

    @Test(expected = BadCommandArgumentsException.class)
    @SuppressWarnings("unchecked")
    public void testNotAllowed() {
        when(graphIndex.getNode(eq(PARENT_UUID))).thenReturn(null);
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.ERROR,
                     result.getType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        assertTrue(parentOutEdges.isEmpty());
        assertTrue(candidateInEdges.isEmpty());
        verify(graphIndex,
               times(1)).removeEdge(any(Edge.class));
        verify(graphIndex,
               times(0)).removeNode(any(Node.class));
        verify(graph,
               times(0)).addNode(any(Node.class));
        verify(graphIndex,
               times(0)).addNode(any(Node.class));
        verify(graphIndex,
               times(0)).addEdge(any(Edge.class));
    }
}
