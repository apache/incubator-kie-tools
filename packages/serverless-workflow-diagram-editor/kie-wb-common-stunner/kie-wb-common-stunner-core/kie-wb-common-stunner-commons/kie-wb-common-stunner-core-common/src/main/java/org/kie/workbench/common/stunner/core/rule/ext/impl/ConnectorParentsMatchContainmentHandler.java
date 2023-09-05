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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.AbstractTreeTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.graph.util.ParentTypesMatcher;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphEvaluationState;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.handler.impl.GraphEvaluationHandlerUtils;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

import static org.kie.workbench.common.stunner.core.rule.context.impl.StatefulGraphEvaluationState.StatefulContainmentState.getParent;

/**
 * A rule handler that checks if both source and target nodes for a given connector
 * have the same parent instance for a given type.
 * <p>
 * This handler applies for graph containment contexts.
 * <p>
 * The RuleExtension instance used by this handler needs the following arguments:
 * - RuleExtension#getTypeArguments()[0] - The parent type
 * - RuleExtension#getArguments()[0] - The rule violation's message
 * <p>
 * Example:
 * <code>
 * @RuleExtension( handler = ConnectorParentsMatchConnectionHandler.class,
 * typeArguments = {TheParentType.class}
 * arguments = {"My violation's message"}
 * )
 * public class MyConnectorBean {
 * }
 * </code>
 */
@ApplicationScoped
public class ConnectorParentsMatchContainmentHandler
        extends AbstractParentsMatchHandler<ConnectorParentsMatchContainmentHandler, NodeContainmentContext> {

    private final DefinitionManager definitionManager;
    private final TreeWalkTraverseProcessor treeWalkTraverseProcessor;
    private final GraphEvaluationHandlerUtils evalUtils;

    protected ConnectorParentsMatchContainmentHandler() {
        this(null,
             null);
    }

    @Inject
    public ConnectorParentsMatchContainmentHandler(final DefinitionManager definitionManager,
                                                   final TreeWalkTraverseProcessor treeWalkTraverseProcessor) {
        this.definitionManager = definitionManager;
        this.treeWalkTraverseProcessor = treeWalkTraverseProcessor;
        this.evalUtils = new GraphEvaluationHandlerUtils(definitionManager);
    }

    @Override
    public Class<ConnectorParentsMatchContainmentHandler> getExtensionType() {
        return ConnectorParentsMatchContainmentHandler.class;
    }

    @Override
    public Class<NodeContainmentContext> getContextType() {
        return NodeContainmentContext.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean accepts(final RuleExtension rule,
                           final NodeContainmentContext context) {
        final Collection<Node<? extends Definition<?>, ? extends Edge>> candidates = context.getCandidates();
        return candidates.stream()
                .anyMatch(node -> hasAnyEdgeOfInterest(node, rule.getId()));
    }

    @SuppressWarnings("unchecked")
    private boolean hasAnyEdgeOfInterest(final Node candidate,
                                         final String edgeDefId) {
        final Node<? extends Definition<?>, ? extends Edge> node = candidate;
        return Stream.concat(node.getInEdges().stream(),
                             node.getOutEdges().stream())
                .filter(e -> GraphUtils.isContentSomeDefinition().test(e))
                .anyMatch(edge -> evalUtils.getElementDefinitionId(edge).equals(edgeDefId));
    }

    @Override
    public RuleViolations evaluate(final RuleExtension rule,
                                   final NodeContainmentContext context) {
        return evaluateContainment(rule,
                                   context);
    }

    @SuppressWarnings("unchecked")
    private RuleViolations evaluateContainment(final RuleExtension rule,
                                               final NodeContainmentContext context) {
        final DefaultRuleViolations result = new DefaultRuleViolations();
        final Collection<Node<? extends Definition<?>, ? extends Edge>> candidates = context.getCandidates();
        candidates.forEach(candidate -> evaluateSingleContainment(result,
                                                                  rule,
                                                                  context,
                                                                  candidate));
        return result;
    }

    @SuppressWarnings("unchecked")
    private void evaluateSingleContainment(final DefaultRuleViolations result,
                                           final RuleExtension rule,
                                           final NodeContainmentContext context,
                                           final Node<? extends Definition<?>, ? extends Edge> candidate) {
        final GraphEvaluationState state = context.getState();
        final Graph<?, ? extends Node> graph = context.getState().getGraph();
        final String connectorId = rule.getId();

        // Walk throw the graph and evaluate connector source and target nodes parent match.
        treeWalkTraverseProcessor
                .traverse(graph,
                          candidate,
                          new AbstractTreeTraverseCallback<Graph, Node, Edge>() {

                              @Override
                              public boolean startNodeTraversal(final Node node) {
                                  // Process incoming edges into the node as well.
                                  final List<? extends Edge> inEdges = node.getInEdges();
                                  if (null != inEdges) {
                                      inEdges.stream().forEach(this::process);
                                  }
                                  return true;
                              }

                              @Override
                              public boolean startEdgeTraversal(final Edge edge) {
                                  return process(edge);
                              }

                              private boolean process(final Edge edge) {
                                  final Optional<String> eId = getId(definitionManager,
                                                                     edge);
                                  if (eId.isPresent() && connectorId.equals(eId.get())) {
                                      final Node sourceNode = state.getConnectionState().getSource(edge);
                                      final Node targetNode = state.getConnectionState().getTarget(edge);
                                      final boolean valid = new ParentTypesMatcher(() -> definitionManager,
                                                                                   e -> getParent(context, e),
                                                                                   rule.getTypeArguments())
                                              .matcher()
                                              .test(sourceNode, targetNode);
                                      if (!valid) {
                                          addViolation(edge.getUUID(),
                                                       rule,
                                                       result);
                                      }
                                  }
                                  return true;
                              }
                          });
    }
}
