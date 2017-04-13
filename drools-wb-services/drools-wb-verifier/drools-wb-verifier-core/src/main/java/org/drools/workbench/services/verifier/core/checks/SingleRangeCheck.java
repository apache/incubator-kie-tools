/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.FieldCondition;
import org.drools.workbench.services.verifier.api.client.index.ObjectField;
import org.drools.workbench.services.verifier.api.client.relations.Operator;
import org.drools.workbench.services.verifier.api.client.reporting.CheckType;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.services.verifier.api.client.reporting.Severity;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.core.cache.inspectors.condition.ConditionInspector;
import org.drools.workbench.services.verifier.core.checks.base.CheckBase;

import static java.util.stream.Collectors.*;
import static org.drools.workbench.services.verifier.api.client.relations.Operator.resolve;

public class SingleRangeCheck extends CheckBase {

    private List<RangeError> errors = new ArrayList<>();

    private final Collection<RuleInspector> ruleInspectors;

    public SingleRangeCheck( AnalyzerConfiguration configuration, Collection<RuleInspector> ruleInspectors ) {
        super( configuration );
        this.ruleInspectors = ruleInspectors;
    }

    @Override
    public boolean check() {
        if (ruleInspectors == null || ruleInspectors.isEmpty()) {
            return hasIssues = false;
        }
        errors.clear();
        int conditionNr = ruleInspectors.iterator().next().getConditionsInspectors().size();
        for (int i = 0; i < conditionNr; i++) {
            checkCondition(i);
        }
        return hasIssues = !errors.isEmpty();
    }

    private void checkCondition(int conditionIndex) {
        Map<OperatorType, Set<ObjectField>> fields = ruleInspectors.stream()
                                                                   .map( r -> r.getConditionsInspectors().get( conditionIndex ) )
                                                                   .flatMap( cond -> cond.keySet().stream() )
                                                                   .collect( groupingBy( f -> getFieldOperatorType(f, conditionIndex), toSet() ) );

        Set<ObjectField> rangeFields = fields.get(OperatorType.RANGE);
        if (rangeFields != null && !rangeFields.isEmpty()) {
            checkRanges( rangeFields, partition(fields.get(OperatorType.PARTITION), ruleInspectors, conditionIndex), conditionIndex );
        }
    }

    private void checkRanges( Collection<ObjectField> rangeFields, Map<PartitionKey, List<RuleInspector>> partitions, int conditionIndex ) {
        for (Map.Entry<PartitionKey, List<RuleInspector>> partition : partitions.entrySet()) {
            for ( ObjectField field : rangeFields ) {
                if ( "Integer".equals( field.getFieldType() ) ) {
                    int upper = getIntegerCoveredUpperBound( partition.getValue(), field, conditionIndex, Integer.MIN_VALUE );
                    if ( upper != Integer.MAX_VALUE ) {
                        errors.add( new RangeError( partition.getValue(), partition.getKey(), upper ) );
                    }
                } else {
                    double upper = getNumericCoveredUpperBound( partition.getValue(), field, conditionIndex, Double.MIN_VALUE );
                    if ( upper != Double.MAX_VALUE ) {
                        errors.add( new RangeError( partition.getValue(), partition.getKey(), upper ) );
                    }
                }
            }
        }
    }

    private OperatorType getFieldOperatorType(ObjectField field, int conditionIndex) {
        return ruleInspectors.stream()
                             .flatMap( r -> getConditionStream( r, field, conditionIndex ) )
                             .map( c -> resolve( (( FieldCondition ) c.getCondition()).getOperator() ) )
                             .map( OperatorType::decode )
                             .reduce( OperatorType.PARTITION, OperatorType::combine );
    }

    enum OperatorType {
        PARTITION, RANGE, UNKNOWN;

        static OperatorType decode(Operator op) {
            return op == Operator.EQUALS ? PARTITION : op.isRangeOperator() ? RANGE : UNKNOWN;
        }

        OperatorType combine(OperatorType other) {
            if (this == UNKNOWN || other == UNKNOWN) {
                return UNKNOWN;
            }
            if (this == RANGE || other == RANGE) {
                return RANGE;
            }
            return PARTITION;
        }
    }

    private Map<PartitionKey, List<RuleInspector>> partition(Collection<ObjectField> partitionFields, Collection<RuleInspector> rules, int conditionIndex) {
        List<PartitionKey> keysWithNull = new ArrayList<>();

        Map<PartitionKey, List<RuleInspector>> partitions = new HashMap<>();
        for (RuleInspector rule : rules) {
            PartitionKey key = getPartitionKey(partitionFields, rule, conditionIndex);
            partitions.computeIfAbsent( key, k -> {
                if (k.hasNulls()) {
                    keysWithNull.add(k);
                }
                return new ArrayList<>();
            } ).add(rule);
        }

        for (PartitionKey key : keysWithNull) {
            for (Map.Entry<PartitionKey, List<RuleInspector>> partition : partitions.entrySet()) {
                if (key.subsumes(partition.getKey())) {
                    partition.getValue().addAll(partitions.get(key));
                }
            }
        }

        keysWithNull.forEach( partitions::remove );
        return partitions;
    }

    private PartitionKey getPartitionKey(Collection<ObjectField> partitionFields, RuleInspector rule, int conditionIndex) {
        return partitionFields == null || partitionFields.isEmpty() ?
               PartitionKey.EMPTY_KEY :
               new PartitionKey( partitionFields.stream().map( f -> getValue(rule, f, conditionIndex) ).toArray() );
    }

    private Object getValue(RuleInspector rule, ObjectField field, int conditionIndex) {
        List<ConditionInspector> conditions = getConditions( rule, field, conditionIndex );
        return conditions != null ? conditions.get(0).getCondition().getValues().iterator().next() : null;
    }

    private Stream<ConditionInspector> getConditionStream( RuleInspector rule, ObjectField field, int conditionIndex ) {
        List<ConditionInspector> conditionInspectors = getConditions( rule, field, conditionIndex );
        return conditionInspectors != null ? conditionInspectors.stream() : Stream.empty();
    }

    private List<ConditionInspector> getConditions( RuleInspector rule, ObjectField field, int conditionIndex ) {
        return rule.getConditionsInspectors() != null ? rule.getConditionsInspectors().get( conditionIndex ).get( field ) : null;
    }

    private static class PartitionKey {
        private static PartitionKey EMPTY_KEY = new PartitionKey( new Object[0] );

        private final Object[] keys;

        private PartitionKey( Object[] keys ) {
            this.keys = keys;
        }

        @Override
        public boolean equals( Object obj ) {
            return Arrays.equals( keys, ((PartitionKey)obj).keys );
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode( keys );
        }

        @Override
        public String toString() {
            return Arrays.toString( keys );
        }

        public boolean hasNulls() {
            return Stream.of( keys ).anyMatch( Objects::isNull );
        }

        public boolean subsumes( PartitionKey other ) {
            return IntStream.range( 0, keys.length ).allMatch( i -> keys[i] == null || keys[i].equals( other.keys[i] ) );
        }
    }

    private int getIntegerCoveredUpperBound(Collection<RuleInspector> rules, ObjectField field, int conditionIndex, int lowerBound) {
        List<IntegerRange> ranges = rules.stream()
                                         .map( r -> r.getConditionsInspectors().get( conditionIndex ) )
                                         .map(c -> c.get(field))
                                         .map( IntegerRange::new )
                                         .sorted().collect( toList() );
        int limit = lowerBound;
        for (IntegerRange range : ranges) {
            if (range.lowerBound > limit) {
                return limit;
            }
            limit = Math.max(limit, range.upperBound);
        }
        return limit;
    }

    private static class IntegerRange implements Comparable<IntegerRange> {
        private int lowerBound = Integer.MIN_VALUE;
        private int upperBound = Integer.MAX_VALUE;

        public IntegerRange(List<ConditionInspector> conditionInspectors) {
            if (conditionInspectors != null) {
                init( conditionInspectors );
            }
        }

        private void init( List<ConditionInspector> conditionInspectors ) {
            for (ConditionInspector c : conditionInspectors) {
                FieldCondition cond = ( FieldCondition ) c.getCondition();
                Operator op = resolve( cond.getOperator() );
                switch (op) {
                    case LESS_OR_EQUAL:
                        upperBound = (Integer) cond.getValues().iterator().next() + 1;
                        break;
                    case LESS_THAN:
                        upperBound = (Integer) cond.getValues().iterator().next();
                        break;
                    case GREATER_OR_EQUAL:
                        lowerBound = (Integer) cond.getValues().iterator().next() - 1;
                        break;
                    case GREATER_THAN:
                        lowerBound = (Integer) cond.getValues().iterator().next();
                        break;
                    case EQUALS:
                        lowerBound = (Integer) cond.getValues().iterator().next();
                        upperBound = (Integer) cond.getValues().iterator().next();
                        break;
                }
            }
        }

        @Override
        public String toString() {
            return lowerBound + " < x < " + upperBound;
        }

        @Override
        public int compareTo( IntegerRange o ) {
            return lowerBound < o.lowerBound ? -1 : lowerBound > o.lowerBound ? 1 : 0;
        }
    }

    private double getNumericCoveredUpperBound(Collection<RuleInspector> rules, ObjectField field, int conditionIndex, double lowerBound) {
        List<NumericRange> ranges = rules.stream()
                                         .map( r -> r.getConditionsInspectors().get( conditionIndex ) )
                                         .map(c -> c.get(field))
                                         .map( NumericRange::new )
                                         .sorted().collect( toList() );
        double limit = lowerBound;
        for (NumericRange range : ranges) {
            if (range.lowerBound > limit) {
                return limit;
            }
            limit = Math.max(limit, range.upperBound);
        }
        return limit;
    }

    private static class NumericRange implements Comparable<NumericRange> {
        private double lowerBound = Double.MIN_VALUE;
        private double upperBound = Double.MAX_VALUE;

        public NumericRange(List<ConditionInspector> conditionInspectors) {
            if (conditionInspectors != null) {
                init( conditionInspectors );
            }
        }

        private void init( List<ConditionInspector> conditionInspectors ) {
            for (ConditionInspector c : conditionInspectors) {
                FieldCondition cond = ( FieldCondition ) c.getCondition();
                Operator op = resolve( cond.getOperator() );
                switch (op) {
                    case LESS_OR_EQUAL:
                    case LESS_THAN:
                        upperBound = ((Number) cond.getValues().iterator().next()).doubleValue();
                        break;
                    case GREATER_THAN:
                    case GREATER_OR_EQUAL:
                        lowerBound = ((Number) cond.getValues().iterator().next()).doubleValue();
                        break;
                }
            }
        }

        @Override
        public String toString() {
            return lowerBound + " < x < " + upperBound;
        }

        @Override
        public int compareTo( NumericRange o ) {
            return lowerBound < o.lowerBound ? -1 : lowerBound > o.lowerBound ? 1 : 0;
        }
    }

    @Override
    protected Issue makeIssue( Severity severity, CheckType checkType ) {
        return errors.get(0).toIssue( severity, checkType );
    }

    private static class RangeError {
        private final Collection<RuleInspector> ruleInspectors;
        private final PartitionKey partitionKey;
        private final Object uncoveredValue;

        private RangeError( Collection<RuleInspector> ruleInspectors, PartitionKey partitionKey, Object uncoveredValue ) {
            this.ruleInspectors = ruleInspectors;
            this.partitionKey = partitionKey;
            this.uncoveredValue = uncoveredValue;
        }

        private Issue toIssue( Severity severity, CheckType checkType ) {
            return new Issue( severity,
                              checkType,
                              new HashSet<>( ruleInspectors.stream().map( r -> r.getRowIndex() + 1 ).collect( toSet() ) )
            ).setDebugMessage( "Uncovered range starting from value " + uncoveredValue + " in partition " + partitionKey );
        }
    }

    @Override
    protected CheckType getCheckType() {
        return CheckType.MISSING_RANGE;
    }

    @Override
    protected Severity getDefaultSeverity() {
        return Severity.NOTE;
    }
}
