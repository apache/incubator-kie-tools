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

import java.util.LinkedList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DefaultRuleViolations implements RuleViolations {

    private List<RuleViolation> violations = new LinkedList<RuleViolation>();

    public DefaultRuleViolations addViolations(final RuleViolations violations) {
        if (null != violations) {
            violations.violations().forEach(this::addViolation);
        }
        return this;
    }

    public DefaultRuleViolations addViolation(final RuleViolation violation) {
        violations.add(violation);
        return this;
    }

    public DefaultRuleViolations clear() {
        violations.clear();
        return this;
    }

    @Override
    public Iterable<RuleViolation> violations() {
        return violations;
    }

    @Override
    public Iterable<RuleViolation> violations(final RuleViolation.Type violationType) {
        return violations;
    }
}
