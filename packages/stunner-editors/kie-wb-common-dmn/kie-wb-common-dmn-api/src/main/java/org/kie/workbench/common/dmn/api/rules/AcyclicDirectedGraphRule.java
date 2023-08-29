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
package org.kie.workbench.common.dmn.api.rules;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphEvaluationState;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.violations.RuleViolationImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@ApplicationScoped
public class AcyclicDirectedGraphRule extends RuleExtensionHandler<AcyclicDirectedGraphRule, GraphConnectionContext> {

    static final String ERROR_MESSAGE = "Connection would violate Directed Acrylic Graph consistency.";

    @Override
    public Class<AcyclicDirectedGraphRule> getExtensionType() {
        return AcyclicDirectedGraphRule.class;
    }

    @Override
    public Class<GraphConnectionContext> getContextType() {
        return GraphConnectionContext.class;
    }

    @Override
    public boolean accepts(final RuleExtension rule,
                           final GraphConnectionContext context) {
        final Object o = DefinitionUtils.getElementDefinition(context.getConnector());
        final Class<?> type = rule.getTypeArguments()[0];
        return o.getClass().equals(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RuleViolations evaluate(final RuleExtension rule,
                                   final GraphConnectionContext context) {
        final GraphEvaluationState state = context.getState();
        final Graph<?, Node> graph = (Graph<?, Node>) state.getGraph();

        final Optional<Node<? extends View<?>, ? extends Edge>> oSource = context.getSource();
        final Optional<Node<? extends View<?>, ? extends Edge>> oTarget = context.getTarget();
        final Edge<? extends View<?>, ? extends Node> oConnector = context.getConnector();
        final DefaultRuleViolations result = new DefaultRuleViolations();

        //Only validate DAG when source and target nodes are set
        if (!(oSource.isPresent() && oTarget.isPresent())) {
            return result;
        }

        final Node<?, Edge> source = (Node<?, Edge>) oSource.get();
        final Node<?, Edge> target = (Node<?, Edge>) oTarget.get();
        final Edge<?, Node> connector = (Edge<?, Node>) oConnector;

        try {
            final TreeWalkTraverseProcessor walker = getTreeWalker(source,
                                                                   target,
                                                                   connector);
            walker.traverse(graph,
                            new TreeTraverseCallback<Graph, Node, Edge>() {

                                final Set<Node> inProgress = new HashSet<>();

                                @Override
                                public void startGraphTraversal(final Graph graph) {
                                }

                                @Override
                                public boolean startNodeTraversal(final Node node) {
                                    if (inProgress.contains(node)) {
                                        throw new DirectedAcrylicGraphViolationException();
                                    }
                                    inProgress.add(node);
                                    return true;
                                }

                                @Override
                                public boolean startEdgeTraversal(final Edge edge) {
                                    return true;
                                }

                                @Override
                                public void endNodeTraversal(final Node node) {
                                    inProgress.remove(node);
                                }

                                @Override
                                public void endEdgeTraversal(final Edge edge) {

                                }

                                @Override
                                public void endGraphTraversal() {
                                }
                            });
        } catch (DirectedAcrylicGraphViolationException e) {
            result.addViolation(new RuleViolationImpl(ERROR_MESSAGE));
        }

        return result;
    }

    protected TreeWalkTraverseProcessor getTreeWalker(final Node<?, Edge> source,
                                                      final Node<?, Edge> target,
                                                      final Edge<?, Node> connector) {
        return new AcyclicDirectedGraphWalker(source,
                                              target,
                                              connector);
    }

    private static class DirectedAcrylicGraphViolationException extends RuntimeException {

    }
}
