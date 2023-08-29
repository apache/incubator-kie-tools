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


package org.kie.workbench.common.stunner.core.rule;

import org.kie.workbench.common.stunner.core.registry.rule.RuleHandlerRegistry;

/**
 * Main entry point for evaluating a set of rules in a
 * certain context.
 * <p>
 * It does:
 * - It provides a rule handler registry instance that
 * is populated with all available evaluation handlers.
 * - Evaluates each of the rules in the rule set with each of the
 * registered evaluation handlers for the given context type.
 * - If no rules are present in the rule set (for any context - an empty rule set),
 * it does not produce any rule violations.
 * - If no rules are present in the rule set for the given context, it depends on the context
 * type if produce any rule violations.
 * @See {@link Rule}
 * @See {@link RuleEvaluationContext}
 * @See {@link RuleEvaluationHandler}
 */
public interface RuleManager {

    /**
     * Provides a rule handler registry that contains
     * all available rule & extension handlers present.
     */
    RuleHandlerRegistry registry();

    /**
     * Evaluates if a given set of rules are allowed
     * for the given context.
     * @param ruleSet The set of rules to evaluate for the context.
     * @param context The rule evaluation context.
     * @return - If no rules are present in the rule set (for any context - an empty rule set),
     * it does not produce any rule violations.
     * - If no rules are present in the rule set for the given context, it depends on the context
     * type if produce any rule violations.
     */
    RuleViolations evaluate(RuleSet ruleSet,
                            RuleEvaluationContext context);
}
