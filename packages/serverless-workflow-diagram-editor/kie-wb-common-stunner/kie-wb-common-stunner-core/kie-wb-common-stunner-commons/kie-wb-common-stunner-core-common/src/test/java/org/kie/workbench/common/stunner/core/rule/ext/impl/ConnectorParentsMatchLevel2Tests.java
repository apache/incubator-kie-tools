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


package org.kie.workbench.common.stunner.core.rule.ext.impl;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstances;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.ContextualGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetChildrenCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionSourceNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.ContextOperationNotAllowedViolation;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.TestingGraphInstances.assertRuleFailedResult;
import static org.kie.workbench.common.stunner.core.TestingGraphInstances.assertSuccessfullResult;
import static org.kie.workbench.common.stunner.core.command.util.CommandUtils.isError;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConnectorParentsMatchLevel2Tests {

    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphInstances.Level2Graph graph;

    @Before
    public void setUp() {
        graphTestHandler = new TestingGraphMockHandler();
        graph = TestingGraphInstances.newLevel2Graph(graphTestHandler);
    }

    // CONNECTION contexts

    @Test
    @SuppressWarnings("unchecked")
    public void testSetEdge1TargetAsEndNode() {
        SetConnectionTargetNodeCommand setTarget = new SetConnectionTargetNodeCommand(graph.endNode, graph.edge1);
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setTarget.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetEdge1TargetAsNodeA() {
        SetConnectionTargetNodeCommand setTarget = new SetConnectionTargetNodeCommand(graph.nodeA, graph.edge1);
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setTarget.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetEdge1TargetAsNodeB() {
        SetConnectionTargetNodeCommand setTarget = new SetConnectionTargetNodeCommand(graph.nodeB, graph.edge1);
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setTarget.allow(executionContext);
        assertRuleFailedResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetEdge1SourceAsEndNode() {
        SetConnectionSourceNodeCommand setTarget = new SetConnectionSourceNodeCommand(graph.endNode, graph.edge1);
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setTarget.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetEdge1SourceAsNodeA() {
        SetConnectionSourceNodeCommand setTarget = new SetConnectionSourceNodeCommand(graph.nodeA, graph.edge1);
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setTarget.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetEdge1SourceAsNodeB() {
        SetConnectionSourceNodeCommand setTarget = new SetConnectionSourceNodeCommand(graph.nodeB, graph.edge1);
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setTarget.allow(executionContext);
        assertRuleFailedResult(result);
    }

    // CONTAINMENT contexts

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveStartNodeIntoNodeA() {
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.nodeA,
                                                                graph.startNode);
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertTrue(isError(result));
        assertTrue(result.getViolations().iterator().hasNext());
        RuleViolation ruleViolation = result.getViolations().iterator().next();
        assertTrue(ruleViolation instanceof ContextOperationNotAllowedViolation);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveStartNodeIntoNodeB() {
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.nodeB,
                                                                graph.startNode);
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertTrue(isError(result));
        assertTrue(result.getViolations().iterator().hasNext());
        RuleViolation ruleViolation = result.getViolations().iterator().next();
        assertTrue(ruleViolation instanceof ContextOperationNotAllowedViolation);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveStartNodeIntoSubProcessNode() {
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.subProcessNode,
                                                                graph.startNode);
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveStartNodeIntoParentNode() {
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.parentNode,
                                                                graph.startNode);
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertRuleFailedResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveIntermNodeIntoSubProcessNode() {
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.subProcessNode,
                                                                graph.intermNode);
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveIntermNodeIntoParentNode() {
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.parentNode,
                                                                graph.intermNode);

        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertRuleFailedResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveEndNodeIntoSubProcessNode() {
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.subProcessNode,
                                                                graph.endNode);
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveEndNodeIntoParentNode() {
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.parentNode,
                                                                graph.endNode);
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertRuleFailedResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveSomeConnectedNodesIntoSubProcessNode() {
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.subProcessNode,
                                                                Arrays.asList(graph.startNode,
                                                                              graph.endNode));
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveSomeConnectedNodesIntoParentNode() {
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.parentNode,
                                                                Arrays.asList(graph.startNode,
                                                                              graph.endNode));
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertRuleFailedResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveAllConnectedNodesIntoSubProcessNode() {
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.subProcessNode,
                                                                Arrays.asList(graph.startNode,
                                                                              graph.intermNode,
                                                                              graph.endNode));
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveAllConnectedNodesIntoParentNode() {
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.parentNode,
                                                                Arrays.asList(graph.startNode,
                                                                              graph.intermNode,
                                                                              graph.endNode));
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertSuccessfullResult(result);
    }

    private ContextualGraphCommandExecutionContext createExecutionContext() {
        return TestingGraphInstances.createConstrainedExecutionContext(graphTestHandler,
                                                                       "edgeId",
                                                                       TestingGraphInstances.SubProcessNodeBean.class);
    }
}
