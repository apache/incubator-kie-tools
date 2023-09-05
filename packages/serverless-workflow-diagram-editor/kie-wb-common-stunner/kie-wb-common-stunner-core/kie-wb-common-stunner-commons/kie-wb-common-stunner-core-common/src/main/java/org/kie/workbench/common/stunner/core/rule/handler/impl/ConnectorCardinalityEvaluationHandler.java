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


package org.kie.workbench.common.stunner.core.rule.handler.impl;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ConnectorCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphEvaluationState;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleEvaluationContextBuilder;
import org.kie.workbench.common.stunner.core.rule.impl.EdgeOccurrences;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

@ApplicationScoped
public class ConnectorCardinalityEvaluationHandler implements RuleEvaluationHandler<EdgeOccurrences, ConnectorCardinalityContext> {

    private final GraphEvaluationHandlerUtils evalUtils;
    private final EdgeCardinalityEvaluationHandler edgeCardinalityEvaluationHandler;

    /*protected ConnectorCardinalityEvaluationHandler() {
        this.evalUtils = null;
        this.edgeCardinalityEvaluationHandler = null;
    }*/

    @Inject
    public ConnectorCardinalityEvaluationHandler(final DefinitionManager definitionManager,
                                                 final EdgeCardinalityEvaluationHandler edgeCardinalityEvaluationHandler) {
        this.evalUtils = new GraphEvaluationHandlerUtils(definitionManager);
        this.edgeCardinalityEvaluationHandler = edgeCardinalityEvaluationHandler;
    }

    ConnectorCardinalityEvaluationHandler(final GraphEvaluationHandlerUtils evalUtils,
                                          final EdgeCardinalityEvaluationHandler edgeCardinalityEvaluationHandler) {
        this.evalUtils = evalUtils;
        this.edgeCardinalityEvaluationHandler = edgeCardinalityEvaluationHandler;
    }

    @Override
    public Class<EdgeOccurrences> getRuleType() {
        return EdgeOccurrences.class;
    }

    @Override
    public Class<ConnectorCardinalityContext> getContextType() {
        return ConnectorCardinalityContext.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean accepts(final EdgeOccurrences rule,
                           final ConnectorCardinalityContext context) {
        final Edge<? extends View<?>, Node> edge = context.getEdge();
        final Element<? extends View<?>> candidate = context.getCandidate();
        final String edgeId = evalUtils.getElementDefinitionId(edge);
        final Set<String> candidateRoles = evalUtils.getLabels(candidate);
        // Take into account that there is no need to provide the candidate count value, as not necessary
        // just to check if the handler accepts the runtime rule and candidates.
        return edgeCardinalityEvaluationHandler.accepts(rule,
                                                        RuleEvaluationContextBuilder.DomainContexts.edgeCardinality(candidateRoles,
                                                                                                                    edgeId,
                                                                                                                    -1,
                                                                                                                    context.getDirection(),
                                                                                                                    context.getOperation()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public RuleViolations evaluate(final EdgeOccurrences rule,
                                   final ConnectorCardinalityContext context) {
        final GraphEvaluationState.ConnectorCardinalityState cardinalityState =
                context.getState().getConnectorCardinalityState();
        final DefaultRuleViolations result = new DefaultRuleViolations();
        final Node<? extends View<?>, Edge> candidate =
                (Node<? extends View<?>, Edge>) context.getCandidate();
        final Edge<? extends View<?>, Node> edge = context.getEdge();
        final Optional<CardinalityContext.Operation> operation = context.getOperation();
        final EdgeCardinalityContext.Direction direction = context.getDirection();
        final Collection<? extends Edge> edges = isIncoming(direction) ?
                cardinalityState.getIncoming(candidate) :
                cardinalityState.getOutgoing(candidate);
        final String edgeId = evalUtils.getElementDefinitionId(edge);
        final int count = evalUtils.countEdges(edgeId,
                                               edges);
        // Delegate to the domain model cardinality rule manager.
        result.addViolations(
                edgeCardinalityEvaluationHandler
                        .evaluate(rule,
                                  RuleEvaluationContextBuilder.DomainContexts.edgeCardinality(candidate.getLabels(),
                                                                                              edgeId,
                                                                                              count,
                                                                                              rule.getDirection(),
                                                                                              operation))
        );

        return GraphEvaluationHandlerUtils.addViolationsSourceUUID(edge.getUUID(),
                                                                   result);
    }

    private boolean isIncoming(final EdgeCardinalityContext.Direction direction) {
        return EdgeCardinalityContext.Direction.INCOMING.equals(direction);
    }
}
