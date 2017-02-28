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
package org.drools.workbench.services.verifier.core.checks;

import java.util.Arrays;
import java.util.HashSet;

import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.DataType;
import org.drools.workbench.services.verifier.api.client.index.FieldCondition;
import org.drools.workbench.services.verifier.api.client.index.ObjectField;
import org.drools.workbench.services.verifier.api.client.index.Rule;
import org.drools.workbench.services.verifier.api.client.maps.InspectorList;
import org.drools.workbench.services.verifier.api.client.maps.LeafInspectorList;
import org.drools.workbench.services.verifier.api.client.relations.SubsumptionResolver;
import org.drools.workbench.services.verifier.api.client.reporting.CheckType;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.services.verifier.api.client.reporting.Severity;
import org.drools.workbench.services.verifier.core.cache.RuleInspectorCache;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.core.cache.inspectors.condition.BooleanConditionInspector;
import org.drools.workbench.services.verifier.core.cache.inspectors.condition.ComparableConditionInspector;
import org.drools.workbench.services.verifier.core.cache.inspectors.condition.ConditionInspector;
import org.drools.workbench.services.verifier.core.cache.inspectors.condition.ConditionsInspectorMultiMap;
import org.drools.workbench.services.verifier.core.cache.inspectors.condition.NumericIntegerConditionInspector;
import org.drools.workbench.services.verifier.core.checks.base.CheckFactory;
import org.drools.workbench.services.verifier.core.checks.base.CheckStorage;
import org.drools.workbench.services.verifier.core.checks.base.OneToManyCheck;

public class RangeCheck
        extends OneToManyCheck {

    private InspectorList<RuleInspector> otherRows;

    public RangeCheck( final RuleInspector ruleInspector,
                       final AnalyzerConfiguration configuration ) {
        super( ruleInspector,
               new RuleInspectorCache.Filter() {
                   @Override
                   public boolean accept( final RuleInspector other ) {
                       return !ruleInspector.getRule()
                               .getUuidKey()
                               .equals( other.getRule()
                                                .getUuidKey() );
                   }
               },
               configuration,
               CheckType.MISSING_RANGE );
    }

    @Override
    public void check() {

        otherRows = getOtherRows();

        if ( otherRows.size() == 0 ) {
            hasIssues = false;
        } else {
            // For some reason these clones always turn out to be evil.
            final RuleInspectorClone evilClone = makeClone();

            if ( evilClone.containsInvertedItems && !isSubsumedByOtherRows( evilClone ) ) {
                hasIssues = true;
            } else {
                hasIssues = false;
            }
        }
    }

    private boolean isSubsumedByOtherRows( final RuleInspectorClone evilClone ) {
        if ( otherRows.isEmpty() ) {
            // Currently not reporting this issue if there is only one row.
            return true;
        } else {
            return !SubsumptionResolver.isSubsumedByAnObjectInThisList( otherRows,
                                                                        evilClone )
                    .foundIssue();
        }
    }

    private FieldCondition invert( final FieldCondition condition,
                                   final AnalyzerConfiguration configuration ) {
        return new FieldCondition<>( condition.getField(),
                                     condition.getColumn(),
                                     invert( condition.getOperator() ),
                                     condition.getValues(),
                                     configuration );
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
    protected Severity getDefaultSeverity() {
        return Severity.NOTE;
    }

    @Override
    protected Issue makeIssue( final Severity severity,
                               final CheckType checkType ) {
        return new Issue( severity,
                          checkType,
                          new HashSet<>( Arrays.asList( ruleInspector.getRowIndex() + 1 ) )
        );
    }

    private class RuleInspectorClone
            extends RuleInspector {

        private final InspectorList<ConditionsInspectorMultiMap> conditionsInspectors;
        boolean containsInvertedItems = false;

        public RuleInspectorClone( final Rule rule,
                                   final RuleInspectorCache cache ) {
            super( rule,
                   new CheckStorage( new CheckFactory( cache.getConfiguration() ) ),
                   cache,
                   cache.getConfiguration() );
            conditionsInspectors = new InspectorList<>( cache.getConfiguration() );
            makeConditionsInspectors();
        }

        @Override
        public InspectorList<ConditionsInspectorMultiMap> getConditionsInspectors() {
            return conditionsInspectors;
        }

        private void makeConditionsInspectors() {
            conditionsInspectors.clear();

            for ( final ConditionsInspectorMultiMap original : super.getConditionsInspectors() ) {

                final ConditionsInspectorMultiMap clone = new ConditionsInspectorMultiMap( getCache().getConfiguration() );

                for ( final ObjectField field : original.keySet() ) {

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

                conditionsInspectors.add( clone );
            }

        }

        private ConditionInspector resolveInspector( final ConditionInspector originalInspector ) {

            if ( originalInspector.getCondition() instanceof FieldCondition ) {
                final FieldCondition fieldCondition = (FieldCondition) originalInspector.getCondition();

                if ( fieldCondition.getField()
                        .getFieldType()
                        .equals( "Integer" ) ) {

                    containsInvertedItems = true;
                    return new NumericIntegerConditionInspector( invert( fieldCondition,
                                                                         getCache().getConfiguration() ),
                                                                 getCache().getConfiguration() );

                } else if ( DataType.isNumeric( fieldCondition.getField()
                                                        .getFieldType() ) ) {

                    containsInvertedItems = true;
                    return new ComparableConditionInspector<>( invert( fieldCondition,
                                                                       getCache().getConfiguration() ),
                                                               getCache().getConfiguration() );

                } else if ( fieldCondition.getField()
                        .getFieldType()
                        .equals( "Boolean" ) ) {

                    containsInvertedItems = true;
                    return new BooleanConditionInspector( invert( fieldCondition,
                                                                  getCache().getConfiguration() ),
                                                          getCache().getConfiguration() );

                }
            }

            return originalInspector;
        }

        @Override
        public boolean subsumes( final Object other ) {
            return other instanceof RuleInspector
                    && getBrlConditionsInspectors().subsumes( ( (RuleInspector) other ).getBrlConditionsInspectors() )
                    && getConditionsInspectors().subsumes( ( (RuleInspector) other ).getConditionsInspectors() );
        }
    }

}
