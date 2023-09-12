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


package org.kie.workbench.common.stunner.core.rule.context;

import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;

/**
 * This rule evaluation context provides the runtime information
 * that allows the evaluation for cardinality operations.
 */
public interface CardinalityContext extends RuleEvaluationContext {

    /**
     * Common cardinality operation types.
     */
    enum Operation {
        ADD,
        DELETE
    }

    /**
     * The candidate roles to evaluate against rules.
     */
    Set<String> getRoles();

    /**
     * The current number of candidates for the
     * roles which this rules applies for.
     */
    int getCurrentCount();

    /**
     * The number of candidates being added/deleted.
     */
    int getCandidateCount();

    /**
     * The cardinality operation.
     * If just validating current context operation can be empty.
     */
    Optional<Operation> getOperation();
}
