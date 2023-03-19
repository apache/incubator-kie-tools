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

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;

@Portable
public class DefaultRuleViolations implements RuleViolations {

    private final List<RuleViolation> violations = new LinkedList<RuleViolation>();

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

    public boolean isEmpty() {
        return violations.isEmpty();
    }

    @Override
    public Iterable<RuleViolation> violations() {
        return violations;
    }

    @Override
    public Iterable<RuleViolation> violations(final RuleViolation.Type violationType) {
        return violations.stream()
                .filter(v -> v.getViolationType().equals(violationType))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return violations.stream()
                .map(v -> "{'" + v.getViolationType() + "' " + v.toString() + "}")
                .reduce(String::concat)
                .orElse("{'No violations found' " + super.toString() + ")");
    }
}
