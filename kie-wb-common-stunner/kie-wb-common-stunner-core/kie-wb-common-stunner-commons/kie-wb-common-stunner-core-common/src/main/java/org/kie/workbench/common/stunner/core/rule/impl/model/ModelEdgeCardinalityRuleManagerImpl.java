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

package org.kie.workbench.common.stunner.core.rule.impl.model;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.rule.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.impl.AbstractEdgeCardinalityRuleManager;
import org.kie.workbench.common.stunner.core.rule.impl.violations.CardinalityMaxRuleViolation;
import org.kie.workbench.common.stunner.core.rule.impl.violations.CardinalityMinRuleViolation;
import org.kie.workbench.common.stunner.core.rule.model.ModelEdgeCardinalityRuleManager;

@Dependent
public class ModelEdgeCardinalityRuleManagerImpl extends AbstractEdgeCardinalityRuleManager
        implements ModelEdgeCardinalityRuleManager {

    private static Logger LOGGER = Logger.getLogger(ModelEdgeCardinalityRuleManagerImpl.class.getName());

    private static final String NAME = "Domain Model Edge Cardinality Rule Manager";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public RuleViolations evaluate(final String edgeId,
                                   final Set<String> labels,
                                   final int count,
                                   final EdgeCardinalityRule.Type ruleType,
                                   final Operation operation) {
        LOGGER.log(Level.FINE,
                   "Evaluating edge cardinality rules with arguments "
                           + "[edgeId=" + edgeId
                           + ", labels=" + labels
                           + ", count=" + count
                           + ", ruleType=" + ruleType
                           + ", operation=" + operation + "]");
        if (rules.isEmpty()) {
            return new DefaultRuleViolations();
        }
        final DefaultRuleViolations results = new DefaultRuleViolations();
        for (EdgeCardinalityRule rule : rules) {
            final int minOccurrences = rule.getMinOccurrences();
            final int maxOccurrences = rule.getMaxOccurrences();
            final EdgeCardinalityRule.Type type = rule.getType();
            if (ruleType.equals(type) && labels != null && labels.contains(rule.getRole())) {
                final int _count = operation.equals(Operation.NONE) ? count :
                        (operation.equals(Operation.ADD) ? count + 1 :
                                (count > 0 ? count - 1 : 0)
                        );
                if (_count < minOccurrences) {
                    results.addViolation(new CardinalityMinRuleViolation(labels.toString(),
                                                                         rule.getName(),
                                                                         minOccurrences,
                                                                         count));
                } else if (maxOccurrences > -1 && _count > maxOccurrences) {
                    results.addViolation(new CardinalityMaxRuleViolation(labels.toString(),
                                                                         rule.getName(),
                                                                         maxOccurrences,
                                                                         count));
                }
            }
        }
        return results;
    }
}
