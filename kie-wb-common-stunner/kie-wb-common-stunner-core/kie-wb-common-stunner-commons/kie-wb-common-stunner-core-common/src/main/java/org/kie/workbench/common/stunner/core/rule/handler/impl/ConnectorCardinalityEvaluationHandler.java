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

package org.kie.workbench.common.stunner.core.rule.handler.impl;

import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ConnectorCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.RuleContextBuilder;
import org.kie.workbench.common.stunner.core.rule.impl.EdgeOccurrences;

@ApplicationScoped
public class ConnectorCardinalityEvaluationHandler implements RuleEvaluationHandler<EdgeOccurrences, ConnectorCardinalityContext> {

    private final GraphEvaluationHandlerUtils evalUtils;
    private final GraphUtils graphUtils;
    private final EdgeCardinalityEvaluationHandler edgeCardinalityEvaluationHandler;

    protected ConnectorCardinalityEvaluationHandler() {
        this(null,
             null,
             null);
    }

    @Inject
    public ConnectorCardinalityEvaluationHandler(final DefinitionManager definitionManager,
                                                 final GraphUtils graphUtils,
                                                 final EdgeCardinalityEvaluationHandler edgeCardinalityEvaluationHandler) {
        this.graphUtils = graphUtils;
        this.evalUtils = new GraphEvaluationHandlerUtils(definitionManager);
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
    public boolean accepts(final EdgeOccurrences rule,
                           final ConnectorCardinalityContext context) {
        final Edge<? extends View<?>, Node> edge = context.getEdge();
        final Element<? extends View<?>> candidate = context.getCandidate();
        final Set<String> labels = evalUtils.getLabels(candidate);
        final String edgeId = evalUtils.getElementDefinitionId(edge);
        // Take into account that there is no need to provide the candidate count value, as not necessary
        // just to check if the handler accepts the runtime rule and candidates.
        return edgeCardinalityEvaluationHandler.accepts(rule,
                                                        RuleContextBuilder.DomainContexts.edgeCardinality(labels,
                                                                                                          edgeId,
                                                                                                          -1,
                                                                                                          context.getDirection(),
                                                                                                          context.getOperation()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public RuleViolations evaluate(final EdgeOccurrences rule,
                                   final ConnectorCardinalityContext context) {

        final Node<? extends View<?>, ? extends Edge> candidate =
                (Node<? extends View<?>, ? extends Edge>) context.getCandidate();
        final Edge<? extends View<?>, Node> edge = context.getEdge();
        final CardinalityContext.Operation operation = context.getOperation();
        final ConnectorCardinalityContext.Direction direction = context.getDirection();
        final List<? extends Edge> edges = isIncoming(direction) ?
                candidate.getInEdges() : candidate.getOutEdges();
        // The edge defintiion's identifier.
        final String edgeId = evalUtils.getElementDefinitionId(edge);
        // Edge count.
        final int count = graphUtils.countEdges(edgeId,
                                                edges);

        // Delegate to the domain model cardinality rule manager.
        final RuleViolations result = edgeCardinalityEvaluationHandler
                .evaluate(rule,
                          RuleContextBuilder.DomainContexts.edgeCardinality(evalUtils.getLabels(candidate),
                                                                            edgeId,
                                                                            count,
                                                                            rule.getDirection(),
                                                                            operation));
        return GraphEvaluationHandlerUtils.addViolationsSourceUUID(edge.getUUID(),
                                                                   result);
    }

    private boolean isIncoming(final ConnectorCardinalityContext.Direction direction) {
        return ConnectorCardinalityContext.Direction.INCOMING.equals(direction);
    }
}
