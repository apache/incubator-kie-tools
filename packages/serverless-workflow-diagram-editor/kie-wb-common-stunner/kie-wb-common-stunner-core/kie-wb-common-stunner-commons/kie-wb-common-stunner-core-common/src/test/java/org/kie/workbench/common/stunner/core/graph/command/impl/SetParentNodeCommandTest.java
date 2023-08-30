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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Parent;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.violations.ContainmentRuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.TestingGraphUtils.verifyContainment;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SetParentNodeCommandTest extends AbstractGraphCommandTest {

    private static final String PARENT_UUID = "parentUUID";
    private static final String CANDIDATE_UUID = "candidateUUID";

    private Node parent;
    private Node candidate;
    private SetParentNodeCommand tested;

    @Before
    public void setup() throws Exception {
        super.init();
        this.parent = mockNode(PARENT_UUID);
        this.candidate = mockNode(CANDIDATE_UUID);
        when(graphIndex.getNode(eq(PARENT_UUID))).thenReturn(parent);
        when(graphIndex.getNode(eq(CANDIDATE_UUID))).thenReturn(candidate);
        this.tested = new SetParentNodeCommand(PARENT_UUID,
                                               CANDIDATE_UUID);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        final ArgumentCaptor<RuleEvaluationContext> contextCaptor = ArgumentCaptor.forClass(RuleEvaluationContext.class);
        verify(ruleManager,
               times(1)).evaluate(eq(ruleSet),
                                  contextCaptor.capture());
        final List<RuleEvaluationContext> contexts = contextCaptor.getAllValues();
        assertEquals(1,
                     contexts.size());
        verifyContainment((NodeContainmentContext) contexts.get(0),
                          parent,
                          candidate);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllowNoRules() {
        useAllowedExecutionContext();
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(ruleManager,
               times(0)).evaluate(eq(ruleSet),
                                  any(RuleEvaluationContext.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotAllowed() {
        final RuleViolations FAILED_VIOLATIONS = new DefaultRuleViolations()
                .addViolation(new ContainmentRuleViolation(graph.getUUID(),
                                                           PARENT_UUID));
        when(ruleManager.evaluate(any(RuleSet.class),
                                  any(RuleEvaluationContext.class))).thenReturn(FAILED_VIOLATIONS);
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
        assertFalse(parent.getOutEdges().isEmpty());
        assertFalse(candidate.getInEdges().isEmpty());
        Edge edge = (Edge) parent.getOutEdges().get(0);
        assertTrue(edge.getContent() instanceof Parent);
        assertEquals(parent,
                     edge.getSourceNode());
        assertEquals(candidate,
                     edge.getTargetNode());
        verify(graphIndex,
               times(1)).addEdge(eq(edge));
        verify(graphIndex,
               times(0)).addNode(any(Node.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteCheckFailed() {
        final RuleViolations FAILED_VIOLATIONS = new DefaultRuleViolations()
                .addViolation(new ContainmentRuleViolation(graph.getUUID(),
                                                           PARENT_UUID));
        when(ruleManager.evaluate(any(RuleSet.class),
                                  any(RuleEvaluationContext.class))).thenReturn(FAILED_VIOLATIONS);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.ERROR,
                     result.getType());
        assertTrue(parent.getOutEdges().isEmpty());
        assertTrue(candidate.getInEdges().isEmpty());
    }
}
