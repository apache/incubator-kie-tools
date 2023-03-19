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

import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public abstract class AbstractGraphViolation implements RuleViolation {

    private String uuid;

    @Override
    public String getUUID() {
        return uuid;
    }

    public AbstractGraphViolation setUUID(final String sourceUUID) {
        this.uuid = sourceUUID;
        return this;
    }

    @Override
    public String toString() {
        return "[{type=" + this.getClass().getSimpleName() + "]"
                + ",{uuid=" + uuid + "]"
                + ",{args=" + getArguments() + "}"
                + ",{message=" + getMessage() + "}"
                + "]";
    }
}
