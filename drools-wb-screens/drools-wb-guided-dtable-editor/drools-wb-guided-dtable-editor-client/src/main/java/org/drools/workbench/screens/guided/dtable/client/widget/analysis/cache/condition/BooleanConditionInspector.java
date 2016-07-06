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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.FieldCondition;

public class BooleanConditionInspector
        extends ComparableConditionInspector {


    public BooleanConditionInspector( final FieldCondition<Boolean> fieldCondition ) {
        super( fieldCondition );
    }

    @Override
    public boolean isRedundant( final Object other ) {
        if ( this.equals( other ) ) {
            return true;
        }
        if ( other instanceof BooleanConditionInspector ) {
            switch ( operator ) {
                case EQUALS:
                    switch ( (( BooleanConditionInspector ) other).operator ) {
                        case EQUALS:
                            return getValues().containsAll( (( BooleanConditionInspector ) other).getValues() );
                        case NOT_EQUALS:
                            return !getValue().equals( (( BooleanConditionInspector ) other).getValue() );
                        default:
                            return false;
                    }
                case NOT_EQUALS:
                    switch ( (( BooleanConditionInspector ) other).operator ) {
                        case EQUALS:
                            return !getValues().equals( (( BooleanConditionInspector ) other).getValues() );
                        case NOT_EQUALS:
                            return getValues().containsAll( (( BooleanConditionInspector ) other).getValues() );
                        default:
                            return false;
                    }
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean conflicts( final Object other ) {
        return !isRedundant( other );
    }

    @Override
    public boolean overlaps( final Object other ) {
        return isRedundant( other );
    }

    @Override
    public boolean subsumes( final Object other ) {
        return isRedundant( other );
    }

    @Override
    public String toHumanReadableString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( field.getFactType() );
        stringBuilder.append( "." );
        stringBuilder.append( field.getName() );
        stringBuilder.append( " " );
        stringBuilder.append( operator );
        stringBuilder.append( " " );
        stringBuilder.append( getValues() );

        return stringBuilder.toString();
    }
}
