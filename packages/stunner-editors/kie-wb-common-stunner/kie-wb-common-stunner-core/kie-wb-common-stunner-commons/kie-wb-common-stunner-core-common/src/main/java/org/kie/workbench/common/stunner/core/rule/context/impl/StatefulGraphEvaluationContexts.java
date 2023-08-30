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

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ConnectorCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ElementCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.context.NodeDockingContext;

public class StatefulGraphEvaluationContexts {

    private static final Map<Class<? extends GraphEvaluationContext>, ContextConsumer<? extends GraphEvaluationContext>> CONSUMERS_BY_TYPE =
            Stream.of(new AbstractMap.SimpleEntry<>(ElementCardinalityContext.class, new ElementCardinalityContextConsumer()),
                      new AbstractMap.SimpleEntry<>(ConnectorCardinalityContext.class, new ConnectorCardinalityContextConsumer()),
                      new AbstractMap.SimpleEntry<>(GraphConnectionContext.class, new ConnectionStateContextConsumer()),
                      new AbstractMap.SimpleEntry<>(NodeContainmentContext.class, new ContainmentStateContextConsumer()),
                      new AbstractMap.SimpleEntry<>(NodeDockingContext.class, new DockingStateContextConsumer()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    @SuppressWarnings("unchecked")
    public static <T> T evaluate(final GraphEvaluationContext context,
                                 final Function<GraphEvaluationContext, T> function) {
        final T result = function.apply(context);
        consumeContextAndPopulateState(context,
                                       (StatefulGraphEvaluationState) context.getState());
        return result;
    }

    @SuppressWarnings("unchecked")
    private static void consumeContextAndPopulateState(final GraphEvaluationContext context,
                                                       final StatefulGraphEvaluationState state) {
        final Class<? extends GraphEvaluationContext> type = (Class<? extends GraphEvaluationContext>) context.getType();
        final ContextConsumer<GraphEvaluationContext> contextConsumer = (ContextConsumer<GraphEvaluationContext>) CONSUMERS_BY_TYPE.get(type);
        if (null == contextConsumer) {
            throw new IllegalStateException("No evaluation context consumer present for context [" + type.getName() + "]");
        }
        contextConsumer.consume(state, context);
    }

    private interface ContextConsumer<C extends GraphEvaluationContext> {

        void consume(StatefulGraphEvaluationState state,
                     C context);
    }

    private static class ElementCardinalityContextConsumer
            implements ContextConsumer<ElementCardinalityContext> {

        @Override
        @SuppressWarnings("unchecked")
        public void consume(final StatefulGraphEvaluationState state,
                            final ElementCardinalityContext context) {
            final StatefulGraphEvaluationState.StatefulCardinalityState cardinalityState = state.getCardinalityState();
            final Optional<CardinalityContext.Operation> operation = context.getOperation();
            final Collection<Element<? extends View<?>>> candidates = context.getCandidates();
            if (operation.isPresent()) {
                final boolean isAdd = isAdd(operation.get());
                final Consumer<Element> addORDeleteIt = candidate -> {
                    if (isAdd) {
                        cardinalityState.add(candidate);
                    } else {
                        cardinalityState.delete(candidate);
                    }
                };
                candidates.forEach(addORDeleteIt::accept);
            }
        }
    }

    private static class ConnectorCardinalityContextConsumer
            implements ContextConsumer<ConnectorCardinalityContext> {

        @Override
        @SuppressWarnings("unchecked")
        public void consume(final StatefulGraphEvaluationState state,
                            final ConnectorCardinalityContext context) {
            final StatefulGraphEvaluationState.StatefulConnectorCardinalityState connectorCardinalityState =
                    state.getConnectorCardinalityState();
            final Edge<? extends View<?>, Node> edge = context.getEdge();
            final Element<? extends View<?>> candidate = context.getCandidate();
            final EdgeCardinalityContext.Direction direction = context.getDirection();
            final Optional<CardinalityContext.Operation> operation = context.getOperation();
            if (!operation.isPresent() || null == candidate.asNode()) {
                return;
            }
            final Node<? extends View<?>, Edge> node = (Node<? extends View<?>, Edge>) candidate;
            if (isIncoming(direction)) {
                if (isAdd(operation.get())) {
                    connectorCardinalityState.addIncoming(node, edge);
                } else {
                    connectorCardinalityState.deleteIncoming(node, edge);
                }
            } else {
                if (isAdd(operation.get())) {
                    connectorCardinalityState.addOutgoing(node, edge);
                } else {
                    connectorCardinalityState.deleteOutgoing(node, edge);
                }
            }
        }
    }

    private static class ConnectionStateContextConsumer
            implements ContextConsumer<GraphConnectionContext> {

        @Override
        @SuppressWarnings("unchecked")
        public void consume(final StatefulGraphEvaluationState state,
                            final GraphConnectionContext context) {
            final StatefulGraphEvaluationState.StatefulConnectionState connectionState = state.getConnectionState();
            final Edge connector = context.getConnector();
            final Optional<Node<? extends View<?>, ? extends Edge>> source = context.getSource();
            final Optional<Node<? extends View<?>, ? extends Edge>> target = context.getTarget();
            connectionState.setSourceNode(connector, source.orElse(null));
            connectionState.setTargetNode(connector, target.orElse(null));
        }
    }

    private static class ContainmentStateContextConsumer
            implements ContextConsumer<NodeContainmentContext> {

        @Override
        @SuppressWarnings("unchecked")
        public void consume(final StatefulGraphEvaluationState state,
                            final NodeContainmentContext context) {
            final StatefulGraphEvaluationState.StatefulContainmentState containmentState = state.getContainmentState();
            final Element parent = context.getParent();
            final Collection<Node<? extends Definition<?>, ? extends Edge>> candidates = context.getCandidates();
            candidates.forEach(candidate -> containmentState.setParent(candidate, parent));
        }
    }

    private static class DockingStateContextConsumer
            implements ContextConsumer<NodeDockingContext> {

        @Override
        public void consume(final StatefulGraphEvaluationState state,
                            final NodeDockingContext context) {
            final StatefulGraphEvaluationState.StatefulDockingState dockingState = state.getDockingState();
            final Node candidate = context.getCandidate();
            final Element parent = context.getParent();
            dockingState.setDockedTo(candidate, parent);
        }
    }

    private static boolean isIncoming(EdgeCardinalityContext.Direction direction) {
        return direction.equals(EdgeCardinalityContext.Direction.INCOMING);
    }

    private static boolean isAdd(CardinalityContext.Operation operation) {
        return operation.equals(CardinalityContext.Operation.ADD);
    }
}
