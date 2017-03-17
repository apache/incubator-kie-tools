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

package org.kie.workbench.common.stunner.core.rule.context;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;

/**
 * Just a helper class for creating a bean for any of the built-in rule
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

    private static final boolean POLICY_CONTAINMENT_DEFAULT_DENY = true;
    private static final boolean POLICY_DOCKING_DEFAULT_DENY = true;
    private static final boolean POLICY_CONNECTION_DEFAULT_DENY = true;
    private static final boolean POLICY_CARDINALITY_DEFAULT_DENY = false;
    private static final boolean POLICY_EDGE_CARDINALITY_DEFAULT_DENY = false;

    /**
     * Rule contexts for the domain model, do not depend on the graph structure,
     * just rules by id or role based for the domain model instances.
     */
    public static class DomainContexts {

        public static ContainmentContext containment(final String id,
                                                     final Set<String> candidateRoles) {
            return new ContainmentContext() {
                @Override
                public String getName() {
                    return "Domain containment context";
                }

                @Override
                public boolean isDefaultDeny() {
                    return POLICY_CONTAINMENT_DEFAULT_DENY;
                }

                @Override
                public Class<? extends RuleEvaluationContext> getType() {
                    return ContainmentContext.class;
                }

                @Override
                public String getId() {
                    return id;
                }

                @Override
                public Set<String> getCandidateRoles() {
                    return candidateRoles;
                }
            };
        }

        public static DockingContext docking(final String id,
                                             final Set<String> candidateRoles) {
            return new DockingContext() {
                @Override
                public String getName() {
                    return "Domain docking context";
                }

                @Override
                public boolean isDefaultDeny() {
                    return POLICY_DOCKING_DEFAULT_DENY;
                }

                @Override
                public Class<? extends RuleEvaluationContext> getType() {
                    return DockingContext.class;
                }

                @Override
                public String getId() {
                    return id;
                }

                @Override
                public Set<String> getAllowedRoles() {
                    return candidateRoles;
                }
            };
        }

        public static ConnectionContext connection(final String connectorId,
                                                   final Optional<Set<String>> sourceRoles,
                                                   final Optional<Set<String>> targetRoles) {
            return new ConnectionContext() {
                @Override
                public String getName() {
                    return "Domain connection context";
                }

                @Override
                public boolean isDefaultDeny() {
                    return POLICY_CONNECTION_DEFAULT_DENY;
                }

                @Override
                public Class<? extends RuleEvaluationContext> getType() {
                    return ConnectionContext.class;
                }

                @Override
                public String getConnectorId() {
                    return connectorId;
                }

                @Override
                public Optional<Set<String>> getSourceRoles() {
                    return sourceRoles;
                }

                @Override
                public Optional<Set<String>> getTargetRoles() {
                    return targetRoles;
                }
            };
        }

        public static CardinalityContext cardinality(final Set<String> roles,
                                                     final int candidateCount,
                                                     final CardinalityContext.Operation operation) {
            return new CardinalityContext() {
                @Override
                public String getName() {
                    return "Domain cardinality context";
                }

                @Override
                public boolean isDefaultDeny() {
                    return POLICY_CARDINALITY_DEFAULT_DENY;
                }

                @Override
                public Class<? extends RuleEvaluationContext> getType() {
                    return CardinalityContext.class;
                }

                @Override
                public Set<String> getRoles() {
                    return roles;
                }

                @Override
                public int getCandidateCount() {
                    return candidateCount;
                }

                @Override
                public Operation getOperation() {
                    return operation;
                }
            };
        }

        public static CardinalityContext cardinality(final String role,
                                                     final int candidateCount,
                                                     final CardinalityContext.Operation operation) {
            return cardinality(new HashSet<String>(1) {
                                   {
                                       add(role);
                                   }
                               },
                               candidateCount,
                               operation);
        }

        public static EdgeCardinalityContext edgeCardinality(final Set<String> candidateRoles,
                                                             final String edgeId,
                                                             final int candidateCount,
                                                             final ConnectorCardinalityContext.Direction direction,
                                                             final CardinalityContext.Operation operation) {
            return new EdgeCardinalityContext() {
                @Override
                public String getName() {
                    return "Domain edge cardinality context";
                }

                @Override
                public boolean isDefaultDeny() {
                    return POLICY_EDGE_CARDINALITY_DEFAULT_DENY;
                }

                @Override
                public Class<? extends RuleEvaluationContext> getType() {
                    return EdgeCardinalityContext.class;
                }

                @Override
                public String getEdgeId() {
                    return edgeId;
                }

                @Override
                public ConnectorCardinalityContext.Direction getDirection() {
                    return direction;
                }

                @Override
                public Set<String> getRoles() {
                    return candidateRoles;
                }

                @Override
                public int getCandidateCount() {
                    return candidateCount;
                }

                @Override
                public Operation getOperation() {
                    return operation;
                }
            };
        }
    }

    /**
     * Rule contexts for the runtime graph structure.
     */
    public static class GraphContexts {

        public static NodeContainmentContext containment(final Graph<?, ? extends Node> graph,
                                                         final Optional<Element<? extends Definition<?>>> parent,
                                                         final Node<? extends Definition<?>, ? extends Edge> candidate) {
            return new NodeContainmentContext() {
                @Override
                public String getName() {
                    return "Graph containment context";
                }

                @Override
                public boolean isDefaultDeny() {
                    return POLICY_CONTAINMENT_DEFAULT_DENY;
                }

                @Override
                public Graph<?, ? extends Node> getGraph() {
                    return graph;
                }

                @Override
                public Class<? extends RuleEvaluationContext> getType() {
                    return NodeContainmentContext.class;
                }

                @Override
                public Optional<Element<? extends Definition<?>>> getParent() {
                    return parent;
                }

                @Override
                public Node<? extends Definition<?>, ? extends Edge> getCandidate() {
                    return candidate;
                }
            };
        }

        public static NodeDockingContext docking(final Graph<?, ? extends Node> graph,
                                                 final Optional<Element<? extends Definition<?>>> parent,
                                                 final Node<? extends Definition<?>, ? extends Edge> candidate) {
            return new NodeDockingContext() {
                @Override
                public String getName() {
                    return "Graph docking context";
                }

                @Override
                public boolean isDefaultDeny() {
                    return POLICY_DOCKING_DEFAULT_DENY;
                }

                @Override
                public Graph<?, ? extends Node> getGraph() {
                    return graph;
                }

                @Override
                public Class<? extends RuleEvaluationContext> getType() {
                    return NodeDockingContext.class;
                }

                @Override
                public Optional<Element<? extends Definition<?>>> getParent() {
                    return parent;
                }

                @Override
                public Node<? extends Definition<?>, ? extends Edge> getCandidate() {
                    return candidate;
                }
            };
        }

        public static GraphConnectionContext connection(final Graph<?, ? extends Node> graph,
                                                        final Edge<? extends View<?>, ? extends Node> connector,
                                                        final Optional<Node<? extends View<?>, ? extends Edge>> sourceNode,
                                                        final Optional<Node<? extends View<?>, ? extends Edge>> targetNode) {
            return new GraphConnectionContext() {
                @Override
                public String getName() {
                    return "Graph connection context";
                }

                @Override
                public boolean isDefaultDeny() {
                    return POLICY_CONNECTION_DEFAULT_DENY;
                }

                @Override
                public Graph<?, ? extends Node> getGraph() {
                    return graph;
                }

                @Override
                public Class<? extends RuleEvaluationContext> getType() {
                    return GraphConnectionContext.class;
                }

                @Override
                public Edge<? extends View<?>, ? extends Node> getConnector() {
                    return connector;
                }

                @Override
                public Optional<Node<? extends View<?>, ? extends Edge>> getSource() {
                    return sourceNode;
                }

                @Override
                public Optional<Node<? extends View<?>, ? extends Edge>> getTarget() {
                    return targetNode;
                }
            };
        }

        public static ElementCardinalityContext cardinality(final Graph<?, ? extends Node> graph,
                                                            final Element<? extends View<?>> candidate,
                                                            final CardinalityContext.Operation operation) {
            return new ElementCardinalityContext() {
                @Override
                public String getName() {
                    return "Graph cardinality context";
                }

                @Override
                public boolean isDefaultDeny() {
                    return POLICY_CARDINALITY_DEFAULT_DENY;
                }

                @Override
                public Class<? extends RuleEvaluationContext> getType() {
                    return ElementCardinalityContext.class;
                }

                @Override
                public Graph<?, ? extends Node> getGraph() {
                    return graph;
                }

                @Override
                public Element<? extends View<?>> getCandidate() {
                    return candidate;
                }

                @Override
                public CardinalityContext.Operation getOperation() {
                    return operation;
                }
            };
        }

        public static ConnectorCardinalityContext edgeCardinality(final Graph<?, ? extends Node> graph,
                                                                  final Element<? extends View<?>> candidate,
                                                                  final Edge<? extends View<?>, Node> edge,
                                                                  final ConnectorCardinalityContext.Direction direction,
                                                                  final CardinalityContext.Operation operation) {
            return new ConnectorCardinalityContext() {
                @Override
                public String getName() {
                    return "Graph connector cardinality context";
                }

                @Override
                public boolean isDefaultDeny() {
                    return POLICY_EDGE_CARDINALITY_DEFAULT_DENY;
                }

                @Override
                public Class<? extends RuleEvaluationContext> getType() {
                    return ConnectorCardinalityContext.class;
                }

                @Override
                public Edge<? extends View<?>, Node> getEdge() {
                    return edge;
                }

                @Override
                public Direction getDirection() {
                    return direction;
                }

                @Override
                public Graph<?, ? extends Node> getGraph() {
                    return graph;
                }

                @Override
                public Element<? extends View<?>> getCandidate() {
                    return candidate;
                }

                @Override
                public CardinalityContext.Operation getOperation() {
                    return operation;
                }
            };
        }
    }
}
