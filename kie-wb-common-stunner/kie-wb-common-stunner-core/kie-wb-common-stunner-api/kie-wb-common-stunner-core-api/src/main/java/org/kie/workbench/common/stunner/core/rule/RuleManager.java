/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.rule;

public interface RuleManager<R extends Rule> {

    /**
     * Common operation types.
     */
    enum Operation {
        NONE,
        ADD,
        DELETE
    }

    /**
     * The rule manager name.
     */
    String getName();

    boolean supports( final Rule rule );

    /**
     * Add a rule to the Rule Manager
     */
    RuleManager addRule( final R rule );

    /**
     * Clear all rules.
     */
    RuleManager clearRules();
}
