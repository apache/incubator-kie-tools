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

import java.util.Collections;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.NodeDockingContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleEvaluationContextBuilder;
import org.kie.workbench.common.stunner.core.rule.impl.CanDock;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

@ApplicationScoped
public class NodeDockingEvaluationHandler implements RuleEvaluationHandler<CanDock, NodeDockingContext> {

    private final DockingEvaluationHandler dockingHandler;
    private final GraphEvaluationHandlerUtils evalUtils;

    protected NodeDockingEvaluationHandler() {
        this(null,
             null);
    }

    @Inject
    public NodeDockingEvaluationHandler(final DefinitionManager definitionManager,
                                        final DockingEvaluationHandler dockingHandler) {
        this.dockingHandler = dockingHandler;
        this.evalUtils = new GraphEvaluationHandlerUtils(definitionManager);
    }

    @Override
    public Class<CanDock> getRuleType() {
        return CanDock.class;
    }

    @Override
    public Class<NodeDockingContext> getContextType() {
        return NodeDockingContext.class;
    }

    @Override
    public boolean accepts(final CanDock rule,
                           final NodeDockingContext context) {
        final Set<String> parentLabels = evalUtils.getLabels(context.getParent());
        // As for acceptance checking, the delegated handler only needs the parent node id, no need
        // to calculate roles for the candidate node, so using an empty set.
        return dockingHandler.accepts(rule,
                                      RuleEvaluationContextBuilder.DomainContexts.docking(parentLabels,
                                                                                          Collections.emptySet()));
    }

    @Override
    public RuleViolations evaluate(final CanDock rule,
                                   final NodeDockingContext context) {
        final Node<? extends Definition<?>, ? extends Edge> target = context.getCandidate();
        final Set<String> parentLabels = evalUtils.getLabels(context.getParent());
        final Set<String> candidateLabels = evalUtils.getLabels(target);
        final DefaultRuleViolations result = new DefaultRuleViolations();
        result.addViolations(
                dockingHandler
                        .evaluate(rule,
                                  RuleEvaluationContextBuilder.DomainContexts.docking(parentLabels,
                                                                                      candidateLabels))
        );
        return GraphEvaluationHandlerUtils.addViolationsSourceUUID(target.getUUID(),
                                                                   result);
    }
}
