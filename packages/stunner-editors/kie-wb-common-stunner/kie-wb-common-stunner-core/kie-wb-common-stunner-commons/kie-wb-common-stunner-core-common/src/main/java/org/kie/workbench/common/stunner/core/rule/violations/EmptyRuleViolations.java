/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;

import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;

public class EmptyRuleViolations implements RuleViolations {

    public static final RuleViolations INSTANCE = new EmptyRuleViolations();

    @Override
    public Iterable<RuleViolation> violations() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<RuleViolation> violations(RuleViolation.Type violationType) {
        return Collections.emptyList();
    }
}
