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


package org.kie.workbench.common.stunner.core.graph.command;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.AbstractGraphEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleEvaluationContextBuilder;
import org.kie.workbench.common.stunner.core.rule.context.impl.StatefulGraphEvaluationContexts;
import org.kie.workbench.common.stunner.core.rule.context.impl.StatefulGraphEvaluationState;

/**
 * This graph execution context type provides composite rule context evaluations.
 * Each evaluations accumulates the state in the actual context, so composite operations
 * can share evaluation states.
 */
public class ContextualGraphCommandExecutionContext extends AbstractGraphCommandExecutionContext {

    private final transient RuleManager ruleManager;
    private final transient RuleSet ruleSet;
    private final transient RuleEvaluationContextBuilder.StatefulGraphContextBuilder contextBuilder;

    public ContextualGraphCommandExecutionContext(final DefinitionManager definitionManager,
                                                  final FactoryManager factoryManager,
                                                  final RuleManager ruleManager,
                                                  final Index<?, ?> graphIndex,
                                                  final RuleSet ruleSet) {
        this(definitionManager,
             factoryManager,
             ruleManager,
             new RuleEvaluationContextBuilder.StatefulGraphContextBuilder(graphIndex.getGraph()),
             graphIndex,
             ruleSet);
    }

    ContextualGraphCommandExecutionContext(final DefinitionManager definitionManager,
                                           final FactoryManager factoryManager,
                                           final RuleManager ruleManager,
                                           final RuleEvaluationContextBuilder.StatefulGraphContextBuilder contextBuilder,
                                           final Index<?, ?> graphIndex,
                                           final RuleSet ruleSet) {
        super(definitionManager,
              factoryManager,
              graphIndex);
        this.contextBuilder = contextBuilder;
        this.ruleManager = ruleManager;
        this.ruleSet = ruleSet;
    }

    public void clear() {
        getState().clear();
    }

    @Override
    public RuleViolations evaluate(final GraphEvaluationContext context) {
        ((AbstractGraphEvaluationContext) context).setState(this::getState);
        return StatefulGraphEvaluationContexts.evaluate(context,
                                                        c -> ruleManager.evaluate(ruleSet, c));
    }

    @Override
    public RuleSet getRuleSet() {
        return ruleSet;
    }

    private StatefulGraphEvaluationState getState() {
        return contextBuilder.getState();
    }

    @Override
    protected RuleEvaluationContextBuilder.GraphContextBuilder getContextBuilder() {
        return contextBuilder;
    }
}
