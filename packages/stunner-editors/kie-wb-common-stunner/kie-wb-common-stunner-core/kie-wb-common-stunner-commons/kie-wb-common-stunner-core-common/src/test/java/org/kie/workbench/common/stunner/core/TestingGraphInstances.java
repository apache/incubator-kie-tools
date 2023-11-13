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


package org.kie.workbench.common.stunner.core;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.ContextualGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionMultiHandler;
import org.kie.workbench.common.stunner.core.rule.ext.impl.ConnectorParentsMatchConnectionHandler;
import org.kie.workbench.common.stunner.core.rule.ext.impl.ConnectorParentsMatchContainmentHandler;
import org.kie.workbench.common.stunner.core.rule.ext.impl.ConnectorParentsMatchHandler;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.kie.workbench.common.stunner.core.rule.impl.CanContain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.command.util.CommandUtils.isError;
import static org.kie.workbench.common.stunner.core.command.util.CommandUtils.isWarn;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class TestingGraphInstances {

    public static final String RULE_ERROR_MESSAGE = "the error argument text";

    /**
     * Level1Graph Structure:
     * -                       parentNode
     * ------------------------------------------------------------------------------
     * |                       containerNode                                        |
     * |   -----------------------------------------------------------              |
     * |   | startNode --(edge1)--> intermNode --(edge2)--> endNode  |     nodeA    |
     * |   -----------------------------------------------------------              |
     * |                                                                            |
     * ------------------------------------------------------------------------------
     */

    public static class Level1Graph {

        public Graph graph;
        public ParentNodeBean parentNodeBean;
        public Node parentNode;
        public ContainerNodeBean containerNodeBean;
        public Node containerNode;
        public Node startNode;
        public Node intermNode;
        public Node endNode;
        public Edge edge1;
        public Edge edge2;
        public Node nodeA;
    }

    public static Level1Graph newLevel1Graph(TestingGraphMockHandler graphTestHandler) {
        Level1Graph result = new Level1Graph();
        populate(graphTestHandler, result);
        graphTestHandler
                .setChild(result.parentNode,
                          result.containerNode)
                .setChild(result.containerNode,
                          result.startNode)
                .setChild(result.containerNode,
                          result.intermNode)
                .setChild(result.containerNode,
                          result.endNode)
                .addEdge(result.edge1,
                         result.startNode)
                .connectTo(result.edge1,
                           result.intermNode)
                .addEdge(result.edge2,
                         result.intermNode)
                .connectTo(result.edge2,
                           result.endNode)
                .setChild(result.parentNode,
                          result.nodeA);
        return result;
    }

    /**
     * Level2Graph Structure:
     * -                       parentNode
     * ---------------------------------------------------------------------------------------------
     * |                      subProcessNode                                                       |
     * |   ------------------------------------------------------------------------------          |
     * |   |                       containerNode                                        |          |
     * |   |   -----------------------------------------------------------              |          |
     * |   |   | startNode --(edge1)--> intermNode --(edge2)--> endNode  |     nodeA    |   nodeB  |
     * |   |   -----------------------------------------------------------              |          |
     * |   |                                                                            |          |
     * |   ------------------------------------------------------------------------------          |
     * |                                                                                           |
     * ---------------------------------------------------------------------------------------------
     */

    public static class Level2Graph extends Level1Graph {

        public SubProcessNodeBean subProcessNodeBean;
        public Node subProcessNode;
        public Node nodeB;
    }

    public static Level2Graph newLevel2Graph(TestingGraphMockHandler graphTestHandler) {
        Level2Graph result = new Level2Graph();
        populate(graphTestHandler, result);
        result.subProcessNodeBean = new SubProcessNodeBean();
        graphTestHandler.mockDefAttributes(result.subProcessNodeBean,
                                           getDefinitionId(SubProcessNodeBean.class),
                                           Optional.of(new String[]{"subProcessNodeBeanLabel", "all"}));
        result.subProcessNode = graphTestHandler.newNode("subProcessNodeUUID",
                                                         Optional.of(result.subProcessNodeBean));
        result.nodeB =
                graphTestHandler.newNode("nodeBUUID",
                                         "nodeBId",
                                         Optional.of(new String[]{"nodeBLabel", "all"}));
        graphTestHandler
                .setChild(result.parentNode,
                          result.subProcessNode)
                .setChild(result.subProcessNode,
                          result.containerNode)
                .setChild(result.containerNode,
                          result.startNode)
                .setChild(result.containerNode,
                          result.intermNode)
                .setChild(result.containerNode,
                          result.endNode)
                .addEdge(result.edge1,
                         result.startNode)
                .connectTo(result.edge1,
                           result.intermNode)
                .addEdge(result.edge2,
                         result.intermNode)
                .connectTo(result.edge2,
                           result.endNode)
                .setChild(result.subProcessNode,
                          result.nodeA)
                .setChild(result.parentNode,
                          result.nodeB);
        return result;
    }

    private static void populate(TestingGraphMockHandler graphTestHandler,
                                 Level1Graph result) {
        result.graph = graphTestHandler.graph;
        // Parent
        result.parentNodeBean = new ParentNodeBean();
        graphTestHandler.mockDefAttributes(result.parentNodeBean,
                                           getDefinitionId(ParentNodeBean.class),
                                           Optional.of(new String[]{"parentNodeLabel", "all"}));
        result.parentNode =
                graphTestHandler.newNode("parentNodeUUID",
                                         Optional.of(result.parentNodeBean));
        // Container.
        result.containerNodeBean = new ContainerNodeBean();
        graphTestHandler.mockDefAttributes(result.containerNodeBean,
                                           getDefinitionId(ContainerNodeBean.class),
                                           Optional.of(new String[]{"containerNodeLabel", "all"}));
        result.containerNode =
                graphTestHandler.newNode("containerNodeUUID",
                                         Optional.of(result.containerNodeBean));

        result.startNode =
                graphTestHandler.newNode("startNodeUUID",
                                         "startNodeId",
                                         Optional.of(new String[]{"startNodeLabel", "all"}));
        result.intermNode =
                graphTestHandler.newNode("intermNodeUUID",
                                         "intermNodeId",
                                         Optional.of(new String[]{"intermNodeLabel", "all"}));
        result.endNode =
                graphTestHandler.newNode("endNodeUUID",
                                         "endNodeId",
                                         Optional.of(new String[]{"endNodeLabel", "all"}));
        result.edge1 =
                graphTestHandler.newEdge("edge1UUID",
                                         "edgeId",
                                         Optional.empty());
        result.edge2 =
                graphTestHandler.newEdge("edge2UUID",
                                         "edgeId",
                                         Optional.empty());
        result.nodeA =
                graphTestHandler.newNode("nodeAUUID",
                                         "nodeAId",
                                         Optional.of(new String[]{"nodeALabel", "all"}));
    }

    public static ContextualGraphCommandExecutionContext createConstrainedExecutionContext(TestingGraphMockHandler graphTestHandler,
                                                                                           String ruleId,
                                                                                           Class<?> constrainedType) {
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext(graphTestHandler);
        RuleExtension edge1ParentMatch = new RuleExtension("connectorParentMatchExtRule", ruleId)
                .setHandlerType(ConnectorParentsMatchHandler.class)
                .setArguments(new String[]{RULE_ERROR_MESSAGE})
                .setTypeArguments(new Class[]{constrainedType});
        graphTestHandler.ruleSet.getRules().add(edge1ParentMatch);
        return executionContext;
    }

    public static ContextualGraphCommandExecutionContext createExecutionContext(TestingGraphMockHandler graphTestHandler) {
        RuleManager ruleManager = configureDomain(graphTestHandler);
        return new ContextualGraphCommandExecutionContext(graphTestHandler.getDefinitionManager(),
                                                          graphTestHandler.getFactoryManager(),
                                                          ruleManager,
                                                          graphTestHandler.graphIndex,
                                                          graphTestHandler.ruleSet);
    }

    public static RuleManager configureDomain(TestingGraphMockHandler graphTestHandler) {
        CanConnect connectionRule1 = new CanConnect("allowConnectionsForEdge1",
                                                    "edgeId",
                                                    Stream.of(new CanConnect.PermittedConnection("all",
                                                                                               "all"))
                                                            .collect(Collectors.toList())
        );
        CanContain parentNodeContainment = new CanContain("parentCanContainAll",
                                                          getDefinitionId(ParentNodeBean.class),
                                                          Stream.of("all").collect(Collectors.toSet())
        );
        CanContain containerNodeContainment = new CanContain("containerNodeCanContainAll",
                                                             getDefinitionId(ContainerNodeBean.class),
                                                             Stream.of("all").collect(Collectors.toSet())
        );
        CanContain subProcessNodeContainment = new CanContain("subProcessNodeCanContainAll",
                                                              getDefinitionId(SubProcessNodeBean.class),
                                                              Stream.of("all").collect(Collectors.toSet())
        );

        graphTestHandler.ruleSet.getRules().add(connectionRule1);
        graphTestHandler.ruleSet.getRules().add(parentNodeContainment);
        graphTestHandler.ruleSet.getRules().add(containerNodeContainment);
        graphTestHandler.ruleSet.getRules().add(subProcessNodeContainment);

        RuleManager ruleManager = graphTestHandler.createRuleManagerImplementation();

        ConnectorParentsMatchConnectionHandler connectionHandler = new ConnectorParentsMatchConnectionHandler(graphTestHandler.getDefinitionManager());
        ConnectorParentsMatchContainmentHandler containmentHandler = new ConnectorParentsMatchContainmentHandler(graphTestHandler.getDefinitionManager(), new TreeWalkTraverseProcessorImpl());
        RuleExtensionMultiHandler multiHandler = new RuleExtensionMultiHandler();
        ConnectorParentsMatchHandler matchHandler = new ConnectorParentsMatchHandler(connectionHandler, containmentHandler, multiHandler);
        matchHandler.init();
        ruleManager.registry().register(matchHandler);

        return ruleManager;
    }

    public static class ParentNodeBean {

    }

    public static class ContainerNodeBean {

    }

    public static class SubProcessNodeBean {

    }

    public static void assertSuccessfullResult(CommandResult<RuleViolation> result) {
        assertFalse(isError(result));
        assertFalse(isWarn(result));
        assertFalse(result.getViolations().iterator().hasNext());
    }

    public static void assertRuleFailedResult(CommandResult<RuleViolation> result) {
        assertTrue(isError(result));
        assertTrue(result.getViolations().iterator().hasNext());
        RuleViolation ruleViolation = result.getViolations().iterator().next();
        assertEquals(RULE_ERROR_MESSAGE, ruleViolation.getArguments().get()[0]);
    }
}
