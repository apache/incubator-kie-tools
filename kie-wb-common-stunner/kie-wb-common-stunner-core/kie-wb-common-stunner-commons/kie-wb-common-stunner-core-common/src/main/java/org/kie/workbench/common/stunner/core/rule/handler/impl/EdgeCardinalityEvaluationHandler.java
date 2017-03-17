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

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ConnectorCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.impl.EdgeOccurrences;
import org.kie.workbench.common.stunner.core.rule.violations.CardinalityMaxRuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.CardinalityMinRuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

@ApplicationScoped
public class EdgeCardinalityEvaluationHandler implements RuleEvaluationHandler<EdgeOccurrences, EdgeCardinalityContext> {

    @Override
    public Class<EdgeOccurrences> getRuleType() {
        return EdgeOccurrences.class;
    }

    @Override
    public Class<EdgeCardinalityContext> getContextType() {
        return EdgeCardinalityContext.class;
    }

    @Override
    public boolean accepts(final EdgeOccurrences rule,
                           final EdgeCardinalityContext context) {
        final ConnectorCardinalityContext.Direction direction = rule.getDirection();
        final String ruleRole = rule.getRole();
        return direction.equals(context.getDirection()) && context.getRoles().contains(ruleRole);
    }

    @Override
    public RuleViolations evaluate(final EdgeOccurrences rule,
                                   final EdgeCardinalityContext context) {

        final int minOccurrences = rule.getMinOccurrences();
        final int maxOccurrences = rule.getMaxOccurrences();
        final Set<String> candidateRoles = context.getRoles();
        final int candidatesCount = context.getCandidateCount();
        final CardinalityContext.Operation operation = context.getOperation();
        final DefaultRuleViolations results = new DefaultRuleViolations();
        final int _count = operation.equals(CardinalityContext.Operation.NONE) ? candidatesCount :
                (operation.equals(CardinalityContext.Operation.ADD) ? candidatesCount + 1 :
                        (candidatesCount > 0 ? candidatesCount - 1 : 0)
                );
        if (_count < minOccurrences) {
            results.addViolation(new CardinalityMinRuleViolation(candidateRoles.toString(),
                                                                 rule.getName(),
                                                                 minOccurrences,
                                                                 candidatesCount));
        } else if (maxOccurrences > -1 && _count > maxOccurrences) {
            results.addViolation(new CardinalityMaxRuleViolation(candidateRoles.toString(),
                                                                 rule.getName(),
                                                                 maxOccurrences,
                                                                 candidatesCount));
        }
        return results;
    }
}
