/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.rule.ext.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.AbstractTreeTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.util.FilteredParentsTypeMatcher;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.handler.impl.GraphEvaluationHandlerUtils;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

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

    private static Logger LOGGER = Logger.getLogger(ConnectorParentsMatchContainmentHandler.class.getName());

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
    public boolean accepts(final RuleExtension rule,
                           final NodeContainmentContext context) {
        if (!GraphUtils.hasConnections(context.getCandidate())) {
            //this is not necessary to check rules in case there is no connections
            return false;
        }
        return acceptsContainment(rule,
                                  context);
    }

    @Override
    public RuleViolations evaluate(final RuleExtension rule,
                                   final NodeContainmentContext context) {
        return evaluateContainment(rule,
                                   context);
    }

    private boolean acceptsContainment(final RuleExtension rule,
                                       final NodeContainmentContext context) {
        final Class<?> parentType = getParentType(rule, context.getParent());
        if (!hasParentType(rule) || Objects.isNull(parentType)) {
            return true;
        }

        final String expectedParentId = BindableAdapterUtils.getDefinitionId(parentType);
        final Element<? extends Definition<?>> parent = context.getParent();
        final Node<? extends Definition<?>, ? extends Edge> candidate = context.getCandidate();
        final String parentId = evalUtils.getElementDefinitionId(parent);
        return parentId.equals(expectedParentId) || hasOldParentType(candidate, expectedParentId);
    }

    @SuppressWarnings("unchecked")
    private RuleViolations evaluateContainment(final RuleExtension rule,
                                               final NodeContainmentContext context) {
        final String connectorId = rule.getId();
        final Graph<?, ? extends Node> graph = context.getGraph();
        final Element<? extends Definition<?>> parent = context.getParent();
        final Node<? extends Definition<?>, ? extends Edge> candidate = context.getCandidate();
        final Class<?> parentType = getParentType(rule, context.getParent());
        final DefaultRuleViolations result = new DefaultRuleViolations();

        // Walk throw the graph and evaluate connector source and target nodes parent match.
        treeWalkTraverseProcessor
                .traverse(graph,
                          candidate,
                          new AbstractTreeTraverseCallback<Graph, Node, Edge>() {

                              private final FilteredParentsTypeMatcher matcher =
                                      new FilteredParentsTypeMatcher(definitionManager,
                                                                     parent,
                                                                     candidate)
                                              .forParentType(parentType);

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
                                      final Node sourceNode = edge.getSourceNode();
                                      final Node targetNode = edge.getTargetNode();
                                      boolean valid = true;
                                      if (null != sourceNode && null != targetNode) {
                                          valid = matcher
                                                  .test(sourceNode,
                                                        targetNode);
                                      }
                                      if (!valid) {
                                          addViolation(edge.getUUID(),
                                                       rule,
                                                       result);
                                      }
                                  }
                                  return true;
                              }
                          });
        return result;
    }

    private boolean hasOldParentType(final Node<? extends Definition<?>, ? extends Edge> candidate,
                                     final String pId) {
        return GraphUtils.getParentByDefinitionId(definitionManager,
                                                  candidate,
                                                  pId).isPresent();
    }
}
