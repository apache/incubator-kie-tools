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


package org.kie.workbench.common.stunner.core.rule.context.impl;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstances;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ConnectorCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ElementCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.context.NodeDockingContext;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.Silent.class)
public class StatefulGraphEvaluationContextsTests {

    private static final Object RESULT = mock(Object.class);
    private static final Function<GraphEvaluationContext, Object> EVALUATOR = context -> RESULT;

    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphInstances.Level1Graph graphInstance;
    private RuleEvaluationContextBuilder.GraphContextBuilderImpl<StatefulGraphEvaluationState> contextBuilder;
    private StatefulGraphEvaluationState state;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        graphTestHandler = new TestingGraphMockHandler();
        graphInstance = TestingGraphInstances.newLevel1Graph(graphTestHandler);
        state = new StatefulGraphEvaluationState(graphInstance.graph);
        contextBuilder = new RuleEvaluationContextBuilder.GraphContextBuilderImpl<>(state);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCardinalityState() {
        Node someNewNode = new NodeImpl("someNewNodeUUID");
        // Test cardinality - ADD
        ElementCardinalityContext addNewNodeContext = contextBuilder.cardinality(singletonList(someNewNode),
                                                                                 CardinalityContext.Operation.ADD);
        Object result = StatefulGraphEvaluationContexts.evaluate(addNewNodeContext, EVALUATOR);
        assertEquals(RESULT, result);
        Iterable<Node> stateNodes = state.getCardinalityState().nodes();
        long nodesCount = count(stateNodes);
        assertTrue(state.getCardinalityState().getAddedElements().contains(someNewNode));
        assertEquals(7, nodesCount);
        // Test cardinality - DELETE
        ElementCardinalityContext removeNodeAContext = contextBuilder.cardinality(singletonList(graphInstance.nodeA),
                                                                                  CardinalityContext.Operation.DELETE);
        result = StatefulGraphEvaluationContexts.evaluate(removeNodeAContext, EVALUATOR);
        assertEquals(RESULT, result);
        stateNodes = state.getCardinalityState().nodes();
        nodesCount = count(stateNodes);
        assertTrue(state.getCardinalityState().getDeletedElements().contains(graphInstance.nodeA));
        assertEquals(6, nodesCount);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConnectorCardinalityState() {
        Edge someNewEdge = graphTestHandler.buildEdge("someNewEdgeUUID",
                                                      graphTestHandler.newDef("someNewEdgeId",
                                                                              Optional.of(new String[]{"someNewEdgeLabel"})));

        // Test cardinality - Add outgoing edge.
        ConnectorCardinalityContext addOutgoingEdge = contextBuilder.edgeCardinality(graphInstance.startNode,
                                                                                     someNewEdge,
                                                                                     EdgeCardinalityContext.Direction.OUTGOING,
                                                                                     Optional.of(CardinalityContext.Operation.ADD));
        Object result = StatefulGraphEvaluationContexts.evaluate(addOutgoingEdge, EVALUATOR);
        assertEquals(RESULT, result);
        Collection<Edge<? extends View<?>, Node>> outgoingEdges = state.getConnectorCardinalityState().getOutgoing(graphInstance.startNode);
        assertEquals(2, outgoingEdges.size());
        assertTrue(outgoingEdges.contains(someNewEdge));

        // Test cardinality - Add incoming edge.
        ConnectorCardinalityContext addIncomingEdge = contextBuilder.edgeCardinality(graphInstance.intermNode,
                                                                                     someNewEdge,
                                                                                     EdgeCardinalityContext.Direction.INCOMING,
                                                                                     Optional.of(CardinalityContext.Operation.ADD));
        result = StatefulGraphEvaluationContexts.evaluate(addIncomingEdge, EVALUATOR);
        assertEquals(RESULT, result);
        Collection<Edge<? extends View<?>, Node>> incomingEdges = state.getConnectorCardinalityState().getIncoming(graphInstance.intermNode);
        assertEquals(2, incomingEdges.size());
        assertTrue(incomingEdges.contains(someNewEdge));

        // Test cardinality - delete outgoing edge.
        ConnectorCardinalityContext deleteOutgoingEdge = contextBuilder.edgeCardinality(graphInstance.startNode,
                                                                                        graphInstance.edge1,
                                                                                        EdgeCardinalityContext.Direction.OUTGOING,
                                                                                        Optional.of(CardinalityContext.Operation.DELETE));
        result = StatefulGraphEvaluationContexts.evaluate(deleteOutgoingEdge, EVALUATOR);
        assertEquals(RESULT, result);
        outgoingEdges = state.getConnectorCardinalityState().getOutgoing(graphInstance.startNode);
        assertFalse(outgoingEdges.contains(graphInstance.edge1));

        // Test cardinality - delete incoming edge.
        ConnectorCardinalityContext deleteIncomingEdge = contextBuilder.edgeCardinality(graphInstance.intermNode,
                                                                                        graphInstance.edge1,
                                                                                        EdgeCardinalityContext.Direction.INCOMING,
                                                                                        Optional.of(CardinalityContext.Operation.DELETE));
        result = StatefulGraphEvaluationContexts.evaluate(deleteIncomingEdge, EVALUATOR);
        assertEquals(RESULT, result);
        incomingEdges = state.getConnectorCardinalityState().getIncoming(graphInstance.intermNode);
        assertFalse(incomingEdges.contains(graphInstance.edge1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConnectionState() {
        GraphConnectionContext context = contextBuilder.connection(graphInstance.edge1,
                                                                   Optional.of(graphInstance.intermNode),
                                                                   Optional.of(graphInstance.endNode));
        Object result = StatefulGraphEvaluationContexts.evaluate(context, EVALUATOR);
        assertEquals(RESULT, result);

        Node<? extends View<?>, ? extends Edge> source = state.getConnectionState().getSource(graphInstance.edge1);
        Node<? extends View<?>, ? extends Edge> target = state.getConnectionState().getTarget(graphInstance.edge1);
        assertEquals(graphInstance.intermNode, source);
        assertEquals(graphInstance.endNode, target);

        context = contextBuilder.connection(graphInstance.edge2,
                                            Optional.of(graphInstance.intermNode),
                                            Optional.of(graphInstance.nodeA));
        result = StatefulGraphEvaluationContexts.evaluate(context, EVALUATOR);
        assertEquals(RESULT, result);

        source = state.getConnectionState().getSource(graphInstance.edge2);
        target = state.getConnectionState().getTarget(graphInstance.edge2);
        assertEquals(graphInstance.intermNode, source);
        assertEquals(graphInstance.nodeA, target);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testContainmentState() {
        NodeContainmentContext context = contextBuilder.containment(graphInstance.containerNode,
                                                                    graphInstance.nodeA);
        Object result = StatefulGraphEvaluationContexts.evaluate(context, EVALUATOR);
        assertEquals(RESULT, result);
        assertEquals(graphInstance.containerNode, state.getContainmentState().getParent(graphInstance.startNode));
        assertEquals(graphInstance.containerNode, state.getContainmentState().getParent(graphInstance.intermNode));
        assertEquals(graphInstance.containerNode, state.getContainmentState().getParent(graphInstance.endNode));
        assertEquals(graphInstance.containerNode, state.getContainmentState().getParent(graphInstance.nodeA));
        context = contextBuilder.containment(graphInstance.parentNode,
                                             graphInstance.startNode);
        result = StatefulGraphEvaluationContexts.evaluate(context, EVALUATOR);
        assertEquals(RESULT, result);
        assertEquals(graphInstance.parentNode, state.getContainmentState().getParent(graphInstance.startNode));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDockingState() {
        NodeDockingContext context = contextBuilder.docking(graphInstance.startNode,
                                                            graphInstance.nodeA);
        Object result = StatefulGraphEvaluationContexts.evaluate(context, EVALUATOR);
        assertEquals(RESULT, result);
        assertEquals(graphInstance.startNode, state.getDockingState().getDockedTo(graphInstance.nodeA));
    }

    private static long count(Iterable<Node> nodes) {
        return StreamSupport.stream(nodes.spliterator(), false).count();
    }
}
