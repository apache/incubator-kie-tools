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
import java.util.Set;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.action.ActionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.action.BRLActionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.BRLConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.ConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.Check;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.CheckManager;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.HumanReadable;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsConflicting;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsDeficient;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsRedundant;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.AllListener;
import org.uberfire.commons.validation.PortablePreconditions;

public class RuleInspector
        implements IsRedundant,
                   IsSubsuming,
                   IsConflicting,
                   IsDeficient<RuleInspector>,
                   HumanReadable,
                   HasKeys {

    private final Rule rule;

    private final CheckManager       checkManager;
    private final RuleInspectorCache cache;

    private final UUIDKey uuidKey = new UUIDKey( this );

    private final InspectorList<PatternInspector>            patternInspectorList    = new InspectorList<>();
    private final InspectorList<ConditionInspector>          brlConditionsInspectors = new InspectorList<>(true);
    private final InspectorList<ActionInspector>             brlActionInspectors     = new InspectorList<>(true);
    private final InspectorList<ActionsInspectorMultiMap>    actionsInspectors       = new InspectorList<>( true );
    private final InspectorList<ConditionsInspectorMultiMap> conditionsInspectors    = new InspectorList<>(true);

    public RuleInspector( final Rule rule,
                          final CheckManager checkManager,
                          final RuleInspectorCache cache ) {
        this.rule = PortablePreconditions.checkNotNull( "rule", rule );
        this.checkManager = PortablePreconditions.checkNotNull( "checkManager", checkManager );
        this.cache = PortablePreconditions.checkNotNull( "cache", cache );

        makePatternsInspectors();
        makeBRLActionInspectors();
        makeBRLConditionInspectors();
        makeActionsInspectors();
        makeConditionsInspectors();

        makeChecks();

    }

    private void makeConditionsInspectors() {
        for ( final PatternInspector patternInspector : patternInspectorList ) {
            conditionsInspectors.add( patternInspector.getConditionsInspector() );
        }
    }

    private void makeActionsInspectors() {
        for ( final PatternInspector patternInspector : patternInspectorList ) {
            actionsInspectors.add( patternInspector.getActionsInspector() );
        }
    }

    private void makeBRLConditionInspectors() {
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

    private void makeBRLActionInspectors() {
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
    }

    private void makePatternsInspectors() {
        for ( final Pattern pattern : rule.getPatterns()
                                          .where( Pattern.uuid().any() )
                                          .select().all() ) {
            final PatternInspector patternInspector = new PatternInspector( pattern );

            patternInspectorList.add( patternInspector );
        }
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

    public InspectorList<ConditionsInspectorMultiMap> getConditionsInspectors() {
        return conditionsInspectors;
    }

    public InspectorList<ActionsInspectorMultiMap> getActionsInspectors() {
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
                && brlConditionsInspectors.isRedundant( (( RuleInspector ) other).brlConditionsInspectors )
                && brlActionInspectors.isRedundant( (( RuleInspector ) other).brlActionInspectors )
                && getActionsInspectors().isRedundant( (( RuleInspector ) other).getActionsInspectors() )
                && getConditionsInspectors().isRedundant( (( RuleInspector ) other).getConditionsInspectors() );
    }

    @Override
    public boolean subsumes( final Object other ) {
        return other instanceof RuleInspector
                && brlActionInspectors.subsumes( (( RuleInspector ) other).brlActionInspectors )
                && brlConditionsInspectors.subsumes( (( RuleInspector ) other).brlConditionsInspectors )
                && getActionsInspectors().subsumes( (( RuleInspector ) other).getActionsInspectors() )
                && getConditionsInspectors().subsumes( (( RuleInspector ) other).getConditionsInspectors() );
    }

    @Override
    public boolean conflicts( final Object other ) {
        if ( other instanceof RuleInspector ) {
            if ( getActionsInspectors().conflicts( (( RuleInspector ) other).getActionsInspectors() ) ) {
                if ( getConditionsInspectors().subsumes( (( RuleInspector ) other).getConditionsInspectors() ) ) {
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

        if ( other.atLeastOneActionHasAValue() && !getActionsInspectors().conflicts( other.getActionsInspectors() ) ) {
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

    public boolean isEmpty() {
        return !atLeastOneConditionHasAValue() && !atLeastOneActionHasAValue();
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

    public InspectorList<ConditionInspector> getBrlConditionsInspectors() {
        return brlConditionsInspectors;
    }

    public InspectorList<ActionInspector> getBrlActionInspectors() {
        return brlActionInspectors;
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey
        };
    }

    public Set<Check> getChecks() {
        return checkManager.getChecks( this );
    }

    private void makeChecks() {
        checkManager.makeChecks( this );
    }

    public Set<Check> clearChecks() {
        return checkManager.remove( this );
    }

    public CheckManager getCheckManager() {
        return checkManager;
    }
}
