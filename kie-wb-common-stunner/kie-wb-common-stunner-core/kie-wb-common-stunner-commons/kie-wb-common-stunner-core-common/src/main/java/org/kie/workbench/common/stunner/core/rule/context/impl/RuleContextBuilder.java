/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.rule.context.impl;

import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ConnectionContext;
import org.kie.workbench.common.stunner.core.rule.context.ConnectorCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ContainmentContext;
import org.kie.workbench.common.stunner.core.rule.context.DockingContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ElementCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.context.NodeDockingContext;

/**
 * Just a helper class for creating an instance for any of the built-in rule
 * evaluation contexts.
 * <p>
 * Consider two main context types:
 * - Domain contexts - when there is no concrete graph structure, the evaluations
 * only depend on bean types and the runtime scenario.
 * E.g: It is used by some of the LookupManager subtypes - which need to evaluate
 * possible operations at a point where the graph instance has or cannot be yet updated.
 * <p>
 * - Graph contexts - the rules can be evaluated by using a graph structure and its nodes
 * and relationships, by consuming as well the rules defined in the domain moodel at runtime.
 * <p>
 * For each of the above context types, there exist the following built-in context types:
 * - Containment
 * - Docking
 * - Connection
 * - Cardinality
 * - Edge cardinality
 */
public class RuleContextBuilder {

    /**
     * Rule contexts for the domain model, do not depend on the graph structure,
     * just rules by id or role based for the domain model instances.
     */
    public static class DomainContexts {

        public static ContainmentContext containment(final Set<String> parentRoles,
                                                     final Set<String> candidateRoles) {
            return new ContainmentContextImpl("Domain containment",
                                              parentRoles,
                                              candidateRoles);
        }

        public static DockingContext docking(final Set<String> parentRoles,
                                             final Set<String> candidateRoles) {
            return new DockingContextImpl("Domain docking",
                                          parentRoles,
                                          candidateRoles);
        }

        public static ConnectionContext connection(final String connectorRole,
                                                   final Optional<Set<String>> sourceRoles,
                                                   final Optional<Set<String>> targetRoles) {
            return new ConnectionContextImpl("Domain connection",
                                             connectorRole,
                                             sourceRoles,
                                             targetRoles);
        }

        public static CardinalityContext cardinality(final Set<String> roles,
                                                     final int candidateCount,
                                                     final Optional<CardinalityContext.Operation> operation) {

            return new CardinalityContextImpl("Domain cardinality",
                                              roles,
                                              candidateCount,
                                              operation);
        }

        public static EdgeCardinalityContext edgeCardinality(final Set<String> candidateRoles,
                                                             final String edgeRole,
                                                             final int candidateCount,
                                                             final EdgeCardinalityContext.Direction direction,
                                                             final Optional<CardinalityContext.Operation> operation) {
            return new EdgeCardinalityContextImpl("Domain edge cardinality",
                                                  edgeRole,
                                                  candidateRoles,
                                                  candidateCount,
                                                  operation,
                                                  direction);
        }
    }

    /**
     * Rule contexts for the runtime graph structure.
     */
    public static class GraphContexts {

        public static NodeContainmentContext containment(final Graph<?, ? extends Node> graph,
                                                         final Element<? extends Definition<?>> parent,
                                                         final Node<? extends Definition<?>, ? extends Edge> candidate) {
            return new NodeContainmentContextImpl("Containment",
                                                  graph,
                                                  parent,
                                                  candidate);
        }

        public static NodeDockingContext docking(final Graph<?, ? extends Node> graph,
                                                 final Element<? extends Definition<?>> parent,
                                                 final Node<? extends Definition<?>, ? extends Edge> candidate) {
            return new NodeDockingContextImpl("Docking",
                                              graph,
                                              parent,
                                              candidate);
        }

        public static GraphConnectionContext connection(final Graph<?, ? extends Node> graph,
                                                        final Edge<? extends View<?>, ? extends Node> connector,
                                                        final Optional<Node<? extends View<?>, ? extends Edge>> sourceNode,
                                                        final Optional<Node<? extends View<?>, ? extends Edge>> targetNode) {
            return new GraphConnectionContextImpl("Connection",
                                                  graph,
                                                  connector,
                                                  sourceNode,
                                                  targetNode);
        }

        public static ElementCardinalityContext cardinality(final Graph<?, ? extends Node> graph,
                                                            final Optional<Element<? extends View<?>>> candidate,
                                                            final Optional<CardinalityContext.Operation> operation) {
            return new ElementCardinalityContextImpl("Cardinality",
                                                     graph,
                                                     candidate,
                                                     operation);
        }

        public static ConnectorCardinalityContext edgeCardinality(final Graph<?, ? extends Node> graph,
                                                                  final Element<? extends View<?>> candidate,
                                                                  final Edge<? extends View<?>, Node> edge,
                                                                  final EdgeCardinalityContext.Direction direction,
                                                                  final Optional<CardinalityContext.Operation> operation) {
            return new ConnectorCardinalityContextImpl("Edge cardinality",
                                                       graph,
                                                       candidate,
                                                       edge,
                                                       direction,
                                                       operation);
        }
    }
}
