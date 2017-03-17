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

package org.kie.workbench.common.stunner.core.rule.handler.impl;

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.ContainmentContext;
import org.kie.workbench.common.stunner.core.rule.impl.CanContain;
import org.kie.workbench.common.stunner.core.rule.violations.ContainmentRuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

@ApplicationScoped
public class ContainmentEvaluationHandler implements RuleEvaluationHandler<CanContain, ContainmentContext> {

    @Override
    public Class<CanContain> getRuleType() {
        return CanContain.class;
    }

    @Override
    public Class<ContainmentContext> getContextType() {
        return ContainmentContext.class;
    }

    @Override
    public boolean accepts(final CanContain rule,
                           final ContainmentContext context) {
        return rule.getParentId().equals(context.getId());
    }

    @Override
    public RuleViolations evaluate(final CanContain rule,
                                   final ContainmentContext context) {
        final Set<String> allowedRoles = rule.getAllowedRoles();
        final String targetId = context.getId();
        final Set<String> candidateRoles = context.getCandidateRoles();
        final DefaultRuleViolations results = new DefaultRuleViolations();
        final Set<String> permittedStrings = new HashSet<String>(allowedRoles);
        permittedStrings.retainAll(candidateRoles);
        if (permittedStrings.isEmpty()) {
            results.addViolation(new ContainmentRuleViolation(targetId,
                                                              candidateRoles.toString()));
        }
        return results;
    }
}
