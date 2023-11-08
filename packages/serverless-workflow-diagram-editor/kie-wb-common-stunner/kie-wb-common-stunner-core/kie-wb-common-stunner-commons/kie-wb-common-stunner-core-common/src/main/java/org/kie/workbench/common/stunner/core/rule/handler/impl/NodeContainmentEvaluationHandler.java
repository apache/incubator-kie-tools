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
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleEvaluationContextBuilder;
import org.kie.workbench.common.stunner.core.rule.impl.CanContain;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

import static org.kie.workbench.common.stunner.core.rule.handler.impl.GraphEvaluationHandlerUtils.addViolationsSourceUUID;

@ApplicationScoped
public class NodeContainmentEvaluationHandler implements RuleEvaluationHandler<CanContain, NodeContainmentContext> {

    private final ContainmentEvaluationHandler containmentHandler;
    private final GraphEvaluationHandlerUtils evalUtils;

    protected NodeContainmentEvaluationHandler() {
        this(null,
             null);
    }

    @Inject
    public NodeContainmentEvaluationHandler(final DefinitionManager definitionManager,
                                            final ContainmentEvaluationHandler containmentHandler) {
        this.containmentHandler = containmentHandler;
        this.evalUtils = new GraphEvaluationHandlerUtils(definitionManager);
    }

    @Override
    public Class<CanContain> getRuleType() {
        return CanContain.class;
    }

    @Override
    public Class<NodeContainmentContext> getContextType() {
        return NodeContainmentContext.class;
    }

    @Override
    public boolean accepts(final CanContain rule,
                           final NodeContainmentContext context) {
        final Set<String> parenteLabels = evalUtils.getLabels(context.getParent());
        // As for acceptance checking, the delegated handler only needs the parent node id, no need
        // to calculate roles for the candidate node.
        return containmentHandler.accepts(rule,
                                          RuleEvaluationContextBuilder.DomainContexts.containment(parenteLabels,
                                                                                                  Collections.emptySet()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public RuleViolations evaluate(final CanContain rule,
                                   final NodeContainmentContext context) {
        final Element<? extends Definition<?>> parent = context.getParent();
        final Set<String> parentLabels = evalUtils.getLabels(parent);
        final DefaultRuleViolations result = new DefaultRuleViolations();
        final Collection<Node<? extends Definition<?>, ? extends Edge>> candidates = context.getCandidates();
        candidates.forEach(candidate -> result.addViolations(evaluate(rule,
                                                                      candidate.getUUID(),
                                                                      evalUtils.getLabels(candidate),
                                                                      parentLabels)));
        return result;
    }

    private RuleViolations evaluate(final CanContain rule,
                                    final String candidateUUID,
                                    final Set<String> candidateLabels,
                                    final Set<String> parentLabels) {
        RuleViolations result = containmentHandler
                .evaluate(rule,
                          RuleEvaluationContextBuilder.DomainContexts.containment(parentLabels,
                                                                                  candidateLabels));
        return addViolationsSourceUUID(candidateUUID,
                                       result);
    }
}
