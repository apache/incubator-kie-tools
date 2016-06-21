/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import java.util.Collection;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Conflict;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.HumanReadable;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsConflicting;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsDeficient;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsRedundant;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Redundancy;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Actions;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Condition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Conditions;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Pattern;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Rule;

public class RuleInspector
        implements IsRedundant,
                   IsSubsuming,
                   IsConflicting,
                   IsDeficient<RuleInspector>,
                   HumanReadable {

    private final Rule               rule;
    private final RuleInspectorCache cache;

    private final InspectorList<PatternInspector>    patternInspectorList = new InspectorList<>();

    public RuleInspector( final Rule rule,
                          final RuleInspectorCache cache ) {
        this.rule = rule;
        this.cache = cache;

        for ( final Pattern pattern : rule.getPatterns()
                                          .where( Pattern.uuid().any() )
                                          .select().all() ) {
            final PatternInspector patternInspector = new PatternInspector( pattern );

            patternInspectorList.add( patternInspector );
        }
    }

    private InspectorList<ConditionsInspector> getConditionsInspectors() {
        final InspectorList<ConditionsInspector> conditionsInspectors = new InspectorList<>();

        for ( final PatternInspector patternInspector : patternInspectorList ) {
            conditionsInspectors.add( patternInspector.getConditionsInspector() );
        }

        return conditionsInspectors;
    }

    private InspectorList<ActionsInspector> getActionsInspectors() {
        final InspectorList<ActionsInspector> actionsInspectors = new InspectorList<>();
        for ( final PatternInspector patternInspector : patternInspectorList ) {
            actionsInspectors.add( patternInspector.getActionsInspector() );
        }
        return actionsInspectors;
    }

    public InspectorList<PatternInspector> getPatternsInspector() {
        return patternInspectorList;
    }

    public int getRowIndex() {
        return rule.getRowNumber();
    }

    public RuleInspectorCache getCache() {
        return cache;
    }

    @Override
    public boolean isRedundant( final Object other ) {
        return other instanceof RuleInspector
                && Redundancy.isRedundant( patternInspectorList,
                                           (( RuleInspector ) other).patternInspectorList );
    }

    @Override
    public boolean subsumes( final Object other ) {
        return other instanceof RuleInspector
                && Redundancy.subsumes( patternInspectorList,
                                        (( RuleInspector ) other).patternInspectorList );
    }

    @Override
    public boolean conflicts( final Object other ) {
        if ( other instanceof RuleInspector ) {
            if ( Conflict.isConflicting( getActionsInspectors(),
                                         (( RuleInspector ) other).getActionsInspectors() ) ) {
                if ( Redundancy.subsumes( getConditionsInspectors(),
                                          (( RuleInspector ) other).getConditionsInspectors() ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public Rule getRule() {
        return rule;
    }

    @Override
    public boolean isDeficient( final RuleInspector other ) {

        if ( other.atLeastOneActionHasAValue() && !Conflict.isConflicting( getActionsInspectors(),
                                                                           other.getActionsInspectors() ) ) {
            return false;
        }

        final Collection<Condition> allConditionsFromTheOtherRule = other.rule.getPatterns()
                                                    .where( Pattern.uuid().any() )
                                                    .select().fields()
                                                    .where( Field.uuid().any() )
                                                    .select().conditions()
                                                    .where( Condition.uuid().isNot( null ) )
                                                    .select().all();

        if ( allConditionsFromTheOtherRule.isEmpty() ) {
            return true;
        } else {

            for ( final Condition condition : allConditionsFromTheOtherRule ) {

                if ( condition.getValue() == null ) {
                    continue;
                }

                final Conditions conditions = rule.getPatterns()
                                                  .where( Pattern.name().is( condition.getField().getFactType() ) )
                                                  .select().fields()
                                                  .where( Field.name().is( condition.getField().getName() ) )
                                                  .select().conditions();
                if ( conditions
                        .where( Condition.value().isNot( null ) )
                        .select().exists() ) {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean atLeastOneActionHasAValue() {
        final Actions actions = rule.getPatterns()
                                    .where( Pattern.uuid().any() )
                                    .select().fields()
                                    .where( Field.uuid().any() )
                                    .select().actions();

        final int amountOfActions = actions.where( Condition.value().isNot( null ) ).select().all().size();

        return amountOfActions > 0;
    }

    public boolean atLeastOneConditionHasAValue() {
        final Conditions conditions = rule.getPatterns()
                                          .where( Pattern.uuid().any() )
                                          .select().fields()
                                          .where( Field.uuid().any() )
                                          .select().conditions();

        final int amountOfConditions = conditions.where( Condition.value().isNot( null ) ).select().all().size();

        return amountOfConditions > 0;
    }

    @Override
    public String toHumanReadableString() {
        return rule.getRowNumber().toString();
    }
}
