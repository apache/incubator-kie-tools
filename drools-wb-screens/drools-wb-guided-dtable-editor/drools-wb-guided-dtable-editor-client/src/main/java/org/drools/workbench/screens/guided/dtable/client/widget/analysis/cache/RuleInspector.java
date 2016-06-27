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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.action.ActionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.action.BRLActionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.BRLConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.ConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Conflict;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.HumanReadable;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsConflicting;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsDeficient;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsRedundant;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Redundancy;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Action;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.ActionSuperType;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.BRLAction;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.BRLCondition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Condition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.ConditionSuperType;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Conditions;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.FieldCondition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Pattern;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Rule;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.AllListener;

public class RuleInspector
        implements IsRedundant,
                   IsSubsuming,
                   IsConflicting,
                   IsDeficient<RuleInspector>,
                   HumanReadable {

    private final Rule               rule;
    private final RuleInspectorCache cache;

    private final InspectorList<PatternInspector> patternInspectorList = new InspectorList<>();

    private final List<ConditionInspector> brlConditionsInspectors = new ArrayList<>();
    private final List<ActionInspector>    brlActionInspectors     = new ArrayList<>();

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

        updateBRLActionInspectors( rule.getActions()
                                       .where( Action.superType().is( ActionSuperType.BRL_ACTION ) )
                                       .select().all() );
        rule.getActions()
            .where( Action.superType().is( ActionSuperType.BRL_ACTION ) )
            .listen().all( new AllListener<Action>() {
            @Override
            public void onAllChanged( final Collection<Action> all ) {
                updateBRLActionInspectors( all );
            }
        } );

        updateBRLConditionInspectors( rule.getConditions()
                                          .where( Condition.superType().is( ConditionSuperType.BRL_CONDITION ) )
                                          .select().all() );
        rule.getConditions()
            .where( Condition.superType().is( ConditionSuperType.BRL_CONDITION ) )
            .listen().all( new AllListener<Condition>() {
            @Override
            public void onAllChanged( final Collection<Condition> all ) {
                updateBRLConditionInspectors( all );
            }
        } );
    }


    private void updateBRLConditionInspectors( final Collection<Condition> conditions ) {
        this.brlConditionsInspectors.clear();
        for ( final Condition condition : conditions ) {
            this.brlConditionsInspectors.add( new BRLConditionInspector( ( BRLCondition ) condition ) );
        }
    }

    private void updateBRLActionInspectors( final Collection<Action> actions ) {
        this.brlActionInspectors.clear();
        for ( final Action action : actions ) {
            this.brlActionInspectors.add( new BRLActionInspector( ( BRLAction ) action ) );
        }
    }

    public InspectorList<ConditionsInspector> getConditionsInspectors() {
        final InspectorList<ConditionsInspector> conditionsInspectors = new InspectorList<>();

        for ( final PatternInspector patternInspector : patternInspectorList ) {
            conditionsInspectors.add( patternInspector.getConditionsInspector() );
        }

        return conditionsInspectors;
    }

    public InspectorList<ActionsInspector> getActionsInspectors() {
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
                && Redundancy.isRedundant( brlConditionsInspectors,
                                           (( RuleInspector ) other).brlConditionsInspectors )
                && Redundancy.isRedundant( brlActionInspectors,
                                           (( RuleInspector ) other).brlActionInspectors )
                && Redundancy.isRedundant( getActionsInspectors(),
                                           (( RuleInspector ) other).getActionsInspectors() )
                && Redundancy.isRedundant( getConditionsInspectors(),
                                           (( RuleInspector ) other).getConditionsInspectors() );
    }

    @Override
    public boolean subsumes( final Object other ) {
        return other instanceof RuleInspector
                && Redundancy.subsumes( brlActionInspectors,
                                        (( RuleInspector ) other).brlActionInspectors )
                && Redundancy.subsumes( brlConditionsInspectors,
                                        (( RuleInspector ) other).brlConditionsInspectors )
                && Redundancy.subsumes( getActionsInspectors(),
                                        (( RuleInspector ) other).getActionsInspectors() )
                && Redundancy.subsumes( getConditionsInspectors(),
                                        (( RuleInspector ) other).getConditionsInspectors() );
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

        final Collection<Condition> allConditionsFromTheOtherRule = other.rule.getConditions()
                                                                              .where( Condition.value().any() )
                                                    .select().all();

        if ( allConditionsFromTheOtherRule.isEmpty() ) {
            return true;
        } else {

            for ( final Condition condition : allConditionsFromTheOtherRule ) {

                if ( condition.getValues() == null ) {
                    continue;
                }

                if ( condition instanceof FieldCondition ) {
                    final FieldCondition fieldCondition = ( FieldCondition ) condition;
                    final Conditions conditions = rule.getPatterns()
                                                      .where( Pattern.name().is( fieldCondition.getField().getFactType() ) )
                                                      .select().fields()
                                                      .where( Field.name().is( fieldCondition.getField().getName() ) )
                                                      .select().conditions();
                    if ( conditions
                            .where( Condition.value().any() )
                            .select().exists() ) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public boolean atLeastOneActionHasAValue() {
        final int amountOfActions = rule.getActions()
                                        .where( Action.value().any() )
                                        .select().all().size();
        return amountOfActions > 0;
    }

    public boolean atLeastOneConditionHasAValue() {
        final int amountOfConditions = rule.getConditions()
                                           .where( Condition.value().any() )
                                           .select().all().size();
        return amountOfConditions > 0;
    }

    @Override
    public String toHumanReadableString() {
        return rule.getRowNumber().toString();
    }

    public List<ConditionInspector> getBrlConditionsInspectors() {
        return brlConditionsInspectors;
    }

    public List<ActionInspector> getBrlActionInspectors() {
        return brlActionInspectors;
    }
}
