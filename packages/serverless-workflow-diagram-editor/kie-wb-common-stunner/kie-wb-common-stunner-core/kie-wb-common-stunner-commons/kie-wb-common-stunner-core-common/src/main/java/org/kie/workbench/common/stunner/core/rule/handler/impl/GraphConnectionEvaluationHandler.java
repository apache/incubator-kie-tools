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

import java.util.Optional;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphEvaluationState;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleEvaluationContextBuilder;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

@ApplicationScoped
public class GraphConnectionEvaluationHandler implements RuleEvaluationHandler<CanConnect, GraphConnectionContext> {

    private final ConnectionEvaluationHandler connectionEvaluationHandler;
    private final GraphEvaluationHandlerUtils evalUtils;

    protected GraphConnectionEvaluationHandler() {
        this(null,
             null);
    }

    @Inject
    public GraphConnectionEvaluationHandler(final DefinitionManager definitionManager,
                                            final ConnectionEvaluationHandler connectionEvaluationHandler) {
        this.connectionEvaluationHandler = connectionEvaluationHandler;
        this.evalUtils = new GraphEvaluationHandlerUtils(definitionManager);
    }

    @Override
    public Class<CanConnect> getRuleType() {
        return CanConnect.class;
    }

    @Override
    public Class<GraphConnectionContext> getContextType() {
        return GraphConnectionContext.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean accepts(final CanConnect rule,
                           final GraphConnectionContext context) {
        final Edge<? extends View<?>, ? extends Node> connector = context.getConnector();
        final Set<String> labels = evalUtils.getLabels(connector);
        return labels.stream()
                .filter(cr -> rule.getRole().equals(cr) &&
                        // As for acceptance the delegated handler only needs the connector id, no need
                        // to calculate roles for current source/target nodes.
                        connectionEvaluationHandler.accepts(rule,
                                                            RuleEvaluationContextBuilder.DomainContexts.connection(cr,
                                                                                                                   Optional.empty(),
                                                                                                                   Optional.empty())))
                .findAny()
                .isPresent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public RuleViolations evaluate(final CanConnect rule,
                                   final GraphConnectionContext context) {
        final GraphEvaluationState.ConnectionState connectionState = context.getState().getConnectionState();
        final Edge<? extends View<?>, ? extends Node> connector = context.getConnector();
        final Node<? extends View<?>, ? extends Edge> source =
                (Node<? extends View<?>, ? extends Edge>) context.getSource()
                        .orElse(connectionState.getSource(connector));
        final Node<? extends View<?>, ? extends Edge> target =
                (Node<? extends View<?>, ? extends Edge>) context.getTarget()
                        .orElse(connectionState.getTarget(connector));
        if (source == null || target == null) {
            return new DefaultRuleViolations();
        }
        final Set<String> edgeLabels = evalUtils.getLabels(connector);
        final Optional<Set<String>> sourceLabels = Optional.of(evalUtils.getLabels(source));
        final Optional<Set<String>> targetLabels = Optional.of(evalUtils.getLabels(target));
        final DefaultRuleViolations result = new DefaultRuleViolations();
        edgeLabels.stream()
                .filter(pr -> rule.getRole().equals(pr))
                .forEach(pr -> result.addViolations(connectionEvaluationHandler
                                                            .evaluate(rule,
                                                                      RuleEvaluationContextBuilder.DomainContexts.connection(pr,
                                                                                                                             sourceLabels,
                                                                                                                             targetLabels))));
        return GraphEvaluationHandlerUtils.addViolationsSourceUUID(connector.getUUID(),
                                                                   result);
    }
}
