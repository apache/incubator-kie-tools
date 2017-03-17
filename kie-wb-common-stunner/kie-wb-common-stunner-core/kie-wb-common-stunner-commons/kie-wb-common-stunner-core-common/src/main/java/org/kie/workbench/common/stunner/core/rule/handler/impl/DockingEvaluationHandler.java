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
import org.kie.workbench.common.stunner.core.rule.context.DockingContext;
import org.kie.workbench.common.stunner.core.rule.impl.CanDock;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.violations.DockingRuleViolation;

@ApplicationScoped
public class DockingEvaluationHandler implements RuleEvaluationHandler<CanDock, DockingContext> {

    @Override
    public Class<CanDock> getRuleType() {
        return CanDock.class;
    }

    @Override
    public Class<DockingContext> getContextType() {
        return DockingContext.class;
    }

    @Override
    public boolean accepts(final CanDock rule,
                           final DockingContext context) {
        return rule.getParentId().equals(context.getId());
    }

    @Override
    public RuleViolations evaluate(final CanDock rule,
                                   final DockingContext context) {
        final Set<String> allowedRoles = rule.getAllowedRoles();
        final String targetId = context.getId();
        final Set<String> candidateRoles = context.getAllowedRoles();
        final DefaultRuleViolations results = new DefaultRuleViolations();
        final Set<String> permittedStrings = new HashSet<String>(allowedRoles);
        permittedStrings.retainAll(candidateRoles);
        if (permittedStrings.isEmpty()) {
            results.addViolation(new DockingRuleViolation(targetId,
                                                          candidateRoles.toString()));
        }
        return results;
    }
}
