/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks;

import java.util.Collection;
import java.util.Set;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.ConditionsInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.InspectorList;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.LeafInspectorList;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspectorCache;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.BooleanConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.ComparableConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.ConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.NumericIntegerConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.OneToManyCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Redundancy;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.FieldCondition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.ObjectField;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Rule;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Severity;

public class RangeCheck
        extends OneToManyCheck {

    public RangeCheck( final RuleInspector ruleInspector ) {
        super( ruleInspector,
               new RuleInspectorCache.Filter() {
                   @Override
                   public boolean accept( final RuleInspector other ) {
                       return !ruleInspector.getRule().getUuidKey().equals( other.getRule().getUuidKey() );
                   }
               } );
    }

    @Override
    public void check() {

        // For some reason these clones always turn out to be evil.
        final RuleInspectorClone evilClone = makeClone();

        if ( evilClone.containsInvertedItems && !isSubsumedByOtherRows( evilClone ) ) {
            hasIssues = true;
        } else {
            hasIssues = false;
        }
    }

    private boolean isSubsumedByOtherRows( final RuleInspectorClone evilClone ) {
        final Collection<RuleInspector> otherRows = getOtherRows();
        if ( otherRows.isEmpty() ) {
            // Currently not reporting this issue if there is only one row.
            return true;
        } else {
            return Redundancy.isSubsumedByAnObjectInThisList( otherRows,
                                                              evilClone );
    }
    }

    private FieldCondition invert( final FieldCondition condition ) {
        return new FieldCondition<>( condition.getField(),
                                     condition.getColumn(),
                                     invert( condition.getOperator() ),
                                     condition.getValues() );
    }

    private String invert( final String operator ) {

        switch ( operator ) {
            case "==":
                return "!=";
            case "!=":
                return "==";
            case ">":
                return "<=";
            case "<":
                return ">=";
            case ">=":
                return "<";
            case "<=":
                return ">";
            default:
                return operator;
        }
    }

    private RuleInspectorClone makeClone() {
        return new RuleInspectorClone( ruleInspector.getRule(),
                                       ruleInspector.getCache() );
    }

    @Override
    public Issue getIssue() {
        final Issue issue = new Issue( Severity.NOTE,
                                       AnalysisConstants.INSTANCE.MissingRangeTitle(),
                                       ruleInspector.getRowIndex() + 1 );

        issue.getExplanation()
             .addParagraph( AnalysisConstants.INSTANCE.MissingRangeP1( ruleInspector.getRowIndex() + 1 ) );

        return issue;
    }

    private class RuleInspectorClone
            extends RuleInspector {

        private final InspectorList<ConditionsInspector> conditionsInspectors;
        boolean containsInvertedItems = false;

        public RuleInspectorClone( final Rule rule,
                                   final RuleInspectorCache cache ) {
            super( rule,
                   cache );
            conditionsInspectors = makeConditionsInspectors();
        }

        @Override
        public InspectorList<ConditionsInspector> getConditionsInspectors() {
            return conditionsInspectors;
        }

        private InspectorList<ConditionsInspector> makeConditionsInspectors() {
            final InspectorList<ConditionsInspector> clonedConditionInspectors = new InspectorList<>();

            InspectorList<ConditionsInspector> conditionsInspectors1 = super.getConditionsInspectors();
            for ( final ConditionsInspector original : conditionsInspectors1 ) {

                final ConditionsInspector clone = new ConditionsInspector();

                Set<ObjectField> keys = original.keys();
                for ( final ObjectField field : keys ) {

                    LeafInspectorList<ConditionInspector> originalConditionInspectors = original.get( field );
                    if ( originalConditionInspectors.isEmpty() ) {
                        clone.putAllValues( field,
                                            originalConditionInspectors );
                    } else {
                        for ( final ConditionInspector originalInspector : originalConditionInspectors ) {
                            clone.put( field,
                                       resolveInspector( originalInspector ) );
                        }
                    }
                }

                clonedConditionInspectors.add( clone );
            }

            return clonedConditionInspectors;
        }

        private ConditionInspector resolveInspector( final ConditionInspector originalInspector ) {

            if ( originalInspector.getCondition() instanceof FieldCondition ) {
                final FieldCondition fieldCondition = ( FieldCondition ) originalInspector.getCondition();

                if ( fieldCondition.getField().getFieldType().equals( "Integer" ) ) {

                    containsInvertedItems = true;
                    return new NumericIntegerConditionInspector( invert( fieldCondition ) );

                } else if ( DataType.isNumeric( fieldCondition.getField().getFieldType() ) ) {

                    containsInvertedItems = true;
                    return new ComparableConditionInspector<>( invert( fieldCondition ) );

                } else if ( fieldCondition.getField().getFieldType().equals( "Boolean" ) ) {

                    containsInvertedItems = true;
                    return new BooleanConditionInspector( invert( fieldCondition ) );

                }
            }

            return originalInspector;
        }

        @Override
        public boolean subsumes( final Object other ) {
            return other instanceof RuleInspector
                    && Redundancy.subsumes( getBrlConditionsInspectors(),
                                            (( RuleInspector ) other).getBrlConditionsInspectors() )
                    && Redundancy.subsumes( getConditionsInspectors(),
                                            (( RuleInspector ) other).getConditionsInspectors() );
        }
    }
}
