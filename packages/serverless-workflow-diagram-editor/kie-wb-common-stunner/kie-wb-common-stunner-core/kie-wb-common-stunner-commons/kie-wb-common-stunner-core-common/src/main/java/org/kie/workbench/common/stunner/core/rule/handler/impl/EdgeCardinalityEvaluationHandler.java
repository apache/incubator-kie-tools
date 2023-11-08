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
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.impl.EdgeOccurrences;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.violations.EdgeCardinalityMaxRuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.EdgeCardinalityMinRuleViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;

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
        final EdgeCardinalityContext.Direction direction = rule.getDirection();
        return context.getEdgeRole().equals(rule.getConnectorRole())
                && direction.equals(context.getDirection())
                && context.getRoles().contains(rule.getRole());
    }

    @Override
    public RuleViolations evaluate(final EdgeOccurrences rule,
                                   final EdgeCardinalityContext context) {
        final DefaultRuleViolations results = new DefaultRuleViolations();
        final int minOccurrences = rule.getMinOccurrences();
        final int maxOccurrences = rule.getMaxOccurrences();
        final int candidatesCount = context.getCurrentCount();
        final Optional<CardinalityContext.Operation> operation = context.getOperation();
        final EdgeCardinalityContext.Direction direction = rule.getDirection();
        final Violation.Type type = operation
                .filter(CardinalityContext.Operation.ADD::equals)
                .isPresent() ? Violation.Type.ERROR : Violation.Type.WARNING;
        final int _count = !operation.isPresent() ? candidatesCount :
                (operation.get().equals(CardinalityContext.Operation.ADD) ? candidatesCount + 1 :
                        (candidatesCount > 0 ? candidatesCount - 1 : 0)
                );
        if (_count < minOccurrences) {
            results.addViolation(new EdgeCardinalityMinRuleViolation(context.getRoles().toString(),
                                                                     context.getEdgeRole(),
                                                                     minOccurrences,
                                                                     candidatesCount,
                                                                     direction,
                                                                     type));
        } else if (maxOccurrences > -1 && _count > maxOccurrences) {
            results.addViolation(new EdgeCardinalityMaxRuleViolation(context.getRoles().toString(),
                                                                     context.getEdgeRole(),
                                                                     maxOccurrences,
                                                                     candidatesCount,
                                                                     direction,
                                                                     type));
        }
        return results;
    }
}
