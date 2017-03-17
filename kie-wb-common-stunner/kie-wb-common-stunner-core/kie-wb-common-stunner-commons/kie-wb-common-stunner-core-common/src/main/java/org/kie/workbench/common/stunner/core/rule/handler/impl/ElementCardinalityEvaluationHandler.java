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

import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ElementCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.RuleContextBuilder;
import org.kie.workbench.common.stunner.core.rule.impl.Occurrences;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

@ApplicationScoped
public class ElementCardinalityEvaluationHandler implements RuleEvaluationHandler<Occurrences, ElementCardinalityContext> {

    private final CardinalityEvaluationHandler cardinalityEvaluationHandler;
    private final GraphEvaluationHandlerUtils evalUtils;

    protected ElementCardinalityEvaluationHandler() {
        this(null,
             null);
    }

    @Inject
    public ElementCardinalityEvaluationHandler(final DefinitionManager definitionManager,
                                               final CardinalityEvaluationHandler cardinalityEvaluationHandler) {
        this.cardinalityEvaluationHandler = cardinalityEvaluationHandler;
        this.evalUtils = new GraphEvaluationHandlerUtils(definitionManager);
    }

    @Override
    public Class<Occurrences> getRuleType() {
        return Occurrences.class;
    }

    @Override
    public Class<ElementCardinalityContext> getContextType() {
        return ElementCardinalityContext.class;
    }

    @Override
    public boolean accepts(final Occurrences rule,
                           final ElementCardinalityContext context) {
        final Set<String> labels = context.getCandidate().getLabels();
        // Take into account that there is no need to provide the candidate count value, as not necessary
        // just to check if the handler accepts the runtime rule and candidates.
        return cardinalityEvaluationHandler.accepts(rule,
                                                    RuleContextBuilder.DomainContexts.cardinality(labels,
                                                                                                  -1,
                                                                                                  context.getOperation()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public RuleViolations evaluate(final Occurrences rule,
                                   final ElementCardinalityContext context) {

        final Element<? extends View<?>> candidate = context.getCandidate();
        final Set<String> labels = candidate.getLabels();
        final Map<String, Integer> graphLabelCount = countLabels(context.getGraph(),
                                                                 labels);
        final CardinalityContext.Operation operation = context.getOperation();
        final DefaultRuleViolations results = new DefaultRuleViolations();
        labels.stream().forEach(role -> {
            final Integer i = graphLabelCount.get(role);
            final RuleViolations violations =
                    cardinalityEvaluationHandler
                            .evaluate(rule,
                                      RuleContextBuilder.DomainContexts.cardinality(role,
                                                                                    null != i ? i : 0,
                                                                                    operation));
            results.addViolations(violations);
        });
        return GraphEvaluationHandlerUtils.addViolationsSourceUUID(candidate.getUUID(),
                                                                   results);
    }

    Map<String, Integer> countLabels(final Graph<?, ? extends Node> target,
                                     final Set<String> filter) {
        return GraphUtils.getLabelsCount(target,
                                         filter);
    }
}
