/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.core.rule.handler.impl;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.impl.Occurrences;
import org.kie.workbench.common.stunner.core.rule.violations.CardinalityMaxRuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.CardinalityMinRuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.validation.Violation;

@ApplicationScoped
public class CardinalityEvaluationHandler implements RuleEvaluationHandler<Occurrences, CardinalityContext> {

    @Override
    public Class<Occurrences> getRuleType() {
        return Occurrences.class;
    }

    @Override
    public Class<CardinalityContext> getContextType() {
        return CardinalityContext.class;
    }

    @Override
    public boolean accepts(final Occurrences rule,
                           final CardinalityContext context) {
        return context.getRoles().contains(rule.getRole());
    }

    @Override
    public RuleViolations evaluate(final Occurrences rule,
                                   final CardinalityContext context) {
        final DefaultRuleViolations results = new DefaultRuleViolations();
        final int minOccurrences = rule.getMinOccurrences();
        final int maxOccurrences = rule.getMaxOccurrences();
        final int currentCount = context.getCurrentCount();
        final int candidateCount = context.getCandidateCount();
        final Optional<CardinalityContext.Operation> operation = context.getOperation();
        final Violation.Type type = operation
                .filter(CardinalityContext.Operation.ADD::equals)
                .isPresent() ? Violation.Type.ERROR : Violation.Type.WARNING;
        final int count =
                !operation.isPresent() ?
                        currentCount :
                        (operation.get().equals(CardinalityContext.Operation.ADD) ?
                                currentCount + candidateCount :
                                currentCount - candidateCount);
        if (count < minOccurrences) {
            results.addViolation(new CardinalityMinRuleViolation(context.getRoles().toString(),
                                                                 minOccurrences,
                                                                 currentCount,
                                                                 type));
        } else if (maxOccurrences > -1 && count > maxOccurrences) {
            results.addViolation(new CardinalityMaxRuleViolation(context.getRoles().toString(),
                                                                 maxOccurrences,
                                                                 currentCount,
                                                                 type));
        }
        return results;
    }
}
