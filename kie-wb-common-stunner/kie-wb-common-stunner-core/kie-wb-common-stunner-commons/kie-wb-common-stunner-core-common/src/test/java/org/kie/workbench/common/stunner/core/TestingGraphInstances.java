/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core;

import java.util.Optional;
import java.util.Set;

import org.kie.soup.commons.util.Lists;
import org.kie.soup.commons.util.Sets;
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
                                           newLabelsSet("subProcessNodeBeanLabel"));
        result.subProcessNode = graphTestHandler.newNode("subProcessNodeUUID",
                                                         Optional.of(result.subProcessNodeBean));
        result.nodeB =
                graphTestHandler.newNode("nodeBUUID",
                                         "nodeBId",
                                         newLabelsSet("nodeBLabel"));
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
                                           newLabelsSet("parentNodeLabel"));
        result.parentNode =
                graphTestHandler.newNode("parentNodeUUID",
                                         Optional.of(result.parentNodeBean));
        // Container.
        result.containerNodeBean = new ContainerNodeBean();
        graphTestHandler.mockDefAttributes(result.containerNodeBean,
                                           getDefinitionId(ContainerNodeBean.class),
                                           newLabelsSet("containerNodeLabel"));
        result.containerNode =
                graphTestHandler.newNode("containerNodeUUID",
                                         Optional.of(result.containerNodeBean));

        result.startNode =
                graphTestHandler.newNode("startNodeUUID",
                                         "startNodeId",
                                         newLabelsSet("startNodeLabel"));
        result.intermNode =
                graphTestHandler.newNode("intermNodeUUID",
                                         "intermNodeId",
                                         newLabelsSet("intermNodeLabel"));
        result.endNode =
                graphTestHandler.newNode("endNodeUUID",
                                         "endNodeId",
                                         newLabelsSet("endNodeLabel"));
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
                                         newLabelsSet("nodeALabel"));
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
        return new ContextualGraphCommandExecutionContext(graphTestHandler.definitionManager,
                                                          graphTestHandler.factoryManager,
                                                          ruleManager,
                                                          graphTestHandler.graphIndex,
                                                          graphTestHandler.ruleSet);
    }

    public static RuleManager configureDomain(TestingGraphMockHandler graphTestHandler) {
        CanConnect connectionRule1 = new CanConnect("allowConnectionsForEdge1",
                                                    "edgeId",
                                                    new Lists.Builder<CanConnect.PermittedConnection>()
                                                            .add(new CanConnect.PermittedConnection("all",
                                                                                                    "all"))
                                                            .build()
        );
        CanContain parentNodeContainment = new CanContain("parentCanContainAll",
                                                          getDefinitionId(ParentNodeBean.class),
                                                          new Sets.Builder<String>().add("all").build()
        );
        CanContain containerNodeContainment = new CanContain("containerNodeCanContainAll",
                                                             getDefinitionId(ContainerNodeBean.class),
                                                             new Sets.Builder<String>().add("all").build()
        );
        CanContain subProcessNodeContainment = new CanContain("subProcessNodeCanContainAll",
                                                              getDefinitionId(SubProcessNodeBean.class),
                                                              new Sets.Builder<String>().add("all").build()
        );

        graphTestHandler.ruleSet.getRules().add(connectionRule1);
        graphTestHandler.ruleSet.getRules().add(parentNodeContainment);
        graphTestHandler.ruleSet.getRules().add(containerNodeContainment);
        graphTestHandler.ruleSet.getRules().add(subProcessNodeContainment);

        RuleManager ruleManager = graphTestHandler.createRuleManagerImplementation();

        ConnectorParentsMatchConnectionHandler connectionHandler = new ConnectorParentsMatchConnectionHandler(graphTestHandler.definitionManager);
        ConnectorParentsMatchContainmentHandler containmentHandler = new ConnectorParentsMatchContainmentHandler(graphTestHandler.definitionManager, new TreeWalkTraverseProcessorImpl());
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

    public static Optional<Set<String>> newLabelsSet(String label) {
        return Optional.of(new Sets.Builder<String>().add(label).add("all").build());
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
