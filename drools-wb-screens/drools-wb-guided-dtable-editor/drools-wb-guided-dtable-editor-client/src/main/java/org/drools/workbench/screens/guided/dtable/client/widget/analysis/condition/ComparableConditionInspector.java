/*
 * Copyright 2011 JBoss Inc
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

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Covers;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Operator;

public abstract class ComparableConditionInspector<T extends Comparable<T>>
        extends ConditionInspector
        implements Covers<T> {

    protected final Operator operator;
    protected T from = null;
    protected T to = null;

    public ComparableConditionInspector( Pattern52 pattern,
                                         String factField,
                                         T value,
                                         String operator ) {
        super( pattern,
               factField );

        this.operator = Operator.resolve( operator );

        setValue( value );
    }

    private void setValue( T value ) {
        switch ( this.operator ) {
            case EQUALS:
            case NOT_EQUALS:
                from = value;
                to = value;
                break;
            case GREATER_THAN:
            case GREATER_THAN_OR_EQUALS:
                from = value;
                break;
            case LESS_THAN:
            case LESS_THAN_OR_EQUALS:
                to = value;
                break;
        }
    }

    public T getValue() {
        if ( from != null ) {
            return from;
        } else {
            return to;
        }
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public boolean conflicts( Object other ) {
        if ( other instanceof ComparableConditionInspector ) {
            switch ( ( (ComparableConditionInspector) other ).getOperator() ) {
                case NOT_EQUALS:
                    switch ( operator ) {
                        case NOT_EQUALS:
                            return false;
                    }
                default:
                    return !overlaps( other );
            }
        }
        return false;
    }

    @Override
    public boolean isRedundant( Object b ) {
        if ( b instanceof ComparableConditionInspector ) {
            ComparableConditionInspector other = (ComparableConditionInspector) b;
            return this.operator.equals( other.operator )
                    && nullSafeEquals( from, other.from )
                    && nullSafeEquals( to, other.to );

        }

        return false;
    }

    @Override
    public boolean hasValue() {
        return from != null || to != null;
    }

    @Override
    public boolean overlaps( Object other ) {
        if ( other instanceof ComparableConditionInspector ) {

            ComparableConditionInspector<T> anotherPoint = (ComparableConditionInspector) other;

            if ( anotherPoint != null ) {
                switch ( anotherPoint.getOperator() ) {
                    case NOT_EQUALS:
                        if ( operator.equals( Operator.NOT_EQUALS ) ) {
                            return getValue().equals( anotherPoint.getValue() );
                        } else {
                            return !covers( anotherPoint.getValue() );
                        }
                    case EQUALS:
                        switch ( operator ) {
                            case NOT_EQUALS:
                                return !( (ComparableConditionInspector) other ).covers( getValue() );
                        }
                    case GREATER_THAN_OR_EQUALS:
                    case LESS_THAN_OR_EQUALS:
                        switch ( operator ) {
                            case NOT_EQUALS:
                                return !covers( anotherPoint.getValue() );
                            default:
                                return covers( anotherPoint.getValue() ) || anotherPoint.covers( getValue() );
                        }
                    case LESS_THAN:
                        switch ( operator ) {
                            case NOT_EQUALS:
                                return !covers( anotherPoint.getValue() );
                            case LESS_THAN:
                                return valueIsEqualTo( anotherPoint.getValue() )
                                        || anotherPoint.covers( getValue() );
                            default:
                                return covers( anotherPoint.getValue() ) || anotherPoint.covers( getValue() );

                        }
                    case GREATER_THAN:
                        switch ( operator ) {
                            case NOT_EQUALS:
                                return !covers( anotherPoint.getValue() );
                            case GREATER_THAN:
                                return valueIsEqualTo( anotherPoint.getValue() )
                                        || anotherPoint.covers( getValue() );
                            case LESS_THAN:
                                return covers( anotherPoint.getValue() ) || anotherPoint.covers( getValue() );
                            default:
                                return covers( anotherPoint.getValue() ) || anotherPoint.covers( getValue() );
                        }
                    default:
                        return false;
                }
            }
        }

        return false;
    }

    @Override
    public boolean subsumes( Object other ) {
        if ( other instanceof ComparableConditionInspector ) {

            ComparableConditionInspector anotherPoint = (ComparableConditionInspector) other;

            if ( anotherPoint != null ) {
                switch ( anotherPoint.getOperator() ) {
                    case NOT_EQUALS:
                        if ( operator.equals( Operator.NOT_EQUALS ) ) {
                            return getValue().equals( anotherPoint.getValue() );
                        } else {
                            return !covers( anotherPoint.getValue() );
                        }
                    case EQUALS:
                        return covers( anotherPoint.getValue() );
                    case GREATER_THAN_OR_EQUALS:
                        switch ( operator ) {
                            case GREATER_THAN_OR_EQUALS:
                            case GREATER_THAN:
                                return covers( anotherPoint.getValue() );
                            case NOT_EQUALS:
                                return valueIsGreaterThan( anotherPoint.getValue() );
                            default:
                                return false;
                        }

                    case LESS_THAN_OR_EQUALS:
                        switch ( operator ) {
                            case LESS_THAN_OR_EQUALS:
                            case LESS_THAN:
                                return covers( anotherPoint.getValue() );
                            case NOT_EQUALS:
                                return valueIsLessThan( anotherPoint.getValue() );
                            default:
                                return false;
                        }
                    case LESS_THAN:
                        switch ( operator ) {
                            case LESS_THAN_OR_EQUALS:
                                return covers( anotherPoint.getValue() );
                            case LESS_THAN:
                            case NOT_EQUALS:
                                return valueIsLessThanOrEqualTo( anotherPoint.getValue() );
                            default:
                                return false;
                        }
                    case GREATER_THAN:
                        switch ( operator ) {
                            case GREATER_THAN_OR_EQUALS:
                                return covers( anotherPoint.getValue() );
                            case GREATER_THAN:
                            case NOT_EQUALS:
                                return valueIsGreaterThanOrEqualTo( anotherPoint.getValue() );
                            default:
                                return false;

                        }
                    default:
                        return false;
                }
            }
        }

        return false;
    }

    @Override
    public boolean covers( Comparable<T> otherValue ) {
        if ( otherValue instanceof Comparable ) {
            switch ( operator ) {
                case EQUALS:
                    return valueIsEqualTo( otherValue );
                case NOT_EQUALS:
                    return !valueIsEqualTo( otherValue );
                case GREATER_THAN_OR_EQUALS:
                    return valueIsGreaterThanOrEqualTo( otherValue );
                case LESS_THAN_OR_EQUALS:
                    return valueIsLessThanOrEqualTo( otherValue );
                case LESS_THAN:
                    return valueIsLessThan( otherValue );
                case GREATER_THAN:
                    return valueIsGreaterThan( otherValue );
                default:
                    return false;
            }

        } else {
            return false;
        }
    }

    private boolean valueIsGreaterThanOrEqualTo( Comparable<T> otherValue ) {
        return valueIsEqualTo( otherValue ) || valueIsGreaterThan( otherValue );
    }

    private boolean valueIsLessThanOrEqualTo( Comparable<T> otherValue ) {
        return valueIsEqualTo( otherValue ) || valueIsLessThan( otherValue );
    }

    private boolean valueIsGreaterThan( Comparable<T> otherValue ) {
        return otherValue.compareTo( getValue() ) > 0;
    }

    private boolean valueIsLessThan( Comparable<T> otherValue ) {
        return otherValue.compareTo( getValue() ) < 0;
    }

    private boolean valueIsEqualTo( Comparable<T> otherValue ) {
        return otherValue.compareTo( getValue() ) == 0;
    }
}
