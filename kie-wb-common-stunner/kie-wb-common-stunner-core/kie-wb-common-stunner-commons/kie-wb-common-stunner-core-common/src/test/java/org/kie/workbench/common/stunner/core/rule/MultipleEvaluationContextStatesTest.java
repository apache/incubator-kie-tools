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

package org.kie.workbench.common.stunner.core.rule;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Lists;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.ContextualGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.kie.workbench.common.stunner.core.rule.impl.EdgeOccurrences;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.kie.workbench.common.stunner.core.TestingGraphInstances.newLabelsSet;
import static org.kie.workbench.common.stunner.core.command.util.CommandUtils.isError;
import static org.kie.workbench.common.stunner.core.command.util.CommandUtils.isWarn;

@RunWith(MockitoJUnitRunner.class)
public class MultipleEvaluationContextStatesTest {

    private TestGraph graph1Instance;
    private TestingGraphMockHandler graphTestHandler;

    @Before
    public void setUp() {
        graphTestHandler = new TestingGraphMockHandler();
        graph1Instance = newTestGraph(graphTestHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValidShortcut() {
        SafeDeleteNodeCommand deleteNodeCommand = new SafeDeleteNodeCommand(graph1Instance.intermNode);
        CommandResult<RuleViolation> result = deleteNodeCommand.allow(createExecutionContext());
        assertFalse(isError(result));
        assertFalse(isWarn(result));
        assertFalse(result.getViolations().iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllowShortcutWithRules() {
        SafeDeleteNodeCommand deleteNodeCommand = new SafeDeleteNodeCommand(graph1Instance.intermNode);
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = deleteNodeCommand.allow(executionContext);
        assertFalse(isError(result));
        assertFalse(isWarn(result));
        assertFalse(result.getViolations().iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteShortcutWithRules() {
        SafeDeleteNodeCommand deleteNodeCommand = new SafeDeleteNodeCommand(graph1Instance.intermNode);
        ContextualGraphCommandExecutionContext executionContext = createExecutionContext();
        CommandResult<RuleViolation> result = deleteNodeCommand.allow(executionContext);
        assertFalse(isError(result));
        assertFalse(isWarn(result));
        assertFalse(result.getViolations().iterator().hasNext());
    }

    @SuppressWarnings("unchecked")
    private ContextualGraphCommandExecutionContext createExecutionContext() {
        EdgeOccurrences rule1 = new EdgeOccurrences("restrictEdge1InEdgesCount",
                                                    "edgeBeanId",
                                                    "endNodeBeanLabel",
                                                    EdgeCardinalityContext.Direction.INCOMING,
                                                    0, 1);
        graphTestHandler.ruleSet.getRules().add(rule1);

        CanConnect connectionRule = new CanConnect("allowConnectionsForEdge1",
                                                   "edgeBeanId",
                                                   new Lists.Builder<CanConnect.PermittedConnection>()
                                                           .add(new CanConnect.PermittedConnection("all",
                                                                                                   "all"))
                                                           .build()
        );
        graphTestHandler.ruleSet.getRules().add(connectionRule);

        RuleManager ruleManager = graphTestHandler.createRuleManagerImplementation();
        return new ContextualGraphCommandExecutionContext(graphTestHandler.definitionManager,
                                                          graphTestHandler.factoryManager,
                                                          ruleManager,
                                                          graphTestHandler.graphIndex,
                                                          graphTestHandler.ruleSet);
    }

    /**
     * Structure:
     * startNode --(edge1)--> intermNode --(edge2)--> endNode
     * Both edge1 and edge2 are sharing same Definition "edgeBean".
     */
    public static class TestGraph {

        public Graph graph;
        public Object startNodeBean;
        public Node startNode;
        public Object intermNodeBean;
        public Node intermNode;
        public Object endNodeBean;
        public Node endNode;
        public Object edgeBean;
        public Edge edge1;
        public Edge edge2;
    }

    private static TestGraph newTestGraph(final TestingGraphMockHandler graphTestHandler) {
        TestGraph result = new TestGraph();
        result.graph = graphTestHandler.graph;
        result.startNodeBean = graphTestHandler.newDef("startNodeBeanId",
                                                       newLabelsSet("startNodeBeanLabel"));
        result.startNode =
                graphTestHandler.newNode("startNodeUUID",
                                         Optional.of(result.startNodeBean));
        result.intermNodeBean = graphTestHandler.newDef("intermNodeBeanId",
                                                        newLabelsSet("intermNodeBeanLabel"));
        result.intermNode =
                graphTestHandler.newNode("intermNodeUUID",
                                         Optional.of(result.intermNodeBean));
        result.endNodeBean = graphTestHandler.newDef("endNodeBeanId",
                                                     newLabelsSet("endNodeBeanLabel"));
        result.endNode =
                graphTestHandler.newNode("endNodeUUID",
                                         Optional.of(result.endNodeBean));
        result.edgeBean = graphTestHandler.newDef("edgeBeanId",
                                                  Optional.empty());
        result.edge1 =
                graphTestHandler.newEdge("edge1UUID",
                                         Optional.of(result.edgeBean));
        result.edge2 =
                graphTestHandler.newEdge("edge2UUID",
                                         Optional.of(result.edgeBean));
        graphTestHandler
                .addEdge(result.edge1,
                         result.startNode)
                .connectTo(result.edge1,
                           result.intermNode)
                .addEdge(result.edge2,
                         result.intermNode)
                .connectTo(result.edge2,
                           result.endNode);
        return result;
    }
}
