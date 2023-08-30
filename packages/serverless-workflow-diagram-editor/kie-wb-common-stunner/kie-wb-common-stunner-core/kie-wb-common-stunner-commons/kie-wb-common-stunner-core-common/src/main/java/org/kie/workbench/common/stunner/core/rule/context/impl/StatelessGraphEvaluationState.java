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
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

public class StatelessGraphEvaluationState extends AbstractGraphEvaluationState {

    private final StatelessCardinalityState cardinalityState;
    private final StatelessConnectorCardinalityState connectorCardinalityState;
    private final StatelessConnectionState connectionState;
    private final StatelessContainmentState containmentState;
    private final StatelessDockingState dockingState;

    public StatelessGraphEvaluationState(final Graph<?, ? extends Node> graph) {
        super(graph);
        this.cardinalityState = new StatelessCardinalityState();
        this.connectorCardinalityState = new StatelessConnectorCardinalityState();
        this.connectionState = new StatelessConnectionState();
        this.containmentState = new StatelessContainmentState();
        this.dockingState = new StatelessDockingState();
    }

    @Override
    public StatelessCardinalityState getCardinalityState() {
        return cardinalityState;
    }

    @Override
    public StatelessConnectorCardinalityState getConnectorCardinalityState() {
        return connectorCardinalityState;
    }

    @Override
    public StatelessConnectionState getConnectionState() {
        return connectionState;
    }

    @Override
    public StatelessContainmentState getContainmentState() {
        return containmentState;
    }

    @Override
    public StatelessDockingState getDockingState() {
        return dockingState;
    }

    public class StatelessCardinalityState implements CardinalityState {

        @Override
        @SuppressWarnings("unchecked")
        public Iterable<Node> nodes() {
            return (Iterable<Node>) getGraph().nodes();
        }
    }

    public static class StatelessConnectorCardinalityState implements ConnectorCardinalityState {

        @Override
        public Collection<Edge<? extends View<?>, Node>> getIncoming(final Node<? extends View<?>, Edge> node) {
            return castValidEdges(node.getInEdges());
        }

        @Override
        public Collection<Edge<? extends View<?>, Node>> getOutgoing(final Node<? extends View<?>, Edge> node) {
            return castValidEdges(node.getOutEdges());
        }

        private static Collection<Edge<? extends View<?>, Node>> castValidEdges(Collection<Edge> edges) {
            return unmodifiableCast(edges.stream()
                                            .filter(e -> e.getContent() instanceof Definition)
                                            .collect(Collectors.toList()));
        }
    }

    public static class StatelessConnectionState implements ConnectionState {

        @Override
        @SuppressWarnings("unchecked")
        public Node<? extends View<?>, ? extends Edge> getSource(final Edge<? extends View<?>, ? extends Node> edge) {
            return edge.getSourceNode();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Node<? extends View<?>, ? extends Edge> getTarget(final Edge<? extends View<?>, ? extends Node> edge) {
            return edge.getTargetNode();
        }
    }

    public static class StatelessContainmentState implements ContainmentState {

        @Override
        @SuppressWarnings("unchecked")
        public Element<? extends Definition<?>> getParent(final Node<? extends Definition<?>, ? extends Edge> node) {
            return (Element<? extends Definition<?>>) GraphUtils.getParent(node);
        }
    }

    public static class StatelessDockingState implements DockingState {

        @Override
        @SuppressWarnings("unchecked")
        public Element<? extends Definition<?>> getDockedTo(final Node<? extends Definition<?>, ? extends Edge> node) {
            return dockParentSupplier.apply(node);
        }
    }
}
