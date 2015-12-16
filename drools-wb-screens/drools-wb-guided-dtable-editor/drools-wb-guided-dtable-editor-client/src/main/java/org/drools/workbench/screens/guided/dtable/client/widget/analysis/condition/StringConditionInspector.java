/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Operator;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Redundancy;

public class StringConditionInspector
        extends ComparableConditionInspector<String> {

    private final List<String> values = new ArrayList<String>();

    public StringConditionInspector( final Pattern52 pattern,
                                     final String factField,
                                     final String value,
                                     final String operator ) {
        super( pattern,
               factField,
               value,
               operator );


        if ( operator.equals( "== null" ) || operator.equals( "!= null" ) ) {
            values.add( "null" );
        } else {
            switch (this.operator) {
                case NOT_IN:
                case IN:
                    for (String item : value.split( "," )) {
                        values.add( item.trim() );
                    }
                    break;
                default:
                    values.add( value );
            }
        }
    }

    public List<String> getValues() {
        return values;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public boolean isRedundant( Object other ) {
        if ( this.equals( other ) ) {
            return true;
        }
        if ( other instanceof IsSubsuming ) {
            boolean b = subsumes( other ) && ( (IsSubsuming) other ).subsumes( this );
            return b;
        } else {
            return false;
        }
    }

    @Override
    public boolean conflicts( Object other ) {
        if ( this.equals( other ) ) {
            return false;
        }

        if ( other instanceof StringConditionInspector ) {

            if ( !hasValue() || !( (StringConditionInspector) other ).hasValue() ) {
                return false;
            }

            if ( (doesNotContainAll( ((StringConditionInspector) other).getValues() )
                    || ((StringConditionInspector) other).doesNotContainAll( getValues() ))
                    &&
                    (eitherOperatorIs( (StringConditionInspector) other, Operator.LESS_THAN )
                            || eitherOperatorIs( (StringConditionInspector) other, Operator.LESS_OR_EQUAL )
                            || eitherOperatorIs( (StringConditionInspector) other, Operator.GREATER_THAN )
                            || eitherOperatorIs( (StringConditionInspector) other, Operator.GREATER_OR_EQUAL )) ) {
                return false;
            }


            if ( operatorsAre( ((StringConditionInspector) other),
                               Operator.NOT_EQUALS ) ) {
                return false;
            }
        }

        boolean conflicts = !overlaps( other );
        return conflicts;
    }

    private boolean eitherOperatorIs( StringConditionInspector other, Operator operator ) {
        return other.getOperator().equals( operator )
                || this.operator.equals( operator );
    }

    @Override
    public boolean overlaps( Object other ) {
        if ( other instanceof StringConditionInspector ) {
            StringConditionInspector otherInspector = (StringConditionInspector) other;

            if ( value.isEmpty() || ((StringConditionInspector) other).getValue().isEmpty() ) {
                return false;
            }

            if ( operatorsAre( otherInspector, Operator.LESS_THAN )
                    || (operatorsAre( otherInspector, Operator.GREATER_THAN ))
                    || (operatorsAre( otherInspector, Operator.LESS_OR_EQUAL ))
                    || (operatorsAre( otherInspector, Operator.GREATER_OR_EQUAL ))
                    || operatorsAre( otherInspector, Operator.LESS_THAN, Operator.LESS_OR_EQUAL )
                    || operatorsAre( otherInspector, Operator.GREATER_THAN, Operator.GREATER_OR_EQUAL ) ) {
                return true;
            }

            if ( value.equals( otherInspector.getValue() )
                    && (operator.equals( otherInspector.getOperator() )) ) {
                return true;
            }

            if ( ((StringConditionInspector) other).getOperator().equals( Operator.LESS_THAN )
                    || operator.equals( Operator.LESS_THAN )
                    || ((StringConditionInspector) other).getOperator().equals( Operator.GREATER_THAN )
                    || operator.equals( Operator.GREATER_THAN ) ) {
                return false;
            }

            if ( !otherInspector.hasValue() ) {
                return false;
            } else {

                switch ( operator ) {
                    case MATCHES:
                    case SOUNDSLIKE:
                    case EQUALS:
                    case GREATER_OR_EQUAL:
                    case LESS_OR_EQUAL:
                        switch (otherInspector.getOperator()) {
                            case NOT_EQUALS:
                                return !otherInspector.containsAll( values );
                            case EQUALS:
                            case MATCHES:
                            case SOUNDSLIKE:
                                return otherInspector.containsAll( values );
                            case IN:
                                return otherInspector.valuesContains( values.get( 0 ) );
                            case NOT_IN:
                                return !otherInspector.valuesContains( values.get( 0 ) );
                            default:
                                return super.overlaps( other );
                        }
                    case NOT_IN:
                        switch (otherInspector.getOperator()) {
                            case EQUALS:
                            case MATCHES:
                            case SOUNDSLIKE:
                                return !valuesContains( otherInspector.getValue() );
                            case IN:
                                return doesNotContainAll( otherInspector.getValues() );
                            default:
                                return !otherInspector.containsAll( values );
                        }
                    case NOT_EQUALS:
                        switch (otherInspector.getOperator()) {
                            case IN:
                                return doesNotContainAll( ((StringConditionInspector) other).getValues() );
                            case NOT_EQUALS:
                                return !otherInspector.containsAll( values );
                            default:
                                return !otherInspector.valuesContains( values.get( 0 ) );
                        }
                    case IN:
                        switch (otherInspector.getOperator()) {
                            case EQUALS:
                            case MATCHES:
                            case SOUNDSLIKE:
                            case GREATER_OR_EQUAL:
                            case LESS_OR_EQUAL:
                                return valuesContains( otherInspector.getValues().get( 0 ) );
                            case NOT_EQUALS:
                                return otherInspector.doesNotContainAll( getValues() );
                            case NOT_IN:
                                return doesNotContainAll( otherInspector.getValues() );
                            case IN:
                                return containsAny( otherInspector.values );
                        }
                }
            }
        }

        return super.overlaps( other );
    }

    private boolean operatorsAre( final StringConditionInspector otherInspector,
                                  final Operator operator ) {
        return this.operator.equals( operator ) && otherInspector.getOperator().equals( operator );
    }

    private boolean operatorsAre( final StringConditionInspector otherInspector,
                                  final Operator a,
                                  final Operator b ) {
        return (this.operator.equals( a ) && otherInspector.getOperator().equals( b ))
                || (this.operator.equals( b ) && otherInspector.getOperator().equals( a ));
    }

    @Override
    public boolean covers( Comparable<String> otherValue ) {

        switch (operator) {
            case STR_STARTS_WITH:
                return getValue().startsWith( otherValue.toString() );
            case STR_ENDS_WITH:
                return getValue().endsWith( otherValue.toString() );
            case MATCHES:
            case SOUNDSLIKE:
                return valueIsEqualTo( otherValue );
            case CONTAINS:
                return false;
            case NOT_MATCHES:
                return !valueIsEqualTo( otherValue );
            case IN:
                return valuesContains( otherValue.toString() );
            case NOT_IN:
                return !valuesContains( otherValue.toString() );
            default:
                return super.covers( otherValue );
        }

    }

    protected boolean valueIsGreaterThanOrEqualTo( final Comparable<String> otherValue ) {
        return valueIsEqualTo( otherValue );
    }

    protected boolean valueIsLessThanOrEqualTo( final Comparable<String> otherValue ) {
        return valueIsEqualTo( otherValue );
    }

    protected boolean valueIsGreaterThan( final Comparable<String> otherValue ) {
        return false;
    }

    protected boolean valueIsLessThan( final Comparable<String> otherValue ) {
        return false;
    }

    protected boolean valueIsEqualTo( final Comparable<String> otherValue ) {
        return valuesContains( otherValue.toString() );
    }

    private boolean valuesContains( String value ) {
        return values.contains( value );
    }

    private boolean containsAll( final List<String> otherValues ) {
        if ( values.isEmpty() || otherValues.isEmpty() ) {
            return false;
        } else {
            for (String otherValue : otherValues) {
                if ( !values.contains( otherValue ) ) {
                    return false;
                }
            }
            return true;
        }
    }

    private boolean doesNotContainAll( final List<String> otherValues ) {
        for (String otherValue : otherValues) {
            if ( !values.contains( otherValue ) ) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAny( final List<String> otherValues ) {
        for (String thisValue : values) {
            for (String otherValue : otherValues) {
                if ( thisValue.equals( otherValue ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean subsumes( Object other ) {
        if ( other instanceof StringConditionInspector ) {

            if ( ( (StringConditionInspector) other ).getOperator().equals( operator ) ) {
                boolean subsumes = Redundancy.subsumes( getValues(),
                                                        ((StringConditionInspector) other).getValues() );
                return subsumes;
            }

            switch (operator) {
                case EQUALS:
                case MATCHES:
                case SOUNDSLIKE:
                case LESS_OR_EQUAL:
                case GREATER_OR_EQUAL:

                    if ( operatorsAre( (StringConditionInspector) other, Operator.LESS_OR_EQUAL )
                            || operatorsAre( (StringConditionInspector) other, Operator.GREATER_OR_EQUAL )
                            || operatorsAre( (StringConditionInspector) other, Operator.LESS_OR_EQUAL, Operator.LESS_THAN )
                            || operatorsAre( (StringConditionInspector) other, Operator.GREATER_OR_EQUAL, Operator.GREATER_THAN ) ) {
                        return getValue().equals( ((StringConditionInspector) other).getValue() );
                    } else {
                        switch (((StringConditionInspector) other).getOperator()) {
                            case IN:
                            case EQUALS:
                            case MATCHES:
                            case SOUNDSLIKE:
                                return getValue().equals( ((StringConditionInspector) other).getValue() );
                        }
                    }
                    break;
                case IN:
                    switch (((StringConditionInspector) other).getOperator()) {
                        case EQUALS:
                        case MATCHES:
                        case SOUNDSLIKE:
                            return getValues().contains( ((StringConditionInspector) other).getValues().get( 0 ) );
                    }

                    break;
                case NOT_IN:
                    switch (((StringConditionInspector) other).getOperator()) {
                        case IN:
                        case EQUALS:
                        case MATCHES:
                        case SOUNDSLIKE:
                            return !containsAll( ((StringConditionInspector) other).getValues() );
                        case NOT_EQUALS:
                            return getValues().contains( ((StringConditionInspector) other).getValue() );
                    }
                    break;
                case NOT_EQUALS:
                    switch (((StringConditionInspector) other).getOperator()) {
                        case NOT_IN:
                            return getValue().equals( ((StringConditionInspector) other).getValue() );
                        case IN:
                            return !((StringConditionInspector) other).getValues().contains( getValue() );
                        case EQUALS:
                        case MATCHES:
                        case SOUNDSLIKE:
                            return !getValue().equals( ((StringConditionInspector) other).getValue() );
                    }
                    break;
            }
        }

        return false;
    }

    @Override
    public boolean hasValue() {
        return values != null && !values.isEmpty() && hasAValueSetInList();
    }

    @Override
    public String toHumanReadableString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( getFactField() );
        stringBuilder.append( " " );
        stringBuilder.append( operator );
        stringBuilder.append( " " );

        Iterator<String> iterator = getValues().iterator();
        while ( iterator.hasNext() ) {
            stringBuilder.append( iterator.next() );
            if ( iterator.hasNext() ) {
                stringBuilder.append( ", " );
            }
        }

        return stringBuilder.toString();
    }

    private boolean hasAValueSetInList() {
        for ( String value : values ) {
            if ( value != null && !value.trim().isEmpty() ) {
                return true;
            }
        }
        return false;
    }
}
