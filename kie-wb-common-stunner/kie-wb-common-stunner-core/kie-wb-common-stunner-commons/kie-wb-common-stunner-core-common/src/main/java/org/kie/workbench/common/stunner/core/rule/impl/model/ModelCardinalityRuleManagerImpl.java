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

import org.kie.workbench.common.stunner.core.rule.CardinalityRule;
import org.kie.workbench.common.stunner.core.rule.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.impl.AbstractCardinalityRuleManager;
import org.kie.workbench.common.stunner.core.rule.impl.violations.CardinalityMaxRuleViolation;
import org.kie.workbench.common.stunner.core.rule.impl.violations.CardinalityMinRuleViolation;
import org.kie.workbench.common.stunner.core.rule.model.ModelCardinalityRuleManager;

import javax.enterprise.context.Dependent;

@Dependent
public class ModelCardinalityRuleManagerImpl extends AbstractCardinalityRuleManager
        implements ModelCardinalityRuleManager {

    private static final String NAME = "Domain Model Cardinality Rule Manager";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public RuleViolations evaluate( final String label,
                                     final int candidatesCount,
                                     final Operation operation ) {
        if ( rules.isEmpty() ) {
            return new DefaultRuleViolations();
        }
        final DefaultRuleViolations results = new DefaultRuleViolations();
        for ( CardinalityRule rule : rules ) {
            if ( label.equals( rule.getRole() ) ) {
                final long minOccurrences = rule.getMinOccurrences();
                final long maxOccurrences = rule.getMaxOccurrences();
                final int count = operation.equals( Operation.NONE ) ? candidatesCount :
                        ( operation.equals( Operation.ADD ) ? candidatesCount + 1 : candidatesCount - 1 );
                if ( count < minOccurrences ) {
                    results.addViolation( new CardinalityMinRuleViolation( label, rule.getRole(), ( int ) minOccurrences, candidatesCount ) );
                } else if ( maxOccurrences > -1 && count > maxOccurrences ) {
                    results.addViolation( new CardinalityMaxRuleViolation( label, rule.getRole(), ( int ) maxOccurrences, candidatesCount ) );
                }
            }
        }
        return results;
    }

}
