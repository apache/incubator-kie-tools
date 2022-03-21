/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.command;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public final class CanvasViolationImpl
        implements CanvasViolation {

    private transient final RuleViolation ruleViolation;

    CanvasViolationImpl(final RuleViolation ruleViolation) {
        this.ruleViolation = ruleViolation;
    }

    @Override
    public String getUUID() {
        return ruleViolation.getUUID();
    }

    @Override
    public Optional<Object[]> getArguments() {
        return ruleViolation.getArguments();
    }

    @Override
    public Type getViolationType() {
        return ruleViolation.getViolationType();
    }

    @Override
    public RuleViolation getRuleViolation() {
        return ruleViolation;
    }

    public static final class Builder {

        public static CanvasViolation build(final RuleViolation violation) {
            return new CanvasViolationImpl(violation);
        }
    }

    @Override
    public String getMessage() {
        return ruleViolation.getMessage();
    }

    @Override
    public String toString() {
        return "CanvasViolationImpl{" +
                "ruleViolation=" + ruleViolation +
                '}';
    }
}
