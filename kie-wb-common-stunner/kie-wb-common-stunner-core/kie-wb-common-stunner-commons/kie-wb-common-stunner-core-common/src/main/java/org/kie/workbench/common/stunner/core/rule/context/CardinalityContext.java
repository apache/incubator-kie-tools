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

package org.kie.workbench.common.stunner.core.rule.context;

import java.util.Set;

import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.handler.impl.CardinalityEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.impl.Occurrences;

/**
 * This rule evaluation context provides the runtime information
 * that allows the evaluation for cardinality operations.
 * @See {@link Occurrences}
 * @See {@link CardinalityEvaluationHandler}
 */
public interface CardinalityContext extends RuleEvaluationContext {

    /**
     * Common cardinality operation types.
     */
    enum Operation {
        NONE,
        ADD,
        DELETE
    }

    /**
     * The roles that the candidate must satisfy
     * for this rule.
     */
    Set<String> getRoles();

    /**
     * The current number of candidates for the
     * roles which this rules applies for.
     */
    int getCandidateCount();

    /**
     * The cardinality operation.
     */
    Operation getOperation();
}
