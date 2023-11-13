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
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstances;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.ContextualGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddChildNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddConnectorCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetChildrenCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionSourceNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.junit.MockitoJUnitRunner;

import static org.kie.workbench.common.stunner.core.TestingGraphInstances.assertRuleFailedResult;
import static org.kie.workbench.common.stunner.core.TestingGraphInstances.assertSuccessfullResult;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConnectorParentsMatchLevel1Tests {

    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphInstances.Level1Graph graph;

    @Before
    public void setUp() {
        graphTestHandler = new TestingGraphMockHandler();
        graph = TestingGraphInstances.newLevel1Graph(graphTestHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveStartNodeIntoParentNode() {
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.parentNode,
                                                                graph.startNode);
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveIntermNodeIntoParentNode() {
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.parentNode,
                                                                graph.intermNode);
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveEndNodeIntoParentNode() {
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.parentNode,
                                                                graph.endNode);
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConstrainedMoveStartNodeIntoParentNode() {
        ContextualGraphCommandExecutionContext executionContext = createConstrainedExecutionContext();
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.parentNode,
                                                                graph.startNode);
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertRuleFailedResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConstrainedMoveIntermNodeIntoParentNode() {
        ContextualGraphCommandExecutionContext executionContext = createConstrainedExecutionContext();
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.parentNode,
                                                                graph.intermNode);
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertRuleFailedResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConstrainedMoveEndNodeIntoParentNode() {
        ContextualGraphCommandExecutionContext executionContext = createConstrainedExecutionContext();
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.parentNode,
                                                                graph.endNode);
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertRuleFailedResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConstrainedMoveStartIntermNodesIntoParentNode() {
        ContextualGraphCommandExecutionContext executionContext = createConstrainedExecutionContext();
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.parentNode,
                                                                Arrays.asList(graph.startNode,
                                                                              graph.intermNode));
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertRuleFailedResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConstrainedMoveAllConnectedNodesIntoParentNode() {
        ContextualGraphCommandExecutionContext executionContext = createConstrainedExecutionContext();
        SetChildrenCommand setChildren = new SetChildrenCommand(graph.parentNode,
                                                                Arrays.asList(graph.startNode,
                                                                              graph.intermNode,
                                                                              graph.endNode));
        CommandResult<RuleViolation> result = setChildren.allow(executionContext);
        assertSuccessfullResult(result);
    }

    // CONNECTION contexts

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
    public void testSetEdge1TargetAsEndNode() {
        SetConnectionTargetNodeCommand setTarget = new SetConnectionTargetNodeCommand(graph.endNode, graph.edge1);
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = setTarget.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetEdge1TargetAsEndNodeEvenWithConstraintsButDoesNotApply() {
        SetConnectionTargetNodeCommand setTarget = new SetConnectionTargetNodeCommand(graph.endNode, graph.edge1);
        ContextualGraphCommandExecutionContext executionContext = createConstrainedExecutionContext();
        CommandResult<RuleViolation> result = setTarget.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConstrainedSetEdge1TargetAsNodeA() {
        SetConnectionTargetNodeCommand setTarget = new SetConnectionTargetNodeCommand(graph.nodeA, graph.edge1);
        ContextualGraphCommandExecutionContext executionContext = createConstrainedExecutionContext();
        CommandResult<RuleViolation> result = setTarget.allow(executionContext);
        assertRuleFailedResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConstrainedSetEdge1SourceAsNodeA() {
        SetConnectionSourceNodeCommand setTarget = new SetConnectionSourceNodeCommand(graph.nodeA, graph.edge1);
        ContextualGraphCommandExecutionContext executionContext = createConstrainedExecutionContext();
        CommandResult<RuleViolation> result = setTarget.allow(executionContext);
        assertRuleFailedResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConstrainedSetEdge1TargetAsNull() {
        SetConnectionTargetNodeCommand setTarget = new SetConnectionTargetNodeCommand(null, graph.edge1);
        ContextualGraphCommandExecutionContext executionContext = createConstrainedExecutionContext();
        CommandResult<RuleViolation> result = setTarget.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConstrainedSetEdge1SourceAsNull() {
        SetConnectionSourceNodeCommand setTarget = new SetConnectionSourceNodeCommand(null, graph.edge1);
        ContextualGraphCommandExecutionContext executionContext = createConstrainedExecutionContext();
        CommandResult<RuleViolation> result = setTarget.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    public void testAddNewEdgeAndNode() {
        Command<GraphCommandExecutionContext, RuleViolation> command = addNewEdgeAndNodeIntoParent(graph.containerNode);
        ContextualGraphCommandExecutionContext executionContext = createConstrainedExecutionContext();
        CommandResult<RuleViolation> result = command.allow(executionContext);
        assertSuccessfullResult(result);
    }

    @Test
    public void testAddNewEdgeAndNodeButNoParentsTypeMatch() {
        Command<GraphCommandExecutionContext, RuleViolation> command = addNewEdgeAndNodeIntoParent(graph.parentNode);
        ContextualGraphCommandExecutionContext executionContext = createConstrainedExecutionContext();
        CommandResult<RuleViolation> result = command.allow(executionContext);
        assertRuleFailedResult(result);
    }

    @SuppressWarnings("unchecked")
    private Command<GraphCommandExecutionContext, RuleViolation> addNewEdgeAndNodeIntoParent(Node parent) {
        Edge someEdge = graphTestHandler.newEdge("someEdgeUUID",
                                                 "edgeId",
                                                 Optional.empty());
        Node someNode = graphTestHandler.newNode("someNodeUUID",
                                                 "someNodeId",
                                                 Optional.of(new String[]{"someNodeLabel", "all"}));
        return
                new CompositeCommand.Builder<GraphCommandExecutionContext, RuleViolation>()
                        .addCommand(new AddChildNodeCommand(parent,
                                                            someNode))
                        .addCommand(new AddConnectorCommand(graph.endNode,
                                                            someEdge,
                                                            MagnetConnection.Builder.at(0, 0)))
                        .addCommand(new SetConnectionTargetNodeCommand(someNode,
                                                                       someEdge,
                                                                       MagnetConnection.Builder.at(0, 0)))
                        .build();
    }

    private ContextualGraphCommandExecutionContext createConstrainedExecutionContext() {
        return TestingGraphInstances.createConstrainedExecutionContext(graphTestHandler,
                                                                       "edgeId",
                                                                       TestingGraphInstances.ContainerNodeBean.class);
    }

    private ContextualGraphCommandExecutionContext createExecutionContext() {
        return TestingGraphInstances.createExecutionContext(graphTestHandler);
    }
}
