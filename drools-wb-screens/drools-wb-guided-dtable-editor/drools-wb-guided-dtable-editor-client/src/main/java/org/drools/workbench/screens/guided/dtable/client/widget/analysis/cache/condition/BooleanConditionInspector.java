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

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Operator;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;

public class BooleanConditionInspector
        extends ConditionInspector {

    private final Boolean value;

    public BooleanConditionInspector( final Field field,
                                      final Boolean value,
                                      final String operator ) {
        super( field,
               operator );

        switch ( this.operator ) {
            case EQUALS:
                this.value = value;
                break;
            case NOT_EQUALS:
                this.value = !value;
                break;
            default:
                this.value = null;
        }

    }

    @Override
    public boolean isRedundant( final Object other ) {
        if ( this.equals( other ) ) {
            return true;
        }
        if ( other instanceof BooleanConditionInspector ) {
            return value.compareTo( (( BooleanConditionInspector ) other).value ) == 0;
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
    public boolean hasValue() {
        return value != null;
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
        stringBuilder.append( value );

        return stringBuilder.toString();
    }
}
