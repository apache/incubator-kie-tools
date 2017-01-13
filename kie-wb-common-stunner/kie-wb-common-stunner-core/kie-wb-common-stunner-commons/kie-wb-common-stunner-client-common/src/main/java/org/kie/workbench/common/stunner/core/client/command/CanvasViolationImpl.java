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

package org.kie.workbench.common.stunner.core.client.command;

import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public final class CanvasViolationImpl implements CanvasViolation {

    private final String message;
    private final Type type;

    CanvasViolationImpl(final String message,
                        final Type type) {
        this.type = type;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Type getViolationType() {
        return type;
    }

    public static final class CanvasViolationBuilder {

        private final RuleViolation ruleViolation;

        public CanvasViolationBuilder(final RuleViolation ruleViolation) {
            this.ruleViolation = ruleViolation;
        }

        public CanvasViolation build() {
            return new CanvasViolationImpl(ruleViolation.getMessage(),
                                           ruleViolation.getViolationType());
        }
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
