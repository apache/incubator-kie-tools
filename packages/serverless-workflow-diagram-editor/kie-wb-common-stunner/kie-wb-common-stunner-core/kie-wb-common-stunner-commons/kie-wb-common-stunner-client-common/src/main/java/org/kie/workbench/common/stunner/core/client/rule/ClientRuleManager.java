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


package org.kie.workbench.common.stunner.core.client.rule;

import io.crysknife.client.ManagedInstance;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.registry.rule.RuleHandlerRegistry;
import org.kie.workbench.common.stunner.core.rule.CachedRuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;

@ApplicationScoped
public class ClientRuleManager implements RuleManager {

    private final CachedRuleManager ruleManager;
    private final ManagedInstance<RuleEvaluationHandler> ruleEvaluationHandlerInstances;

    protected ClientRuleManager() {
        this(null,
             null);
    }

    @Inject
    public ClientRuleManager(final CachedRuleManager ruleManager,
                             final @Any ManagedInstance<RuleEvaluationHandler> ruleEvaluationHandlerInstances) {
        this.ruleManager = ruleManager;
        this.ruleEvaluationHandlerInstances = ruleEvaluationHandlerInstances;
    }

    @PostConstruct
    public void init() {
        ruleEvaluationHandlerInstances.forEach(registry()::register);
    }

    @Override
    public RuleHandlerRegistry registry() {
        return ruleManager.registry();
    }

    @Override
    public RuleViolations evaluate(final RuleSet ruleSet,
                                   final RuleEvaluationContext context) {
        return ruleManager.evaluate(ruleSet,
                                    context);
    }
}
