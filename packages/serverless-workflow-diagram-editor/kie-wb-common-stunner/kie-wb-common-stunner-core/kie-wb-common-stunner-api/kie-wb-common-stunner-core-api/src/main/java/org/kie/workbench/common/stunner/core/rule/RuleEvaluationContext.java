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
 * The rule evaluation context type provides the runtime
 * inputs that allow the evaluation of some features or behaviors
 * against some set of rules.
 * <p>
 * As Stunner's graph is a labeled property graph structure, each
 * domain is able to provide its own graph structure and semantics
 * by custom rule evaluation context types.
 * <p>
 * Stunner provides some built-in evaluation contexts that are
 * integrated into the default's stunner graph implementation
 * and commands, such as containment, docking, connection
 * or cardinality contexts.
 * @See {@link Rule}
 * @See {@link RuleEvaluationHandler}
 */
public interface RuleEvaluationContext {

    /**
     * A representative name for the evaluation's context.
     */
    String getName();

    /**
     * Returns the default policy to apply once
     * evaluating this context type and any of the
     * rules being evaluated accept the context inputs.
     */
    boolean isDefaultDeny();

    /**
     * Returns the type of rule evaluation context that
     * will be evaluated by one or more evaluation handlers and
     * used as well for context and dependency injection
     * purposes.
     */
    Class<? extends RuleEvaluationContext> getType();
}
