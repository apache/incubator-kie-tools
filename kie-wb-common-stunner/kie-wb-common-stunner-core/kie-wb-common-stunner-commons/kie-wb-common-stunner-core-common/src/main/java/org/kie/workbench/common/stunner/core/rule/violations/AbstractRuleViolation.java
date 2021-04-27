/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.rule.violations;

import java.util.Optional;

public abstract class AbstractRuleViolation extends AbstractGraphViolation {

    private final Type type;

    private final String message;

    public AbstractRuleViolation() {
        this(Type.ERROR);
    }

    public AbstractRuleViolation(final Type type) {
        this(type, "");
    }

    protected AbstractRuleViolation(final Type type, final String message) {
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

    protected final Optional<Object[]> of(Object... param) {
        return Optional.ofNullable(param);
    }
}
