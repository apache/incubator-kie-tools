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


package org.kie.workbench.common.stunner.core.rule.context.impl;

import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;

class CardinalityContextImpl implements CardinalityContext {

    private final Set<String> roles;
    private final int currentCount;
    private final int candidateCount;
    private final Optional<Operation> operation;

    CardinalityContextImpl(final Set<String> roles,
                           final int count,
                           final Optional<Operation> operation) {
        this(roles, count, 1, operation);
    }

    CardinalityContextImpl(final Set<String> roles,
                           final int currentCount,
                           final int candidateCount,
                           final Optional<Operation> operation) {
        this.roles = roles;
        this.currentCount = currentCount;
        this.candidateCount = candidateCount;
        this.operation = operation;
    }

    @Override
    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public int getCurrentCount() {
        return currentCount;
    }

    @Override
    public int getCandidateCount() {
        return candidateCount;
    }

    @Override
    public Optional<Operation> getOperation() {
        return operation;
    }

    @Override
    public String getName() {
        return "Cardinality";
    }

    @Override
    public boolean isDefaultDeny() {
        return false;
    }

    @Override
    public Class<? extends RuleEvaluationContext> getType() {
        return CardinalityContext.class;
    }
}
