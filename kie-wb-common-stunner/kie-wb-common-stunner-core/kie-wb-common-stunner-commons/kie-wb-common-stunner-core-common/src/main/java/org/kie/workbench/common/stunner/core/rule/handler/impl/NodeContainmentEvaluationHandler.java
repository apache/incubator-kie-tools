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

import java.util.Collections;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.context.RuleContextBuilder;
import org.kie.workbench.common.stunner.core.rule.impl.CanContain;

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
        final String parentId = evalUtils.getElementDefinitionId(getParent(context));
        // As for acceptance checking, the delegated handler only needs the parent node id, no need
        // to calculate roles for the candidate node.
        return containmentHandler.accepts(rule,
                                          RuleContextBuilder.DomainContexts.containment(parentId,
                                                                                        Collections.emptySet()));
    }

    @Override
    public RuleViolations evaluate(final CanContain rule,
                                   final NodeContainmentContext context) {
        final Element<? extends Definition<?>> source = getParent(context);
        final Node<? extends Definition<?>, ? extends Edge> target = context.getCandidate();
        final String sourceId = evalUtils.getElementDefinitionId(source);
        final Set<String> candidateLabels = evalUtils.getLabels(target);
        final RuleViolations result = containmentHandler
                .evaluate(rule,
                          RuleContextBuilder.DomainContexts.containment(sourceId,
                                                                        candidateLabels));
        return GraphEvaluationHandlerUtils.addViolationsSourceUUID(target.getUUID(),
                                                                   result);
    }

    @SuppressWarnings("unchecked")
    private Element<? extends Definition<?>> getParent(final NodeContainmentContext context) {
        return context.getParent().orElse((Element<? extends Definition<?>>) context.getGraph());
    }
}
