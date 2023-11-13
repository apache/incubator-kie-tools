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

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.rule.EmptyRuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleEvaluationContextBuilder;
import org.kie.workbench.common.stunner.core.rule.violations.EmptyRuleViolations;

/**
 * This graph execution context type does not provide rule context evaluation,
 * so commands will be directly executed.
 */
@NonPortable
public class DirectGraphCommandExecutionContext extends AbstractGraphCommandExecutionContext {

    private final transient RuleSet ruleSet;
    private final RuleEvaluationContextBuilder.StatelessGraphContextBuilder contextBuilder;

    public DirectGraphCommandExecutionContext(final DefinitionManager definitionManager,
                                              final FactoryManager factoryManager,
                                              final Index<?, ?> graphIndex) {
        this(definitionManager,
             factoryManager,
             new RuleEvaluationContextBuilder.StatelessGraphContextBuilder(graphIndex.getGraph()),
             graphIndex);
    }

    DirectGraphCommandExecutionContext(final DefinitionManager definitionManager,
                                       final FactoryManager factoryManager,
                                       final RuleEvaluationContextBuilder.StatelessGraphContextBuilder contextBuilder,
                                       final Index<?, ?> graphIndex) {
        super(definitionManager,
              factoryManager,
              graphIndex);
        this.ruleSet = new EmptyRuleSet();
        this.contextBuilder = contextBuilder;
    }

    @Override
    protected RuleEvaluationContextBuilder.GraphContextBuilder getContextBuilder() {
        return contextBuilder;
    }

    @Override
    public RuleViolations evaluate(final GraphEvaluationContext context) {
        return EmptyRuleViolations.INSTANCE;
    }

    @Override
    public RuleSet getRuleSet() {
        return ruleSet;
    }
}
