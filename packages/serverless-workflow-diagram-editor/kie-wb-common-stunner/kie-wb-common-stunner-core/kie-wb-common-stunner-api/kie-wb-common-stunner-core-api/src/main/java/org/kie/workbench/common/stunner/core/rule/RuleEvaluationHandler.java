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

/**
 * A rule evaluation handler is able to evaluate a certain rule type in a
 * given context type.
 * <p>
 * Rule evaluation handlers are application scoped beans that
 * allow or deny some operation in a certain context against
 * onr or more rule instances Rules provide the runtime inputs and
 * rule contexts provide the evaluation scenario.
 * @param <R> The rule type.
 * @param <C> The evaluation context type.
 * @See {@link Rule}
 * @See {@link RuleEvaluationContext}
 * <p>
 * Consider the following example, which is provided as an stunner
 * built-in feature - Consider an entity-relation graph structure
 * that allows parent-child relationships. In that structure a node can
 * be set as a child for other node but only in a certain condition, in this
 * case a condition based on the roles for the candidate node (the node to
 * be set as child for the parent one). This way:
 * - Exists an evaluation context type for containment goals, which provides
 * the necessary information to evaluate if the operation must be allowed or denied.
 * - Exist one or more rules that provide the concrete candidate's roles allowed for
 * the containment relationship.
 * - Finally given one or more rules and the containment context inputs, the rule evaluation
 * handler is able to perform the evaluation/s at runtime.
 */
public interface RuleEvaluationHandler<R extends Rule, C extends RuleEvaluationContext> {

    /**
     * Returns the type of rule that this handler
     * is able to evaluate.
     */
    Class<R> getRuleType();

    /**
     * Returns the type of context that this handler requires
     * for the evaluation of the rule..
     */
    Class<C> getContextType();

    /**
     * If this handler support the rule type and context
     * type given by <code>getRuleType()</code> and
     * <code>getContextType()</code>, this method
     * allows to check if the concrete rule applies for
     * the context information at runtime.
     * As an example consider a rule that is being
     * used on different beans for the same
     * context.
     */
    boolean accepts(final R rule,
                    final C context);

    /**
     * Once this handler accepts the runtime rule and
     * context instances for evaluation, it performs the
     * necessary operations and can potentially result in
     * rule violations if the operation breaks the domain
     * semantics.
     */
    RuleViolations evaluate(final R rule,
                            final C context);
}
