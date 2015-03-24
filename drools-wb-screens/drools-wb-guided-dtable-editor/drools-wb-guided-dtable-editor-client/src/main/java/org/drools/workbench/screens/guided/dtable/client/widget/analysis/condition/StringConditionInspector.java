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

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Operator;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Redundancy;

public class StringConditionInspector
        extends ConditionInspector {

    private List<String> values = new ArrayList<String>();
    private Operator operator;

    public StringConditionInspector( Pattern52 pattern,
                                     String factField,
                                     String value,
                                     String operator ) {
        super( pattern,
               factField );
        this.operator = Operator.resolve( operator );

        switch ( this.operator ) {
            case EQUALS:
            case NOT_EQUALS:
                values.add( value );
                break;
            case IN:
                for ( String item : value.split( "," ) ) {
                    values.add( item.trim() );
                }
                break;
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
        if ( other instanceof IsSubsuming ) {
            return subsumes( other ) && ( (IsSubsuming) other ).subsumes( this );
        } else {
            return false;
        }
    }

    @Override
    public boolean conflicts( Object other ) {
        if ( other instanceof StringConditionInspector ) {
            switch ( ( (StringConditionInspector) other ).getOperator() ) {
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
    public boolean overlaps( Object other ) {
        if ( other instanceof StringConditionInspector ) {
            switch ( operator ) {
                case EQUALS:
                    switch ( ( (StringConditionInspector) other ).getOperator() ) {
                        case NOT_EQUALS:
                            return !( (StringConditionInspector) other ).values.contains( values.get( 0 ) );
                        default:
                            return ( (StringConditionInspector) other ).values.contains( values.get( 0 ) );
                    }
                case NOT_EQUALS:
                    return !( (StringConditionInspector) other ).values.contains( values.get( 0 ) );
                case IN:
                    switch ( ( (StringConditionInspector) other ).getOperator() ) {
                        case EQUALS:
                            return values.contains( ( (StringConditionInspector) other ).getValues().get( 0 ) );
                        case NOT_EQUALS:
                            return !values.contains( ( (StringConditionInspector) other ).getValues().get( 0 ) );
                        case IN:
                            if ( containsAny( ( (StringConditionInspector) other ).values ) ) {
                                return true;
                            }
                    }
            }
        }

        return false;
    }

    private boolean containsAny( List<String> otherValues ) {
        for ( String thisValue : values ) {
            for ( String otherValue : otherValues ) {
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
                return Redundancy.isSubsumptant( getValues(),
                                                 ( (StringConditionInspector) other ).getValues() );
            } else if ( operator.equals( Operator.IN ) && ( (StringConditionInspector) other ).getOperator().equals( Operator.EQUALS ) ) {
                return getValues().contains( ( (StringConditionInspector) other ).getValues().get( 0 ) );
            } else if ( operator.equals( Operator.IN ) && ( (StringConditionInspector) other ).getOperator().equals( Operator.NOT_EQUALS ) ) {
                return !getValues().contains( ( (StringConditionInspector) other ).getValues().get( 0 ) );
            } else if ( operator.equals( Operator.NOT_EQUALS ) && ( (StringConditionInspector) other ).getOperator().equals( Operator.IN ) ) {
                return !getValues().contains( ( (StringConditionInspector) other ).getValues().get( 0 ) );
            }

            return false;
        } else {
            return false;
        }
    }

    @Override
    public boolean hasValue() {
        return values != null && !values.isEmpty() && hasAValueSetInList();
    }

    private boolean hasAValueSetInList() {
        for ( String value : values ) {
            if ( value != null && !value.isEmpty() ) {
                return true;
            }
        }
        return false;
    }
}
