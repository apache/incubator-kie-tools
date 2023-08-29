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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;

public class StatefulGraphEvaluationState extends AbstractGraphEvaluationState {

    private final StatefulCardinalityState cardinality;
    private final StatefulConnectorCardinalityState connectorCardinality;
    private final StatefulConnectionState connection;
    private final StatefulContainmentState containment;
    private final StatefulDockingState docking;

    public StatefulGraphEvaluationState(final Graph<?, ? extends Node> graph) {
        super(graph);
        this.cardinality = new StatefulCardinalityState();
        this.connectorCardinality = new StatefulConnectorCardinalityState();
        this.connection = new StatefulConnectionState();
        this.containment = new StatefulContainmentState();
        this.docking = new StatefulDockingState();
    }

    StatefulGraphEvaluationState(final Graph<?, ? extends Node> graph,
                                 final StatefulCardinalityState cardinality,
                                 final StatefulConnectorCardinalityState connectorCardinality,
                                 final StatefulConnectionState connection,
                                 final StatefulContainmentState containment,
                                 final StatefulDockingState docking) {
        super(graph);
        this.cardinality = cardinality;
        this.connectorCardinality = connectorCardinality;
        this.connection = connection;
        this.containment = containment;
        this.docking = docking;
    }

    @Override
    public StatefulCardinalityState getCardinalityState() {
        return cardinality;
    }

    @Override
    public StatefulConnectorCardinalityState getConnectorCardinalityState() {
        return connectorCardinality;
    }

    @Override
    public StatefulConnectionState getConnectionState() {
        return connection;
    }

    @Override
    public StatefulContainmentState getContainmentState() {
        return containment;
    }

    @Override
    public StatefulDockingState getDockingState() {
        return docking;
    }

    public void clear() {
        cardinality.clear();
        connectorCardinality.clear();
        connection.clear();
        containment.clear();
        docking.clear();
    }

    public class StatefulCardinalityState implements CardinalityState {

        private final Collection<Element<? extends View<?>>> added;
        private final Collection<Element<? extends View<?>>> deleted;

        public StatefulCardinalityState() {
            this.added = new HashSet<>();
            this.deleted = new HashSet<>();
        }

        boolean add(final Element<? extends View<?>> element) {
            return added.add(element);
        }

        boolean delete(final Element<? extends View<?>> element) {
            return deleted.add(element);
        }

        void clear() {
            added.clear();
            deleted.clear();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Iterable<Node> nodes() {
            final Iterable<Node> nodes = (Iterable<Node>) getGraph().nodes();
            return StreamSupport.stream(nodes.spliterator(), false)
                    .filter(e -> !getDeletedElements().contains(e))
                    .collect(Collectors.collectingAndThen(Collectors.toList(),
                                                          this::appendAddedNodes));
        }

        private Collection<Node> appendAddedNodes(final Collection<Node> nodes) {
            getAddedElements().stream()
                    .filter(e -> Objects.nonNull(e.asNode()))
                    .forEach(node -> nodes.add((Node) node));
            return nodes;
        }

        Collection<Element<? extends View<?>>> getAddedElements() {
            return Collections.unmodifiableCollection(added);
        }

        Collection<Element<? extends View<?>>> getDeletedElements() {
            return Collections.unmodifiableCollection(deleted);
        }
    }

    public class StatefulConnectorCardinalityState implements ConnectorCardinalityState {

        private final Map<Node, Collection<Edge>> incoming;
        private final Map<Node, Collection<Edge>> outgoing;

        public StatefulConnectorCardinalityState() {
            this.incoming = new HashMap<>();
            this.outgoing = new HashMap<>();
        }

        void clear() {
            incoming.clear();
            outgoing.clear();
        }

        void addIncoming(final Node<? extends View<?>, Edge> node,
                         final Edge<? extends View<?>, Node> connector) {
            final Collection<Edge> inEdges = getOrPutIncoming(node);
            inEdges.add(connector);
        }

        boolean deleteIncoming(final Node<? extends View<?>, Edge> node,
                               final Edge<? extends View<?>, Node> connector) {
            final Collection<Edge> inEdges = getOrPutIncoming(node);
            return inEdges.remove(connector);
        }

        void addOutgoing(final Node<? extends View<?>, Edge> node,
                         final Edge<? extends View<?>, Node> connector) {
            final Collection<Edge> outEdges = getOrPutOutgoing(node);
            outEdges.add(connector);
        }

        boolean deleteOutgoing(final Node<? extends View<?>, Edge> node,
                               final Edge<? extends View<?>, Node> connector) {
            final Collection<Edge> outEdges = getOrPutOutgoing(node);
            return outEdges.remove(connector);
        }

        Map<Node, Collection<Edge>> getIncoming() {
            return incoming;
        }

        Map<Node, Collection<Edge>> getOutgoing() {
            return outgoing;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Collection<Edge<? extends View<?>, Node>> getIncoming(final Node<? extends View<?>, Edge> node) {
            return unmodifiableCast(getOrPutIncoming(node));
        }

        @Override
        @SuppressWarnings("unchecked")
        public Collection<Edge<? extends View<?>, Node>> getOutgoing(final Node<? extends View<?>, Edge> node) {
            return unmodifiableCast(getOrPutOutgoing(node));
        }

        private Collection<Edge> getOrPutIncoming(final Node<? extends View<?>, Edge> node) {
            return getOrPut(incoming, node.getInEdges(), node);
        }

        private Collection<Edge> getOrPutOutgoing(final Node<? extends View<?>, Edge> node) {
            return getOrPut(outgoing, node.getOutEdges(), node);
        }
    }

    private static Collection<Edge> getOrPut(final Map<Node, Collection<Edge>> edges,
                                             final Collection<Edge> value,
                                             final Node<? extends View<?>, Edge> node) {
        Collection<Edge> result = edges.get(node);
        if (null == result) {
            result = new ArrayList<>();
            if (null != value) {
                result.addAll(value.stream()
                                      .filter(e -> e.getContent() instanceof Definition)
                                      .collect(Collectors.toList()));
            }
            edges.put(node, result);
        }
        return result;
    }

    public static class StatefulConnectionState implements ConnectionState {

        private final StateMap<Edge, Node> sources;
        private final StateMap<Edge, Node> targets;

        public StatefulConnectionState() {
            this.sources = new StateMap<>(Edge::getSourceNode);
            this.targets = new StateMap<>(Edge::getTargetNode);
        }

        void clear() {
            sources.clear();
            targets.clear();
        }

        StatefulConnectionState setSourceNode(final Edge edge,
                                              final Node node) {
            sources.set(edge, node);
            return this;
        }

        StatefulConnectionState setTargetNode(final Edge edge,
                                              final Node node) {
            targets.set(edge, node);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Node<? extends View<?>, ? extends Edge> getSource(final Edge<? extends View<?>, ? extends Node> edge) {
            return sources.get(edge);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Node<? extends View<?>, ? extends Edge> getTarget(final Edge<? extends View<?>, ? extends Node> edge) {
            return targets.get(edge);
        }
    }

    public static class StatefulContainmentState implements ContainmentState {

        private final StateMap<Node, Element> parents;

        public StatefulContainmentState() {
            this.parents = new StateMap<>(GraphUtils::getParent);
        }

        void clear() {
            parents.clear();
        }

        StatefulContainmentState setParent(final Node node,
                                           final Element parent) {
            parents.set(node, parent);
            return this;
        }

        StateMap<Node, Element> getParents() {
            return parents;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Element<? extends Definition<?>> getParent(final Node<? extends Definition<?>, ? extends Edge> node) {
            Element parent = parents.get(node);
            return parent;
        }

        @SuppressWarnings("unchecked")
        public static Element<? extends Definition<?>> getParent(final NodeContainmentContext context,
                                                                 final Node<? extends Definition<?>, ? extends Edge> node) {
            if (context.getCandidates().contains(node)) {
                return context.getParent();
            } else {
                return context.getState().getContainmentState().getParent(node);
            }
        }
    }

    public static class StatefulDockingState implements DockingState {

        private final StateMap<Node, Element> docks;

        public StatefulDockingState() {
            this.docks = new StateMap<>(dockParentSupplier::apply);
        }

        void clear() {
            docks.clear();
        }

        StatefulDockingState setDockedTo(final Node node,
                                         final Element parent) {
            docks.set(node, parent);
            return this;
        }

        StateMap<Node, Element> getDockedElements() {
            return docks;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Element<? extends Definition<?>> getDockedTo(final Node<? extends Definition<?>, ? extends Edge> node) {
            return docks.get(node);
        }
    }

    public static class StateMap<T, V> {

        private final Map<T, Value<V>> state;
        private final Function<T, V> defaultValueSupplier;

        public StateMap(final Function<T, V> defaultValueSupplier) {
            this.defaultValueSupplier = defaultValueSupplier;
            this.state = new HashMap<>();
        }

        public void set(final T key,
                        final V value) {
            state.put(key, new Value<>(value));
        }

        public V get(final T key) {
            final Value<V> value = state.get(key);
            if (null == value) {
                return defaultValueSupplier.apply(key);
            }
            return value.get();
        }

        public boolean isEmpty() {
            return state.isEmpty();
        }

        public void clear() {
            state.clear();
        }
    }

    public static class Value<V> {

        private final V value;

        private Value(final V value) {
            this.value = value;
        }

        public V get() {
            return value;
        }
    }
}
