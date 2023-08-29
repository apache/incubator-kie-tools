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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ElementCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphEvaluationState;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleEvaluationContextBuilder;
import org.kie.workbench.common.stunner.core.rule.impl.Occurrences;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

import static org.kie.workbench.common.stunner.core.rule.handler.impl.GraphEvaluationHandlerUtils.addViolationsSourceUUID;

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
    @SuppressWarnings("unchecked")
    public boolean accepts(final Occurrences rule,
                           final ElementCardinalityContext context) {
        final Collection<Element<? extends View<?>>> candidates = context.getCandidates();
        return candidates.isEmpty() || candidates.stream().anyMatch(candidate -> accepts(rule, context, candidate));
    }

    @SuppressWarnings("unchecked")
    private boolean accepts(final Occurrences rule,
                            final ElementCardinalityContext context,
                            final Element<? extends View<?>> candidate) {
        final Set<String> candidateLabels = evalUtils.getLabels(candidate);
        return cardinalityEvaluationHandler
                .accepts(rule,
                         RuleEvaluationContextBuilder.DomainContexts.cardinality(
                                 candidateLabels,
                                 -1,
                                 context.getOperation()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public RuleViolations evaluate(final Occurrences rule,
                                   final ElementCardinalityContext context) {
        final GraphEvaluationState state = context.getState();
        final String role = rule.getRole();
        final Set<String> roles = Collections.singleton(role);
        final Map<String, Integer> graphLabelCount = countLabels(state, roles);
        final Collection<Element<? extends View<?>>> candidates = context.getCandidates();
        final Collection<Element<? extends View<?>>> filteredCandidates = candidates.stream()
                .filter(candidate -> accepts(rule, context, candidate))
                .collect(Collectors.toSet());
        final int size = filteredCandidates.size();
        final int count = graphLabelCount.isEmpty() ? 0 : graphLabelCount.get(role);
        final Optional<CardinalityContext.Operation> operation = context.getOperation();
        final DefaultRuleViolations results = new DefaultRuleViolations();
        // Ensure processing the role even if not used along the graph, so
        // cardinality min rules can be evaluated.
        final Function<String, RuleViolations> evaluator =
                uuid -> evaluate(rule,
                                 uuid,
                                 roles,
                                 count,
                                 size,
                                 operation);
        if (size == 0) {
            results.addViolations(evaluator.apply(null));
        } else {
            filteredCandidates.forEach(candidate -> results.addViolations(evaluator.apply(candidate.getUUID())));
        }
        return results;
    }

    private RuleViolations evaluate(final Occurrences rule,
                                    final String candidateUUID,
                                    final Set<String> roles,
                                    final int currentCount,
                                    final int candidateCount,
                                    final Optional<CardinalityContext.Operation> operation) {
        final RuleViolations violations = cardinalityEvaluationHandler
                .evaluate(rule,
                          RuleEvaluationContextBuilder.DomainContexts.cardinality(roles,
                                                                                  currentCount,
                                                                                  candidateCount,
                                                                                  operation));
        return null != candidateUUID ?
                addViolationsSourceUUID(candidateUUID, violations) :
                violations;
    }

    Map<String, Integer> countLabels(final GraphEvaluationState state,
                                     final Set<String> roleFilter) {
        final GraphEvaluationState.CardinalityState cardinalityState = state.getCardinalityState();

        final Iterable<Node> nodes = cardinalityState.nodes();
        final Map<String, Integer> labelsCount = new HashMap<>();
        StreamSupport.stream(nodes.spliterator(), false)
                .forEach(node -> GraphUtils.computeLabelsCount(node, labelsCount, roleFilter));
        return labelsCount;
    }
}
