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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.action;

import java.util.Date;
import java.util.Iterator;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.util.HasKeys;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.HumanReadable;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsConflicting;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsRedundant;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Action;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Values;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

public abstract class ActionInspector
        implements IsRedundant,
                   IsSubsuming,
                   IsConflicting,
                   HumanReadable,
                   HasKeys {

    private static final String         DATE_FORMAT    = ApplicationPreferences.getDroolsDateFormat();
    private UUIDKey uuidKey = new UUIDKey( this );
    private static final DateTimeFormat DATE_FORMATTER = DateTimeFormat.getFormat( DATE_FORMAT );

    protected Action action;

    protected ActionInspector( final Action action ) {
        this.action = action;
    }

    @Override
    public boolean isRedundant( final Object other ) {
        if ( other instanceof ActionInspector ) {
            return areValuesRedundant( (( ActionInspector ) other).action.getValues() );
        } else {
            return false;
        }
    }

    private boolean areValuesRedundant( final Values<Comparable> others ) {


        for ( final Comparable comparable : action.getValues() ) {
            if ( !isValueRedundant( others,
                                    comparable ) ) {
                return false;
            }
        }

        for ( final Comparable comparable : others ) {
            if ( !isValueRedundant( action.getValues(),
                                    comparable ) ) {
                return false;
            }
        }

        return !(action.getValues().isEmpty() && others.isEmpty());
    }

    private boolean isValueRedundant( final Values<Comparable> others,
                                      final Comparable comparable ) {
        for ( final Comparable other : others ) {
            if ( isValueRedundant( comparable,
                                   other ) ) {
                return true;
            }
        }
        return false;
    }

    private boolean isValueRedundant( final Comparable value,
                                      final Comparable other ) {
        if ( value.equals( other ) ) {
            return true;
        } else if ( value instanceof Date ) {
            return areDatesEqual( ( Date ) value,
                                  other );
        } else if ( other instanceof Date ) {
            return areDatesEqual( ( Date ) other,
                                  value );
        } else {
            return value.toString().equals( other.toString() );
        }
    }

    private boolean areDatesEqual( final Date value,
                                   final Comparable other ) {
        if ( other instanceof String ) {
            return format( value ).toString().equals( other );
        } else {
            return false;
        }
    }

    protected String format( final Date dateValue ) {
        return DATE_FORMATTER.format( dateValue );
    }

    @Override
    public boolean conflicts( final Object other ) {
        if ( other instanceof ActionInspector ) {
            final ActionInspector otherActionInspector = ( ActionInspector ) other;
            return !areValuesRedundant( otherActionInspector.action.getValues() );
        } else {
            return false;
        }
    }

    @Override
    public boolean subsumes( final Object other ) {
        // At the moment we are not smart enough to figure out subsumption in the RHS.
        // So redundancy == subsumption in this case.
        return isRedundant( other );
    }

    public String toHumanReadableString() {
        final StringBuilder builder = new StringBuilder();

        final Iterator<Comparable> iterator = action.getValues().iterator();

        while ( iterator.hasNext() ) {
            builder.append( iterator.next() );
            if ( iterator.hasNext() ) {
                builder.append( ", " );
            }
        }

        return builder.toString();
    }

    public boolean hasValue() {
        return action.getValues().isEmpty();
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    @Override
    public Key[] keys() {
        return new Key[0];
    }
}
